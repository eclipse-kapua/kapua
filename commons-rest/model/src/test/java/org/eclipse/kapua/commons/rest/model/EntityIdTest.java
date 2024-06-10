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

import java.math.BigInteger;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class EntityIdTest {

    @Test
    public void entityIdTest() {
        String[] compactEntityId = { "111", "entityId", "1000000", "-111", "12" };
        String[] expectedValues = { "-10403", "134670355735069", "-174798351539", "-303755", "-41" };

        for (int i = 0; i < compactEntityId.length; i++) {
            EntityId entityId = new EntityId(compactEntityId[i]);

            Assert.assertEquals("Expected and actual values should be the same.", expectedValues[i], entityId.getId().toString());
            Assert.assertEquals("Expected and actual values should be the same.", expectedValues[i], entityId.toString());
        }
    }

    @Test(expected = NullPointerException.class)
    public void entityIdNullTest() {
        new EntityId(null);
    }

    @Test(expected = NumberFormatException.class)
    public void entityIdEmptyTest() {
        new EntityId("");
    }

    @Test
    public void setAndGetIdToStringTest() {
        EntityId entityId = new EntityId("111");

        Assert.assertEquals("Expected and actual values should be the same.", "-10403", entityId.getId().toString());
        Assert.assertEquals("Expected and actual values should be the same.", "-10403", entityId.toString());

        entityId.setId(BigInteger.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", "1", entityId.getId().toString());
        Assert.assertEquals("Expected and actual values should be the same.", "1", entityId.toString());

        entityId.setId(null);
        Assert.assertNull("Null expected.", entityId.getId());
    }

    @Test(expected = NullPointerException.class)
    public void toStringNullIdTest() {
        EntityId entityId = new EntityId("111");
        entityId.setId(null);
        entityId.toString();
    }
} 