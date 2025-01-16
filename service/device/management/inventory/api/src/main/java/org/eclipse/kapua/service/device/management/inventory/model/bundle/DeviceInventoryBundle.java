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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceInventoryBundle} definition.
 * <p>
 * It represents a bundle present on a device.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceInventoryBundle")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceInventoryBundlesXmlRegistry.class, factoryMethod = "newDeviceInventoryBundle")
public interface DeviceInventoryBundle {

    /**
     * Gets the identifier.
     *
     * @return The identifier.
     * @since 1.5.0
     */
    @XmlElement(name = "id")
    String getId();

    /**
     * Sets the identifier.
     *
     * @param id The identifier.
     * @since 1.5.0
     */
    void setId(String id);

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 1.5.0
     */
    @XmlElement(name = "name")
    @Schema(example = "org.eclipse.osgi")
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
    @Schema(example = "3.16.0.v20200828-0759")
    String getVersion();

    /**
     * Sets the version.
     *
     * @param version The version.
     * @since 1.5.0
     */
    void setVersion(String version);

    /**
     * Gets the status.
     *
     * @return The status.
     * @since 1.5.0
     */
    @XmlElement(name = "status")
    @Schema(example = "ACTIVE")
    String getStatus();

    /**
     * Sets the status.
     *
     * @param status The status.
     * @since 1.5.0
     */
    void setStatus(String status);

    /**
     * Whether the bundle is signed.
     *
     * @return {@code true} if is signed, {@code false} if not or {@code null} if the information is not known.
     * @since 2.0.0
     */
    Boolean getSigned();

    /**
     * Sets whether the bundle is signed.
     *
     * @param signed {@code true} if is signed, {@code false} if not or {@code null} if the information is not known.
     * @since 2.0.0
     */
    void setSigned(Boolean signed);
}
