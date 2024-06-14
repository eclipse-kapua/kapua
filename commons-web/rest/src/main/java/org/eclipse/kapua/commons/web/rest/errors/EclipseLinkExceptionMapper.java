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

import org.eclipse.kapua.commons.rest.model.errors.ThrowableInfo;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLConversionException;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EclipseLinkExceptionMapper implements ExceptionMapper<EclipseLinkException> {

    private static final Logger LOG = LoggerFactory.getLogger(EclipseLinkExceptionMapper.class);

    private final boolean showStackTrace;

    @Inject
    public EclipseLinkExceptionMapper(ExceptionConfigurationProvider exceptionConfigurationProvider) {
        this.showStackTrace = exceptionConfigurationProvider.showStackTrace();
    }

    @Override
    public Response toResponse(EclipseLinkException eclipseException) {
        LOG.error(eclipseException.getMessage(), eclipseException);

        if (eclipseException instanceof ConversionException ||
                eclipseException instanceof DescriptorException ||
                eclipseException instanceof XMLMarshalException ||
                eclipseException instanceof XMLConversionException) { // These are subset of EclipseLinkExceptions thrown by MOXy, so we have a problem with JAXB parsing of the XML/JSON
            ThrowableInfo responseError = new ThrowableInfo(Response.Status.BAD_REQUEST.getStatusCode(), eclipseException, showStackTrace);
            responseError.setMessage("An error occurred during the parsing of the XML/JSON. Check the correctness of its format. Details of the exception thrown: " + responseError.getMessage());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(responseError)
                    .build();
        } else {
            //Generic error message for other EclipseLinkExceptions
            return Response
                    .serverError()
                    .entity(new ThrowableInfo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), eclipseException, showStackTrace))
                    .build();
        }
    }

}
