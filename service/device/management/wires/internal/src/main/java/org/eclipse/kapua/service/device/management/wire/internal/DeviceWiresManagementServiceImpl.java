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
package org.eclipse.kapua.service.device.management.wire.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.management.commons.AbstractDeviceManagementTransactionalServiceImpl;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallBuilder;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestChannel;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestMessage;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestPayload;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponseMessage;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementRequestContentException;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.storage.TxManager;
import org.eclipse.kapua.service.device.management.wire.DeviceWiresManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Date;

/**
 * {@link DeviceWiresManagementService} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class DeviceWiresManagementServiceImpl extends AbstractDeviceManagementTransactionalServiceImpl implements DeviceWiresManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceWiresManagementServiceImpl.class);
    private final DeviceConfigurationFactory deviceConfigurationFactory;

    private static final String SCOPE_ID = "scopeId";
    private static final String DEVICE_ID = "deviceId";

    public DeviceWiresManagementServiceImpl(TxManager txManager,
                                                    AuthorizationService authorizationService,
                                                    PermissionFactory permissionFactory,
                                                    DeviceEventService deviceEventService,
                                                    DeviceEventFactory deviceEventFactory,
                                                    DeviceRegistryService deviceRegistryService,
                                                    DeviceConfigurationFactory deviceConfigurationFactory) {
        super(txManager,
                authorizationService,
                permissionFactory,
                deviceEventService,
                deviceEventFactory,
                deviceRegistryService
        );
        this.deviceConfigurationFactory = deviceConfigurationFactory;
    }

    @Override
    public DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        ConfigurationRequestChannel configurationRequestChannel = new ConfigurationRequestChannel();
        configurationRequestChannel.setAppName(DeviceWireAppProperties.APP_NAME);
        configurationRequestChannel.setVersion(DeviceWireAppProperties.APP_VERSION);
        configurationRequestChannel.setMethod(KapuaMethod.READ);

        ConfigurationRequestPayload configurationRequestPayload = new ConfigurationRequestPayload();

        ConfigurationRequestMessage configurationRequestMessage = new ConfigurationRequestMessage();
        configurationRequestMessage.setScopeId(scopeId);
        configurationRequestMessage.setDeviceId(deviceId);
        configurationRequestMessage.setCapturedOn(new Date());
        configurationRequestMessage.setPayload(configurationRequestPayload);
        configurationRequestMessage.setChannel(configurationRequestChannel);

        // Build request
        DeviceCallBuilder<ConfigurationRequestChannel, ConfigurationRequestPayload, ConfigurationRequestMessage, ConfigurationResponseMessage> configurationDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(configurationRequestMessage)
                        .withTimeoutOrDefault(timeout);

            ConfigurationResponseMessage responseMessage;
            try {
                responseMessage = configurationDeviceCallBuilder.send();
            } catch (Exception e) {
                LOG.error("Error while getting Device Wire Graph for Device {}. Error: {}", deviceId, e.getMessage(), e);
                throw e;
            }

            // Create event
            createDeviceEvent(scopeId, deviceId, configurationRequestMessage, responseMessage);
            // Check response
            DeviceConfiguration deviceWireConfiguration = checkResponseAcceptedOrThrowError(responseMessage,
                    () -> responseMessage.getPayload().getDeviceConfigurations().orElse(deviceConfigurationFactory.newConfigurationInstance()));

            return deviceWireConfiguration;
    }

    @Override
    public void put(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration wireGraphConfig, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(wireGraphConfig, "wireGraphConfiguration");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        // Prepare the request
        ConfigurationRequestChannel configurationRequestChannel = new ConfigurationRequestChannel();
        configurationRequestChannel.setAppName(DeviceWireAppProperties.APP_NAME);
        configurationRequestChannel.setVersion(DeviceWireAppProperties.APP_VERSION);
        configurationRequestChannel.setMethod(KapuaMethod.WRITE);

        ConfigurationRequestPayload configurationRequestPayload = new ConfigurationRequestPayload();

        try {
            configurationRequestPayload.setDeviceConfigurations(wireGraphConfig);
        } catch (Exception e) {
            throw new DeviceManagementRequestContentException(e, wireGraphConfig);
        }

        ConfigurationRequestMessage configurationRequestMessage = new ConfigurationRequestMessage();
        configurationRequestMessage.setScopeId(scopeId);
        configurationRequestMessage.setDeviceId(deviceId);
        configurationRequestMessage.setCapturedOn(new Date());
        configurationRequestMessage.setPayload(configurationRequestPayload);
        configurationRequestMessage.setChannel(configurationRequestChannel);

        // Build request
        DeviceCallBuilder<ConfigurationRequestChannel, ConfigurationRequestPayload, ConfigurationRequestMessage, ConfigurationResponseMessage> configurationDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(configurationRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do put
        ConfigurationResponseMessage responseMessage;
        try {
            responseMessage = configurationDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while putting Device Wire Graph {} for Device {}. Error: {}", wireGraphConfig, deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, configurationRequestMessage, responseMessage);
        // Check response
        checkResponseAcceptedOrThrowError(responseMessage);
    }

    @Override
    public void del(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_MANAGEMENT, Actions.delete, scopeId));
        // Prepare the request
        ConfigurationRequestChannel configurationRequestChannel = new ConfigurationRequestChannel();
        configurationRequestChannel.setAppName(DeviceWireAppProperties.APP_NAME);
        configurationRequestChannel.setVersion(DeviceWireAppProperties.APP_VERSION);
        configurationRequestChannel.setMethod(KapuaMethod.DEL);

        ConfigurationRequestPayload configurationRequestPayload = new ConfigurationRequestPayload();

        ConfigurationRequestMessage configurationRequestMessage = new ConfigurationRequestMessage();
        configurationRequestMessage.setScopeId(scopeId);
        configurationRequestMessage.setDeviceId(deviceId);
        configurationRequestMessage.setCapturedOn(new Date());
        configurationRequestMessage.setPayload(configurationRequestPayload);
        configurationRequestMessage.setChannel(configurationRequestChannel);

        // Build request
        DeviceCallBuilder<ConfigurationRequestChannel, ConfigurationRequestPayload, ConfigurationRequestMessage, ConfigurationResponseMessage> configurationDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(configurationRequestMessage)
                        .withTimeoutOrDefault(timeout);

        ConfigurationResponseMessage responseMessage;
        try {
            responseMessage = configurationDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting Device Wire Graph for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, configurationRequestMessage, responseMessage);
        // Check response
        checkResponseAcceptedOrThrowError(responseMessage);
    }

}
