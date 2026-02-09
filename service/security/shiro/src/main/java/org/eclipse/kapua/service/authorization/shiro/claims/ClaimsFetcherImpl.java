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
package org.eclipse.kapua.service.authorization.shiro.claims;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.token.LoginInfo;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.domain.DomainFactory;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * {@link ClaimsFetcher} implementation.
 * A ClaimsFetcher that enumerates only the domains and actions assigned to the user in session
 *
 * @since 2.0.0
 */

public class ClaimsFetcherImpl implements ClaimsFetcher {

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final DomainRegistryService domainService;
    private final DomainFactory domainFactory;
    private Set<Domain> knownDomains;

    @Inject
    public ClaimsFetcherImpl(
            AuthorizationService authorizationService,
            AuthenticationService authenticationService,
            PermissionFactory permissionFactory,
            DomainRegistryService domainService,
            DomainFactory domainFactory
    ) {
        this.authorizationService = authorizationService;
        this.authenticationService = authenticationService;
        this.permissionFactory = permissionFactory;
        this.domainService = domainService;
        this.domainFactory = domainFactory;
    }

    @Override
    public Set<String> fetchUserClaims(KapuaId inScope) throws KapuaException {
        if (this.knownDomains == null ) {
            this.knownDomains = KapuaSecurityUtils.doPrivileged(() -> new HashSet<>(domainService.query(domainFactory.newQuery(null)).getItems()));
        }

        //
        // Retrieve all permissions from both AccessPermissions and RolePermissions
        LoginInfo loginInfo = authenticationService.getLoginInfo();

        Set<Permission> allPermissions = new HashSet<>();

        allPermissions.addAll(
                loginInfo.getAccessPermission()
                        .stream()
                        .map(accessPermission -> (Permission) accessPermission.getPermission())
                        .collect(Collectors.toList())
        );

        allPermissions.addAll(
                loginInfo.getRolePermission()
                        .stream()
                        .map(rolePermission -> (Permission) rolePermission.getPermission())
                        .collect(Collectors.toList())
        );

        allPermissions.addAll(
                loginInfo.getGroupPermission()
                        .stream()
                        .map(groupPermission -> (Permission) groupPermission.getPermission())
                        .collect(Collectors.toList())
        );

        allPermissions.addAll(
                loginInfo.getGroupRolePermission()
                        .stream()
                        .map(groupRolePermission -> (Permission) groupRolePermission.getPermission())
                        .collect(Collectors.toList())
        );

        //
        // Resolve claims from list of Permissions
        Set<String> claims = new java.util.HashSet<>();
        Queue<String> domains = new java.util.LinkedList<>();
        Queue<Actions> actions = new java.util.LinkedList<>();
        for (Permission permission : allPermissions) {

            resolveDomain(permission, domains); //Resolve domains considering the "*" a possibility

            while (!domains.isEmpty()) {
                String domain = domains.poll();
                resolveAction(permission, domain, actions); //Resolve actions considering the "*" a possibility

                while (!actions.isEmpty()) {
                    Actions action = actions.poll();
                    try {
                        final Permission permissionToCheck = permissionFactory.newPermission(domain, action, inScope, permission.getGroupId()); //GroupId could be null (no group) or a specific group
                        if (authorizationService.isPermitted(permissionToCheck)) {
                            claims.add(String.format("%s:%s", domain, action));
                        }
                    } catch (KapuaException e) {
                        // Ignore (don't add claim)
                    }
                }
            }
        }
        return claims;
    }

    private void resolveDomain(Permission p, Queue<String> domains) {
        if (p.getDomain() != null) { //Specific domain
            domains.add(p.getDomain());
        } else { //All domains
            for (Domain d : knownDomains) {
                domains.add(d.getName());
            }
        }
    }

    private void resolveAction(Permission p, String d, Queue<Actions> actions) {
        if (p.getAction() != null) { //Specific Action
            actions.add(p.getAction());
        } else { //All Actions
            Domain targetDomain = knownDomains.stream()
                    .filter(domain -> d.equals(domain.getName()))
                    .findFirst()
                    .orElse(null);

            actions.addAll(targetDomain.getActions());
        }
    }
}
