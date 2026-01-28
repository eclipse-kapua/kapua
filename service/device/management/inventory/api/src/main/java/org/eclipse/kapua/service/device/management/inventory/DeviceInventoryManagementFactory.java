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

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundles;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainers;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImage;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImages;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventoryItem;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackage;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackages;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackage;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackages;

/**
 * {@link DeviceInventoryItem} {@link KapuaObjectFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaObjectFactory
 * @since 1.5.0
 */
public interface DeviceInventoryManagementFactory extends KapuaObjectFactory {

    /**
     * Instantiates a new {@link DeviceInventory}.
     *
     * @return The newly instantiated {@link DeviceInventory}
     * @since 1.5.0
     */
    DeviceInventory newDeviceInventory();

    /**
     * Instantiates a new {@link DeviceInventoryItem}.
     *
     * @return The newly instantiated {@link DeviceInventoryItem}
     * @since 1.5.0
     */
    DeviceInventoryItem newDeviceInventoryItem();

    /**
     * Instantiates a new {@link DeviceInventoryBundle}.
     *
     * @return The newly instantiated {@link DeviceInventoryBundle}
     * @since 1.5.0
     */
    DeviceInventoryBundle newDeviceInventoryBundle();

    /**
     * Instantiates a new {@link DeviceInventoryBundles}.
     *
     * @return The newly instantiated {@link DeviceInventoryBundles}
     * @since 1.5.0
     */
    DeviceInventoryBundles newDeviceInventoryBundles();

    /**
     * Instantiates a new {@link DeviceInventoryContainer}.
     *
     * @return The newly instantiated {@link DeviceInventoryContainer}
     * @since 2.0.0
     */
    DeviceInventoryContainer newDeviceInventoryContainer();

    /**
     * Instantiates a new {@link DeviceInventoryContainers}.
     *
     * @return The newly instantiated {@link DeviceInventoryContainers}
     * @since 2.0.0
     */
    DeviceInventoryContainers newDeviceInventoryContainers();


    /**
     * Instantiates a new {@link DeviceInventorySystemPackage}.
     *
     * @return The newly instantiated {@link DeviceInventorySystemPackage}
     * @since 1.5.0
     */
    DeviceInventorySystemPackage newDeviceInventorySystemPackage();

    /**
     * Instantiates a new {@link DeviceInventorySystemPackages}.
     *
     * @return The newly instantiated {@link DeviceInventorySystemPackages}
     * @since 1.5.0
     */
    DeviceInventorySystemPackages newDeviceInventorySystemPackages();

    /**
     * Instantiates a new {@link DeviceInventoryPackage}.
     *
     * @return The newly instantiated {@link DeviceInventoryPackage}
     * @since 1.5.0
     */
    DeviceInventoryPackage newDeviceInventoryPackage();

    /**
     * Instantiates a new {@link DeviceInventoryPackages}.
     *
     * @return The newly instantiated {@link DeviceInventoryPackages}
     * @since 1.5.0
     */
    DeviceInventoryPackages newDeviceInventoryPackages();

    /**
     * Instantiates a new {@link DeviceInventoryImage}.
     *
     * @return The newly instantiated {@link DeviceInventoryImage}
     * @since 2.0.0
     */
    DeviceInventoryImage newDeviceInventoryImage();

    /**
     * Instantiates a new {@link DeviceInventoryImages}.
     *
     * @return The newly instantiated {@link DeviceInventoryImages}
     * @since 2.0.0
     */
    DeviceInventoryImages newDeviceInventoryImages();

}
