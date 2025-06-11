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
public class UsernamePasswordCredentialsImpl implements UsernamePasswordCredentials, KapuaAuthenticationToken {

    private static final long serialVersionUID = -7549848672967689716L;

    private String username;
    private String password;
    private String authenticationCode;
    private String trustKey;
    private boolean trustMe;

    /**
     * Constructor.
     *
     * @param username The credential username.
     * @param password The credential password.
     * @since 1.0.0
     */
    public UsernamePasswordCredentialsImpl(@NotNull String username, @NotNull String password) {
        setUsername(username);
        setPassword(password);
    }

    /**
     * Clone constructor.
     *
     * @param usernamePasswordCredentials The {@link UsernamePasswordCredentials} to clone.
     * @since 1.5.0
     */
    public UsernamePasswordCredentialsImpl(@NotNull UsernamePasswordCredentials usernamePasswordCredentials) {
        setUsername(usernamePasswordCredentials.getUsername());
        setPassword(usernamePasswordCredentials.getPassword());
        setAuthenticationCode(usernamePasswordCredentials.getAuthenticationCode());
        setTrustKey(usernamePasswordCredentials.getTrustKey());
        setTrustMe(usernamePasswordCredentials.getTrustMe());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public String getAuthenticationCode() {
        return authenticationCode;
    }

    @Override
    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    @Override
    public String getTrustKey() {
        return trustKey;
    }

    @Override
    public void setTrustKey(String trustKey) {
        this.trustKey = trustKey;
    }

    @Override
    public boolean getTrustMe() {
        return trustMe;
    }

    @Override
    public void setTrustMe(boolean trustMe) {
        this.trustMe = trustMe;
    }

    /**
     * Parses a {@link UsernamePasswordCredentials} into a {@link UsernamePasswordCredentialsImpl}.
     *
     * @param usernamePasswordCredentials The {@link UsernamePasswordCredentials} to parse.
     * @return An instance of {@link UsernamePasswordCredentialsImpl}.
     * @since 1.5.0
     */
    public static UsernamePasswordCredentialsImpl parse(@Nullable UsernamePasswordCredentials usernamePasswordCredentials) {
        return usernamePasswordCredentials != null ?
                (usernamePasswordCredentials instanceof UsernamePasswordCredentialsImpl ?
                        (UsernamePasswordCredentialsImpl) usernamePasswordCredentials :
                        new UsernamePasswordCredentialsImpl(usernamePasswordCredentials))
                : null;
    }

    @Override
    public Optional<String> getOpenIdToken() {
        return Optional.empty();
    }
}
