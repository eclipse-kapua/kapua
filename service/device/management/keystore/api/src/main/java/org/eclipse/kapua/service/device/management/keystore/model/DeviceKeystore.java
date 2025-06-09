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
package org.eclipse.kapua.service.device.management.keystore.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link DeviceKeystore} definition.
 * <p>
 * It represents a keystore present on the device.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystore")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceKeystore {

    private String id;
    private String keystoreType;
    private Integer size;

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
     * Gets the keystore type.
     *
     * @return The version.
     * @since 1.5.0
     */
    @XmlElement(name = "keystoreType")
    public String getKeystoreType() {
        return keystoreType;
    }

    /**
     * Sets the keystore type.
     *
     * @param keystoreType
     *         The keystore type.
     * @since 1.5.0
     */
    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    /**
     * Gets the size.
     *
     * @return The size.
     * @since 1.5.0
     */
    @XmlElement(name = "size")
    public Integer getSize() {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param size
     *         The size.
     * @since 1.5.0
     */
    public void setSize(Integer size) {
        this.size = size;
    }
}
