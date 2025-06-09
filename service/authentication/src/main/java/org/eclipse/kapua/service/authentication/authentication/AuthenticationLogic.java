/*******************************************************************************
 * Copyright (c) 2017, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.authentication;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.shiro.ShiroException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.security.bean.AclUtils;
import org.eclipse.kapua.client.security.bean.AuthAcl;
import org.eclipse.kapua.client.security.bean.AuthAcl.Action;
import org.eclipse.kapua.client.security.bean.AuthContext;
import org.eclipse.kapua.client.security.metric.AuthMetric;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.device.registry.ConnectionUserCouplingMode;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionCreator;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionAttributes;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionFactory;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication logic definition
 *
 * @since 1.0.0
 */
public abstract class AuthenticationLogic {

    protected static final Logger logger = LoggerFactory.getLogger(AuthenticationLogic.class);

    protected static final String PERMISSION_LOG = "{0}/{1}/{2} - {3}";
    //TODO move to configuration
    protected boolean invalidateCache = true;

    protected final AclCreator aclCreator;
    protected final AuthMetric authenticationMetric;
    protected final DeviceConnectionOptionFactory deviceConnectionOptionFactory;
    protected final DeviceConnectionOptionService deviceConnectionOptionService;
    protected final AuthorizationService authorizationService;
    protected final DeviceConnectionFactory deviceConnectionFactory;
    protected final DeviceConnectionService deviceConnectionService;

    private static final String USER_NOT_AUTHORIZED = "User not authorized!";

    protected AuthenticationLogic(
            AclCreator aclCreator,
            AuthMetric authenticationMetric,
            DeviceConnectionOptionFactory deviceConnectionOptionFactory,
            DeviceConnectionOptionService deviceConnectionOptionService,
            AuthorizationService authorizationService,
            DeviceConnectionFactory deviceConnectionFactory,
            DeviceConnectionService deviceConnectionService) {
        this.aclCreator = aclCreator;
        this.authenticationMetric = authenticationMetric;
        this.deviceConnectionOptionFactory = deviceConnectionOptionFactory;
        this.deviceConnectionOptionService = deviceConnectionOptionService;
        this.authorizationService = authorizationService;
        this.deviceConnectionFactory = deviceConnectionFactory;
        this.deviceConnectionService = deviceConnectionService;
    }

    /**
     * Execute the connect logic returning the authorization list (ACL)
     *
     * @param authContext
     * @return
     * @throws KapuaException
     */
    public abstract List<AuthAcl> connect(AuthContext authContext)
            throws KapuaException;

    /**
     * Execute the disconnection logic
     *
     * @param authContext
     * @return true send disconnect message (if the disconnection is a clean disconnection) false don't send disconnect message (the disconnection is caused by a stealing link or the device is
     *         currently connected to another node)
     */
    public abstract boolean disconnect(AuthContext authContext);

    /**
     * @param userPermissions
     * @param authContext
     * @return
     */
    protected abstract List<AuthAcl> buildAuthorizationMap(UserPermissions userPermissions, AuthContext authContext);

    /**
     * Format the ACL resource based on the pattern and the account name looking into the connection context property
     *
     * @param pattern
     * @param authContext
     * @return
     */
    protected String formatAcl(String pattern, AuthContext authContext) {
        return MessageFormat.format(pattern, authContext.getAccountName());
    }

    /**
     * Format the ACL resource based on the pattern and the account name and client id looking into the connection context property
     *
     * @param pattern
     * @param authContext
     * @return
     */
    protected String formatAclFull(String pattern, AuthContext authContext) {
        return MessageFormat.format(pattern, authContext.getAccountName(), authContext.getClientId());
    }

    /**
     * Create the authorization entry base on the ACL and the resource address
     *
     * @param action
     * @param address
     * @param aclDestinationsLog
     * @return
     */
    protected AuthAcl createAuthorizationEntry(Action action, String address, StringBuilder aclDestinationsLog) {
        AuthAcl entry = new AuthAcl(address, action);
        aclDestinationsLog.append(MessageFormat.format(PERMISSION_LOG,
                AclUtils.isRead(action) ? "r" : "_",
                AclUtils.isWrite(action) ? "w" : "_",
                AclUtils.isAdmin(action) ? "a" : "_",
                address));
        aclDestinationsLog.append("\n");
        return entry;
    }

