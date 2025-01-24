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
package org.eclipse.kapua.service.authentication.shiro;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.shiro.realm.KapuaAuthenticationToken;

/**
 * {@link UsernamePasswordCredentials} implementation.
 * <p>
 * This implements also {@link KapuaAuthenticationToken} to allow usage in Apache Shiro.
 *
 * @since 1.0.0
 */
public class UsernamePasswordCredentialsImpl implements KapuaAuthenticationToken {

    private static final long serialVersionUID = -7549848672967689716L;

    private String username;
    private String password;
    private String authenticationCode;
    private String trustKey;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    public String getTrustKey() {
        return trustKey;
    }

    /**
     * Constructor.
     *
     * @param username
     *         The credential username.
     * @param password
     *         The credential password.
     * @since 1.0.0
     */
    public UsernamePasswordCredentialsImpl(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
        this.authenticationCode = null;
        this.trustKey = null;
    }

    /**
     * Clone constructor.
     *
     * @param usernamePasswordCredentials
     *         The {@link UsernamePasswordCredentials} to clone.
     * @since 1.5.0
     */
    public UsernamePasswordCredentialsImpl(@NotNull UsernamePasswordCredentials usernamePasswordCredentials) {
        this.username = usernamePasswordCredentials.getUsername();
        this.password = usernamePasswordCredentials.getPassword();
        this.authenticationCode = usernamePasswordCredentials.getAuthenticationCode();
        this.trustKey = usernamePasswordCredentials.getTrustKey();
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    /**
     * Parses a {@link UsernamePasswordCredentials} into a {@link UsernamePasswordCredentialsImpl}.
     *
     * @param usernamePasswordCredentials
     *         The {@link UsernamePasswordCredentials} to parse.
     * @return An instance of {@link UsernamePasswordCredentialsImpl}.
     * @since 1.5.0
     */
    public static UsernamePasswordCredentialsImpl parse(@Nullable UsernamePasswordCredentials usernamePasswordCredentials) {
        return usernamePasswordCredentials != null ?
                new UsernamePasswordCredentialsImpl(usernamePasswordCredentials)
                : null;
    }

    @Override
    public Optional<String> getOpenIdToken() {
        return Optional.empty();
    }
}
