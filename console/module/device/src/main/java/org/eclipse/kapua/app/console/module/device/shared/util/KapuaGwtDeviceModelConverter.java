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
package org.eclipse.kapua.app.console.module.device.shared.util;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.module.api.client.util.KapuaSafeHtmlUtils;
import org.eclipse.kapua.app.console.module.api.shared.util.KapuaGwtCommonsModelConverter;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates.GwtGroupDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection.GwtConnectionUserCouplingMode;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionOption;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionStatus;
import org.eclipse.kapua.app.console.module.device.shared.model.event.GwtDeviceEvent;
import org.eclipse.kapua.app.console.module.device.shared.model.management.assets.GwtDeviceAsset;
import org.eclipse.kapua.app.console.module.device.shared.model.management.assets.GwtDeviceAssetChannel;
import org.eclipse.kapua.app.console.module.device.shared.model.management.assets.GwtDeviceAssets;
import org.eclipse.kapua.app.console.module.device.shared.model.management.registry.GwtDeviceManagementOperation;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.type.ObjectTypeConverter;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.service.device.management.asset.DeviceAsset;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannel;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationProperty;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOption;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;

import java.util.ArrayList;
import java.util.List;

public class KapuaGwtDeviceModelConverter {

    private KapuaGwtDeviceModelConverter() {
    }

    public static GwtDevice convertDevice(Device device)
            throws KapuaException {

        GwtDevice gwtDevice = new GwtDevice();
        gwtDevice.setId(KapuaGwtCommonsModelConverter.convertKapuaId(device.getId()));
        gwtDevice.setScopeId(KapuaGwtCommonsModelConverter.convertKapuaId(device.getScopeId()));
        gwtDevice.setGwtDeviceStatus(device.getStatus().toString());
        gwtDevice.setClientId(device.getClientId());
        gwtDevice.setDisplayName(device.getDisplayName());
        gwtDevice.setModelId(device.getModelId());
        gwtDevice.setModelName(device.getModelName());
        gwtDevice.setSerialNumber(device.getSerialNumber());
        if (device.getGroupId() != null) {
            gwtDevice.setGroupDevice(GwtGroupDevice.NO_GROUP.name());
        } else {
            gwtDevice.setGroupDevice(GwtGroupDevice.ANY.name());
        }
        gwtDevice.setGroupId(KapuaGwtCommonsModelConverter.convertKapuaId(device.getGroupId()));
        gwtDevice.setFirmwareVersion(device.getFirmwareVersion());
        gwtDevice.setBiosVersion(device.getBiosVersion());
        gwtDevice.setOsVersion(device.getOsVersion());
        gwtDevice.setJvmVersion(device.getJvmVersion());
        gwtDevice.setOsgiVersion(device.getOsgiFrameworkVersion());
        gwtDevice.setAcceptEncoding(device.getAcceptEncoding());
        gwtDevice.setApplicationIdentifiers(device.getApplicationIdentifiers());
        gwtDevice.setIotFrameworkVersion(device.getApplicationFrameworkVersion());
        gwtDevice.setIccid(device.getIccid());
        gwtDevice.setImei(device.getImei());
        gwtDevice.setImsi(device.getImsi());
        gwtDevice.setCustomAttribute1(device.getCustomAttribute1());
        gwtDevice.setCustomAttribute2(device.getCustomAttribute2());
        gwtDevice.setCustomAttribute3(device.getCustomAttribute3());
        gwtDevice.setCustomAttribute4(device.getCustomAttribute4());
        gwtDevice.setCustomAttribute5(device.getCustomAttribute5());
        gwtDevice.setOptlock(device.getOptlock());

        // Tag Ids
        List<String> gwtTagIds = new ArrayList<String>();
        for (KapuaId tagId : device.getTagIds()) {
            gwtTagIds.add(KapuaGwtCommonsModelConverter.convertKapuaId(tagId));
        }
        gwtDevice.setTagIds(gwtTagIds);

        // Last device event
        if (device.getLastEvent() != null) {
            DeviceEvent lastEvent = device.getLastEvent();

            gwtDevice.setLastEventType(lastEvent.getType());
            gwtDevice.setLastEventOn(lastEvent.getReceivedOn());

        }

        // Device connection
        gwtDevice.setConnectionIp(device.getConnectionIp());
        gwtDevice.setConnectionInterface(device.getConnectionInterface());
        if (device.getConnection() != null) {
            DeviceConnection connection = device.getConnection();
            gwtDevice.setDeviceConnectionId(KapuaGwtCommonsModelConverter.convertKapuaId(connection.getId()));
            gwtDevice.setClientIp(connection.getClientIp());
            gwtDevice.setGwtDeviceConnectionStatus(connection.getStatus().toString());
            gwtDevice.setDeviceUserId(connection.getUserId().toCompactId());
        } else {
            gwtDevice.setGwtDeviceConnectionStatus(GwtDeviceConnectionStatus.UNKNOWN.name());
        }
        return gwtDevice;
    }

