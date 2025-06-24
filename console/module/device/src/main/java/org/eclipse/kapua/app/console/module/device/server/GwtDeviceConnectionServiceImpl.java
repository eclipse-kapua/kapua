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
package org.eclipse.kapua.app.console.module.device.server;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.server.util.UserCreatedByModifiedByUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection.GwtConnectionUserCouplingMode;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionQuery;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceConnectionService;
import org.eclipse.kapua.app.console.module.device.shared.util.GwtKapuaDeviceModelConverter;
import org.eclipse.kapua.app.console.module.device.shared.util.KapuaGwtDeviceModelConverter;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * The server side implementation of the RPC service.
 */
public class GwtDeviceConnectionServiceImpl extends KapuaRemoteServiceServlet implements GwtDeviceConnectionService {
    private static final long serialVersionUID = 3314502846487119577L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final DeviceConnectionService DEVICE_CONNECTION_SERVICE = LOCATOR.getService(DeviceConnectionService.class);
    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);

    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);
    private static final String CONNECTION_INFO = "connectionInfo";
    private static final String CONNECTION_USER_COUPLING_MODE_INFO = "connectionUserCouplingModeInfo";

    @Override
    public PagingLoadResult<GwtDeviceConnection> query(PagingLoadConfig loadConfig, GwtDeviceConnectionQuery gwtDeviceConnectionQuery) throws GwtKapuaException {
        try {
            DeviceConnectionQuery query = GwtKapuaDeviceModelConverter.convertConnectionQuery(loadConfig, gwtDeviceConnectionQuery);

            final KapuaListResult<DeviceConnection> deviceConnections = DEVICE_CONNECTION_SERVICE.query(query);

            List<GwtDeviceConnection> gwtDeviceConnections = new ArrayList<GwtDeviceConnection>();
            if (!deviceConnections.isEmpty()) {

                Map<KapuaId, String> idUsernameMap = UserCreatedByModifiedByUtils.resolveFromListResultAndFields(
                        deviceConnections,
                        new Callable<Set<KapuaId>>() {
                            @Override
                            public Set<KapuaId> call() {
                                Set<KapuaId> additionalKapuaIds = new HashSet<KapuaId>();
                                for (DeviceConnection deviceConnection : deviceConnections.getItems()) {
                                    if (deviceConnection.getUserId() != null) {
                                        additionalKapuaIds.add(deviceConnection.getUserId());
                                    }
                                    if (deviceConnection.getReservedUserId() != null) {
                                        additionalKapuaIds.add(deviceConnection.getReservedUserId());
                                    }
                                }

                                return additionalKapuaIds;
                            }
                        });

                for (DeviceConnection deviceConnection : deviceConnections.getItems()) {
                    GwtDeviceConnection gwtDeviceConnection = KapuaGwtDeviceModelConverter.convertDeviceConnection(deviceConnection);

                    gwtDeviceConnection.setCreatedByName(idUsernameMap.get(deviceConnection.getCreatedBy()));
                    gwtDeviceConnection.setModifiedBy(idUsernameMap.get(deviceConnection.getModifiedBy()));

                    if (deviceConnection.getUserId() != null) {
                        gwtDeviceConnection.setUserName(idUsernameMap.get(deviceConnection.getUserId()));
                    }

                    if (deviceConnection.getReservedUserId() != null) {
                        gwtDeviceConnection.setReservedUserName(idUsernameMap.get(deviceConnection.getReservedUserId()));
                    }

                    gwtDeviceConnections.add(gwtDeviceConnection);
                }
            }

            return new BasePagingLoadResult<GwtDeviceConnection>(gwtDeviceConnections, loadConfig.getOffset(), deviceConnections.getTotalCount().intValue());
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public GwtDeviceConnection find(String scopeIdString, String deviceConnectionIdString) throws GwtKapuaException {
        KapuaId deviceConnectionId = KapuaEid.parseCompactId(deviceConnectionIdString);
        KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);

        GwtDeviceConnection gwtDeviceConnection = null;
        try {

            gwtDeviceConnection = KapuaGwtDeviceModelConverter.convertDeviceConnection(DEVICE_CONNECTION_SERVICE.find(scopeId, deviceConnectionId));
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtDeviceConnection;
    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> getConnectionInfo(String scopeIdString, String gwtDeviceConnectionId)
            throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceConnectionId = KapuaEid.parseCompactId(gwtDeviceConnectionId);

            DeviceConnection deviceConnection = DEVICE_CONNECTION_SERVICE.find(scopeId, deviceConnectionId);

            List<GwtGroupedNVPair> deviceConnectionPropertiesPairs = new ArrayList<GwtGroupedNVPair>();
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionStatus", deviceConnection.getStatus().toString()));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionModifiedOn", deviceConnection.getModifiedOn()));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionModifiedBy", UserCreatedByModifiedByUtils.resolveFromId(deviceConnection.getModifiedBy())));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionProtocol", deviceConnection.getProtocol()));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionClientId", deviceConnection.getClientId()));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionUser", UserCreatedByModifiedByUtils.resolveFromId(deviceConnection.getUserId())));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionClientIp", deviceConnection.getClientIp()));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionServerIp", deviceConnection.getServerIp()));
            GwtConnectionUserCouplingMode gwtConnectionUserCouplingMode = null;
            if (deviceConnection.getUserCouplingMode() != null) {
                gwtConnectionUserCouplingMode = GwtConnectionUserCouplingMode.valueOf(deviceConnection.getUserCouplingMode().name());
            }
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_USER_COUPLING_MODE_INFO, "connectionUserCouplingMode", gwtConnectionUserCouplingMode != null ? gwtConnectionUserCouplingMode.getLabel() : null));

            if (AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.USER, Actions.read, scopeId))) {
                deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_USER_COUPLING_MODE_INFO, "connectionReservedUser", deviceConnection.getReservedUserId() != null ? UserCreatedByModifiedByUtils.resolveFromId(deviceConnection.getReservedUserId()) : null));
                deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_USER_COUPLING_MODE_INFO, "allowUserChange", deviceConnection.getAllowUserChange()));
            }

            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionAuthenticationType", deviceConnection.getAuthenticationType()));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionLastAuthenticationType", deviceConnection.getLastAuthenticationType()));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionFirstEstablishedOn", deviceConnection.getCreatedOn()));
            deviceConnectionPropertiesPairs.add(new GwtGroupedNVPair(CONNECTION_INFO, "connectionFirstEstablishedBy", UserCreatedByModifiedByUtils.resolveFromId(deviceConnection.getCreatedBy())));

            return new BaseListLoadResult<GwtGroupedNVPair>(deviceConnectionPropertiesPairs);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public void disconnect(GwtXSRFToken xsrfToken, String scopeIdString, String deviceConnectionIdString) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceConnectionId = KapuaEid.parseCompactId(deviceConnectionIdString);

            DEVICE_CONNECTION_SERVICE.disconnect(scopeId, deviceConnectionId);
        }
        catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }
}
