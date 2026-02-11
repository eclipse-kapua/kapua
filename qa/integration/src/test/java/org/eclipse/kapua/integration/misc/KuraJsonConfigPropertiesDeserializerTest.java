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
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraPassword;
import org.eclipse.kapua.service.device.call.kura.model.configuration.json.KuraJsonConfigPropertiesDeserializer;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import org.junit.Assert;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KuraJsonConfigPropertiesDeserializerTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new KuraJsonConfigPropertiesDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void testDeserializeSimpleInteger() throws Exception {
        String json = "{\n" +
                "    \"exampleProperty\": {\n" +
                "        \"value\": 42,\n" +
                "        \"type\": \"INTEGER\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.containsKey("exampleProperty"));
        Assert.assertEquals(42, result.get("exampleProperty"));
    }

    @Test
    public void testDeserializeSimpleString() throws Exception {
        String json = "{\n" +
                "    \"stringProp\": {\n" +
                "        \"value\": \"hello\",\n" +
                "        \"type\": \"STRING\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals("hello", result.get("stringProp"));
    }

    @Test
    public void testDeserializeBoolean() throws Exception {
        String json = "{\n" +
                "    \"boolProp\": {\n" +
                "        \"value\": true,\n" +
                "        \"type\": \"BOOLEAN\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals(true, result.get("boolProp"));
    }

    @Test
    public void testDeserializeDouble() throws Exception {
        String json = "{\n" +
                "    \"doubleProp\": {\n" +
                "        \"value\": 3.14,\n" +
                "        \"type\": \"DOUBLE\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals(3.14, result.get("doubleProp"));
    }

    @Test
    public void testDeserializeArrayOfIntegers() throws Exception {
        String json = "{\n" +
                "    \"arrayProp\": {\n" +
                "        \"value\": [1, 2, 3, 4, 5],\n" +
                "        \"type\": \"INTEGER\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Object value = result.get("arrayProp");
        Assert.assertNotNull(value);
        Assert.assertTrue(value.getClass().isArray());

        Integer[] array = (Integer[]) value;
        Assert.assertEquals(5, array.length);
        Assert.assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    public void testDeserializeArrayOfStrings() throws Exception {
        String json = "{\n" +
                "    \"stringArrayProp\": {\n" +
                "        \"value\": [\"one\", \"two\", \"three\"],\n" +
                "        \"type\": \"STRING\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        String[] array = (String[]) result.get("stringArrayProp");
        Assert.assertArrayEquals(new String[]{"one", "two", "three"}, array);
    }

    @Test
    public void testDeserializeMultipleProperties() throws Exception {
        String json = "{\n" +
                "    \"intProp\": {\n" +
                "        \"value\": 100,\n" +
                "        \"type\": \"INTEGER\"\n" +
                "    },\n" +
                "    \"stringProp\": {\n" +
                "        \"value\": \"test\",\n" +
                "        \"type\": \"STRING\"\n" +
                "    },\n" +
                "    \"boolProp\": {\n" +
                "        \"value\": false,\n" +
                "        \"type\": \"BOOLEAN\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals(3, result.size());
        Assert.assertEquals(100, result.get("intProp"));
        Assert.assertEquals("test", result.get("stringProp"));
        Assert.assertEquals(false, result.get("boolProp"));
    }

    @Test
    public void testDeserializeWithoutType() throws Exception {
        String json = "{\n" +
                "    \"noTypeProp\": {\n" +
                "        \"value\": \"defaultString\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals("defaultString", result.get("noTypeProp"));
    }

    @Test
    public void testDeserializeLong() throws Exception {
        String json = "{\n" +
                "    \"longProp\": {\n" +
                "        \"value\": 9223372036854775807,\n" +
                "        \"type\": \"LONG\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals(9223372036854775807L, result.get("longProp"));
    }

    @Test
    public void testDeserializeFloat() throws Exception {
        String json = "{\n" +
                "    \"floatProp\": {\n" +
                "        \"value\": 2.5,\n" +
                "        \"type\": \"FLOAT\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals(2.5f, result.get("floatProp"));
    }

    @Test
    public void testDeserializePassword() throws Exception {
        String json = "{\n" +
                "    \"passwordProp\": {\n" +
                "        \"value\": \"secret123\",\n" +
                "        \"type\": \"PASSWORD\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals(new KuraPassword("secret123").toString(), result.get("passwordProp").toString());
    }

    @Test
    public void testDeserializeChar() throws Exception {
        String json = "{\n" +
                "    \"charProp\": {\n" +
                "        \"value\": \"A\",\n" +
                "        \"type\": \"CHAR\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals('A', result.get("charProp"));
    }

    @Test
    public void testDeserializeByte() throws Exception {
        String json = "{\n" +
                "    \"byteProp\": {\n" +
                "        \"value\": 127,\n" +
                "        \"type\": \"BYTE\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals((byte) 127, result.get("byteProp"));
    }

    @Test
    public void testDeserializeShort() throws Exception {
        String json = "{\n" +
                "    \"shortProp\": {\n" +
                "        \"value\": 32767,\n" +
                "        \"type\": \"SHORT\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertEquals((short) 32767, result.get("shortProp"));
    }

    @Test
    public void testDeserializeNullValue() throws Exception {
        String json = "{\n" +
                "    \"nullProp\": {\n" +
                "        \"value\": null,\n" +
                "        \"type\": \"STRING\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertTrue(result.containsKey("nullProp"));
        Assert.assertNull(result.get("nullProp"));
    }

    @Test
    public void testDeserializeEmptyJson() throws Exception {
        String json = "{}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testDeserializeEmptyArray() throws Exception {
        String json = "{\n" +
                "    \"emptyArrayProp\": {\n" +
                "        \"value\": [],\n" +
                "        \"type\": \"INTEGER\"\n" +
                "    }\n" +
                "}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        Integer[] array = (Integer[]) result.get("emptyArrayProp");
        Assert.assertEquals(0, array.length);
    }
}