    public static GwtDeviceManagementOperation convertManagementOperation(DeviceManagementOperation deviceManagementOperation) {
        GwtDeviceManagementOperation gwtDeviceManagementOperation = new GwtDeviceManagementOperation();

        KapuaGwtCommonsModelConverter.convertUpdatableEntity(deviceManagementOperation, gwtDeviceManagementOperation);

        gwtDeviceManagementOperation.setStartedOn(deviceManagementOperation.getStartedOn());
        gwtDeviceManagementOperation.setEndedOn(deviceManagementOperation.getEndedOn());
        gwtDeviceManagementOperation.setResource(deviceManagementOperation.getResource());
        gwtDeviceManagementOperation.setDeviceId(KapuaGwtCommonsModelConverter.convertKapuaId(deviceManagementOperation.getDeviceId()));
        gwtDeviceManagementOperation.setOperationId(KapuaGwtCommonsModelConverter.convertKapuaId(deviceManagementOperation.getOperationId()));
        gwtDeviceManagementOperation.setAppId(deviceManagementOperation.getAppId());
        gwtDeviceManagementOperation.setActionType(deviceManagementOperation.getAction().name());
        gwtDeviceManagementOperation.setStatus(deviceManagementOperation.getStatus().name());
        gwtDeviceManagementOperation.setLog(deviceManagementOperation.getLog());

        for (DeviceManagementOperationProperty dmop : deviceManagementOperation.getInputProperties()) {
            gwtDeviceManagementOperation.addInputProperty(dmop.getName(), dmop.getPropertyValue());
        }

        return gwtDeviceManagementOperation;
    }

    public static GwtDeviceEvent convertDeviceEvent(DeviceEvent deviceEvent) {
        GwtDeviceEvent gwtDeviceEvent = new GwtDeviceEvent();
        gwtDeviceEvent.setDeviceId(deviceEvent.getDeviceId().toCompactId());
        gwtDeviceEvent.setSentOn(deviceEvent.getSentOn());
        gwtDeviceEvent.setReceivedOn(deviceEvent.getReceivedOn());
        gwtDeviceEvent.setEventType(deviceEvent.getResource());
        gwtDeviceEvent.setGwtActionType(deviceEvent.getAction().toString());
        gwtDeviceEvent.setGwtResponseCode(deviceEvent.getResponseCode().toString());

        String escapedMessage = KapuaSafeHtmlUtils.htmlEscape(deviceEvent.getEventMessage());
        gwtDeviceEvent.setEventMessage(escapedMessage);

        return gwtDeviceEvent;
    }

