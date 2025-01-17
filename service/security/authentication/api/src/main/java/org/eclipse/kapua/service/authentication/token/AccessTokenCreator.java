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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * Access token creator service definition
 *
 * @since 1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "tokenId",
        "userId",
        "expiresOn",
        "refreshToken",
        "refreshExpiresOn",
        "tokenIdentifier"
})
public class AccessTokenCreator extends KapuaEntityCreator<AccessToken> {

    private static final long serialVersionUID = -27718046815190710L;

    private String tokenId;
    private KapuaId userId;
    private Date expiresOn;
    private String refreshToken;
    private Date refreshExpiresOn;
    private String tokenIdentifier;

    public AccessTokenCreator() {
    }

    public AccessTokenCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public AccessTokenCreator(KapuaEntityCreator<AccessToken> entityCreator) {
        super(entityCreator);
    }

    /**
     * Instantiates a new {@link AccessTokenCreator}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to set into the{@link AccessToken}.
     * @param userId
     *         The {@link org.eclipse.kapua.service.user.User} {@link KapuaId} to set into the{@link AccessToken}.
     * @param tokenId
     *         The token id to set into the{@link AccessToken}.
     * @param expiresOn
     *         The expiration date to set into the{@link AccessToken}.
     * @param refreshToken
     *         The refresh token to set into the{@link AccessToken}.
     * @since 1.0.0
     */
    public AccessTokenCreator(
            KapuaId scopeId,
            KapuaId userId,
            String tokenId,
            Date expiresOn,
            String refreshToken,
            Date refreshExpiresOn,
            String tokenIdentifier) {
        super(scopeId);
        this.userId = userId;
        this.tokenId = tokenId;
        this.expiresOn = expiresOn;
        this.refreshToken = refreshToken;
        this.refreshExpiresOn = refreshExpiresOn;
        this.tokenIdentifier = tokenIdentifier;
    }

    /**
     * Gets the token id This represents the content of the JWT token
     *
     * @return The token id
     * @since 1.0
     */
    @XmlElement(name = "tokenId")
    public String getTokenId() {
        return tokenId;
    }

    /**
     * Sets the token id
     *
     * @param tokenId
     *         the token id to set
     * @since 1.0
     */
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * Gets the user id owner of this token
     *
     * @return The user id owner of this token
     * @since 1.0
     */
    @XmlElement(name = "userId")
    public KapuaId getUserId() {
        return userId;
    }

    /**
     * Sets the user id owner of this token.
     *
     * @param userId
     *         The user id owner of this token.
     * @since 1.0
     */
    public void setUserId(KapuaId userId) {
        this.userId = userId;
    }

    /**
     * Gets the expire date of this token.
     *
     * @return The expire date of this token.
     * @since 1.0
     */
    @XmlElement(name = "expiresOn")
    public Date getExpiresOn() {
        return expiresOn;
    }

    /**
     * Sets the expire date of this token.
     *
     * @param expiresOn
     *         The expire date of this token.
     * @since 1.0
     */
    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

    /**
     * Gets the refresh token to obtain a new {@link AccessToken} after expiration.
     *
     * @since 1.0
     */
    @XmlElement(name = "refreshToken")
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets the refresh token to obtain a new {@link AccessToken} after expiration.
     *
     * @param refreshToken
     *         The refresh token
     * @since 1.0
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Gets the expiration date of the refresh token.
     *
     * @since 1.0
     */
    @XmlElement(name = "refreshExpiresOn")
    public Date getRefreshExpiresOn() {
        return refreshExpiresOn;
    }

    /**
     * Sets the expire date of this token.
     *
     * @param refreshExpiresOn
     *         The expiration date of the refresh token.
     * @since 1.0
     */
    public void setRefreshExpiresOn(Date refreshExpiresOn) {
        this.refreshExpiresOn = refreshExpiresOn;
    }

    /**
     * Gets the token identifier This represents an id for the JWT token and is meant to be inserted inside its payload
     *
     * @return The token id
     * @since 2.0
     */
    @XmlElement(name = "tokenIdentifier")
    public String getTokenIdentifier() {
        return tokenIdentifier;
    }

    /**
     * Sets the token identifier
     *
     * @param tokenId
     *         the token id to set
     * @since 2.0
     */
    public void setTokenIdentifier(String tokenId) {
        this.tokenIdentifier = tokenId;
    }

}
