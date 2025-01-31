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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.bundle.internal;

import java.util.Date;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleFactory;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.management.bundle.message.internal.BundleRequestChannel;
import org.eclipse.kapua.service.device.management.bundle.message.internal.BundleRequestMessage;
import org.eclipse.kapua.service.device.management.bundle.message.internal.BundleRequestPayload;
import org.eclipse.kapua.service.device.management.bundle.message.internal.BundleResponseMessage;
import org.eclipse.kapua.service.device.management.commons.AbstractDeviceManagementTransactionalServiceImpl;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallBuilder;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DeviceBundleManagementService implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceBundleManagementServiceImpl extends AbstractDeviceManagementTransactionalServiceImpl implements DeviceBundleManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceBundleManagementServiceImpl.class);

    private static final String SCOPE_ID = "scopeId";
    private static final String DEVICE_ID = "deviceId";

    private final DeviceBundleFactory deviceBundleFactory;

    public DeviceBundleManagementServiceImpl(TxManager txManager,
            AuthorizationService authorizationService,
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService, DeviceBundleFactory deviceBundleFactory) {
        super(txManager,
                authorizationService,
                deviceEventService,
                deviceEventFactory,
                deviceRegistryService);
        this.deviceBundleFactory = deviceBundleFactory;
    }

    @Override
    public DeviceBundles get(KapuaId scopeId, KapuaId deviceId, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        BundleRequestChannel bundleRequestChannel = new BundleRequestChannel();
        bundleRequestChannel.setAppName(DeviceBundleAppProperties.APP_NAME);
        bundleRequestChannel.setVersion(DeviceBundleAppProperties.APP_VERSION);
        bundleRequestChannel.setMethod(KapuaMethod.READ);

        BundleRequestPayload bundleRequestPayload = new BundleRequestPayload();

        BundleRequestMessage bundleRequestMessage = new BundleRequestMessage();
        bundleRequestMessage.setScopeId(scopeId);
        bundleRequestMessage.setDeviceId(deviceId);
        bundleRequestMessage.setCapturedOn(new Date());
        bundleRequestMessage.setPayload(bundleRequestPayload);
        bundleRequestMessage.setChannel(bundleRequestChannel);

        // Build request
        DeviceCallBuilder<BundleRequestChannel, BundleRequestPayload, BundleRequestMessage, BundleResponseMessage> bundleDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(bundleRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        BundleResponseMessage responseMessage;
        try {
            responseMessage = bundleDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while reading DeviceBundles for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        //
        // Create event
        createDeviceEvent(scopeId, deviceId, bundleRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDeviceBundles().orElse(deviceBundleFactory.newBundleListResult()));
    }

    @Override
    public void start(KapuaId scopeId, KapuaId deviceId, String bundleId, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notEmptyOrNull(bundleId, "bundleId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.execute, scopeId));
        // Prepare the request
        BundleRequestChannel bundleRequestChannel = new BundleRequestChannel();
        bundleRequestChannel.setAppName(DeviceBundleAppProperties.APP_NAME);
        bundleRequestChannel.setVersion(DeviceBundleAppProperties.APP_VERSION);
        bundleRequestChannel.setMethod(KapuaMethod.EXECUTE);
        bundleRequestChannel.setStart(true);
        bundleRequestChannel.setBundleId(bundleId);

        BundleRequestPayload bundleRequestPayload = new BundleRequestPayload();

        BundleRequestMessage bundleRequestMessage = new BundleRequestMessage();
        bundleRequestMessage.setScopeId(scopeId);
        bundleRequestMessage.setDeviceId(deviceId);
        bundleRequestMessage.setCapturedOn(new Date());
        bundleRequestMessage.setPayload(bundleRequestPayload);
        bundleRequestMessage.setChannel(bundleRequestChannel);

        // Build request
        DeviceCallBuilder<BundleRequestChannel, BundleRequestPayload, BundleRequestMessage, BundleResponseMessage> bundleDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(bundleRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do start
        BundleResponseMessage responseMessage;
        try {
            responseMessage = bundleDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while starting DeviceBundle {} for Device {}. Error: {}", bundleId, deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, bundleRequestMessage, responseMessage);
        // Check response
        checkResponseAcceptedOrThrowError(responseMessage);
    }

    @Override
    public void stop(KapuaId scopeId, KapuaId deviceId, String bundleId, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notEmptyOrNull(bundleId, "bundleId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.execute, scopeId));
        // Prepare the request
        BundleRequestChannel bundleRequestChannel = new BundleRequestChannel();
        bundleRequestChannel.setAppName(DeviceBundleAppProperties.APP_NAME);
        bundleRequestChannel.setVersion(DeviceBundleAppProperties.APP_VERSION);
        bundleRequestChannel.setMethod(KapuaMethod.EXECUTE);
        bundleRequestChannel.setStart(false);
        bundleRequestChannel.setBundleId(bundleId);

        BundleRequestPayload bundleRequestPayload = new BundleRequestPayload();

        BundleRequestMessage bundleRequestMessage = new BundleRequestMessage();
        bundleRequestMessage.setScopeId(scopeId);
        bundleRequestMessage.setDeviceId(deviceId);
        bundleRequestMessage.setCapturedOn(new Date());
        bundleRequestMessage.setPayload(bundleRequestPayload);
        bundleRequestMessage.setChannel(bundleRequestChannel);

        // Build request
        DeviceCallBuilder<BundleRequestChannel, BundleRequestPayload, BundleRequestMessage, BundleResponseMessage> bundleDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(bundleRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do stop
        BundleResponseMessage responseMessage;
        try {
            responseMessage = bundleDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while stopping DeviceBundle {} for Device {}. Error: {}", bundleId, deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, bundleRequestMessage, responseMessage);
        // Check response
        checkResponseAcceptedOrThrowError(responseMessage);
    }

}
