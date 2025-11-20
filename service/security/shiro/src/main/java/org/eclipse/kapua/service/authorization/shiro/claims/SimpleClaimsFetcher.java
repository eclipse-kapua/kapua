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

import com.google.inject.Provider;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.token.LoginInfo;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermission;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleClaimsFetcher implements ClaimsFetcher {

    //Provider added to break circular dependency between AuthZServiceImpl -> AuthNServiceImpl -> CertificateServiceImpl -> AuthZServiceImpl
    private final Provider<AuthenticationService> authenticationServiceProvider;
    private final Provider<AuthorizationService> authorizationServiceProvider;
    private final Set<Domain> knownDomains;
    private final PermissionFactory permissionFactory;

    @Inject
    public SimpleClaimsFetcher(Provider<AuthorizationService> authorizationServiceProvider,
                               Provider<AuthenticationService> authenticationServiceProvider,
                               PermissionFactory permissionFactory,
                               Set<Domain> knownDomains) {
        this.authorizationServiceProvider = authorizationServiceProvider;
        this.authenticationServiceProvider = authenticationServiceProvider;
        this.permissionFactory = permissionFactory;
        this.knownDomains = knownDomains;
    }

    @Override
    public Set<String> fetchUserClaims(KapuaId inScope) throws KapuaException {
        LoginInfo loginInfo = authenticationServiceProvider.get().getLoginInfo();
        //Retrieve all permissions from both AccessPermissions and RolePermissions
        Set<AccessPermission> accessPermissions = loginInfo.getAccessPermission();
        Set<RolePermission> accessRolePermissions = loginInfo.getRolePermission();
        Set<Permission> permissions = accessPermissions.stream().map(ap -> (Permission) ap.getPermission()).collect(Collectors.toSet());
        Set<Permission> permissionsRole = accessRolePermissions.stream().map(ap -> (Permission) ap.getPermission()).collect(Collectors.toSet());
        permissions.addAll(permissionsRole);

        Set<String> claims = new java.util.HashSet<>();
        List<String> domains = new ArrayList<>();
        List<Actions> actions = new ArrayList<>();

        for (Permission p : permissions) {
            resolveDomain(p, domains); //Resolve domains considering the "*" a possibility
            for (String d : domains) {
                resolveAction(p, d, actions); //Resolve actions considering the "*" a possibility
                for (Actions a : actions) {
                    try {
                        final Permission permission = permissionFactory.newPermission(d, a, inScope, p.getGroupId()); //GroupId could be null (no group) or a specific group
                        if (this.authorizationServiceProvider.get().isPermitted(permission)) {
                            claims.add(String.format("%s:%s", d, a));
                        }
                    } catch (KapuaException e) {
                        // Ignore (don't add claim)
                    }
                }
                actions.clear();
            }
            domains.clear();
        }
        return claims;
    }

    private void resolveDomain(Permission p, List<String> domains) {
        if (p.getDomain() != null) { //Specific domain
            domains.add(p.getDomain());
        } else { //All domains
            for (Domain d : knownDomains) {
                domains.add(d.getName());
            }
        }
    }

    private void resolveAction(Permission p, String d, List<Actions> actions) {
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
