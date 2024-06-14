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
package org.eclipse.kapua.commons.web.rest.errors;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.kapua.commons.web.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.commons.web.rest.model.errors.MfaRequiredExceptionInfo;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaAuthenticationExceptionMapper implements ExceptionMapper<KapuaAuthenticationException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaAuthenticationExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public KapuaAuthenticationExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(KapuaAuthenticationException kapuaAuthenticationException) {
        LOG.error(kapuaAuthenticationException.getMessage(), kapuaAuthenticationException);

        if (kapuaAuthenticationException.getCode().equals(KapuaAuthenticationErrorCodes.REQUIRE_MFA_CREDENTIALS)) {
            return Response
                    .status(Status.FORBIDDEN)
                    .entity(new MfaRequiredExceptionInfo(Status.FORBIDDEN.getStatusCode(), kapuaAuthenticationException, showStackTrace))
                    .build();
        }

        if (kapuaAuthenticationException.getCode().equals(KapuaAuthenticationErrorCodes.REFRESH_ERROR)) {
            return Response
                    .status(Status.UNAUTHORIZED)
                    .entity(new ExceptionInfo(Status.UNAUTHORIZED.getStatusCode(), kapuaAuthenticationException, showStackTrace))
                    .build();
        }

        return Response.status(Status.UNAUTHORIZED).build();
    }

}
