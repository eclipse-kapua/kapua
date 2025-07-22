/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Converts {@link Date} into {@link String} and vice-versa
 *
 * @since 2.1.0
 */
public class DateConverter {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");

    private DateConverter() {
    }

    /**
     * Converts the given {@link Date} to {@link String} using the {@link #DATE_FORMAT} format
     *
     * @param date The {@link Date} to convert, can be {@code null}
     * @return The {@link String} formatted {@link Date}
     * @since 2.1.0
     */
    public static String toString(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setTimeZone(TIME_ZONE_UTC);
        return format.format(date);
    }

    /**
     * Converts the given {@link String} into {@link Date}
     *
     * @param stringDate The {@link String} date to convert, can be null
     * @return The converted{@link Date}
     * @since 2.1.0
     */
    public static Date fromString(String stringDate) {
        if (Strings.isNullOrEmpty(stringDate)) {
            return null;
        }

        return DatatypeConverter.parseDateTime(stringDate).getTime();
    }
}
