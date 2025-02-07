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
package org.eclipse.kapua.service.device.management.inventory.model.packages;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;

/**
 * {@link DeviceInventoryPackage} definition.
 * <p>
 * It represents a system package present on a device.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceInventoryDeploymentPackage")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceInventoryPackage {

    private String name;
    private String version;
    private List<DeviceInventoryBundle> packageBundles;

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 1.5.0
     */
    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *         The name.
     * @since 1.5.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the bundle version.
     *
     * @return The version.
     * @since 1.5.0
     */
    @XmlElement(name = "version")
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     *         The version.
     * @since 1.5.0
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the {@link List} of {@link DeviceInventoryBundle}s.
     *
     * @return The {@link List} of {@link DeviceInventoryBundle}s.
     * @since 1.5.0
     */
    public List<DeviceInventoryBundle> getPackageBundles() {
        if (packageBundles == null) {
            packageBundles = new ArrayList<>();
        }

        return packageBundles;
    }

    /**
     * Adds a {@link DeviceInventoryBundle} to the {@link List}.
     *
     * @param inventoryBundle
     *         The {@link DeviceInventoryBundle} to add.
     * @since 1.5.0
     */
    public void addPackageBundle(DeviceInventoryBundle inventoryBundle) {
        getPackageBundles().add(inventoryBundle);
    }

    /**
     * Sets the {@link List} of {@link DeviceInventoryBundle}s.
     *
     * @param inventoryBundles
     *         The {@link List} of {@link DeviceInventoryBundle}s.
     * @since 1.5.0
     */
    public void setPackageBundles(List<DeviceInventoryBundle> inventoryBundles) {
        this.packageBundles = inventoryBundles;
    }

}
