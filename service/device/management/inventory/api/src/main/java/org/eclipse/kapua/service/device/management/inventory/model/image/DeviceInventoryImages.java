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

import org.eclipse.kapua.KapuaSerializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * {@link DeviceInventoryImages} definition.
 *
 * @since 2.0.0
 */
@XmlRootElement(name = "deviceInventoryImages")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceInventoryImagesXmlRegistry.class, factoryMethod = "newDeviceInventoryImages")
public interface DeviceInventoryImages extends KapuaSerializable {

    /**
     * Gets the {@link List} of {@link DeviceInventoryImage}s
     *
     * @return The {@link List} of {@link DeviceInventoryImage}s
     * @since 2.0.0
     */
    @XmlElement(name = "inventoryImages")
    List<DeviceInventoryImage> getInventoryImages();

    /**
     * Adds a {@link DeviceInventoryImage} to the {@link List}
     *
     * @param inventoryImage The {@link DeviceInventoryImage} to add.
     * @since 2.0.0
     */
    @XmlTransient
    void addInventoryImage(DeviceInventoryImage inventoryImage);

    /**
     * Sets the {@link List} of {@link DeviceInventoryImage}s
     *
     * @param inventoryImage The {@link List} of {@link DeviceInventoryImage}s
     * @since 2.0.0
     */
    void setInventoryImage(List<DeviceInventoryImage> inventoryImage);

}
