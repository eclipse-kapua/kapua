/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.service.user.group.internal;

import com.google.common.collect.Sets;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.CheckStrategy;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessPermissionFactory;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionQuery;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupPermissionAttributes;
import org.eclipse.kapua.service.authorization.group.GroupPermissionFactory;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionQuery;
import org.eclipse.kapua.service.authorization.group.GroupPermissionService;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionAttributes;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionQuery;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.user.UserAttributes;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserRepository;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupCreator;
import org.eclipse.kapua.service.user.internal.UserQueryImpl;
import org.eclipse.kapua.storage.TxManager;

import java.util.Set;

/**
 * {@link UserGroupServiceValidationUtils} implementation.
 *
 * @since 2.1.0
 */
public final class UserGroupServiceValidationUtilsImpl implements UserGroupServiceValidationUtils {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final GroupService groupService;

    private final AccessPermissionService accessPermissionService;
    private final AccessPermissionFactory accessPermissionFactory;
    private final RolePermissionService rolePermissionService;
    private final RolePermissionFactory rolePermissionFactory;
    private final GroupPermissionService groupPermissionService;
    private final GroupPermissionFactory groupPermissionFactory;
    private final UserRepository userRepository;

    private final TxManager txManager;

    public UserGroupServiceValidationUtilsImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupService groupService,
            AccessPermissionService accessPermissionService,
            AccessPermissionFactory accessPermissionFactory,
            RolePermissionService rolePermissionService,
            RolePermissionFactory rolePermissionFactory,
            GroupPermissionService groupPermissionService,
            GroupPermissionFactory groupPermissionFactory,
            UserRepository userRepository,
            TxManager txManager
        ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.groupService = groupService;
        this.accessPermissionService = accessPermissionService;
        this.accessPermissionFactory = accessPermissionFactory;
        this.rolePermissionService = rolePermissionService;
        this.rolePermissionFactory = rolePermissionFactory;
        this.groupPermissionService = groupPermissionService;
        this.groupPermissionFactory = groupPermissionFactory;
        this.userRepository = userRepository;
        this.txManager = txManager;
    }

    @Override
    public void validateCreatePreConditions(UserGroupCreator userGroupCreator) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(userGroupCreator.getScopeId(), "userGroupCreator.scopeId");
        ArgumentValidator.notEmptyOrNull(userGroupCreator.getName(), "userGroupCreator.name");
        ArgumentValidator.validateEntityName(userGroupCreator.getName(), "userGroupCreator.name");

        //
        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.write, userGroupCreator.getScopeId()),
                permissionFactory.newPermission(Domains.USER_GROUP, Actions.write, userGroupCreator.getScopeId())
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);
    }

    @Override
    public void validateUpdatePreConditions(UserGroup userGroup) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(userGroup, "userGroup");
        ArgumentValidator.notNull(userGroup.getId(), "userGroup.id");
        ArgumentValidator.notNull(userGroup.getScopeId(), "userGroup.scopeId");
        ArgumentValidator.validateEntityName(userGroup.getName(), "userGroup.name");

        //
        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.write, userGroup.getScopeId()),
                permissionFactory.newPermission(Domains.USER_GROUP, Actions.write, userGroup.getScopeId())
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);

        // Check correct domain
        checkGroupDomainIsUser(userGroup.getScopeId(), userGroup.getId());
    }

    @Override
    public void validateFindPreConditions(KapuaId scopeId, KapuaId userGroupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userGroupId, "userGroupId");

        // Check access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId),
                permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, scopeId)
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);
    }

    @Override
    public void validateFindPostConditions(UserGroup userGroup) {
        if (userGroup != null) {

        }
    }

    @Override
    public void validateFetchPermissionPreConditions(KapuaId scopeId, KapuaId userGroupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userGroupId, "userGroupId");

        // Check access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId),
                permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, scopeId)
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);

        // Check correct domain
        checkGroupDomainIsUser(scopeId, userGroupId);
    }

    @Override
    public void validateQueryPreConditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()),
                permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, query.getScopeId())
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);
    }

    @Override
    public void validateCountPreConditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()),
                permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, query.getScopeId())
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);
    }

    @Override
    public void validateDeletePreConditions(KapuaId scopeId, KapuaId userGroupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userGroupId, "userGroupId");

        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.delete, scopeId),
                permissionFactory.newPermission(Domains.USER_GROUP, Actions.delete, scopeId)
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);

        // Check correct domain
        checkGroupDomainIsUser(scopeId, userGroupId);

        // Check no-attached Users
        UserListResult users = txManager.execute((txContext) -> {
            UserQuery userQuery = new UserQueryImpl(scopeId);
            userQuery.setPredicate(
                userQuery.attributePredicate(UserAttributes.GROUP_IDS, userGroupId)
            );

            return KapuaSecurityUtils.doPrivileged(() -> userRepository.query(txContext, userQuery));
        });

        if (!users.isEmpty()) {
            // FIXME: Throw proper exception
            throw new KapuaIllegalArgumentException("userGroupId", userGroupId.toString());
        }

        // Check no-attached AccessPermissions
        AccessPermissionQuery accessPermissionQuery = accessPermissionFactory.newQuery(scopeId);
        accessPermissionQuery.setPredicate(
            accessPermissionQuery.attributePredicate(AccessPermissionAttributes.PERMISSION_GROUP_ID, userGroupId)
        );

        AccessPermissionListResult accessPermissions = accessPermissionService.query(accessPermissionQuery);

        if (!accessPermissions.isEmpty()) {
            // FIXME: Throw proper exception
            throw new KapuaIllegalArgumentException("userGroupId", userGroupId.toString());
        }

        // Check no-attached RolePermissions
        RolePermissionQuery rolePermissionQuery = rolePermissionFactory.newQuery(scopeId);
        rolePermissionQuery.setPredicate(
                rolePermissionQuery.attributePredicate(RolePermissionAttributes.PERMISSION_GROUP_ID, userGroupId)
        );

        RolePermissionListResult rolePermissions = rolePermissionService.query(rolePermissionQuery);

        if (!rolePermissions.isEmpty()) {
            // FIXME: Throw proper exception
            throw new KapuaIllegalArgumentException("userGroupId", userGroupId.toString());
        }

        // Check no-attached GroupPermissions
        GroupPermissionQuery groupPermissionQuery = groupPermissionFactory.newQuery(scopeId);
        groupPermissionQuery.setPredicate(
                groupPermissionQuery.attributePredicate(GroupPermissionAttributes.PERMISSION_GROUP_ID, userGroupId)
        );

        GroupPermissionListResult groupPermissions = groupPermissionService.query(groupPermissionQuery);

        if (!groupPermissions.isEmpty()) {
            // FIXME: Throw proper exception
            throw new KapuaIllegalArgumentException("userGroupId", userGroupId.toString());
        }
    }

    //
    // Private methods
    //

    private void checkGroupDomainIsUser(KapuaId scopeId, KapuaId userGroupId) throws KapuaException {
        // Check Group Domain is `user`
        Group group = KapuaSecurityUtils.doPrivileged(() -> groupService.find(scopeId, userGroupId));

        if (group == null || !group.getDomain().equals("user")) {
            throw new KapuaEntityNotFoundException(UserGroup.TYPE, userGroupId);
        }
    }
}
