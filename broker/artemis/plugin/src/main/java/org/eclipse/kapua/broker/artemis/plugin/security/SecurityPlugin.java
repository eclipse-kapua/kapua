/*******************************************************************************
 * Copyright (c) 2019, 2025 Eurotech and/or its affiliates and others
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

import com.codahale.metrics.Timer.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.handler.ssl.SslHandler;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.ActiveMQExceptionType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyServerConnection;
import org.apache.activemq.artemis.core.security.CheckType;
import org.apache.activemq.artemis.core.security.Role;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager5;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.LoginMetric;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.PublishMetric;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.SubscribeMetric;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSetting;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSettingKey;
import org.eclipse.kapua.client.security.ServiceClient.EntityType;
import org.eclipse.kapua.client.security.ServiceClient.SecurityAction;
import org.eclipse.kapua.client.security.bean.AuthRequest;
import org.eclipse.kapua.client.security.bean.AuthResponse;
import org.eclipse.kapua.client.security.bean.ConnectionInfo;
import org.eclipse.kapua.client.security.bean.EntityRequest;
import org.eclipse.kapua.client.security.bean.EntityResponse;
import org.eclipse.kapua.client.security.bean.KapuaPrincipalImpl;
import org.eclipse.kapua.client.security.context.SessionContext;
import org.eclipse.kapua.client.security.context.Utils;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.KapuaPrincipal;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.auth.Subject;
import javax.security.auth.login.CredentialException;
import javax.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.util.Set;

/**
 * Kapua Artemis security plugin implementation (authentication/authorization)
 */
public class SecurityPlugin implements ActiveMQSecurityManager5 {

    protected static Logger logger = LoggerFactory.getLogger(SecurityPlugin.class);

    private final LoginMetric loginMetric;
    private final PublishMetric publishMetric;
    private final SubscribeMetric subscribeMetric;
    private final PluginUtility pluginUtility;

    protected ServerContext serverContext;
    //to avoid deadlock this field will be initialized by the first internal login call
    protected AccountInfo adminAccountInfo;
    private final LocalCache<String, KapuaId> usernameScopeIdCache;

    public SecurityPlugin() {
        logger.info("Initializing SecurityPlugin...");
        final KapuaLocator kapuaLocator = KapuaLocator.getInstance();
        loginMetric = kapuaLocator.getComponent(LoginMetric.class);
        publishMetric = kapuaLocator.getComponent(PublishMetric.class);
        subscribeMetric = kapuaLocator.getComponent(SubscribeMetric.class);
        serverContext = KapuaLocator.getInstance().getComponent(ServerContext.class);
        pluginUtility = KapuaLocator.getInstance().getComponent(PluginUtility.class);
        final BrokerSetting brokerSettings = kapuaLocator.getComponent(BrokerSetting.class);
        usernameScopeIdCache = new LocalCache<>(
                brokerSettings.getInt(BrokerSettingKey.CACHE_SCOPE_ID_SIZE),
                brokerSettings.getInt(BrokerSettingKey.CACHE_SCOPE_ID_SIZE),
                null);
        logger.info("Initializing SecurityPlugin... DONE");
    }

