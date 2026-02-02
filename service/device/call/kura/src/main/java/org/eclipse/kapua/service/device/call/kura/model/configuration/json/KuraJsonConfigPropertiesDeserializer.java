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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KuraJsonConfigPropertiesDeserializer extends JsonDeserializer<Map<String, Object>> {

    @Override
    public Map<String, Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Map<String, Object> result = new HashMap<>();

        if (jp.getCurrentToken() == null) {
            jp.nextToken();
        }

        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            ctxt.reportWrongTokenException(this, JsonToken.START_OBJECT, "Expected START_OBJECT");
            return result;
        }

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String propertyName = jp.getCurrentName();
            jp.nextToken();

            PropertyHolder holder = parseProperty(jp);

            if (holder.value != null && holder.type != null) {
                result.put(propertyName, convertToType(holder.value, holder.type));
            } else if (holder.value != null) {
                result.put(propertyName, holder.value);
            }
        }

        return result;
    }

    private PropertyHolder parseProperty(JsonParser jp) throws IOException {
        PropertyHolder holder = new PropertyHolder();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            jp.nextToken();

            if ("value".equals(fieldName)) {
                holder.value = parseValue(jp);
            } else if ("type".equals(fieldName)) {
                holder.type = jp.getValueAsString();
            }
        }
        return holder;
    }

    private Object parseValue(JsonParser jp) throws IOException {
        if (jp.currentToken() == JsonToken.START_ARRAY) {
            List<Object> values = new ArrayList<>();
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                values.add(parsePrimitive(jp));
            }
            return values;
        }
        return parsePrimitive(jp);
    }

    private Object parsePrimitive(JsonParser jp) throws IOException {
        switch (jp.currentToken()) {
            case VALUE_NUMBER_INT:
                return jp.getNumberValue();
            case VALUE_NUMBER_FLOAT:
                return jp.getDoubleValue();
            case VALUE_TRUE:
            case VALUE_FALSE:
                return jp.getBooleanValue();
            case VALUE_STRING:
                return jp.getText();
            case VALUE_NULL:
            default:
                return null;
        }
    }

    private Object convertToType(Object value, String type) {
        if (value instanceof List) {
            List<Object> converted = new ArrayList<>();
            for (Object item : (List<?>) value) {
                converted.add(convertSingleValue(item, type));
            }
            return converted;
        }
        return convertSingleValue(value, type);
    }

    private Object convertSingleValue(Object value, String type) {
        if (value == null) {
            return null;
        }

        try {
            switch (type.toUpperCase()) {
                case "INTEGER":
                    return toNumber(value).intValue();
                case "LONG":
                    return toNumber(value).longValue();
                case "DOUBLE":
                    return toNumber(value).doubleValue();
                case "FLOAT":
                    return toNumber(value).floatValue();
                case "BYTE":
                    return toNumber(value).byteValue();
                case "SHORT":
                    return toNumber(value).shortValue();
                case "BOOLEAN":
                    return value instanceof Boolean ? value : Boolean.parseBoolean(value.toString());
                case "CHAR":
                    String str = value.toString();
                    return str.isEmpty() ? null : str.charAt(0);
                case "STRING":
                case "PASSWORD":
                default:
                    return value.toString();
            }
        } catch (Exception e) {
            return value;
        }
    }

    private Number toNumber(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        }
        return Double.parseDouble(value.toString());
    }

    private static class PropertyHolder {
        Object value;
        String type;
    }

}