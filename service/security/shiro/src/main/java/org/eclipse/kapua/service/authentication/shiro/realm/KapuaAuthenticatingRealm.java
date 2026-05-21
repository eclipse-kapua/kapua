/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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


import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.AccountStatus;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.CredentialStatus;
import org.eclipse.kapua.service.authentication.shiro.exceptions.ExpiredAccountException;
import org.eclipse.kapua.service.authentication.shiro.exceptions.TemporaryLockedAccountException;
import org.eclipse.kapua.service.authentication.shiro.session.ShiroSessionKeys;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserStatus;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * Base {@code abstract} {@link AuthenticatingRealm} extension.
 *
 * @since 2.0.0
 */
public abstract class KapuaAuthenticatingRealm extends AuthenticatingRealm {

    protected final AccountService accountService = KapuaLocator.getInstance().getService(AccountService.class);
    protected final CredentialService credentialService = KapuaLocator.getInstance().getService(CredentialService.class);

    // Session

    /**
     * Populates the {@link Session} of the given {@link Subject} with information from the given {@link LoginAuthenticationInfo}.
     *
     * @param subject                 The {@link Subject} of the login.
     * @param loginAuthenticationInfo The {@link LoginAuthenticationInfo} source of the data
     * @since 2.0.0
     */
    protected void populateSession(@NotNull Subject subject, @NotNull LoginAuthenticationInfo loginAuthenticationInfo) {
        Session session = subject.getSession();

        session.setAttribute(ShiroSessionKeys.SCOPE_ID, loginAuthenticationInfo.getAccount().getId());
        session.setAttribute(ShiroSessionKeys.ACCOUNT_NAME, loginAuthenticationInfo.getAccount().getName());
        session.setAttribute(ShiroSessionKeys.USER_ID, loginAuthenticationInfo.getUser().getId());
        session.setAttribute(ShiroSessionKeys.USER_NAME, loginAuthenticationInfo.getUser().getName());
    }
    // Account

    /**
     * Check the given {@link Account#getId()}.
     * <p>
     * Checks performed:
     * <ul>
     *     <li>Existence in the {@link AccountService}</li>
     *     <li>{@link Account#getExpirationDate()}</li>
     * </ul>
     *
     * @param accountId The {@link Account#getId()} to check.
     * @return The found {@link Account}.
     * @since 2.0.0
     */
    protected Account checkAccount(KapuaId accountId) {
        Account account;
        try {
            account = KapuaSecurityUtils.doPrivileged(() -> accountService.find(accountId));
        } catch (AuthenticationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ShiroException("Unexpected error while looking for the account!", e);
        }

        // Check existence
        if (account == null) {
            throw new UnknownAccountException();
        }

        try {
            if (accountService.deriveAccountStatus(account).equals(AccountStatus.DISABLED)) {
                throw new DisabledAccountException();
            }
        } catch (KapuaException ke) {
            throw new ShiroException("Unexpected error while looking for the account status!", ke);
        }

        // Check account expired
        if (account.getExpirationDate() != null && !account.getExpirationDate().after(new Date())) {
            throw new ExpiredAccountException(account.getExpirationDate());
        }

        return account;
    }
    // Credential

    /**
     * Check the given {@link Credential}.
     * <p>
     * Checks performed:
     * <ul>
     *     <li>Existence of the {@link Credential}</li>
     *     <li>{@link Credential#getStatus()}</li>
     *     <li>{@link Credential#getExpirationDate()} ()}</li>
     * </ul>
     *
     * @param credential The {@link Credential} to check.
     * @throws UnknownAccountException     if {@link Credential} is {@code null}
     * @throws DisabledAccountException    if {@link Credential#getStatus()} is {@link CredentialStatus#DISABLED}.
     * @throws ExpiredCredentialsException if {@link Credential#getExpirationDate()} is passed.
     * @since 2.0.0
     */
    protected void checkCredential(Credential credential) throws UnknownAccountException, DisabledAccountException, ExpiredCredentialsException {
        if (credential == null) {
            throw new UnknownAccountException();
        }

        // Check credential disabled
        if (CredentialStatus.DISABLED.equals(credential.getStatus())) {
            throw new DisabledAccountException();
        }

        // Check if credential expired
        if (credential.getExpirationDate() != null && !credential.getExpirationDate().after(new Date())) {
            throw new ExpiredCredentialsException();
        }
    }

    /**
     * Checks the {@link Credential} against the Lockout policy of the {@link CredentialService#getConfigValues(KapuaId)}.
     *
     * @param credential              The {@link Credential} to check.
     * @param credentialServiceConfig The {@link CredentialService#getConfigValues(KapuaId)}
     * @throws TemporaryLockedAccountException if the {@link Credential} is temporary locked.
     * @since 2.0.0
     */
    protected void checkCredentialLockout(Credential credential, Map<String, Object> credentialServiceConfig) throws TemporaryLockedAccountException {

        boolean lockoutPolicyEnabled = (boolean) credentialServiceConfig.get("lockoutPolicy.enabled");

        if (lockoutPolicyEnabled) {
            Date now = new Date();
            if (credential.getLockoutReset() != null && now.before(credential.getLockoutReset())) {
                throw new TemporaryLockedAccountException(credential.getLockoutReset());
            }
        }
    }

