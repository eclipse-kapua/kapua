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
package org.eclipse.kapua.service.authentication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.authentication.token.AccessToken;

/**
 * {@link AccessToken#getRefreshToken()} {@link LoginCredentials} definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "refreshTokenCredentials")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class RefreshTokenCredentials implements LoginCredentials {

    private String tokenId;
    private String refreshToken;

    public RefreshTokenCredentials() {
    }

    /**
     * Constructor.
     *
     * @param tokenId
     *         The {@link AccessToken#getTokenId()}
     * @param refreshToken
     *         TThe {@link AccessToken#getRefreshToken()}
     * @since 1.0.0
     */
    public RefreshTokenCredentials(String tokenId, String refreshToken) {
        this.tokenId = tokenId;
        this.refreshToken = refreshToken;
    }

    /**
     * Gets the {@link AccessToken#getTokenId()} to authenticate.
     *
     * @return AccessToken The {@link AccessToken#getTokenId()} to authenticate.
     * @since 1.0.0
     */
    @XmlElement(name = "tokenId")
    public String getTokenId() {
        return tokenId;
    }

    /**
     * Set the {@link AccessToken#getTokenId()} to authenticate.
     *
     * @param tokenId
     *         The {@link AccessToken#getTokenId()} to authenticate.
     * @since 1.0.0
     */
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * Gets the AccessToken#getRefreshToken()} to authenticate.
     *
     * @return The {@link AccessToken#getRefreshToken()} to authenticate.
     * @since 1.0.0
     */
    @XmlElement(name = "refreshToken")
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Set the {@link AccessToken#getRefreshToken()} to authenticate.
     *
     * @param refreshToken
     *         The AccessToken#getRefreshToken()} to authenticate.
     * @since 1.0.0
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
