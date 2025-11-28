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
package org.eclipse.kapua.service.authorization.steps;

import com.google.inject.Provides;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.shiro.claims.ClaimsFetcher;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;

public class AuthorizationModuleTests extends AbstractKapuaModule {

    @Provides
    @Singleton
    @Named("NoGroupsClaimsFetcher")
    ClaimsFetcher noGroupsClaimsFetcher(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            Set<Domain> knownDomains) {
        return new NoGroupsClaimsFetcher(
                authorizationService,
                permissionFactory,
                knownDomains);
    }

    @Override
    protected void configureModule() {
    }

}
