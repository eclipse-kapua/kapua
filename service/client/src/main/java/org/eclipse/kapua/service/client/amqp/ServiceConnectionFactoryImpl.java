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
package org.eclipse.kapua.service.client.amqp;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceConnectionFactoryImpl extends JmsConnectionFactory {

    protected static final Logger logger = LoggerFactory.getLogger(ServiceConnectionFactoryImpl.class);

    private final static AtomicInteger INDEX = new AtomicInteger();

    private String clientId;

    public ServiceConnectionFactoryImpl(String url, String username, String password, String clientId) {
        this("amqp", url, username, password, clientId);
    }

    public ServiceConnectionFactoryImpl(String schema, String url, String username, String password, String clientId) {
        super(username, password, schema + "://" + url);
        this.clientId = clientId;
        logger.info("Created connection factory with client id: {}", this.clientId);
    }

    @Override
    public Connection createConnection(String username, String password) throws JMSException {
        Connection connection = super.createConnection(username, password);
        String generatedClient = generateClientId();
        logger.info("Created connection for generated client id: {}", generatedClient);
        connection.setClientID(generatedClient);
        return connection;
    }

    protected String generateClientId() {
        return clientId + "-" + INDEX.incrementAndGet();
    }

}
