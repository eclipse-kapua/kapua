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
package org.eclipse.kapua.integration.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraPassword;
import org.eclipse.kapua.service.device.call.kura.model.configuration.json.KuraJsonConfigPropertiesDeserializer;
import org.eclipse.kapua.service.device.call.kura.model.configuration.json.KuraJsonConfigPropertiesSerializer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KuraJsonConfigPropertiesSerializerTest {

    private ObjectMapper objectMapper;

    //Wrapper class to test the serialization of the properties map with the custom serializer and deserializer
    static class PropertiesWrapper {
        @JsonDeserialize(using = KuraJsonConfigPropertiesDeserializer.class)
        @JsonSerialize(using = KuraJsonConfigPropertiesSerializer.class)
        private Map<String, Object> properties;

        public PropertiesWrapper() {
        }

        public PropertiesWrapper(Map<String, Object> properties) {
            this.properties = properties;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }
    }

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSerializeSimpleInteger() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("exampleProperty", 42);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"exampleProperty\""));
        Assert.assertTrue(json.contains("\"value\":42"));
        Assert.assertTrue(json.contains("\"type\":\"INTEGER\""));
    }

    @Test
    public void testSerializeSimpleString() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("stringProp", "hello");

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"stringProp\""));
        Assert.assertTrue(json.contains("\"value\":\"hello\""));
        Assert.assertTrue(json.contains("\"type\":\"STRING\""));
    }

    @Test
    public void testSerializeBoolean() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("boolProp", true);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"boolProp\""));
        Assert.assertTrue(json.contains("\"value\":true"));
        Assert.assertTrue(json.contains("\"type\":\"BOOLEAN\""));
    }

    @Test
    public void testSerializeDouble() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("doubleProp", 3.14);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"doubleProp\""));
        Assert.assertTrue(json.contains("\"value\":3.14"));
        Assert.assertTrue(json.contains("\"type\":\"DOUBLE\""));
    }

    @Test
    public void testSerializeArrayOfIntegers() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("arrayProp", new Integer[]{1, 2, 3, 4, 5});

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"arrayProp\""));
        Assert.assertTrue(json.contains("\"value\":[1,2,3,4,5]"));
        Assert.assertTrue(json.contains("\"type\":\"INTEGER\""));
    }

    @Test
    public void testSerializeArrayOfStrings() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("stringArrayProp", new String[]{"one", "two", "three"});

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"stringArrayProp\""));
        Assert.assertTrue(json.contains("\"value\":[\"one\",\"two\",\"three\"]"));
        Assert.assertTrue(json.contains("\"type\":\"STRING\""));
    }

    @Test
    public void testSerializeMultipleProperties() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("intProp", 100);
        properties.put("stringProp", "test");
        properties.put("boolProp", false);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"intProp\""));
        Assert.assertTrue(json.contains("\"value\":100"));
        Assert.assertTrue(json.contains("\"stringProp\""));
        Assert.assertTrue(json.contains("\"value\":\"test\""));
        Assert.assertTrue(json.contains("\"boolProp\""));
        Assert.assertTrue(json.contains("\"value\":false"));
    }

    @Test
    public void testSerializeLong() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("longProp", 9223372036854775807L);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"longProp\""));
        Assert.assertTrue(json.contains("\"value\":9223372036854775807"));
        Assert.assertTrue(json.contains("\"type\":\"LONG\""));
    }

    @Test
    public void testSerializeFloat() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("floatProp", 2.5f);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"floatProp\""));
        Assert.assertTrue(json.contains("\"value\":2.5"));
        Assert.assertTrue(json.contains("\"type\":\"FLOAT\""));
    }

    @Test
    public void testSerializePassword() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("passwordProp", new KuraPassword("secret123"));

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"passwordProp\""));
        Assert.assertTrue(json.contains("\"value\":\"c2VjcmV0MTIz\"")); //base64 encoded (because the XML adapter encodes the value...no worries when passed to ESF considering the XML adapter decodes it back)
        Assert.assertTrue(json.contains("\"type\":\"PASSWORD\""));
    }

    @Test
    public void testSerializeChar() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("charProp", 'A');

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"charProp\""));
        Assert.assertTrue(json.contains("\"value\":\"A\""));
        Assert.assertTrue(json.contains("\"type\":\"CHAR\""));
    }

    @Test
    public void testSerializeByte() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("byteProp", (byte) 127);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"byteProp\""));
        Assert.assertTrue(json.contains("\"value\":127"));
        Assert.assertTrue(json.contains("\"type\":\"BYTE\""));
    }

    @Test
    public void testSerializeShort() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("shortProp", (short) 32767);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"shortProp\""));
        Assert.assertTrue(json.contains("\"value\":32767"));
        Assert.assertTrue(json.contains("\"type\":\"SHORT\""));
    }

    @Test
    public void testSerializeNullValue() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("nullProp", null);

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"nullProp\""));
        Assert.assertTrue(json.contains("\"value\":null"));
        Assert.assertTrue(json.contains("\"type\":\"STRING\""));
    }

    @Test
    public void testSerializeEmptyMap() throws Exception {
        Map<String, Object> properties = new HashMap<>();

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"properties\":{}"));
    }

    @Test
    public void testSerializeEmptyArray() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("emptyArrayProp", new Integer[]{});

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"emptyArrayProp\""));
        Assert.assertTrue(json.contains("\"value\":null"));
        Assert.assertTrue(json.contains("\"type\":\"STRING\""));
    }

    @Test
    public void testSerializeSingleElementArray() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("singleElementArray", new Integer[]{42});

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"singleElementArray\""));
        Assert.assertTrue(json.contains("\"value\":[42]"));
        Assert.assertTrue(json.contains("\"type\":\"INTEGER\""));
    }

    @Test
    public void testSerializeArrayOfBooleans() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("boolArrayProp", new Boolean[]{true, false, true});

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"boolArrayProp\""));
        Assert.assertTrue(json.contains("\"value\":[true,false,true]"));
        Assert.assertTrue(json.contains("\"type\":\"BOOLEAN\""));
    }

    @Test
    public void testSerializeArrayOfDoubles() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("doubleArrayProp", new Double[]{1.1, 2.2, 3.3});

        PropertiesWrapper wrapper = new PropertiesWrapper(properties);
        String json = objectMapper.writeValueAsString(wrapper);

        Assert.assertTrue(json.contains("\"doubleArrayProp\""));
        Assert.assertTrue(json.contains("\"value\":[1.1,2.2,3.3]"));
        Assert.assertTrue(json.contains("\"type\":\"DOUBLE\""));
    }
}