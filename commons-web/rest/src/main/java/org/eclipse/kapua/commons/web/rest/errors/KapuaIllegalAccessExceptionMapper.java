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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.kapua.KapuaIllegalAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaIllegalAccessExceptionMapper implements ExceptionMapper<KapuaIllegalAccessException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaIllegalAccessExceptionMapper.class);

    @Override
    public Response toResponse(KapuaIllegalAccessException kapuaAuthorizationException) {
        LOG.error(kapuaAuthorizationException.getMessage(), kapuaAuthorizationException);

        return Response.status(Status.FORBIDDEN).entity(kapuaAuthorizationException.getMessage()).type("text/plain").build();
    }

}
