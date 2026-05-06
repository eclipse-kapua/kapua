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
package org.eclipse.kapua.service.authentication;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenService;
import org.eclipse.kapua.service.authentication.token.LoginInfo;

/**
 * {@link AuthenticationService} definition.
 *
 * @since 1.0.0
 */
public interface AuthenticationService extends KapuaService {

    /**
     * Logins the provided {@link LoginCredentials}.
     * <p>
     * Creates a new session that is represented by the {@link AccessToken}.
     *
     * @param loginCredentials The {@link LoginCredentials} to validate.
     * @return The {@link AccessToken} created by this login
     * @throws KapuaException
     * @since 1.0.0
     */
    AccessToken login(LoginCredentials loginCredentials) throws KapuaException;

    /**
     * Logins the provided {@link LoginCredentials}.
     *
     * @param loginCredentials The {@link LoginCredentials} to validate.
     * @param enableTrust      Whether to generate a trustkey.
     * @return The {@link AccessToken} created by this login
     * @throws KapuaException
     * @since 1.0.0
     * @deprecated Since 2.0.0. Please make use of {@link #login(LoginCredentials)}.
     */
    @Deprecated
    AccessToken login(LoginCredentials loginCredentials, boolean enableTrust) throws KapuaException;

    /**
     * Logins the provided {@link SessionCredentials}.
     * <p>
     * Restores a previously created session from a {@link #login(LoginCredentials)}.
     *
     * @param sessionCredentials The {@link SessionCredentials} to validate
     * @throws KapuaException
     * @since 1.0.0
     */
    void authenticate(SessionCredentials sessionCredentials) throws KapuaException;

    /**
     * Verifies the given {@link LoginCredentials}.
     * <p>
     * This does not create a new session and does not create a new {@link AccessToken}.
     *
     * @param loginCredentials The {@link LoginCredentials} to validate.
     * @throws KapuaException
     * @since 1.0.0
     */
    void verifyCredentials(LoginCredentials loginCredentials) throws KapuaException;

    /**
     * Verifies the given {@link SessionCredentials}.
     * <p>
     * This does not establish a KapuaSession
     *
     * @param sessionCredentials The {@link SessionCredentials} to validate.
     * @throws KapuaException
     * @since 2.1.0
     */
    void verifyCredentials(SessionCredentials sessionCredentials) throws KapuaException;

    /**
     * Checks if there is a session that is authenticated.
     *
     * @return {@code true} if session is authenticated, {@code false} otherwise.
     * @throws KapuaException
     * @since 2.0.0
     */
    boolean isAuthenticated() throws KapuaException;

    /**
     * Returns the {@link LoginInfo} related to the current session.
     *
     * @return The {@link LoginInfo} related to the current session.
     * @throws KapuaException
     * @since 1.1.0
     */
    LoginInfo getLoginInfo() throws KapuaException;

    /**
     * Logouts the current logged user.
     * <p>
     * Destroys the session represented by the {@link AccessToken}.
     *
     * @throws KapuaException
     * @since 1.0.0
     */
    void logout() throws KapuaException;

    /**
     * Gets the {@link AccessToken} identified by its {@link AccessToken#getTokenId()}.
     * In other words, given the JWT it returns the respective Access Token entity
     *
     * @param tokenId The {@link AccessToken#getTokenId()} to look for.
     * @return The found {@link AccessToken} or {@code null} if not present.
     * @throws KapuaException if the provided tokenId (JWT) is not valid in its header or payload content
     * @since 1.0.0
     * @deprecated Since 2.1.0. It has been moved to {@link AccessTokenService#findByJwt(String)}
     */
    @Deprecated
    AccessToken findAccessToken(String tokenId) throws KapuaException;

    /**
     * Refreshes the current {@link AccessToken} with a new one.
     *
     * @param tokenId      The current {@link AccessToken#getTokenId()}
     * @param refreshToken The current {@link AccessToken#getRefreshToken()}
     * @return A new {@link AccessToken}.
     * @throws KapuaException
     * @since 1.0.0
     */
    AccessToken refreshAccessToken(String tokenId, String refreshToken) throws KapuaException;

    /**
     * Return a Refreshable {@link AccessToken} identified by the provided token identifier. A Refreshable token may be
     * already expired or not, but its Refresh Token is still valid
     *
     * @param tokenId The ID of the {@link AccessToken}
     * @return The desired {@link AccessToken} object
     * @throws KapuaException if no {@link AccessToken} is found for that token identifier
     * @since 1.3.0
     * @deprecated Since 2.0.0. This has been added to this API but is not necessary.
     */
    @Deprecated
    AccessToken findRefreshableAccessToken(String tokenId) throws KapuaException;
}
