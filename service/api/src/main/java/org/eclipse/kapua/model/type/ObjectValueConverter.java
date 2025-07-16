/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.model.id.KapuaIdImpl;

import java.math.BigInteger;
import java.util.Date;

/**
 * Utilities to convert the value of objects to serialize them.
 *
 * @since 1.0.0
 */
public class ObjectValueConverter {

    private ObjectValueConverter() {
    }

    /**
     * Converts the given value to its {@link String} representation.
     * <p>
     * For {@link Byte}[] and {@code byte}[] {@link ByteArrayConverter#toString(Byte[])} is used.
     * Other types are converted using {@link Object#toString()}
     *
     * @param value The value to convert.
     * @return The {@link String} representation.
     * @since 1.0.0
     */
    public static String toString(Object value) {

        String stringValue = null;
        if (value != null) {
            Class<?> clazz = value.getClass();
            if (clazz == byte[].class) {
                stringValue = ByteArrayConverter.toString((byte[]) value);
            } else if (clazz == Byte[].class) {
                stringValue = ByteArrayConverter.toString((Byte[]) value);
            } else if (clazz == Date.class) {
                stringValue = DateConverter.toString((Date) value);
            } else {
                // String
                // Integer
                // Long
                // Float
                // Double
                // Boolean
                // Enum
                stringValue = value.toString();
            }
        }

        return stringValue;
    }

    /**
     * Converts the given {@link String} into the given {@link Class} type.
     *
     * @param stringValue The {@link String} to convert.
     * @param type        The {@link Class} to which to convert to.
     * @return The converted value.
     * @since 1.0.0
     */
    public static Object fromString(String stringValue, Class<?> type) {

        Object value = null;
        if (stringValue != null) {
            if (type == String.class) {
                value = stringValue;
            } else if (type == Integer.class) {
                value = Integer.parseInt(stringValue);
            } else if (type == Long.class) {
                value = Long.parseLong(stringValue);
            } else if (type == Float.class) {
                value = Float.parseFloat(stringValue);
            } else if (type == Double.class) {
                value = Double.parseDouble(stringValue);
            } else if (type == Boolean.class) {
                value = Boolean.parseBoolean(stringValue);
            } else if (type == byte[].class || type == Byte[].class) {
                value = ByteArrayConverter.fromString(stringValue);
            } else if (type == Date.class) {
                value = DateConverter.fromString(stringValue);
            } else if (type == KapuaId.class || KapuaId.class.isAssignableFrom(type)) {
                value = new KapuaIdImpl(new BigInteger(stringValue));
            } else if (type.isEnum()) {
                Class<? extends Enum> enumType = (Class<? extends Enum>) type;

                value = Enum.valueOf(enumType, stringValue);
            } else {
                value = stringValue;
            }
        }

        return value;
    }
}
