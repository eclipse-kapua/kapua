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
package org.eclipse.kapua.commons.rest.errors;

import org.eclipse.kapua.commons.rest.model.errors.DeviceManagementResponseCodeExceptionInfo;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DeviceManagementResponseNotFoundExceptionMapper implements ExceptionMapper<DeviceManagementResponseNotFoundException> {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceManagementResponseNotFoundExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public DeviceManagementResponseNotFoundExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(DeviceManagementResponseNotFoundException deviceManagementResponseCodeException) {
        LOG.error(deviceManagementResponseCodeException.getMessage(), deviceManagementResponseCodeException);

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(new DeviceManagementResponseCodeExceptionInfo(404, deviceManagementResponseCodeException, showStackTrace))
                .build();
    }
}
