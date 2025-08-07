/*******************************************************************************
 * Copyright (c) 2021, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.broker.artemis.plugin.security.context;

import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.artemis.plugin.security.MetricsSecurityPlugin;
import org.eclipse.kapua.broker.artemis.plugin.security.PluginUtility;
import org.eclipse.kapua.broker.artemis.plugin.security.RunWithLock;
import org.eclipse.kapua.broker.artemis.plugin.security.RunWithLock.LockType;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.LoginMetric;
import org.eclipse.kapua.client.security.AuthErrorCodes;
import org.eclipse.kapua.client.security.KapuaIllegalDeviceStateException;
import org.eclipse.kapua.client.security.ServiceClient.SecurityAction;
import org.eclipse.kapua.client.security.bean.AuthAcl;
import org.eclipse.kapua.client.security.bean.AuthRequest;
import org.eclipse.kapua.client.security.context.SessionContext;
import org.eclipse.kapua.client.security.context.Utils;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.localevent.ExecutorWrapper;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.service.authentication.KapuaPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * TODO move this under DI. Ask for a way to do so in Artemis (is still Artemis managed by Spring as ActiveMQ 5?)
 * So the singleton can be managed by the DI.
 * <B>NOTE! This class should be a singleton but this is in charge of the caller.</B>
 */
public final class SecurityContext {

    protected static Logger logger = LoggerFactory.getLogger(SecurityContext.class);
    private static final String REPORT_HEADER = "################################################################################################";
    private static final String REPORT_SEPARATOR = "------------------------------------------------------------------------------------------------";

    private PluginUtility pluginUtility;

    private enum ReportType {
        Full,
        Compact,
        DetailedServer
    }

    private LoginMetric loginMetric;

    //reserved String used as separator by Artemis on NOT_DURABLE_QUEUES (and DURABLE also?)
    private static final String DOUBLE_COLON = "::";

    //concurrency shouldn't be an issue since this set will contain the list of active connections
    private final Set<String> activeConnections = new HashSet<>();
    private final LocalCache<String, ConnectionToken> connectionTokenCache;
    private final LocalCache<String, SessionContext> sessionContextCache;
    private final LocalCache<String, Acl> aclCache;
    private final MetricsSecurityPlugin metricsSecurityPlugin;
    private final RunWithLock runWithLock;

    //use string as key since some method returns DefaultChannelId as connection id, some other a string
    //the string returned by some method as connection id is the asShortText of DefaultChannelId
    private final Map<String, SessionContext> sessionContextMapByClient;

    //by connection id context
    private final Map<String, SessionContext> sessionContextMap;
    private final Map<String, Acl> aclMap;

    private final boolean printData;
    private ExecutorWrapper executorWrapper;

    @Inject
    public SecurityContext(LoginMetric loginMetric,
                           boolean printData,
                           LocalCache<String, ConnectionToken> connectionTokenCache,
                           LocalCache<String, SessionContext> sessionContextCache,
                           LocalCache<String, Acl> aclCache,
                           MetricsSecurityPlugin metricsSecurityPlugin,
                           PluginUtility pluginUtility,
                           RunWithLock runWithLock) {
        this.loginMetric = loginMetric;
        this.printData = printData;
        this.connectionTokenCache = connectionTokenCache;
        this.sessionContextCache = sessionContextCache;
        this.aclCache = aclCache;
        this.metricsSecurityPlugin = metricsSecurityPlugin;
        this.pluginUtility = pluginUtility;
        this.runWithLock = runWithLock;
        this.sessionContextMapByClient = new ConcurrentHashMap<>();
        this.sessionContextMap = new ConcurrentHashMap<>();
        this.aclMap = new ConcurrentHashMap<>();
    }

