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
package org.eclipse.kapua.service.device.management.inventory.model.inventory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceInventoryItem} definition.
 * <p>
 * It represents an item present on a device.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceInventoryItem")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceInventoryXmlRegistry.class, factoryMethod = "newDeviceInventoryItem")
public interface DeviceInventoryItem {

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 1.5.0
     */
    @XmlElement(name = "name")
    @Schema(example = "adduser")
    String getName();

    /**
     * Sets the name.
     *
     * @param name The name.
     * @since 1.5.0
     */
    void setName(String name);

    /**
     * Gets the bundle version.
     *
     * @return The version.
     * @since 1.5.0
     */
    @XmlElement(name = "version")
    @Schema(example = "3.118")
    String getVersion();

    /**
     * Sets the version.
     *
     * @param version The version.
     * @since 1.5.0
     */
    void setVersion(String version);

    /**
     * Gets the type.
     *
     * @return The type.
     * @since 1.5.0
     */
    @XmlElement(name = "itemType")
    @Schema(example = "DEB")
    String getItemType();

    /**
     * Sets the type.
     *
     * @param itemType The type.
     * @since 1.5.0
     */
    void setItemType(String itemType);
}