    public static GwtDeviceConnection convertDeviceConnection(DeviceConnection deviceConnection) {
        GwtDeviceConnection gwtDeviceConnection = new GwtDeviceConnection();
        // Convert commons attributes
        KapuaGwtCommonsModelConverter.convertUpdatableEntity(deviceConnection, gwtDeviceConnection);
        // Convert other attributes
        gwtDeviceConnection.setClientId(deviceConnection.getClientId());
        gwtDeviceConnection.setUserId(KapuaGwtCommonsModelConverter.convertKapuaId(deviceConnection.getUserId()));
        gwtDeviceConnection.setClientIp(deviceConnection.getClientIp());
        gwtDeviceConnection.setServerIp(deviceConnection.getServerIp());
        gwtDeviceConnection.setProtocol(deviceConnection.getProtocol());
        gwtDeviceConnection.setConnectionStatus(convertDeviceConnectionStatus(deviceConnection.getStatus()));
        gwtDeviceConnection.setOptlock(deviceConnection.getOptlock());
        if (deviceConnection.getId() == null) {
            gwtDeviceConnection.setConnectionStatus(GwtDeviceConnectionStatus.UNKNOWN.name());
        }
        // convertDeviceAssetChannel user coupling attributes
        gwtDeviceConnection.setReservedUserId(KapuaGwtCommonsModelConverter.convertKapuaId(deviceConnection.getReservedUserId()));
        if (deviceConnection.getUserCouplingMode() != null) {
            GwtConnectionUserCouplingMode gwtConnectionUserCouplingMode = GwtConnectionUserCouplingMode.valueOf(deviceConnection.getUserCouplingMode().name());
            gwtDeviceConnection.setConnectionUserCouplingMode(gwtConnectionUserCouplingMode != null ? gwtConnectionUserCouplingMode.getLabel() : "");
        }
        gwtDeviceConnection.setAllowUserChange(deviceConnection.getAllowUserChange());
        gwtDeviceConnection.setAuthenticationType(deviceConnection.getAuthenticationType());
        gwtDeviceConnection.setLastAuthenticationType(deviceConnection.getLastAuthenticationType());
        // Return converted entity
        return gwtDeviceConnection;
    }

    private static String convertDeviceConnectionStatus(DeviceConnectionStatus status) {
        return status.toString();
    }

    public static GwtDeviceAssets convertDeviceAssets(DeviceAssets assets) {
        GwtDeviceAssets gwtAssets = new GwtDeviceAssets();
        List<GwtDeviceAsset> gwtAssetsList = new ArrayList<GwtDeviceAsset>();
        for (DeviceAsset asset : assets.getAssets()) {
            gwtAssetsList.add(convertDeviceAsset(asset));
        }
        gwtAssets.setAssets(gwtAssetsList);
        return gwtAssets;
    }

    public static GwtDeviceAsset convertDeviceAsset(DeviceAsset asset) {
        GwtDeviceAsset gwtAsset = new GwtDeviceAsset();
        List<GwtDeviceAssetChannel> gwtChannelsList = new ArrayList<GwtDeviceAssetChannel>();
        gwtAsset.setName(asset.getName());
        for (DeviceAssetChannel channel : asset.getChannels()) {
            gwtChannelsList.add(convertDeviceAssetChannel(channel));
        }
        gwtAsset.setChannels(gwtChannelsList);
        return gwtAsset;
    }

    public static GwtDeviceAssetChannel convertDeviceAssetChannel(DeviceAssetChannel channel) {
        GwtDeviceAssetChannel gwtChannel = new GwtDeviceAssetChannel();
        gwtChannel.setName(channel.getName());
        gwtChannel.setError(channel.getError());
        gwtChannel.setTimestamp(channel.getTimestamp());
        gwtChannel.setMode(channel.getMode().toString());
        gwtChannel.setType(ObjectTypeConverter.toString(channel.getType()));
        gwtChannel.setValue(ObjectValueConverter.toString(channel.getValue()));
        return gwtChannel;
    }

    public static GwtDeviceConnectionOption convertDeviceConnectionOption(DeviceConnectionOption deviceConnectionOption) {
        GwtDeviceConnectionOption gwtDeviceConnectionOption = new GwtDeviceConnectionOption();
        // Convert commons attributes
        KapuaGwtCommonsModelConverter.convertUpdatableEntity(deviceConnectionOption, gwtDeviceConnectionOption);

        // convertDeviceAssetChannel user coupling attributes
        gwtDeviceConnectionOption.setReservedUserId(KapuaGwtCommonsModelConverter.convertKapuaId(deviceConnectionOption.getReservedUserId()));
        if (deviceConnectionOption.getUserCouplingMode() != null) {
            GwtConnectionUserCouplingMode gwtConnectionUserCouplingMode = GwtConnectionUserCouplingMode.getEnumFromLabel(deviceConnectionOption.getUserCouplingMode().name());
            gwtDeviceConnectionOption.setConnectionUserCouplingMode(gwtConnectionUserCouplingMode != null ? gwtConnectionUserCouplingMode.getLabel() : "");
        }
        gwtDeviceConnectionOption.setAllowUserChange(deviceConnectionOption.getAllowUserChange());
        gwtDeviceConnectionOption.setAuthenticationType(deviceConnectionOption.getAuthenticationType());
        // Return converted entity
        return gwtDeviceConnectionOption;
    }

}
