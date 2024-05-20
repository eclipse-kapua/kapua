/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.app.core.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.kapua.app.api.core.auth.KapuaTokenAuthenticationFilter;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.job.engine.SessionInfoHttpHeaders;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is meant to be used ONLY internally (as in: not in services exposed to the world) to reconstruct locally a privileged session (e.g.: KapuaSecurityUtils.doPrivileged). It works in
 * conjunction with a SessionInfo filter on the client side that sets the trusted header when executing the call from a privileged context
 */
public class RebuildTrustedSessionFilter extends KapuaTokenAuthenticationFilter {

    private final KapuaIdFactory kapuaIdFactory = KapuaLocator.getInstance().getFactory(KapuaIdFactory.class);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String authMode = httpRequest.getHeader(SessionInfoHttpHeaders.AUTH_MODE);
        logger.trace("Passing through RebuildTrustedSessionFilter.onAccessDenied, authMode: {}, request: {}", authMode, request);
        switch (authMode) {
        case "trusted":
            logger.trace("Passing through RebuildTrustedSessionFilter.onAccessDenied, scopeId: {}, userId: {}",
                    httpRequest.getHeader(SessionInfoHttpHeaders.SCOPE_ID_HTTP_HEADER), httpRequest.getHeader(SessionInfoHttpHeaders.USER_ID_HTTP_HEADER));
            KapuaId scopeId = kapuaIdFactory.newKapuaId(httpRequest.getHeader(SessionInfoHttpHeaders.SCOPE_ID_HTTP_HEADER));
            KapuaId userId = kapuaIdFactory.newKapuaId(httpRequest.getHeader(SessionInfoHttpHeaders.USER_ID_HTTP_HEADER));
            KapuaSecurityUtils.setSession(KapuaSession.createFrom(scopeId, userId));
            return true;
        case "access_token":
        default:
            return super.isAccessAllowed(request, response, mappedValue);
        }
    }
}
