/*******************************************************************************
 * Copyright (c) 2026 Réda Housni Alaoui and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Réda Housni Alaoui - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.transport.mqtt.test.mqtt;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.kapua.transport.mqtt.MqttResponseCallback;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Category(JUnitTests.class)
public class MqttResponseCallbackTest {

    @Test
    public void waitCompletionReturnsWhenResponseArrivedBeforeWaitingTest() throws InterruptedException {
        List<MqttMessage> responses = new ArrayList<>();
        MqttResponseCallback mqttResponseCallback = new MqttResponseCallback(new MqttTopic("response/topic"), responses);
        mqttResponseCallback.messageArrived("response/topic", new org.eclipse.paho.client.mqttv3.MqttMessage("response".getBytes()));

        long start = System.nanoTime();
        mqttResponseCallback.waitCompletion(TimeUnit.MINUTES.toMillis(1));

        Assert.assertTrue("Waiting lasted until the timeout although the response had already arrived", System.nanoTime() - start < TimeUnit.SECONDS.toNanos(30));
        Assert.assertEquals(1, responses.size());
    }

    @Test
    public void waitCompletionReturnsWhenResponseArrivesWhileWaitingTest() throws InterruptedException {
        List<MqttMessage> responses = new ArrayList<>();
        MqttResponseCallback mqttResponseCallback = new MqttResponseCallback(new MqttTopic("response/topic"), responses);
        Thread responder = new Thread(() -> mqttResponseCallback.messageArrived("response/topic", new org.eclipse.paho.client.mqttv3.MqttMessage("response".getBytes())));

        long start = System.nanoTime();
        responder.start();
        mqttResponseCallback.waitCompletion(TimeUnit.MINUTES.toMillis(1));
        responder.join();

        Assert.assertTrue("Waiting lasted until the timeout although the response arrived", System.nanoTime() - start < TimeUnit.SECONDS.toNanos(30));
        Assert.assertEquals(1, responses.size());
    }

    @Test
    public void waitCompletionReturnsAfterTimeoutWithoutResponseTest() throws InterruptedException {
        List<MqttMessage> responses = new ArrayList<>();
        MqttResponseCallback mqttResponseCallback = new MqttResponseCallback(new MqttTopic("response/topic"), responses);

        long start = System.nanoTime();
        mqttResponseCallback.waitCompletion(200);

        Assert.assertTrue("Waiting ended before the timeout", System.nanoTime() - start >= TimeUnit.MILLISECONDS.toNanos(200));
        Assert.assertTrue(responses.isEmpty());
    }
}
