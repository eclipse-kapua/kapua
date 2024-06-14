/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.service.internal.KapuaServiceDisabledException;
import org.eclipse.kapua.exception.KapuaFeatureDisabledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * {@link KapuaFeatureDisabledException}'s {@link ExceptionMapper}
 *
 * @since 2.1.0
 */
@Provider
public class KapuaFeatureDisabledExceptionMapper implements ExceptionMapper<KapuaFeatureDisabledException> {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaServiceDisabledException.class);

    private static final Status STATUS = Status.FORBIDDEN;

    @Inject
    public ExceptionConfigurationProvider exceptionConfigurationProvider;

    @Override
    public Response toResponse(KapuaFeatureDisabledException kapuaFeatureDisabledException) {
        LOG.error(kapuaFeatureDisabledException.getMessage(), kapuaFeatureDisabledException);

        boolean showStackTrace = exceptionConfigurationProvider.showStackTrace();
        return Response
                .status(STATUS)
                .entity(new ExceptionInfo(STATUS.getStatusCode(), kapuaFeatureDisabledException, showStackTrace))
                .build();
    }

}
