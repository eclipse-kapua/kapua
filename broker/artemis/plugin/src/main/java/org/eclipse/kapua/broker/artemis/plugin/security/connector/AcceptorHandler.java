/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.broker.artemis.plugin.security.connector;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.ActiveMQExceptionType;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSetting;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSettingKey;
import org.eclipse.kapua.locator.KapuaLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class to handle acceptor lifecycle (add/remove acceptors on demand)
 */
public class AcceptorHandler {

    private final Logger logger = LoggerFactory.getLogger(AcceptorHandler.class);

    private static final int RECEIVE_BUFFER_SIZE = 1048576;
    private static final int SEND_BUFFER_SIZE = 1048576;

    private int receiveBufferSize;
    private int sendBufferSize;

    private ActiveMQServer server;
    //TODO should take care of concurrency?
    private Map<String, String> definedAcceptors;

    /**
     * Creates the handler (without synchronizing acceptors so the {@link #syncAcceptors()} should be call to make acceptor configuration applied)
     *
     * @param definedAcceptors
     * @param server
     */
    public AcceptorHandler(ActiveMQServer server, Map<String, String> definedAcceptors) {
        this.server = server;
        if (definedAcceptors != null) {
            this.definedAcceptors = definedAcceptors;
        } else {
            this.definedAcceptors = new ConcurrentHashMap<>();
        }
        //read buffer parameter
        final BrokerSetting brokerSettings = KapuaLocator.getInstance().getComponent(BrokerSetting.class);
        receiveBufferSize = brokerSettings.getInt(BrokerSettingKey.RECEIVE_BUFFER_SIZE, RECEIVE_BUFFER_SIZE);
        sendBufferSize = brokerSettings.getInt(BrokerSettingKey.SEND_BUFFER_SIZE, SEND_BUFFER_SIZE);
    }

    /**
     * Add acceptor
     *
     * @param name acceptor name
     * @param uri  acceptor uri
     * @return the previous acceptor uri (if present)
     * @throws Exception
     */
    public String addAcceptor(String name, String uri) throws Exception {
        String previousAcceptor = definedAcceptors.put(name, uri);
        syncAcceptors();
        return previousAcceptor;
    }

    /**
     * Remove acceptor
     *
     * @param name acceptor name
     * @return the current acceptor uri (if present)
     * @throws Exception
     */
    public String removeAcceptor(String name) throws Exception {
        String currentAcceptor = definedAcceptors.remove(name);
        syncAcceptors();
        return currentAcceptor;
    }

    /**
     * Synchronize acceptors (to be used at startup since add and remove acceptor are already calling this method)
     *
     * @throws Exception
     */
    public void syncAcceptors() throws Exception {
        logger.info("Init acceptors... server started: {} - {}", server.isStarted(), server.getState());
        if (server.isStarted()) {
            List<String> acceptorToRemove = new ArrayList<>();
            server.getConfiguration().getAcceptorConfigurations().forEach(tc -> {
                String acceptorName = tc.getName();
                logger.info("Checking acceptor {}", acceptorName);
                if (definedAcceptors.get(acceptorName) == null) {
                    acceptorToRemove.add(acceptorName);
                    logger.info("Adding acceptor {} to the remove list", acceptorName);
                } else {
                    logger.info("Leaving acceptor {} running", acceptorName);
                }
            });
            acceptorToRemove.forEach(acceptorName -> {
                logger.info("Stopping acceptor {}...", acceptorName);
                try {
                    server.getRemotingService().getAcceptor(acceptorName).stop();
                    server.getRemotingService().destroyAcceptor(acceptorName);
                    TransportConfiguration tc = getByName(acceptorName);
                    server.getConfiguration().getAcceptorConfigurations().remove(tc);
                } catch (Exception e) {
                    logger.error("Error stopping acceptor {}... Error: {}", acceptorName, e.getMessage(), e);
                }
                logger.info("Stopping acceptor {}... DONE", acceptorName);
            });
        }
//        server.getConfiguration().clearAcceptorConfigurations();

        definedAcceptors.forEach((name, uri) -> {
            logger.info("Adding acceptor... name: {} - uri: {}", name, uri);
            try {
                if (server.getRemotingService().getAcceptor(name) == null || !server.getRemotingService().getAcceptor(name).isStarted()) {
                    if (!uri.contains("tcpReceiveBufferSize")) {
                        uri += ";tcpReceiveBufferSize=" + receiveBufferSize;
                    }
                    if (!uri.contains("tcpSendBufferSize")) {
                        uri += ";tcpSendBufferSize=" + sendBufferSize;
                    }
                    logger.info("Adding acceptor (after buffer size tuning)... name: {} - uri: {}", name, uri);
                    server.getConfiguration().addAcceptorConfiguration(name, uri);
                    server.getRemotingService().createAcceptor(name, uri);
                    if (server.isStarted()) {
                        server.getRemotingService().getAcceptor(name).start();
                    }
                }
            } catch (Exception e) {
                logger.error("Error initializing acceptor {}... Error: {}", name, e.getMessage(), e);
            }
        });
        logAcceptorConfigurations();
    }

    private TransportConfiguration getByName(String name) throws ActiveMQException {
        for (TransportConfiguration tc : server.getConfiguration().getAcceptorConfigurations()) {
            if (tc.getName().equals(name)) {
                return tc;
            }
        }
        //TODO find proper exception
        throw new ActiveMQException(ActiveMQExceptionType.SECURITY_EXCEPTION, "Internal error!");
    }

    private void logAcceptorConfigurations() {
        logger.info("Defined acceptors...");
        server.getConfiguration().getAcceptorConfigurations().forEach(tc -> {
            logger.info("name: {} - factory: {}", tc.getName(), tc.getFactoryClassName());
            logger.info("\tparams");
            tc.getParams().forEach((key, value) -> logger.info("\t\t{} : {}", key, value));
            logger.info("\textraparams");
            tc.getExtraParams().forEach((key, value) -> logger.info("\t\t{} : {}", key, value));
        });
    }
}
