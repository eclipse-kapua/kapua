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
package org.eclipse.kapua.job.engine.client.filter;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.job.engine.client.settings.JobEngineClientSetting;
import org.eclipse.kapua.job.engine.client.settings.JobEngineClientSettingKeys;
import org.eclipse.kapua.locator.KapuaLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;

/**
 * {@link ClientRequestFilter} used to populate HTTP request headers with information about the current {@link KapuaSession}.
 *
 * @since 1.5.0
 */
public class SessionInfoFilter implements ClientRequestFilter {

    private final JobEngineClientSetting jobEngineClientSetting = KapuaLocator.getInstance().getComponent(JobEngineClientSetting.class);
    private final String jobEngineClientSettingAuthMode = jobEngineClientSetting.getString(JobEngineClientSettingKeys.JOB_ENGINE_CLIENT_AUTH_MODE, "access_token");
    private final boolean jobEngineClientAuthTrusted = "trusted".equals(jobEngineClientSettingAuthMode);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
        logger.trace("SessionInfoFilter.filter, jobEngineClientAuthTrusted: {}, sessionTrusted: {}", jobEngineClientAuthTrusted, kapuaSession.isTrustedMode());
        if (jobEngineClientAuthTrusted || kapuaSession.isTrustedMode()) {
            requestContext.getHeaders().putSingle(SessionInfoHttpHeaders.AUTH_MODE, "trusted");
        } else {
            requestContext.getHeaders().putSingle(SessionInfoHttpHeaders.AUTH_MODE, "access_token");
            requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, kapuaSession.getAccessToken() != null ? "Bearer " + kapuaSession.getAccessToken().getTokenId() : null);
        }

        requestContext.getHeaders().putSingle(SessionInfoHttpHeaders.SCOPE_ID_HTTP_HEADER, kapuaSession.getScopeId().toCompactId());
        requestContext.getHeaders().putSingle(SessionInfoHttpHeaders.USER_ID_HTTP_HEADER, kapuaSession.getUserId().toCompactId());
    }
}
