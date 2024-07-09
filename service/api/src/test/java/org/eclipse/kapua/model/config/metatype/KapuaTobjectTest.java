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
package org.eclipse.kapua.model.config.metatype;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaTobjectTest {

    @Before
    public void createInstanceOfClasses() {
        tobjectImpl = new KapuaTobject();
    }

    KapuaTobject tobjectImpl;

    @Test
    public void getAttributeTest() {
        Assert.assertTrue(tobjectImpl.getAttribute().isEmpty());
    }

    @Test
    public void getAnyTest() {
        Assert.assertTrue(tobjectImpl.getAny().isEmpty());
    }

    @Test
    public void setAndGetOcdrefToNullTest() {
        tobjectImpl.setOcdref(null);
        Assert.assertNull(tobjectImpl.getOcdref());
    }

    @Test
    public void setAndGetOcdrefTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularOcdref", "regular Ocdref", "49", "regularOcdref49", "OCDREF",
                "246465494135646120009090049684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            tobjectImpl.setOcdref(value);
            Assert.assertTrue(tobjectImpl.getOcdref().contains(value));
        }
    }

    @Test
    public void testGetOtherAttributes() {
        Assert.assertTrue(tobjectImpl.getOtherAttributes().isEmpty());
    }
}
