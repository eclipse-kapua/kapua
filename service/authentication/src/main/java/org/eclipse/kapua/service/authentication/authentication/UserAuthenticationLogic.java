/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.ShiroException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.client.security.bean.AuthAcl;
import org.eclipse.kapua.client.security.bean.AuthContext;
import org.eclipse.kapua.client.security.metric.AuthMetric;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionFactory;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionService;

import com.codahale.metrics.Timer.Context;

/**
 * User profile authentication logic implementation
 *
 * @since 1.0
 */
public class UserAuthenticationLogic extends AuthenticationLogic {

    public UserAuthenticationLogic(
            AclCreator aclCreator,
            AuthMetric authenticationMetric,
            DeviceConnectionOptionFactory deviceConnectionOptionFactory,
            DeviceConnectionOptionService deviceConnectionOptionService,
            AuthorizationService authorizationService,
            DeviceConnectionFactory deviceConnectionFactory,
            DeviceConnectionService deviceConnectionService) {
        super(aclCreator, authenticationMetric, deviceConnectionOptionFactory, deviceConnectionOptionService, authorizationService, deviceConnectionFactory, deviceConnectionService);
    }

    @Override
    public List<AuthAcl> connect(AuthContext authContext) throws KapuaException {
        Context timeUserTotal = authenticationMetric.getExtConnectorTime().getUserAddConnection().time();
        Context timeUserTotalCheckAccess = authenticationMetric.getExtConnectorTime().getUserCheckAccess().time();
        UserPermissions userPermissions = updatePermissions(authContext);
        timeUserTotalCheckAccess.stop();

        Context timeUserTotalFindDevice = authenticationMetric.getExtConnectorTime().getUserFindDevice().time();
        DeviceConnection deviceConnection = KapuaSecurityUtils.doPrivileged(() -> deviceConnectionService.findByClientId(
                KapuaEid.parseCompactId(authContext.getScopeId()), authContext.getClientId()));
        timeUserTotalFindDevice.stop();

        // enforce the user-device bound
        final KapuaId scopeId = KapuaEid.parseCompactId(authContext.getScopeId());
        final KapuaId userId = KapuaEid.parseCompactId(authContext.getUserId());
        enforceDeviceConnectionUserBound(KapuaSecurityUtils.doPrivileged(() -> deviceConnectionService.getConfigValues(scopeId)), deviceConnection, scopeId, userId);

        Context timeUserTotalUpdateDevice = authenticationMetric.getExtConnectorTime().getUserUpdateDevice().time();
        deviceConnection = deviceConnection != null ? updateDeviceConnection(authContext, deviceConnection) : createDeviceConnection(authContext);
        if (deviceConnection != null && deviceConnection.getId() != null) {
            authContext.setKapuaConnectionId(deviceConnection.getId());
        }
        timeUserTotalUpdateDevice.stop();

        List<AuthAcl> authorizationEntries = buildAuthorizationMap(userPermissions, authContext);
        timeUserTotal.stop();

        return authorizationEntries;
    }

    @Override
    public boolean disconnect(AuthContext authContext) {
        Context timeTotal = authenticationMetric.getExtConnectorTime().getUserRemoveConnection().time();
        boolean deviceOwnedByTheCurrentNode = true;
        if (!authContext.isStealingLink() && !authContext.isIllegalState()) {
            // update device connection (if the disconnection wasn't caused by a stealing link)
            DeviceConnection deviceConnection = getDeviceConnection(authContext);
            deviceOwnedByTheCurrentNode = isDeviceOwnedByTheCurrentNode(authContext, deviceConnection);
            if (deviceOwnedByTheCurrentNode) {
                //update status only if the old status wasn't missing
                if (DeviceConnectionStatus.MISSING.equals(deviceConnection.getStatus())) {
                    logger.warn("Skipping device status update for device {} since last status was MISSING!", deviceConnection.getClientId());
                } else {
                    deviceConnection.setStatus(!authContext.isMissing() ? DeviceConnectionStatus.DISCONNECTED : DeviceConnectionStatus.MISSING);
                    try {
                        KapuaSecurityUtils.doPrivileged(() -> deviceConnectionService.update(deviceConnection));
                    } catch (Exception e) {
                        throw new ShiroException("Error while updating the device connection status!", e);
                    }
                }
            }
        }
        timeTotal.stop();
        return !authContext.isStealingLink() && deviceOwnedByTheCurrentNode && !authContext.isMissing();
    }

    @Override
    protected List<AuthAcl> buildAuthorizationMap(UserPermissions userPermissions, AuthContext authContext) {
        StringBuilder aclDestinationsLog = new StringBuilder();
        List<AuthAcl> ael = aclCreator.buildAcls(userPermissions.hasPermissions, authContext.getAccountName(), authContext.getClientId(), aclDestinationsLog);
        logger.info("User ACLs: {}", aclDestinationsLog);
        return ael;
    }

    protected UserPermissions updatePermissions(AuthContext authContext) throws KapuaException {
        List<Permission> permissions = new ArrayList<>();
        KapuaId scopeId = KapuaEid.parseCompactId(authContext.getScopeId());
        permissions.add(new Permission(Domains.BROKER, Actions.connect, scopeId));
        permissions.add(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        permissions.add(new Permission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        permissions.add(new Permission(Domains.DATASTORE, Actions.read, scopeId));
        permissions.add(new Permission(Domains.DATASTORE, Actions.write, scopeId));
        UserPermissions userPermissions = new UserPermissions(authorizationService.isPermitted(permissions));

        if (!userPermissions.isBrokerConnect()) {
            throw new KapuaIllegalAccessException(new Permission(Domains.BROKER, Actions.connect, scopeId).toString());
        }
        return userPermissions;
    }

}
