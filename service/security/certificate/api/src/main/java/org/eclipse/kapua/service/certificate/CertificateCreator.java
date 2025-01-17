/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.certificate;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

/**
 * {@link Certificate} {@link org.eclipse.kapua.model.KapuaEntityCreator}encapsulates all the information needed to create a new {@link Certificate} in the system.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "certificateCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class CertificateCreator extends KapuaNamedEntityCreator<Certificate> {

    private String certificate;
    private CertificateStatus status;
    private String privateKey;
    private KapuaId caId;
    private String password;
    private Set<CertificateUsage> certificateUsages;
    private Boolean forwardable;

    public CertificateCreator() {
    }

    public CertificateCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public CertificateCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }

    @XmlElement(name = "certificate")
    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @XmlElement(name = "status")
    public CertificateStatus getStatus() {
        //Kind hackish, but the database does not allow nulls here.
        //default value was previously set in the constructor, but that only works if the field is not present in the "dto" (request payload for rest apis), and not when the field is passed as null, and changing the field to be required would break back-compatibility.
        return status == null ? CertificateStatus.VALID : status;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    @XmlElement(name = "privateKey")
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @XmlElement(name = "caId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getCaId() {
        return caId;
    }

    public void setCaId(KapuaId caId) {
        this.caId = caId;
    }

    @XmlElement(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElementWrapper(name = "certificateUsages")
    @XmlElement(name = "certificateUsage")
    public Set<CertificateUsage> getCertificateUsages() {
        return certificateUsages;
    }

    public void setCertificateUsages(Set<CertificateUsage> set) {
        Set<CertificateUsage> newSet = new HashSet<>();
        for (CertificateUsage certificateUsage : set) {
            newSet.add(certificateUsage);
        }
        certificateUsages = newSet;
    }

    @XmlElement(name = "forwardable")
    public Boolean getForwardable() {
        return forwardable;
    }

    public void setForwardable(Boolean forwardable) {
        this.forwardable = forwardable;
    }

}
