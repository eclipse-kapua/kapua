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

import java.math.BigInteger;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaTiconTest {

    @Before
    public void createInstanceOfClass() {

        ticonImpl = new KapuaTicon();
    }

    KapuaTicon ticonImpl;

    @Test
    public void getAnyTest() {
        Assert.assertTrue(ticonImpl.getAny().isEmpty());
    }

    @Test
    public void setAndGetResourcesToNullTest() {
        ticonImpl.setResource(null);
        Assert.assertNull(ticonImpl.getResource());
    }

    @Test
    public void setAndGetResourcesTest() {
        String[] permittedValues = { "", "regularResources", "49", "regular Resources", "regular esources with spaces", "!@#$%&*()_+/->,<", "RESOURCES", "resources123" };
        for (String value : permittedValues) {
            ticonImpl.setResource(value);
            Assert.assertTrue(ticonImpl.getResource().contains(value));
        }
    }

    @Test
    public void setAndGetSizeToNullTest() {
        ticonImpl.setSize(null);
        Assert.assertNull(ticonImpl.getSize());
    }

    @Test
    public void setAndGetSizeBigIntTest() {
        String numStr = "453453453456465765234923423094723472394723423482304823095734957320948305712324000123123";
        BigInteger num = new BigInteger(numStr);
        ticonImpl.setSize(num);
        Assert.assertEquals("ticonImpl.size", num, ticonImpl.getSize());
    }

    @Test
    public void getOtherAttributesTest() {
        Assert.assertTrue(ticonImpl.getOtherAttributes().isEmpty());
    }
}
