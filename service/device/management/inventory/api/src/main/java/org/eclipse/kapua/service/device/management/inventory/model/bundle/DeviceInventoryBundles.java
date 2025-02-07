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
package org.eclipse.kapua.service.device.management.inventory.model.bundle;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * {@link DeviceInventoryBundles} definition.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceInventoryBundles")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceInventoryBundles implements KapuaSerializable {

    private static final long serialVersionUID = 5229149965375932561L;

    private List<DeviceInventoryBundle> inventoryBundles;

    /**
     * Gets the {@link List} of {@link DeviceInventoryBundle}s
     *
     * @return The {@link List} of {@link DeviceInventoryBundle}s
     * @since 1.5.0
     */
    @XmlElement(name = "inventoryBundles")
    public List<DeviceInventoryBundle> getInventoryBundles() {
        if (inventoryBundles == null) {
            inventoryBundles = new ArrayList<>();
        }

        return inventoryBundles;
    }

    /**
     * Adds a {@link DeviceInventoryBundle} to the {@link List}
     *
     * @param inventoryBundle
     *         The {@link DeviceInventoryBundle} to add.
     * @since 1.5.0
     */
    @XmlTransient
    public void addInventoryBundle(DeviceInventoryBundle inventoryBundle) {
        getInventoryBundles().add(inventoryBundle);
    }

    /**
     * Sets the {@link List} of {@link DeviceInventoryBundle}s
     *
     * @param inventoryBundles
     *         The {@link List} of {@link DeviceInventoryBundle}s
     * @since 1.5.0
     */
    public void setInventoryBundles(List<DeviceInventoryBundle> inventoryBundles) {
        this.inventoryBundles = inventoryBundles;
    }

}