    @Override
    public Subject authenticate(String username, String password, RemotingConnection remotingConnection, String securityDomain) {
        //like a cache looks for an already authenticated user in context
        //Artemis does the authenticate call even when checking for authorization (publish, subscribe, manage)
        //since we keep a "Kapua session" map that is cleaned when the connection is dropped no security issues will come if this cache is used to avoid redundant login process
        String connectionId = pluginUtility.getConnectionId(remotingConnection);
        String clientIp = remotingConnection.getTransportConnection().getRemoteAddress();
        String clientId = extractAndValidateClientId(remotingConnection);
        try {
            SessionContext sessionContext = serverContext.getSecurityContext().getSessionContextWithCacheFallback(connectionId);
            if (sessionContext != null && sessionContext.getPrincipal() != null) {
                logger.debug("### authenticate user (cache found): {} - clientId: {} - remoteIP: {} - connectionId: {}", username, clientId, remotingConnection.getTransportConnection().getRemoteAddress(), connectionId);
                loginMetric.getAuthenticateFromCache().inc();
                return serverContext.getSecurityContext().buildFromPrincipal(sessionContext.getPrincipal());
            } else {
                logger.debug("### authenticate user (no cache): {} - clientId: {} - remoteIP: {} - connectionId: {}", username, clientId, remotingConnection.getTransportConnection().getRemoteAddress(), connectionId);
                if (!remotingConnection.getTransportConnection().isOpen()) {
                    logger.info("Connection (connectionId: {}) is closed (stealing link occurred?)", connectionId);
                    loginMetric.getLoginClosedConnectionFailure().inc();
                    return null;
                }
                ConnectionInfo connectionInfo = new ConnectionInfo(
                        pluginUtility.getConnectionId(remotingConnection),//connectionId
                        clientId,//clientId
                        clientIp,//clientIp
                        remotingConnection.getTransportConnection().getConnectorConfig().getName(),//connectorName
                        remotingConnection.getProtocolName(),//transportProtocol
                        (String) remotingConnection.getTransportConnection().getConnectorConfig().getCombinedParams().get("sslEnabled"),//sslEnabled
                        getPeerCertificates(remotingConnection));//clientsCertificates
                return pluginUtility.isInternal(remotingConnection) ?
                        authenticateInternalConn(connectionInfo, connectionId, username, password, remotingConnection) :
                        authenticateExternalConn(connectionInfo, connectionId, username, password, remotingConnection);
            }
        } catch (Exception e) {
            //internal error. do not disclose any info about the reason. just deny the login
            logger.error("Internal error!", e);
            return null;
        }
    }

    private String extractAndValidateClientId(RemotingConnection remotingConnection) {
        String clientId = remotingConnection.getClientID();
        //leave the clientId validation to the DeviceCreator. Here just check for / or ::
        //ArgumentValidator.match(clientId, DeviceValidationRegex.CLIENT_ID, "deviceCreator.clientId");
        if (clientId != null && (clientId.contains("/") || clientId.contains("::"))) {
            //TODO look for the right exception mapped to MQTT invalid client id error code
            throw new SecurityException("Invalid Client Id!");
        }
        return clientId;
    }

    private Subject authenticateInternalConn(ConnectionInfo connectionInfo, String connectionId, String username, String password, RemotingConnection remotingConnection) {
        loginMetric.getInternalConnector().getAttempt().inc();
        String usernameToCompare = SystemSetting.getInstance().getString(SystemSettingKey.BROKER_INTERNAL_CONNECTOR_USERNAME);
        String passToCompare = SystemSetting.getInstance().getString(SystemSettingKey.BROKER_INTERNAL_CONNECTOR_PASSWORD);
        try {
            if (usernameToCompare == null || !usernameToCompare.equals(username) ||
                    passToCompare == null || !passToCompare.equals(password)) {
                throw new ActiveMQException(ActiveMQExceptionType.SECURITY_EXCEPTION, "User not authorized! Internal connection credential did not match!");
            }
            logger.info("Authenticate internal: user: {} - clientId: {} - connectionIp: {} - connectionId: {} - remoteIP: {} - isOpen: {}",
                    username, connectionInfo.getClientId(), connectionInfo.getClientIp(), remotingConnection.getID(),
                    remotingConnection.getTransportConnection().getRemoteAddress(), remotingConnection.getTransportConnection().isOpen());
            KapuaPrincipal kapuaPrincipal = buildInternalKapuaPrincipal(getAdminAccountInfo().getId(), username, connectionInfo.getClientId());
            //from JMS 2 specs "Although setting client ID remains mandatory when creating an unshared durable subscription, it is optional when creating a shared durable subscription."
            //update client id with account|clientId (see pattern)
            String fullClientId = Utils.getFullClientId(getAdminAccountInfo().getId(), connectionInfo.getClientId());
            remotingConnection.setClientID(fullClientId);
            Subject subject = buildInternalSubject(kapuaPrincipal);
            SessionContext sessionContext = new SessionContext(kapuaPrincipal, getAdminAccountInfo().getName(), connectionInfo,
                    serverContext.getBrokerIdentity().getBrokerId(), serverContext.getBrokerIdentity().getBrokerHost(),
                    true, false);
            serverContext.getSecurityContext().setSessionContext(sessionContext, null);
            loginMetric.getInternalConnector().getSuccess().inc();
            return subject;
        } catch (Exception e) {
            loginMetric.getInternalConnector().getFailure().inc();
            logger.error("Authenticate internal: error: {}", e.getMessage());
            return null;
        }
    }

