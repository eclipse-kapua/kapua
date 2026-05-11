/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.token;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;
import org.eclipse.kapua.service.authentication.AuthenticationService;

/**
 * {@link AccessToken} {@link KapuaEntityService} definition.
 *
 * @since 1.0.0
 */
public interface AccessTokenService extends KapuaEntityService<AccessToken, AccessTokenCreator>, KapuaUpdatableEntityService<AccessToken> {

    /**
     * Finds all {@link AccessToken}s associated with the given userId
     *
     * @param scopeId The User.scopeId
     * @param userId The User.id
     * @return The {@link AccessTokenListResult} with matching results
     * @throws KapuaException
     * @since 1.0.0
     */
    AccessTokenListResult findByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException;

    /**
     * Finds the {@link AccessToken} by the given {@link AccessToken#getTokenIdentifier()}
     *
     * @param tokenIdentifier The {@link AccessToken#getTokenIdentifier()}
     * @return The found {@link AccessToken} or {@code null}
     * @throws KapuaException
     * @since 1.0.0
     */
    AccessToken findByTokenIdentifier(String tokenIdentifier) throws KapuaException;

    /**
     * Finds the {@link AccessToken} by the given {@link AccessToken#getTokenIdentifier()}
     *
     * @param tokenIdentifier The {@link AccessToken#getTokenIdentifier()}
     * @return The found {@link AccessToken} or {@code null}
     * @throws KapuaException
     * @since 1.0.0
     * @deprecated Since 2.1.0. Please make use of {@link #findByTokenIdentifier(String)}. The name of this method misleads which fields the find is performed on. This method looks at {@link AccessToken#getTokenIdentifier()}, not {@link AccessToken#getTokenId()}. To search by {@link AccessToken#getTokenId()} use {@link #findByJwt(String)}
     */
    @Deprecated
    AccessToken findByTokenId(String tokenIdentifier) throws KapuaException;

    /**
     * Finds the {@link AccessToken} by the given {@link AccessToken#getTokenId()} aka JWT
     *
     * @param jwt The {@link AccessToken#getTokenId()}
     * @return The found {@link AccessToken} or {@code null}
     * @throws KapuaException
     * @since 2.1.0
     */
    AccessToken findByJwt(String jwt) throws KapuaException;

    /**
     * Generates a new {@link AccessToken} for the given User.
     *
     * @param scopeId The User.scopeId
     * @param userId The User.id
     * @return A new AccessToken for the given User.
     * @since 2.1.0
     */
    AccessToken generateAccessToken(KapuaId scopeId, KapuaId userId) throws KapuaException;

    /**
     * Refreshes the current {@link AccessToken} with a new one.
     *
     * @param tokenId      The current {@link AccessToken#getTokenId()}
     * @param refreshToken The current {@link AccessToken#getRefreshToken()}
     * @return A new {@link AccessToken}.
     * @throws KapuaException
     * @since 2.1.0
     */
    AccessToken refreshAccessToken(String tokenId, String refreshToken) throws KapuaException;

    /**
     * Invalidates the {@link AccessToken} by {@link AccessToken#getId()}
     * <p>
     * After calling this method the {@link AccessToken} will be no longer valid and a new
     * {@link AuthenticationService#login(org.eclipse.kapua.service.authentication.LoginCredentials)} invocation is required in order to get a new valid {@link AccessToken}.
     *
     * @param scopeId The {@link AccessToken#getScopeId()}
     * @param id      The {@link AccessToken#getId()}
     * @since 1.0.0
     */
    void invalidate(KapuaId scopeId, KapuaId id) throws KapuaException;
}
