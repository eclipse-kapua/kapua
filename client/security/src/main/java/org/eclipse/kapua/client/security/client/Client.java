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
package org.eclipse.kapua.client.security.client;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
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
 * Helper class to handle request/reply through AMQP messaging layer
 *
 */
public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    private static final long WAIT_BETWEEN_RECONNECTION_ATTEMPT = 2000;

    private ConnectionFactory connectionFactory;//is this reference needed?
    private Connection connection;//keep to implement cleanup (and object lifecycle)
    private Session session;
    private MessageConsumer consumer;
    private MessageProducer producer;
    private String clientId;
    private String requestAddress;
    private String replyAddress;
    private ClientMessageListener clientMessageListener;
    private ExceptionListener exceptionListener;

    private boolean active;
    private boolean connectionStatus;

    public Client(String username, String password, String host, int port, String clientId,
            String requestAddress, String replyAddress, ClientMessageListener clientMessageListener) throws JMSException {
        this.clientId = clientId;
        this.requestAddress = requestAddress;
        this.replyAddress = replyAddress;
        this.clientMessageListener = clientMessageListener;
        connectionFactory = new JmsConnectionFactory(username, password, "amqp://" + host + ":" + port);
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

    public void sendMessage(TextMessage message) throws JMSException {
        producer.send(message);
        message.acknowledge();
    }

    public TextMessage createTextMessage() throws JMSException {
        return session.createTextMessage();
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
                logger.info("AMQP client binding request sender to: {}", requestAddress);
                logger.info("AMQP client binding message listener to: {}", replyAddress);
                consumer = session.createConsumer(session.createQueue(replyAddress));
                consumer.setMessageListener(clientMessageListener);
                producer = session.createProducer(session.createQueue(requestAddress));
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