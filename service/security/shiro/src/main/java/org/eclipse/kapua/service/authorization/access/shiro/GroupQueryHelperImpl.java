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
package org.eclipse.kapua.service.authorization.access.shiro;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoRepository;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionRepository;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleRepository;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainRepository;
import org.eclipse.kapua.service.authorization.group.GroupAttributes;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.group.shiro.GroupPermissionRepository;
import org.eclipse.kapua.service.authorization.group.shiro.GroupQueryImpl;
import org.eclipse.kapua.service.authorization.group.shiro.GroupRoleRepository;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionRepository;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GroupQueryHelperImpl implements GroupQueryHelper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TxManager txManager;
    private final AccessInfoFactory accessInfoFactory;
    private final AccessInfoRepository accessInfoRepository;
    private final AccessPermissionRepository accessPermissionRepository;
    private final AccessRoleRepository accessRoleRepository;
    private final GroupPermissionRepository groupPermissionRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final DomainRepository domainRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    private final UserService userService;

    @Inject
    public GroupQueryHelperImpl(
            TxManager txManager,
            AccessInfoFactory accessInfoFactory,
            AccessInfoRepository accessInfoRepository,
            AccessPermissionRepository accessPermissionRepository,
            AccessRoleRepository accessRoleRepository,
            GroupPermissionRepository groupPermissionRepository,
            GroupRoleRepository groupRoleRepository,
            DomainRepository domainRepository,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            UserService userService
    ) {
        this.txManager = txManager;
        this.accessInfoFactory = accessInfoFactory;
        this.accessInfoRepository = accessInfoRepository;
        this.accessPermissionRepository = accessPermissionRepository;
        this.accessRoleRepository = accessRoleRepository;
        this.groupPermissionRepository = groupPermissionRepository;
        this.groupRoleRepository = groupRoleRepository;
        this.domainRepository = domainRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userService = userService;
    }

    @Override
    public void handleKapuaQueryGroupPredicate(KapuaQuery query, String domain, String groupPredicateName) throws KapuaException {
        final KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
        if (accessInfoFactory != null) {
            if (kapuaSession != null && !kapuaSession.isTrustedMode()) {
                txManager.execute(tx -> {
                    handleKapuaQueryGroupPredicate(tx, kapuaSession, query, domain, groupPredicateName);
                    return null;
                });
            }
        } else {
            logger.warn("'Access Group Permission' feature is disabled");
        }
    }

    @Override
    public void handleGroupVisibility(KapuaQuery query) throws KapuaException {
        final KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
        if (accessInfoFactory != null) {
            if (kapuaSession != null && !kapuaSession.isTrustedMode()) {
                txManager.execute(tx -> {
                    handleGroupVisibility(tx, kapuaSession, query);
                    return null;
                });
            }
        } else {
            logger.warn("'Access Group Permission' feature is disabled");
        }
    }

    private void handleKapuaQueryGroupPredicate(TxContext txContext, KapuaSession kapuaSession, KapuaQuery query, String domain, String groupPredicateName) throws KapuaException {
        try {
            // Gather all groups that the user has access to
            KapuaId userId = kapuaSession.getUserId();

            Optional<AccessInfo> optionalAccessInfo = accessInfoRepository.findByUserId(txContext, kapuaSession.getScopeId(), userId);

            List<Permission> groupPermissions = new ArrayList<>();
            if (optionalAccessInfo.isPresent()) {
                AccessInfo accessInfo = optionalAccessInfo.get();

                // Read from Permission granted to the User
                AccessPermissionListResult accessPermissions = accessPermissionRepository.findByAccessInfoId(txContext, accessInfo.getScopeId(), accessInfo.getId());
                for (AccessPermission accessPermission : accessPermissions.getItems()) {
                    if (checkGroupPermission(domain, groupPermissions, accessPermission.getPermission())) {
                        break;
                    }
                }

                // Read from Roles granted to the User
                AccessRoleListResult accessRoles = accessRoleRepository.findByAccessInfoId(txContext, accessInfo.getScopeId(), accessInfo.getId());
                for (AccessRole accessRole : accessRoles.getItems()) {
                    KapuaId roleId = accessRole.getRoleId();

                    Role role = roleRepository
                            .find(txContext, accessRole.getScopeId(), roleId)
                            .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, roleId));

                    RolePermissionListResult rolePermissions = rolePermissionRepository.findByRoleId(txContext, role.getScopeId(), roleId);
                    for (RolePermission rolePermission : rolePermissions.getItems()) {
                        if (checkGroupPermission(domain, groupPermissions, rolePermission.getPermission())) {
                            break;
                        }
                    }
                }
            }

            // Check User Group Permissions and Roles
            User user = KapuaSecurityUtils.doPrivileged(() -> userService.find(kapuaSession.getScopeId(), userId));

            // For each User Group
            for (KapuaId groupId : user.getGroupIds()) {

                // Read from Permission granted to the User Group
                GroupPermissionListResult userGroupPermissions = groupPermissionRepository.findByGroupId(txContext, user.getScopeId(), groupId);

                for (GroupPermission userGroupPermission : userGroupPermissions.getItems()) {
                    checkGroupPermission(domain, groupPermissions, userGroupPermission.getPermission());
                }

                // Read from Roles granted to the User Group
                GroupRoleListResult userGroupRoles = groupRoleRepository.findByGroupId(txContext, user.getScopeId(), groupId);
                for (GroupRole userGroupRole : userGroupRoles.getItems()) {
                    KapuaId roleId = userGroupRole.getRoleId();

                    Role role = roleRepository
                            .find(txContext, userGroupRole.getScopeId(), roleId)
                            .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, roleId));

                    RolePermissionListResult rolePermissions = rolePermissionRepository.findByRoleId(txContext, role.getScopeId(), roleId);
                    for (RolePermission rolePermission : rolePermissions.getItems()) {
                        if (checkGroupPermission(domain, groupPermissions, rolePermission.getPermission())) {
                            break;
                        }
                    }
                }
            }

            // Create AttributePredicate on Groups if Permission with Groups are granted to User
            AndPredicate andPredicate = query.andPredicate();
            if (!groupPermissions.isEmpty()) {
                int i = 0;
                KapuaId[] groupsIds = new KapuaEid[groupPermissions.size()];
                for (Permission p : groupPermissions) {
                    groupsIds[i++] = p.getGroupId();
                }
                andPredicate.and(query.attributePredicate(groupPredicateName, groupsIds));
            }

            // Merge User-defined query predicates with the AttributePredicate on Groups
            if (query.getPredicate() != null) {
                andPredicate.and(query.getPredicate());
            }

            query.setPredicate(andPredicate);
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error while grouping!");
        }
    }

    private void handleGroupVisibility(TxContext txContext, KapuaSession kapuaSession, KapuaQuery query) throws KapuaException{
        try {
            //
            // Gather all groups that the user has access to
            KapuaId userId = kapuaSession.getUserId();

            Optional<AccessInfo> optionalAccessInfo = accessInfoRepository.findByUserId(txContext, kapuaSession.getScopeId(), userId);

            Set<Permission> allPermissions = new HashSet<>();
            if (optionalAccessInfo.isPresent()) {
                AccessInfo accessInfo = optionalAccessInfo.get();

                // Read from Permission granted to the User
                AccessPermissionListResult accessPermissions = accessPermissionRepository.findByAccessInfoId(txContext, accessInfo.getScopeId(), accessInfo.getId());

                for (AccessPermission accessPermission : accessPermissions.getItems()) {
                    allPermissions.add(accessPermission.getPermission());
                }

                // Read from Roles granted to the User
                AccessRoleListResult accessRoles = accessRoleRepository.findByAccessInfoId(txContext, accessInfo.getScopeId(), accessInfo.getId());
                for (AccessRole ar : accessRoles.getItems()) {
                    KapuaId roleId = ar.getRoleId();

                    Role role = roleRepository
                            .find(txContext, ar.getScopeId(), roleId)
                            .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, roleId));

                    RolePermissionListResult rolePermissions = rolePermissionRepository.findByRoleId(txContext, role.getScopeId(), roleId);
                    for (RolePermission rolePermission : rolePermissions.getItems()) {
                        allPermissions.add(rolePermission.getPermission());
                    }
                }
            }

            // Group Access
            User user = KapuaSecurityUtils.doPrivileged(() ->userService.find(kapuaSession.getScopeId(), userId));

            for (KapuaId userGroupId : user.getGroupIds()) {
                // Group Permission
                GroupPermissionListResult userGroupPermissions = groupPermissionRepository.findByGroupId(txContext, user.getScopeId(), userGroupId);

                for (GroupPermission userGroupPermission : userGroupPermissions.getItems()) {
                    allPermissions.add(userGroupPermission.getPermission());
                }

                // Group Roles
                GroupRoleListResult userGroupRoles = groupRoleRepository.findByGroupId(txContext, user.getScopeId(), userGroupId);

                for (GroupRole userGroupRole : userGroupRoles.getItems()) {
                    Optional<Role> optionalRole = roleRepository.find(txContext, userGroupRole.getScopeId(), userGroupRole.getRoleId());

                    if (optionalRole.isPresent()) {
                        Role role = optionalRole.get();

                        RolePermissionListResult rolePermissions = rolePermissionRepository.findByRoleId(txContext, role.getScopeId(), role.getId());
                        for (RolePermission rp : rolePermissions.getItems()) {
                            allPermissions.add(rp.getPermission());
                        }
                    }
                }
            }

            //
            // Extract list of Groups
            // FIXME: make this load once
            Map<String, Domain> allDomains =
                this.domainRepository.query(
                                txManager.getTxContext(),
                                new GroupQueryImpl(KapuaId.ANY)
                        )
                        .getItemsAsMap(Domain::getName);

            Set<KapuaId> groupIds = new HashSet<>();
            for (Permission permission : allPermissions) {
                Domain permissionDomain = allDomains.get(permission.getDomain());

                if (permissionDomain == null || permissionDomain.getGroupable()) {
                    if (permission.getGroupId() != null) {
                        groupIds.add(permission.getGroupId());
                    }
                    else {
                        groupIds.clear();
                        break;
                    }
                }
            }

            //
            // Create AttributePredicate on Group.id if there is a specific set of AccessGroup in Permissions
            AndPredicate andPredicate = query.andPredicate();
            if (!groupIds.isEmpty()) {
                andPredicate.and(
                        query.attributePredicate(GroupAttributes.ENTITY_ID, groupIds)
                );
            }

            //
            // Merge User-defined query predicates with the AttributePredicate on Groups
            if (query.getPredicate() != null) {
                andPredicate.and(query.getPredicate());
            }

            query.setPredicate(andPredicate);
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error filtering visible domains! Error: " + e.getMessage());
        }
    }

    private static boolean checkGroupPermission(@NonNull String domain, @NonNull List<Permission> groupPermissions, @NonNull Permission permission) {
        if ((permission.getDomain() == null || domain.equals(permission.getDomain())) &&
                (permission.getAction() == null || Actions.read.equals(permission.getAction()))) {
            if (permission.getGroupId() == null) {
                groupPermissions.clear();
                return true;
            } else {
                groupPermissions.add(permission);
            }
        }
        return false;
    }

}
