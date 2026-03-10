/*******************************************************************************
 * Copyright (c) 2025, 2026 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupAttributes;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionService;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupRepository;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.group.GroupRoleService;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupCreator;
import org.eclipse.kapua.service.user.group.UserGroupListResult;
import org.eclipse.kapua.service.user.group.UserGroupService;
import org.eclipse.kapua.storage.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link UserGroupService} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class UserGroupServiceImpl implements UserGroupService {

    private final TxManager txManager;
    private final GroupService groupService;
    private final GroupFactory groupFactory;
    private final GroupQueryHelper groupQueryHelper;
    private final GroupPermissionService groupPermissionService;
    private final GroupRoleService groupRoleService;
    private final RolePermissionService rolePermissionService;
    private final UserGroupServiceValidationUtils userGroupServiceValidationUtils;

    /**
     * Injectable constructor
     *
     * @param groupService The {@link GroupRepository} instance.
     * @since 2.0.0
     */
    @Inject
    public UserGroupServiceImpl(
            TxManager txManager,
            GroupService groupService,
            GroupFactory groupFactory,
            GroupQueryHelper groupQueryHelper,
            GroupPermissionService groupPermissionService,
            GroupRoleService groupRoleService,
            RolePermissionService rolePermissionService,
            UserGroupServiceValidationUtils userGroupServiceValidationUtils
    ) {
        this.txManager = txManager;
        this.groupService = groupService;
        this.groupFactory = groupFactory;
        this.groupQueryHelper = groupQueryHelper;
        this.groupPermissionService = groupPermissionService;
        this.groupRoleService = groupRoleService;
        this.rolePermissionService = rolePermissionService;
        this.userGroupServiceValidationUtils = userGroupServiceValidationUtils;
    }

    @Override
    public UserGroup create(UserGroupCreator userGroupCreator) throws KapuaException {
        // Validate preconditions
        userGroupServiceValidationUtils.validateCreatePreConditions(userGroupCreator);

        // Convert
        GroupCreator groupCreator = groupFactory.newCreator(userGroupCreator.getScopeId());
        groupCreator.setName(userGroupCreator.getName());
        groupCreator.setDescription(userGroupCreator.getDescription());
        groupCreator.setDomain(Domains.USER);
        groupCreator.setTagIds(userGroupCreator.getTagIds());

        // Do create
        Group group = KapuaSecurityUtils.doPrivileged(() -> groupService.create(groupCreator));

        // Return result
        return new UserGroupImpl(group);
    }

    @Override
    public UserGroup update(UserGroup userGroup) throws KapuaException {
        // Validate preconditions
        userGroupServiceValidationUtils.validateUpdatePreConditions(userGroup);

        // Convert
        Group group = groupFactory.newEntity(userGroup.getScopeId());
        group.setId(userGroup.getId());
        group.setTagIds(userGroup.getTagIds());
        group.setName(userGroup.getName());
        group.setDomain(Domains.USER);
        group.setDescription(userGroup.getDescription());
        group.setEntityAttributes(userGroup.getEntityAttributes());
        group.setEntityProperties(userGroup.getEntityProperties());
        group.setOptlock(userGroup.getOptlock());

        // Do update
        Group updatedGroup = KapuaSecurityUtils.doPrivileged(() -> groupService.update(group));

        // Return result
        return new UserGroupImpl(updatedGroup);
    }

    @Override
    public UserGroup find(KapuaId scopeId, KapuaId userGroupId) throws KapuaException {
        // Validate preconditions
        userGroupServiceValidationUtils.validateFindPreConditions(scopeId, userGroupId);

        // Do find
        Group group = KapuaSecurityUtils.doPrivileged(() -> groupService.find(scopeId, userGroupId));

        if (!group.getDomain().equals(Domains.USER)) {
            throw new KapuaEntityNotFoundException(UserGroup.TYPE, userGroupId);
        }

        // Convert
        UserGroup userGroup = new UserGroupImpl(group);

        // Validate post-conditions
        userGroupServiceValidationUtils.validateFindPostConditions(userGroup);

        // Return result
        return userGroup;
    }

    @Override
    public Set<Permission> fetchPermissions(KapuaId scopeId, KapuaId userGroupId) throws KapuaException {
        // Validate preconditions
        userGroupServiceValidationUtils.validateFetchPermissionPreConditions(scopeId, userGroupId);

        //
        // Retrieve Permissions

        // From GroupPermission
        GroupPermissionListResult groupPermissions = groupPermissionService.findByGroupId(scopeId, userGroupId);

        Set<Permission> userGroupPermissions = groupPermissions
                .getItems()
                .stream()
                .map(groupPermission -> (Permission) groupPermission.getPermission())
                .collect(Collectors.toSet());

        // From GroupRoles
        GroupRoleListResult groupRoles = groupRoleService.findByGroupId(scopeId, userGroupId);

        for (GroupRole groupRole : groupRoles.getItems()) {
            RolePermissionListResult rolePermissions = rolePermissionService.findByRoleId(groupRole.getScopeId(), groupRole.getRoleId());

            userGroupPermissions.addAll(
                    rolePermissions
                            .getItems()
                            .stream()
                            .map(rolePermission -> (Permission) rolePermission.getPermission())
                            .collect(Collectors.toSet())
            );
        }

        //
        // Return result
        return userGroupPermissions;
    }

    @Override
    public UserGroupListResult query(KapuaQuery query) throws KapuaException {
        // Validate preconditions
        userGroupServiceValidationUtils.validateQueryPreConditions(query);

        // Convert
        GroupQuery groupQuery = groupFactory.newQuery(query.getScopeId());
        groupQuery.setAskTotalCount(query.getAskTotalCount());
        groupQuery.setLimit(query.getLimit());
        groupQuery.setOffset(query.getOffset());
        groupQuery.setSortCriteria(query.getSortCriteria());
        groupQuery.setFetchAttributes(query.getFetchAttributes());

        QueryPredicate queryPredicate;
        if (query.getPredicate() != null) {
            queryPredicate = groupQuery.andPredicate(
                groupQuery.attributePredicate(GroupAttributes.DOMAIN, Domains.USER),
                query.getPredicate()
            );
        }
        else {
            queryPredicate = groupQuery.attributePredicate(GroupAttributes.DOMAIN, Domains.USER);
        }

        groupQuery.setPredicate(queryPredicate);

        // Do query
        txManager.execute(tx -> {
            groupQueryHelper.handleGroupVisibility(Domains.USER, groupQuery);
            return null;
        });

        GroupListResult groups = KapuaSecurityUtils.doPrivileged(() -> groupService.query(groupQuery));

        // Convert
        UserGroupListResult userGroups = new UserGroupListResultImpl();
        userGroups.setLimitExceeded(groups.isLimitExceeded());
        userGroups.setTotalCount(groups.getTotalCount());
        userGroups.addItems(
            groups.getItems()
                  .stream()
                  .map(UserGroupImpl::new)
            .collect(Collectors.toList())
        );

        // Return result
        return userGroups;
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Validate preconditions
        userGroupServiceValidationUtils.validateCountPreConditions(query);

        // Convert
        GroupQuery groupQuery = groupFactory.newQuery(query.getScopeId());
        groupQuery.setAskTotalCount(query.getAskTotalCount());
        groupQuery.setLimit(query.getLimit());
        groupQuery.setOffset(query.getOffset());
        groupQuery.setSortCriteria(query.getSortCriteria());
        groupQuery.setFetchAttributes(query.getFetchAttributes());

        QueryPredicate queryPredicate;
        if (query.getPredicate() != null) {
            queryPredicate = groupQuery.andPredicate(
                    groupQuery.attributePredicate(GroupAttributes.DOMAIN, "user"),
                    query.getPredicate()
            );
        }
        else {
            queryPredicate = groupQuery.attributePredicate(GroupAttributes.DOMAIN, "user");
        }

        groupQuery.setPredicate(queryPredicate);

        // Do count
        txManager.execute(tx -> {
            groupQueryHelper.handleGroupVisibility(Domains.USER, groupQuery);
            return null;
        });

        return KapuaSecurityUtils.doPrivileged(() -> groupService.count(query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId userGroupId) throws KapuaException {
        // Validate preconditions
        userGroupServiceValidationUtils.validateDeletePreConditions(scopeId, userGroupId);

        // Do delete
        KapuaSecurityUtils.doPrivileged(() -> groupService.delete(scopeId, userGroupId));
    }
}
