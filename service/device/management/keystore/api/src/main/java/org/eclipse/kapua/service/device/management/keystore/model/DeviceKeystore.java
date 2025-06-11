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

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceKeystore} definition.
 * <p>
 * It represents a keystore present on the device.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystore")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceKeystoreXmlRegistry.class, factoryMethod = "newDeviceKeystore")
public interface DeviceKeystore {

    /**
     * Gets the identifier.
     *
     * @return The identifier.
     * @since 1.5.0
     */
    @XmlElement(name = "id")
    @Schema(example = "SSLKeystore")
    String getId();

    /**
     * Sets the identifier.
     *
     * @param id The identifier.
     * @since 1.5.0
     */
    void setId(String id);

    /**
     * Gets the keystore type.
     *
     * @return The version.
     * @since 1.5.0
     */
    @XmlElement(name = "keystoreType")
    @Schema(example = "jks")
    String getKeystoreType();

    /**
     * Sets the keystore type.
     *
     * @param keystoreType The keystore type.
     * @since 1.5.0
     */
    void setKeystoreType(String keystoreType);

    /**
     * Gets the size.
     *
     * @return The size.
     * @since 1.5.0
     */
    @XmlElement(name = "size")
    @Schema(example = "3")
    Integer getSize();

    /**
     * Sets the size.
     *
     * @param size The size.
     * @since 1.5.0
     */
    void setSize(Integer size);
}
