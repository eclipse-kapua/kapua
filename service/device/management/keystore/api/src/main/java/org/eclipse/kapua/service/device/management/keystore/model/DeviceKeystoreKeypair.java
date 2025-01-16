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

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.service.device.registry.Device;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * {@link DeviceKeystoreKeypair} definition.
 * <p>
 * Represent a keypair to be created on the {@link Device}
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreKeypair")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceKeystoreXmlRegistry.class, factoryMethod = "newDeviceKeystoreKeypair")
public interface DeviceKeystoreKeypair extends KapuaSerializable {

    /**
     * Gets the keystore id.
     *
     * @return The keystore id.
     * @since 1.5.0
     */
    @XmlElement(name = "keystoreId")
    @Schema(example = "SSLKeystore")
    String getKeystoreId();

    /**
     * Sets the keystore id.
     *
     * @param keystoreId The keystore id.
     * @since 1.5.0
     */
    void setKeystoreId(String keystoreId);

    /**
     * Gets the alias.
     *
     * @return The alias.
     * @since 1.5.0
     */
    @XmlElement(name = "alias")
    @Schema(example = "ssl-eclipse")
    String getAlias();

    /**
     * Sets the alias.
     *
     * @param alias The alias.
     * @since 1.5.0
     */
    void setAlias(String alias);

    /**
     * Gets the algorithm.
     *
     * @return The algorithm.
     * @since 1.5.0
     */
    @XmlElement(name = "algorithm")
    @Schema(example = "RSA")
    String getAlgorithm();

    /**
     * Sets the algorithm.
     *
     * @param algorithm The algorithm.
     * @since 1.5.0
     */
    void setAlgorithm(String algorithm);

    /**
     * Gets the size in bytes.
     *
     * @return The size in bytes.
     * @since 1.5.0
     */
    @XmlElement(name = "size")
    @Schema(example = "2048")
    Integer getSize();

    /**
     * Sets the size in bytes.
     *
     * @param size The size in bytes.
     * @since 1.5.0
     */
    void setSize(Integer size);

    /**
     * Gets the signature algorithm.
     *
     * @return The signature algorithm.
     * @since 1.5.0
     */
    @XmlElement(name = "signatureAlgorithm")
    @Schema(example = "SHA256withRSA")
    String getSignatureAlgorithm();

    /**
     * Sets the signature algorithm.
     *
     * @param signatureAlgorithm The signature algorithm.
     * @since 1.5.0
     */
    void setSignatureAlgorithm(String signatureAlgorithm);

    /**
     * Gets the attributes.
     *
     * @return The attributes.
     * @since 1.5.0
     */
    @XmlElement(name = "attributes")
    @Schema(example = "CN=Let's Encrypt Authority X3,O=Let's Encrypt,C=US")
    String getAttributes();

    /**
     * Sets the attributes.
     *
     * @param attributes The attributes.
     * @since 1.5.0
     */
    void setAttributes(String attributes);
}
