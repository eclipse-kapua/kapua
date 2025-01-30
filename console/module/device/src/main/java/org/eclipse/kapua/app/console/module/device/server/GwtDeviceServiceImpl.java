/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSetting;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSettingKeys;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceCreator;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQuery;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection.GwtConnectionUserCouplingMode;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionStatus;
import org.eclipse.kapua.app.console.module.device.shared.model.event.GwtDeviceEvent;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceService;
import org.eclipse.kapua.app.console.module.device.shared.util.GwtKapuaDeviceModelConverter;
import org.eclipse.kapua.app.console.module.device.shared.util.KapuaGwtDeviceModelConverter;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceExtendedProperty;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceStatus;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventAttributes;
import org.eclipse.kapua.service.device.registry.event.DeviceEventQuery;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * The server side implementation of the Device RPC service.
 */
public class GwtDeviceServiceImpl extends KapuaRemoteServiceServlet implements GwtDeviceService {

    private static final long serialVersionUID = -1391026997499175151L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);
    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);

    private boolean isSameId;

    private static final String DEV_INFO = "devInfo";
    private static final String CONN_INFO = "connInfo";
    private static final String NET_INFO = "netInfo";
    private static final String DEV_LAST_EVENT_TYPE = "devLastEventType";
    private static final String DEV_LAST_EVENT_ON = "devLastEventOn";
    private static final String DEV_ATTRIBUTES_INFO = "devAttributesInfo";
    private static final String DEV_HW = "devHw";
    private static final String DEV_SW = "devSw";
    private static final String GPS_INFO = "gpsInfo";
    private static final String MODEM_INFO = "modemInfo";

    @Override
    public GwtDevice findDevice(String scopeIdString, String deviceIdString)
            throws GwtKapuaException {
        GwtDevice gwtDevice = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
            Device device = deviceRegistryService.find(scopeId, deviceId);

            gwtDevice = KapuaGwtDeviceModelConverter.convertDevice(device);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return gwtDevice;
    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> findDeviceProfile(String scopeIdString, String deviceIdString)
            throws GwtKapuaException {
        List<GwtGroupedNVPair> pairs = new ArrayList<GwtGroupedNVPair>();
        KapuaLocator locator = KapuaLocator.getInstance();

        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        final DeviceConnectionService deviceConnectionService = locator.getService(DeviceConnectionService.class);
        GroupService groupService = locator.getService(GroupService.class);
        final UserService userService = locator.getService(UserService.class);

        try {

            final KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);
            final Device device = deviceRegistryService.find(scopeId, deviceId);

            if (device != null) {
                pairs.add(new GwtGroupedNVPair(DEV_INFO, "devStatus", device.getStatus().toString()));

                final DeviceConnection deviceConnection;
                if (device.getConnectionId() != null) {
                    if (device.getConnection() != null) {
                        deviceConnection = device.getConnection();
                    } else {
                        deviceConnection = KapuaSecurityUtils.doPrivileged(new Callable<DeviceConnection>() {

                            @Override
                            public DeviceConnection call() throws Exception {
                                return deviceConnectionService.find(device.getScopeId(), device.getConnectionId());
                            }
                        });
                    }
                } else {
                    deviceConnection = null;
                }
                if (deviceConnection != null) {
                    User lastConnectedUser = KapuaSecurityUtils.doPrivileged(new Callable<User>() {

                        @Override
                        public User call() throws Exception {
                            return userService.find(scopeId, deviceConnection.getUserId());
                        }
                    });
                    User reservedUser = null;
                    if (deviceConnection.getReservedUserId() != null) {
                        reservedUser = KapuaSecurityUtils.doPrivileged(new Callable<User>() {

                            @Override
                            public User call() throws Exception {
                                return userService.find(scopeId, deviceConnection.getReservedUserId());
                            }
                        });
                    }

                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connConnectionStatus", deviceConnection.getStatus().toString()));
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connClientId", device.getClientId()));
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connUserName", lastConnectedUser != null ? lastConnectedUser.getName() : null));
                    if (AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.USER, Actions.read, scopeId))) {
                        pairs.add(new GwtGroupedNVPair(CONN_INFO, "connReservedUserId", reservedUser != null ? reservedUser.getName() : null));
                    }
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connUserCouplingMode", GwtConnectionUserCouplingMode.valueOf(deviceConnection.getUserCouplingMode().name()).getLabel()));
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connClientIp", deviceConnection.getClientIp()));
                    pairs.add(new GwtGroupedNVPair(NET_INFO, "netConnIface", device.getConnectionInterface()));
                    pairs.add(new GwtGroupedNVPair(NET_INFO, "netConnIp", deviceConnection.getClientIp()));
                    pairs.add(new GwtGroupedNVPair(NET_INFO, "netConnIfaceIp", device.getConnectionIp()));
                    pairs.add(new GwtGroupedNVPair(DEV_INFO, "devConnectionStatus", deviceConnection.getStatus().toString()));

                } else {
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connConnectionStatus", DeviceConnectionStatus.DISCONNECTED.toString()));
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connClientId", null));
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connUserName", null));
                    if (AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.USER, Actions.read, scopeId))) {
                        pairs.add(new GwtGroupedNVPair(CONN_INFO, "connReservedUserId", null));
                    }
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connUserCouplingMode", null));
                    pairs.add(new GwtGroupedNVPair(CONN_INFO, "connClientIp", null));
                    pairs.add(new GwtGroupedNVPair(NET_INFO, "netConnIface", null));
                    pairs.add(new GwtGroupedNVPair(NET_INFO, "netConnIp", null));
                    pairs.add(new GwtGroupedNVPair(NET_INFO, "netConnIfaceIp", null));
                    pairs.add(new GwtGroupedNVPair(DEV_INFO, "devConnectionStatus", DeviceConnectionStatus.DISCONNECTED.toString()));
                }

                pairs.add(new GwtGroupedNVPair(DEV_INFO, "devClientId", device.getClientId()));
                pairs.add(new GwtGroupedNVPair(DEV_INFO, "devDisplayName", device.getDisplayName()));

                if (AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.GROUP, Actions.read, device.getScopeId()))) {
                    if (device.getGroupId() != null) {

                        Group group = groupService.find(scopeId, device.getGroupId());
                        if (group != null) {
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, "devGroupName", group.getName()));
                        }
                    } else {
                        pairs.add(new GwtGroupedNVPair(DEV_INFO, "devGroupName", null));
                    }
                }

                if (AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.DEVICE_EVENT, Actions.read, device.getScopeId()))) {
                    if (device.getLastEventId() != null) {
                        DeviceEvent lastEvent = deviceEventService.find(scopeId, device.getLastEventId());

                        if (lastEvent != null) {
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, DEV_LAST_EVENT_TYPE, lastEvent.getResource()));
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, DEV_LAST_EVENT_ON, lastEvent.getReceivedOn()));
                        } else {
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, DEV_LAST_EVENT_TYPE, null));
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, DEV_LAST_EVENT_ON, null));
                        }
                    } else {
                        if (deviceConnection != null) {
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, DEV_LAST_EVENT_TYPE, deviceConnection.getStatus().name()));
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, DEV_LAST_EVENT_ON, deviceConnection.getModifiedOn()));
                        } else {
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, DEV_LAST_EVENT_TYPE, null));
                            pairs.add(new GwtGroupedNVPair(DEV_INFO, DEV_LAST_EVENT_ON, null));
                        }
                    }
                }

                pairs.add(new GwtGroupedNVPair(DEV_INFO, "devApps", device.getApplicationIdentifiers()));
                pairs.add(new GwtGroupedNVPair(DEV_INFO, "devAccEnc", device.getAcceptEncoding()));

                pairs.add(new GwtGroupedNVPair(DEV_ATTRIBUTES_INFO, "devCustomAttribute1", device.getCustomAttribute1()));
                pairs.add(new GwtGroupedNVPair(DEV_ATTRIBUTES_INFO, "devCustomAttribute2", device.getCustomAttribute2()));
                pairs.add(new GwtGroupedNVPair(DEV_ATTRIBUTES_INFO, "devCustomAttribute3", device.getCustomAttribute3()));
                pairs.add(new GwtGroupedNVPair(DEV_ATTRIBUTES_INFO, "devCustomAttribute4", device.getCustomAttribute4()));
                pairs.add(new GwtGroupedNVPair(DEV_ATTRIBUTES_INFO, "devCustomAttribute5", device.getCustomAttribute5()));

                pairs.add(new GwtGroupedNVPair(DEV_HW, "devModelId", device.getModelId()));
                pairs.add(new GwtGroupedNVPair(DEV_HW, "devModelName", device.getModelName()));
                pairs.add(new GwtGroupedNVPair(DEV_HW, "devSerialNumber", device.getSerialNumber()));
                pairs.add(new GwtGroupedNVPair(DEV_HW, "devTamperStatus", device.getTamperStatus()));

                pairs.add(new GwtGroupedNVPair(DEV_SW, "devFirmwareVersion", device.getFirmwareVersion()));
                pairs.add(new GwtGroupedNVPair(DEV_SW, "devBiosVersion", device.getBiosVersion()));
                pairs.add(new GwtGroupedNVPair(DEV_SW, "devOsVersion", device.getOsVersion()));

                pairs.add(new GwtGroupedNVPair("devJava", "devJvmVersion", device.getJvmVersion()));

                // GPS infos retrieval
                if (AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.DEVICE_EVENT, Actions.read, device.getScopeId()))) {
                    DeviceEventQuery query = new DeviceEventQuery(device.getScopeId());
                    query.setLimit(1);
                    query.setSortCriteria(query.fieldSortCriteria(DeviceEventAttributes.RECEIVED_ON, SortOrder.DESCENDING));

                    AndPredicate andPredicate = query.andPredicate(
                            query.attributePredicate(DeviceEventAttributes.DEVICE_ID, device.getId()),
                            query.attributePredicate(DeviceEventAttributes.RESOURCE, "BIRTH")
                    );

                    query.setPredicate(andPredicate);

                    KapuaListResult<DeviceEvent> events = deviceEventService.query(query);
                    DeviceEvent lastEvent = events.getFirstItem();
                    if (lastEvent != null) {
                        KapuaPosition eventPosition = lastEvent.getPosition();
                        if (eventPosition != null) {
                            pairs.add(new GwtGroupedNVPair(GPS_INFO, "gpsLat", String.valueOf(eventPosition.getLatitude())));
                            pairs.add(new GwtGroupedNVPair(GPS_INFO, "gpsLong", String.valueOf(eventPosition.getLongitude())));
                        }
                    } else {
                        pairs.add(new GwtGroupedNVPair(GPS_INFO, "gpsLat", null));
                        pairs.add(new GwtGroupedNVPair(GPS_INFO, "gpsLong", null));
                    }
                }

                pairs.add(new GwtGroupedNVPair(MODEM_INFO, "modemImei", device.getImei()));
                pairs.add(new GwtGroupedNVPair(MODEM_INFO, "modemImsi", device.getImsi()));
                pairs.add(new GwtGroupedNVPair(MODEM_INFO, "modemIccid", device.getIccid()));
                // Extended Properties
                for (DeviceExtendedProperty deviceExtendedProperty : device.getExtendedProperties()) {
                    pairs.add(
                            new GwtGroupedNVPair(
                                    deviceExtendedProperty.getGroupName(),
                                    deviceExtendedProperty.getName(),
                                    deviceExtendedProperty.getValue()
                            )
                    );
                }
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return new BaseListLoadResult<GwtGroupedNVPair>(pairs);
    }

    @Override
    public PagingLoadResult<GwtDevice> query(PagingLoadConfig loadConfig, GwtDeviceQuery gwtDeviceQuery)
            throws GwtKapuaException {
        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);

        List<GwtDevice> gwtDevices = new ArrayList<GwtDevice>();
        BasePagingLoadResult<GwtDevice> gwtResults;
        int totalResult = 0;
        try {
            DeviceQuery deviceQuery = GwtKapuaDeviceModelConverter.convertDeviceQuery(loadConfig, gwtDeviceQuery);
            deviceQuery.addFetchAttributes(DeviceAttributes.CONNECTION);
            deviceQuery.addFetchAttributes(DeviceAttributes.LAST_EVENT);

            KapuaListResult<Device> devices = deviceRegistryService.query(deviceQuery);
            totalResult = devices.getTotalCount().intValue();
            for (Device d : devices.getItems()) {
                GwtDevice gwtDevice = KapuaGwtDeviceModelConverter.convertDevice(d);

                // Connection info

                gwtDevice.setConnectionIp(d.getConnectionIp());
                gwtDevice.setConnectionInterface(d.getConnectionInterface());

                DeviceConnection deviceConnection = d.getConnection();
                if (deviceConnection != null) {
                    gwtDevice.setClientIp(deviceConnection.getClientIp());
                    gwtDevice.setGwtDeviceConnectionStatus(deviceConnection.getStatus().name());
                    gwtDevice.setLastEventOn(deviceConnection.getModifiedOn());
                    gwtDevice.setLastEventType(deviceConnection.getStatus().name());
                } else {
                    gwtDevice.setGwtDeviceConnectionStatus(GwtDeviceConnectionStatus.UNKNOWN.name());
                }

                if (d.getLastEvent() != null) {
                    DeviceEvent lastEvent = d.getLastEvent();

                    gwtDevice.setLastEventType(lastEvent.getResource());
                    gwtDevice.setLastEventOn(lastEvent.getReceivedOn());
                }

                gwtDevices.add(gwtDevice);
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        gwtResults = new BasePagingLoadResult<GwtDevice>(gwtDevices);
        gwtResults.setOffset(loadConfig != null ? loadConfig.getOffset() : 0);
        gwtResults.setTotalLength(totalResult);

        return gwtResults;
    }

    @Override
    public List<GwtDevice> query(GwtDeviceQuery gwtDeviceQuery) throws GwtKapuaException {
        return query(null, gwtDeviceQuery).getData();
    }

    @Override
    public GwtDevice createDevice(GwtXSRFToken xsrfToken, GwtDeviceCreator gwtDeviceCreator)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        GwtDevice gwtDevice = null;

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtDeviceCreator.getScopeId());

            DeviceCreator deviceCreator = new DeviceCreator(scopeId, gwtDeviceCreator.getClientId());
            deviceCreator.setDisplayName(gwtDeviceCreator.getDisplayName());
            deviceCreator.setGroupId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtDeviceCreator.getGroupId()));
            deviceCreator.setStatus((DeviceStatus.valueOf(gwtDeviceCreator.getDeviceStatus())));

            // FIXME One day it will be specified from the form. In the meantime, defaults to LOOSE
            // deviceCreator.setCredentialsMode(DeviceCredentialsMode.LOOSE);

            deviceCreator.setCustomAttribute1(gwtDeviceCreator.getCustomAttribute1());
            deviceCreator.setCustomAttribute2(gwtDeviceCreator.getCustomAttribute2());
            deviceCreator.setCustomAttribute3(gwtDeviceCreator.getCustomAttribute3());
            deviceCreator.setCustomAttribute4(gwtDeviceCreator.getCustomAttribute4());
            deviceCreator.setCustomAttribute5(gwtDeviceCreator.getCustomAttribute5());

            Device device = deviceRegistryService.create(deviceCreator);

            gwtDevice = KapuaGwtDeviceModelConverter.convertDevice(device);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtDevice;
    }

    @Override
    public GwtDevice updateAttributes(GwtXSRFToken xsrfToken, GwtDevice gwtDevice)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = null;
        GwtDevice gwtDeviceUpdated = null;

        try {
            // Find original device
            KapuaId scopeId = KapuaEid.parseCompactId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(gwtDevice.getId());
            device = deviceRegistryService.find(scopeId, deviceId);
            // Updated values
            // Gerenal info
            device.setDisplayName(gwtDevice.getUnescapedDisplayName());
            device.setStatus(DeviceStatus.valueOf(gwtDevice.getGwtDeviceStatus()));
            device.setGroupId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtDevice.getGroupId()));

            // Security Stuff
            // device.setCredentialsMode(DeviceCredentialsMode.valueOf(gwtDevice.getCredentialsTight()));
            // KapuaId deviceUserId = KapuaEid.parseCompactId(gwtDevice.getDeviceUserId());
            // device.setPreferredUserId(deviceUserId);

            // Custom attributes
            device.setCustomAttribute1(gwtDevice.getUnescapedCustomAttribute1());
            device.setCustomAttribute2(gwtDevice.getUnescapedCustomAttribute2());
            device.setCustomAttribute3(gwtDevice.getUnescapedCustomAttribute3());
            device.setCustomAttribute4(gwtDevice.getUnescapedCustomAttribute4());
            device.setCustomAttribute5(gwtDevice.getUnescapedCustomAttribute5());

            device.setOptlock(gwtDevice.getOptlock());

            // Do the update
            device = deviceRegistryService.update(device);

            // Convert to gwt object
            gwtDeviceUpdated = KapuaGwtDeviceModelConverter.convertDevice(device);

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return gwtDeviceUpdated;
    }

    @Override
    public void deleteDevice(GwtXSRFToken xsrfToken, String scopeIdString, String clientId)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceRegistryService drs = locator.getService(DeviceRegistryService.class);
            Device d = drs.findByClientId(scopeId, clientId);
            drs.delete(d.getScopeId(), d.getId());
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public void addDeviceTag(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, String tagIdString) throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);
            KapuaId tagId = KapuaEid.parseCompactId(tagIdString);

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceRegistryService drs = locator.getService(DeviceRegistryService.class);
            TagService tagService = locator.getService(TagService.class);
            Device device = drs.find(scopeId, deviceId);

            Set<KapuaId> tagIds = device.getTagIds();
            if (tagIds.contains(tagId)) {
                Tag tag = tagService.find(scopeId, tagId);
                isSameId = true;
                if (tag != null) {
                    throw new KapuaDuplicateNameException(tag.getName());
                }
            }
            tagIds.add(tagId);
            device.setTagIds(tagIds);

            drs.update(device);

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public void deleteDeviceTag(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, String tagIdString) throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);
            KapuaId tagId = KapuaEid.parseCompactId(tagIdString);

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceRegistryService drs = locator.getService(DeviceRegistryService.class);

            Device device = drs.find(scopeId, deviceId);

            Set<KapuaId> tagIds = device.getTagIds();
            tagIds.remove(tagId);
            device.setTagIds(tagIds);

            drs.update(device);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public PagingLoadResult<GwtDeviceEvent> findDeviceEvents(PagingLoadConfig loadConfig,
            GwtDevice gwtDevice,
            Date startDate,
            Date endDate)
            throws GwtKapuaException {
        ArrayList<GwtDeviceEvent> gwtDeviceEvents = new ArrayList<GwtDeviceEvent>();
        BasePagingLoadResult<GwtDeviceEvent> gwtResults = null;

        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceEventService des = locator.getService(DeviceEventService.class);

        try {

            // prepare the query
            BasePagingLoadConfig bplc = (BasePagingLoadConfig) loadConfig;
            DeviceEventQuery query = new DeviceEventQuery(KapuaEid.parseCompactId(gwtDevice.getScopeId()));

            AndPredicate andPredicate = query.andPredicate(
                    query.attributePredicate(DeviceEventAttributes.DEVICE_ID, KapuaEid.parseCompactId(gwtDevice.getId())),
                    query.attributePredicate(DeviceEventAttributes.RECEIVED_ON, startDate, Operator.GREATER_THAN),
                    query.attributePredicate(DeviceEventAttributes.RECEIVED_ON, endDate, Operator.LESS_THAN)
            );

            query.setPredicate(andPredicate);
            query.setSortCriteria(query.fieldSortCriteria(DeviceEventAttributes.RECEIVED_ON, SortOrder.DESCENDING));
            query.setOffset(bplc.getOffset());
            query.setLimit(bplc.getLimit());
            query.setAskTotalCount(true);
            // query execute
            KapuaListResult<DeviceEvent> deviceEvents = des.query(query);

            // prepare results
            for (DeviceEvent deviceEvent : deviceEvents.getItems()) {
                gwtDeviceEvents.add(KapuaGwtDeviceModelConverter.convertDeviceEvent(deviceEvent));
            }
            gwtResults = new BasePagingLoadResult<GwtDeviceEvent>(gwtDeviceEvents);
            gwtResults.setOffset(loadConfig.getOffset());
            gwtResults.setTotalLength(deviceEvents.getTotalCount().intValue());

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return gwtResults;
    }

    @Override
    public String getTileEndpoint() throws GwtKapuaException {
        return ConsoleSetting.getInstance().getString(ConsoleSettingKeys.DEVICE_MAP_TILE_URI);
    }

    @Override
    public boolean isMapEnabled() {
        return ConsoleSetting.getInstance().getBoolean(ConsoleSettingKeys.DEVICE_MAP_ENABLED);
    }

}
