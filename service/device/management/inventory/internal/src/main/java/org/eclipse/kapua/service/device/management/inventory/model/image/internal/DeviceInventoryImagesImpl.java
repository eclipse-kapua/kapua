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
package org.eclipse.kapua.service.device.management.inventory.model.image.internal;

import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImage;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImages;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link DeviceInventoryImages} implementation.
 *
 * @since 2.0.0
 */
public class DeviceInventoryImagesImpl implements DeviceInventoryImages {

    private List<DeviceInventoryImage> inventoryImages;

    @Override
    public List<DeviceInventoryImage> getInventoryImages() {
        if (inventoryImages == null) {
            inventoryImages = new ArrayList<>();
        }

        return inventoryImages;
    }

    @Override
    public void addInventoryImage(DeviceInventoryImage inventoryImage) {
        getInventoryImages().add(inventoryImage);
    }

    @Override
    public void setInventoryImage(List<DeviceInventoryImage> inventoryImages) {
        this.inventoryImages = inventoryImages;
    }
}
