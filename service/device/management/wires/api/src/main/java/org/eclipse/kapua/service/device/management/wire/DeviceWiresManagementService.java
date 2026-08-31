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
package org.eclipse.kapua.service.device.management.wire;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.DeviceManagementService;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;

public interface DeviceWiresManagementService extends DeviceManagementService {

    /**
     * Get the Wire Graph configuration for the given device identifier
     *
     * @param scopeId
     * @param deviceId
     * @param timeout                   timeout waiting for the device response
     * @return
     * @throws KapuaException
     */
    DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId,
                            Long timeout)
            throws KapuaException;

    /**
     * Put the provided Wire Graph configuration to the device identified by the provided device identifier
     *
     * @param scopeId
     * @param deviceId
     * @param wireGraphConfig
     * @param timeout      timeout waiting for the device response
     * @throws KapuaException
     */
    void put(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration wireGraphConfig, Long timeout) throws KapuaException;

    /**
     * Deletes the current Wire Graph configuration to the device identified by the provided device identifier
     *
     * @param scopeId
     * @param deviceId
     * @param timeout               timeout waiting for the device response
     * @throws KapuaException
     */
    void del(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException;
}
