/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.plugin.sso.openid;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.plugin.sso.openid.exception.OpenIDTokenException;
import org.eclipse.kapua.plugin.sso.openid.exception.uri.OpenIDUriException;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

import javax.json.JsonObject;
import java.net.URI;

/**
 * OpenID Connect Single Sign On service interface.
 *
 * @since 1.0.0
 */
public interface OpenIDService extends KapuaConfigurableService {

    /**
     * Check if the service is enabled.
     *
     * @return <code>true</code> if the service is enabled, <code>false</code> otherwise.
     * @since 1.0.0
     */
    boolean isEnabled();

    /**
     * Get the login URI.
     *
     * @param state       the state parameter used by OpenID to maintain state between the request and the callback.
     * @param redirectUri a URI object representing the redirect URI.
     * @return the login URI in the form of a String.
     * @throws OpenIDUriException if it fails to retrieve the login URI.
     * @since 1.0.0
     */
    String getLoginUri(String state, URI redirectUri) throws OpenIDUriException;

    /**
     * Get the Relying-Party-Initiated logout.
     * This must be implemented following https://openid.net/specs/openid-connect-session-1_0.html#RPLogout
     *
     * @param idTokenHint           the ID Token representing the identity of the user that is requesting to log out.
     * @param postLogoutRedirectUri a URI object representing the destination where the user is redirected after the
     *                              logout.
     * @param state                 the state parameter used by OpenID to maintain state between the logout request and the callback
     *                              to the {@code postLogoutRedirectUris} endpoint.
     * @return the logout URI in the form of a String.
     * @throws OpenIDUriException if it fails to retrieve the logout URI.
     * @since 1.2.0
     */
    String getLogoutUri(String idTokenHint, URI postLogoutRedirectUri, String state) throws OpenIDUriException;

    /**
     * Get the access token.
     *
     * @param authCode    the authorization code from the HttpServletRequest.
     * @param redirectUri a URI object representing the redirect URI.
     * @return the OpenID Connect tokens in the form of a {@link JsonObject}.
     * @throws OpenIDTokenException if it fails to retrieve the tokens.
     * @since 1.0.0
     */
    JsonObject getTokens(String authCode, URI redirectUri) throws OpenIDTokenException;

    /**
     * Get the users ingo token.
     *
     * @param accessToken the access token from the HttpServletRequest.
     * @return the OpenID Connect tokens in the form of a {@link JsonObject}.
     * @throws OpenIDTokenException if it fails to retrieve the tokens.
     * @since 2.0.0
     */
    JsonObject getUserInfo(String accessToken) throws OpenIDTokenException;

    String getId();

    /**
     * Get the SSO data for the given account
     *
     * @param accountId the accountid of the Account for which to retrieve the SSO data
     * @return the SSO Data for the account OR null if the OpenID provider doesn't support brokering
     * @throws KapuaException if it fails to retrieve the tokens.
     * @since 2.0.0
     */
    SSOData retrieveSSODataForAccount(KapuaId accountId) throws KapuaException;

    /**
     * Whether THE SSO BROKERING is enabled for the given scope.
     *
     * @param accountId The accountid of the Account for which to check.
     * @return {@code true} if the {@link KapuaService} is enabled, {@code false} otherwise.
     * @since 2.0.0
     */
    default boolean isBrokeringEnabledForAccount(KapuaId accountId) throws KapuaException {
        return false;
    }
}
