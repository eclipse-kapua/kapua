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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.device.registry.Device;

/**
 * {@link DeviceKeystoreItem} definition.
 * <p>
 * Identifies an item of the {@link DeviceKeystore} for the {@link Device}.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreItem")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceKeystoreItem {

    private String keystoreId;
    private String itemType;
    private Integer size;
    private String algorithm;
    private String alias;
    private String subjectDN;
    private List<DeviceKeystoreSubjectAN> subjectAN;
    private String issuer;
    private Date notBefore;
    private Date notAfter;
    private String certificate;
    private List<String> certificateChain;

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
     * Gets the item type.
     * <p>
     * Examples:
     * <ul>
     *     <li>PRIVATE_KEY</li>
     *     <li>TRUSTED_CERTIFICATE</li>
     * </ul>
     *
     * @return The keystore item type.
     * @return The item type.
     * @since 1.5.0
     */
    @XmlElement(name = "itemType")
    public String getItemType() {
        return itemType;
    }

    /**
     * Sets the item type.
     *
     * @param itemType
     *         The item type.
     * @since 1.5.0
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
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
     * Gets the subject distinguished name.
     *
     * @return The subject distinguished name.
     * @since 1.5.0
     */
    @XmlElement(name = "subjectDN")
    public String getSubjectDN() {
        return subjectDN;
    }

    /**
     * Sets the subject distinguished name.
     *
     * @param subjectDN
     *         The subject distinguished name.
     * @since 1.5.0
     */
    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    /**
     * Gets the {@link List} of {@link DeviceKeystoreSubjectAN}s.
     *
     * @return The {@link List} of {@link DeviceKeystoreSubjectAN}s.
     * @since 1.5.0
     */
    @XmlElement(name = "subjectANs")
    public List<DeviceKeystoreSubjectAN> getSubjectAN() {
        if (subjectAN == null) {
            subjectAN = new ArrayList<>();
        }

        return subjectAN;
    }

    /**
     * Adds a {@link DeviceKeystoreSubjectAN} to {@link #getSubjectAN()}.
     *
     * @param subjectAN
     *         The {@link DeviceKeystoreSubjectAN} to add.
     * @since 1.5.0
     */
    public void addSubjectAN(DeviceKeystoreSubjectAN subjectAN) {
        getSubjectAN().add(subjectAN);
    }

    /**
     * Sets the {@link List} of {@link DeviceKeystoreSubjectAN}s.
     *
     * @param subjectAN
     *         The {@link List} of {@link DeviceKeystoreSubjectAN}s.
     * @since 1.5.0
     */
    public void setSubjectAN(List<DeviceKeystoreSubjectAN> subjectAN) {
        this.subjectAN = subjectAN;
    }

    /**
     * Gets the issuer.
     *
     * @return The issuer.
     * @since 1.5.0
     */
    @XmlElement(name = "issuer")
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the issuer.
     *
     * @param issuer
     *         The issuer.
     * @since 1.5.0
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Gets the not before {@link Date}.
     *
     * @return The not before {@link Date}.
     * @since 1.5.0
     */
    @XmlElement(name = "notBefore")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getNotBefore() {
        return notBefore;
    }

    /**
     * Sets the not before {@link Date}.
     *
     * @param notBefore
     *         The not before {@link Date}.
     * @since 1.5.0
     */
    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    /**
     * Gets the not after {@link Date}.
     *
     * @return The not after {@link Date}.
     * @since 1.5.0
     */
    @XmlElement(name = "notAfter")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getNotAfter() {
        return notAfter;
    }

    /**
     * Sets the not after {@link Date}.
     *
     * @param notAfter
     *         The not after {@link Date}.
     * @since 1.5.0
     */
    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    /**
     * Gets the certificate.
     *
     * @return The certificate.
     * @since 1.5.0
     */
    @XmlElement(name = "certificate")
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets the certificate.
     *
     * @param certificate
     *         The certificate.
     * @since 1.5.0
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    /**
     * Gets the certificate chain.
     *
     * @return The certificate chain.
     * @since 1.5.0
     */
    @XmlElement(name = "certificateChain")
    public List<String> getCertificateChain() {
        if (certificateChain == null) {
            certificateChain = new ArrayList<>();
        }

        return certificateChain;
    }

    /**
     * Adds a certificate to the {@link #getCertificateChain()}.
     *
     * @param certificate
     *         The certificate to add.
     * @since 1.5.0
     */
    public void addCertificateChain(String certificate) {
        getCertificateChain().add(certificate);
    }

    /**
     * Sets the certificate chain.
     *
     * @param certificateChain
     *         The certificate chain.
     * @since 1.5.0
     */
    public void setCertificateChain(List<String> certificateChain) {
        this.certificateChain = certificateChain;
    }

}
