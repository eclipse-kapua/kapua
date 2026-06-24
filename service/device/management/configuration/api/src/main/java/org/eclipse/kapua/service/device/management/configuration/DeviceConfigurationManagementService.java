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
package org.eclipse.kapua.service.device.management.configuration;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.DeviceManagementService;

/**
 * Device configuration service definition.
 *
 * @since 1.0
 */
public interface DeviceConfigurationManagementService extends DeviceManagementService {

    /**
     * Get the device configuration for the given device identifier and configuration identifier
     *
     * @param scopeId
     * @param deviceId
     * @param configurationId
     * @param configurationComponentPid
     * @param timeout                   timeout waiting for the device response
     * @return
     * @throws KapuaException
     */
    DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId,
                            String configurationId,
                            String configurationComponentPid,
                            Long timeout)
            throws KapuaException;

    DeviceComponentConfigurationFactories getFactories(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException;

    void create(KapuaId scopeId, KapuaId deviceId, String componentFactoryId, String componentId, Long timeout) throws KapuaException;

    /**
     * Put the provided configuration to the device identified by the provided device identifier
     *
     * @param scopeId
     * @param deviceId
     * @param xmlDeviceConfig xml marshalled device configuration
     * @param timeout         timeout waiting for the device response
     * @throws KapuaException
     */
    void put(KapuaId scopeId, KapuaId deviceId, String xmlDeviceConfig, Long timeout) throws KapuaException;

    /**
     * Put the provided configuration to the device identified by the provided device identifier
     *
     * @param scopeId
     * @param deviceId
     * @param deviceConfig
     * @param timeout      timeout waiting for the device response
     * @throws KapuaException
     */
    void put(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration deviceConfig, Long timeout) throws KapuaException;

    /**
     * Put the provided configuration to the device identified by the provided device identifier
     *
     * @param scopeId
     * @param deviceId
     * @param deviceComponentConfig
     * @param timeout               timeout waiting for the device response
     * @throws KapuaException
     */
    void put(KapuaId scopeId, KapuaId deviceId, DeviceComponentConfiguration deviceComponentConfig, Long timeout) throws KapuaException;
}
