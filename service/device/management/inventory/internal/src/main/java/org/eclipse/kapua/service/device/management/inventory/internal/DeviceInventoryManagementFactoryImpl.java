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

import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementFactory;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundles;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.internal.DeviceInventoryBundleImpl;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.internal.DeviceInventoryBundlesImpl;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainers;
import org.eclipse.kapua.service.device.management.inventory.model.container.internal.DeviceInventoryContainerImpl;
import org.eclipse.kapua.service.device.management.inventory.model.container.internal.DeviceInventoryContainersImpl;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImage;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImages;
import org.eclipse.kapua.service.device.management.inventory.model.image.internal.DeviceInventoryImageImpl;
import org.eclipse.kapua.service.device.management.inventory.model.image.internal.DeviceInventoryImagesImpl;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventoryItem;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.internal.DeviceInventoryImpl;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.internal.DeviceInventoryItemImpl;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackage;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackages;
import org.eclipse.kapua.service.device.management.inventory.model.packages.internal.DeviceInventoryPackageImpl;
import org.eclipse.kapua.service.device.management.inventory.model.packages.internal.DeviceInventoryPackagesImpl;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackage;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackages;
import org.eclipse.kapua.service.device.management.inventory.model.system.internal.DeviceInventorySystemPackageImpl;
import org.eclipse.kapua.service.device.management.inventory.model.system.internal.DeviceInventorySystemPackagesImpl;

import javax.inject.Singleton;

/**
 * {@link DeviceInventoryManagementFactory} implementation.
 *
 * @since 1.5.0
 */
@Singleton
public class DeviceInventoryManagementFactoryImpl implements DeviceInventoryManagementFactory {

    @Override
    public DeviceInventory newDeviceInventory() {
        return new DeviceInventoryImpl();
    }

    @Override
    public DeviceInventoryItem newDeviceInventoryItem() {
        return new DeviceInventoryItemImpl();
    }

    @Override
    public DeviceInventoryBundle newDeviceInventoryBundle() {
        return new DeviceInventoryBundleImpl();
    }

    @Override
    public DeviceInventoryBundles newDeviceInventoryBundles() {
        return new DeviceInventoryBundlesImpl();
    }

    @Override
    public DeviceInventoryContainer newDeviceInventoryContainer() {
        return new DeviceInventoryContainerImpl();
    }

    @Override
    public DeviceInventoryContainers newDeviceInventoryContainers() {
        return new DeviceInventoryContainersImpl();
    }

    @Override
    public DeviceInventorySystemPackage newDeviceInventorySystemPackage() {
        return new DeviceInventorySystemPackageImpl();
    }

    @Override
    public DeviceInventorySystemPackages newDeviceInventorySystemPackages() {
        return new DeviceInventorySystemPackagesImpl();
    }

    @Override
    public DeviceInventoryPackage newDeviceInventoryPackage() {
        return new DeviceInventoryPackageImpl();
    }

    @Override
    public DeviceInventoryPackages newDeviceInventoryPackages() {
        return new DeviceInventoryPackagesImpl();
    }

    @Override
    public DeviceInventoryImage newDeviceInventoryImage() {
        return new DeviceInventoryImageImpl();
    }

    @Override
    public DeviceInventoryImages newDeviceInventoryImages() {
        return new DeviceInventoryImagesImpl();
    }
}
