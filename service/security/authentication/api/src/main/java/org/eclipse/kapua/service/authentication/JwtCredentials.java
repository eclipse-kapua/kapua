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

/**
 * JWT {@link LoginCredentials} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jwtCredentials")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JwtCredentials implements LoginCredentials {

    private static final long serialVersionUID = -5920944517814926028L;

    private String accessToken;
    private String idToken;

    public JwtCredentials() {
    }

    /**
     * Constructor.
     *
     * @param accessToken
     *         The credential access token
     * @param idToken
     *         The credential id token.
     * @since 1.4.0
     */
    public JwtCredentials(String accessToken, String idToken) {
        setAccessToken(accessToken);
        setIdToken(idToken);
    }

    /**
     * Gets the OpenID Connect <a href="https://auth0.com/blog/id-token-access-token-what-is-the-difference/#What-Is-an-Access-Token">accessToken</a>.
     *
     * @return The OpenID Connect accessToken.
     * @since 1.3.0
     */
    @XmlElement(name = "accessToken")
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Set the OpenID Connect accessToken.
     *
     * @param accessToken
     *         The OpenID Connect accessToken.
     * @since 1.3.0
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets the OpenID Connect <a href="https://auth0.com/blog/id-token-access-token-what-is-the-difference/#What-Is-an-ID-Token">idToken</a>.
     *
     * @return The OpenID Connect idToken.
     * @since 1.3.0
     */
    @XmlElement(name = "idToken")
    public String getIdToken() {
        return idToken;
    }

    /**
     * Set the OpenID Connect idToken.
     *
     * @param idToken
     *         The OpenID Connect idToken.
     * @since 1.3.0
     */
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

}
