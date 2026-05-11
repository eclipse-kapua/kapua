/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.Provider;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.CheckStrategy;
import org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.shiro.claims.ClaimsFetcher;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;

/**
 * {@link AuthorizationService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserService userService;
    private final PermissionMapper permissionMapper;
    private final Provider<ClaimsFetcher> claimsFetcherProvider; //I want a lazy injection here, to avoid a potential circular dependency and because ClaimsFetcher may not be needed

    @Inject
    public AuthorizationServiceImpl(
            UserService userService,
            PermissionMapper permissionMapper,
            Provider<ClaimsFetcher> claimsFetcherProvider
    ) {
        this.userService = userService;
        this.permissionMapper = permissionMapper;
        this.claimsFetcherProvider = claimsFetcherProvider;
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
    public boolean[] isPermitted(Collection<Permission> permissions) throws KapuaException {
        KapuaSession session = KapuaSecurityUtils.getSession();

        if (session == null) {
            throw new KapuaUnauthenticatedException();
        }

        if (session.isTrustedMode()) {
            boolean[] returnedPermissions = new boolean[permissions.size()];
            Arrays.fill(returnedPermissions, true);
            return returnedPermissions;
        } else {
            List<org.apache.shiro.authz.Permission> permissionsShiro =
                permissions
                    .stream()
                    .map(permissionMapper::mapPermission)
                    .collect(Collectors.toList());

            return SecurityUtils.getSubject().isPermitted(permissionsShiro);
        }
    }

    @Override
    public void checkPermission(Permission permission)
            throws KapuaException {
        if (!isPermitted(permission)) {
            throw new SubjectUnauthorizedException(permission);
        }
    }

    @Override
    public void checkPermission(KapuaId userId, Permission permission) throws KapuaException {
        //
        // Find User
        User user = KapuaSecurityUtils.doPrivileged(()-> userService.findById(userId));

        if (user == null) {
            throw new KapuaEntityNotFoundException(User.TYPE, userId);
        }

        //
        // Build subject on the fly
        Subject.Builder subjectBuilder = new Subject.Builder();
        subjectBuilder.principals(new SimplePrincipalCollection(user, KapuaAuthorizingRealm.REALM_NAME));
        Subject subject = subjectBuilder.buildSubject();

        //
        // Check permission
        if (!subject.isPermitted(permissionMapper.mapPermission(permission))) {
            throw new SubjectUnauthorizedException(permission);
        }
    }

    @Override
    public void checkPermissions(Collection<Permission> permissions) throws KapuaException {
        checkPermissions(permissions, CheckStrategy.ALL_OF);
    }

    @Override
    public void checkPermissions(Collection<Permission> permissions, CheckStrategy checkStrategy) throws KapuaException {
        if (permissions.isEmpty()) {
            return;
        }

        switch (checkStrategy) {
        case ALL_OF: {
            for (Permission permission : permissions) {
                checkPermission(permission);
            }
        }
        break;
        case AT_LEAST_ONE_OF: {
            for (Permission permission : permissions) {
                if (isPermitted(permission)) {
                    return;
                }
            }

            // Mark any of the given permissions as missing permission
            // TODO: add Collection<Permission> to SubjectUnauthorizedException
            throw new SubjectUnauthorizedException(permissions.stream().findAny().get());
        }
        default:
            throw KapuaRuntimeException.internalError("Permission check error. Unrecognised CheckStrategy: " + checkStrategy);
        }
    }

    @Override
    public Set<String> fetchUserClaims(KapuaId inScope) throws KapuaException {
        return claimsFetcherProvider.get().fetchUserClaims(inScope);
    }
}
