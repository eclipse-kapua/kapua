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

import java.util.Properties;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class ServiceConfigCreatorTest {

    ServiceConfigCreator configCreator;

    @Before
    public void createInstanceOfClass() {
        KapuaId id = KapuaId.ONE;
        configCreator = new ServiceConfigCreator(id);
    }

    @Test
    public void setPidTest() {
        configCreator.setPid("1");
        Assert.assertEquals(configCreator.getPid(), "1");
    }

    @Test
    public void getPidTest() {
        Assert.assertNull(configCreator.getPid());
        configCreator.setPid("123");
        Assert.assertEquals(configCreator.getPid(), "123");
    }

    @Test
    public void setPidToStringTest() {
        Assert.assertNull(configCreator.getPid());
        configCreator.setPid("asdf");
        Assert.assertEquals(configCreator.getPid(), "asdf");
    }

    @Test
    public void setPidToSymbolsTest() {
        Assert.assertNull(configCreator.getPid());
        configCreator.setPid("@!#$%&");
        Assert.assertEquals(configCreator.getPid(), "@!#$%&");
    }

    @Test
    public void setAndGetConfigurationsTest() {
        Properties properties = new Properties();
        properties.setProperty("prop1", "value1");
        configCreator.setConfigurations(properties);
        Assert.assertEquals(configCreator.getConfigurations(), properties);
    }
}
