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
package org.eclipse.kapua.commons.web.rest.errors;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.kapua.commons.web.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.commons.web.rest.model.errors.DeviceNotConnectedExceptionInfo;
import org.eclipse.kapua.service.device.management.exception.DeviceNotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class DeviceNotConnectedExceptionMapper implements ExceptionMapper<DeviceNotConnectedException> {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceNotConnectedExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public DeviceNotConnectedExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(DeviceNotConnectedException managementRequestContentException) {
        LOG.error(managementRequestContentException.getMessage(), managementRequestContentException);

        return Response
                .status(Status.CONFLICT)
                .entity(new DeviceNotConnectedExceptionInfo(managementRequestContentException, showStackTrace))
                .build();
    }

}
