/*******************************************************************************
 * Copyright (c) 2020, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.type;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests for {@link ByteArrayConverter}
 *
 * @since 1.3.0
 */
@Category(JUnitTests.class)
public class ByteArrayConverterTest {

    Byte[] byteClassArray;
    byte[] bytePrimitiveArray;
    String byteArrayString;

    @Before
    public void initialize() {
        byteClassArray = new Byte[]{-128, -10, 0, 1, 10, 127};
        bytePrimitiveArray = new byte[]{-128, -10, 0, 1, 10, 127};
        byteArrayString = "gPYAAQp/";
    }

    @Test
    public void toStringConvertByteClassArray() {
        String convertedByteArrayString = ByteArrayConverter.toString(byteClassArray);

        Assert.assertEquals(byteArrayString, convertedByteArrayString);
    }

    @Test
    public void toStringConvertByteClassNull() {
        String convertedByteArrayString = ByteArrayConverter.toString((Byte[]) null);

        Assert.assertNull(convertedByteArrayString);
    }

    @Test
    public void toStringConvertBytePrimitiveArray() {
        String convertedByteArrayString = ByteArrayConverter.toString(bytePrimitiveArray);

        Assert.assertEquals(byteArrayString, convertedByteArrayString);
    }

    @Test
    public void toStringConvertBytePrimitiveNull() {
        String convertedByteArrayString = ByteArrayConverter.toString((byte[]) null);

        Assert.assertNull(convertedByteArrayString);
    }


    @Test
    public void fromStringConvertString() {
        byte[] convertedByteArray = ByteArrayConverter.fromString(byteArrayString);

        Assert.assertArrayEquals(convertedByteArray, bytePrimitiveArray);
    }

    @Test
    public void fromStringConvertStringNull() {
        byte[] convertedByteArray = ByteArrayConverter.fromString(null);

        Assert.assertNull(convertedByteArray);
    }

    @Test
    public void fromStringConvertStringEmpty() {
        byte[] convertedByteArray = ByteArrayConverter.fromString("");

        Assert.assertNotNull(convertedByteArray);
        Assert.assertArrayEquals(convertedByteArray, new byte[0]);
    }

}
