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
 * Tests for {@link ObjectTypeConverter}
 *
 * @since 1.3.0
 */
@Category(JUnitTests.class)
public class ObjectTypeConverterTest {

    //
    // toString
    //

    @Test
    public void toStringClassString() {
        String convertedClassString = ObjectTypeConverter.toString(String.class);

        Assert.assertEquals("string", convertedClassString);
    }

    @Test
    public void toStringClassInteger() {
        String convertedClassString = ObjectTypeConverter.toString(Integer.class);

        Assert.assertEquals("integer", convertedClassString);
    }

    @Test
    public void toStringClassLong() {
        String convertedClassString = ObjectTypeConverter.toString(Long.class);

        Assert.assertEquals("long", convertedClassString);
    }

    @Test
    public void toStringClassFloat() {
        String convertedClassString = ObjectTypeConverter.toString(Float.class);

        Assert.assertEquals("float", convertedClassString);
    }

    @Test
    public void toStringClassDouble() {
        String convertedClassString = ObjectTypeConverter.toString(Double.class);

        Assert.assertEquals("double", convertedClassString);
    }

    @Test
    public void toStringClassBoolean() {
        String convertedClassString = ObjectTypeConverter.toString(Boolean.class);

        Assert.assertEquals("boolean", convertedClassString);
    }

    @Test
    public void toStringClassDate() {
        String convertedClassString = ObjectTypeConverter.toString(Date.class);

        Assert.assertEquals("date", convertedClassString);
    }

    @Test
    public void toStringClassBinaryClass() {
        String convertedClassString = ObjectTypeConverter.toString(Byte[].class);

        Assert.assertEquals("binary", convertedClassString);
    }

    @Test
    public void toStringClassBinaryPrimitive() {
        String convertedClassString = ObjectTypeConverter.toString(byte[].class);

        Assert.assertEquals("binary", convertedClassString);
    }

    @Test
    public void toStringClassSomeClass() {
        String convertedClassString = ObjectTypeConverter.toString(KapuaId.class);

        Assert.assertEquals("org.eclipse.kapua.model.id.KapuaId", convertedClassString);
    }

    @Test
    public void toStringClassNull() {
        String convertedClassString = ObjectTypeConverter.toString(null);

        Assert.assertNull(convertedClassString);
    }


    //
    // fromString
    //

    @Test
    public void fromStringStringString() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("string");

        Assert.assertEquals(String.class, convertedClassString);
    }

    @Test
    public void fromStringStringInteger() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("integer");

        Assert.assertEquals(Integer.class, convertedClassString);

        convertedClassString = ObjectTypeConverter.fromString("int");

        Assert.assertEquals(Integer.class, convertedClassString);
    }

    @Test
    public void fromStringStringLong() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("long");

        Assert.assertEquals(Long.class, convertedClassString);
    }

    @Test
    public void fromStringStringFloat() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("float");

        Assert.assertEquals(Float.class, convertedClassString);
    }

    @Test
    public void fromStringStringDouble() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("double");

        Assert.assertEquals(Double.class, convertedClassString);
    }

    @Test
    public void fromStringStringBoolean() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("boolean");

        Assert.assertEquals(Boolean.class, convertedClassString);
    }

    @Test
    public void fromStringStringDate() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("date");

        Assert.assertEquals(Date.class, convertedClassString);
    }

    @Test
    public void fromStringStringBinary() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("binary");

        Assert.assertEquals(byte[].class, convertedClassString);
    }

    @Test
    public void fromStringStringSomeClass() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("org.eclipse.kapua.model.id.KapuaId");

        Assert.assertEquals(KapuaId.class, convertedClassString);
    }

    @Test
    public void fromStringStringNull() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString(null);

        Assert.assertNull(convertedClassString);
    }

    @Test
    public void fromStringStringEmpty() throws ClassNotFoundException {
        Class<?> convertedClassString = ObjectTypeConverter.fromString("");

        Assert.assertNull(convertedClassString);
    }

    @Test(expected = ClassNotFoundException.class)
    public void fromStringStringNotClass() throws ClassNotFoundException {
        ObjectTypeConverter.fromString("org.eclipse.kapua.this.is.not.a.Class");
    }
}
