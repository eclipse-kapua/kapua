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

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionAttributes;
import org.eclipse.kapua.service.authorization.group.GroupPermissionCreator;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionQuery;
import org.eclipse.kapua.service.authorization.group.GroupPermissionService;
import org.eclipse.kapua.service.authorization.group.GroupRepository;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionValidator;
import org.eclipse.kapua.storage.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link GroupPermissionService} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class GroupPermissionServiceImpl implements GroupPermissionService {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final GroupPermissionRepository groupPermissionRepository;
    private final GroupRepository groupRepository;
    private final PermissionValidator permissionValidator;

    @Inject
    public GroupPermissionServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            GroupPermissionRepository groupPermissionRepository,
            GroupRepository groupRepository,
            PermissionValidator permissionValidator
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.groupPermissionRepository = groupPermissionRepository;
        this.groupRepository = groupRepository;
        this.permissionValidator = permissionValidator;
    }

    @Override
    public GroupPermission create(GroupPermissionCreator groupPermissionCreator)
            throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(groupPermissionCreator, "groupPermissionCreator");
        ArgumentValidator.notNull(groupPermissionCreator.getGroupId(), "groupPermissionCreator.groupId");
        ArgumentValidator.notNull(groupPermissionCreator.getPermission(), "groupPermissionCreator.permission");

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.write, groupPermissionCreator.getScopeId()));

        //
        // Validate Permission
        permissionValidator.validatePermission(groupPermissionCreator.getScopeId(), groupPermissionCreator.getPermission());

        //
        // Do create
        return txManager.execute(tx -> {
            // Check duplicates
            GroupPermissionQuery query = new GroupPermissionQueryImpl(groupPermissionCreator.getScopeId());
            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(KapuaEntityAttributes.SCOPE_ID, groupPermissionCreator.getScopeId()),
                            query.attributePredicate(GroupPermissionAttributes.GROUP_ID, groupPermissionCreator.getGroupId()),
                            query.attributePredicate(GroupPermissionAttributes.PERMISSION_DOMAIN, groupPermissionCreator.getPermission().getDomain()),
                            query.attributePredicate(GroupPermissionAttributes.PERMISSION_ACTION, groupPermissionCreator.getPermission().getAction()),
                            query.attributePredicate(GroupPermissionAttributes.PERMISSION_TARGET_SCOPE_ID, groupPermissionCreator.getPermission().getTargetScopeId()),
                            query.attributePredicate(GroupPermissionAttributes.PERMISSION_GROUP_ID, groupPermissionCreator.getPermission().getGroupId()),
                            query.attributePredicate(GroupPermissionAttributes.PERMISSION_FORWARDABLE, groupPermissionCreator.getPermission().getForwardable())
                    )
            );

            if (groupPermissionRepository.count(tx, query) > 0) {
                List<Map.Entry<String, Object>> uniquesFieldValues = new ArrayList<>();

                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(KapuaEntityAttributes.SCOPE_ID, groupPermissionCreator.getScopeId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(GroupPermissionAttributes.GROUP_ID, groupPermissionCreator.getGroupId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(GroupPermissionAttributes.PERMISSION_DOMAIN, groupPermissionCreator.getPermission().getDomain()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(GroupPermissionAttributes.PERMISSION_ACTION, groupPermissionCreator.getPermission().getAction()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(GroupPermissionAttributes.PERMISSION_TARGET_SCOPE_ID, groupPermissionCreator.getPermission().getTargetScopeId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(GroupPermissionAttributes.PERMISSION_GROUP_ID, groupPermissionCreator.getPermission().getGroupId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(GroupPermissionAttributes.PERMISSION_FORWARDABLE, groupPermissionCreator.getPermission().getForwardable()));

                throw new KapuaEntityUniquenessException(GroupPermission.TYPE, uniquesFieldValues);
            }

            // Create
            Group group =
                groupRepository
                    .find(tx, groupPermissionCreator.getScopeId(), groupPermissionCreator.getGroupId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Group.TYPE, groupPermissionCreator.getGroupId()));

            if (!group.getDomain().equals("user")) {
                throw new KapuaIllegalArgumentException("groupPermission.groupId", groupPermissionCreator.getGroupId().toString());
            }

            GroupPermission groupPermission = new GroupPermissionImpl(groupPermissionCreator.getScopeId());
            groupPermission.setGroupId(groupPermissionCreator.getGroupId());
            groupPermission.setPermission(groupPermissionCreator.getPermission());

            return groupPermissionRepository.create(tx, groupPermission);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId groupPermissionId) throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupPermissionId, KapuaEntityAttributes.ENTITY_ID);

        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.delete, scopeId));

        txManager.execute(tx -> groupPermissionRepository.delete(tx, scopeId, groupPermissionId));
    }

    @Override
    public GroupPermission find(KapuaId scopeId, KapuaId groupPermissionId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupPermissionId, KapuaEntityAttributes.ENTITY_ID);

        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId));

        return txManager
                .execute(tx -> groupPermissionRepository.find(tx, scopeId, groupPermissionId))
                .orElse(null);
    }

    @Override
    public GroupPermissionListResult findByGroupId(KapuaId scopeId, KapuaId groupInfoId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(groupInfoId, "groupInfoId");

        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId));

        //
        return txManager.execute(tx -> groupPermissionRepository.findByGroupId(tx, scopeId, groupInfoId));
    }

    @Override
    public GroupPermissionListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");

        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> groupPermissionRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");

        // Check Group
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> groupPermissionRepository.count(tx, query));
    }
}
