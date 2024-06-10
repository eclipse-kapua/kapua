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
package org.eclipse.kapua.commons.rest.model;

import java.util.Date;

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class MetricTypeTest {

    @Test
    public void metricTypeTest() throws KapuaIllegalArgumentException {
        String[] types = { "string", "integer", "int", "long", "float", "double", "boolean", "date", "binary" };
        Class[] expectedClasses = { String.class, Integer.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, Date.class, byte[].class };

        for (int i = 0; i < types.length; i++) {
            MetricType metricType = new MetricType(types[i]);
            Assert.assertEquals("Expected and actual values should be the same.", expectedClasses[i], metricType.getType());
        }
    }

    @Test
    public void metricEmptyTypeTest() throws KapuaIllegalArgumentException {
        MetricType metricType = new MetricType("");
        Assert.assertNull("Null expected.", metricType.getType());
    }

    @Test
    public void metricNullTypeTest() throws KapuaIllegalArgumentException {
        MetricType metricType = new MetricType(null);
        Assert.assertNull("Null expected.", metricType.getType());
    }

    @Test(expected = KapuaIllegalArgumentException.class)
    public void metricInvalidTypeTest() throws KapuaIllegalArgumentException {
        new MetricType("invalid type");
    }
}