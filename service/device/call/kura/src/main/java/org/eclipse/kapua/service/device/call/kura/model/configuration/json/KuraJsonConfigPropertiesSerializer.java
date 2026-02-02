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
import java.util.List;
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

                // Write value
                gen.writeFieldName("value");

                // Handle arrays
                if (propertyValue instanceof List) {
                    gen.writeStartArray();
                    for (Object item : (List<?>) propertyValue) {
                        writeValue(gen, item);
                    }
                    gen.writeEndArray();
                } else {
                    writeValue(gen, propertyValue);
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

    private void writeValue(JsonGenerator gen, Object value) throws IOException {
        if (value instanceof Integer) {
            gen.writeNumber((Integer) value);
        } else if (value instanceof Long) {
            gen.writeNumber((Long) value);
        } else if (value instanceof Double) {
            gen.writeNumber((Double) value);
        } else if (value instanceof Float) {
            gen.writeNumber((Float) value);
        } else if (value instanceof Byte) {
            gen.writeNumber((Byte) value);
        } else if (value instanceof Short) {
            gen.writeNumber((Short) value);
        } else if (value instanceof Boolean) {
            gen.writeBoolean((Boolean) value);
        } else {
            gen.writeString(value.toString());
        }
    }

    private String determineType(Object value) {
        // Handle arrays - get type from first element
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            if (list.isEmpty()) {
                return "STRING";
            }
            return determineType(list.get(0));
        }

        // Single values
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
            return "STRING";
        }
    }
}
