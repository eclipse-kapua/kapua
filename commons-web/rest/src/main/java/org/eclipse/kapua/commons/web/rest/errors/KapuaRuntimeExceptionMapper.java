/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaRuntimeErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.ThrowableInfo;
import org.eclipse.kapua.commons.web.rest.ExceptionConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaRuntimeExceptionMapper implements ExceptionMapper<KapuaRuntimeException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaRuntimeExceptionMapper.class);
    public static final int UNPROCESSABLE_CONTENT_HTTP_CODE = 422;

    private final boolean showStackTrace;

    @Inject
    public KapuaRuntimeExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(KapuaRuntimeException runtimeException) {
        LOG.error(runtimeException.getMessage(), runtimeException);
        if (runtimeException.getCode() instanceof KapuaRuntimeErrorCodes) {
            switch ((KapuaRuntimeErrorCodes) runtimeException.getCode()) {
            case SERVICE_OPERATION_NOT_SUPPORTED:
                return Response
                        .status(UNPROCESSABLE_CONTENT_HTTP_CODE) //Unprocessable content.
                        .entity(new ThrowableInfo(UNPROCESSABLE_CONTENT_HTTP_CODE, runtimeException, showStackTrace))
                        .build();
            }
        }
        return Response
                .serverError()
                .entity(new ThrowableInfo(Status.INTERNAL_SERVER_ERROR.getStatusCode(), runtimeException, showStackTrace))
                .build();
    }

}
