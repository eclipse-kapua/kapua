/*******************************************************************************
 * Copyright (c) 2024, 2022 Eurotech and/or its affiliates and others
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
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.commons.rest.model.errors.DuplicateNameExceptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class KapuaDuplicateNameExceptionMapper implements ExceptionMapper<KapuaDuplicateNameException> {
    private static final Logger LOG = LoggerFactory.getLogger(KapuaDuplicateNameExceptionMapper.class);
    private final boolean showStackTrace;


    @Inject
    public KapuaDuplicateNameExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }


    @Override
    public Response toResponse(KapuaDuplicateNameException kapuaDuplicateNameException) {
        LOG.error(kapuaDuplicateNameException.getMessage(), kapuaDuplicateNameException);
        return Response
                   .status(Response.Status.fromStatusCode(409))
                   .entity(new DuplicateNameExceptionInfo(kapuaDuplicateNameException, showStackTrace))
                   .build();
    }
}
