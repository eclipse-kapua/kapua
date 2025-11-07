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
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.user.group.internal;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleCreator;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.group.GroupRoleService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupRoleService;
import org.eclipse.kapua.service.user.group.UserGroupService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link UserGroupRoleService} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class UserGroupRoleServiceImpl implements UserGroupRoleService {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final GroupRoleService groupRoleService;
    private final UserGroupService userGroupService;

    @Inject
    public UserGroupRoleServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupRoleService groupRoleService,
            UserGroupService userGroupService
    ) {
        this.authorizationService = authorizationService;
        this.userGroupService = userGroupService;
        this.permissionFactory = permissionFactory;
        this.groupRoleService = groupRoleService;
    }

    @Override
    public GroupRole create(GroupRoleCreator groupRoleCreator)
            throws KapuaException {
        // Argument validation

        ArgumentValidator.notNull(groupRoleCreator, "groupRoleCreator");
        ArgumentValidator.notNull(groupRoleCreator.getGroupId(), "groupRoleCreator.groupInfoId");
        ArgumentValidator.notNull(groupRoleCreator.getRoleId(), "groupRoleCreator.roleId");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.write, groupRoleCreator.getScopeId()));

        // Check User Group Existence
        UserGroup userGroup = userGroupService.find(groupRoleCreator.getScopeId(), groupRoleCreator.getGroupId());

        if (userGroup == null) {
            throw new KapuaEntityNotFoundException(UserGroup.TYPE, groupRoleCreator.getGroupId());
        }

        return groupRoleService.create(groupRoleCreator);
    }

    @Override
    public GroupRole find(KapuaId scopeId, KapuaId groupRoleId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupRoleId, KapuaEntityAttributes.ENTITY_ID);

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, scopeId));

        // Do find
        return groupRoleService.find(scopeId, groupRoleId);
    }

    @Override
    public GroupRoleListResult findByGroupId(KapuaId scopeId, KapuaId userGroupId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(userGroupId, "userGroupId");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, scopeId));

        // Check cache
        return groupRoleService.findByGroupId(scopeId, userGroupId);
    }

    @Override
    public GroupRoleListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, query.getScopeId()));

        // Do query
        return groupRoleService.query(query);
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, query.getScopeId()));

        // Do count
        return groupRoleService.count(query);
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId groupRoleId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupRoleId, KapuaEntityAttributes.ENTITY_ID);

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.delete, scopeId));

        // Do delete
        groupRoleService.delete(scopeId, groupRoleId);
    }
}
