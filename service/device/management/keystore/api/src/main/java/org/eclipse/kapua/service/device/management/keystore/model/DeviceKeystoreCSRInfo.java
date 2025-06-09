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
 * {@link DeviceKeystoreCSRInfo} definition.
 * <p>
 * Represent a certificate signing request to be sent to the {@link Device}
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreCSRInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceKeystoreCSRInfo implements KapuaSerializable {

    private String keystoreId;
    private String alias;
    private String signatureAlgorithm;
    private String attributes;

    /**
     * Gets the {@link DeviceKeystoreItem#getKeystoreId()} target to be used for signing.
     *
     * @return The {@link DeviceKeystoreItem#getKeystoreId()} target to be used for signing.
     * @since 1.5.0
     */
    @XmlElement(name = "keystoreId")
    public String getKeystoreId() {
        return keystoreId;
    }

    /**
     * Sets the {@link DeviceKeystoreItem#getKeystoreId()} target to be used for signing.
     *
     * @param keystoreId
     *         The {@link DeviceKeystoreItem#getKeystoreId()} target to be used for signing.
     * @since 1.5.0
     */
    public void setKeystoreId(String keystoreId) {
        this.keystoreId = keystoreId;
    }

    /**
     * Gets the {@link DeviceKeystoreItem#getAlias()} target to be used for signing.
     *
     * @return The {@link DeviceKeystoreItem#getAlias()} target to be used for signing.
     * @since 1.5.0
     */
    @XmlElement(name = "alias")
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the {@link DeviceKeystoreItem#getAlias()} target to be used for signing.
     *
     * @param alias
     *         The {@link DeviceKeystoreItem#getAlias()} target to be used for signing.
     * @since 1.5.0
     */
    public void setAlias(String alias) {
        this.alias = alias;
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
     * Gets the attributes to sign.
     *
     * @return The attributes to sign.
     * @since 1.5.0
     */
    @XmlElement(name = "attributes")
    public String getAttributes() {
        return attributes;
    }

    /**
     * Sets the attributes to sign.
     *
     * @param attributes
     *         The attributes to sign.
     * @since 1.5.0
     */
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

}
