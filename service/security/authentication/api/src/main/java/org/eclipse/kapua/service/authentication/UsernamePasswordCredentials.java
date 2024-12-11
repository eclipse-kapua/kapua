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

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Username and password {@link LoginCredentials} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "usernamePasswordCredentials")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = AuthenticationXmlRegistry.class, factoryMethod = "newUsernamePasswordCredentials")
public interface UsernamePasswordCredentials extends LoginCredentials {

    /**
     * Gets the username.
     *
     * @return The username.
     * @since 1.0.0
     */
    @Schema(description = "The credential username.", example = "kapua-sys", defaultValue = "kapua-sys")
    String getUsername();

    /**
     * Sets the username.
     *
     * @param username The username.
     * @since 1.0.0
     */
    void setUsername(String username);

    /**
     * Gets the password.
     *
     * @return The password.
     * @since 1.0.0
     */
    @Schema(description = "The credential password.", example = "kapua-password", defaultValue = "kapua-password")
    String getPassword();

    /**
     * Sets the password.
     *
     * @param password The password.
     * @since 1.0.0
     */
    void setPassword(String password);

    /**
     * Gets the MFA authentication code.
     *
     * @return The MFA authentication code.
     * @since 1.3.0
     */
    @Schema(description = "Optional authentication code.", example = "123456", hidden = true)
    String getAuthenticationCode();

    /**
     * Sets the MFA authentication code.
     *
     * @param authenticationCode The MFA authentication code.
     * @since 1.3.0
     */
    void setAuthenticationCode(String authenticationCode);

    /**
     * Gets the trust key.
     *
     * @return The trust key.
     * @since 1.3.0
     */
    @Schema(description = "Optional trust key.", example = "trust-key-abc", hidden = true)
    String getTrustKey();

    /**
     * Sets the trust key.
     *
     * @param trustKey The trust key.
     * @since 1.3.0
     */
    void setTrustKey(String trustKey);

    /**
     * Gets whether create a trust key or not.
     *
     * @return {@code true} if to be created, {@code false} otherwise
     * @since 2.0.0
     */
    @Schema(description = "Indicates if the trust mechanism is enabled.", hidden = true, example = "false")
    boolean getTrustMe();

    /**
     * Sets whether create a trust key or not.
     *
     * @param trustMe {@code true} if to be created, {@code false} if not.
     * @since 2.0.0
     */
    void setTrustMe(boolean trustMe);
}
