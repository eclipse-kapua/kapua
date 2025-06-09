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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Username and password {@link LoginCredentials} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "usernamePasswordCredentials")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class UsernamePasswordCredentials implements LoginCredentials {

    private static final long serialVersionUID = -7549848672967689716L;

    private String username;
    private String password;
    private String authenticationCode;
    private String trustKey;
    private boolean trustMe;

    public UsernamePasswordCredentials() {
    }

    public UsernamePasswordCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the username.
     *
     * @return The username.
     * @since 1.0.0
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username
     *         The username.
     * @since 1.0.0
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return The password.
     * @since 1.0.0
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *         The password.
     * @since 1.0.0
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the MFA authentication code.
     *
     * @return The MFA authentication code.
     * @since 1.3.0
     */
    public String getAuthenticationCode() {
        return authenticationCode;
    }

    /**
     * Sets the MFA authentication code.
     *
     * @param authenticationCode
     *         The MFA authentication code.
     * @since 1.3.0
     */
    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    /**
     * Gets the trust key.
     *
     * @return The trust key.
     * @since 1.3.0
     */
    public String getTrustKey() {
        return trustKey;
    }

    /**
     * Sets the trust key.
     *
     * @param trustKey
     *         The trust key.
     * @since 1.3.0
     */
    public void setTrustKey(String trustKey) {
        this.trustKey = trustKey;
    }

    /**
     * Gets whether create a trust key or not.
     *
     * @return {@code true} if to be created, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean getTrustMe() {
        return trustMe;
    }

    /**
     * Sets whether create a trust key or not.
     *
     * @param trustMe
     *         {@code true} if to be created, {@code false} if not.
     * @since 2.0.0
     */
    public void setTrustMe(boolean trustMe) {
        this.trustMe = trustMe;
    }

}