    private Subject authenticateExternalConn(ConnectionInfo connectionInfo, String connectionId, String username, String password, RemotingConnection remotingConnection) throws Exception {
        if (connectionInfo.getClientId()==null) {
            return authenticateExternalConnCallable(connectionInfo, connectionId, username, password, remotingConnection);
        }
        else {
            return serverContext.getSecurityContext().callWithLock(connectionInfo.getClientId(),
                () -> authenticateExternalConnCallable(connectionInfo, connectionId, username, password, remotingConnection));
        }
    }

    private Subject authenticateExternalConnCallable(ConnectionInfo connectionInfo, String connectionId, String username, String password, RemotingConnection remotingConnection) {
        loginMetric.getExternalConnector().getAttempt().inc();
        Context timeTotal = loginMetric.getExternalAddConnection().time();
        try {
            logger.info("Authenticate external: user: {} - clientId: {} - connectionIp: {} - connectionId: {} - isOpen: {}",
                    username, connectionInfo.getClientId(), connectionInfo.getClientIp(), remotingConnection.getID(), remotingConnection.getTransportConnection().isOpen());
            String fullClientId = Utils.getFullClientId(getScopeId(username), connectionInfo.getClientId());
            AuthRequest authRequest = new AuthRequest(
                    serverContext.getClusterName(),
                    serverContext.getBrokerIdentity().getBrokerHost(), SecurityAction.brokerConnect.name(),
                    username, password, connectionInfo,
                    serverContext.getBrokerIdentity().getBrokerHost(), serverContext.getBrokerIdentity().getBrokerId());
            SessionContext currentSessionContext = serverContext.getSecurityContext().getSessionContextByClientId(fullClientId);

            serverContext.getSecurityContext().updateStealingLinkAndIllegalState(authRequest, connectionId, currentSessionContext != null ? currentSessionContext.getConnectionId() : null);
            AuthResponse authResponse = serverContext.getAuthServiceClient().brokerConnect(authRequest);
            validateAuthResponse(authResponse);
            KapuaPrincipal principal = new KapuaPrincipalImpl(authResponse);
            SessionContext sessionContext = new SessionContext(principal, authResponse.getAccountName(), connectionInfo, authResponse.getKapuaConnectionId(),
                    serverContext.getBrokerIdentity().getBrokerId(), serverContext.getBrokerIdentity().getBrokerHost(),
                    authResponse.isAdmin(), authResponse.isMissing());

            //update client id with account|clientId (see pattern)
            remotingConnection.setClientID(fullClientId);
            logger.info("Authenticate external: connectionId: {} - old: {}", sessionContext.getConnectionId(), currentSessionContext != null ? currentSessionContext.getConnectionId() : "N/A");
            Subject subject = null;
            //this call is synchronized on sessionId value
            if (serverContext.getSecurityContext().setSessionContext(sessionContext, authResponse.getAcls())) {
                subject = serverContext.getSecurityContext().buildFromPrincipal(sessionContext.getPrincipal());
            }
            loginMetric.getExternalConnector().getSuccess().inc();
            return subject;
        } catch (Exception e) {
            loginMetric.getExternalConnector().getFailure().inc();
            logger.error("Authenticate external: error: {}", e.getMessage());
            return null;
        } finally {
            timeTotal.stop();
        }
    }

