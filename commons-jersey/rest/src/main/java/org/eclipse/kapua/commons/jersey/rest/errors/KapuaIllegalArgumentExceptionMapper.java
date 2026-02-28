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

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.jersey.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.commons.jersey.rest.model.errors.IllegalArgumentExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaIllegalArgumentExceptionMapper implements ExceptionMapper<KapuaIllegalArgumentException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaIllegalArgumentExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public KapuaIllegalArgumentExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(KapuaIllegalArgumentException kapuaIllegalArgumentException) {
        LOG.error(kapuaIllegalArgumentException.getMessage(), kapuaIllegalArgumentException);
        return Response
                .status(Status.BAD_REQUEST)
                .entity(new IllegalArgumentExceptionInfo(Status.BAD_REQUEST.getStatusCode(), kapuaIllegalArgumentException, showStackTrace))
                .build();
    }
}
