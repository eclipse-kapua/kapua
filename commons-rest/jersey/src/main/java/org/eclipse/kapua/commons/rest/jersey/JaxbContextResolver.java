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
package org.eclipse.kapua.commons.rest.jersey;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.locator.KapuaLocator;

/**
 * Provide a customized JAXBContext that makes the concrete implementations known and available for marshalling
 *
 * @since 1.0.0
 */
@Provider
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class JaxbContextResolver implements ContextResolver<JAXBContext> {

    private final JAXBContext jaxbContext;

    public JaxbContextResolver() {
        try {
            jaxbContext = KapuaLocator.getInstance().getComponent(JAXBContextProvider.class).getJAXBContext();
        } catch (KapuaException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JAXBContext getContext(Class<?> type) {
        return jaxbContext;
    }

}