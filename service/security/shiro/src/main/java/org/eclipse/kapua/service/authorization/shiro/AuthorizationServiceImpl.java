/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authorization.shiro;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;

/**
 * {@link AuthorizationService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AuthorizationServiceImpl implements AuthorizationService {

    private final PermissionFactory permissionFactory;
    private final Set<Domain> knownDomains;
    private final PermissionMapper permissionMapper;

    @Inject
    public AuthorizationServiceImpl(
            PermissionFactory permissionFactory,
            Set<Domain> knownDomains,
            PermissionMapper permissionMapper) {
        this.permissionFactory = permissionFactory;
        this.knownDomains = knownDomains;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public boolean[] isPermitted(List<Permission> permissions) throws KapuaException {
        KapuaSession session = KapuaSecurityUtils.getSession();

        if (session == null) {
            throw new KapuaUnauthenticatedException();
        }
        if (session.isTrustedMode()) {
            boolean[] returnedPermissions = new boolean[permissions.size()];
            Arrays.fill(returnedPermissions, true);
            return returnedPermissions;
        } else {
            List<org.apache.shiro.authz.Permission> permissionsShiro = permissions.stream()
                    .map(permission -> permissionMapper.mapPermission(permission))
                    .collect(Collectors.toList());
            return SecurityUtils.getSubject().isPermitted(permissionsShiro);
        }
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
                                    return this.isPermitted(permission);
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

    @Override
    public boolean isPermitted(Permission permission)
            throws KapuaException {
        KapuaSession session = KapuaSecurityUtils.getSession();

        if (session == null) {
            throw new KapuaUnauthenticatedException();
        }

        return session.isTrustedMode() ||
                SecurityUtils.getSubject().isPermitted(permissionMapper.mapPermission(permission));
    }

    @Override
    public void checkPermission(Permission permission)
            throws KapuaException {
        if (!isPermitted(permission)) {
            throw new SubjectUnauthorizedException(permission);
        }
    }

}
