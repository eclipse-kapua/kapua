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

/**
 * {@link DeviceInventoryBundle} definition.
 * <p>
 * It represents a bundle present on a device.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceInventoryBundle")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceInventoryBundle {

    private String id;
    private String name;
    private String version;
    private String status;
    private Boolean signed;

    /**
     * Gets the identifier.
     *
     * @return The identifier.
     * @since 1.5.0
     */
    @XmlElement(name = "id")
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier.
     *
     * @param id
     *         The identifier.
     * @since 1.5.0
     */
    public void setId(String id) {
        this.id = id;
    }

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
     * Gets the status.
     *
     * @return The status.
     * @since 1.5.0
     */
    @XmlElement(name = "status")
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status
     *         The status.
     * @since 1.5.0
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Whether the bundle is signed.
     *
     * @return {@code true} if is signed, {@code false} if not or {@code null} if the information is not known.
     * @since 2.0.0
     */
    public Boolean getSigned() {
        return signed;
    }

    /**
     * Sets whether the bundle is signed.
     *
     * @param signed
     *         {@code true} if is signed, {@code false} if not or {@code null} if the information is not known.
     * @since 2.0.0
     */
    public void setSigned(Boolean signed) {
        this.signed = signed;
    }

}