    /**
     * Gets the {@link CredentialService#getConfigValues(KapuaId)} for the given scope {@link KapuaId}.
     *
     * @param scopeId The scope {@link KapuaId}.
     * @return The {@link Map} of configuration values of the {@link CredentialService}.
     * @since 2.0.0
     */
    protected Map<String, Object> getCredentialServiceConfig(KapuaId scopeId) {
        try {
            return KapuaSecurityUtils.doPrivileged(() -> credentialService.getConfigValues(scopeId));
        } catch (KapuaException e) {
            throw new ShiroException("Unexpected error while looking for the CredentialService!", e);
        }
    }

    /**
     * Increases the failed login attempts according to {@link CredentialService#getConfigValues(KapuaId)}.
     *
     * @param loginAuthenticationInfo The {@link LoginAuthenticationInfo} the failed login.
     * @since 2.0.0
     */
    protected void increaseLockoutPolicyCount(LoginAuthenticationInfo loginAuthenticationInfo) {
        try {
            Credential failedCredential = (Credential) loginAuthenticationInfo.getCredentials();

            KapuaSecurityUtils.doPrivileged(() -> {
                Map<String, Object> credentialServiceConfig = loginAuthenticationInfo.getCredentialServiceConfig();
                boolean lockoutPolicyEnabled = (boolean) credentialServiceConfig.get("lockoutPolicy.enabled");
                if (lockoutPolicyEnabled) {
                    Date now = new Date();
                    int resetAfterSeconds = (int) credentialServiceConfig.get("lockoutPolicy.resetAfter");

                    Date firstLoginFailure;
                    boolean resetAttempts = failedCredential.getFirstLoginFailure() == null || now.after(failedCredential.getLoginFailuresReset());
                    if (resetAttempts) {
                        firstLoginFailure = now;
                        failedCredential.setLoginFailures(1);
                    } else {
                        firstLoginFailure = failedCredential.getFirstLoginFailure();
                        failedCredential.setLoginFailures(failedCredential.getLoginFailures() + 1);
                    }

                    Date loginFailureWindowExpiration = new Date(firstLoginFailure.getTime() + (resetAfterSeconds * 1000L));
                    failedCredential.setFirstLoginFailure(firstLoginFailure);
                    failedCredential.setLoginFailuresReset(loginFailureWindowExpiration);
                    int maxLoginFailures = (int) credentialServiceConfig.get("lockoutPolicy.maxFailures");
                    if (failedCredential.getLoginFailures() >= maxLoginFailures) {
                        long lockoutDuration = (int) credentialServiceConfig.get("lockoutPolicy.lockDuration");
                        Date resetDate = new Date(now.getTime() + (lockoutDuration * 1000));
                        failedCredential.setLockoutReset(resetDate);
                    }
                }
                credentialService.update(failedCredential);
            });
        } catch (KapuaException kex) {
            throw new ShiroException("Error while updating lockout policy", kex);
        }
    }

    /**
     * Reset the lockout policy of the {@link Credential}.
     * To be used after a succeessful login.
     *
     * @param credential The {@link Credential} to reset.
     * @since 2.0.0
     */
    protected void resetCredentialLockout(Credential credential) {
        //TODO find a proper way to update only if needed (obviously database update has a cost)
        if (shouldResetCredentialLockout(credential)) {
            credential.setFirstLoginFailure(null);
            credential.setLoginFailuresReset(null);
            credential.setLockoutReset(null);
            credential.setLoginFailures(0);
            try {
                KapuaSecurityUtils.doPrivileged(() -> credentialService.update(credential));
            } catch (KapuaException kex) {
                throw new ShiroException("Unexpected error while looking for the lockout policy", kex);
            }
        }
    }

    private boolean shouldResetCredentialLockout(Credential credential) {
        return credential.getFirstLoginFailure() != null ||
                credential.getLoginFailuresReset() != null ||
                credential.getLockoutReset() != null ||
                credential.getLoginFailures() != 0;
    }

    // User

    /**
     * Check the given {@link User}.
     * <p>
     * Checks performed:
     * <ul>
     *     <li>Existence of the {@link User}</li>
     *     <li>{@link User#getStatus()}</li>
     *     <li>{@link User#getExpirationDate()} ()}</li>
     * </ul>
     *
     * @param user The {@link User} to check.
     * @throws UnknownAccountException     if {@link User} is {@code null}
     * @throws DisabledAccountException    if {@link User#getStatus()} is {@link UserStatus#DISABLED}.
     * @throws ExpiredCredentialsException if {@link User#getExpirationDate()} is passed.
     * @since 2.0.0
     */
    protected void checkUser(User user) {
        if (user == null) {
            throw new UnknownAccountException();
        }

        // Check disabled
        if (UserStatus.DISABLED.equals(user.getStatus())) {
            throw new DisabledAccountException();
        }

        // Check if expired
        if (user.getExpirationDate() != null && !user.getExpirationDate().after(new Date())) {
            throw new ExpiredCredentialsException();
        }
    }
}
