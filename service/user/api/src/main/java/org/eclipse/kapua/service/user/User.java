/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.Set;

/**
 * {@link User} {@link KapuaEntity} definition.
 *
 * @since 1.0.0
 * @see KapuaEntity
 */
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = UserXmlRegistry.class, factoryMethod = "newUser")
public interface User extends KapuaNamedEntity {

    String TYPE = "user";

    @Override
    default String getType() {
        return TYPE;
    }

    /**
     * Gets the set of Tag id assigned to this entity.
     *
     * @return The set Tag id assigned to this entity.
     * @since 2.1.0
     */
    @XmlElementWrapper(name = "tagIds")
    @XmlElement(name = "tagId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    Set<KapuaId> getTagIds();

    /**
     * Sets the set of Tag id of this entity.
     *
     * @param tagIds The set Tag id to assign.
     * @since 2.1.0
     */
    void setTagIds(Set<KapuaId> tagIds);

    /**
     * Gets the {@link UserStatus}
     *
     * @return the {@link UserStatus}
     * @since 1.0.0
     */
    @XmlElement(name = "status")
    UserStatus getStatus();

    /**
     * Sets the {@link UserStatus}
     *
     * @param status the {@link UserStatus}
     * @since 1.0.0
     */
    void setStatus(UserStatus status);

    /**
     * Gets the display name (may be a friendlier name)
     *
     * @return the display name (may be a friendlier name)
     * @since 1.0.0
     */
    @XmlElement(name = "displayName")
    String getDisplayName();

    /**
     * Sets the display name (may be a friendlier name)
     *
     * @param displayName the display name (may be a friendlier name)
     * @since 1.0.0
     */
    void setDisplayName(String displayName);

    /**
     * Gets the email
     *
     * @return the email
     * @since 1.0.0
     */
    @XmlElement(name = "email")
    String getEmail();

    /**
     * Sets the user email
     *
     * @param email the user email
     * @since 1.0.0
     */
    void setEmail(String email);

    /**
     * Gets the phone number
     *
     * @return the phone number
     * @since 1.0.0
     */
    @XmlElement(name = "phoneNumber")
    String getPhoneNumber();

    /**
     * Sets the phone number
     *
     * @param phoneNumber the phone number
     * @since 1.0.0
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * Gets the {@link UserType}
     *
     * @return the {@link UserType}
     * @since 1.0.0
     */
    @XmlElement(name = "userType")
    UserType getUserType();

    /**
     * Sets the user type
     *
     * @param userType the {@link UserType}
     * @since 1.0.0
     */
    void setUserType(UserType userType);

    /**
     * Gets the external id.
     * <p>
     * This field is used to store external SSO identity bound to this {@link User}
     *
     * @return the external id.
     * @since 1.0.0
     */
    @XmlElement(name = "externalId")
    String getExternalId();

    /**
     * Sets the external id.
     * <p>
     * This field is used to store external SSO identity bound to this {@link User}
     *
     * @param externalId the external id.
     * @since 1.0.0
     */
    void setExternalId(String externalId);

    /**
     * Gets the external username.
     *
     * @return The external username.
     * @since 2.0.0
     */
    @XmlElement(name = "externalUsername")
    String getExternalUsername();

    /**
     * Sets the external username.
     *
     * @param externalUsername The external username.
     * @since 2.0.0
     */
    void setExternalUsername(String externalUsername);

    /**
     * Gets the expiration date
     *
     * @return the expiration date
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getExpirationDate();

    /**
     * Sets the expiration date
     *
     * @param expirationDate the expiration date
     * @since 1.0.0
     */
    void setExpirationDate(Date expirationDate);
}
