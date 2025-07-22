/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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

import com.google.common.base.Strings;

import java.util.Date;

public class ObjectTypeConverter {

    private static final String TYPE_STRING = "string";
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_INT = "int";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_DATE = "date";
    private static final String TYPE_BINARY = "binary";

    private ObjectTypeConverter() {
    }

    public static String toString(Class<?> clazz) {
        String value = null;
        if (clazz != null) {
            if (clazz == String.class) {
                value = TYPE_STRING;
            } else if (clazz == Integer.class) {
                value = TYPE_INTEGER;
            } else if (clazz == Long.class) {
                value = TYPE_LONG;
            } else if (clazz == Float.class) {
                value = TYPE_FLOAT;
            } else if (clazz == Double.class) {
                value = TYPE_DOUBLE;
            } else if (clazz == Boolean.class) {
                value = TYPE_BOOLEAN;
            } else if (clazz == Date.class) {
                value = TYPE_DATE;
            } else if (clazz == byte[].class || clazz == Byte[].class) {
                value = TYPE_BINARY;
            } else {
                value = clazz.getName();
            }
        }
        return value;
    }

    public static Class<?> fromString(String value) throws ClassNotFoundException {
        if (!Strings.isNullOrEmpty(value)) {
            switch (value) {
                case TYPE_STRING:
                    return String.class;
                case TYPE_INTEGER:
                case TYPE_INT:
                    return Integer.class;
                case TYPE_LONG:
                    return Long.class;
                case TYPE_FLOAT:
                    return Float.class;
                case TYPE_DOUBLE:
                    return Double.class;
                case TYPE_BOOLEAN:
                    return Boolean.class;
                case TYPE_DATE:
                    return Date.class;
                case TYPE_BINARY:
                    return byte[].class;
                default:
                    return Class.forName(value);
            }
        }

        return null;
    }

}
