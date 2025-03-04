/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.configuration;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class ServiceComponentConfigurationTest {

    ServiceComponentConfiguration serviceComponentConfiguration;

    @Before
    public void createInstanceOfClass() {
        serviceComponentConfiguration = new ServiceComponentConfiguration();
    }

    @Test
    public void createInstanceWithSecondConstructorTest() {
        ServiceComponentConfiguration componentConfiguration = new ServiceComponentConfiguration("12");
        Assert.assertEquals(componentConfiguration.getId(), "12");
    }

    @Test
    public void getIdIfNullTest() {
        Assert.assertNull(serviceComponentConfiguration.getId());
    }

    @Test
    public void getIdIfIsSetTest() {
        serviceComponentConfiguration.setId("1");
        Assert.assertEquals(serviceComponentConfiguration.getId(), "1");
    }

    @Test
    public void setIdToNullValueTest() {
        serviceComponentConfiguration.setId(null);
        Assert.assertNull(serviceComponentConfiguration.getId());
    }

    @Test
    public void setIdToLargeValueTest() {
        serviceComponentConfiguration.setId("12312312312312331232313123123123");
        Assert.assertEquals(serviceComponentConfiguration.getId(), "12312312312312331232313123123123");
    }

    @Test
    public void setIdToSymbolsTest() {
        serviceComponentConfiguration.setId("@!#");
        Assert.assertEquals(serviceComponentConfiguration.getId(), "@!#");
    }

    @Test
    public void setIdToEmptyStringTest() {
        serviceComponentConfiguration.setId("");
        Assert.assertEquals(serviceComponentConfiguration.getId(), "");
    }

    @Test
    public void getNameIfNullTest() {
        Assert.assertNull(serviceComponentConfiguration.getName());
    }

    @Test
    public void setNameIfNotNullTest() {
        serviceComponentConfiguration.setName("name");
        Assert.assertEquals(serviceComponentConfiguration.getName(), "name");
    }

    @Test
    public void setNameToNullTest() {
        serviceComponentConfiguration.setName(null);
        Assert.assertNull(serviceComponentConfiguration.getName());
    }

    @Test
    public void setNameToNameThatContainsSpacesTest() {
        serviceComponentConfiguration.setName("regular Name");
        Assert.assertEquals(serviceComponentConfiguration.getName(), "regular Name");
    }

    @Test
    public void setNameToNameThatContainsSymbolsTest() {
        serviceComponentConfiguration.setName("regular Name !$%&/&@");
        Assert.assertEquals(serviceComponentConfiguration.getName(), "regular Name !$%&/&@");
    }

    @Test
    public void setDefinitionToNullTest() {
        serviceComponentConfiguration.setDefinition(null);
        Assert.assertNull(serviceComponentConfiguration.getDefinition());
    }

    @Test
    public void setDefinitionToRegularValueTest() {
        KapuaTocd tocd = new KapuaTocd();
        serviceComponentConfiguration.setDefinition(tocd);
        Assert.assertEquals(serviceComponentConfiguration.getDefinition(), tocd);
    }

    @Test
    public void setPropertiesToNullTest() {
        serviceComponentConfiguration.setProperties(null);
        Assert.assertNull(serviceComponentConfiguration.getProperties());
    }

    @Test
    public void setPropertiesToRegularValueTest() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("property1", 10);
        properties.put("property2", "string");
        properties.put("property3", 'c');
        properties.put("property4", (double) 10);
        serviceComponentConfiguration.setProperties(properties);
        Assert.assertEquals(properties, serviceComponentConfiguration.getProperties());
    }
}
