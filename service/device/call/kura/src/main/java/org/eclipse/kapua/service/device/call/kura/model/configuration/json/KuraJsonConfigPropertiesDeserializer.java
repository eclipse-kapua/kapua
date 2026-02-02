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

        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == null) {
            currentToken = jp.nextToken();
        }

        if (currentToken != JsonToken.START_OBJECT) {
            ctxt.reportWrongTokenException(this, JsonToken.START_OBJECT, "Expected START_OBJECT");
            return result;
        }

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String propertyName = jp.getCurrentName();
            jp.nextToken();

            Object value = null;
            String type = null;

            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                jp.nextToken();

                if ("value".equals(fieldName)) {
                    // Check if value is an array
                    if (jp.currentToken() == JsonToken.START_ARRAY) {
                        List<Object> arrayValues = new ArrayList<>();
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            if (jp.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                                arrayValues.add(jp.getNumberValue());
                            } else if (jp.currentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
                                arrayValues.add(jp.getDoubleValue());
                            } else if (jp.currentToken() == JsonToken.VALUE_TRUE || jp.currentToken() == JsonToken.VALUE_FALSE) {
                                arrayValues.add(jp.getBooleanValue());
                            } else {
                                arrayValues.add(jp.getValueAsString());
                            }
                        }
                        value = arrayValues;
                    } else {
                        value = jp.getValueAsString();
                    }
                } else if ("type".equals(fieldName)) {
                    type = jp.getValueAsString();
                }
            }

            if (value != null && type != null) {
                result.put(propertyName, convertValue(value, type));
            } else if (value != null) {
                result.put(propertyName, value);
            }
        }

        return result;
    }

    private Object convertValue(Object value, String type) {
        if (type == null) {
            return value;
        }

        // Handle array values
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return convertArrayValue(list, type);
        }

        // Handle single values
        if (!(value instanceof String)) {
            return value; // Already parsed as correct type
        }

        String strValue = (String) value;
        try {
            switch (type.toUpperCase()) {
                case "INTEGER":
                    return Integer.parseInt(strValue);
                case "LONG":
                    return Long.parseLong(strValue);
                case "DOUBLE":
                case "FLOAT":
                    return Double.parseDouble(strValue);
                case "BOOLEAN":
                    return Boolean.parseBoolean(strValue);
                case "BYTE":
                    return Byte.parseByte(strValue);
                case "SHORT":
                    return Short.parseShort(strValue);
                case "CHAR":
                    return strValue.charAt(0);
                case "STRING":
                case "PASSWORD":
                default:
                    return strValue;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return strValue;
        }
    }

    private Object convertArrayValue(List<?> list, String type) {
        if (list.isEmpty()) {
            return list;
        }

        // If first element is already the correct type, assume all are correct
        Object firstElement = list.get(0);
        if (isCorrectType(firstElement, type)) {
            return list;
        }

        // Convert string representations to correct types
        List<Object> convertedList = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof String) {
                convertedList.add(convertValue(item, type));
            } else {
                convertedList.add(item);
            }
        }
        return convertedList;
    }

    private boolean isCorrectType(Object value, String type) {
        if (value == null) {
            return true;
        }

        switch (type.toUpperCase()) {
            case "INTEGER":
                return value instanceof Integer;
            case "LONG":
                return value instanceof Long;
            case "DOUBLE":
            case "FLOAT":
                return value instanceof Double || value instanceof Float;
            case "BOOLEAN":
                return value instanceof Boolean;
            case "BYTE":
                return value instanceof Byte;
            case "SHORT":
                return value instanceof Short;
            case "CHAR":
                return value instanceof Character;
            case "STRING":
            case "PASSWORD":
            default:
                return value instanceof String;
        }
    }

}