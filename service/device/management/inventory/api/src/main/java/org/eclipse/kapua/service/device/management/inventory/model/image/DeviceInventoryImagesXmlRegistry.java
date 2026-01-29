/*******************************************************************************
 * Copyright (c) 2021, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.inventory.model.image;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementFactory;

public class DeviceInventoryImagesXmlRegistry {

    private final DeviceInventoryManagementFactory factory = KapuaLocator.getInstance().getFactory(DeviceInventoryManagementFactory.class);

    /**
     * Instantiates a new {@link DeviceInventoryImages}.
     *
     * @return The newly instantiated {@link DeviceInventoryImages}
     * @since 2.0.0
     */
    public DeviceInventoryImages newDeviceInventoryImages() {
        return factory.newDeviceInventoryImages();
    }

    /**
     * Instantiates a new {@link DeviceInventoryImage}.
     *
     * @return The newly instantiated {@link DeviceInventoryImage}
     * @since 2.0.0
     */
    public DeviceInventoryImage newDeviceInventoryImage() {
        return factory.newDeviceInventoryImage();
    }
}
