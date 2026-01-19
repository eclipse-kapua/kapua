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
package org.eclipse.kapua.service.device.management.inventory;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.DeviceManagementService;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundleAction;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundles;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainerAction;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainers;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImages;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventoryItem;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackages;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackages;
import org.eclipse.kapua.service.device.registry.Device;

/**
 * {@link DeviceInventoryItem} {@link KapuaService} definition.
 *
 * @see org.eclipse.kapua.service.KapuaService
 * @since 1.5.0
 */
public interface DeviceInventoryManagementService extends DeviceManagementService {

    /**
     * Gets the {@link DeviceInventory}
     *
     * @param scopeId  The scope {@link KapuaId} of the target {@link Device}
     * @param deviceId The {@link Device#getId()}
     * @param timeout  The timeout waiting for the device response
     * @return The {@link DeviceInventory} retrieved from the {@link Device}
     * @throws KapuaException
     * @since 1.5.0
     */
    DeviceInventory getInventory(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException;

    /**
     * Gets the {@link DeviceInventoryBundles}
     *
     * @param scopeId  The scope {@link KapuaId} of the target {@link Device}
     * @param deviceId The {@link Device#getId()}
     * @param timeout  The timeout waiting for the device response
     * @return The {@link DeviceInventoryBundles} retrieved from the {@link Device}
     * @throws KapuaException
     * @since 1.5.0
     */
    DeviceInventoryBundles getBundles(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException;

    /**
     * Executes an action on a {@link DeviceInventoryBundle}.
     *
     * @param scopeId                     The scope {@link KapuaId} of the target {@link Device}
     * @param deviceId                    The {@link Device#getId()}
     * @param deviceInventoryBundle       The {@link DeviceInventoryBundle} to execute the action on.
     * @param deviceInventoryBundleAction The {@link DeviceInventoryBundleAction}.
     * @param timeout                     The timeout waiting for the device response
     * @throws KapuaException
     * @since 1.5.0
     */
    void execBundle(KapuaId scopeId, KapuaId deviceId, DeviceInventoryBundle deviceInventoryBundle, DeviceInventoryBundleAction deviceInventoryBundleAction, Long timeout) throws KapuaException;

    /**
     * Gets the {@link DeviceInventoryContainers}
     *
     * @param scopeId  The scope {@link KapuaId} of the target {@link Device}
     * @param deviceId The {@link Device#getId()}
     * @param timeout  The timeout waiting for the device response
     * @return The {@link DeviceInventoryContainers} retrieved from the {@link Device}
     * @throws KapuaException
     * @since 1.5.0
     */
    DeviceInventoryContainers getContainers(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException;

    /**
     * Executes an action on a {@link DeviceInventoryContainer}.
     *
     * @param scopeId                        The scope {@link KapuaId} of the target {@link Device}
     * @param deviceId                       The {@link Device#getId()}
     * @param deviceInventoryContainer       The {@link DeviceInventoryContainer} to execute the action on.
     * @param deviceInventoryContainerAction The {@link DeviceInventoryContainerAction}.
     * @param timeout                        The timeout waiting for the device response
     * @throws KapuaException
     * @since 2.0.0
     */
    void execContainer(KapuaId scopeId, KapuaId deviceId, DeviceInventoryContainer deviceInventoryContainer, DeviceInventoryContainerAction deviceInventoryContainerAction, Long timeout) throws KapuaException;

    /**
     * Gets the {@link DeviceInventorySystemPackages}
     *
     * @param scopeId  The scope {@link KapuaId} of the target {@link Device}
     * @param deviceId The {@link Device#getId()}
     * @param timeout  The timeout waiting for the device response
     * @return The {@link DeviceInventorySystemPackages} retrieved from the {@link Device}
     * @throws KapuaException
     * @since 2.0.0
     */
    DeviceInventorySystemPackages getSystemPackages(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException;

    /**
     * Gets the {@link DeviceInventoryPackages}
     *
     * @param scopeId  The scope {@link KapuaId} of the target {@link Device}
     * @param deviceId The {@link Device#getId()}
     * @param timeout  The timeout waiting for the device response
     * @return The {@link DeviceInventoryPackages} retrieved from the {@link Device}
     * @throws KapuaException
     * @since 1.5.0
     */
    DeviceInventoryPackages getDeploymentPackages(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException;

    /**
     * Gets the {@link DeviceInventoryImages}
     *
     * @param scopeId  The scope {@link KapuaId} of the target {@link Device}
     * @param deviceId The {@link Device#getId()}
     * @param timeout  The timeout waiting for the device response
     * @return The {@link DeviceInventoryImages} retrieved from the {@link Device}
     * @throws KapuaException
     * @since 2.0.0
     */
    DeviceInventoryImages getImages(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException;
}
