/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.core.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.shiro.AccessTokenCredentialsImpl;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ShiroFilter} override.
 * <p>
 * Used to intercept the HTTP request execute and plug into thread context the {@link KapuaSession}.
 *
 * @since 1.0.0
 */
public class KapuaWebFilter extends ShiroFilter {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaWebFilter.class);

    private final AuthenticationService authenticationService = KapuaLocator.getInstance().getService(AuthenticationService.class);

    @Override
    protected void executeChain(ServletRequest request, ServletResponse response, FilterChain origChain)
            throws IOException, ServletException {

        try {
            KapuaSession kapuaSession = getSession();

            if (kapuaSession != null) {
                checkAndRefreshAccessTokenIfExpired(kapuaSession.getAccessToken());
            }

            KapuaSecurityUtils.setSession(getSession());

            super.executeChain(request, response, origChain);
        } catch (KapuaException e) {
            LOG.error("Error while plugging KapuaSession into the ServletRequest", e);

            throw new ServletException(e);
        } finally {
            // unbind kapua session
            KapuaSecurityUtils.clearSession();
        }
    }

    /**
     * Gets the {@link KapuaSession} from the {@link Subject}
     *
     * @return The {@link KapuaSession} from the {@link Subject}
     * @since 2.0.0
     */
    protected KapuaSession getSession() {
        Subject shiroSubject = SecurityUtils.getSubject();
        return (KapuaSession) shiroSubject.getSession().getAttribute(KapuaSession.KAPUA_SESSION_KEY);
    }

    /**
     * Check the {@link AccessToken#getExpiresOn()} and refreshes it on behalf of the user.
     *
     * @param accessToken
     *         The {@link AccessToken} to check and refresh if needed.
     * @throws KapuaException
     *         If one of the checks fails or refreshing the token fails.
     * @since 2.0.0
     */
    protected void checkAndRefreshAccessTokenIfExpired(AccessToken accessToken) throws KapuaException {
        if (accessToken == null) {
            return;
        }

        Date now = new Date();

        if (now.after(accessToken.getExpiresOn()) && now.before(accessToken.getRefreshExpiresOn())) {
            LOG.info("Refreshing AccessToken for user {} of scope {} expired on {} - token: {}", accessToken.getUserId(), accessToken.getScopeId(), accessToken.getExpiresOn(),
                    accessToken.getTokenId());

            // Remove logout the user to perform a new login with the refreshed token.
            SecurityUtils.getSubject().logout();

            // Get a new AccessToken from the current AccessToken.refreshToken
            AccessToken refreshAccessToken = authenticationService.refreshAccessToken(accessToken.getTokenId(), accessToken.getRefreshToken());

            // Authenticate with the refreshed AccessToken
            authenticationService.authenticate(new AccessTokenCredentialsImpl(refreshAccessToken.getTokenId()));
        } else if (now.after(accessToken.getRefreshExpiresOn())) {
            throw new AuthenticationException("AccessToken.refreshToken is expired!");
        }
    }
}