    @Override
    public boolean authorize(Subject subject, Set<Role> roles, CheckType checkType, String address) {
        boolean allowed = false;
        //TODO improve it to check for null
        KapuaPrincipal principal = getKapuaPrincipal(subject);
        logger.debug("### authorizing address: {} - check type: {}", address, checkType.name());
        if (principal != null) {
            logger.debug("### authorizing address: {} - check type: {} - clientId: {} - clientIp: {}", address, checkType.name(), principal.getClientId(), principal.getClientIp());
            if (!principal.isInternal()) {
                SessionContext sessionContext;
                try {
                    sessionContext = serverContext.getSecurityContext().getSessionContextWithCacheFallback(principal.getConnectionId());
                } catch (Exception e) {
                    //exception not expected
                    logger.error("", e);
                    return false;
                }
                switch (checkType) {
                    case CONSUME:
                        allowed = serverContext.getSecurityContext().checkConsumerAllowed(sessionContext, address);
                        if (!allowed) {
                            subscribeMetric.getNotAllowedMessages().inc();
                        }
                        break;
                    case SEND:
                        allowed = serverContext.getSecurityContext().checkPublisherAllowed(sessionContext, address);
                        if (!allowed) {
                            publishMetric.getNotAllowedMessages().inc();
                        } else {
                            publishMetric.getAllowedMessages().inc();
                        }
                        break;
                    case BROWSE:
                        allowed = serverContext.getSecurityContext().checkConsumerAllowed(sessionContext, address);
                        break;
                    case DELETE_DURABLE_QUEUE:
                        allowed = true;
                        break;
                    case CREATE_NON_DURABLE_QUEUE:
                        allowed = serverContext.getSecurityContext().checkConsumerAllowed(sessionContext, address);
                        break;
                    case DELETE_ADDRESS:
                        serverContext.getAddressAccessTracker().remove(address);
                        break;
                    default:
                        allowed = serverContext.getSecurityContext().checkAdminAllowed(sessionContext, address);
                        break;
                }
            } else {
                switch (checkType) {
                case SEND:
                    publishMetric.getAllowedMessagesInternal().inc();
                    break;
                default:
                    break;
                }
                allowed = true;
            }
        }
        if (!allowed) {
            logger.info("### authorizing address NOT ALLOWED: {} - check type: {} - clientId: {} - clientIp: {}", address, checkType.name(), principal.getClientId(), principal.getClientIp());
        }
        //otherwise no principal (or error while getting it) so no authorization
        return allowed;
    }

    @Override
    public boolean validateUser(String user, String password) {
        logger.info("### validate {} - {}", user, password);
        return false;
    }

    @Override
    public boolean validateUserAndRole(String user, String password, Set<Role> roles, CheckType checkType) {
        logger.info("### validate user and role {} - {}", user, password);
        return false;
    }

    //
    //Utilities methods
    //
    private void validateAuthResponse(AuthResponse authResponse) throws CredentialException, ActiveMQException {
        if (authResponse == null) {
            throw new ActiveMQException(ActiveMQExceptionType.SECURITY_EXCEPTION, "User not authorized! Authentication response is empty!");
        } else if (authResponse.getErrorCode() != null) {
            //analyze response code
            String errorCode = authResponse.getErrorCode();
            if (KapuaAuthenticationErrorCodes.UNKNOWN_LOGIN_CREDENTIAL.name().equals(errorCode) ||
                    KapuaAuthenticationErrorCodes.INVALID_LOGIN_CREDENTIALS.name().equals(errorCode) ||
                    KapuaAuthenticationErrorCodes.INVALID_CREDENTIALS_TYPE_PROVIDED.name().equals(errorCode)) {
                logger.warn("Invalid username or password for user {} ({})", authResponse.getUsername(), errorCode);
                // activeMQ will map CredentialException into a CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD message (see javadoc on top of this method)
                loginMetric.getInvalidUserPassword().inc();
                throw new CredentialException("Invalid username and/or password or disabled or expired account!");
            } else if (KapuaAuthenticationErrorCodes.LOCKED_LOGIN_CREDENTIAL.name().equals(errorCode) ||
                    KapuaAuthenticationErrorCodes.DISABLED_LOGIN_CREDENTIAL.name().equals(errorCode) ||
                    KapuaAuthenticationErrorCodes.EXPIRED_LOGIN_CREDENTIALS.name().equals(errorCode)) {
                logger.warn("User {} not authorized ({})", authResponse.getUsername(), errorCode);
                //TODO check if it's still valid with Artemis
                // activeMQ-MQ will map SecurityException into a CONNECTION_REFUSED_NOT_AUTHORIZED message (see javadoc on top of this method)
                throw new SecurityException("User not authorized! Credentials provided are either locked, disabled or expired");
            } else {
                //KapuaAuthenticationErrorCodes.AUTHENTICATION_ERROR - ILLEGAL_ACCESS etc
                //TODO throw other exception?
                throw new SecurityException("User not authorized! Error returned from the Broker Authentication Service: " + errorCode);
            }
        }
    }

