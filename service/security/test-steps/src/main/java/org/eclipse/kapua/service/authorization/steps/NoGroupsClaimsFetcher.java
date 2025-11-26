/*******************************************************************************
 * Copyright (c) 2021, 2025 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.shiro.claims.ClaimsFetcher;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//A ClaimsFetcher that enumerates all possible domain&actions
//BE AWARE THAT PERFORMS WRONG IN PRESENCE OF PERMISSIONS RESTRICTED TO GROUPS

/**
 * {@link ClaimsFetcher} implementation.
 * A ClaimsFetcher that enumerates all possible domains and actions without taking into account groups (i.e. if a permission is assigned to a group it is not included in the claims).
 * Used for test purposes only
 *
 * @since 2.0.0
 */

public class NoGroupsClaimsFetcher implements ClaimsFetcher {

    private final AuthorizationService authorizationServiceProvider;
    private final Set<Domain> knownDomains;
    private final PermissionFactory permissionFactory;

    @Inject
    public NoGroupsClaimsFetcher(AuthorizationService authorizationService,
                                 PermissionFactory permissionFactory,
                                 Set<Domain> knownDomains) {
        this.authorizationServiceProvider = authorizationService;
        this.permissionFactory = permissionFactory;
        this.knownDomains = knownDomains;
    }

    @Override
    public Set<String> fetchUserClaims(KapuaId inScope) {
        final Set<String> claims = knownDomains.stream()
                .flatMap(domain -> {
                    final Stream<String> domainClaims = domain.getActions()
                            .stream()
                            .filter(action -> {
                                try {
                                    final Permission permission = permissionFactory.newPermission(domain.getName(), action, inScope);
                                    return authorizationServiceProvider.isPermitted(permission);
                                } catch (KapuaException e) {
                                    return false;
                                }
                            })
                            .map(action -> String.format("%s:%s", domain.getName(), action));
                    return domainClaims;
                })
                .collect(Collectors.toSet());
        return claims;
    }

}
