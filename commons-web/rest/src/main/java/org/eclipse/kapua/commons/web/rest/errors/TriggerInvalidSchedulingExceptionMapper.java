/*******************************************************************************
 * Copyright (c) 2024, 2024 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.commons.rest.model.errors.TriggerInvalidSchedulingExceptionInfo;
import org.eclipse.kapua.service.scheduler.exception.TriggerInvalidSchedulingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TriggerInvalidSchedulingExceptionMapper implements ExceptionMapper<TriggerInvalidSchedulingException> {

    private static final Logger LOG = LoggerFactory.getLogger(TriggerInvalidSchedulingExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public TriggerInvalidSchedulingExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(TriggerInvalidSchedulingException triggerInvalidSchedulingException) {
        LOG.error(triggerInvalidSchedulingException.getMessage(), triggerInvalidSchedulingException);

        return Response
                .status(Status.BAD_REQUEST)
                .entity(new TriggerInvalidSchedulingExceptionInfo(triggerInvalidSchedulingException, showStackTrace))
                .build();
    }

}