    /**
     * Enforce the device connection/user bound (if enabled)<br>
     * <b>Utility method used by the connection logic</b>
     *
     * @param options
     * @param deviceConnection
     * @param scopeId
     * @param userId
     * @throws KapuaException
     */
    protected void enforceDeviceConnectionUserBound(Map<String, Object> options, DeviceConnection deviceConnection, KapuaId scopeId, KapuaId userId) throws KapuaException {
        if (deviceConnection != null) {
            ConnectionUserCouplingMode connectionUserCouplingMode = deviceConnection.getUserCouplingMode();
            if (ConnectionUserCouplingMode.INHERITED.equals(deviceConnection.getUserCouplingMode())) {
                connectionUserCouplingMode = loadConnectionUserCouplingModeFromConfig(options);
            }
            enforceDeviceUserBound(connectionUserCouplingMode, deviceConnection, scopeId, userId);
        } else {
            logger.debug("Enforce Device-User bound - no device connection found so user account settings for enforcing the bound (user id - '{}')", userId);
            enforceDeviceUserBound(loadConnectionUserCouplingModeFromConfig(options), null, scopeId, userId);
        }
    }

    /**
     * Enforce the device connection/user bound (if enabled)<br>
     * <b>Utility method used by the connection logic</b>
     *
     * @param connectionUserCouplingMode
     * @param deviceConnection
     * @param scopeId
     * @param userId
     * @throws KapuaException
     */
    protected void enforceDeviceUserBound(ConnectionUserCouplingMode connectionUserCouplingMode, DeviceConnection deviceConnection, KapuaId scopeId, KapuaId userId)
            throws KapuaException {
        if (ConnectionUserCouplingMode.STRICT.equals(connectionUserCouplingMode)) {
            if (deviceConnection == null) {
                checkConnectionCountByReservedUserId(scopeId, userId, 0);
            } else {
                if (deviceConnection.getReservedUserId() == null) {
                    checkConnectionCountByReservedUserId(scopeId, userId, 0);
                    if (!deviceConnection.getAllowUserChange() && !userId.equals(deviceConnection.getUserId())) {
                        throw new SecurityException(USER_NOT_AUTHORIZED + " DeviceConnection cannot change the user to connect!");
                        // TODO manage the error message. is it better to throw a more specific exception or keep it obfuscated for security reason?
                    }
                } else {
                    checkConnectionCountByReservedUserId(scopeId, deviceConnection.getReservedUserId(), 1);
                    if (!userId.equals(deviceConnection.getReservedUserId())) {
                        throw new SecurityException(USER_NOT_AUTHORIZED + " DeviceConnection must use the Reserved User assigned to connect!");
                        // TODO manage the error message. is it better to throw a more specific exception or keep it obfuscated for security reason?
                    }
                }
            }
        } else {
            if (deviceConnection != null && deviceConnection.getReservedUserId() != null) {
                if (userId.equals(deviceConnection.getReservedUserId())) {
                    checkConnectionCountByReservedUserId(scopeId, userId, 1);
                }
                else {
                    throw new SecurityException(USER_NOT_AUTHORIZED + " DeviceConnection cannot use User different from reserved User!");
                }
            } else {
                checkConnectionCountByReservedUserId(scopeId, userId, 0);
            }
        }
    }

    /**
     * Check the connection count for a specific reserved user id<br>
     * <b>Utility method used by the connection logic</b>
     *
     * @param scopeId
     * @param userId
     * @param count
     * @throws KapuaException
     */
    protected void checkConnectionCountByReservedUserId(KapuaId scopeId, KapuaId userId, long count) throws KapuaException {
        // check that no devices have this user as strict user
        final KapuaQuery query = new KapuaQuery(scopeId);
        query.setPredicate(query.attributePredicate(DeviceConnectionOptionAttributes.RESERVED_USER_ID, userId));
        query.setLimit(1);

        Long connectionCountByReservedUserId = KapuaSecurityUtils.doPrivileged(() -> deviceConnectionOptionService.count(query));
        if (connectionCountByReservedUserId != null && connectionCountByReservedUserId > count) {
            throw new SecurityException(USER_NOT_AUTHORIZED + " DeviceConnection cannot use this user because its reserved for another DeviceConnection");
            // TODO manage the error message. is it better to throw a more specific exception or keep it obfuscated for security reason?
        }
    }

    /**
     * Load the device connection/user coupling mode<br>
     * <b>Utility method used by the connection logic</b>
     *
     * @param options
     * @return
     */
    protected ConnectionUserCouplingMode loadConnectionUserCouplingModeFromConfig(Map<String, Object> options) {
        String tmp = (String) options.get("deviceConnectionUserCouplingDefaultMode");// TODO move to constants
        if (tmp != null) {
            ConnectionUserCouplingMode tmpConnectionUserCouplingMode = ConnectionUserCouplingMode.valueOf(tmp);
            if (tmpConnectionUserCouplingMode == null) {
                throw new SecurityException(String
                        .format("Cannot parse the default Device-User coupling mode in the registry service configuration! (found '%s' - allowed values are 'LOOSE' - 'STRICT')", tmp));
                // TODO manage the error message. is it better to throw a more specific exception or keep it obfuscated for security reason?
            } else {
                return tmpConnectionUserCouplingMode;
            }
        } else {
            throw new SecurityException("Cannot find default Device-User coupling mode in the registry service configuration! (deviceConnectionUserCouplingDefaultMode");
            // TODO manage the error message. is it better to throw a more specific exception or keep it obfuscated for security reason?
        }
    }

