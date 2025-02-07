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

/**
 * {@link DeviceKeystoreKeypair} definition.
 * <p>
 * Represent a keypair to be created on the {@link Device}
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreKeypair")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceKeystoreKeypair implements KapuaSerializable {

    private String keystoreId;
    private String alias;
    private String algorithm;
    private String signatureAlgorithm;
    private Integer size;
    private String attributes;

    /**
     * Gets the keystore id.
     *
     * @return The keystore id.
     * @since 1.5.0
     */
    @XmlElement(name = "keystoreId")
    public String getKeystoreId() {
        return keystoreId;
    }

    /**
     * Sets the keystore id.
     *
     * @param keystoreId
     *         The keystore id.
     * @since 1.5.0
     */
    public void setKeystoreId(String keystoreId) {
        this.keystoreId = keystoreId;
    }

    /**
     * Gets the alias.
     *
     * @return The alias.
     * @since 1.5.0
     */
    @XmlElement(name = "alias")
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     *
     * @param alias
     *         The alias.
     * @since 1.5.0
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Gets the algorithm.
     *
     * @return The algorithm.
     * @since 1.5.0
     */
    @XmlElement(name = "algorithm")
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the algorithm.
     *
     * @param algorithm
     *         The algorithm.
     * @since 1.5.0
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Gets the size in bytes.
     *
     * @return The size in bytes.
     * @since 1.5.0
     */
    @XmlElement(name = "size")
    public Integer getSize() {
        return size;
    }

    /**
     * Sets the size in bytes.
     *
     * @param size
     *         The size in bytes.
     * @since 1.5.0
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * Gets the signature algorithm.
     *
     * @return The signature algorithm.
     * @since 1.5.0
     */
    @XmlElement(name = "signatureAlgorithm")
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * Sets the signature algorithm.
     *
     * @param signatureAlgorithm
     *         The signature algorithm.
     * @since 1.5.0
     */
    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    /**
     * Gets the attributes.
     *
     * @return The attributes.
     * @since 1.5.0
     */
    @XmlElement(name = "attributes")
    public String getAttributes() {
        return attributes;
    }

    /**
     * Sets the attributes.
     *
     * @param attributes
     *         The attributes.
     * @since 1.5.0
     */
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

}
