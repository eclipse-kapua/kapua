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

import org.eclipse.kapua.service.authentication.JwtCredentials;
import org.eclipse.kapua.service.authentication.shiro.realm.KapuaAuthenticationToken;

/**
 * {@link JwtCredentials} implementation.
 * <p>
 * This implements also {@link KapuaAuthenticationToken} to allow usage in Apache Shiro.
 *
 * @since 1.0.0
 */
public class JwtCredentialsImpl implements KapuaAuthenticationToken {

    private String accessToken;
    private String idToken;

    /**
     * Clone constructor.
     *
     * @param jwtCredentials
     *         The {@link JwtCredentials} to clone
     * @since 1.5.0
     */
    public JwtCredentialsImpl(@NotNull JwtCredentials jwtCredentials) {
        this.accessToken = jwtCredentials.getAccessToken();
        this.idToken = jwtCredentials.getIdToken();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    @Override
    public Object getPrincipal() {
        return this.accessToken;
    }

    @Override
    public Object getCredentials() {
        return this.accessToken;
    }

    @Override
    public Optional<String> getOpenIdToken() {
        return Optional.of(this.idToken);
    }
}
