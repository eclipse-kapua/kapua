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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link DeviceInventoryContainer} definition.
 * <p>
 * It represents a container present on a device.
 *
 * @since 2.0.0
 */
@XmlRootElement(name = "deviceInventoryContainer")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceInventoryContainer {

    private String name;
    private String version;
    private String containerType;

    private DeviceInventoryContainerState state;

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 2.0.0
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
     * @since 2.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the container version.
     *
     * @return The version.
     * @since 2.0.0
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
     * @since 2.0.0
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the type.
     *
     * @return The type.
     * @since 2.0.0
     */
    @XmlElement(name = "containerType")
    public String getContainerType() {
        return containerType;
    }

    /**
     * Sets the container type.
     *
     * @param containerType
     *         The container type.
     * @since 2.0.0
     */
    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    /**
     * Gets the {@link DeviceInventoryContainerState}.
     *
     * @return The {@link DeviceInventoryContainerState}.
     * @since 2.0.0
     */
    public DeviceInventoryContainerState getState() {
        return state;
    }

    /**
     * Sets the {@link DeviceInventoryContainerState}.
     *
     * @param state
     *         The {@link DeviceInventoryContainerState}.
     * @since 2.0.0
     */
    public void setState(DeviceInventoryContainerState state) {
        this.state = state;
    }

}