    public void init(ActiveMQServer server) {
        if (printData) {
            if (executorWrapper == null) {
                executorWrapper = new ExecutorWrapper("ServerReport", () -> printCompactReport(server, "ServerReportTask"), 60, 30, TimeUnit.SECONDS);
                executorWrapper.start();
            } else {
                logger.warn("ServerReportTask already started!");
            }
        }
        try {
            metricsSecurityPlugin.init(server,
                    () -> sessionContextMap.size(),
                    () -> sessionContextMapByClient.size(),
                    () -> aclMap.size(),
                    () -> activeConnections.size());
        } catch (KapuaException e) {
            //do nothing
            //in this case one or more metrics are not registered but it's not a blocking issue
            logger.error("Cannot register one or more broker core metrics!", e);
        }
    }

    public void shutdown(ActiveMQServer server) {
        if (executorWrapper != null) {
            executorWrapper.stop();
        }
    }

    /**
     * return true if an old token was present for the provided connection id (so, likely, a stealing link occurred)
     * @param sessionContext
     * @param authAcls
     * @return
     * @throws Exception
     */
    public boolean setSessionContext(SessionContext sessionContext, List<AuthAcl> authAcls) throws Exception {
        logger.debug("Updating session context for connection id: {}", sessionContext.getConnectionId());
        String connectionId = sessionContext.getConnectionId();
        return runWithLock.run(LockType.CONNECTION_ID, connectionId, () -> {
            if (updateConnectionTokenOnConnection(connectionId) == null) {
                logger.debug("Setting session context for connection id: {}", connectionId);
                activeConnections.add(connectionId);
                //fill by connection id context
                sessionContextMap.put(connectionId, sessionContext);
                aclMap.put(connectionId, new Acl(loginMetric, sessionContext.getPrincipal(), authAcls));
                //fill by full client id context
                sessionContextMapByClient.put(Utils.getFullClientId(sessionContext), sessionContext);
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Put the new token in cache.<br>
     * If no previous token where present means no stealing link occurs (probably) and return null.<br>
     * If a previous token is found it will be returned.
     *
     * @param connectionId
     * @return
     */
    private ConnectionToken updateConnectionTokenOnConnection(String connectionId) {
        ConnectionToken connectionToken = connectionTokenCache.getAndRemove(connectionId);
        if (connectionToken == null) {
            connectionTokenCache.put(connectionId,
                    new ConnectionToken(SecurityAction.brokerConnect, KapuaDateUtils.getKapuaSysDate()));
        } else {
            //the disconnect callback is called before the connect so nothing to add to the context
            loginMetric.getDisconnectCallbackCallFailure().inc();
            logger.debug("Connect callback called before the disconnection callback ({} - {} - {})",
                    connectionId, connectionToken.getAction(), connectionToken.getActionDate());
        }
        return connectionToken;
    }

    public void updateConnectionTokenOnDisconnection(String connectionId) throws Exception {
        if (connectionTokenCache.getAndRemove(connectionId) == null) {
            //put the connection token
            connectionTokenCache.put(connectionId,
                new ConnectionToken(SecurityAction.brokerDisconnect, KapuaDateUtils.getKapuaSysDate()));
        }
    }

    public SessionContext cleanSessionContext(SessionContext sessionContext) throws Exception {
        String connectionId = sessionContext.getConnectionId();
        logger.debug("Updating session context for connection id: {}", connectionId);
        //cleaning context and filling cache
        SessionContext sessionContextOld = sessionContextMap.remove(connectionId);
        if (sessionContextOld != null) {
            sessionContextCache.put(connectionId, sessionContextOld);
        }
        Acl aclOld = aclMap.remove(connectionId);
        if (aclOld != null) {
            aclCache.put(connectionId, aclOld);
        }
        activeConnections.remove(connectionId);

        String fullClientId = Utils.getFullClientId(sessionContext);
        SessionContext currentSessionContext = sessionContextMapByClient.get(fullClientId);
        //if no stealing link remove the context by client id
        //on a stealing link currentSessionContext could be null if the disconnect of the latest connected client happens before the others
        if (currentSessionContext == null) {
            logger.debug("Cannot find session context by full client id: {}", fullClientId);
            loginMetric.getSessionContextByClientIdNotFound().inc();
        } else {
            if (connectionId.equals(currentSessionContext.getConnectionId())) {
                sessionContextMapByClient.remove(fullClientId);
                logger.debug("Disconnect: NO stealing - remove session context by clientId: {} - connection id: {}",
                    currentSessionContext.getClientId(), currentSessionContext.getConnectionId());
            } else {
                logger.info("Disconnect: stealing - leave session context by clientId: {} - connection id: {} (old connection id: {})",
                    currentSessionContext.getClientId(), currentSessionContext.getConnectionId(), connectionId);
            }
        }
        return currentSessionContext;
    }

    public SessionContext getSessionContextByClientId(String fullClientId) {
        return sessionContextMapByClient.get(fullClientId);
    }

    public SessionContext getSessionContextWithCacheFallback(String connectionId) throws Exception {
        return runWithLock.run(LockType.CONNECTION_ID, connectionId, () -> {
            SessionContext sessionContext = sessionContextMap.get(connectionId);
            if (sessionContext == null) {
                sessionContext = sessionContextCache.get(connectionId);
            }
            return sessionContext;
        });
    }

    public <T> T callWithLock(LockType lockType, String key, Callable<T> callable) throws Exception {
        return runWithLock.run(lockType, key, callable);
    }

    public SessionContext getSessionContext(String connectionId) {
        return sessionContextMap.get(connectionId);
    }

    public boolean checkPublisherAllowed(SessionContext sessionContext, String address) {
        Acl acl = getAcl(sessionContext.getConnectionId());
        return acl != null && acl.canWrite(sessionContext.getPrincipal(), cleanSubscriptionPrefix(address));
    }

    public boolean checkConsumerAllowed(SessionContext sessionContext, String address) {
        Acl acl = getAcl(sessionContext.getConnectionId());
        return acl != null && acl.canRead(sessionContext.getPrincipal(), cleanSubscriptionPrefix(address));
    }

    public boolean checkAdminAllowed(SessionContext sessionContext, String address) {
        Acl acl = getAcl(sessionContext.getConnectionId());
        return acl != null && acl.canManage(sessionContext.getPrincipal(), cleanSubscriptionPrefix(address));
    }

    private String cleanSubscriptionPrefix(String address) {
        int doubleColonPos = address.indexOf(DOUBLE_COLON);
        if (doubleColonPos > -1) {
            return address.substring(doubleColonPos + 1 + address.substring(doubleColonPos).indexOf('.'));
        } else {
            return address;
        }
    }

    private Acl getAcl(String connectionId) {
        Acl acl = aclMap.get(connectionId);
        if (acl == null) {
            //try from cache
            acl = aclCache.get(connectionId);
            if (acl != null) {
                loginMetric.getAclCacheHit().inc();
                logger.warn("Got acl for connectionId {} from cache!", connectionId);
            }
        }
        return acl;
    }

    public Subject buildFromPrincipal(KapuaPrincipal kapuaPrincipal) {
        Subject subject = new Subject();
        subject.getPrincipals().add(kapuaPrincipal);
        return subject;
    }

    public boolean updateStealingLinkAndIllegalState(AuthRequest authRequest, String connectionId, String oldConnectionId) {
        boolean isStealingLink = isStealingLink(connectionId, oldConnectionId);
        authRequest.setStealingLink(isStealingLink);
        authRequest.setIllegalState(isIllegalState(authRequest));
        return isStealingLink;
    }

    private boolean isStealingLink(String connectionId, String oldConnectionId) {
        return oldConnectionId != null && !oldConnectionId.equals(connectionId);
    }

    private boolean isIllegalState(AuthRequest authRequest) {
        //TODO make this check based on instanceof
        //something like Class.forName(exceptionClass).. just are we sure we have the exceptionClass implementation available at runtime?
        return KapuaIllegalDeviceStateException.class.getName().equals(authRequest.getExceptionClass()) && AuthErrorCodes.DUPLICATE_CLIENT_ID.name().equals(authRequest.getErrorCode());
    }

    //logger features
    public void printReport(ActiveMQServer server, String caller) {
        printReport(ReportType.Full, server, caller);
    }

    public void printCompactReport(ActiveMQServer server, String caller) {
        printReport(ReportType.Compact, server, caller);
    }

    public void printDetailedServerReport(ActiveMQServer server, String caller) {
        printReport(ReportType.DetailedServer, server, caller);
    }

    private void printReport(ReportType reportType, ActiveMQServer server, String caller) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n").append(REPORT_HEADER).append("\n");
        switch (reportType) {
            case Full:
                appendServerContextReport(builder, server);
                builder.append(REPORT_SEPARATOR).append("\n");
                appendSessionInfoReport(builder, server);
                builder.append(REPORT_SEPARATOR).append("\n");
                appendDetailedServerContextReport(builder, caller, server);
                break;
            case Compact:
                appendServerContextReport(builder, server);
                break;
            case DetailedServer:
                appendDetailedServerContextReport(builder, caller, server);
                break;
            default:
                break;
        }
        builder.append(REPORT_HEADER);
        logger.info("{}", builder);
    }

    private void appendServerContextReport(StringBuilder builder, ActiveMQServer server) {
        builder.append("## Session count: ").append(server.getSessions().size()).
                append(" - Connection count: ").append(server.getConnectionCount()).
                append(" - Broker connections: ").append(server.getBrokerConnections().size()).append("\n");
        builder.append("## session context: ").append(sessionContextMap.size()).append("\n");
        builder.append("## session context by client: ").append(sessionContextMapByClient.size()).append("\n");
        builder.append("## acl: ").append(aclMap.size()).append("\n");
        builder.append("## connection: ").append(activeConnections.size()).append("\n");
    }

    private void appendSessionInfoReport(StringBuilder builder, ActiveMQServer server) {
        builder.append("## Sessions:").append("\n");
        Map<Object, Integer> sessionById = new HashMap<>();
        server.getSessions().forEach(session -> {
            Integer tmp = sessionById.get(session.getConnectionID());
            if (tmp == null) {
                sessionById.put(session.getConnectionID(), new Integer(1));
            } else {
                sessionById.put(session.getConnectionID(), new Integer(tmp.intValue() + 1));
            }
        });
        sessionById.forEach((id, count) -> builder.append("##\tid:count ").append(id).append(":").append(count.intValue()).append("\n"));
    }

    private void appendDetailedServerContextReport(StringBuilder builder, String caller, ActiveMQServer server) {
        builder.append("## Security context: (caller: ").append(caller).append(")\n");
        builder.append("## connection info by client id\n");
        sessionContextMapByClient.forEach((key, sessionContext) -> builder.append("##\tclientId: ").append(key).append(" - ip: ").append(sessionContext.getClientIp()).append(" - conId: ").append(sessionContext.getConnectionId()).append("\tinternal: ").append(sessionContext.isInternal()).append("\n"));
        builder.append("## connection info by connection id\n");
        sessionContextMap.forEach((key, sessionContext) -> builder.append("##\tconId: ").append(key).append(" - clientId: ").append(sessionContext.getClientId()).append(" - ip: ").append(sessionContext.getClientIp()).append("\tinternal: ").append(sessionContext.isInternal()).append("\n"));
        builder.append("## acl by connection id\n");
        aclMap.forEach((key, acl) -> builder.append("##\tconnId: ").append(key).append("\n"));
        builder.append("## connection::");
        server.getSessions().stream().forEach(session -> {
            RemotingConnection remotingConnection = session.getRemotingConnection();
            String connectionId = pluginUtility.getConnectionId(remotingConnection);
            SessionContext sessionContext = getSessionContextByClientId(connectionId);
            String connectionFullClientId = sessionContext != null ? Utils.getFullClientId(sessionContext) : "N/A";
            builder.append("\n\t\t\tconnection id: ").append(connectionId).append(" - client Id: ").append(connectionFullClientId);
        });
    }

}
