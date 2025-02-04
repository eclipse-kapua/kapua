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
package org.eclipse.kapua.service.user;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.xml.DateXmlAdapter;

/**
 * {@link UserCreator} {@link org.eclipse.kapua.model.KapuaEntityCreator} definition
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "userCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class UserCreator extends KapuaNamedEntityCreator {

    private static final long serialVersionUID = 4664940282892151008L;

    private UserStatus status;
    private String displayName;
    private String email;
    private String phoneNumber;
    private UserType userType = UserType.INTERNAL;
    private String externalId;
    private String externalUsername;
    private Date expirationDate;

    public UserCreator() {
        this(null, null);
    }

    public UserCreator(KapuaId scopeId) {
        this(scopeId, null);
    }

    /**
     * Constructor
     *
     * @param accountId
     * @param name
     */
    public UserCreator(KapuaId accountId, String name) {
        super(accountId, name);
        setStatus(UserStatus.ENABLED);
        setUserType(UserType.INTERNAL);
    }

    /**
     * Return the display name (may be a friendly username to show in the UI)
     *
     * @return
     */
    @XmlElement(name = "displayName")
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the display name
     *
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the email
     *
     * @return
     */
    @XmlElement(name = "email")
    public String getEmail() {
        return email;
    }

    /**
     * Set the email
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the phone number
     *
     * @return
     */
    @XmlElement(name = "phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set the phone number
     *
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get the user type
     *
     * @return
     */
    @XmlElement(name = "userType")
    public UserType getUserType() {
        return userType;
    }

    /**
     * Set the user type
     *
     * @param userType
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Get the external ID
     *
     * @return
     */
    @XmlElement(name = "externalId")
    public String getExternalId() {
        return externalId;
    }

    /**
     * Set the external ID
     *
     * @param externalId
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Gets the external username.
     *
     * @return The external username.
     * @since 2.0.0
     */
    @XmlElement(name = "externalUsername")
    public String getExternalUsername() {
        return externalUsername;
    }

    /**
     * Sets the external username.
     *
     * @param externalUsername
     *         The external username.
     * @since 2.0.0
     */
    public void setExternalUsername(String externalUsername) {
        this.externalUsername = externalUsername;
    }

    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @XmlElement(name = "status")
    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
