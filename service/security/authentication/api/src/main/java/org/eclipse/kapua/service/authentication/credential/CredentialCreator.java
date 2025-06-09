/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.credential;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.user.User;

/**
 * {@link Credential} {@link KapuaEntityCreator}
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "credentialCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class CredentialCreator extends KapuaEntityCreator {

    private static final long serialVersionUID = -5020680413729882095L;

    private KapuaId userId;
    private String credentialType;
    private String credentialKey;
    private Date expirationDate;
    private CredentialStatus credentialStatus;

    public CredentialCreator() {
    }

    public CredentialCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public CredentialCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The {@link CredentialCreator#getScopeId()}
     * @param userId
     *         The {@link CredentialCreator#getUserId()}
     * @param credentialType
     *         The {@link CredentialCreator#getCredentialType()}
     * @param credentialKey
     *         The plain {@link CredentialCreator#getCredentialPlainKey()}
     * @param credentialStatus
     *         The {@link CredentialCreator#getCredentialStatus()}
     * @param expirationDate
     *         The {@link CredentialCreator#getExpirationDate()}
     * @since 1.0.0
     */
    public CredentialCreator(KapuaId scopeId,
            KapuaId userId,
            String credentialType,
            String credentialKey,
            CredentialStatus credentialStatus,
            Date expirationDate) {
        super(scopeId);

        this.userId = userId;
        this.credentialType = credentialType;
        this.credentialKey = credentialKey;
        this.credentialStatus = credentialStatus;
        this.expirationDate = expirationDate;
    }

    /**
     * Gets the {@link User#getId()} owner of the {@link Credential}
     *
     * @return The {@link User#getId()}
     * @since 1.0.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getUserId() {
        return userId;
    }

    /**
     * Sets the {@link User#getId()} owner of the {@link Credential}
     *
     * @param userId
     *         The {@link User#getId()}
     * @since 1.0.0
     */
    public void setUserId(KapuaId userId) {
        this.userId = userId;
    }

    /**
     * Gets the type.
     *
     * @return The type.
     * @since 1.0.0
     */
    @XmlElement(name = "credentialType")
    public String getCredentialType() {
        return credentialType;
    }

    /**
     * Sets the type.
     *
     * @param credentialType
     *         The type.
     * @since 1.0.0
     */
    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    /**
     * Gets the plain secret key
     *
     * @return The plain secret key
     * @since 1.0.0
     */
    @XmlElement(name = "credentialKey")
    public String getCredentialPlainKey() {
        return credentialKey;
    }

    /**
     * Set the plain secret key
     *
     * @param plainKey
     *         The plain secret key
     * @since 1.0.0
     */
    public void setCredentialPlainKey(String plainKey) {
        this.credentialKey = plainKey;
    }

    /**
     * Gets the expiration date
     *
     * @return The expiration date
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expiration date
     *
     * @param expirationDate
     *         The expiration date
     * @since 1.0.0
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Gets the {@link CredentialStatus}
     *
     * @return The {@link CredentialStatus}
     * @since 1.0.0
     */
    @XmlElement(name = "credentialStatus")
    public CredentialStatus getCredentialStatus() {
        return credentialStatus;
    }

    /**
     * Sets the {@link CredentialStatus}
     *
     * @param credentialStatus
     *         The {@link CredentialStatus}
     * @since 1.0.0
     */
    public void setCredentialStatus(CredentialStatus credentialStatus) {
        this.credentialStatus = credentialStatus;
    }
}
