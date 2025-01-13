/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.asset.internal;

import java.util.Date;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetFactory;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetManagementService;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.asset.message.internal.AssetRequestChannel;
import org.eclipse.kapua.service.device.management.asset.message.internal.AssetRequestMessage;
import org.eclipse.kapua.service.device.management.asset.message.internal.AssetRequestPayload;
import org.eclipse.kapua.service.device.management.asset.message.internal.AssetResponseMessage;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;
import org.eclipse.kapua.service.device.management.commons.AbstractDeviceManagementTransactionalServiceImpl;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallBuilder;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementRequestContentException;
import org.eclipse.kapua.service.device.management.exception.DeviceNeverConnectedException;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DeviceAssetManagementService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceAssetManagementServiceImpl extends AbstractDeviceManagementTransactionalServiceImpl implements DeviceAssetManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceAssetManagementServiceImpl.class);

    private static final String SCOPE_ID = "scopeId";
    private static final String DEVICE_ID = "deviceId";
    private static final String DEVICE_ASSETS = "deviceAssets";

    private final DeviceAssetStoreService deviceAssetStoreService;
    private final DeviceAssetFactory deviceAssetFactory;

    public DeviceAssetManagementServiceImpl(
            TxManager txManager,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService,
            DeviceAssetStoreService deviceAssetStoreService, DeviceAssetFactory deviceAssetFactory) {
        super(txManager,
                authorizationService,
                permissionFactory,
                deviceEventService,
                deviceEventFactory,
                deviceRegistryService);
        this.deviceAssetStoreService = deviceAssetStoreService;
        this.deviceAssetFactory = deviceAssetFactory;
    }

    @Override
    public DeviceAssets get(KapuaId scopeId, KapuaId deviceId, DeviceAssets deviceAssets, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(deviceAssets, DEVICE_ASSETS);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        AssetRequestChannel assetRequestChannel = new AssetRequestChannel();
        assetRequestChannel.setAppName(DeviceAssetAppProperties.APP_NAME);
        assetRequestChannel.setVersion(DeviceAssetAppProperties.APP_VERSION);
        assetRequestChannel.setMethod(KapuaMethod.READ);

        AssetRequestPayload assetRequestPayload = new AssetRequestPayload();
        try {
            assetRequestPayload.setDeviceAssets(deviceAssets);
        } catch (Exception e) {
            throw new DeviceManagementRequestContentException(e, deviceAssets);
        }

        AssetRequestMessage assetRequestMessage = new AssetRequestMessage();
        assetRequestMessage.setScopeId(scopeId);
        assetRequestMessage.setDeviceId(deviceId);
        assetRequestMessage.setCapturedOn(new Date());
        assetRequestMessage.setPayload(assetRequestPayload);
        assetRequestMessage.setChannel(assetRequestChannel);

        // Build call
        DeviceCallBuilder<AssetRequestChannel, AssetRequestPayload, AssetRequestMessage, AssetResponseMessage> assetDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(assetRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        if (isDeviceConnected(scopeId, deviceId)) {
            AssetResponseMessage responseMessage;
            try {
                responseMessage = assetDeviceCallBuilder.send();
            } catch (Exception e) {
                LOG.error("Error while reading DeviceAssets {} for Device {}. Error: {}", deviceAssets, deviceId, e.getMessage(), e);
                throw e;
            }

            // Create event
            createDeviceEvent(scopeId, deviceId, assetRequestMessage, responseMessage);
            // Check response
            DeviceAssets onlineDeviceAssets = checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDeviceAssets().orElse(new DeviceAssets()));
            // Store value and return
            if (deviceAssetStoreService.isServiceEnabled(scopeId) &&
                    deviceAssetStoreService.isApplicationEnabled(scopeId, deviceId)) {
                deviceAssetStoreService.storeAssets(scopeId, deviceId, onlineDeviceAssets);
            }

            return onlineDeviceAssets;
        } else {
            if (deviceAssetStoreService.isServiceEnabled(scopeId)) {
                return deviceAssetStoreService.getAssets(scopeId, deviceId, deviceAssets);
            } else {
                throw new DeviceNeverConnectedException(deviceId);
            }
        }
    }

    @Override
    public DeviceAssets read(KapuaId scopeId, KapuaId deviceId, DeviceAssets deviceAssets, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(deviceAssets, DEVICE_ASSETS);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        AssetRequestChannel assetRequestChannel = new AssetRequestChannel();
        assetRequestChannel.setAppName(DeviceAssetAppProperties.APP_NAME);
        assetRequestChannel.setVersion(DeviceAssetAppProperties.APP_VERSION);
        assetRequestChannel.setMethod(KapuaMethod.EXECUTE);
        assetRequestChannel.setRead(true);

        AssetRequestPayload assetRequestPayload = new AssetRequestPayload();
        try {
            assetRequestPayload.setDeviceAssets(deviceAssets);
        } catch (Exception e) {
            throw new DeviceManagementRequestContentException(e, deviceAssets);
        }

        AssetRequestMessage assetRequestMessage = new AssetRequestMessage();
        assetRequestMessage.setScopeId(scopeId);
        assetRequestMessage.setDeviceId(deviceId);
        assetRequestMessage.setCapturedOn(new Date());
        assetRequestMessage.setPayload(assetRequestPayload);
        assetRequestMessage.setChannel(assetRequestChannel);

        // Build call
        DeviceCallBuilder<AssetRequestChannel, AssetRequestPayload, AssetRequestMessage, AssetResponseMessage> assetDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(assetRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do read
        if (isDeviceConnected(scopeId, deviceId)) {
            AssetResponseMessage responseMessage;
            try {
                responseMessage = assetDeviceCallBuilder.send();
            } catch (Exception e) {
                LOG.error("Error while reading DeviceAssets values {} for Device {}. Error: {}", deviceAssets, deviceId, e.getMessage(), e);
                throw e;
            }

            // Create event
            createDeviceEvent(scopeId, deviceId, assetRequestMessage, responseMessage);
            // Check response
            DeviceAssets onlineDeviceAssets = checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDeviceAssets().orElse(new DeviceAssets()));
            // Store value and return
            if (deviceAssetStoreService.isServiceEnabled(scopeId) &&
                    deviceAssetStoreService.isApplicationEnabled(scopeId, deviceId)) {
                deviceAssetStoreService.storeAssetsValues(scopeId, deviceId, onlineDeviceAssets);
            }

            return onlineDeviceAssets;
        } else {
            if (deviceAssetStoreService.isServiceEnabled(scopeId) &&
                    deviceAssetStoreService.isApplicationEnabled(scopeId, deviceId)) {
                return deviceAssetStoreService.getAssetsValues(scopeId, deviceId, deviceAssets);
            } else {
                throw new DeviceNeverConnectedException(deviceId);
            }
        }
    }

    @Override
    public DeviceAssets write(KapuaId scopeId, KapuaId deviceId, DeviceAssets deviceAssets, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(deviceAssets, DEVICE_ASSETS);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        // Prepare the request
        AssetRequestChannel assetRequestChannel = new AssetRequestChannel();
        assetRequestChannel.setAppName(DeviceAssetAppProperties.APP_NAME);
        assetRequestChannel.setVersion(DeviceAssetAppProperties.APP_VERSION);
        assetRequestChannel.setMethod(KapuaMethod.EXECUTE);
        assetRequestChannel.setWrite(true);

        AssetRequestPayload assetRequestPayload = new AssetRequestPayload();
        try {
            assetRequestPayload.setDeviceAssets(deviceAssets);
        } catch (Exception e) {
            throw new DeviceManagementRequestContentException(e, deviceAssets);
        }

        AssetRequestMessage assetRequestMessage = new AssetRequestMessage();
        assetRequestMessage.setScopeId(scopeId);
        assetRequestMessage.setDeviceId(deviceId);
        assetRequestMessage.setCapturedOn(new Date());
        assetRequestMessage.setPayload(assetRequestPayload);
        assetRequestMessage.setChannel(assetRequestChannel);

        // Build call
        DeviceCallBuilder<AssetRequestChannel, AssetRequestPayload, AssetRequestMessage, AssetResponseMessage> assetDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(assetRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do write
        AssetResponseMessage responseMessage;
        try {
            responseMessage = assetDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while writing DeviceAssets {} for Device {}. Error: {}", deviceAssets, deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, assetRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDeviceAssets().orElse(new DeviceAssets()));
    }
}
