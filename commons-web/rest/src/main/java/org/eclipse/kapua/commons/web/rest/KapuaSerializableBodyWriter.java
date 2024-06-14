/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.web.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.kapua.KapuaSerializable;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class KapuaSerializableBodyWriter implements MessageBodyWriter<KapuaSerializable> {

    @Context
    Providers providers;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // TODO any ode here to do more significant check on the types ?
        return true;
    }

    @Override
    public long getSize(KapuaSerializable t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(KapuaSerializable t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        try {
            if (providers == null) {
                throw new WebApplicationException("Unable to find any provider.");
            }

            ContextResolver<JAXBContext> cr = providers.getContextResolver(JAXBContext.class,
                    MediaType.APPLICATION_XML_TYPE);
            JAXBContext jaxbContext = cr.getContext(JAXBContext.class);
            if (jaxbContext == null) {
                throw new WebApplicationException("Unable to get a JAXBContext.");
            }

            // serialize the entity myBean to the entity output stream
            jaxbContext.createMarshaller().marshal(t, entityStream);
        } catch (JAXBException e) {
            throw new WebApplicationException(e);
        }
    }

}
