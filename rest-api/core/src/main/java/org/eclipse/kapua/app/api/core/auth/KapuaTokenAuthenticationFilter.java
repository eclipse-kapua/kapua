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
package org.eclipse.kapua.app.api.core.auth;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.service.authentication.shiro.AccessTokenCredentialsImpl;
import org.eclipse.kapua.service.authentication.shiro.exceptions.ExpiredAccessTokenException;
import org.eclipse.kapua.service.authentication.shiro.exceptions.InvalidatedAccessTokenException;
import org.eclipse.kapua.service.authentication.shiro.exceptions.MalformedAccessTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KapuaTokenAuthenticationFilter extends AuthenticatingFilter {

    private static final String OPTIONS = "OPTIONS";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KapuaTokenAuthenticationFilter() {
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        logger.trace("Passing through KapuaTokenAuthenticationFilter.isAccessAllowed, request: {}, response: {}, mappedValue: {}", request, response, mappedValue);
        if (OPTIONS.equals(((HttpServletRequest) request).getMethod())) {
            return true;
        }

        try {
            return executeLogin(request, response);
        } catch (AuthenticationException ae) {
            return onLoginFailure(null, ae, request, response);
        } catch (Exception e) {
            throw KapuaRuntimeException.internalError(e);
        }
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        // Extract token
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
        String tokenId = null;
        if (authorizationHeader != null) {
            tokenId = httpRequest.getHeader(AUTHORIZATION_HEADER).replace(BEARER + " ", "");
        }
        // Build AccessToken for Shiro Auth
        AccessTokenCredentialsImpl accessTokenCredentials = new AccessTokenCredentialsImpl(tokenId);
        // Return token
        return (AuthenticationToken) accessTokenCredentials;
    }

    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
            ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //now I set a dummy header to propagate the error message to the CORSResponseFilter Class, that eventually will send this error message if CORS filter passes
        httpResponse.setHeader("exceptionMessagePropagatedToCORS", handleAuthException(e));
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // Continue with the filter chain, because CORS headers are still needed
        return true;
    }

    //with this method we choose what exceptions we want to hide in the response and what we want to show as an error message
    private String handleAuthException(AuthenticationException ae) {
        String errorMessageInResponse = "An error occurred during the authentication process with the provided access token";
        if (ae instanceof MalformedAccessTokenException ||
                ae instanceof InvalidatedAccessTokenException ||
                ae instanceof ExpiredAccessTokenException) {
            errorMessageInResponse = ae.getMessage();
        }
        return errorMessageInResponse;
    }

}
