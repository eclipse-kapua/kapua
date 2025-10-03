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
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionRepository;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupQueryHelperImpl implements GroupQueryHelper {
    private final TxManager txManager;
    private final AccessInfoFactory accessInfoFactory;
    private final AccessInfoRepository accessInfoRepository;
    private final AccessPermissionRepository accessPermissionRepository;
    private final AccessRoleRepository accessRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public GroupQueryHelperImpl(
            TxManager txManager,
            AccessInfoFactory accessInfoFactory,
            AccessInfoRepository accessInfoRepository,
            AccessPermissionRepository accessPermissionRepository,
            AccessRoleRepository accessRoleRepository,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository) {
        this.txManager = txManager;
        this.accessInfoFactory = accessInfoFactory;
        this.accessInfoRepository = accessInfoRepository;
        this.accessPermissionRepository = accessPermissionRepository;
        this.accessRoleRepository = accessRoleRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
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

    private void handleKapuaQueryGroupPredicate(TxContext txContext, KapuaSession kapuaSession, KapuaQuery query, String domain, String groupPredicateName) throws KapuaException {
        try {
            KapuaId userId = kapuaSession.getUserId();

            final Optional<AccessInfo> maybeAccessInfo = accessInfoRepository.findByUserId(txContext, kapuaSession.getScopeId(), userId);

            final List<Permission> groupPermissions = new ArrayList<>();
            if (maybeAccessInfo.isPresent()) {
                final AccessInfo accessInfo = maybeAccessInfo.get();
                AccessPermissionListResult accessPermissions = accessPermissionRepository.findByAccessInfoId(txContext, accessInfo.getScopeId(), accessInfo.getId());
                for (AccessPermission ap : accessPermissions.getItems()) {
                    if (checkGroupPermission(domain, groupPermissions, ap.getPermission())) {
                        break;
                    }
                }

                AccessRoleListResult accessRoles = accessRoleRepository.findByAccessInfoId(txContext, accessInfo.getScopeId(), accessInfo.getId());

                for (AccessRole ar : accessRoles.getItems()) {
                    KapuaId roleId = ar.getRoleId();

                    Role role = roleRepository.find(txContext, ar.getScopeId(), roleId)
                            .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, roleId));

                    RolePermissionListResult rolePermissions = rolePermissionRepository.findByRoleId(txContext, role.getScopeId(), roleId);

                    for (RolePermission rp : rolePermissions.getItems()) {
                        if (checkGroupPermission(domain, groupPermissions, rp.getPermission())) {
                            break;
                        }
                    }
                }
            }

            AndPredicate andPredicate = query.andPredicate();
            if (!groupPermissions.isEmpty()) {
                int i = 0;
                KapuaId[] groupsIds = new KapuaEid[groupPermissions.size()];
                for (Permission p : groupPermissions) {
                    groupsIds[i++] = p.getGroupId();
                }
                andPredicate.and(query.attributePredicate(groupPredicateName, groupsIds));
            }

            if (query.getPredicate() != null) {
                andPredicate.and(query.getPredicate());
            }

            query.setPredicate(andPredicate);
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error while grouping!");
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
