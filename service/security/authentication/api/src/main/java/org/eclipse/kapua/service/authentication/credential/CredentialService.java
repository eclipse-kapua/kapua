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
package org.eclipse.kapua.service.authentication.credential;

import java.util.Set;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;
import org.eclipse.kapua.service.authentication.user.PasswordResetRequest;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.user.User;

/**
 * {@link Credential} {@link KapuaEntityService} definition.
 *
 * @since 1.0.0
 */
public interface CredentialService extends KapuaEntityService<Credential, CredentialCreator>,
        KapuaUpdatableEntityService<Credential>,
        KapuaConfigurableService {

    /**
     * Gets the {@link Credential}s filtered by {@link User#getId()}.
     *
     * @param scopeId
     *         The {@link Credential#getScopeId()}
     * @param userId
     *         The {@link User#getId()}
     * @return The {@link CredentialListResult}
     * @throws KapuaException
     * @since 1.0.0
     */
    CredentialListResult findByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException;

    /**
     * Gets the {@link Credential}s filtered by given parameters
     *
     * @param scopeId
     *         The {@link Credential#getScopeId()}
     * @param userId
     *         The {@link User#getId()}
     * @param credentialType
     *         The {@link Credential#getCredentialType()}
     * @return The {@link CredentialListResult}
     * @throws KapuaException
     * @since 1.0.0
     */
    CredentialListResult findByUserId(KapuaId scopeId, KapuaId userId, String credentialType) throws KapuaException;

    /**
     * Gets a {@link Credential} by its ApiKey. To be used when {@link Credential#getCredentialType()} is {@code API_KEY}
     *
     * @param tokenApiKey
     *         The API key to match
     * @return The matched {@link Credential}
     * @throws KapuaException
     * @since 1.0.0
     */
    Credential findByApiKey(String tokenApiKey) throws KapuaException;

    /**
     * Gets the {@link Credential}s filtered by the {@link KapuaQuery}.
     *
     * @param query
     *         The {@link KapuaQuery} to filter results
     * @since 1.0.0
     */
    @Override
    CredentialListResult query(KapuaQuery query) throws KapuaException;

    /**
     * Unlocks a {@link Credential}
     *
     * @param scopeId
     *         The {@link Credential#getScopeId()}
     * @param credentialId
     *         The {@link Credential#getId()}
     * @throws KapuaException
     * @since 1.0.0
     */
    void unlock(KapuaId scopeId, KapuaId credentialId) throws KapuaException;

    /**
     * Gets the minimum password length according to Account setting and system default.
     *
     * @param scopeId
     *         The id of the Account to check the setting
     * @return The minimum required password length
     * @throws KapuaException
     * @since 1.3.0
     */
    int getMinimumPasswordLength(KapuaId scopeId) throws KapuaException;

    /**
     * Checks if the provided password meets all the password's requirements.
     *
     * @param scopeId
     *         The scope ID in which to perform the check
     * @param plainPassword
     *         The plain password to check requirements
     * @throws KapuaException
     * @since 2.0.0
     */
    void validatePassword(KapuaId scopeId, String plainPassword) throws KapuaException;

    /**
     * Gets the {@link Credential} with {@link Credential#getCredentialKey()} not removed.
     *
     * @param scopeId
     *         The {@link Credential#getScopeId()}
     * @param credentialId
     *         The {@link Credential#getId()}
     * @return The {@link Credential} matched.
     * @throws KapuaException
     * @since 2.0.0
     */
    Credential findWithKey(KapuaId scopeId, KapuaId credentialId) throws KapuaException;

    /**
     * Resets the password of a {@link User}, according to the given {@link PasswordResetRequest}
     *
     * @param scopeId
     *         The {@link Credential#getScopeId()}
     * @param userId
     *         The {@link Credential#getUserId()}
     * @param passwordResetRequest
     *         The {@link PasswordResetRequest}
     * @return The updated {@link Credential}
     * @since 2.0.0
     */
    Credential adminResetUserPassword(KapuaId scopeId, KapuaId userId, PasswordResetRequest passwordResetRequest) throws KapuaException;

    /**
     * Gets the available {@link Credential#getCredentialType()}s
     *
     * @return The available {@link Credential#getCredentialType()}s
     * @throws KapuaException
     * @since 2.1.0
     */
    Set<String> getAvailableCredentialTypes() throws KapuaException;
}
