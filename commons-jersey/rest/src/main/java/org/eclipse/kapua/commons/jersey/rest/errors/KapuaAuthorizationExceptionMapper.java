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

import org.eclipse.kapua.service.authorization.exception.KapuaAuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class KapuaAuthorizationExceptionMapper implements ExceptionMapper<KapuaAuthorizationException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaAuthorizationExceptionMapper.class);

    @Override
    public Response toResponse(KapuaAuthorizationException kapuaAuthorizationException) {
        LOG.error(kapuaAuthorizationException.getMessage(), kapuaAuthorizationException);

        return Response.status(Status.FORBIDDEN).build();
    }

}
