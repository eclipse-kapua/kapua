/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial implementation
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.connection.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.RootUserTester;
import org.eclipse.kapua.commons.configuration.ServiceConfigRepository;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManagerImpl;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.authentication.api.DeviceConnectionCredentialAdapter;
import org.eclipse.kapua.service.device.registry.KapuaDeviceRegistrySettingKeys;
import org.eclipse.kapua.service.device.registry.KapuaDeviceRegistrySettings;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.storage.TxContext;

/**
 * {@link DeviceConnection} {@link ServiceConfigurationManager} implementation.
 *
 * @since 2.0.0
 */
public class DeviceConnectionServiceConfigurationManager extends ServiceConfigurationManagerImpl implements ServiceConfigurationManager {

    private final Map<String, DeviceConnectionCredentialAdapter> availableDeviceConnectionAdapters;

    private final KapuaDeviceRegistrySettings deviceRegistrySettings;

    /**
     * Constructor.
     *
     * @param serviceConfigRepository
     *         The {@link ServiceConfigRepository} instance.
     * @param rootUserTester
     *         The {@link RootUserTester} instance.
     * @param availableDeviceConnectionAdapters
     *         The {@link Map} of available {@link DeviceConnectionCredentialAdapter}.
     * @since 2.0.0
     */
    public DeviceConnectionServiceConfigurationManager(
            ServiceConfigRepository serviceConfigRepository,
            RootUserTester rootUserTester,
            Map<String, DeviceConnectionCredentialAdapter> availableDeviceConnectionAdapters,
            KapuaDeviceRegistrySettings kapuaDeviceRegistrySettings) {
        super(DeviceConnectionService.class.getName(), serviceConfigRepository, rootUserTester);

        this.availableDeviceConnectionAdapters = availableDeviceConnectionAdapters;
        this.deviceRegistrySettings = kapuaDeviceRegistrySettings;
    }

    /**
     * Gets the {@link DeviceConnectionService} {@link KapuaTocd}.
     * <p>
     * Behaviour is overridden for the following reasons:
     * <ul>
     *     <li>to insert dynamic {@link KapuaToption} for 'deviceConnectionAuthenticationType' {@link KapuaTad}</li>
     * </ul>
     *
     * @param txContext
     *         The {@link TxContext}.
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @param excludeDisabled
     *         Whether to exclude disabled {@link KapuaTocd}s and {@link KapuaTad}s.
     * @return The {@link DeviceConnectionService} {@link KapuaTocd}.
     * @throws KapuaException
     * @since 2.0.0
     */
    @Override
    public KapuaTocd getConfigMetadata(TxContext txContext, KapuaId scopeId, boolean excludeDisabled) throws KapuaException {

        KapuaTocd deviceConnectionServiceConfigDefinition = super.getConfigMetadata(txContext, scopeId, excludeDisabled);

        // Find the 'deviceConnectionAuthenticationType' KapuaTad
        Optional<KapuaTad> authenticationTypeConfigDefinition = findAuthenticationTypeTad(deviceConnectionServiceConfigDefinition);

        // Add the KapuaToption to the KapuaTad
        authenticationTypeConfigDefinition.ifPresent(tad -> {
            List<KapuaToption> authTypeToptionList =
                    availableDeviceConnectionAdapters
                            .keySet()
                            .stream()
                            .map(authType -> {
                                KapuaToption authTypeToption = new KapuaToption();
                                authTypeToption.setLabel(authType);
                                authTypeToption.setValue(authType);
                                return authTypeToption;
                            })
                            .collect(Collectors.toList());

            // Set the default KapuaToption from the DeviceRegistrySettings
            tad.setDefault(deviceRegistrySettings.getString(KapuaDeviceRegistrySettingKeys.DEVICE_CONNECTION_AUTHENTICATION_TYPE_DEFAULT));

            tad.setOption(authTypeToptionList);
        });

        return deviceConnectionServiceConfigDefinition;
    }

    /**
     * Looks for the {@link KapuaTad} with 'deviceConnectionAuthenticationType' {@link KapuaTad#getId()}.
     *
     * @param deviceConnectionServiceConfigDefinition
     *         The {@link DeviceConnectionService} {@link KapuaTad}.
     * @return The {@link Optional} {@link KapuaTad}
     * @since 2.0.0
     */
    protected Optional<KapuaTad> findAuthenticationTypeTad(KapuaTocd deviceConnectionServiceConfigDefinition) {
        return deviceConnectionServiceConfigDefinition
                .getAD()
                .stream()
                .filter(kapuaTad -> "deviceConnectionAuthenticationType".equals(kapuaTad.getId()))
                .findFirst();
    }
}
