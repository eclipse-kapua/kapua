/*******************************************************************************
 * Copyright (c) 2020, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.client.gateway.kura.internal;

import com.google.protobuf.ByteString;
import org.eclipse.kapua.client.gateway.kura.proto.KuraPayloadProto;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Category(JUnitTests.class)
public class MetricsTest {

    private static final int BYTE_BUFFER_SIZE = 1024;

    KuraPayloadProto.KuraPayload.Builder builder;

    @Before
    public void initialize() {
        builder = KuraPayloadProto.KuraPayload.newBuilder();
    }

    @Test(expected = NullPointerException.class)
    public void buildMetricsNullBuilderTest() {
        Map<String, String> metrics = new HashMap<>();
        metrics.put("key", "value");
        Metrics.buildMetrics(null, metrics);
    }

    @Test(expected = NullPointerException.class)
    public void buildMetricsNullMetricsTest() {
        Metrics.buildMetrics(builder, null);
    }

    @Test
    public void buildMetricsTest() {
        Map<String, String> metrics = new HashMap<>();
        metrics.put("key", "value");
        List expectedList = new LinkedList();
        KuraPayloadProto.KuraPayload.KuraMetric.Builder expectedBuilder = KuraPayloadProto.KuraPayload.KuraMetric.newBuilder();
        expectedBuilder.setName("key");
        expectedBuilder.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.STRING);
        expectedBuilder.setStringValue("value");

        Assert.assertTrue("True expected.", builder.getMetricList().isEmpty());
        Assert.assertEquals("Expected and actual values should be the same.", expectedList, builder.getMetricList());

        Metrics.buildMetrics(builder, metrics);
        expectedList.add(expectedBuilder);

        Assert.assertFalse("False expected.", builder.getMetricList().isEmpty());
        Assert.assertEquals("Expected and actual values should be the same.", expectedList.toString(), builder.getMetricList().toString());
    }

    @Test
    public void buildBodyNullBodyTest() {
        ByteString expectedByteString = builder.getBody();

        Metrics.buildBody(builder, null);

        Assert.assertEquals("Expected and actual values should be the same.", expectedByteString.toString(), builder.getBody().toString());
    }

    @Test(expected = NullPointerException.class)
    public void buildBodyNullBuilderTest() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BYTE_BUFFER_SIZE);
        Metrics.buildBody(null, byteBuffer);
    }

    @Test
    public void buildBodyTest() {
        ByteBuffer body = ByteBuffer.allocateDirect(BYTE_BUFFER_SIZE);
        ByteString expectedByteString = builder.getBody();

        Metrics.buildBody(builder, body);

        Assert.assertNotEquals("Expected and actual values should not be the same.", expectedByteString.toString(), builder.getBody().toString());
    }

    @Test(expected = NullPointerException.class)
    public void addMetricNullBuilderTest() {
        String key = "Key";
        int value = 10;
        Metrics.addMetric(null, key, value);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void addMetricNullKeyTest() {
        byte[] byteArray = {1, 2, 3};
        Object[] values = {true, 10, "String", 10L, 10.10d, 10f, byteArray};

        for (Object value : values) {
            Metrics.addMetric(builder, null, value);
        }
    }

    @Test
    public void addMetricNullValueTest() {
        String key = "Key";

        Metrics.addMetric(builder, key, null);
        Assert.assertTrue("True expected.", builder.getMetricList().isEmpty());
    }

    @Test
    public void addMetricTest() {
        String key = "Key";
        byte[] byteArray = {1, 2, 3};
        Object[] values = {true, 10, "String", 10L, 10.10d, 10f, byteArray};

        Assert.assertTrue("True expected.", builder.getMetricList().isEmpty());
        for (Object value : values) {
            Metrics.addMetric(builder, key, value);
            Assert.assertFalse("False expected.", builder.getMetricList().isEmpty());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMetricIllegalArgumentTest() {
        String key = "Key";
        Object value = new Object();
        Metrics.addMetric(builder, key, value);
    }

    @Test
    public void extractMetricsNullKuraPayloadTest() {
        Assert.assertNull("Null expected.", Metrics.extractMetrics((KuraPayloadProto.KuraPayload) null));
    }

    @Test
    public void extractMetricsKuraPayloadTest() {
        KuraPayloadProto.KuraPayload.KuraMetric kuraMetric = KuraPayloadProto.KuraPayload.KuraMetric.newBuilder().setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.BOOL).setName("name").build();
        KuraPayloadProto.KuraPayload payload = KuraPayloadProto.KuraPayload.newBuilder().addMetric(kuraMetric).build();
        Map<String, Object> expectedResult = new TreeMap<>();
        expectedResult.put("name", kuraMetric.getBoolValue());

        Assert.assertEquals("Expected and actual values should be the same.", expectedResult, Metrics.extractMetrics(payload));
        Assert.assertThat("Instance of Map expected.", Metrics.extractMetrics(payload), IsInstanceOf.instanceOf(Map.class));
    }

    @Test
    public void extractMetricsNullKuraMetricListTest() {
        Assert.assertNull("Null expected.", Metrics.extractMetrics((List<KuraPayloadProto.KuraPayload.KuraMetric>) null));
    }

    @Test
    public void extractMetricsKuraMetricListTest() {
        List<KuraPayloadProto.KuraPayload.KuraMetric> kuraMetricList = new LinkedList<>();
        KuraPayloadProto.KuraPayload.KuraMetric.ValueType[] valueType = {KuraPayloadProto.KuraPayload.KuraMetric.ValueType.BOOL, KuraPayloadProto.KuraPayload.KuraMetric.ValueType.BYTES,
                KuraPayloadProto.KuraPayload.KuraMetric.ValueType.DOUBLE, KuraPayloadProto.KuraPayload.KuraMetric.ValueType.FLOAT, KuraPayloadProto.KuraPayload.KuraMetric.ValueType.INT32,
                KuraPayloadProto.KuraPayload.KuraMetric.ValueType.INT64, KuraPayloadProto.KuraPayload.KuraMetric.ValueType.STRING};
        String name = "Name";
        Map<String, Object> expectedResult = new TreeMap<>();
        Map<String, Object> expectedResultWholeList = new TreeMap<>();

        //every element separately
        for (int i = 0; i < valueType.length; i++) {
            KuraPayloadProto.KuraPayload.KuraMetric kuraMetric = KuraPayloadProto.KuraPayload.KuraMetric.newBuilder().setType(valueType[i]).setName(name).build();
            Object[] value = {kuraMetric.getBoolValue(), kuraMetric.getBytesValue().toByteArray(), kuraMetric.getDoubleValue(), kuraMetric.getFloatValue(),
                    kuraMetric.getIntValue(), kuraMetric.getLongValue(), kuraMetric.getStringValue()};
            kuraMetricList.add(kuraMetric);
            expectedResult.put(name, value[i]);

            Assert.assertEquals("Expected and actual values should be the same.", expectedResult, Metrics.extractMetrics(kuraMetricList));
        Assert.assertThat("Instance of Map expected.", Metrics.extractMetrics(kuraMetricList), IsInstanceOf.instanceOf(Map.class));
            expectedResultWholeList.put(name, kuraMetric.getStringValue());
        }

        //whole list
        Assert.assertEquals("Expected and actual values should be the same.", expectedResultWholeList, Metrics.extractMetrics(kuraMetricList));
        Assert.assertThat("Instance of Map expected.", Metrics.extractMetrics(kuraMetricList), IsInstanceOf.instanceOf(Map.class));
    }

    @Test
    public void getAsStringMetricsKeyTest() {
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> metricsWithoutKey = new HashMap<>();
        Object[] value = {null, "value", 1};
        String key = "Key";
        String differentKey = "Second key";
        String[] expectedValue = {null, "value", null};

        Assert.assertNull("Null expected.", Metrics.getAsString(metricsWithoutKey, key));

        for (int i = 0; i < value.length; i++) {
            metrics.put("Key", value[i]);
            Assert.assertEquals("Expected and actual values should be the same.", expectedValue[i], Metrics.getAsString(metrics, key));
            Assert.assertNull("Null expected.", Metrics.getAsString(metrics, differentKey));
        }
    }

    @Test(expected = NullPointerException.class)
    public void getAsStringNullMetricsKeyTest() {
        String key = "Key";
        Assert.assertNull("Null expected.", Metrics.getAsString(null, key));
    }

    @Test
    public void getAsStringMetricsNullKeyTest() {
        Map<String, Object> metrics = new HashMap<>();
        String key = "Key";

        Assert.assertNull("Null expected.", Metrics.getAsString(metrics, null));

        metrics.put(null, "value");
        Assert.assertEquals("Expected and actual values should be the same.", "value", Metrics.getAsString(metrics, null));
        metrics.put(key, null);
        Assert.assertEquals("Expected and actual values should be the same.", "value", Metrics.getAsString(metrics, null));
    }

    @Test
    public void getAsStringMetricsKeyDefaultValueTest() {
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> metricsWithoutKey = new HashMap<>();
        Object[] value = {null, "value", 1};
        String key = "Key";
        String differentKey = "Second key";
        String defaultValue = "Default Value";
        String[] expectedValue = {defaultValue, "value", defaultValue};

        Assert.assertEquals("Expected and actual values should be the same.", defaultValue, Metrics.getAsString(metricsWithoutKey, key, defaultValue));

        for (int i = 0; i < value.length; i++) {
            metrics.put("Key", value[i]);
            Assert.assertEquals("Expected and actual values should be the same.", expectedValue[i], Metrics.getAsString(metrics, key, defaultValue));
            Assert.assertEquals("Expected and actual values should be the same.", defaultValue, Metrics.getAsString(metrics, differentKey, defaultValue));
        }
    }

    @Test(expected = NullPointerException.class)
    public void getAsStringNullMetricsKeyDefaultValueTest() {
        String key = "Key";
        String defaultValue = "Default Value";
        Assert.assertEquals("Expected and actual values should be the same.", defaultValue, Metrics.getAsString(null, key, defaultValue));
    }

    @Test
    public void getAsStringMetricsNullKeyDefaultValueTest() {
        Map<String, Object> metrics = new HashMap<>();
        Object[] value = {null, "value", 1};
        String defaultValue = "Default Value";
        for (int i = 0; i < value.length; i++) {
            metrics.put("Key", value[i]);
            Assert.assertEquals("Expected and actual values should be the same.", defaultValue, Metrics.getAsString(metrics, null, defaultValue));
        }
    }

    @Test
    public void getAsStringMetricsKeyNullDefaultValueTest() {
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> metricsWithoutKey = new HashMap<>();
        Object[] value = {null, "value", 1};
        String key = "Key";
        String differentKey = "Second key";
        String[] expectedValue = {null, "value", null};

        Assert.assertNull("Null expected.", Metrics.getAsString(metricsWithoutKey, key, null));

        for (int i = 0; i < value.length; i++) {
            metrics.put("Key", value[i]);
            Assert.assertEquals("Expected and actual values should be the same.", expectedValue[i], Metrics.getAsString(metrics, key, null));
            Assert.assertNull("Null expected.", Metrics.getAsString(metrics, differentKey, null));
        }
    }
}
