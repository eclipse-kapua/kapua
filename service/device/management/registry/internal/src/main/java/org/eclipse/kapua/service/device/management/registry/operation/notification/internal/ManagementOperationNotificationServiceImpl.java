/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.registry.operation.notification.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRepository;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationCreator;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationListResult;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationRepository;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationService;
import org.eclipse.kapua.storage.TxManager;

@Singleton
public class ManagementOperationNotificationServiceImpl implements ManagementOperationNotificationService {

    private final AuthorizationService authorizationService;
    private final ManagementOperationNotificationFactory entityFactory;
    private final TxManager txManager;
    private final ManagementOperationNotificationRepository repository;
    private final DeviceManagementOperationRepository deviceManagementOperationRepository;

    @Inject
    public ManagementOperationNotificationServiceImpl(
            AuthorizationService authorizationService,
            ManagementOperationNotificationFactory entityFactory,
            TxManager txManager,
            ManagementOperationNotificationRepository repository,
            DeviceManagementOperationRepository deviceManagementOperationRepository) {
        this.authorizationService = authorizationService;
        this.entityFactory = entityFactory;
        this.txManager = txManager;
        this.repository = repository;
        this.deviceManagementOperationRepository = deviceManagementOperationRepository;
    }

    @Override
    public ManagementOperationNotification create(ManagementOperationNotificationCreator creator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(creator, "managementOperationNotificationCreator");
        ArgumentValidator.notNull(creator.getScopeId(), "managementOperationNotificationCreator.scopeId");
        ArgumentValidator.notNull(creator.getOperationId(), "managementOperationNotificationCreator.operationId");
        ArgumentValidator.notNull(creator.getSentOn(), "managementOperationNotificationCreator.sentOn");
        ArgumentValidator.notNull(creator.getStatus(), "managementOperationNotificationCreator.status");
        ArgumentValidator.notNull(creator.getProgress(), "managementOperationNotificationCreator.progress");
        ArgumentValidator.notNegative(creator.getProgress(), "managementOperationNotificationCreator.progress");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.write, null));

        return txManager.execute(tx -> {
            // Check operation existence
            if (!deviceManagementOperationRepository.find(tx, creator.getScopeId(), creator.getOperationId()).isPresent()) {
                throw new KapuaEntityNotFoundException(DeviceManagementOperation.TYPE, creator.getOperationId());
            }
            // Create DeviceManagementOperationNotification
            final ManagementOperationNotification newEntity = entityFactory.newEntity(creator.getScopeId());
            newEntity.setOperationId(creator.getOperationId());
            newEntity.setSentOn(creator.getSentOn());
            newEntity.setStatus(creator.getStatus());
            newEntity.setResource(creator.getResource());
            newEntity.setProgress(creator.getProgress());
            newEntity.setMessage(creator.getMessage());
            // Do create
            return repository.create(tx, newEntity);
        });
    }

    @Override
    public ManagementOperationNotification find(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(entityId, "managementOperationNotificationId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> repository.find(tx, scopeId, entityId))
                .orElse(null);
    }

    @Override
    public ManagementOperationNotificationListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> repository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> repository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(entityId, "managementOperationNotificationId");

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.delete, null));

        // Do delete
        txManager.execute(tx -> repository.delete(tx, scopeId, entityId));
    }
}
