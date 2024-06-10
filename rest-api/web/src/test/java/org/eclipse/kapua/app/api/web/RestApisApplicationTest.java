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
package org.eclipse.kapua.app.api.web;

import javax.xml.bind.JAXBException;

import org.eclipse.kapua.commons.rest.jersey.JaxbContextResolver;
import org.eclipse.kapua.commons.rest.jersey.KapuaSerializableBodyWriter;
import org.eclipse.kapua.commons.rest.jersey.ListBodyWriter;
import org.eclipse.kapua.commons.rest.jersey.MoxyJsonConfigContextResolver;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class RestApisApplicationTest {

    @Test
    public void restApisApplicationTest() throws JAXBException {
        RestApisApplication restApisApplication = new RestApisApplication();

        Assert.assertEquals("{jersey.config.server.mediaTypeMappings={xml=application/xml, json=application/json}, jersey.config.server.wadl.disableWadl=true}",
                restApisApplication.getProperties().toString());
        Assert.assertTrue("True expected.", restApisApplication.isRegistered(UriConnegFilter.class));
        Assert.assertTrue("True expected.", restApisApplication.isRegistered(JaxbContextResolver.class));
        //        Assert.assertTrue("True expected.", restApisApplication.isRegistered(RestApiJAXBContextProvider.class));
        Assert.assertTrue("True expected.", restApisApplication.isRegistered(KapuaSerializableBodyWriter.class));
        Assert.assertTrue("True expected.", restApisApplication.isRegistered(ListBodyWriter.class));
        Assert.assertTrue("True expected.", restApisApplication.isRegistered(MoxyJsonConfigContextResolver.class));

        Assert.assertFalse("False expected.", restApisApplication.isRegistered(ContainerLifecycleListener.class));
        Assert.assertFalse("False expected.", restApisApplication.isRegistered(String.class));

        restApisApplication.register(ContainerLifecycleListener.class);
        Assert.assertTrue("True expected.", restApisApplication.isRegistered(ContainerLifecycleListener.class));
    }
}