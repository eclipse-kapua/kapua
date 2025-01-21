/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.user.shiro;

import java.util.Optional;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.CredentialsFactory;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialRepository;
import org.eclipse.kapua.service.authentication.credential.shiro.PasswordResetter;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.user.PasswordChangeRequest;
import org.eclipse.kapua.service.authentication.user.PasswordResetRequest;
import org.eclipse.kapua.service.authentication.user.UserCredentialsService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link UserCredentialsService} implementation.
 *
 * @since 2.0.0
 */
@Singleton
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final CredentialsFactory credentialsFactory;
    private final CredentialFactory credentialFactory;
    private final TxManager txManager;
    private final UserService userService;
    private final CredentialRepository credentialRepository;
    private final PasswordResetter passwordResetter;

    public UserCredentialsServiceImpl(
            AuthenticationService authenticationService,
            AuthorizationService authorizationService, PermissionFactory permissionFactory,
            CredentialsFactory credentialsFactory,
            CredentialFactory credentialFactory,
            TxManager txManager,
            UserService userService,
            CredentialRepository credentialRepository,
            PasswordResetter passwordResetter) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.credentialsFactory = credentialsFactory;
        this.credentialFactory = credentialFactory;
        this.txManager = txManager;
        this.userService = userService;
        this.credentialRepository = credentialRepository;
        this.passwordResetter = passwordResetter;
    }

    @Override
    public Credential changePassword(KapuaId scopeId, KapuaId userId, PasswordChangeRequest passwordChangeRequest) throws KapuaException {
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "userId");
        ArgumentValidator.notNull(passwordChangeRequest.getNewPassword(), "passwordChangeRequest.newPassword");
        ArgumentValidator.notNull(passwordChangeRequest.getCurrentPassword(), "passwordChangeRequest.currentPassword");

        final User user = Optional.ofNullable(KapuaSecurityUtils.doPrivileged(() -> userService.find(scopeId, userId))
        ).orElseThrow(() -> new KapuaEntityNotFoundException(User.TYPE, userId));
        return txManager.execute(tx -> {
            final UsernamePasswordCredentials usernamePasswordCredentials = credentialsFactory.newUsernamePasswordCredentials(user.getName(), passwordChangeRequest.getCurrentPassword());
            try {
                authenticationService.verifyCredentials(usernamePasswordCredentials);
            } catch (KapuaAuthenticationException e) {
                throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INCORRECT_CURRENT_PASSWORD);
            }
            final PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
            passwordResetRequest.setNewPassword(passwordChangeRequest.getNewPassword());
            try {
                return passwordResetter.resetPassword(tx, scopeId, userId, true, passwordResetRequest);
            } catch (KapuaIllegalArgumentException ignored) {
                throw new KapuaIllegalArgumentException("passwordChangeRequest.newPassword", passwordChangeRequest.getNewPassword());
            }
        });
    }

    @Override
    public Credential resetPassword(KapuaId scopeId, KapuaId credentialId, PasswordResetRequest passwordResetRequest) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(credentialId, "credentialId");
        ArgumentValidator.notNull(passwordResetRequest.getNewPassword(), "passwordResetRequest.newPassword");

        // Check accessauth
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.write, scopeId));

        return txManager.execute(tx -> passwordResetter.resetPassword(tx, scopeId, credentialId, passwordResetRequest));
    }
}
