/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.client.gateway.kura;

import java.nio.ByteBuffer;
import java.time.Instant;

import org.eclipse.kapua.client.gateway.Payload;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class KuraBinaryPayloadCodecTest {

    private static final int BYTE_BUFFER_SIZE = 1024;

    KuraBinaryPayloadCodec.Builder builder;
    Payload payload;

    @Before
    public void initialize() {
        builder = new KuraBinaryPayloadCodec.Builder();
        payload = Mockito.mock(Payload.class);
    }

    @Test
    public void builderBuildTest() {
        Assert.assertNotNull("Null not expected", builder);
        Assert.assertThat("Instance of KuraBinaryPayloadCodec expected.", builder.build(), IsInstanceOf.instanceOf(KuraBinaryPayloadCodec.class));
    }

    @Test(expected = NullPointerException.class)
    public void encodeNullPayloadTest() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BYTE_BUFFER_SIZE);
        builder.build().encode(null, byteBuffer);
    }

    @Test
    public void encodeNullBufferTest() throws Exception {
        Mockito.when(payload.getTimestamp()).thenReturn(Instant.ofEpochSecond(10L));

        Assert.assertNotNull("Null not expected.", builder.build().encode(payload, null));
        Assert.assertThat("Instance of ByteBuffer expected.", builder.build().encode(payload, null), IsInstanceOf.instanceOf(ByteBuffer.class));
    }

    @Test(expected = NullPointerException.class)
    public void encodeNullPayloadNullBufferTest() throws Exception {
        builder.build().encode(null, null);
    }

    @Test
    public void encodeMergedBufferTest() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BYTE_BUFFER_SIZE);
        Mockito.when(payload.getTimestamp()).thenReturn(Instant.ofEpochSecond(10L));

        Assert.assertNotNull("Null not expected.", builder.build().encode(payload, byteBuffer));
        Assert.assertThat("Instance of ByteBuffer expected.", builder.build().encode(payload, byteBuffer), IsInstanceOf.instanceOf(ByteBuffer.class));
    }

    @Test
    public void encodeTest() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        Mockito.when(payload.getTimestamp()).thenReturn(Instant.ofEpochSecond(10L));

        Assert.assertThat("Instance of ByteBuffer expected.", builder.build().encode(payload, byteBuffer), IsInstanceOf.instanceOf(ByteBuffer.class));
        Assert.assertEquals("Expected and actual values should be the same.", 100, builder.build().encode(payload, byteBuffer).limit());
    }

    @Test(expected = NullPointerException.class)
    public void decodeNullBufferTest() throws Exception {
        builder.build().decode(null);
    }

    @Test
    public void decodeTest() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(0);

        Assert.assertNotNull("Null not expected.", builder.build().decode(byteBuffer));
        Assert.assertThat("Instance of Payload expected.", builder.build().decode(byteBuffer), IsInstanceOf.instanceOf(Payload.class));
    }
}
