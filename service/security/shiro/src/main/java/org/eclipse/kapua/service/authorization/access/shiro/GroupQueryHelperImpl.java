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
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainRepository;
import org.eclipse.kapua.service.authorization.group.GroupAttributes;
import org.eclipse.kapua.service.authorization.group.shiro.GroupQueryImpl;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.group.UserGroupService;
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
    private final AccessInfoService accessInfoService;
    private final AccessInfoFactory accessInfoFactory;
    private final AccessInfoRepository accessInfoRepository;
    private final DomainRepository domainRepository;
    private final UserService userService;
    private final UserGroupService userGroupService;

    @Inject
    public GroupQueryHelperImpl(
            TxManager txManager,
            AccessInfoService accessInfoService,
            AccessInfoFactory accessInfoFactory,
            AccessInfoRepository accessInfoRepository,
            DomainRepository domainRepository,
            UserService userService,
            UserGroupService userGroupService
    ) {
        this.txManager = txManager;
        this.accessInfoService = accessInfoService;
        this.accessInfoFactory = accessInfoFactory;
        this.accessInfoRepository = accessInfoRepository;
        this.domainRepository = domainRepository;
        this.userService = userService;
        this.userGroupService = userGroupService;
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
        handleGroupVisibility(null, query);
    }

    @Override
    public void handleGroupVisibility(String domainFilter, KapuaQuery query) throws KapuaException {
        final KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
        if (accessInfoFactory != null) {
            if (kapuaSession != null && !kapuaSession.isTrustedMode()) {
                txManager.execute(tx -> {
                    handleGroupVisibility(tx, kapuaSession, domainFilter, query);
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

            List<Permission> filteringGroupPermissions = new ArrayList<>();

            // Fetch Permissions from AccessInfo
            if (optionalAccessInfo.isPresent()) {
                AccessInfo accessInfo = optionalAccessInfo.get();

                Set<Permission> accessPermissions = KapuaSecurityUtils.doPrivileged(() -> accessInfoService.fetchPermissions(accessInfo.getScopeId(), accessInfo.getId()));

                for (Permission accessPermission : accessPermissions) {
                    if (checkGroupPermission(domain, filteringGroupPermissions, accessPermission)) {
                        break;
                    }
                }
            }

            // Check User Group Permissions and Roles
            User user = KapuaSecurityUtils.doPrivileged(() -> userService.find(kapuaSession.getScopeId(), userId));

            // Fetch Permissions from User Groups
            for (KapuaId userGroupId : user.getGroupIds()) {
                Set<Permission> userGroupPermissions = KapuaSecurityUtils.doPrivileged(() -> userGroupService.fetchPermissions(user.getScopeId(), userGroupId));

                for (Permission groupPermission : userGroupPermissions) {
                    if (checkGroupPermission(domain, filteringGroupPermissions, groupPermission)) {
                        break;
                    }
                }
            }

            // Create AttributePredicate on Groups if Permission with Groups are granted to User
            AndPredicate andPredicate = query.andPredicate();
            if (!filteringGroupPermissions.isEmpty()) {
                int i = 0;
                KapuaId[] groupsIds = new KapuaEid[filteringGroupPermissions.size()];
                for (Permission p : filteringGroupPermissions) {
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

    private void handleGroupVisibility(TxContext txContext, KapuaSession kapuaSession, String domainFilter, KapuaQuery query) throws KapuaException{
        try {
            //
            // Gather all groups that the user has access to
            KapuaId userId = kapuaSession.getUserId();

            Optional<AccessInfo> optionalAccessInfo = accessInfoRepository.findByUserId(txContext, kapuaSession.getScopeId(), userId);

            Set<Permission> allPermissions = new HashSet<>();
            // Fetch permission from AccessInfo
            if (optionalAccessInfo.isPresent()) {
                AccessInfo accessInfo = optionalAccessInfo.get();

                Set<Permission> accessPermissions = KapuaSecurityUtils.doPrivileged(() -> accessInfoService.fetchPermissions(accessInfo.getScopeId(), accessInfo.getId()));

                allPermissions.addAll(accessPermissions);
            }

            // Fetch Permissions from User Groups
            User user = KapuaSecurityUtils.doPrivileged(() -> userService.find(kapuaSession.getScopeId(), userId));

            for (KapuaId userGroupId : user.getGroupIds()) {
                Set<Permission> userGroupPermissions = KapuaSecurityUtils.doPrivileged(() -> userGroupService.fetchPermissions(user.getScopeId(), userGroupId));

                allPermissions.addAll(userGroupPermissions);
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
                if (domainFilter != null && !domainFilter.equals(permission.getDomain())) {
                    continue;
                }

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
