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
package org.eclipse.kapua.service.authentication.token;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link AccessToken} entity.
 *
 * @since 1.0.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { //
        "tokenId", //
        "userId", //
        "expiresOn", //
        "refreshToken", //
        "refreshExpiresOn", //
        "invalidatedOn", //
        "trustKey"  //
}, //
        factoryClass = AccessTokenXmlRegistry.class, //
        factoryMethod = "newAccessToken")
@Schema(
    name = "AccessToken",
    description = "Represents an authentication token with details such as expiry, refresh, and user information."
)
public interface AccessToken extends KapuaUpdatableEntity, Serializable {

    String TYPE = "accessToken";

    @Override
    default String getType() {
        return TYPE;
    }

    /**
     * Return the token identifier
     * This represents the content of the JWT token
     *
     * @return the token identifier
     * @since 1.0.0
     */
    @XmlElement(name = "tokenId")
    @Schema(description = "Unique identifier of the user associated with this token.",
        example = "S6=enLL9P1OcwJNFDjme.FhOsKfDxOxxfb87c_RLkChyTpesYTLfnRWXfZlXmu=yN7EDT3LGxQiZ1KeztF2F8.bYPI_grbdsES8wkzki4T52d0-")
    String getTokenId();

    /**
     * Sets the token id
     *
     * @param tokenId The token id.
     * @since 1.0.0
     */
    void setTokenId(String tokenId);

    /**
     * Return the user identifier
     *
     * @return The user identifier.
     * @since 1.0.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(description = "Unique identifier of the user associated with this token.", type = "string",
        example = "QwrHirFy3OMS849giOq7fglzI-aHneW_NFoBgiUChO_csBO927AeP6q1ZC5bD0HcgVH8OcvhY")
    KapuaId getUserId();

    /**
     * Sets the {@link User} id of this {@link AccessToken}
     *
     * @param userId The {@link User} id to set.
     * @since 1.0.0
     */
    void setUserId(KapuaId userId);

    /**
     * Gets the expire date of this token.
     *
     * @return The expire date of this token.
     * @since 1.0.0
     */
    @XmlElement(name = "expiresOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    @Schema(description = "The date and time when the token will expire.",
        example = "2024-11-21T08:51:44.355Z")
    Date getExpiresOn();

    /**
     * Sets the expire date of this token.
     *
     * @param expiresOn The expire date of this token.
     * @since 1.0.0
     */
    void setExpiresOn(Date expiresOn);

    /**
     * Gets the refresh token to obtain a new {@link AccessToken} after expiration.
     *
     * @return The refresh token to obtain a new {@link AccessToken} after expiration.
     * @since 1.0.0
     */
    @XmlElement(name = "refreshToken")
    @Schema(description = "The refresh token used to obtain a new access token.",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    String getRefreshToken();

    /**
     * Sets the refresh token to obtain a new {@link AccessToken} after expiration.
     *
     * @param refreshToken The refresh token
     * @since 1.0.0
     */
    void setRefreshToken(String refreshToken);

    /**
     * Gets the expiration date of the refresh token.
     *
     * @return The expiration date of the refresh token.
     * @since 1.0.0
     */
    @XmlElement(name = "refreshExpiresOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    @Schema(description = "The date and time when the refresh token will expire.",
        example = "2024-11-21T08:51:44.355Z")
    Date getRefreshExpiresOn();

    /**
     * Sets the expire date of this token.
     *
     * @param refreshExpiresOn The expiration date of the refresh token.
     * @since 1.0.0
     */
    void setRefreshExpiresOn(Date refreshExpiresOn);

    /**
     * Gets the date the token has been invalidated (i.e. the date
     * the refresh token has been used, or it has been invalidated due
     * to a logout)
     *
     * @return The date the token has been invalidated.
     * @since 1.0.0
     */
    @XmlElement(name = "invalidatedOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    @Schema(description = "The date and time when the token was invalidated, if applicable.",
        example = "2024-11-21T08:51:44.355Z", nullable = true)
    Date getInvalidatedOn();

    /**
     * Sets the date the token has been invalidated (i.e. the date
     * the refresh token has been used, or it has been invalidated due
     * to a logout)
     *
     * @param invalidatedOn The date when the token has been invalidated.
     * @since 1.0.0
     */
    void setInvalidatedOn(Date invalidatedOn);

    /**
     * Gets the MFA trust key
     *
     * @return the value of the mfa trust key
     * @since 1.4.0
     */
    @XmlElement(name = "trustKey")
    @Schema(description = "The trust key associated with this token, used for additional security.",
        example = "wnMaCRrbFBGOA9mibCNd=.5jHQ4igCusVIXbsJsNUO7kii2jidtg0L-0jt7ElE2A3OKq1t3=qW5.2KQyKXoYSgkC+eU3mPFnKr.fMMNa6fY+bt6=FJWmF.=29r5kk9hqKXlAKAy")
    String getTrustKey();

    /**
     * Sets the MFA trust key
     *
     * @param trustKey the mfa trust key to be set
     * @since 1.4.0
     */
    void setTrustKey(String trustKey);

    /**
     * Gets the token identifier
     * This represents an id for the JWT token and is meant to be inserted inside its payload
     *
     * @return The token id
     * @since 2.0
     */
    @XmlTransient
    @Schema(description = "A unique identifier for the token.",
        example = "4YlC3tt_R3")
    String getTokenIdentifier();

    /**
     * Sets the token identifier
     *
     * @param tokenId the token id to set
     * @since 2.0
     */
    void setTokenIdentifier(String tokenId);

}
