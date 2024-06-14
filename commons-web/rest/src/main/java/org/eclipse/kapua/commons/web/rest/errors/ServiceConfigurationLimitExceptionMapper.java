/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.commons.configuration.exception.ServiceConfigurationLimitExceededException;
import org.eclipse.kapua.commons.web.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.commons.web.rest.model.errors.ServiceConfigurationLimitExceededExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ServiceConfigurationLimitExceptionMapper implements ExceptionMapper<ServiceConfigurationLimitExceededException> {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceConfigurationLimitExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public ServiceConfigurationLimitExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(ServiceConfigurationLimitExceededException serviceConfigurationLimitExceededException) {
        LOG.error(serviceConfigurationLimitExceededException.getMessage(), serviceConfigurationLimitExceededException);

        return Response
                .status(Status.BAD_REQUEST)
                .entity(new ServiceConfigurationLimitExceededExceptionInfo(serviceConfigurationLimitExceededException, showStackTrace))
                .build();
    }
}
