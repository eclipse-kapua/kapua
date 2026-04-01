/*******************************************************************************
 * Copyright (c) 2017, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.client.gateway.mqtt.fuse;

import org.eclipse.kapua.client.gateway.BinaryPayloadCodec;
import org.eclipse.kapua.client.gateway.Credentials;
import org.eclipse.kapua.client.gateway.mqtt.MqttMessageHandler;
import org.eclipse.kapua.client.gateway.mqtt.MqttNamespace;
import org.eclipse.kapua.client.gateway.spi.Channel;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

@Category(JUnitTests.class)
public class FuseChannelTest {

    private static final int BYTE_BUFFER_SIZE = 1024;

    private FuseChannel.Builder builder;

    @Before
    public void init() throws URISyntaxException {
        builder = new FuseChannel.Builder();
        builder.broker(new URI("string"));
        builder.clientId("clientId");
        builder.namespace(Mockito.mock(MqttNamespace.class));
        builder.codec(Mockito.mock(BinaryPayloadCodec.class));
        builder.credentials(Mockito.mock(Credentials.UserAndPassword.class));
    }

    @Test
    public void builderBuilderTest() {
        FuseChannel.Builder builderExpected = new FuseChannel.Builder();
        Assert.assertEquals("Actual and expected values should be the same.", builderExpected, builderExpected.builder());
    }

    @Test
    public void builderBuildTest() throws Exception {
        builder.credentials(Credentials.userAndPassword("kapua-sys", new char[]{'a', 'b', 'c'}));
        Assert.assertThat("Instance of FuseChannel expected.", builder.build(), IsInstanceOf.instanceOf(FuseChannel.class));
    }

    @Test
    public void builderBuildCredentialsNullTest() throws Exception {
        builder.credentials(null);
        Assert.assertThat("Instance of FuseChannel expected.", builder.build(), IsInstanceOf.instanceOf(FuseChannel.class));
    }

    @Test
    public void handleConnectedTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        Channel.Context context = Mockito.mock(Channel.Context.class);
        fuseChannel.handleInit(context);
        fuseChannel.handleConnected();
        Mockito.verify(context).notifyConnected();
    }

    @Test
    public void handleDisconnectedTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        Channel.Context context = Mockito.mock(Channel.Context.class);
        fuseChannel.handleInit(context);
        fuseChannel.handleClose(context);
        fuseChannel.handleDisconnected();
        Mockito.verify(context).notifyDisconnected();
    }

    @Test
    public void handleInitTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        Channel.Context context = Mockito.mock(Channel.Context.class);
        fuseChannel.handleInit(context);
        final CallbackConnection connection = Mockito.mock(CallbackConnection.class);
        Mockito.verify(connection, Mockito.times(0)).connect(null);
    }

    @Test
    public void handleInitContextNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        fuseChannel.handleInit(null);
        final CallbackConnection connection = Mockito.mock(CallbackConnection.class);
        Mockito.verify(connection, Mockito.times(0)).connect(null);
    }

    @Test
    public void handleCloseTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        Channel.Context context = Mockito.mock(Channel.Context.class);
        fuseChannel.handleClose(context);
        final CallbackConnection connection = Mockito.mock(CallbackConnection.class);
        Mockito.verify(connection, Mockito.times(0)).disconnect(null);
    }

    @Test
    public void handleCloseContextNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        fuseChannel.handleClose(null);
        final CallbackConnection connection = Mockito.mock(CallbackConnection.class);
        Mockito.verify(connection, Mockito.times(0)).disconnect(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void publishMqttTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final String topic = "string";
        fuseChannel.handleInit(Mockito.mock(Channel.Context.class));
        final ByteBuffer payload = ByteBuffer.allocateDirect(BYTE_BUFFER_SIZE);
        fuseChannel.publishMqtt(topic, payload);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void publishMqttTopicNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        fuseChannel.handleInit(Mockito.mock(Channel.Context.class));
        final ByteBuffer payload = ByteBuffer.allocateDirect(BYTE_BUFFER_SIZE);
        fuseChannel.publishMqtt(null, payload);
    }

    @Test(expected = NullPointerException.class)
    public void publishMqttPayloadNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final String topic = "string";
        fuseChannel.handleInit(Mockito.mock(Channel.Context.class));
        fuseChannel.publishMqtt(topic, null);
    }

    @Test(expected = AssertionError.class)
    public void subscribeMqttTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final String topic = "string";
        final MqttMessageHandler messageHandler = Mockito.mock(MqttMessageHandler.class);
        fuseChannel.subscribeMqtt(topic, messageHandler);
    }

    @Test(expected = NullPointerException.class)
    public void subscribeMqttTopicNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final MqttMessageHandler messageHandler = Mockito.mock(MqttMessageHandler.class);
        fuseChannel.subscribeMqtt(null, messageHandler);
    }

    @Test(expected = AssertionError.class)
    public void subscribeMqttMessageHandlerNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final String topic = "string";
        fuseChannel.subscribeMqtt(topic, null);
    }

    @Test(expected = AssertionError.class)
    public void unsubscribeMqttTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        Set<String> topics = new HashSet<>();
        topics.add("India");
        fuseChannel.unsubscribeMqtt(topics);
    }

    @Test(expected = NullPointerException.class)
    public void unsubscribeMqttTopicsNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        fuseChannel.unsubscribeMqtt(null);
    }

    @Test
    public void handleMessageArrivedTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final String topic = "string";
        final Buffer payload = Mockito.mock(Buffer.class);
        final Callback<Callback<Void>> ack = Mockito.mock(Callback.class);
        fuseChannel.handleMessageArrived(topic, payload, ack);
    }

    @Test
    public void handleMessageArrivedTopicNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final Buffer payload = Mockito.mock(Buffer.class);
        final Callback<Callback<Void>> ack = Mockito.mock(Callback.class);
        fuseChannel.handleMessageArrived(null, payload, ack);
    }

    @Test
    public void handleMessageArrivedPayloadNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final String topic = "string";
        final Callback<Callback<Void>> ack = Mockito.mock(Callback.class);
        fuseChannel.handleMessageArrived(topic, null, ack);
    }

    @Test
    public void handleMessageArrivedAckNullTest() throws Exception {
        FuseChannel fuseChannel = builder.build();
        final String topic = "string";
        final Buffer payload = Mockito.mock(Buffer.class);
        final Callback<Callback<Void>> ack = Mockito.mock(Callback.class);
        fuseChannel.handleMessageArrived(topic, payload, null);
    }
}