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
import java.util.HashMap;
import java.util.Map;

public class KuraJsonConfigPropertiesDeserializer extends JsonDeserializer<Map<String, Object>> {

    @Override
    public Map<String, Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Map<String, Object> result = new HashMap<>();

        // We expect to be at START_OBJECT for the properties object
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == null) {
            currentToken = jp.nextToken();
        }

        if (currentToken != JsonToken.START_OBJECT) {
            ctxt.reportWrongTokenException(this, JsonToken.START_OBJECT, "Expected START_OBJECT");
            return result;
        }

        // Iterate through each property
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String propertyName = jp.getCurrentName();
            jp.nextToken(); // Move to START_OBJECT of property value

            String value = null;
            String type = null;

            // Read the property object {value: ..., type: ...}
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                jp.nextToken(); // Move to field value

                if ("value".equals(fieldName)) {
                    value = jp.getValueAsString();
                } else if ("type".equals(fieldName)) {
                    type = jp.getValueAsString();
                }
            }

            // Convert and store the value
            if (value != null && type != null) {
                result.put(propertyName, convertValue(value, type));
            } else if (value != null) {
                result.put(propertyName, value);
            }
        }

        return result;
    }

    private Object convertValue(String value, String type) {
        if (type == null) {
            return value;
        }
        try {
            switch (type.toUpperCase()) {
                case "INTEGER":
                    return Integer.parseInt(value);
                case "LONG":
                    return Long.parseLong(value);
                case "DOUBLE":
                case "FLOAT":
                    return Double.parseDouble(value);
                case "BOOLEAN":
                    return Boolean.parseBoolean(value);
                case "STRING":
                default:
                    return value;
            }
        } catch (NumberFormatException e) {
            return value;
        }
    }

}