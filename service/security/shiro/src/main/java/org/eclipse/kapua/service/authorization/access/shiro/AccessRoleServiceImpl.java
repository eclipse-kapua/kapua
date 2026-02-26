/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoRepository;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleAttributes;
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleQuery;
import org.eclipse.kapua.service.authorization.access.AccessRoleRepository;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermissionAttributes;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.storage.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link AccessRoleService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessRoleServiceImpl implements AccessRoleService {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final RoleRepository roleRepository;
    private final AccessInfoRepository accessInfoRepository;
    private final AccessRoleRepository accessRoleRepository;

    @Inject
    public AccessRoleServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            RoleRepository roleRepository,
            AccessInfoRepository accessInfoRepository,
            AccessRoleRepository accessRoleRepository
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.roleRepository = roleRepository;
        this.accessInfoRepository = accessInfoRepository;
        this.accessRoleRepository = accessRoleRepository;
    }

    @Override
    public AccessRole create(AccessRoleCreator accessRoleCreator) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(accessRoleCreator, "accessRoleCreator");
        ArgumentValidator.notNull(accessRoleCreator.getAccessInfoId(), "accessRoleCreator.accessInfoId");
        ArgumentValidator.notNull(accessRoleCreator.getRoleId(), "accessRoleCreator.roleId");

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.write, accessRoleCreator.getScopeId()));

        // Do create
        return txManager.execute(tx -> {
            // Check that AccessInfo exists
            accessInfoRepository
                    .find(tx, accessRoleCreator.getScopeId(), accessRoleCreator.getAccessInfoId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(AccessInfo.TYPE, accessRoleCreator.getAccessInfoId()));

            // Check that Role exists
            final Role role = roleRepository
                    .find(tx, accessRoleCreator.getScopeId(), accessRoleCreator.getRoleId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, accessRoleCreator.getRoleId()));

            // Check that Role is not already assigned
            AccessRoleQuery query = new AccessRoleQueryImpl(accessRoleCreator.getScopeId());
            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(AccessRoleAttributes.ACCESS_INFO_ID, accessRoleCreator.getAccessInfoId()),
                            query.attributePredicate(RolePermissionAttributes.ROLE_ID, accessRoleCreator.getRoleId())
                    )
            );

            // FIXME: This should throw KapuaEntityUniquenessException
            if (accessRoleRepository.count(tx, query) > 0) {
                throw new KapuaDuplicateNameException(role.getName());
            }

            // Create
            AccessRole accessRole = new AccessRoleImpl(accessRoleCreator.getScopeId());
            accessRole.setAccessInfoId(accessRoleCreator.getAccessInfoId());
            accessRole.setRoleId(accessRoleCreator.getRoleId());

            return accessRoleRepository.create(tx, accessRole);
        });
    }

    @Override
    public AccessRole find(KapuaId scopeId, KapuaId accessRoleId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, AccessRoleAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessRoleId, AccessRoleAttributes.ENTITY_ID);

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, scopeId));

        //
        // Do find
        return txManager
                .execute(tx -> accessRoleRepository.find(tx, scopeId, accessRoleId))
                .orElse(null);
    }

    @Override
    public AccessRoleListResult findByAccessInfoId(KapuaId scopeId, KapuaId accessInfoId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, AccessRoleAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessInfoId, "accessInfoId");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, scopeId));

        //
        // Do find
        return txManager.execute(tx -> accessRoleRepository.findByAccessInfoId(tx, scopeId, accessInfoId));
    }

    @Override
    public AccessRoleListResult query(KapuaQuery query) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, query.getScopeId()));

        //
        // Do query
        return txManager.execute(tx -> accessRoleRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, query.getScopeId()));

        //
        // Do count
        return txManager.execute(tx -> accessRoleRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId accessRoleId)
            throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, AccessRoleAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessRoleId, AccessRoleAttributes.ENTITY_ID);

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.delete, scopeId));

        //
        // Do delete
        txManager.execute(tx -> accessRoleRepository.delete(tx, scopeId, accessRoleId));
    }
}
