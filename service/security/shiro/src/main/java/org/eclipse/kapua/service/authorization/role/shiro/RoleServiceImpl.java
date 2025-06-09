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
package org.eclipse.kapua.service.authorization.role.shiro;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleAttributes;
import org.eclipse.kapua.service.authorization.access.AccessRoleFactory;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionValidator;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleListResult;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionCreator;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionRepository;
import org.eclipse.kapua.service.authorization.role.RoleQuery;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link RoleService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class RoleServiceImpl extends KapuaConfigurableServiceBase implements RoleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RolePermissionFactory rolePermissionFactory;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionValidator permissionValidator;
    public AccessRoleFactory accessRoleFactory;

    public AccessInfoFactory accessInfoFactory;
    public AccessRoleService accessRoleService;

    public AccessInfoService accessInfoService;

    /**
     * Injectable constructor
     *
     * @param authorizationService
     *         The {@link AuthorizationService} instance.
     * @param rolePermissionFactory
     *         The {@link RolePermissionFactory} instance.
     * @param serviceConfigurationManager
     *         The {@link ServiceConfigurationManager} instance.
     * @param txManager
     * @param roleRepository
     * @param rolePermissionRepository
     */
    @Inject
    public RoleServiceImpl(
            AuthorizationService authorizationService,
            RolePermissionFactory rolePermissionFactory,
            AccessRoleFactory accessRoleFactory,
            AccessInfoFactory accessInfoFactory,
            AccessRoleService accessRoleService,
            AccessInfoService accessInfoService,
            ServiceConfigurationManager serviceConfigurationManager,
            TxManager txManager,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            PermissionValidator permissionValidator) {
        super(txManager, serviceConfigurationManager, Domains.ROLE, authorizationService);
        this.rolePermissionFactory = rolePermissionFactory;
        this.accessRoleFactory = accessRoleFactory;
        this.accessInfoFactory = accessInfoFactory;
        this.accessRoleService = accessRoleService;
        this.accessInfoService = accessInfoService;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionValidator = permissionValidator;
    }

    @Override
    public Role create(RoleCreator roleCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(roleCreator, "roleCreator");
        ArgumentValidator.notNull(roleCreator.getScopeId(), "roleCreator.scopeId");
        ArgumentValidator.validateEntityName(roleCreator.getName(), "roleCreator.name");
        ArgumentValidator.notNull(roleCreator.getPermissions(), "roleCreator.permissions");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.write, roleCreator.getScopeId()));

        return txManager.execute(tx -> {
            // Check entity limit
            serviceConfigurationManager.checkAllowedEntities(tx, roleCreator.getScopeId(), "Roles");

            // Check duplicate name
            if (roleRepository.countEntitiesWithNameInScope(tx, roleCreator.getScopeId(), roleCreator.getName()) > 0) {
                throw new KapuaDuplicateNameException(roleCreator.getName());
            }

            // If permission are created out of the role scope, check that the current user has the permission on the external scopeId.
            if (roleCreator.getPermissions() != null) {
                for (Permission p : roleCreator.getPermissions()) {
                    if (p.getTargetScopeId() == null || !p.getTargetScopeId().equals(roleCreator.getScopeId())) {
                        authorizationService.checkPermission(p);
                    }
                }
            }

            // Check that the given permission matches the definition of the Domains.
            permissionValidator.validatePermissions(roleCreator.getPermissions());

            // Do create
            Role newRole = new RoleImpl(roleCreator.getScopeId());

            newRole.setName(roleCreator.getName());
            newRole.setDescription(roleCreator.getDescription());

            final Role createdRole = roleRepository.create(tx, newRole);

            if (!roleCreator.getPermissions().isEmpty()) {
                for (Permission p : roleCreator.getPermissions()) {

                    RolePermissionCreator rolePermissionCreator = new RolePermissionCreator(roleCreator.getScopeId());

                    rolePermissionCreator.setRoleId(createdRole.getId());
                    rolePermissionCreator.setPermission(p);
                    RolePermission rolePermission = new RolePermissionImpl(rolePermissionCreator.getScopeId());

                    rolePermission.setRoleId(rolePermissionCreator.getRoleId());
                    rolePermission.setPermission(rolePermissionCreator.getPermission());
                    rolePermissionRepository.create(tx, rolePermission);
                }
            }
            return createdRole;
        });

    }

    @Override
    public Role update(Role role) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(role, "role");
        ArgumentValidator.notNull(role.getId(), "role.id");
        ArgumentValidator.notNull(role.getScopeId(), "role.scopeId");
        ArgumentValidator.validateEntityName(role.getName(), "role.name");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.write, role.getScopeId()));

        return txManager.execute(tx -> {
            // Check existence
            final Role current = roleRepository.find(tx, role.getScopeId(), role.getId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, role.getId()));
            // Check duplicate name
            if (roleRepository.countOtherEntitiesWithNameInScope(tx, role.getScopeId(), role.getId(), role.getName()) > 0) {
                throw new KapuaDuplicateNameException(role.getName());
            }
            // Do update
            return roleRepository.update(tx, current, role);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId roleId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(roleId, "roleId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.delete, scopeId));

        if (roleId.equals(KapuaId.ONE)) {
            throw new KapuaException(KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR);
        }

        txManager.execute(tx -> roleRepository.delete(tx, scopeId, roleId));
    }

    @Override
    public Role find(KapuaId scopeId, KapuaId roleId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(roleId, "roleId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> roleRepository.find(tx, scopeId, roleId))
                .orElse(null);
    }

    @Override
    public RoleListResult query(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> roleRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ROLE, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> roleRepository.count(tx, query));
    }

    //@ListenServiceEvent(fromAddress="account")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }

        LOG.info("RoleService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteRoleByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }

    private void deleteRoleByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {

        RoleQuery query = new RoleQuery(accountId);

        RoleListResult rolesToDelete = query(query);

        for (Role r : rolesToDelete.getItems()) {
            delete(r.getScopeId(), r.getId());
        }
    }

    public List<KapuaId> userIdsByRoleId(KapuaId scopeId, KapuaId roleId) throws KapuaException {
        KapuaQuery accessRoleQuery = new KapuaQuery(scopeId);
        accessRoleQuery.setPredicate(accessRoleQuery.attributePredicate(AccessRoleAttributes.ROLE_ID, roleId));
        AccessRoleListResult accessRoleListResult = accessRoleService.query(accessRoleQuery);

        KapuaQuery accessInfoQuery = new KapuaQuery(scopeId);
        accessInfoQuery.setPredicate(
                accessInfoQuery.attributePredicate(KapuaEntityAttributes.ENTITY_ID, accessRoleListResult.getItems().stream().map(AccessRole::getAccessInfoId).collect(Collectors.toList())));
        AccessInfoListResult accessInfoListResult = accessInfoService.query(accessInfoQuery);

        return accessInfoListResult.getItems().stream().map(AccessInfo::getUserId).collect(Collectors.toList());
    }
}
