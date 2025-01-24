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

import java.util.Set;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialRepository;
import org.eclipse.kapua.service.authentication.credential.handler.CredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.handler.shiro.PasswordCredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.shiro.PasswordResetter;
import org.eclipse.kapua.service.authentication.credential.shiro.PasswordResetterImpl;
import org.eclipse.kapua.service.authentication.credential.shiro.PasswordValidator;
import org.eclipse.kapua.service.authentication.user.UserCredentialsService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.UserService;

import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * {@code kapua-security-shiro} {@link Module} implementation.
 *
 * @since 2.0.0
 */
public class UserCredentialsModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
    }

    @Provides
    @Singleton
    PasswordResetter passwordResetter(
            CredentialRepository credentialRepository,
            Set<CredentialTypeHandler> credentialTypeHandlers,
            PasswordValidator passwordValidator
    ) {
        PasswordCredentialTypeHandler passwordCredentialTypeHandler = (PasswordCredentialTypeHandler) credentialTypeHandlers
                .stream()
                .filter(credentialTypeHandler -> credentialTypeHandler instanceof PasswordCredentialTypeHandler)
                .findFirst()
                .orElseThrow(() -> KapuaRuntimeException.internalError("Cannot find an instance of PasswordCredentialTypeHandler between the available CredentialTypeHandlers"));

        return new PasswordResetterImpl(
                credentialRepository,
                passwordCredentialTypeHandler,
                passwordValidator
        );
    }

    @Provides
    @Singleton
    UserCredentialsService userCredentialsService(
            AuthenticationService authenticationService,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            CredentialFactory credentialFactory,
            KapuaJpaTxManagerFactory txManagerFactory,
            UserService userService,
            CredentialRepository credentialRepository,
            PasswordResetter passwordResetter) {
        return new UserCredentialsServiceImpl(
                authenticationService,
                authorizationService,
                permissionFactory,
                credentialFactory,
                txManagerFactory.create("kapua-authorization"),
                userService,
                credentialRepository,
                passwordResetter);
    }
}
