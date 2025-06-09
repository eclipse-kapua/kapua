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

import org.eclipse.kapua.service.authentication.ApiKeyCredentials;
import org.eclipse.kapua.service.authentication.shiro.realm.KapuaAuthenticationToken;

/**
 * {@link ApiKeyCredentials} implementation.
 * <p>
 * This implements also {@link KapuaAuthenticationToken} to allow usage in Apache Shiro.
 *
 * @since 1.0.0
 */
public class ApiKeyCredentialsImpl implements KapuaAuthenticationToken {

    private static final long serialVersionUID = -5920944517814926028L;

    private String apiKey;

    /**
     * Constructor.
     *
     * @param apiKey
     *         The Api Key.
     * @since 1.0.0
     */
    public ApiKeyCredentialsImpl(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Clone constructor.
     *
     * @param apiKeyCredentials
     *         The {@link ApiKeyCredentials} to clone.
     * @since 1.5.0
     */
    public ApiKeyCredentialsImpl(@NotNull ApiKeyCredentials apiKeyCredentials) {
        this.apiKey = apiKeyCredentials.getApiKey();
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return getApiKey();
    }

    @Override
    public Object getCredentials() {
        return getApiKey();
    }

    @Override
    public Optional<String> getOpenIdToken() {
        return Optional.empty();
    }
}
