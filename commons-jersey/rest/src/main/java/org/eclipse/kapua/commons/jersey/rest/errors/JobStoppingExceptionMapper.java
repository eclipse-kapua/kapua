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
import org.eclipse.kapua.job.engine.exception.JobStoppingException;
import org.eclipse.kapua.job.engine.exception.JobStoppingExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class JobStoppingExceptionMapper implements ExceptionMapper<JobStoppingException> {

    private static final Logger LOG = LoggerFactory.getLogger(JobStoppingExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public JobStoppingExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(JobStoppingException jobStoppingException) {
        LOG.error(jobStoppingException.getMessage(), jobStoppingException);

        return Response
                .status(Status.INTERNAL_SERVER_ERROR)
                .entity(new JobStoppingExceptionInfo(jobStoppingException, showStackTrace))
                .build();
    }

}
