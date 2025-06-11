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
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link UserCreator} {@link org.eclipse.kapua.model.KapuaEntityCreator} definition
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "userCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = UserXmlRegistry.class, factoryMethod = "newUserCreator")
@Schema(description = "An object containing the properties for the new User to be created")
public interface UserCreator extends KapuaNamedEntityCreator<User> {

    /**
     * Return the display name (may be a friendly username to show in the UI)
     *
     * @return
     */
    @XmlElement(name = "displayName")
    @Schema(example = "new-user")
    String getDisplayName();

    /**
     * Set the display name
     *
     * @param displayName
     */
    void setDisplayName(String displayName);

    /**
     * Get the email
     *
     * @return
     */
    @XmlElement(name = "email")
    @Schema(example = "foo@bar.com")
    String getEmail();

    /**
     * Set the email
     *
     * @param email
     */
    void setEmail(String email);

    /**
     * Get the phone number
     *
     * @return
     */
    @XmlElement(name = "phoneNumber")
    @Schema(example = "424202424")
    String getPhoneNumber();

    /**
     * Set the phone number
     *
     * @param phoneNumber
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * Get the user type
     *
     * @return
     */
    @XmlElement(name = "userType")
    UserType getUserType();

    /**
     * Set the user type
     *
     * @param userType
     */
    void setUserType(UserType userType);

    /**
     * Get the external ID
     *
     * @return
     */
    @XmlElement(name = "externalId")
    String getExternalId();

    /**
     * Set the external ID
     *
     * @param externalId
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


    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getExpirationDate();

    void setExpirationDate(Date expirationDate);

    @XmlElement(name = "status")
    UserStatus getStatus();

    void setStatus(UserStatus status);
}
