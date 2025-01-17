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
package org.eclipse.kapua.service.device.registry.lifecycle.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaOptimisticLockingException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.device.lifecycle.KapuaAppsMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaBirthChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaBirthMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaBirthPayload;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaLifecycleMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingMessage;
import org.eclipse.kapua.message.internal.device.lifecycle.model.BirthExtendedProperties;
import org.eclipse.kapua.message.internal.device.lifecycle.model.BirthExtendedProperty;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceExtendedProperty;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.internal.DeviceExtendedPropertyImpl;
import org.eclipse.kapua.service.device.registry.lifecycle.DeviceLifeCycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * {@link DeviceLifeCycleService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceLifeCycleServiceImpl implements DeviceLifeCycleService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceLifeCycleServiceImpl.class);

    private static final int MAX_RETRY = 3;
    private static final double MAX_WAIT = 500d;

    protected final ObjectMapper jsonMapper;
    protected final DeviceEventService deviceEventService;
    protected final DeviceEventFactory deviceEventFactory;
    protected final DeviceRegistryService deviceRegistryService;
    protected final DeviceFactory deviceFactory;

    @Inject
    public DeviceLifeCycleServiceImpl(
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService,
            DeviceFactory deviceFactory) {
        this.jsonMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        this.deviceEventService = deviceEventService;
        this.deviceEventFactory = deviceEventFactory;
        this.deviceRegistryService = deviceRegistryService;
        this.deviceFactory = deviceFactory;
    }

    @Override
    public void birth(KapuaId connectionId, KapuaBirthMessage birthMessage) throws KapuaException {

        KapuaId scopeId = birthMessage.getScopeId();
        KapuaId deviceId = birthMessage.getDeviceId();

        KapuaBirthPayload birthPayload = birthMessage.getPayload();
        KapuaBirthChannel birthChannel = birthMessage.getChannel();
        // Device update
        Device device;
        if (deviceId == null) {
            DeviceCreator deviceCreator = new DeviceCreator(scopeId);

            deviceCreator.setClientId(birthChannel.getClientId());
            deviceCreator.setDisplayName(birthPayload.getDisplayName());
            deviceCreator.setSerialNumber(birthPayload.getSerialNumber());
            deviceCreator.setModelId(birthPayload.getModelId());
            deviceCreator.setModelName(birthPayload.getModelName());
            deviceCreator.setImei(birthPayload.getModemImei());
            deviceCreator.setImsi(birthPayload.getModemImsi());
            deviceCreator.setIccid(birthPayload.getModemIccid());
            deviceCreator.setBiosVersion(birthPayload.getBiosVersion());
            deviceCreator.setFirmwareVersion(birthPayload.getFirmwareVersion());
            deviceCreator.setOsVersion(birthPayload.getOsVersion());
            deviceCreator.setJvmVersion(birthPayload.getJvmVersion());
            deviceCreator.setOsgiFrameworkVersion(birthPayload.getContainerFrameworkVersion());
            deviceCreator.setApplicationFrameworkVersion(birthPayload.getApplicationFrameworkVersion());
            deviceCreator.setConnectionInterface(birthPayload.getConnectionInterface());
            deviceCreator.setConnectionIp(birthPayload.getConnectionIp());
            deviceCreator.setApplicationIdentifiers(birthPayload.getApplicationIdentifiers());
            deviceCreator.setAcceptEncoding(birthPayload.getAcceptEncoding());

            deviceCreator.setExtendedProperties(buildDeviceExtendedPropertyFromBirth(birthPayload.getExtendedProperties()));

            deviceCreator.setTamperStatus(birthPayload.getTamperStatus());

            // issue #57
            deviceCreator.setConnectionId(connectionId);

            device = deviceRegistryService.create(deviceCreator);
        } else {
            device = updateDeviceInfoFromMessage(scopeId, deviceId, birthPayload, connectionId);
        }
        // Event create
        createLifecycleEvent(device, "BIRTH", birthMessage);
    }

    @Override
    public void applications(KapuaId connectionId, KapuaAppsMessage message) throws KapuaException {

        Device device = updateDeviceInfoFromMessage(message.getScopeId(), message.getDeviceId(), message.getPayload(), connectionId);
        // Event create
        createLifecycleEvent(device, "APPLICATION", message);
    }

    @Override
    public void missing(KapuaId connectionId, KapuaMissingMessage message) throws KapuaException {
        KapuaId scopeId = message.getScopeId();
        KapuaId deviceId = message.getDeviceId();
        // Event create
        createLifecycleEvent(scopeId, deviceId, "MISSING", message);
    }

    @Override
    public void death(KapuaId connectionId, KapuaDisconnectMessage message) throws KapuaException {
        KapuaId scopeId = message.getScopeId();
        KapuaId deviceId = message.getDeviceId();
        // Event create
        createLifecycleEvent(scopeId, deviceId, "DEATH", message);
    }

    /**
     * Updates the {@link Device} infos from the {@link KapuaBirthPayload} (or {@link org.eclipse.kapua.message.device.lifecycle.KapuaAppsPayload}.
     * <p>
     * Tries to update the {@link Device} a number of times to allow close device updates to be stored properly.
     *
     * @param scopeId
     *         The {@link Device#getScopeId()} to update.
     * @param deviceId
     *         The {@link Device#getId()} to update.
     * @param birthPayload
     *         The {@link KapuaBirthPayload} from which extract data.
     * @param connectionId
     *         The {@link DeviceConnection#getId()}
     * @return The updated {@link Device}.
     * @throws KapuaException
     *         If {@link Device} does not exists or {@link DeviceRegistryService#update(KapuaUpdatableEntity)} causes an error.
     * @since 1.2.0
     */
    private Device updateDeviceInfoFromMessage(KapuaId scopeId, KapuaId deviceId, KapuaBirthPayload birthPayload, KapuaId connectionId) throws KapuaException {

        Device device = null;

        int retry = 0;
        do {
            retry++;

            try {
                device = deviceRegistryService.find(scopeId, deviceId);

                if (device == null) {
                    throw new KapuaEntityNotFoundException(Device.TYPE, deviceId);
                }

                // If the BirthMessage does not contain a 'Display Name' keep the one registered on the DeviceRegistryService.
                if (!Strings.isNullOrEmpty(birthPayload.getDisplayName())) {
                    device.setDisplayName(birthPayload.getDisplayName());
                }

                device.setSerialNumber(birthPayload.getSerialNumber());
                device.setModelId(birthPayload.getModelId());
                device.setModelName(birthPayload.getModelName());
                device.setImei(birthPayload.getModemImei());
                device.setImsi(birthPayload.getModemImsi());
                device.setIccid(birthPayload.getModemIccid());
                device.setBiosVersion(birthPayload.getBiosVersion());
                device.setFirmwareVersion(birthPayload.getFirmwareVersion());
                device.setOsVersion(birthPayload.getOsVersion());
                device.setJvmVersion(birthPayload.getJvmVersion());
                device.setOsgiFrameworkVersion(birthPayload.getContainerFrameworkVersion());
                device.setApplicationFrameworkVersion(birthPayload.getApplicationFrameworkVersion());
                device.setConnectionInterface(birthPayload.getConnectionInterface());
                device.setConnectionIp(birthPayload.getConnectionIp());
                device.setApplicationIdentifiers(birthPayload.getApplicationIdentifiers());
                device.setAcceptEncoding(birthPayload.getAcceptEncoding());

                device.setExtendedProperties(buildDeviceExtendedPropertyFromBirth(birthPayload.getExtendedProperties()));

                device.setTamperStatus(birthPayload.getTamperStatus());

                // issue #57
                device.setConnectionId(connectionId);

                device = deviceRegistryService.update(device);
                break;
            } catch (KapuaOptimisticLockingException e) {
                LOG.warn("Concurrent update for device: {}... Attempt: {}/{}. {}", device.getClientId(), retry, MAX_RETRY, retry < MAX_RETRY ? "Retrying..." : "Raising exception!");

                if (retry < MAX_RETRY) {
                    try {
                        Thread.sleep((long) (Math.random() * MAX_WAIT));
                    } catch (InterruptedException e1) {
                        LOG.warn("Error while waiting retry: {}", e.getMessage());
                    }
                } else {
                    throw e;
                }
            }
        }
        while (true);

        return device;
    }

    /**
     * Creates a {@link DeviceEvent} from the {@link KapuaLifecycleMessage}.
     * <p>
     * Internally invokes {@link DeviceRegistryService#find(KapuaId, KapuaId)} with the given parameters and then uses {@link #createLifecycleEvent(Device, String, KapuaLifecycleMessage)}
     *
     * @param scopeId
     *         The {@link Device#getScopeId()} of the {@link Device} that generated the {@link KapuaLifecycleMessage}.
     * @param deviceId
     *         The {@link Device#getId()} of the {@link Device} that generated the {@link KapuaLifecycleMessage}.
     * @param resource
     *         The resource used to publish the {@link KapuaLifecycleMessage}
     * @param message
     *         The {@link KapuaLifecycleMessage} from which to extract data.
     * @throws KapuaException
     *         if storing the {@link DeviceEvent} throws a {@link KapuaException}
     * @since 1.2.0
     */
    private DeviceEvent createLifecycleEvent(@NotNull KapuaId scopeId, KapuaId deviceId, @NotNull String resource, @NotNull KapuaLifecycleMessage<?, ?> message) throws KapuaException {

        Device device = deviceRegistryService.find(scopeId, deviceId);

        return createLifecycleEvent(device, resource, message);
    }

    /**
     * Creates a {@link DeviceEvent} from the {@link KapuaLifecycleMessage}.
     *
     * @param device
     *         The {@link Device} that generated the {@link KapuaLifecycleMessage}.
     * @param resource
     *         The resource used to publish the {@link KapuaLifecycleMessage}
     * @param message
     *         The {@link KapuaLifecycleMessage} from which to extract data.
     * @throws KapuaException
     *         if storing the {@link DeviceEvent} throws a {@link KapuaException}
     * @since 1.2.0
     */
    private DeviceEvent createLifecycleEvent(@NotNull Device device, @NotNull String resource, @NotNull KapuaLifecycleMessage<?, ?> message) throws KapuaException {

        DeviceEventCreator deviceEventCreator = new DeviceEventCreator(device.getScopeId(), device.getId(), message.getReceivedOn(), resource);
        deviceEventCreator.setResponseCode(KapuaResponseCode.ACCEPTED);
        deviceEventCreator.setSentOn(message.getSentOn());
        deviceEventCreator.setReceivedOn(Date.from(KapuaDateUtils.getKapuaSysDate()));

        if (message.getPayload() != null) {
            deviceEventCreator.setEventMessage(message.getPayload().toDisplayString());
        }

        KapuaPosition position = message.getPosition();
        if (position != null) {
            deviceEventCreator.setPosition(position);
        }

        return KapuaSecurityUtils.doPrivileged(() -> deviceEventService.create(deviceEventCreator));
    }

    /**
     * Builds the {@link List} of {@link DeviceExtendedProperty}es from the {@link KapuaBirthPayload#getExtendedProperties()} property.
     *
     * @param extendedPropertiesString
     *         The {@link KapuaBirthPayload#getExtendedProperties()} to parse.
     * @return The {@link List} of {@link DeviceExtendedProperty}es parsed.
     * @throws KapuaException
     *         If there are exception while parsing the JSON-formatted metric.
     * @since 1.5.0
     */
    private List<DeviceExtendedProperty> buildDeviceExtendedPropertyFromBirth(@Nullable String extendedPropertiesString) throws KapuaException {
        if (Strings.isNullOrEmpty(extendedPropertiesString)) {
            return Collections.emptyList();
        }

        try {
            BirthExtendedProperties birthExtendedProperties = jsonMapper.readValue(extendedPropertiesString, BirthExtendedProperties.class);

            List<DeviceExtendedProperty> deviceExtendedProperties = new ArrayList<>();
            for (Map.Entry<String, BirthExtendedProperty> eps : birthExtendedProperties.getExtendedProperties().entrySet()) {
                for (Map.Entry<String, String> ep : eps.getValue().entrySet()) {
                    deviceExtendedProperties.add(new DeviceExtendedPropertyImpl(eps.getKey(), ep.getKey(), ep.getValue()));
                }
            }

            return deviceExtendedProperties;
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error while parsing Device's extended properties!");
        }
    }
}
