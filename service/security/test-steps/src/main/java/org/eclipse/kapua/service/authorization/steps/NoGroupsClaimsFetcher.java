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
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainFactory;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.shiro.claims.ClaimsFetcher;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * {@link ClaimsFetcher} implementation.
 * A ClaimsFetcher that enumerates all possible domains and actions without taking into account groups (i.e. if a permission is assigned to a group it is not included in the claims).
 * Used for test purposes only
 *
 * @since 2.0.0
 */

public class NoGroupsClaimsFetcher implements ClaimsFetcher {

    private final AuthorizationService authorizationServiceProvider;
    private final PermissionFactory permissionFactory;
    private final DomainRegistryService domainService;
    private final DomainFactory domainFactory;
    private Set<Domain> knownDomains;

    @Inject
    public NoGroupsClaimsFetcher(AuthorizationService authorizationService,
                                 PermissionFactory permissionFactory,
                                 DomainRegistryService domainService,
                                 DomainFactory domainFactory) {
        this.authorizationServiceProvider = authorizationService;
        this.permissionFactory = permissionFactory;
        this.domainService = domainService;
        this.domainFactory = domainFactory;
    }

    @Override
    public Set<String> fetchUserClaims(KapuaId inScope) throws KapuaException {
        if (this.knownDomains == null ) {
            this.knownDomains = KapuaSecurityUtils.doPrivileged(() -> new HashSet<>(domainService.query(domainFactory.newQuery(null)).getItems()));
        }

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
