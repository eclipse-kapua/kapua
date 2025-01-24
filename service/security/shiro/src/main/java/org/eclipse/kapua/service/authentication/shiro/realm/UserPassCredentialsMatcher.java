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
package org.eclipse.kapua.service.authentication.shiro.realm;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.ApiKeyCredentials;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.cache.CacheMetric;
import org.eclipse.kapua.service.authentication.credential.cache.CachedPasswordMatcher;
import org.eclipse.kapua.service.authentication.credential.cache.DefaultPasswordMatcher;
import org.eclipse.kapua.service.authentication.credential.cache.PasswordMatcher;
import org.eclipse.kapua.service.authentication.credential.handler.shiro.PasswordCredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionService;
import org.eclipse.kapua.service.authentication.shiro.AuthenticationServiceShiroImpl;
import org.eclipse.kapua.service.authentication.shiro.UsernamePasswordCredentialsImpl;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSettingKeys;
import org.eclipse.kapua.service.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ApiKeyCredentials} {@link CredentialsMatcher} implementation.
 *
 * @since 1.0.0
 */
public class UserPassCredentialsMatcher implements CredentialsMatcher {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationServiceShiroImpl.class);

    private final KapuaLocator locator;
    private final MfaOptionService mfaOptionService;
    //TODO inject????
    private final PasswordMatcher passwordMatcher;
    private final KapuaAuthenticationSetting kapuaAuthenticationSetting;

    public UserPassCredentialsMatcher() {
        locator = KapuaLocator.getInstance();
        mfaOptionService = locator.getService(MfaOptionService.class);
        kapuaAuthenticationSetting = locator.getComponent(KapuaAuthenticationSetting.class);
        if (kapuaAuthenticationSetting.getBoolean(KapuaAuthenticationSettingKeys.AUTHENTICATION_CREDENTIAL_USERPASS_CACHE_ENABLE, true)) {
            logger.info("Cache enabled. Initializing CachePasswordChecker...");
            try {
                passwordMatcher = new CachedPasswordMatcher(locator.getComponent(CacheMetric.class), locator.getComponent(KapuaAuthenticationSetting.class));
            } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
                throw KapuaRuntimeException.internalError(e, "Cannot instantiate CachedPasswordMatcher");
            }
        } else {
            logger.info("Cache disabled. Initializing NoCachePasswordChecker...");
            passwordMatcher = new DefaultPasswordMatcher();
        }
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        // Token data
        UsernamePasswordCredentialsImpl token = (UsernamePasswordCredentialsImpl) authenticationToken;
        String tokenUsername = token.getUsername();
        String tokenPassword = token.getPassword();
        String tokenAuthenticationCode = token.getAuthenticationCode();
        String tokenTrustKey = token.getTrustKey();
        // Info data
        LoginAuthenticationInfo info = (LoginAuthenticationInfo) authenticationInfo;
        User infoUser = (User) info.getPrincipals().getPrimaryPrincipal();
        Credential infoCredential = (Credential) info.getCredentials();

        if (!PasswordCredentialTypeHandler.TYPE.equals(infoCredential.getCredentialType())) {
            throw KapuaRuntimeException.internalError("Invalid Credential.type for UserPassCredentialsMatcher: " + infoCredential.getCredentialType());
        }

        // Match token with info
        if (tokenUsername.equals(infoUser.getName()) &&
                passwordMatcher.checkPassword(tokenUsername, tokenPassword, infoCredential)) {

            try {
                return mfaOptionService.validateMfaCredentials(infoUser.getScopeId(), infoUser.getId(), tokenAuthenticationCode, tokenTrustKey);
            } catch (KapuaException e) {
                throw new ShiroException(e.getMessage(), e);
            }
        }

        return false;
    }
}