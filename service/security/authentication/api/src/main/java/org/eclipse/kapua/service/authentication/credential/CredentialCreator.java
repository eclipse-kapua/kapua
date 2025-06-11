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
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Credential} {@link KapuaEntityCreator}
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "credentialCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = CredentialXmlRegistry.class, factoryMethod = "newCredentialCreator")
@Schema(description = "An object containing the properties for the new Credential to be created")
public interface CredentialCreator extends KapuaEntityCreator<Credential> {

    /**
     * Gets the {@link User#getId()} owner of the {@link Credential}
     *
     * @return The {@link User#getId()}
     * @since 1.0.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "AQ")
    KapuaId getUserId();

    /**
     * Sets the {@link User#getId()} owner of the {@link Credential}
     *
     * @param userId The {@link User#getId()}
     * @since 1.0.0
     */
    void setUserId(KapuaId userId);

    /**
     * Gets the type.
     *
     * @return The type.
     * @since 1.0.0
     */
    @XmlElement(name = "credentialType")
    @Schema(example = "PASSWORD")
    String getCredentialType();

    /**
     * Sets the type.
     *
     * @param credentialType The type.
     * @since 1.0.0
     */
    void setCredentialType(String credentialType);

    /**
     * Gets the plain secret key
     *
     * @return The plain secret key
     * @since 1.0.0
     */
    @XmlElement(name = "credentialKey")
    String getCredentialPlainKey();

    /**
     * Set the plain secret key
     *
     * @param plainKey The plain secret key
     * @since 1.0.0
     */
    void setCredentialPlainKey(String plainKey);

    /**
     * Gets the expiration date
     *
     * @return The expiration date
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getExpirationDate();

    /**
     * Sets the expiration date
     *
     * @param expirationDate The expiration date
     * @since 1.0.0
     */
    void setExpirationDate(Date expirationDate);

    /**
     * Gets the {@link CredentialStatus}
     *
     * @return The {@link CredentialStatus}
     * @since 1.0.0
     */
    @XmlElement(name = "credentialStatus")
    @Schema(example = "ENABLED")
    CredentialStatus getCredentialStatus();

    /**
     * Sets the {@link CredentialStatus}
     *
     * @param credentialStatus The {@link CredentialStatus}
     * @since 1.0.0
     */
    void setCredentialStatus(CredentialStatus credentialStatus);
}
