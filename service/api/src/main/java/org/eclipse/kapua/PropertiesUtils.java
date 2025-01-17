/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import com.google.common.base.Strings;

/**
 * Utilities around the Java {@link Properties} class.
 *
 * @since 1.1.0
 */
public class PropertiesUtils {

    private PropertiesUtils() {
    }

    /**
     * Reads the given {@link String} and loads it into the returned {@link Properties}.
     *
     * @param stringProperties
     *         The source {@link String}
     * @return The loaded {@link Properties}
     * @throws IOException
     *         If the {@link String} is not properly formatted.
     * @see Properties#load(Reader)
     * @since 1.1.0
     */
    public static Properties readPropertiesFromString(String stringProperties) throws IOException {
        Properties props = new Properties();
        if (!Strings.isNullOrEmpty(stringProperties)) {
            try (Reader reader = new StringReader(stringProperties)) {
                props.load(reader);
            }
        }
        return props;
    }

    /**
     * Writes the given {@link Properties} into the returned {@link String}
     *
     * @param properties
     *         The source {@link Properties}
     * @return The written {@link Properties}.
     * @throws IOException
     *         if error occurs while writing properties.
     * @see Properties#store(Writer, String)
     * @since 1.1.0
     */
    public static String writePropertiesToString(Properties properties) throws IOException {
        String stringProperties = null;
        if (properties != null) {
            try (Writer writer = new StringWriter()) {
                properties.store(writer, null);
                stringProperties = writer.toString();
            }
        }
        return stringProperties;
    }
}