    private Certificate[] getPeerCertificates(RemotingConnection remotingConnection) {
        NettyServerConnection nettyServerConnection = ((NettyServerConnection) remotingConnection.getTransportConnection());
        try {
            SslHandler sslhandler = (SslHandler) nettyServerConnection.getChannel().pipeline().get("ssl");
            if (sslhandler != null) {
                Certificate[] localCertificates = sslhandler.engine().getSession().getLocalCertificates();
                Certificate[] clientCertificates = sslhandler.engine().getSession().getPeerCertificates();
                X509Certificate[] x509ClientCertificates = sslhandler.engine().getSession().getPeerCertificateChain();//???
                return clientCertificates;
            } else {
                return null;
            }
        } catch (SSLPeerUnverifiedException e) {
            logger.error("", e);
            return null;
        }
    }

    private KapuaPrincipal getKapuaPrincipal(Subject subject) {
        try {
            //return the first Principal if it's a KapuaPrincipal, otherwise catch every Exception and return null
            return (KapuaPrincipal) subject.getPrincipals().iterator().next();
        } catch (Exception e) {
            return null;
        }
    }

    private KapuaPrincipal buildInternalKapuaPrincipal(KapuaId accountId, String name, String clientId) {
        return new KapuaPrincipalImpl(accountId, name, clientId);
    }

    private Subject buildInternalSubject(KapuaPrincipal kapuaPrincipal) {
        Subject subject = new Subject();
        subject.getPrincipals().add(kapuaPrincipal);
        return subject;
    }

    private AccountInfo getAdminAccountInfo() throws JsonProcessingException, JMSException, InterruptedException {
        if (adminAccountInfo == null) {
            adminAccountInfo = getAdminAccountInfoNoCache();
        }
        return adminAccountInfo;
    }

    private KapuaId getScopeId(String username) throws JsonProcessingException, JMSException, InterruptedException {
        KapuaId scopeId = usernameScopeIdCache.get(username);
        //no synchronization needed. At the worst the getScopeId will be called few times instead of just one but the overall performances will be better without synchronization
        if (scopeId == null) {
            scopeId = getScopeIdNoCache(username);
            usernameScopeIdCache.put(username, scopeId);
        }
        return scopeId;
    }

    private AccountInfo getAdminAccountInfoNoCache() throws JsonProcessingException, JMSException, InterruptedException {
        EntityRequest accountRequest = new EntityRequest(
                serverContext.getClusterName(),
                serverContext.getBrokerIdentity().getBrokerHost(),
                SecurityAction.getEntity.name(),
                EntityType.account.name(),
                SystemSetting.getInstance().getString(SystemSettingKey.SYS_ADMIN_ACCOUNT));
        EntityResponse accountResponse;
        try {
            accountResponse = serverContext.getAuthServiceClient().getEntity(accountRequest);
            if (accountResponse != null) {
                return new AccountInfo(KapuaEid.parseCompactId(accountResponse.getId()), accountResponse.getName());
            }
        } catch (Exception e) {
            logger.warn("Error getting scopeId for user admin", e);
        }
        throw new SecurityException("User not authorized! Cannot get Admin Account info!");
    }

    /**
     * Return the scopeId, if user exist, otherwise throws SecurityException
     * No checks for user validity, just return scope id to be used, for example, to build full client id
     *
     * @param username
     * @return
     * @throws InterruptedException
     * @throws JMSException
     * @throws JsonProcessingException
     */
    private KapuaId getScopeIdNoCache(String username) throws JsonProcessingException, JMSException, InterruptedException {
        EntityRequest userRequest = new EntityRequest(
                serverContext.getClusterName(),
                serverContext.getBrokerIdentity().getBrokerHost(),
                SecurityAction.getEntity.name(),
                EntityType.user.name(),
                username);
        try {
            EntityResponse userResponse = serverContext.getAuthServiceClient().getEntity(userRequest);
            if (userResponse != null && userResponse.getScopeId() != null) {
                return KapuaEid.parseCompactId(userResponse.getScopeId());
            }
        } catch (Exception e) {
            logger.warn("Error getting scopeId for username {}", username, e);
        }
        throw new SecurityException("User not authorized! Cannot get scopeId for username:" + username);
    }

}

class AccountInfo {

    private KapuaId id;
    private String name;

    public AccountInfo(KapuaId id, String name) {
        this.id = id;
        this.name = name;
    }

    public KapuaId getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
