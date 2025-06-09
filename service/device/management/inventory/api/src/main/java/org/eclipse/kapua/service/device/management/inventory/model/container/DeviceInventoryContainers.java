/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.inventory.model.container;

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
 * {@link DeviceInventoryContainers} definition.
 *
 * @since 2.0.0
 */
@XmlRootElement(name = "deviceInventoryContainers")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceInventoryContainers implements KapuaSerializable {

    private static final long serialVersionUID = 5229149965375932561L;

    private List<DeviceInventoryContainer> inventoryContainers;

    /**
     * Gets the {@link List} of {@link DeviceInventoryContainer}s
     *
     * @return The {@link List} of {@link DeviceInventoryContainer}s
     * @since 2.0.0
     */
    @XmlElement(name = "inventoryContainers")
    public List<DeviceInventoryContainer> getInventoryContainers() {
        if (inventoryContainers == null) {
            inventoryContainers = new ArrayList<>();
        }

        return inventoryContainers;
    }

    /**
     * Adds a {@link DeviceInventoryContainer} to the {@link List}
     *
     * @param inventoryContainer
     *         The {@link DeviceInventoryContainer} to add.
     * @since 2.0.0
     */
    @XmlTransient
    public void addInventoryContainer(DeviceInventoryContainer inventoryContainer) {
        getInventoryContainers().add(inventoryContainer);
    }

    /**
     * Sets the {@link List} of {@link DeviceInventoryContainer}s
     *
     * @param inventoryContainers
     *         The {@link List} of {@link DeviceInventoryContainer}s
     * @since 2.0.0
     */
    public void setInventoryContainers(List<DeviceInventoryContainer> inventoryContainers) {
        this.inventoryContainers = inventoryContainers;
    }

}
