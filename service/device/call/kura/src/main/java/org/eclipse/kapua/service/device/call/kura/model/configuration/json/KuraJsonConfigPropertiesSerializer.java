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
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class KuraJsonConfigPropertiesSerializer extends JsonSerializer<Map<String, Object>> {

    @Override
    public void serialize(Map<String, Object> properties, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            gen.writeObjectFieldStart(entry.getKey());
            writeProperty(gen, entry.getValue());
            gen.writeEndObject();
        }

        gen.writeEndObject();
    }

    private void writeProperty(JsonGenerator gen, Object value) throws IOException {
        gen.writeFieldName("value");

        if (value == null) {
            gen.writeNull();
            gen.writeStringField("type", "STRING");
            return;
        }

        if (value.getClass().isArray()) {
            writeArray(gen, value);
        } else if (value instanceof Collection) {
            writeCollection(gen, (Collection<?>) value); //TODO: maybe not needed
        } else {
            writePrimitive(gen, value);
            gen.writeStringField("type", determineType(value));
        }
    }

    private void writeArray(JsonGenerator gen, Object array) throws IOException {
        int length = Array.getLength(array);
        gen.writeStartArray();
        Object firstElement = null;
        for (int i = 0; i < length; i++) {
            Object item = Array.get(array, i);
            if (i == 0) {
                firstElement = item;
            }
            writePrimitive(gen, item);
        }
        gen.writeEndArray();
        gen.writeStringField("type", determineType(firstElement));
    }

    private void writeCollection(JsonGenerator gen, Collection<?> collection) throws IOException {
        gen.writeStartArray();
        Object firstElement = null;
        boolean first = true;
        for (Object item : collection) {
            if (first) {
                firstElement = item;
                first = false;
            }
            writePrimitive(gen, item);
        }
        gen.writeEndArray();
        gen.writeStringField("type", determineType(firstElement));
    }

    private void writePrimitive(JsonGenerator gen, Object value) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value instanceof Number) {
            writeNumber(gen, (Number) value);
        } else if (value instanceof Boolean) {
            gen.writeBoolean((Boolean) value);
        } else {
            gen.writeString(value.toString());
        }
    }

    private void writeNumber(JsonGenerator gen, Number value) throws IOException {
        if (value instanceof Integer) {
            gen.writeNumber(value.intValue());
        } else if (value instanceof Long) {
            gen.writeNumber(value.longValue());
        } else if (value instanceof Double) {
            gen.writeNumber(value.doubleValue());
        } else if (value instanceof Float) {
            gen.writeNumber(value.floatValue());
        } else if (value instanceof Byte || value instanceof Short) {
            gen.writeNumber(value.intValue());
        } else {
            gen.writeNumber(value.doubleValue());
        }
    }

    private String determineType(Object value) {
        if (value == null) {
            return "STRING";
        } else if (value instanceof Integer) {
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
