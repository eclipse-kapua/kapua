/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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

import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.artemis.plugin.security.RunWithLock.LockType;
import org.eclipse.kapua.broker.artemis.plugin.security.ServerPlugin.Failure;
import org.eclipse.kapua.broker.artemis.plugin.security.context.SecurityContext;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.LoginMetric;
import org.eclipse.kapua.broker.artemis.plugin.utils.BrokerIdentity;
import org.eclipse.kapua.client.security.ServiceClient;
import org.eclipse.kapua.client.security.ServiceClient.SecurityAction;
import org.eclipse.kapua.client.security.bean.AuthRequest;
import org.eclipse.kapua.client.security.context.SessionContext;
import org.slf4j.Logger;

import com.codahale.metrics.Timer.Context;

import javax.inject.Inject;
import javax.inject.Named;

public class ServerContext {

    //TODO provide client pluggability once the rest one will be implemented (now just the AMQP client is available)
    protected final String clusterName;
    protected final ServiceClient authServiceClient;
    protected final SecurityContext securityContext;
    protected final BrokerIdentity brokerIdentity;
    protected ActiveMQServer server;
    protected AddressAccessTracker addressAccessTracker;

    @Inject
    public ServerContext(
            ServiceClient authServiceClient,
            @Named("clusterName") String clusterName,
            BrokerIdentity brokerIdentity,
            SecurityContext securityContext,
            AddressAccessTracker accessTracker) {
        this.clusterName = clusterName;
        this.brokerIdentity = brokerIdentity;
        this.securityContext = securityContext;
        this.addressAccessTracker = accessTracker;
        this.authServiceClient = authServiceClient;
    }

    public void init(ActiveMQServer server) throws KapuaException {
        this.server = server;
        brokerIdentity.init(server);
        securityContext.init(server);
    }

    public void shutdown(ActiveMQServer server) throws KapuaException {
        securityContext.shutdown(server);
    }

    public ActiveMQServer getServer() {
        return server;
    }

    public ServiceClient getAuthServiceClient() {
        return authServiceClient;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public BrokerIdentity getBrokerIdentity() {
        return brokerIdentity;
    }

    public String getClusterName() {
        return clusterName;
    }

    public AddressAccessTracker getAddressAccessTracker() {
        return addressAccessTracker;
    }

    public void closeConnection(Logger logger, LoginMetric loginMetric, RemotingConnection remotingConnection, String connectionId) {
        remotingConnection.disconnect(false);
        remotingConnection.destroy();
        cleanUpConnectionData(logger, loginMetric, connectionId, false, null, null);
    }

    public void cleanUpConnectionData(Logger logger, LoginMetric loginMetric,
            String connectionId, boolean isInternal,
            Failure reason, Exception exception) {
        Context timeTotal = loginMetric.getRemoveConnection().time();
        try {
            securityContext.callWithLock(LockType.CONNECTION_ID, connectionId, () -> {
                SessionContext sessionContext = securityContext.getSessionContext(connectionId);
                //if null probably stealing link occurred and the disconnect stuff are done on connection
                //or during the force disconnections
                if (sessionContext != null) {
                    securityContext.updateConnectionTokenOnDisconnection(connectionId);
                    logErrorMessage(logger, connectionId, exception, reason);
                    SessionContext sessionContextByClient = securityContext.cleanSessionContext(sessionContext, isInternal);
                    if (!isInternal) {
                        AuthRequest authRequest = new AuthRequest(
                                clusterName,
                                brokerIdentity.getBrokerHost(),
                                SecurityAction.brokerDisconnect.name(), sessionContext);
                        securityContext.updateStealingLinkAndIllegalState(authRequest, connectionId, sessionContextByClient != null ? sessionContextByClient.getConnectionId() : null);
                        authServiceClient.brokerDisconnect(authRequest);
                    }
                    else {
                        logger.info("Closing internal connection {} - nothing else to do", connectionId);
                    }
                } else {
                    logger.debug("Cannot find any session context for connection id: {}", connectionId);
                    loginMetric.getCleanupNullSessionFailure().inc();
                }
                return (Void)null;
            });
        } catch (Exception e) {
            loginMetric.getCleanupGenericFailure().inc();
            logger.error("Cleanup connection data error: {}", e.getMessage(), e);
        } finally {
            timeTotal.stop();
        }
    }

    private void logErrorMessage(Logger logger, String connectionId, Exception exception, Failure reason) {
        if (exception != null) {
            String message = "";
            if (StringUtils.isEmpty(exception.getMessage())) {
                message = exception.getCause() != null ? exception.getCause().getMessage() : null;
            }
            else {
                message = exception.getMessage();
            }
            //try to find something meaningful to log (otherwise skip it!)
            if (!StringUtils.isEmpty(message)) {
                logger.info("### cleanUpConnectionData connection: {} - reason: {} - Error: {}", connectionId, reason, message);
            }
            else {
                logger.debug("### cleanUpConnectionData connection: {} - reason: {} - Error: {}", connectionId, reason, message);
            }
            logger.debug("### cleanUpConnectionData error", exception);
        }
    }

}
