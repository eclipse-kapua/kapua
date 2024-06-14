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
package org.eclipse.kapua.commons.web.rest;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class MoxyJsonConfigContextResolverTest {

    @Test
    public void moxyJsonConfigContextResolverTest() {
        Class[] classes = { null, String.class, Integer.class, Short.class, Long.class, Character.class, Double.class, Float.class, String.class };
        MoxyJsonConfigContextResolver moxyJsonConfigContextResolver = new MoxyJsonConfigContextResolver();

        for (Class clazz : classes) {
            Assert.assertTrue("True expected.", moxyJsonConfigContextResolver.getContext(clazz) instanceof MoxyJsonConfig);
            Assert.assertEquals("Expected and actual values should be the same.", moxyJsonConfigContextResolver.config, moxyJsonConfigContextResolver.getContext(clazz));
            Assert.assertEquals("Expected and actual values should be the same.",
                    "{jaxb.formatted.output=false, eclipselink.json.namespace-separator=., eclipselink.json.include-root=false, eclipselink.json.wrapper-as-array-name=true, eclipselink.json.marshal-empty-collections=true}",
                    moxyJsonConfigContextResolver.config.getMarshallerProperties().toString());
        }
    }
}