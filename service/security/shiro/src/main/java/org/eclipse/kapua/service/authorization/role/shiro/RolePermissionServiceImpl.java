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
package org.eclipse.kapua.service.authorization.role.shiro;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionValidator;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionAttributes;
import org.eclipse.kapua.service.authorization.role.RolePermissionCreator;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionRepository;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link RolePermission} service implementation.
 *
 * @since 1.0
 */
@Singleton
public class RolePermissionServiceImpl implements RolePermissionService {

    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionValidator permissionValidator;

    public RolePermissionServiceImpl(
            AuthorizationService authorizationService,
            TxManager txManager,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            PermissionValidator permissionValidator) {
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionValidator = permissionValidator;
    }

    @Override
    public RolePermission create(RolePermissionCreator rolePermissionCreator)
            throws KapuaException {
        ArgumentValidator.notNull(rolePermissionCreator, "rolePermissionCreator");
        ArgumentValidator.notNull(rolePermissionCreator.getRoleId(), "rolePermissionCreator.roleId");
        ArgumentValidator.notNull(rolePermissionCreator.getPermission(), "rolePermissionCreator.permission");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.write, rolePermissionCreator.getScopeId()));

        return txManager.execute(tx -> {
            // Check role existence
            final Role role = roleRepository.find(tx, rolePermissionCreator.getScopeId(), rolePermissionCreator.getRoleId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, rolePermissionCreator.getRoleId()));
            // Check that the given permission matches the definition of the Domains.
            permissionValidator.validatePermission(rolePermissionCreator.getPermission());
            // If permission are created out of the role permission scope, check that the current user has the permission on the external scopeId.
            Permission permission = rolePermissionCreator.getPermission();
            if (permission.getTargetScopeId() == null || !permission.getTargetScopeId().equals(rolePermissionCreator.getScopeId())) {
                authorizationService.checkPermission(permission);
            }
            // Check duplicates
            KapuaQuery query = new KapuaQuery(rolePermissionCreator.getScopeId());
            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(KapuaEntityAttributes.SCOPE_ID, rolePermissionCreator.getScopeId()),
                            query.attributePredicate(RolePermissionAttributes.ROLE_ID, rolePermissionCreator.getRoleId()),
                            query.attributePredicate(RolePermissionAttributes.PERMISSION_DOMAIN, rolePermissionCreator.getPermission().getDomain()),
                            query.attributePredicate(RolePermissionAttributes.PERMISSION_ACTION, rolePermissionCreator.getPermission().getAction()),
                            query.attributePredicate(RolePermissionAttributes.PERMISSION_TARGET_SCOPE_ID, rolePermissionCreator.getPermission().getTargetScopeId()),
                            query.attributePredicate(RolePermissionAttributes.PERMISSION_GROUP_ID, rolePermissionCreator.getPermission().getGroupId()),
                            query.attributePredicate(RolePermissionAttributes.PERMISSION_FORWARDABLE, rolePermissionCreator.getPermission().getForwardable())
                    )
            );
            if (rolePermissionRepository.count(tx, query) > 0) {
                List<Map.Entry<String, Object>> uniquesFieldValues = new ArrayList<>();

                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(KapuaEntityAttributes.SCOPE_ID, rolePermissionCreator.getScopeId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(RolePermissionAttributes.ROLE_ID, rolePermissionCreator.getRoleId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(RolePermissionAttributes.PERMISSION_DOMAIN, rolePermissionCreator.getPermission().getDomain()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(RolePermissionAttributes.PERMISSION_ACTION, rolePermissionCreator.getPermission().getAction()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(RolePermissionAttributes.PERMISSION_TARGET_SCOPE_ID, rolePermissionCreator.getPermission().getTargetScopeId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(RolePermissionAttributes.PERMISSION_GROUP_ID, rolePermissionCreator.getPermission().getGroupId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(RolePermissionAttributes.PERMISSION_FORWARDABLE, rolePermissionCreator.getPermission().getForwardable()));

                throw new KapuaEntityUniquenessException(RolePermission.TYPE, uniquesFieldValues);
            }
            RolePermission rolePermission = new RolePermissionImpl(rolePermissionCreator.getScopeId());

            rolePermission.setRoleId(rolePermissionCreator.getRoleId());
            rolePermission.setPermission(rolePermissionCreator.getPermission());
            return rolePermissionRepository.create(tx, rolePermission);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId rolePermissionId) throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(rolePermissionId, KapuaEntityAttributes.ENTITY_ID);

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.delete, scopeId));

        if (KapuaId.ONE.equals(rolePermissionId)) {
            throw new KapuaException(KapuaErrorCodes.PERMISSION_DELETE_NOT_ALLOWED);
        }
        txManager.execute(tx -> rolePermissionRepository.delete(tx, scopeId, rolePermissionId));
    }

    @Override
    public RolePermission find(KapuaId scopeId, KapuaId rolePermissionId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(rolePermissionId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.read, scopeId));

        return txManager.execute(tx -> rolePermissionRepository.find(tx, scopeId, rolePermissionId))
                .orElse(null);
    }

    @Override
    public RolePermissionListResult findByRoleId(KapuaId scopeId, KapuaId roleId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(roleId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.read, scopeId));

        return txManager.execute(tx -> rolePermissionRepository.findByRoleId(tx, scopeId, roleId));
    }

    @Override
    public RolePermissionListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> rolePermissionRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> rolePermissionRepository.count(tx, query));
    }
}
