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

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoAttributes;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.authorization.access.AccessInfoRepository;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionCreator;
import org.eclipse.kapua.service.authorization.access.AccessPermissionFactory;
import org.eclipse.kapua.service.authorization.access.AccessPermissionRepository;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.eclipse.kapua.service.authorization.access.AccessRoleFactory;
import org.eclipse.kapua.service.authorization.access.AccessRoleRepository;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionValidator;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AccessInfoService} implementation based on JPA.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessInfoServiceImpl implements AccessInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessInfoServiceImpl.class);
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final RoleRepository roleRepository;
    private final AccessRoleRepository accessRoleRepository;
    private final AccessRoleFactory accessRoleFactory;
    private final AccessInfoRepository accessInfoRepository;
    private final AccessInfoFactory accessInfoFactory;
    private final AccessPermissionRepository accessPermissionRepository;
    private final AccessPermissionFactory accessPermissionFactory;
    private final PermissionValidator permissionValidator;

    public AccessInfoServiceImpl(AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            RoleRepository roleRepository,
            AccessRoleFactory accessRoleFactory,
            AccessRoleRepository accessRoleRepository,
            AccessInfoRepository accessInfoRepository,
            AccessInfoFactory accessInfoFactory,
            AccessPermissionRepository accessPermissionRepository,
            AccessPermissionFactory accessPermissionFactory,
            PermissionValidator permissionValidator) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.roleRepository = roleRepository;
        this.accessRoleFactory = accessRoleFactory;
        this.accessRoleRepository = accessRoleRepository;
        this.accessInfoRepository = accessInfoRepository;
        this.accessInfoFactory = accessInfoFactory;
        this.accessPermissionRepository = accessPermissionRepository;
        this.accessPermissionFactory = accessPermissionFactory;
        this.permissionValidator = permissionValidator;
    }

    @Override
    public AccessInfo create(AccessInfoCreator accessInfoCreator)
            throws KapuaException {
        ArgumentValidator.notNull(accessInfoCreator, "accessInfoCreator");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.write, accessInfoCreator.getScopeId()));
        // If permission are created out of the access info scope, check that the current user has the permission on the external scopeId.
        if (accessInfoCreator.getPermissions() != null) {
            for (Permission p : accessInfoCreator.getPermissions()) {
                if (p.getTargetScopeId() == null || !p.getTargetScopeId().equals(accessInfoCreator.getScopeId())) {
                    authorizationService.checkPermission(p);
                }
            }
        }

        permissionValidator.validatePermissions(accessInfoCreator.getPermissions());
        return txManager.execute(tx -> {
            if (accessInfoCreator.getRoleIds() != null) {
                for (KapuaId roleId : accessInfoCreator.getRoleIds()) {
                    // This checks also that the role belong to the same scopeId in which the access info is created
                    Role role = roleRepository.find(tx, accessInfoCreator.getScopeId(), roleId)
                            // If role is not present then roleId does not exist, or it is in another Account scope.
                            .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, roleId));
                }
            }

            final AccessInfo accessInfoToCreate = new AccessInfoImpl(accessInfoCreator.getScopeId());
            accessInfoToCreate.setUserId(accessInfoCreator.getUserId());
            AccessInfo accessInfo = accessInfoRepository.create(tx, accessInfoToCreate);

            if (!accessInfoCreator.getPermissions().isEmpty()) {
                for (Permission p : accessInfoCreator.getPermissions()) {
                    AccessPermissionCreator accessPermissionCreator = new AccessPermissionCreator(accessInfoCreator.getScopeId());

                    accessPermissionCreator.setAccessInfoId(accessInfo.getId());
                    accessPermissionCreator.setPermission(p);

                    AccessPermission accessPermissionToCreate = new AccessPermissionImpl(accessPermissionCreator.getScopeId());

                    accessPermissionToCreate.setAccessInfoId(accessPermissionCreator.getAccessInfoId());
                    accessPermissionToCreate.setPermission(accessPermissionCreator.getPermission());
                    accessPermissionRepository.create(tx, accessPermissionToCreate);
                }
            }

            if (!accessInfoCreator.getRoleIds().isEmpty()) {
                for (KapuaId roleId : accessInfoCreator.getRoleIds()) {
                    AccessRoleCreator accessRoleCreator = new AccessRoleCreator(accessInfoCreator.getScopeId());

                    accessRoleCreator.setAccessInfoId(accessInfo.getId());
                    accessRoleCreator.setRoleId(roleId);
                    AccessRole accessRoleToCreate = new AccessRoleImpl(accessRoleCreator.getScopeId());

                    accessRoleToCreate.setAccessInfoId(accessRoleCreator.getAccessInfoId());
                    accessRoleToCreate.setRoleId(accessRoleCreator.getRoleId());
                    accessRoleRepository.create(tx, accessRoleToCreate);
                }
            }

            return accessInfo;

        });
    }

    @Override
    public AccessInfo find(KapuaId scopeId, KapuaId accessInfoId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, "accountId");
        ArgumentValidator.notNull(accessInfoId, "accessInfoId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, scopeId));

        return txManager.execute(tx -> accessInfoRepository.find(tx, scopeId, accessInfoId))
                .orElse(null);
    }

    @Override
    public AccessInfo findByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {
        ArgumentValidator.notNull(scopeId, "accountId");
        ArgumentValidator.notNull(userId, "userId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, scopeId));

        return txManager.execute(tx -> accessInfoRepository.findByUserId(tx, scopeId, userId))
                .orElse(null);
    }

    @Override
    public AccessInfoListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> accessInfoRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> accessInfoRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId accessInfoId) throws KapuaException {
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.delete, scopeId));

        txManager.execute(tx -> accessInfoRepository.delete(tx, scopeId, accessInfoId));
    }

    //@ListenServiceEvent(fromAddress="account")
    //@ListenServiceEvent(fromAddress="user")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }
        LOGGER.info("AccessInfoService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("user".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteAccessInfoByUserId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        } else if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteAccessInfoByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }

    private void deleteAccessInfoByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {
        final KapuaQuery query = new KapuaQuery(scopeId);
        query.setPredicate(query.attributePredicate(AccessInfoAttributes.USER_ID, userId));

        AccessInfoListResult accessInfosToDelete = query(query);

        for (AccessInfo at : accessInfosToDelete.getItems()) {
            delete(at.getScopeId(), at.getId());
        }
    }

    private void deleteAccessInfoByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        final KapuaQuery query = new KapuaQuery(scopeId);

        AccessInfoListResult accessInfosToDelete = query(query);

        for (AccessInfo at : accessInfosToDelete.getItems()) {
            delete(at.getScopeId(), at.getId());
        }
    }
}
