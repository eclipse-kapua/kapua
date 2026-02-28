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
package org.eclipse.kapua.commons.jersey.rest.errors;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.kapua.commons.jersey.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.service.authentication.exception.PasswordLengthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaPasswordLengthExceptionMapper implements ExceptionMapper<PasswordLengthException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaPasswordLengthExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public KapuaPasswordLengthExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(PasswordLengthException kapuaPassLengthException) {
        LOG.error(kapuaPassLengthException.getMessage(), kapuaPassLengthException);
        return Response
                .status(Status.BAD_REQUEST)
                .entity(new ExceptionInfo(Status.BAD_REQUEST.getStatusCode(), kapuaPassLengthException, showStackTrace))
                .build();
    }

}
