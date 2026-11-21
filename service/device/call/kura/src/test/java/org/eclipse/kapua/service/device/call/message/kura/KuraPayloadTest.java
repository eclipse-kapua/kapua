/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.call.message.kura;

import com.google.protobuf.InvalidProtocolBufferException;
import org.eclipse.kapua.message.internal.MessageException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.device.call.message.kura.proto.KuraPayloadProto;
import org.eclipse.kapua.service.device.call.message.kura.utils.GZIPUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

@Category(JUnitTests.class)
public class KuraPayloadTest {

    private static final byte[] BODY = "the body".getBytes(StandardCharsets.UTF_8);
    private static final byte[] BYTES_METRIC = new byte[]{1, 2, 3, 4};

    @Test
    public void readFromByteArrayTest() throws Exception {
        byte[] encoded = newKuraPayload().toByteArray();

        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.readFromByteArray(encoded);

        assertKuraPayload(kuraPayload);
    }

    @Test
    public void readFromByteArrayCompressedTest() throws Exception {
        byte[] encoded = gzip(newKuraPayload().toByteArray());
        Assert.assertTrue(GZIPUtils.isCompressed(encoded));

        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.readFromByteArray(encoded);

        assertKuraPayload(kuraPayload);
    }

    @Test
    public void readFromByteArrayCompressedBigBodyTest() throws Exception {
        // Much bigger than the GZIP and protobuf stream buffers
        byte[] bigBody = new byte[8 * 1024 * 1024];
        new Random(42).nextBytes(bigBody);

        KuraPayload bigKuraPayload = newKuraPayload();
        bigKuraPayload.setBody(bigBody.clone());
        byte[] encoded = gzip(bigKuraPayload.toByteArray());

        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.readFromByteArray(encoded);

        Assert.assertEquals(7, kuraPayload.getMetrics().size());
        Assert.assertArrayEquals(bigBody, kuraPayload.getBody());
    }

    @Test
    public void readFromByteArrayEmptyPayloadTest() throws Exception {
        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.readFromByteArray(new KuraPayload().toByteArray());

        Assert.assertNull(kuraPayload.getTimestamp());
        Assert.assertNull(kuraPayload.getPosition());
        Assert.assertTrue(kuraPayload.getMetrics().isEmpty());
        Assert.assertNull(kuraPayload.getBody());
    }

    @Test
    public void readFromByteArrayDoesNotReferenceInputTest() throws Exception {
        byte[] encoded = newKuraPayload().toByteArray();

        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.readFromByteArray(encoded);

        // Body and bytes metrics must have been copied out of the input array
        Arrays.fill(encoded, (byte) 0);

        assertKuraPayload(kuraPayload);
    }

    @Test(expected = MessageException.class)
    public void readFromByteArrayInvalidEndTagTest() throws Exception {
        // Field 1 with wire type END_GROUP: rejected by KuraPayloadProto.KuraPayload.parseFrom(byte[])
        new KuraPayload().readFromByteArray(new byte[]{0x0C});
    }

    @Test(expected = MessageException.class)
    public void readFromByteArrayInvalidCompressedTest() throws Exception {
        // GZIP magic number followed by garbage
        new KuraPayload().readFromByteArray(new byte[]{(byte) 0x1f, (byte) 0x8b, 1, 2, 3, 4, 5});
    }

    /**
     * Non-protobuf payloads (i.e. raw payloads published by non-Kura devices) must be accepted or rejected exactly as
     * {@link KuraPayloadProto.KuraPayload#parseFrom(byte[])} does, since rejection triggers the raw body fallback in translators.
     */
    @Test
    public void readFromByteArrayRawPayloadsTest() throws Exception {
        assertSameOutcomeAsParseFrom("{\"temperature\": 22.5, \"humidity\": 40}".getBytes(StandardCharsets.UTF_8));
        assertSameOutcomeAsParseFrom("22.5;40;ok".getBytes(StandardCharsets.UTF_8));
        assertSameOutcomeAsParseFrom("hello".getBytes(StandardCharsets.UTF_8));
        assertSameOutcomeAsParseFrom(gzip("{\"temperature\": 22.5}".getBytes(StandardCharsets.UTF_8)));

        Random random = new Random(42);
        for (int i = 0; i < 2000; i++) {
            byte[] randomBytes = new byte[1 + random.nextInt(64)];
            random.nextBytes(randomBytes);
            assertSameOutcomeAsParseFrom(randomBytes);
        }
    }

