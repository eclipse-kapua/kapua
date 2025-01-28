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

        kapuaTicon = new KapuaTicon();
    }

    KapuaTicon kapuaTicon;

    @Test
    public void getAnyTest() {
        Assert.assertTrue(kapuaTicon.getAny().isEmpty());
    }

    @Test
    public void setAndGetResourcesToNullTest() {
        kapuaTicon.setResource(null);
        Assert.assertNull(kapuaTicon.getResource());
    }

    @Test
    public void setAndGetResourcesTest() {
        String[] permittedValues = { "", "regularResources", "49", "regular Resources", "regular esources with spaces", "!@#$%&*()_+/->,<", "RESOURCES", "resources123" };
        for (String value : permittedValues) {
            kapuaTicon.setResource(value);
            Assert.assertTrue(kapuaTicon.getResource().contains(value));
        }
    }

    @Test
    public void setAndGetSizeToNullTest() {
        kapuaTicon.setSize(null);
        Assert.assertNull(kapuaTicon.getSize());
    }

    @Test
    public void setAndGetSizeBigIntTest() {
        String numStr = "453453453456465765234923423094723472394723423482304823095734957320948305712324000123123";
        BigInteger num = new BigInteger(numStr);
        kapuaTicon.setSize(num);
        Assert.assertEquals("KapuaTicon.size", num, kapuaTicon.getSize());
    }

    @Test
    public void getOtherAttributesTest() {
        Assert.assertTrue(kapuaTicon.getOtherAttributes().isEmpty());
    }
}
