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

import javax.inject.Inject;
import javax.inject.Singleton;

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
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoRepository;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleAttributes;
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleRepository;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermissionAttributes;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link AccessRoleService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessRoleServiceImpl implements AccessRoleService {

    private final TxManager txManager;
    private final RoleRepository roleRepository;
    private final AccessInfoRepository accessInfoRepository;
    private final AccessRoleRepository accessRoleRepository;
    private final AuthorizationService authorizationService;

    @Inject
    public AccessRoleServiceImpl(
            TxManager txManager,
            RoleRepository roleRepository,
            AccessInfoRepository accessInfoRepository,
            AccessRoleRepository accessRoleRepository,
            AuthorizationService authorizationService) {
        this.txManager = txManager;
        this.roleRepository = roleRepository;
        this.accessInfoRepository = accessInfoRepository;
        this.accessRoleRepository = accessRoleRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public AccessRole create(AccessRoleCreator accessRoleCreator)
            throws KapuaException {
        ArgumentValidator.notNull(accessRoleCreator, "accessRoleCreator");
        ArgumentValidator.notNull(accessRoleCreator.getAccessInfoId(), "accessRoleCreator.accessInfoId");
        ArgumentValidator.notNull(accessRoleCreator.getRoleId(), "accessRoleCreator.roleId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ACCESS_INFO, Actions.write, accessRoleCreator.getScopeId()));

        return txManager.execute(tx -> {
            // Check that AccessInfo exists
            final AccessInfo accessInfo = accessInfoRepository.find(tx, accessRoleCreator.getScopeId(), accessRoleCreator.getAccessInfoId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(AccessInfo.TYPE, accessRoleCreator.getAccessInfoId()));

            // Check that Role exists
            final Role role = roleRepository.find(tx, accessRoleCreator.getScopeId(), accessRoleCreator.getRoleId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Role.TYPE, accessRoleCreator.getRoleId()));

            // Check that Role is not already assigned
            final KapuaQuery query = new KapuaQuery(accessRoleCreator.getScopeId());
            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(AccessRoleAttributes.ACCESS_INFO_ID, accessRoleCreator.getAccessInfoId()),
                            query.attributePredicate(RolePermissionAttributes.ROLE_ID, accessRoleCreator.getRoleId())
                    )
            );

            if (accessRoleRepository.count(tx, query) > 0) {
                throw new KapuaDuplicateNameException(role.getName());
            }
            // Do create
            AccessRole accessRole = new AccessRoleImpl(accessRoleCreator.getScopeId());

            accessRole.setAccessInfoId(accessRoleCreator.getAccessInfoId());
            accessRole.setRoleId(accessRoleCreator.getRoleId());
            return accessRoleRepository.create(tx, accessRole);
        });
    }

    @Override
    public AccessRole find(KapuaId scopeId, KapuaId accessRoleId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessRoleId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ACCESS_INFO, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> accessRoleRepository.find(tx, scopeId, accessRoleId))
                .orElse(null);
    }

    @Override
    public AccessRoleListResult findByAccessInfoId(KapuaId scopeId, KapuaId accessInfoId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessInfoId, "accessInfoId");

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ACCESS_INFO, Actions.read, scopeId));

        // Check cache
        return txManager.execute(tx -> accessRoleRepository.findByAccessInfoId(tx, scopeId, accessInfoId));
    }

    @Override
    public AccessRoleListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ACCESS_INFO, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> accessRoleRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ACCESS_INFO, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> accessRoleRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId accessRoleId)
            throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessRoleId, KapuaEntityAttributes.ENTITY_ID);

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.ACCESS_INFO, Actions.delete, scopeId));
        // Do delete
        txManager.execute(tx -> accessRoleRepository.delete(tx, scopeId, accessRoleId));
    }
}
