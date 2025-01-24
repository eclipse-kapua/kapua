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

import org.eclipse.kapua.service.authentication.SessionCredentials;
import org.eclipse.kapua.service.authentication.shiro.realm.KapuaAuthenticationToken;

/**
 * {@link AccessTokenCredentialsImpl} implementation.
 * <p>
 * This implements also {@link KapuaAuthenticationToken} to allow usage in Apache Shiro.
 *
 * @since 1.0.0
 */
public class AccessTokenCredentialsImpl implements KapuaAuthenticationToken, SessionCredentials {

    private static final long serialVersionUID = -7549848672967689716L;

    private String tokenId;

    /**
     * Constructor.
     *
     * @param tokenId
     *         The credential JWT
     * @since 1.0.0
     */
    public AccessTokenCredentialsImpl(@NotNull String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * Clone constructor.
     *
     * @param accessTokenCredentials
     *         The {@link AccessTokenCredentialsImpl} to clone.
     * @since 1.5.0
     */
    public AccessTokenCredentialsImpl(@NotNull AccessTokenCredentialsImpl accessTokenCredentials) {
        this.tokenId = accessTokenCredentials.getTokenId();
    }

    public String getTokenId() {
        return tokenId;
    }

    public AccessTokenCredentialsImpl setTokenId(String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    @Override
    public Object getPrincipal() {
        return tokenId;
    }

    @Override
    public Object getCredentials() {
        return tokenId;
    }

    @Override
    public Optional<String> getOpenIdToken() {
        return Optional.empty();
    }
}

