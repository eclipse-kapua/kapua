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
package org.eclipse.kapua.client.security.amqpclient;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AMQP client implementation
 *
 */
public class ClientAMQP implements Client {

    private static Logger logger = LoggerFactory.getLogger(ClientAMQP.class);

    private static final long WAIT_BETWEEN_RECONNECTION_ATTEMPT = 2000;

    //TODO find a standard enum or whatever instead of defining it here
    public enum DestinationType {
        queue,
        topic
    }

    private ConnectionFactory connectionFactory;//is this reference needed?
    private Connection connection;//keep to implement cleanup (and object lifecycle)
    private Session session;
    private MessageConsumer consumer;
    private MessageProducer producer;
    private String clientId;
    private String requestAddress;
    private String responseAddress;
    private DestinationType destinationType;
    private ClientMessageListener clientMessageListener;
    private ExceptionListener exceptionListener;

    private boolean active;
    private boolean connectionStatus;

    public ClientAMQP(String username, String password, String url, String clientId,
            String requestAddress, String responseAddress, DestinationType destinationType, ClientMessageListener clientMessageListener) throws JMSException {
        this.clientId = clientId;
        this.requestAddress = requestAddress;
        this.responseAddress = responseAddress;
        this.destinationType = destinationType;
        this.clientMessageListener = clientMessageListener;
        connectionFactory = new JmsConnectionFactory(username, password, url);
        exceptionListener = new ExceptionListener() {

            @Override
            public void onException(JMSException exception) {
                connectionStatus = false;
                connect();
            }
        };
        active = true;
        connect();
    }

    @Override
    public void sendMessage(Message message) throws Exception {
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText(message.getBody());
        message.getProperties().forEach((key, value) -> this.addProperty(textMessage, key, value));
        producer.send(textMessage);
        textMessage.acknowledge();
    }

    private void addProperty(TextMessage textMessage, String key, Object value) {
        try {
            if (value instanceof String) {
                textMessage.setStringProperty(key, (String)value);
            }
            else if (value instanceof Integer) {
                textMessage.setIntProperty(key, (int)value);
            }
            else if (value instanceof Long) {
                textMessage.setLongProperty(key, (long)value);
            }
            else if (value instanceof Double) {
                textMessage.setDoubleProperty(key, (double)value);
            }
            else if (value instanceof Float) {
                textMessage.setFloatProperty(key, (float)value);
            }
            else if (value instanceof Boolean) {
                textMessage.setBooleanProperty(key, (boolean)value);
            }
            else {
                textMessage.setObjectProperty(key, value);
            }
        }
        catch (JMSException e) {
            //do nothing
            logger.warn("Cannot set the message header property for key: '{}' - value: '{}' - Error: {}", key, value, e.getMessage());
        }
    }

    public void stop() throws JMSException {
        active = false;
        disconnect();
    }

    private void disconnect() throws JMSException {
        connectionStatus = false;
        if (connection != null) {
            connection.close();
        }
    }

    private void connect() {
        int connectAttempt = 0;
        while (active && !isConnected()) {
            logger.info("Connect attempt {}...", connectAttempt);
            try {
                logger.info("Service client {} - restarting attempt... {}", this, connectAttempt);
                disconnect();
                connection = connectionFactory.createConnection();
                connection.setExceptionListener(exceptionListener);
                connection.setClientID(clientId);
                connection.start();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                logger.info("AMQP client binding message listener to: {}", responseAddress);
                consumer = session.createConsumer(createDestination(responseAddress));
                consumer.setMessageListener(clientMessageListener);
                logger.info("AMQP client binding request sender to: {}", requestAddress);
                producer = session.createProducer(createDestination(requestAddress));
                clientMessageListener.init(session, producer);
                connectionStatus = true;
                logger.info("Service client {} - restarting attempt... {} DONE (Connection restored)", this, connectAttempt);
            } catch (JMSException e) {
                logger.info("Connect attempt {}... FAIL", connectAttempt, e);
                //wait a bit
                waitBeforeRetry();
            }
            connectAttempt++;
        }
    }

    private Destination createDestination(String address) throws JMSException {
        if (DestinationType.queue.equals(destinationType)) {
            return session.createQueue(address);
        }
        else {
            return session.createTopic(address);
        }
    }

    private boolean isConnected() {
        try {
            return connection!=null && connection.getClientID()!=null && connectionStatus;
        } catch (JMSException e) {
            logger.warn("Error while validating connection: {}", e.getMessage(), e);
            return false;
        }
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(WAIT_BETWEEN_RECONNECTION_ATTEMPT);
        } catch (InterruptedException e) {
            logger.error("Wait for connect interrupted!", e);
        }
    }

}