/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.inventory.internal;

import java.util.Date;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.management.commons.AbstractDeviceManagementTransactionalServiceImpl;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallBuilder;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementRequestContentException;
import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementFactory;
import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementService;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryBundleExecRequestMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryBundlesResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryContainerExecRequestMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryContainersResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryEmptyRequestMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryListResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryNoContentResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryPackagesResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryRequestChannel;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryRequestPayload;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventorySystemPackagesResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundleAction;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundles;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainerAction;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainers;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackages;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackages;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DeviceInventoryManagementService} implementation.
 *
 * @since 1.5.0
 */
@Singleton
public class DeviceInventoryManagementServiceImpl extends AbstractDeviceManagementTransactionalServiceImpl implements DeviceInventoryManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceInventoryManagementServiceImpl.class);

    private static final String SCOPE_ID = "scopeId";
    private static final String DEVICE_ID = "deviceId";

    private final DeviceInventoryManagementFactory deviceInventoryManagementFactory;

    public DeviceInventoryManagementServiceImpl(
            TxManager txManager,
            AuthorizationService authorizationService,
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService, DeviceInventoryManagementFactory deviceInventoryManagementFactory) {
        super(txManager,
                authorizationService,
                deviceEventService,
                deviceEventFactory,
                deviceRegistryService);
        this.deviceInventoryManagementFactory = deviceInventoryManagementFactory;
    }

    @Override
    public DeviceInventory getInventory(KapuaId scopeId, KapuaId deviceId, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        InventoryRequestChannel inventoryRequestChannel = new InventoryRequestChannel();
        inventoryRequestChannel.setAppName(DeviceInventoryAppProperties.APP_NAME);
        inventoryRequestChannel.setVersion(DeviceInventoryAppProperties.APP_VERSION);
        inventoryRequestChannel.setMethod(KapuaMethod.READ);
        inventoryRequestChannel.setResource("inventory");

        InventoryRequestPayload inventoryRequestPayload = new InventoryRequestPayload();

        InventoryEmptyRequestMessage inventoryRequestMessage = new InventoryEmptyRequestMessage() {

            @Override
            public Class<InventoryListResponseMessage> getResponseClass() {
                return InventoryListResponseMessage.class;
            }
        };

        inventoryRequestMessage.setScopeId(scopeId);
        inventoryRequestMessage.setDeviceId(deviceId);
        inventoryRequestMessage.setCapturedOn(new Date());
        inventoryRequestMessage.setPayload(inventoryRequestPayload);
        inventoryRequestMessage.setChannel(inventoryRequestChannel);

        // Build request
        DeviceCallBuilder<InventoryRequestChannel, InventoryRequestPayload, InventoryEmptyRequestMessage, InventoryListResponseMessage> inventoryDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(inventoryRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        InventoryListResponseMessage responseMessage;
        try {
            responseMessage = inventoryDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DeviceInventory for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, inventoryRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDeviceInventory().orElse(deviceInventoryManagementFactory.newDeviceInventory()));
    }

    @Override
    public DeviceInventoryBundles getBundles(KapuaId scopeId, KapuaId deviceId, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        InventoryRequestChannel inventoryRequestChannel = new InventoryRequestChannel();
        inventoryRequestChannel.setAppName(DeviceInventoryAppProperties.APP_NAME);
        inventoryRequestChannel.setVersion(DeviceInventoryAppProperties.APP_VERSION);
        inventoryRequestChannel.setMethod(KapuaMethod.READ);
        inventoryRequestChannel.setResource("bundles");

        InventoryRequestPayload inventoryRequestPayload = new InventoryRequestPayload();

        InventoryEmptyRequestMessage inventoryRequestMessage = new InventoryEmptyRequestMessage() {

            @Override
            public Class<InventoryBundlesResponseMessage> getResponseClass() {
                return InventoryBundlesResponseMessage.class;
            }
        };

        inventoryRequestMessage.setScopeId(scopeId);
        inventoryRequestMessage.setDeviceId(deviceId);
        inventoryRequestMessage.setCapturedOn(new Date());
        inventoryRequestMessage.setPayload(inventoryRequestPayload);
        inventoryRequestMessage.setChannel(inventoryRequestChannel);

        // Build request
        DeviceCallBuilder<InventoryRequestChannel, InventoryRequestPayload, InventoryEmptyRequestMessage, InventoryBundlesResponseMessage> inventoryDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(inventoryRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        InventoryBundlesResponseMessage responseMessage;
        try {
            responseMessage = inventoryDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DeviceInventoryBundles for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, inventoryRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDeviceInventoryBundles().orElse(deviceInventoryManagementFactory.newDeviceInventoryBundles()));
    }

    @Override
    public void execBundle(KapuaId scopeId, KapuaId deviceId, DeviceInventoryBundle deviceInventoryBundle, DeviceInventoryBundleAction deviceInventoryBundleAction, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(deviceInventoryBundle, "deviceInventoryBundle");
        ArgumentValidator.notNull(deviceInventoryBundle.getId(), "deviceInventoryBundle.id");
        ArgumentValidator.notNull(deviceInventoryBundle.getName(), "deviceInventoryBundle.name");
        ArgumentValidator.notNull(deviceInventoryBundleAction, "deviceInventoryBundleAction");

        performAdditionalValidationOnDeviceInventoryBundleId(deviceInventoryBundle.getId());

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        // Prepare the request
        InventoryRequestChannel inventoryRequestChannel = new InventoryRequestChannel();
        inventoryRequestChannel.setAppName(DeviceInventoryAppProperties.APP_NAME);
        inventoryRequestChannel.setVersion(DeviceInventoryAppProperties.APP_VERSION);
        inventoryRequestChannel.setMethod(KapuaMethod.EXECUTE);
        inventoryRequestChannel.setResource("bundles");
        inventoryRequestChannel.setBundleAction(deviceInventoryBundleAction);

        InventoryRequestPayload inventoryRequestPayload = new InventoryRequestPayload();
        try {
            inventoryRequestPayload.setDeviceInventoryBundle(deviceInventoryBundle);
        } catch (Exception e) {
            throw new DeviceManagementRequestContentException(e, deviceInventoryBundle);
        }

        InventoryBundleExecRequestMessage inventoryRequestMessage = new InventoryBundleExecRequestMessage() {

            @Override
            public Class<InventoryNoContentResponseMessage> getResponseClass() {
                return InventoryNoContentResponseMessage.class;
            }
        };

        inventoryRequestMessage.setScopeId(scopeId);
        inventoryRequestMessage.setDeviceId(deviceId);
        inventoryRequestMessage.setCapturedOn(new Date());
        inventoryRequestMessage.setPayload(inventoryRequestPayload);
        inventoryRequestMessage.setChannel(inventoryRequestChannel);

        // Build request
        DeviceCallBuilder<InventoryRequestChannel, InventoryRequestPayload, InventoryBundleExecRequestMessage, InventoryNoContentResponseMessage> inventoryDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(inventoryRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do exec
        InventoryNoContentResponseMessage responseMessage;
        try {
            responseMessage = inventoryDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while executing {} on DeviceInventoryBundle {} for Device {}. Error: {}", deviceInventoryBundleAction, deviceInventoryBundle.getName(), deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, inventoryRequestMessage, responseMessage);
        // Check response
        checkResponseAcceptedOrThrowError(responseMessage);
    }

    @Override
    public DeviceInventoryContainers getContainers(KapuaId scopeId, KapuaId deviceId, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        InventoryRequestChannel inventoryRequestChannel = new InventoryRequestChannel();
        inventoryRequestChannel.setAppName(DeviceInventoryAppProperties.APP_NAME);
        inventoryRequestChannel.setVersion(DeviceInventoryAppProperties.APP_VERSION);
        inventoryRequestChannel.setMethod(KapuaMethod.READ);
        inventoryRequestChannel.setResource("containers");

        InventoryRequestPayload inventoryRequestPayload = new InventoryRequestPayload();

        InventoryEmptyRequestMessage inventoryRequestMessage = new InventoryEmptyRequestMessage() {

            @Override
            public Class<InventoryContainersResponseMessage> getResponseClass() {
                return InventoryContainersResponseMessage.class;
            }
        };

        inventoryRequestMessage.setScopeId(scopeId);
        inventoryRequestMessage.setDeviceId(deviceId);
        inventoryRequestMessage.setCapturedOn(new Date());
        inventoryRequestMessage.setPayload(inventoryRequestPayload);
        inventoryRequestMessage.setChannel(inventoryRequestChannel);

        // Build request
        DeviceCallBuilder<InventoryRequestChannel, InventoryRequestPayload, InventoryEmptyRequestMessage, InventoryContainersResponseMessage> inventoryDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(inventoryRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        InventoryContainersResponseMessage responseMessage;
        try {
            responseMessage = inventoryDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DeviceInventoryContainers {} for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, inventoryRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage,
                () -> responseMessage.getPayload().getDeviceInventoryContainers().orElse(deviceInventoryManagementFactory.newDeviceInventoryContainers()));
    }

    @Override
    public void execContainer(KapuaId scopeId, KapuaId deviceId, DeviceInventoryContainer deviceInventoryContainer, DeviceInventoryContainerAction deviceInventoryContainerAction, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(deviceInventoryContainer, "deviceInventoryContainer");
        ArgumentValidator.notNull(deviceInventoryContainer.getName(), "deviceInventoryContainer.name");
        ArgumentValidator.notNull(deviceInventoryContainer.getVersion(), "deviceInventoryContainer.version");
        ArgumentValidator.notNull(deviceInventoryContainerAction, "deviceInventoryContainerAction");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        // Prepare the request
        InventoryRequestChannel inventoryRequestChannel = new InventoryRequestChannel();
        inventoryRequestChannel.setAppName(DeviceInventoryAppProperties.APP_NAME);
        inventoryRequestChannel.setVersion(DeviceInventoryAppProperties.APP_VERSION);
        inventoryRequestChannel.setMethod(KapuaMethod.EXECUTE);
        inventoryRequestChannel.setResource("containers");
        inventoryRequestChannel.setContainerAction(deviceInventoryContainerAction);

        InventoryRequestPayload inventoryRequestPayload = new InventoryRequestPayload();
        try {
            inventoryRequestPayload.setDeviceInventoryContainer(deviceInventoryContainer);
        } catch (Exception e) {
            throw new DeviceManagementRequestContentException(e, deviceInventoryContainer);
        }

        InventoryContainerExecRequestMessage inventoryRequestMessage = new InventoryContainerExecRequestMessage() {

            @Override
            public Class<InventoryNoContentResponseMessage> getResponseClass() {
                return InventoryNoContentResponseMessage.class;
            }
        };

        inventoryRequestMessage.setScopeId(scopeId);
        inventoryRequestMessage.setDeviceId(deviceId);
        inventoryRequestMessage.setCapturedOn(new Date());
        inventoryRequestMessage.setPayload(inventoryRequestPayload);
        inventoryRequestMessage.setChannel(inventoryRequestChannel);

        // Build request
        DeviceCallBuilder<InventoryRequestChannel, InventoryRequestPayload, InventoryContainerExecRequestMessage, InventoryNoContentResponseMessage> inventoryDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(inventoryRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do exec
        InventoryNoContentResponseMessage responseMessage;
        try {
            responseMessage = inventoryDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while executing {} on DeviceInventoryContainer {}:{} for Device {}. Error: {}", deviceInventoryContainerAction, deviceInventoryContainer.getName(),
                    deviceInventoryContainer.getVersion(), deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, inventoryRequestMessage, responseMessage);
        // Check response
        checkResponseAcceptedOrThrowError(responseMessage);
    }

    @Override
    public DeviceInventorySystemPackages getSystemPackages(KapuaId scopeId, KapuaId deviceId, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        InventoryRequestChannel inventoryRequestChannel = new InventoryRequestChannel();
        inventoryRequestChannel.setAppName(DeviceInventoryAppProperties.APP_NAME);
        inventoryRequestChannel.setVersion(DeviceInventoryAppProperties.APP_VERSION);
        inventoryRequestChannel.setMethod(KapuaMethod.READ);
        inventoryRequestChannel.setResource("systemPackages");

        InventoryRequestPayload inventoryRequestPayload = new InventoryRequestPayload();

        InventoryEmptyRequestMessage inventoryRequestMessage = new InventoryEmptyRequestMessage() {

            @Override
            public Class<InventorySystemPackagesResponseMessage> getResponseClass() {
                return InventorySystemPackagesResponseMessage.class;
            }
        };

        inventoryRequestMessage.setScopeId(scopeId);
        inventoryRequestMessage.setDeviceId(deviceId);
        inventoryRequestMessage.setCapturedOn(new Date());
        inventoryRequestMessage.setPayload(inventoryRequestPayload);
        inventoryRequestMessage.setChannel(inventoryRequestChannel);

        // Build request
        DeviceCallBuilder<InventoryRequestChannel, InventoryRequestPayload, InventoryEmptyRequestMessage, InventorySystemPackagesResponseMessage> inventoryDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(inventoryRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        InventorySystemPackagesResponseMessage responseMessage;
        try {
            responseMessage = inventoryDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DeviceInventorySystemPackages for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, inventoryRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage,
                () -> responseMessage.getPayload().getDeviceInventorySystemPackages().orElse(deviceInventoryManagementFactory.newDeviceInventorySystemPackages()));
    }

    @Override
    public DeviceInventoryPackages getDeploymentPackages(KapuaId scopeId, KapuaId deviceId, Long timeout)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        InventoryRequestChannel inventoryRequestChannel = new InventoryRequestChannel();
        inventoryRequestChannel.setAppName(DeviceInventoryAppProperties.APP_NAME);
        inventoryRequestChannel.setVersion(DeviceInventoryAppProperties.APP_VERSION);
        inventoryRequestChannel.setMethod(KapuaMethod.READ);
        inventoryRequestChannel.setResource("deploymentPackages");

        InventoryRequestPayload inventoryRequestPayload = new InventoryRequestPayload();

        InventoryEmptyRequestMessage inventoryRequestMessage = new InventoryEmptyRequestMessage() {

            @Override
            public Class<InventoryPackagesResponseMessage> getResponseClass() {
                return InventoryPackagesResponseMessage.class;
            }
        };

        inventoryRequestMessage.setScopeId(scopeId);
        inventoryRequestMessage.setDeviceId(deviceId);
        inventoryRequestMessage.setCapturedOn(new Date());
        inventoryRequestMessage.setPayload(inventoryRequestPayload);
        inventoryRequestMessage.setChannel(inventoryRequestChannel);

        // Build request
        DeviceCallBuilder<InventoryRequestChannel, InventoryRequestPayload, InventoryEmptyRequestMessage, InventoryPackagesResponseMessage> inventoryDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(inventoryRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        InventoryPackagesResponseMessage responseMessage;
        try {
            responseMessage = inventoryDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DeviceInventoryPackages for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, inventoryRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage,
                () -> responseMessage.getPayload().getDeviceInventoryPackages().orElse(deviceInventoryManagementFactory.newDeviceInventoryPackages()));
    }

    /**
     * Performs an additional check on {@link DeviceInventoryBundle#getId()} to verify that it can be converted to a {@link Integer}.
     * <p>
     * This check is required because initially the property was created as a {@link String} even if in Kura it is a {@link Integer}. See Kura documentation on Device Inventory Bundle <a
     * href="https://eclipse.github.io/kura/docs-release-5.4/administration/system-component-inventory/">here</a>
     * <p>
     * We cannot change the type of {@link DeviceInventoryBundle#getId()} from {@link String} to {@link Integer} because it would be an API breaking change. We can add a validation to improve the
     * error returned in case a non-integer value is provided, since the current error returned is {@link NumberFormatException} (at line TranslatorAppInventoryBundleExecKapuaKura:74).
     *
     * @param deviceInventoryBundleId
     *         The {@link DeviceInventoryBundle#getId()} to check
     * @throws KapuaIllegalArgumentException
     *         If {@link DeviceInventoryBundle#getId()} cannot be converted to a {@link Integer}.
     * @since 2.0.0
     */
    private void performAdditionalValidationOnDeviceInventoryBundleId(String deviceInventoryBundleId) throws KapuaIllegalArgumentException {
        try {
            Integer.parseInt(deviceInventoryBundleId);
        } catch (NumberFormatException nfe) {
            throw new KapuaIllegalArgumentException("deviceInventoryBundle.id", deviceInventoryBundleId);
        }
    }

}
