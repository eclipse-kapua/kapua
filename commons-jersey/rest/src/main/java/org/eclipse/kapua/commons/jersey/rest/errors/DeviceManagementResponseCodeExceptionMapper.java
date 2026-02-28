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

import org.eclipse.kapua.commons.jersey.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.commons.jersey.rest.model.errors.DeviceManagementResponseCodeExceptionInfo;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class DeviceManagementResponseCodeExceptionMapper implements ExceptionMapper<DeviceManagementResponseCodeException> {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceManagementResponseCodeExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public DeviceManagementResponseCodeExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(DeviceManagementResponseCodeException deviceManagementResponseCodeException) {
        LOG.error(deviceManagementResponseCodeException.getMessage(), deviceManagementResponseCodeException);

        return Response
                .status(Status.INTERNAL_SERVER_ERROR)
                .entity(new DeviceManagementResponseCodeExceptionInfo(deviceManagementResponseCodeException, showStackTrace))
                .build();
    }

}
