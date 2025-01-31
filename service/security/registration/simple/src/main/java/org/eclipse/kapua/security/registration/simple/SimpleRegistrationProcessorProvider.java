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
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.user.UserService;

public class SimpleRegistrationProcessorProvider implements RegistrationProcessorProvider {

    private final SimpleSetting simpleSetting;
    private final AccountService accountService;
    private final CredentialService credentialService;
    private final DeviceRegistryService deviceRegistryService;
    private final UserService userService;
    private final AccessInfoService accessInfoService;

    @Inject
    public SimpleRegistrationProcessorProvider(
            SimpleSetting simpleSetting,
            AccountService accountService,
            CredentialService credentialService,
            DeviceRegistryService deviceRegistryService,
            UserService userService,
            AccessInfoService accessInfoService) {
        this.simpleSetting = simpleSetting;
        this.accountService = accountService;
        this.credentialService = credentialService;
        this.deviceRegistryService = deviceRegistryService;
        this.userService = userService;
        this.accessInfoService = accessInfoService;
    }

    @Override
    public Set<? extends RegistrationProcessor> createAll() {
        final Optional<Settings> result = SimpleRegistrationProcessor.Settings.loadSimpleSettings(userService, simpleSetting);
        return result
                .map(settings -> new SimpleRegistrationProcessor(
                        accountService,
                        credentialService,
                        deviceRegistryService,
                        userService,
                        accessInfoService,
                        simpleSetting,
                        "preferred_username",
                        settings))
                .map(Collections::singleton)
                .orElseGet(Collections::emptySet);
    }

}
