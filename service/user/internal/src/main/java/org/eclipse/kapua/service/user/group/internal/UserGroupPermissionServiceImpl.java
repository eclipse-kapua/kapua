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
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionCreator;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupPermissionService;
import org.eclipse.kapua.service.user.group.UserGroupService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link UserGroupPermissionService} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class UserGroupPermissionServiceImpl implements UserGroupPermissionService {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final UserGroupService userGroupService;
    private final GroupPermissionService groupPermissionService;

    @Inject
    public UserGroupPermissionServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            UserGroupService userGroupService,
            GroupPermissionService groupPermissionService
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.userGroupService = userGroupService;
        this.groupPermissionService = groupPermissionService;
    }

    @Override
    public GroupPermission create(GroupPermissionCreator groupPermissionCreator)
            throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(groupPermissionCreator, "groupPermissionCreator");
        ArgumentValidator.notNull(groupPermissionCreator.getGroupId(), "groupPermissionCreator.groupId");
        ArgumentValidator.notNull(groupPermissionCreator.getPermission(), "groupPermissionCreator.permission");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.write, groupPermissionCreator.getScopeId()));

        // Check User Group Existence
        UserGroup userGroup = userGroupService.find(groupPermissionCreator.getScopeId(), groupPermissionCreator.getGroupId());

        if (userGroup == null) {
            throw new KapuaEntityNotFoundException(UserGroup.TYPE, groupPermissionCreator.getGroupId());
        }

        // Do create
        return groupPermissionService.create(groupPermissionCreator);
    }

    @Override
    public GroupPermission find(KapuaId scopeId, KapuaId groupPermissionId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupPermissionId, KapuaEntityAttributes.ENTITY_ID);

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, scopeId));

        // Do Find
        return groupPermissionService.find(scopeId, groupPermissionId);
    }

    @Override
    public GroupPermissionListResult findByGroupId(KapuaId scopeId, KapuaId userGroupId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(userGroupId, "userGroupId");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, scopeId));

        // Check User Group Existence
        UserGroup userGroup = userGroupService.find(scopeId, userGroupId);

        if (userGroup == null) {
            throw new KapuaEntityNotFoundException(UserGroup.TYPE, userGroupId);
        }

        // Do find
        return groupPermissionService.findByGroupId(scopeId, userGroupId);
    }

    @Override
    public GroupPermissionListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, query.getScopeId()));

        // Do query
        return groupPermissionService.query(query);
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.read, query.getScopeId()));

        // Do query
        return groupPermissionService.count(query);
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId groupPermissionId) throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupPermissionId, KapuaEntityAttributes.ENTITY_ID);

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER_GROUP, Actions.delete, scopeId));

        // Do delete
        groupPermissionService.delete(scopeId, groupPermissionId);
    }
}
