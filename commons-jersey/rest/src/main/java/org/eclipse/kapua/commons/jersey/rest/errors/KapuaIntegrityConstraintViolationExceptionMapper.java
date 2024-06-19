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
package org.eclipse.kapua.commons.rest.errors;

import org.eclipse.kapua.kapuaIntegrityConstraintViolationException;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class KapuaIntegrityConstraintViolationExceptionMapper implements ExceptionMapper<kapuaIntegrityConstraintViolationException> {
    private static final Logger LOG = LoggerFactory.getLogger(kapuaIntegrityConstraintViolationException.class);

    private static final Status STATUS = Status.CONFLICT;
    @Inject
    public ExceptionConfigurationProvider exceptionConfigurationProvider;

    @Override
    public Response toResponse(kapuaIntegrityConstraintViolationException kapuaIntegrityConstraintViolationException) {
        final boolean showStackTrace = exceptionConfigurationProvider.showStackTrace();
        LOG.error(kapuaIntegrityConstraintViolationException.getMessage(), kapuaIntegrityConstraintViolationException);
        return Response
                .status(STATUS)
                .entity(new ExceptionInfo(STATUS.getStatusCode(), kapuaIntegrityConstraintViolationException, showStackTrace))
                .build();
    }

}
