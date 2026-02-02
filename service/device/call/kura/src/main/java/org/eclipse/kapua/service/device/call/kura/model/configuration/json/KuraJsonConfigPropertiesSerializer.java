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
package org.eclipse.kapua.service.device.call.kura.model.configuration.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

public class KuraJsonConfigPropertiesSerializer extends JsonSerializer<Map<String, Object>> {

    @Override
    public void serialize(Map<String, Object> properties, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            Object propertyValue = entry.getValue();

            gen.writeObjectFieldStart(propertyName);

            if (propertyValue != null) {
                String type = determineType(propertyValue);

                // Write value as the appropriate JSON type
                gen.writeFieldName("value");
                if (propertyValue instanceof Integer) {
                    gen.writeNumber((Integer) propertyValue);
                } else if (propertyValue instanceof Long) {
                    gen.writeNumber((Long) propertyValue);
                } else if (propertyValue instanceof Double) {
                    gen.writeNumber((Double) propertyValue);
                } else if (propertyValue instanceof Float) {
                    gen.writeNumber((Float) propertyValue);
                } else if (propertyValue instanceof Byte) {
                    gen.writeNumber((Byte) propertyValue);
                } else if (propertyValue instanceof Short) {
                    gen.writeNumber((Short) propertyValue);
                } else if (propertyValue instanceof Boolean) {
                    gen.writeBoolean((Boolean) propertyValue);
                } else if (propertyValue instanceof Character) {
                    gen.writeString(propertyValue.toString());
                } else {
                    gen.writeString(propertyValue.toString());
                }
                gen.writeStringField("type", type);
            } else {
                gen.writeNullField("value");
                gen.writeStringField("type", "STRING");
            }

            gen.writeEndObject();
        }

        gen.writeEndObject();
    }

    private String determineType(Object value) {
        if (value instanceof Integer) {
            return "INTEGER";
        } else if (value instanceof Long) {
            return "LONG";
        } else if (value instanceof Double) {
            return "DOUBLE";
        } else if (value instanceof Float) {
            return "FLOAT";
        } else if (value instanceof Byte) {
            return "BYTE";
        } else if (value instanceof Short) {
            return "SHORT";
        } else if (value instanceof Boolean) {
            return "BOOLEAN";
        } else if (value instanceof Character) {
            return "CHAR";
        } else {
            return "STRING"; // Default for String and PASSWORD
        }
    }
}
