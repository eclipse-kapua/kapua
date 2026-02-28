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
package org.eclipse.kapua.commons.jersey.rest.errors;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.commons.jersey.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.commons.jersey.rest.model.errors.EntityUniquenessExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaEntityUniquenessExceptionMapper implements ExceptionMapper<KapuaEntityUniquenessException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaEntityUniquenessExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public KapuaEntityUniquenessExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(KapuaEntityUniquenessException kapuaEntityUniquenessException) {
        LOG.error(kapuaEntityUniquenessException.getMessage(), kapuaEntityUniquenessException);
        return Response
                .status(Status.fromStatusCode(409))
                .entity(new EntityUniquenessExceptionInfo(409, kapuaEntityUniquenessException, showStackTrace))
                .build();
    }

}
