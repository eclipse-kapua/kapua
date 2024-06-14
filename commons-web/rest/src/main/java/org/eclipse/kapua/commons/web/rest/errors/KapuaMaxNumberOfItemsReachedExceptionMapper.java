/*******************************************************************************
 * Copyright (c) 2024 Eurotech and/or its affiliates and others
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

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.kapua.KapuaMaxNumberOfItemsReachedException;
import org.eclipse.kapua.commons.rest.model.errors.MaxNumberOfItemsReachedExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaMaxNumberOfItemsReachedExceptionMapper implements ExceptionMapper<KapuaMaxNumberOfItemsReachedException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaMaxNumberOfItemsReachedExceptionMapper.class);
    private final boolean showStackTrace;


    @Inject
    public KapuaMaxNumberOfItemsReachedExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }


    @Override
    public Response toResponse(KapuaMaxNumberOfItemsReachedException kapuaMaxNumberOfItemsReachedException) {
        LOG.error(kapuaMaxNumberOfItemsReachedException.getMessage(), kapuaMaxNumberOfItemsReachedException);
        return Response
                .status(Status.FORBIDDEN)
                .entity(new MaxNumberOfItemsReachedExceptionInfo(Status.FORBIDDEN.getStatusCode(), kapuaMaxNumberOfItemsReachedException, showStackTrace))
                .build();
    }
}
