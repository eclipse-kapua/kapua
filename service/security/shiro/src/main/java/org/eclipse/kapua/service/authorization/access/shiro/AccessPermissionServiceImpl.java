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

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoRepository;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessPermissionCreator;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionQuery;
import org.eclipse.kapua.service.authorization.access.AccessPermissionRepository;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
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
 * {@link AccessPermissionService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessPermissionServiceImpl implements AccessPermissionService {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final AccessPermissionRepository accessPermissionRepository;
    private final AccessInfoRepository accessInfoRepository;
    private final PermissionValidator permissionValidator;

    @Inject
    public AccessPermissionServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            AccessInfoRepository accessInfoRepository,
            AccessPermissionRepository accessPermissionRepository,
            PermissionValidator permissionValidator
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.accessPermissionRepository = accessPermissionRepository;
        this.accessInfoRepository = accessInfoRepository;
        this.permissionValidator = permissionValidator;
    }

    @Override
    public AccessPermission create(AccessPermissionCreator accessPermissionCreator) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(accessPermissionCreator, "accessPermissionCreator");
        ArgumentValidator.notNull(accessPermissionCreator.getAccessInfoId(), "accessPermissionCreator.accessInfoId");
        ArgumentValidator.notNull(accessPermissionCreator.getPermission(), "accessPermissionCreator.permission");

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.write, accessPermissionCreator.getScopeId()));

        //
        // Validate Permission
        permissionValidator.validatePermission(accessPermissionCreator.getScopeId(), accessPermissionCreator.getPermission());

        //
        // Do create
        return txManager.execute(tx -> {
            // Check AccessInfo existence
            accessInfoRepository
                    .find(tx, accessPermissionCreator.getScopeId(), accessPermissionCreator.getAccessInfoId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(AccessInfo.TYPE, accessPermissionCreator.getAccessInfoId()));

            // Check duplicates
            AccessPermissionQuery query = new AccessPermissionQueryImpl(accessPermissionCreator.getScopeId());
            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(AccessPermissionAttributes.SCOPE_ID, accessPermissionCreator.getScopeId()),
                            query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessPermissionCreator.getAccessInfoId()),
                            query.attributePredicate(AccessPermissionAttributes.PERMISSION_DOMAIN, accessPermissionCreator.getPermission().getDomain()),
                            query.attributePredicate(AccessPermissionAttributes.PERMISSION_ACTION, accessPermissionCreator.getPermission().getAction()),
                            query.attributePredicate(AccessPermissionAttributes.PERMISSION_TARGET_SCOPE_ID, accessPermissionCreator.getPermission().getTargetScopeId()),
                            query.attributePredicate(AccessPermissionAttributes.PERMISSION_GROUP_ID, accessPermissionCreator.getPermission().getGroupId()),
                            query.attributePredicate(AccessPermissionAttributes.PERMISSION_FORWARDABLE, accessPermissionCreator.getPermission().getForwardable())
                    )
            );

            if (accessPermissionRepository.count(tx, query) > 0) {
                List<Map.Entry<String, Object>> uniquesFieldValues = new ArrayList<>();

                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(AccessPermissionAttributes.SCOPE_ID, accessPermissionCreator.getScopeId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(AccessPermissionAttributes.ACCESS_INFO_ID, accessPermissionCreator.getAccessInfoId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(AccessPermissionAttributes.PERMISSION_DOMAIN, accessPermissionCreator.getPermission().getDomain()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(AccessPermissionAttributes.PERMISSION_ACTION, accessPermissionCreator.getPermission().getAction()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(AccessPermissionAttributes.PERMISSION_TARGET_SCOPE_ID, accessPermissionCreator.getPermission().getTargetScopeId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(AccessPermissionAttributes.PERMISSION_GROUP_ID, accessPermissionCreator.getPermission().getGroupId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(AccessPermissionAttributes.PERMISSION_FORWARDABLE, accessPermissionCreator.getPermission().getForwardable()));

                throw new KapuaEntityUniquenessException(AccessPermission.TYPE, uniquesFieldValues);
            }

            // Create
            AccessPermission accessPermission = new AccessPermissionImpl(accessPermissionCreator.getScopeId());
            accessPermission.setAccessInfoId(accessPermissionCreator.getAccessInfoId());
            accessPermission.setPermission(accessPermissionCreator.getPermission());

            return accessPermissionRepository.create(tx, accessPermission);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId accessPermissionId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, AccessPermissionAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessPermissionId, AccessPermissionAttributes.ENTITY_ID);

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.delete, scopeId));

        txManager.execute(tx -> accessPermissionRepository.delete(tx, scopeId, accessPermissionId));
    }

    @Override
    public AccessPermission find(KapuaId scopeId, KapuaId accessPermissionId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, AccessPermissionAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessPermissionId, AccessPermissionAttributes.ENTITY_ID);

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, scopeId));

        //
        // Do find
        return txManager
                .execute(tx -> accessPermissionRepository.find(tx, scopeId, accessPermissionId))
                .orElse(null);
    }

    @Override
    public AccessPermissionListResult findByAccessInfoId(KapuaId scopeId, KapuaId accessInfoId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, AccessPermissionAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessInfoId, "accessInfoId");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, scopeId));

        //
        // Do find
        return txManager.execute(tx -> accessPermissionRepository.findByAccessInfoId(tx, scopeId, accessInfoId));
    }

    @Override
    public AccessPermissionListResult query(KapuaQuery query) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_INFO, Actions.read, query.getScopeId()));

        //
        // Do query
        return txManager.execute(tx -> accessPermissionRepository.query(tx, query));
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
        return txManager.execute(tx -> accessPermissionRepository.count(tx, query));
    }
}
