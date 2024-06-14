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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeErrorCodes;
import org.eclipse.kapua.commons.web.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.ExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaExceptionMapper implements ExceptionMapper<KapuaException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaExceptionMapper.class);
    public static final int UNPROCESSABLE_CONTENT_HTTP_CODE = 422;

    private final boolean showStackTrace;

    @Inject
    public KapuaExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(KapuaException kapuaException) {
        LOG.error(kapuaException.getMessage(), kapuaException);
        if (kapuaException.getCode() instanceof KapuaRuntimeErrorCodes) {
            switch ((KapuaRuntimeErrorCodes) kapuaException.getCode()) {
            case SERVICE_OPERATION_NOT_SUPPORTED:
                return Response
                        .status(UNPROCESSABLE_CONTENT_HTTP_CODE) //Unprocessable content.
                        .entity(new ExceptionInfo(UNPROCESSABLE_CONTENT_HTTP_CODE, kapuaException, showStackTrace))
                        .build();
            }
        }
        return Response
                .serverError()
                .entity(new ExceptionInfo(Status.INTERNAL_SERVER_ERROR.getStatusCode(), kapuaException, showStackTrace))
                .build();
    }

}