    //
    // Private methods
    //

    private static KuraPayload newKuraPayload() {
        KuraPosition kuraPosition = new KuraPosition();
        kuraPosition.setLatitude(45.5);
        kuraPosition.setLongitude(13.5);

        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.setTimestamp(new Date(1_700_000_000_000L));
        kuraPayload.setPosition(kuraPosition);
        kuraPayload.addMetric("double", 1.5d);
        kuraPayload.addMetric("float", 2.5f);
        kuraPayload.addMetric("long", 3L);
        kuraPayload.addMetric("int", 4);
        kuraPayload.addMetric("boolean", true);
        kuraPayload.addMetric("string", "five");
        kuraPayload.addMetric("bytes", BYTES_METRIC.clone());
        kuraPayload.setBody(BODY.clone());
        return kuraPayload;
    }

    private static void assertKuraPayload(KuraPayload kuraPayload) {
        Assert.assertEquals(new Date(1_700_000_000_000L), kuraPayload.getTimestamp());
        Assert.assertNotNull(kuraPayload.getPosition());
        Assert.assertEquals(45.5, kuraPayload.getPosition().getLatitude(), 0d);
        Assert.assertEquals(13.5, kuraPayload.getPosition().getLongitude(), 0d);

        Assert.assertEquals(7, kuraPayload.getMetrics().size());
        Assert.assertEquals(1.5d, kuraPayload.getMetrics().get("double"));
        Assert.assertEquals(2.5f, kuraPayload.getMetrics().get("float"));
        Assert.assertEquals(3L, kuraPayload.getMetrics().get("long"));
        Assert.assertEquals(4, kuraPayload.getMetrics().get("int"));
        Assert.assertEquals(true, kuraPayload.getMetrics().get("boolean"));
        Assert.assertEquals("five", kuraPayload.getMetrics().get("string"));
        Assert.assertArrayEquals(BYTES_METRIC, (byte[]) kuraPayload.getMetrics().get("bytes"));

        Assert.assertArrayEquals(BODY, kuraPayload.getBody());
    }

    private static void assertSameOutcomeAsParseFrom(byte[] bytes) throws Exception {
        byte[] reference = GZIPUtils.isCompressed(bytes) ? safeDecompress(bytes) : bytes;

        KuraPayloadProto.KuraPayload expected = null;
        if (reference != null) {
            try {
                expected = KuraPayloadProto.KuraPayload.parseFrom(reference);
            } catch (InvalidProtocolBufferException e) {
                // expected stays null
            }
        }

        KuraPayload actual = new KuraPayload();
        try {
            actual.readFromByteArray(bytes.clone());
        } catch (MessageException e) {
            actual = null;
        }

        if (expected == null) {
            Assert.assertNull("Payload should have been rejected: " + Arrays.toString(bytes), actual);
        } else {
            Assert.assertNotNull("Payload should have been accepted: " + Arrays.toString(bytes), actual);
            Assert.assertEquals(expected.getMetricCount(), actual.getMetrics().size());
            if (expected.hasBody()) {
                Assert.assertArrayEquals(expected.getBody().toByteArray(), actual.getBody());
            } else {
                Assert.assertNull(actual.getBody());
            }
        }
    }

    private static byte[] gzip(byte[] bytes) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try (GZIPOutputStream out = new GZIPOutputStream(result)) {
            out.write(bytes);
        }
        return result.toByteArray();
    }

    private static byte[] safeDecompress(byte[] bytes) {
        try {
            return GZIPUtils.decompress(bytes);
        } catch (Exception e) {
            return null;
        }
    }
}
