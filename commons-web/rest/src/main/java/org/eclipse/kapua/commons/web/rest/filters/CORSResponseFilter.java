/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.web.rest.filters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.util.WebUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountQuery;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoFactory;
import org.eclipse.kapua.service.endpoint.EndpointInfoListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.HttpHeaders;

/**
 * CORS {@link Filter} implementation.
 * <p>
 * This filter handles the CORS request per-scope basis.jobeng
 *
 * @since 1.5.0
 */
public class CORSResponseFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(CORSResponseFilter.class);

    private final KapuaLocator locator = KapuaLocator.getInstance();
    private final AccountService accountService = locator.getService(AccountService.class);
    private final AccountFactory accountFactory = locator.getFactory(AccountFactory.class);
    private final EndpointInfoService endpointInfoService = locator.getService(EndpointInfoService.class);
    private final EndpointInfoFactory endpointInfoFactory = locator.getFactory(EndpointInfoFactory.class);
    private final KapuaRestFiltersSetting kapuaRestFiltersSetting = locator.getComponent(KapuaRestFiltersSetting.class);
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> refreshTask;

    private Multimap<String, KapuaId> allowedOrigins = HashMultimap.create();
    private final List<String> allowedSystemOrigins = kapuaRestFiltersSetting.getList(String.class, KapuaRestFiltersSettingKeys.API_CORS_ORIGINS_ALLOWED);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("Initializing with FilterConfig: {}...", filterConfig);
        int intervalSecs = kapuaRestFiltersSetting.getInt(KapuaRestFiltersSettingKeys.API_CORS_REFRESH_INTERVAL, 60);
        initRefreshThread(intervalSecs);
        logger.info("Initializing with FilterConfig: {}... DONE!", filterConfig);
    }

    @Override
    public void destroy() {
        logger.info("Shutting down...");
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
        logger.info("Shutting down... DONE!");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        HttpServletRequest httpRequest = WebUtils.toHttp(request);

        // Depending on the type of the request the KapuaSession might not yet be present.
        // For preflight request or session not yet established the KapuaSession will be null.
        // For the actual request it will be available and we will check the CORS according to the scope.
        KapuaId scopeId = KapuaSecurityUtils.getSession() != null ? KapuaSecurityUtils.getSession().getScopeId() : null;
        String origin = httpRequest.getHeader(HttpHeaders.ORIGIN);
        String fetchSite = httpRequest.getHeader(HttpHeaders.SEC_FETCH_SITE);
        String errorMessage = null;

        if (Strings.isNullOrEmpty(fetchSite)) {
            logger.warn("Sec-Fetch-Site' header not present in request: {} {}. CORSResponseFilter may produce false positives for this request. User-Agent is: {}", httpRequest.getMethod(),
                    httpRequest.getPathInfo(), httpRequest.getHeader(HttpHeaders.USER_AGENT));
        }
        if (Strings.isNullOrEmpty(origin)) {
            logger.warn("'Origin' header not present in request: {} {}. User-Agent is: {}", httpRequest.getMethod(), httpRequest.getPathInfo(), httpRequest.getHeader(HttpHeaders.USER_AGENT));
        } else {
            if (!"same-origin".equals(fetchSite)) {
                // Origin header present, so it's a CORS request. Apply all the required logics
                httpResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, DELETE, PUT");
                httpResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "X-Requested-With, Content-Type, Authorization");

                if (checkOrigin(origin, scopeId)) {
                    // Origin matches at least one defined Endpoint
                    httpResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                    httpResponse.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                    httpResponse.addHeader("Vary", HttpHeaders.ORIGIN);
                } else {
                    //this log, for clients not supporting sec-fetch-site, logs false positive for same origin CORS. This thing is inevitable considering that here we cannot understand if the request comes from the same origin
                    errorMessage = scopeId != null ?
                            String.format("HTTP Origin not allowed: %s for scope: %s for the request path: %s %s", origin, scopeId.toCompactId(), httpRequest.getMethod(), httpRequest.getPathInfo()) :
                            String.format("HTTP Origin not allowed: %s for the request path: %s %s", origin, httpRequest.getMethod(), httpRequest.getPathInfo());
                    logger.error(errorMessage);
                }
            } else {
                logger.debug("HTTP sec-fetch-site same-origin detected and allowed. Request: {} {}. User-Agent is: {}", httpRequest.getMethod(), httpRequest.getPathInfo(),
                        httpRequest.getHeader(HttpHeaders.USER_AGENT));
            }
        }
        int errorCode = httpResponse.getStatus();
        if (errorCode >= 400) {
            if (errorMessage == null) { //CORS filter passed...show propagated error message to the client
                errorMessage = httpResponse.getHeader("exceptionMessagePropagatedToCORS");
            }
            httpResponse.setHeader("exceptionMessagePropagatedToCORS", null); //no more needed, delete from response header
            httpResponse.sendError(errorCode, errorMessage);
            return;
        }
        chain.doFilter(request, response);
    }

    private String getExplicitOrigin(String origin) throws MalformedURLException {
        URL originUrl = new URL(origin);
        if (originUrl.getPort() != -1) {
            return origin;
        }

        switch (originUrl.getProtocol()) {
        case "http":
            return origin + ":80";
        case "https":
            return origin + ":443";
        default:
            return origin;
        }
    }

    private boolean checkOrigin(String origin, KapuaId scopeId) {
        String explicitOrigin;
        try {
            explicitOrigin = getExplicitOrigin(origin);
        } catch (MalformedURLException malformedURLException) {
            return false;
        }

        if (scopeId == null) {
            // No scopeId, so the call is not authenticated. Return true only if origin
            // is enabled in any account or system settings
            return allowedOrigins.containsKey(explicitOrigin);
        } else {
            // scopeId has a value, so validate the account as well
            Collection<KapuaId> allowedAccountIds = allowedOrigins.get(explicitOrigin);
            return allowedAccountIds.contains(scopeId) || allowedAccountIds.contains(KapuaId.ANY);
        }
    }

    private synchronized void initRefreshThread(int intervalSecs) {
        if (refreshTask == null) {
            refreshTask = executorService.scheduleAtFixedRate(this::refreshOrigins, 0, intervalSecs, TimeUnit.SECONDS);
        }
    }

    private synchronized void refreshOrigins() {
        try {
            logger.info("Refreshing list of origins...");

            Multimap<String, KapuaId> newAllowedOrigins = HashMultimap.create();
            AccountQuery accounts = accountFactory.newQuery(null);
            AccountListResult accountListResult = KapuaSecurityUtils.doPrivileged(() -> accountService.query(accounts));
            accountListResult.getItems().forEach(account -> {
                EndpointInfoQuery endpointInfoQuery = endpointInfoFactory.newQuery(account.getId());
                try {
                    EndpointInfoListResult endpointInfoListResult = KapuaSecurityUtils.doPrivileged(() -> endpointInfoService.query(endpointInfoQuery, EndpointInfo.ENDPOINT_TYPE_CORS));
                    endpointInfoListResult.getItems().forEach(endpointInfo -> newAllowedOrigins.put(endpointInfo.toStringURI(), account.getId()));
                } catch (KapuaException kapuaException) {
                    logger.warn("Unable to add endpoints for account {} to CORS filter", account.getId().toCompactId(), kapuaException);
                }
            });

            for (String allowedSystemOrigin : allowedSystemOrigins) {
                try {
                    String explicitAllowedSystemOrigin = getExplicitOrigin(allowedSystemOrigin);
                    newAllowedOrigins.put(explicitAllowedSystemOrigin, KapuaId.ANY);
                } catch (MalformedURLException malformedURLException) {
                    logger.warn("Unable to parse origin: {}", allowedSystemOrigin, malformedURLException);
                }
            }
            allowedOrigins = newAllowedOrigins;

            logger.info("Refreshing list of origins... DONE! Loaded {} origins", allowedOrigins.size());
        } catch (Exception exception) {
            logger.warn("Refreshing list of origins... ERROR!", exception);
        }
    }
}
