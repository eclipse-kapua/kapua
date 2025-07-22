/*******************************************************************************
 * Copyright (c) 2017, 2025 Eurotech and/or its affiliates and others
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

import javax.xml.bind.DatatypeConverter;

/**
 * Converts a {@link Byte}[] to {@link String} and vice-versa
 *
 * @since 1.0.0
 */
public class ByteArrayConverter {

    private ByteArrayConverter() {
    }

    /**
     * Converts the given {@link Byte}[] to a Base64 {@link String}
     *
     * @param byteArray The {@link Byte}[] to convert, can be {@code null}
     * @return The Base64 representation.
     * @since 1.0.0
     */
    public static String toString(Byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        byte[] unboxedByteArray = new byte[byteArray.length];

        for (int i = 0; i < byteArray.length; i++) {
            unboxedByteArray[i] = byteArray[i];
        }

        return toString(unboxedByteArray);
    }

    /**
     * Converts the given {@code byte}[] to a Base64 {@link String}
     *
     * @param byteArray The {@code byte}[] to convert, can be {@code null}
     * @return The Base64 representation.
     * @since 1.0.0
     */
    public static String toString(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        return DatatypeConverter.printBase64Binary(byteArray);
    }

    /**
     * Converts the given Base64 {@link String} into a {@code byte}[]
     *
     * @param base64 The Base64 {@link String}, can be {@code null}.
     * @return The converted {@code byte[]}
     * @since 1.0.0
     */
    public static byte[] fromString(String base64) {
        if (base64 == null) {
            return null;
        }

        if (base64.isEmpty()) {
            return new byte[0];
        }

        return DatatypeConverter.parseBase64Binary(base64);
    }
}
