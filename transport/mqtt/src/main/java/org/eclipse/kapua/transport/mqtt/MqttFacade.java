/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.transport.mqtt;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.kapua.transport.TransportFacade;
import org.eclipse.kapua.transport.exception.TransportClientGetException;
import org.eclipse.kapua.transport.exception.TransportException;
import org.eclipse.kapua.transport.exception.TransportSendException;
import org.eclipse.kapua.transport.exception.TransportTimeoutException;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientCallbackSetException;
import org.eclipse.kapua.transport.mqtt.exception.MqttClientSubscribeException;
import org.eclipse.kapua.transport.mqtt.pooling.MqttClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link TransportFacade} API for MQTT transport facade.
 *
 * @since 1.0.0
 */
public class MqttFacade implements TransportFacade<MqttTopic, MqttPayload, MqttMessage, MqttMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(MqttFacade.class);

    /**
     * The {@link MqttClient} used to make requests.
     *
     * @since 1.0.0
     */
    private MqttClient borrowedClient;

    /**
     * The host that this {@link MqttClient} connects to.
     */
    private final String nodeUri;

    /**
     * Initializes a {@link MqttFacade} to be used to send requests to devices.
     *
     * @throws TransportClientGetException
     *         When {@link MqttClient} is not available for the given node URI.
     * @since 1.0.0
     */
    public MqttFacade(@NotNull String nodeUri) throws TransportException {
        this.nodeUri = nodeUri;
        // Get the client form the pool
        try {
            MqttClientPool perBrokerMqttClientPool = MqttClientPool.getInstance(nodeUri);
            Long maxBorrowTimeout = perBrokerMqttClientPool.getMaxWaitMillis();

            long borrowTimerStart = System.nanoTime();
            borrowedClient = perBrokerMqttClientPool.borrowObject();
            long borrowTimerStop = System.nanoTime();

            if (LOG.isDebugEnabled()) {
                // Logging statistics on borrow time
                LOG.debug("MqttClient borrowed for BrokerNode {} in {}ns. Max time waited currently is {}ms with a {}ms timeout",
                        nodeUri, (borrowTimerStop - borrowTimerStart), perBrokerMqttClientPool.getMaxBorrowWaitTimeMillis(), maxBorrowTimeout);
            }
        } catch (Exception e) {
            throw new TransportClientGetException(e, nodeUri);
        }
    }
    // Message management

    @Override
    public void sendAsync(@NotNull MqttMessage mqttMessage) throws TransportTimeoutException, TransportSendException {
        sendSync(mqttMessage, null);
    }

    @Override
    public MqttMessage sendSync(@NotNull MqttMessage mqttMessage, @Nullable Long timeout) throws TransportTimeoutException, TransportSendException {
        List<MqttMessage> responses = new ArrayList<>();

        sendInternal(mqttMessage, responses, timeout);

        if (timeout != null) {
            if (responses.isEmpty()) {
                throw new TransportTimeoutException(timeout);
            }

            return responses.get(0);
        } else {
            return null;
        }
    }

    /**
     * Actual implementation of the send operations.
     * <p>
     * According to the parameters given, it will make a sync or async request.
     * </p>
     *
     * @param mqttMessage
     *         The request to send.
     * @param responses
     *         The container in which load responses received from the device
     * @param timeout
     *         The timeout of waiting the response from the device. If {@code null} request will be fired without waiting for the response. If mqttMessage has no response message set, timeout will be
     *         ignore even if set.
     * @throws TransportSendException
     *         if sending the request produces any error.
     * @see MqttMessage#getResponseTopic()
     * @since 1.0.0
     */
    private void sendInternal(@NotNull MqttMessage mqttMessage, @NotNull List<MqttMessage> responses, @Nullable Long timeout) throws TransportSendException {
        try {
            // Subscribe if necessary
            MqttResponseCallback mqttClientCallback = mqttMessage.expectResponse() ? subscribeToResponse(mqttMessage.getResponseTopic(), responses) : null;

            // Publish message
            publishMessage(mqttMessage);

            // Wait the response if expected
            if (timeout != null && mqttClientCallback != null) {
                waitResponse(timeout, mqttClientCallback);
            }
        } catch (Exception e) {
            throw new TransportSendException(e, mqttMessage);
        }
    }

    @Override
    public String getClientId() {
        return borrowedClient.getClientId();
    }

    @Override
    public Class<MqttMessage> getMessageClass() {
        return MqttMessage.class;
    }

    @Override
    public void clean() {
        close();
    }

    @Override
    public void close() {
        try {
            MqttClientPool.getInstance(nodeUri).returnObject(borrowedClient);
        } finally {
            borrowedClient = null;
        }
    }
    // Private methods

    /**
     * Publish the given {@link MqttMessage} using the {@link MqttClient}.
     *
     * @param mqttMessage
     *         The {@link MqttMessage} to publish.
     * @throws TransportSendException
     *         if error occurs while publishing the given {@link MqttMessage}
     * @since 1.1.0
     */
    private void publishMessage(@NotNull MqttMessage mqttMessage) throws TransportSendException {
        try {
            borrowedClient.publish(mqttMessage);
        } catch (Exception e) {
            throw new TransportSendException(e, mqttMessage);
        }
    }

    /**
     * Subscribe to response topic and adds the response messages to the given {@code responses} container.
     *
     * @param responseTopic
     *         The {@link MqttTopic} to subscribe to receive the response.
     * @param responses
     *         The container of the received responses
     * @return The {@link MqttResponseCallback} which handles the received responses.
     * @throws MqttClientCallbackSetException
     *         if {@link MqttClient#setCallback(MqttResponseCallback)} fails.
     * @throws MqttClientSubscribeException
     *         if the {@link MqttClient#subscribe(MqttTopic)} fails.
     * @since 1.1.0
     */
    private MqttResponseCallback subscribeToResponse(MqttTopic responseTopic, List<MqttMessage> responses) throws MqttClientCallbackSetException, MqttClientSubscribeException {
        MqttResponseCallback mqttClientCallback = new MqttResponseCallback(responseTopic, responses);
        borrowedClient.setCallback(mqttClientCallback);
        borrowedClient.subscribe(responseTopic);

        return mqttClientCallback;
    }

    /**
     * Waits for the response.
     *
     * @param timeout
     *         The timeout time of waiting the message response.
     * @param mqttClientCallback
     *         The {@link MqttResponseCallback} which handles the received responses.
     * @since 1.1.0
     */
    private void waitResponse(long timeout, MqttResponseCallback mqttClientCallback) {

        MqttResponseTimeoutTimer responseTimeoutTimer = new MqttResponseTimeoutTimer(borrowedClient.getClientId(), mqttClientCallback, timeout);

        try {
            synchronized (mqttClientCallback) {
                mqttClientCallback.wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            responseTimeoutTimer.cancel();
        }
    }
}
