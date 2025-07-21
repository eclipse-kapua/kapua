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

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Date;

/**
 * Tests for {@link ObjectValueConverter}
 *
 * @since 1.3.0
 */
@Category(JUnitTests.class)
public class ObjectValueConverterTest {

    //
    // toString
    //

    @Test
    public void toStringObjectString() {
        String convertedObjectString = ObjectValueConverter.toString("aString");

        Assert.assertEquals("aString", convertedObjectString);
    }

    @Test
    public void toStringObjectInteger() {
        String convertedObjectString = ObjectValueConverter.toString(1024);

        Assert.assertEquals("1024", convertedObjectString);
    }

    @Test
    public void toStringObjectLong() {
        String convertedObjectString = ObjectValueConverter.toString(1024L);

        Assert.assertEquals("1024", convertedObjectString);
    }

    @Test
    public void toStringObjectFloat() {
        String convertedObjectString = ObjectValueConverter.toString(10.24f);

        Assert.assertEquals("10.24", convertedObjectString);
    }

    @Test
    public void toStringObjectDouble() {
        String convertedObjectString = ObjectValueConverter.toString(10.24d);

        Assert.assertEquals("10.24", convertedObjectString);
    }

    @Test
    public void toStringObjectBoolean() {
        String convertedObjectString = ObjectValueConverter.toString(Boolean.TRUE);

        Assert.assertEquals("true", convertedObjectString);
    }

    @Test
    public void toStringObjectDate() {
        String convertedObjectString = ObjectValueConverter.toString(new Date(1735689600000L));

        Assert.assertEquals("2025-01-01T00:00:00.000Z", convertedObjectString);
    }

    @Test
    public void toStringObjectBinaryClass() {
        String convertedObjectString = ObjectValueConverter.toString(new Byte[]{-128, -10, 0, 1, 10, 127});

        Assert.assertEquals("gPYAAQp/", convertedObjectString);
    }

    @Test
    public void toStringObjectBinaryPrimitive() {
        String convertedObjectString = ObjectValueConverter.toString(new byte[]{-128, -10, 0, 1, 10, 127});

        Assert.assertEquals("gPYAAQp/", convertedObjectString);
    }

    @Test
    public void toStringObjectSomeClass() {
        String convertedObjectString = ObjectValueConverter.toString(KapuaId.ONE);

        Assert.assertEquals("1", convertedObjectString);
    }

    @Test
    public void toStringObjectEnum() {
        String convertedObjectString = ObjectValueConverter.toString(ObjectValueConverterTestEnum.TEST);

        Assert.assertEquals("TEST", convertedObjectString);
    }

    @Test
    public void toStringObjectNull() {
        String convertedObjectString = ObjectValueConverter.toString(null);

        Assert.assertNull(convertedObjectString);
    }

    //
    // fromString
    //

    @Test
    public void fromStringString() {
        Object convertedObject = ObjectValueConverter.fromString("aString", String.class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(String.class, convertedObject.getClass());
        Assert.assertEquals("aString", convertedObject);
    }

    @Test
    public void fromStringInteger() {
        Object convertedObject = ObjectValueConverter.fromString("1024", Integer.class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(Integer.class, convertedObject.getClass());
        Assert.assertEquals(1024, convertedObject);
    }

    @Test(expected = NumberFormatException.class)
    public void fromStringIntegerNai() {
        ObjectValueConverter.fromString("NaI", Integer.class);
    }

    @Test
    public void fromStringLong() {
        Object convertedObject = ObjectValueConverter.fromString("1024", Long.class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(Long.class, convertedObject.getClass());
        Assert.assertEquals(1024L, convertedObject);
    }

    @Test(expected = NumberFormatException.class)
    public void fromStringLongNal() {
        ObjectValueConverter.fromString("NaL", Long.class);
    }

    @Test
    public void fromStringFloat() {
        Object convertedObject = ObjectValueConverter.fromString("10.24", Float.class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(Float.class, convertedObject.getClass());
        Assert.assertEquals(10.24f, convertedObject);
    }

    @Test(expected = NumberFormatException.class)
    public void fromStringFloatNaf() {
        ObjectValueConverter.fromString("NaF", Float.class);
    }

    @Test
    public void fromStringDouble() {
        Object convertedObject = ObjectValueConverter.fromString("10.24", Double.class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(Double.class, convertedObject.getClass());
        Assert.assertEquals(10.24d, convertedObject);
    }

    @Test(expected = NumberFormatException.class)
    public void fromStringDoubleNad() {
        ObjectValueConverter.fromString("NaD", Double.class);
    }

    @Test
    public void fromStringBoolean() {
        Object convertedObject = ObjectValueConverter.fromString("true", Boolean.class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(Boolean.class, convertedObject.getClass());
        Assert.assertEquals(Boolean.TRUE, convertedObject);
    }

    @Test(expected = NumberFormatException.class)
    public void fromStringBooleanNab() {
        ObjectValueConverter.fromString("NaB", Double.class);
    }

    @Test
    public void fromStringDate() {
        Object convertedObject = ObjectValueConverter.fromString("2025-01-01T00:00:00.000Z", Date.class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(Date.class, convertedObject.getClass());
        Assert.assertEquals(new Date(1735689600000L), convertedObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromStringDateNad() {
        ObjectValueConverter.fromString("NaD", Date.class);
    }

    @Test
    public void fromStringByteClass() {
        Object convertedObject = ObjectValueConverter.fromString("gPYAAQp/", Byte[].class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(byte[].class, convertedObject.getClass());
        Assert.assertArrayEquals(new byte[]{-128, -10, 0, 1, 10, 127}, (byte[]) convertedObject);
    }

    @Test
    public void fromStringBytePrimitive() {
        Object convertedObject = ObjectValueConverter.fromString("gPYAAQp/", byte[].class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(byte[].class, convertedObject.getClass());
        Assert.assertArrayEquals(new byte[]{-128, -10, 0, 1, 10, 127}, (byte[]) convertedObject);
    }

    @Test
    public void fromStringEnum() {
        Object convertedObject = ObjectValueConverter.fromString("TEST", ObjectValueConverterTestEnum.class);

        Assert.assertNotNull(convertedObject);
        Assert.assertEquals(ObjectValueConverterTestEnum.class, convertedObject.getClass());
        Assert.assertEquals(ObjectValueConverterTestEnum.TEST, convertedObject);
    }

    @Test
    public void fromStringNull() {
        Object convertedObject = ObjectValueConverter.fromString(null, Object.class);

        Assert.assertNull(convertedObject);
    }
}
