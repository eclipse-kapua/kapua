/*******************************************************************************
 * Copyright (c) 2022, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.broker.artemis.plugin.security;

import org.apache.activemq.artemis.core.protocol.mqtt.MQTTConnection;
import org.apache.activemq.artemis.protocol.amqp.broker.ActiveMQProtonRemotingConnection;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.apache.activemq.artemis.spi.core.remoting.Connection;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSetting;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSettingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class PluginUtility {

    protected static Logger logger = LoggerFactory.getLogger(SecurityPlugin.class);

    private final String amqpInternalConnectorPort;
    private final String amqpInternalConnectorName;
    private final String mqttInternalConnectorPort;
    private final String mqttInternalConnectorName;

    @Inject
    public PluginUtility(BrokerSetting brokerSetting) {
        amqpInternalConnectorPort = ":" + brokerSetting.getString(BrokerSettingKey.INTERNAL_AMQP_ACCEPTOR_PORT);
        amqpInternalConnectorName = brokerSetting.getString(BrokerSettingKey.INTERNAL_AMQP_ACCEPTOR_NAME);
        mqttInternalConnectorPort = ":" + brokerSetting.getString(BrokerSettingKey.INTERNAL_MQTT_ACCEPTOR_PORT);
        mqttInternalConnectorName = brokerSetting.getString(BrokerSettingKey.INTERNAL_MQTT_ACCEPTOR_NAME);
    }

    public String getConnectionId(RemotingConnection remotingConnection) {
        return remotingConnection.getID().toString();
    }

    public boolean isInternal(RemotingConnection remotingConnection) {
        String protocolName = remotingConnection.getProtocolName();
        if (remotingConnection instanceof ActiveMQProtonRemotingConnection) {
//            AMQPConnectionContext connectionContext = ((ActiveMQProtonRemotingConnection)remotingConnection).getAmqpConnection();
            Connection connection = ((ActiveMQProtonRemotingConnection) remotingConnection).getAmqpConnection().getConnectionCallback().getTransportConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("Connector: {} - Remote container: {} - connection id: {} - local address: {}",
                    remotingConnection.getProtocolName(), ((ActiveMQProtonRemotingConnection) remotingConnection).getAmqpConnection().getRemoteContainer(), connection.getID(), connection.getLocalAddress());
            }
            return isAmqpInternal(connection.getLocalAddress());
        } else if (remotingConnection instanceof MQTTConnection) {
            Connection connection = ((MQTTConnection) remotingConnection).getTransportConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("Connector: {} - Remote address: {} - connection id: {} - local address: {}",
                    remotingConnection.getProtocolName(), connection.getRemoteAddress(), connection.getID(), connection.getLocalAddress());
            }
            return isMqttInternal(connection.getLocalAddress());
        }
        else {
            return false;
        }
    }

    //is internal if the inbound connection is coming from the amqp connector
    public boolean isAmqpInternal(String localAddress) {
        return localAddress.endsWith(amqpInternalConnectorPort);
    }

    private boolean isMqttInternal(String localAddress, String protocolName) {
        //is internal if the inbound connection is coming from the mqtt internal connector
        //are the first check redundant? If the connector name is what is expected should be enough?
        return
                (localAddress.endsWith(mqttInternalConnectorPort) && //local port internal mqtt
                        mqttInternalConnectorName.equalsIgnoreCase(protocolName));
    }
}
