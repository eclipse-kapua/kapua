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

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.commons.lang3.StringUtils;
import org.apache.qpid.jms.JmsConnectionFactory;

public class ServiceConnectionFactoryImpl extends JmsConnectionFactory {

    //JMS specification 2.0 requires client id null to be able to share subscription between multiple consumers
    //So remove any option to allow to set the client id since we use only shared durable subscriptions
    public ServiceConnectionFactoryImpl(String url, String username, String password) {
        this(url, username, password, null);
    }

    public ServiceConnectionFactoryImpl(String url, String username, String password, String clientId) {
        super(username, password, url);
        if (!StringUtils.isEmpty(clientId)) {
            setClientID(clientId);
        }
    }

    @Override
    public Connection createConnection(String username, String password) throws JMSException {
        return super.createConnection(username, password);
    }

}
