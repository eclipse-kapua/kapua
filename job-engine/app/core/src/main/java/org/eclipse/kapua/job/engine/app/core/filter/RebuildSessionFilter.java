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

import org.eclipse.kapua.app.api.core.auth.KapuaTokenAuthenticationFilter;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.job.engine.client.filter.SessionInfoHttpHeaders;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RebuildSessionFilter extends KapuaTokenAuthenticationFilter {

    private final KapuaIdFactory kapuaIdFactory = KapuaLocator.getInstance().getFactory(KapuaIdFactory.class);

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authMode = httpRequest.getHeader(SessionInfoHttpHeaders.AUTH_MODE);
        switch (authMode) {
            case "trusted":
                KapuaId scopeId = kapuaIdFactory.newKapuaId(httpRequest.getHeader(SessionInfoHttpHeaders.SCOPE_ID_HTTP_HEADER));
                KapuaId userId = kapuaIdFactory.newKapuaId(httpRequest.getHeader(SessionInfoHttpHeaders.USER_ID_HTTP_HEADER));
                KapuaSecurityUtils.setSession(KapuaSession.createFrom(scopeId, userId));
                return true;
            case "access_token":
            default:
                return super.onAccessDenied(request, response);
        }
    }

}
