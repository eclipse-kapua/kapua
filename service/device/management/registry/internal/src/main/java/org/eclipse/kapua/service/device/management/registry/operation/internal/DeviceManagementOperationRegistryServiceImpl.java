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
package org.eclipse.kapua.service.device.management.registry.operation.internal;

import java.util.Date;

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
import org.eclipse.kapua.service.device.management.message.notification.NotifyStatus;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationCreator;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationListResult;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRegistryService;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRepository;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.storage.TxManager;

@Singleton
public class DeviceManagementOperationRegistryServiceImpl
        implements DeviceManagementOperationRegistryService {

    private final AuthorizationService authorizationService;
    private final DeviceRepository deviceRepository;
    private final TxManager txManager;
    private final DeviceManagementOperationRepository repository;
    private final DeviceManagementOperationFactory entityFactory;

    @Inject
    public DeviceManagementOperationRegistryServiceImpl(
            AuthorizationService authorizationService,
            DeviceRepository deviceRepository,
            TxManager txManager,
            DeviceManagementOperationRepository repository,
            DeviceManagementOperationFactory entityFactory) {
        this.authorizationService = authorizationService;
        this.deviceRepository = deviceRepository;
        this.txManager = txManager;
        this.repository = repository;
        this.entityFactory = entityFactory;
    }

    @Override
    public DeviceManagementOperation create(DeviceManagementOperationCreator creator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(creator, "creator");
        ArgumentValidator.notNull(creator.getScopeId(), "creator.scopeId");
        ArgumentValidator.notNull(creator.getStartedOn(), "creator.startedOn");
        ArgumentValidator.notNull(creator.getDeviceId(), "creator.deviceId");
        ArgumentValidator.notNull(creator.getOperationId(), "creator.operationId");
        ArgumentValidator.notNull(creator.getStatus(), "creator.status");
        ArgumentValidator.notNull(creator.getAppId(), "creator.appId");
        ArgumentValidator.notNull(creator.getAction(), "creator.action");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.write, null));

        return txManager.execute(tx -> {
            // Check device existence
            if (!deviceRepository.find(tx, creator.getScopeId(), creator.getDeviceId()).isPresent()) {
                throw new KapuaEntityNotFoundException(Device.TYPE, creator.getDeviceId());
            }
            // Create DeviceManagementOperationNotification
            DeviceManagementOperation newEntity = entityFactory.newEntity(creator.getScopeId());
            newEntity.setStartedOn(creator.getStartedOn());
            newEntity.setDeviceId(creator.getDeviceId());
            newEntity.setOperationId(creator.getOperationId());
            newEntity.setAppId(creator.getAppId());
            newEntity.setAction(creator.getAction());
            newEntity.setResource(creator.getResource());
            newEntity.setStatus(creator.getStatus());
            newEntity.setStatus(creator.getStatus());
            newEntity.setInputProperties(creator.getInputProperties());
            // Do create
            return repository.create(tx, newEntity);
        });
    }

    @Override
    public DeviceManagementOperation update(DeviceManagementOperation entity) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(entity, "deviceManagementOperation");
        ArgumentValidator.notNull(entity.getScopeId(), "deviceManagementOperation.scopeId");
        ArgumentValidator.notNull(entity.getId(), "deviceManagementOperation.id");
        ArgumentValidator.notNull(entity.getStartedOn(), "deviceManagementOperation.startedOn");
        ArgumentValidator.notNull(entity.getDeviceId(), "deviceManagementOperation.deviceId");
        ArgumentValidator.notNull(entity.getOperationId(), "deviceManagementOperation.operationId");
        ArgumentValidator.notNull(entity.getStatus(), "deviceManagementOperation.status");
        ArgumentValidator.notNull(entity.getAppId(), "deviceManagementOperation.appId");
        ArgumentValidator.notNull(entity.getAction(), "deviceManagementOperation.action");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.write, null));

        return txManager.execute(tx -> {
            // Check device existence
            if (!deviceRepository.find(tx, entity.getScopeId(), entity.getDeviceId()).isPresent()) {
                throw new KapuaEntityNotFoundException(Device.TYPE, entity.getDeviceId());
            }
            // Check existence
            if (!repository.find(tx, entity.getScopeId(), entity.getId()).isPresent()) {
                throw new KapuaEntityNotFoundException(DeviceManagementOperation.TYPE, entity.getId());
            }
            // Do update
            return repository.update(tx, entity);
        });
    }

    @Override
    public DeviceManagementOperation find(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(entityId, "deviceManagementOperationId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> repository.find(tx, scopeId, entityId))
                .orElse(null);

    }

    @Override
    public DeviceManagementOperation findByOperationId(KapuaId scopeId, KapuaId operationId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(operationId, "operationId");

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> repository.findByOperationId(tx, scopeId, operationId))
                .orElse(null);
    }

    @Override
    public DeviceManagementOperationListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> repository.query(tx, query));
    }

    @Override
    public void updateStatus(KapuaId scopeId, KapuaId operationId, NotifyStatus notifyStatus, Date endedOnDate) throws KapuaException {
        txManager.execute(tx -> {
            final DeviceManagementOperation currentRecord = repository.findByOperationId(tx, scopeId, operationId)
                    .orElseThrow(() -> new KapuaEntityNotFoundException(DeviceManagementOperation.TYPE, operationId));
            currentRecord.setStatus(notifyStatus);
            currentRecord.setEndedOn(endedOnDate);
            return currentRecord;
        });
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
        ArgumentValidator.notNull(entityId, "deviceManagementOperationId");

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT_REGISTRY, Actions.delete, scopeId));

        // Do delete
        txManager.execute(tx -> repository.delete(tx, scopeId, entityId));
    }
}
