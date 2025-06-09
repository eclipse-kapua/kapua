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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.authentication.shiro.realm;

import javax.json.JsonObject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.Destroyable;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.plugin.sso.openid.JwtProcessor;
import org.eclipse.kapua.plugin.sso.openid.OpenIDLocator;
import org.eclipse.kapua.plugin.sso.openid.OpenIDService;
import org.eclipse.kapua.plugin.sso.openid.exception.OpenIDException;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authentication.ApiKeyCredentials;
import org.eclipse.kapua.service.authentication.JwtCredentials;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialStatus;
import org.eclipse.kapua.service.authentication.credential.handler.shiro.JwtCredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.shiro.CredentialImpl;
import org.eclipse.kapua.service.authentication.shiro.JwtCredentialsImpl;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSettingKeys;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * {@link ApiKeyCredentials} based {@link AuthenticatingRealm} implementation.
 *
 * @since 1.0.0
 */
public class JwtAuthenticatingRealm extends KapuaAuthenticatingRealm implements Destroyable {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticatingRealm.class);

    private final Boolean ssoUserExternalIdAutofill;
    private final Boolean ssoUserExternalUsernameAutofill;
    // Get services
    private final UserService userService = KapuaLocator.getInstance().getService(UserService.class);
    private final OpenIDService openIDService = KapuaLocator.getInstance().getComponent(OpenIDLocator.class).getService();
    private final KapuaAuthenticationSetting authenticationSetting = KapuaLocator.getInstance().getComponent(KapuaAuthenticationSetting.class);
    /**
     * JWT Processor.
     */
    private final JwtProcessor jwtProcessor;

    /**
     * Realm name.
     */
    public static final String REALM_NAME = "jwtAuthenticatingRealm";

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public JwtAuthenticatingRealm() {
        setName(REALM_NAME);
        try {
            jwtProcessor = KapuaLocator.getInstance().getComponent(OpenIDLocator.class).getProcessor();
        } catch (OpenIDException se) {
            throw new ShiroException("Unexpected error while creating Jwt Processor!", se);
        }
        ssoUserExternalIdAutofill = authenticationSetting.getBoolean(KapuaAuthenticationSettingKeys.AUTHENTICATION_SSO_USER_EXTERNAL_ID_AUTOFILL);
        ssoUserExternalUsernameAutofill = authenticationSetting.getBoolean(KapuaAuthenticationSettingKeys.AUTHENTICATION_SSO_USER_EXTERNAL_USERNAME_AUTOFILL);
    }

    @Override
    protected void onInit() {
        super.onInit();

        setCredentialsMatcher(new JwtCredentialsMatcher(jwtProcessor));
    }

    @Override
    public void destroy() throws Exception {
        if (jwtProcessor != null) {
            jwtProcessor.close();
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // Extract credentials
        JwtCredentialsImpl jwtCredentials = (JwtCredentialsImpl) authenticationToken;
        String jwtIdToken = jwtCredentials.getIdToken();
        // Get the associated user by external id
        User user;
        try {
            String userExternalId = extractExternalId(jwtIdToken);

            user = KapuaSecurityUtils.doPrivileged(() -> userService.findByExternalId(userExternalId));

            // Update User.externalUsername if not populated and if autofill is enabled
            if (ssoUserExternalUsernameAutofill &&
                    user != null &&
                    Strings.isNullOrEmpty(user.getExternalUsername())) {

                String externalUsername = extractExternalUsername(jwtIdToken);

                if (!Strings.isNullOrEmpty(externalUsername)) {
                    user.setExternalUsername(externalUsername);
                    user = updateUser(user);
                }
            }
        } catch (AuthenticationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ShiroException("Unexpected error while looking for the user", e);
        }

        if (user == null) {
            // User not found by User.externalId (OpenID Connect 'sub' claim).
            // Checking by other claims (claim checked is configurable in the OpenIDService.
            try {
                String externalUsername = extractExternalUsername(jwtIdToken);
                LOG.debug("JWT contains external username: {}", externalUsername);

                if (!Strings.isNullOrEmpty(externalUsername)) {
                    user = KapuaSecurityUtils.doPrivileged(() -> userService.findByExternalUsername(externalUsername));

                    // Update User.externalId if autofill is enabled
                    if (ssoUserExternalIdAutofill && user != null) {
                        String userExternalId = extractExternalId(jwtIdToken);
                        user.setExternalId(userExternalId);
                        user = updateUser(user);
                    }
                } else {
                    user = resolveExternalUsernameWithOpenIdProvider(jwtCredentials);
                }
            } catch (AuthenticationException ae) {
                throw ae;
            } catch (Exception e) {
                throw new ShiroException("Unexpected error while looking for the user", e);
            }
        }

        // Check user
        checkUser(user);

        // Check account
        Account account = checkAccount(user.getScopeId());

        // Create credential
        Credential credential = new CredentialImpl(user.getScopeId(), user.getId(), JwtCredentialTypeHandler.TYPE, jwtIdToken, CredentialStatus.ENABLED, null);

        // Build AuthenticationInfo
        return new LoginAuthenticationInfo(getName(),
                account,
                user,
                credential,
                null);
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info)
            throws AuthenticationException {
        LoginAuthenticationInfo kapuaInfo = (LoginAuthenticationInfo) info;

        super.assertCredentialsMatch(authcToken, info);
        // Populate Session with info
        populateSession(SecurityUtils.getSubject(), kapuaInfo);
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof JwtCredentialsImpl;
    }
    // Private methods

    /**
     * Extract the subject information
     *
     * @param jwt
     *         the token to use
     * @return the subject, never returns {@code null}
     * @throws ShiroException
     *         in case the subject could not be extracted
     * @since 1.0.0
     */
    private String extractExternalId(String jwt) {
        String id;
        try {
            JwtContext jwtContext = jwtProcessor.process(jwt);
            id = jwtContext.getJwtClaims().getClaimValueAsString(jwtProcessor.getExternalIdClaimName());
        } catch (Exception e) {
            throw new ShiroException("Failed to parse JWT", e);
        }

        if (Strings.isNullOrEmpty(id)) {
            throw new ShiroException("'subject' missing on JWT");
        }

        return id;
    }

    /**
     * Extract the external username information
     *
     * @param jwt
     *         the token to use.
     * @return the external username.
     * @since 2.0.0
     */
    private String extractExternalUsername(String jwt) {
        final String externalUsername;
        try {
            JwtContext ctx = jwtProcessor.process(jwt);
            externalUsername = ctx.getJwtClaims().getClaimValueAsString(jwtProcessor.getExternalUsernameClaimName());
        } catch (Exception e) {
            throw new ShiroException("Failed to parse JWT", e);
        }

        return externalUsername;
    }

    /**
     * Extract the external username
     *
     * @param userInfo
     *         the userInfo to use.
     * @return the external username.
     * @since 2.0.0
     */
    private String extractExternalUsername(JsonObject userInfo) {
        final String externalUsername;
        try {
            // Using JsonObject.getString(String, null) to return 'null' when field is not present in the UserInfo JsonObject.
            // JsonObject.getString(String) throws NPE when element is not present.
            // See JsonObject.getString(String) javadoc.
            externalUsername = userInfo.getString(jwtProcessor.getExternalUsernameClaimName(), null);
        } catch (Exception e) {
            throw new ShiroException("Failed to parse JWT", e);
        }

        return externalUsername;
    }

    /**
     * Tries to resolve {@link User#getExternalUsername()} using the {@link OpenIDService#getUserInfo(String)} resource.
     *
     * @param jwtCredentials
     *         The {@link JwtCredentials}.
     * @return The updated user.
     * @throws KapuaException
     * @since 2.0.0
     */
    private User resolveExternalUsernameWithOpenIdProvider(JwtCredentialsImpl jwtCredentials) throws KapuaException {

        // Ask the OpenID Provider the user's info
        JsonObject userInfo = openIDService.getUserInfo(jwtCredentials.getAccessToken());
        String externalUsername = extractExternalUsername(userInfo);

        User user = null;
        // If externalUsername is returned try to find the User
        if (!Strings.isNullOrEmpty(externalUsername)) {
            user = KapuaSecurityUtils.doPrivileged(() -> userService.findByExternalUsername(externalUsername));

            // Update User.externalId if autofill is configured
            if (ssoUserExternalIdAutofill && user != null) {
                String userExternalId = extractExternalId(jwtCredentials.getIdToken());

                if (!Strings.isNullOrEmpty(userExternalId)) {
                    user.setExternalId(userExternalId);
                    user = updateUser(user);
                }
            }
        }

        return user;
    }

    /**
     * Updates the given {@link User}.
     *
     * @param user
     *         The user to update.
     * @return The updated user.
     * @throws KapuaException
     * @since 2.0.0
     */
    private User updateUser(User user) throws KapuaException {
        return KapuaSecurityUtils.doPrivileged(() -> userService.update(user));
    }
}
