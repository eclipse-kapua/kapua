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
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupRepository;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleAttributes;
import org.eclipse.kapua.service.authorization.group.GroupRoleCreator;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.group.GroupRoleQuery;
import org.eclipse.kapua.service.authorization.group.GroupRoleService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermissionAttributes;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.storage.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link GroupRoleService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class GroupRoleServiceImpl implements GroupRoleService {

    private final TxManager txManager;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;

    @Inject
    public GroupRoleServiceImpl(
            TxManager txManager,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupRepository groupRepository,
            RoleRepository roleRepository,
            GroupRoleRepository groupRoleRepository
    ) {
        this.txManager = txManager;
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.roleRepository = roleRepository;
        this.groupRepository = groupRepository;
        this.groupRoleRepository = groupRoleRepository;
    }

    @Override
    public GroupRole create(GroupRoleCreator groupRoleCreator)
            throws KapuaException {
        ArgumentValidator.notNull(groupRoleCreator, "groupRoleCreator");
        ArgumentValidator.notNull(groupRoleCreator.getGroupId(), "groupRoleCreator.groupInfoId");
        ArgumentValidator.notNull(groupRoleCreator.getRoleId(), "groupRoleCreator.roleId");
        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.write, groupRoleCreator.getScopeId()));

        return txManager.execute(tx -> {
            // Check that GroupInfo exists
            Group groupInfo = groupRepository.find(tx, groupRoleCreator.getScopeId(), groupRoleCreator.getGroupId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Group.TYPE, groupRoleCreator.getGroupId()));

            // Check that Role exists
            final Role role = roleRepository.find(tx, groupRoleCreator.getScopeId(), groupRoleCreator.getRoleId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, groupRoleCreator.getRoleId()));

            // Check that Role is not already assigned
            GroupRoleQuery query = new GroupRoleQueryImpl(groupRoleCreator.getScopeId());
            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(GroupRoleAttributes.GROUP_ID, groupRoleCreator.getGroupId()),
                            query.attributePredicate(RolePermissionAttributes.ROLE_ID, groupRoleCreator.getRoleId())
                    )
            );

            if (groupRoleRepository.count(tx, query) > 0) {
                throw new KapuaDuplicateNameException(role.getName());
            }
            // Do create
            GroupRole groupRole = new GroupRoleImpl(groupRoleCreator.getScopeId());

            groupRole.setGroupId(groupRoleCreator.getGroupId());
            groupRole.setRoleId(groupRoleCreator.getRoleId());
            return groupRoleRepository.create(tx, groupRole);
        });
    }

    @Override
    public GroupRole find(KapuaId scopeId, KapuaId groupRoleId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupRoleId, KapuaEntityAttributes.ENTITY_ID);
        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId));
        // Do find
        return txManager
                .execute(tx -> groupRoleRepository.find(tx, scopeId, groupRoleId))
                .orElse(null);
    }

    @Override
    public GroupRoleListResult findByGroupId(KapuaId scopeId, KapuaId groupInfoId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupInfoId, "groupInfoId");

        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId));

        // Check cache
        return txManager.execute(tx -> groupRoleRepository.findByGroupId(tx, scopeId, groupInfoId));
    }

    @Override
    public GroupRoleListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> groupRoleRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> groupRoleRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId groupRoleId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupRoleId, KapuaEntityAttributes.ENTITY_ID);

        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.delete, scopeId));
        // Do delete
        txManager.execute(tx -> groupRoleRepository.delete(tx, scopeId, groupRoleId));
    }
}
