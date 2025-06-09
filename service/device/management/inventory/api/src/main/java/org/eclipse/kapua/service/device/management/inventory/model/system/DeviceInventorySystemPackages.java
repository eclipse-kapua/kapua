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
package org.eclipse.kapua.service.device.management.inventory.model.system;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * {@link DeviceInventorySystemPackages} definition.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceInventorySystemPackages")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceInventorySystemPackages implements KapuaSerializable {

    private static final long serialVersionUID = -3824464082139248997L;

    private List<DeviceInventorySystemPackage> systemPackages;

    /**
     * Gets the {@link List} of {@link DeviceInventorySystemPackage}s
     *
     * @return The {@link List} of {@link DeviceInventorySystemPackage}s
     * @since 1.5.0
     */
    @XmlElement(name = "systemPackages")
    public List<DeviceInventorySystemPackage> getSystemPackages() {
        if (systemPackages == null) {
            systemPackages = new ArrayList<>();
        }

        return systemPackages;
    }

    /**
     * Adds a {@link DeviceInventorySystemPackage} to the {@link List}
     *
     * @param inventorySystemPackage
     *         The {@link DeviceInventorySystemPackage} to add.
     * @since 1.5.0
     */
    public void addSystemPackage(DeviceInventorySystemPackage inventorySystemPackage) {
        getSystemPackages().add(inventorySystemPackage);
    }

    /**
     * Sets the {@link List} of {@link DeviceInventorySystemPackage}s
     *
     * @param inventorySystemPackages
     *         The {@link List} of {@link DeviceInventorySystemPackage}s
     * @since 1.5.0
     */
    public void setSystemPackages(List<DeviceInventorySystemPackage> inventorySystemPackages) {
        this.systemPackages = inventorySystemPackages;
    }
}
