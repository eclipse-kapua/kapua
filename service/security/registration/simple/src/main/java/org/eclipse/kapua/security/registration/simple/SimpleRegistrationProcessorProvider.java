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
package org.eclipse.kapua.security.registration.simple;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.kapua.security.registration.RegistrationProcessor;
import org.eclipse.kapua.security.registration.RegistrationProcessorProvider;
import org.eclipse.kapua.security.registration.simple.SimpleRegistrationProcessor.Settings;
import org.eclipse.kapua.security.registration.simple.setting.SimpleSetting;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserService;

public class SimpleRegistrationProcessorProvider implements RegistrationProcessorProvider {

    private final SimpleSetting simpleSetting;
    private final AccountService accountService;
    private final AccountFactory accountFactory;
    private final CredentialService credentialService;
    private final CredentialFactory credentialFactory;
    private final DeviceRegistryService deviceRegistryService;
    private final UserService userService;
    private final UserFactory userFactory;
    private final AccessInfoService accessInfoService;
    private final AccessInfoFactory accessInfoFactory;
    private final PermissionFactory permissionFactory;

    @Inject
    public SimpleRegistrationProcessorProvider(
            SimpleSetting simpleSetting,
            AccountService accountService,
            AccountFactory accountFactory,
            CredentialService credentialService,
            CredentialFactory credentialFactory,
            DeviceRegistryService deviceRegistryService,
            UserService userService,
            UserFactory userFactory,
            AccessInfoService accessInfoService,
            AccessInfoFactory accessInfoFactory,
            PermissionFactory permissionFactory) {
        this.simpleSetting = simpleSetting;
        this.accountService = accountService;
        this.accountFactory = accountFactory;
        this.credentialService = credentialService;
        this.credentialFactory = credentialFactory;
        this.deviceRegistryService = deviceRegistryService;
        this.userService = userService;
        this.userFactory = userFactory;
        this.accessInfoService = accessInfoService;
        this.accessInfoFactory = accessInfoFactory;
        this.permissionFactory = permissionFactory;
    }

    @Override
    public Set<? extends RegistrationProcessor> createAll() {
        final Optional<Settings> result = SimpleRegistrationProcessor.Settings.loadSimpleSettings(userService, simpleSetting);
        return result
                .map(settings -> new SimpleRegistrationProcessor(
                        accountService,
                        accountFactory,
                        credentialService,
                        deviceRegistryService,
                        userService,
                        accessInfoService,
                        permissionFactory,
                        simpleSetting,
                        "preferred_username",
                        settings))
                .map(Collections::singleton)
                .orElseGet(Collections::emptySet);
    }

}