    /**
     * Creates a new {@link DeviceConnection} using the info provided.
     *
     * @param authContext
     * @return The created {@link DeviceConnection}
     * @throws KapuaException
     */
    protected DeviceConnection createDeviceConnection(AuthContext authContext) throws KapuaException {
        // TODO manage the stealing link event (may be a good idea to use different connect status (connect -stealing)?
        DeviceConnectionCreator deviceConnectionCreator = new DeviceConnectionCreator(KapuaEid.parseCompactId(authContext.getScopeId()));
        deviceConnectionCreator.setStatus(DeviceConnectionStatus.CONNECTED);
        deviceConnectionCreator.setClientId(authContext.getClientId());
        deviceConnectionCreator.setClientIp(authContext.getClientIp());
        deviceConnectionCreator.setProtocol(authContext.getTransportProtocol());
        deviceConnectionCreator.setServerIp(authContext.getBrokerHost());
        deviceConnectionCreator.setUserId(KapuaEid.parseCompactId(authContext.getUserId()));
        deviceConnectionCreator.setUserCouplingMode(ConnectionUserCouplingMode.INHERITED);
        deviceConnectionCreator.setAllowUserChange(false);
        deviceConnectionCreator.setAuthenticationType(authContext.getAuthenticationType());
        deviceConnectionCreator.setLastAuthenticationType(authContext.getAuthenticationType());
        return KapuaSecurityUtils.doPrivileged(() -> deviceConnectionService.create(deviceConnectionCreator));
    }

    /**
     * Updates a {@link DeviceConnection} using the info provided.
     *
     * @param authContext
     * @param deviceConnection
     *         The {@link DeviceConnection} to update, or null if it needs to be created
     * @return The updated {@link DeviceConnection}
     * @throws KapuaException
     */
    protected DeviceConnection updateDeviceConnection(AuthContext authContext, DeviceConnection deviceConnection) throws KapuaException {
        // TODO manage the stealing link event (may be a good idea to use different connect status (connect -stealing)?
        deviceConnection.setStatus(DeviceConnectionStatus.CONNECTED);
        deviceConnection.setClientIp(authContext.getClientIp());
        deviceConnection.setProtocol(authContext.getTransportProtocol());
        deviceConnection.setServerIp(authContext.getBrokerHost());
        deviceConnection.setUserId(KapuaEid.parseCompactId(authContext.getUserId()));
        deviceConnection.setAllowUserChange(false);
        deviceConnection.setAuthenticationType(authContext.getAuthenticationType());
        deviceConnection.setLastAuthenticationType(authContext.getAuthenticationType());
        // TODO implement the banned status
        // if (DeviceStatus.DISABLED.equals(device.getStatus())) {
        // throw new KapuaIllegalAccessException("clientId - This client ID is disabled and cannot connect");
        // }
        return KapuaSecurityUtils.doPrivileged(() -> deviceConnectionService.update(deviceConnection));
    }

    protected DeviceConnection getDeviceConnection(AuthContext authContext) {
        try {
            return KapuaSecurityUtils.doPrivileged(() -> deviceConnectionService.findByClientId(
                    KapuaEid.parseCompactId(authContext.getScopeId()), authContext.getClientId()));
        } catch (Exception e) {
            throw new ShiroException("Error while looking for device connection on updating the device status!", e);
        }
    }

    protected boolean isDeviceOwnedByTheCurrentNode(AuthContext authContext, DeviceConnection deviceConnection) {
        boolean ownedByTheCurrentNode = false;
        if (authContext.getBrokerHost() == null) {
            logger.warn("Broker Ip or host name is not correctly set! Please check the configuration!");
            authenticationMetric.getFailure().getBrokerHostFailure().inc();
        } else if (deviceConnection == null) {
            logger.warn("Cannot find device connection for device: {}/{}", authContext.getScopeId(), authContext.getClientId());
            authenticationMetric.getFailure().getFindDeviceConnectionFailure().inc();
        } else {
            ownedByTheCurrentNode = authContext.getBrokerHost().equals(deviceConnection.getServerIp());
        }
        return ownedByTheCurrentNode;
    }

}
