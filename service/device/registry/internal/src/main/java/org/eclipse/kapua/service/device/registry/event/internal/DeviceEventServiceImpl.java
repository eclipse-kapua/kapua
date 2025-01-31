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
package org.eclipse.kapua.service.device.registry.event.internal;

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
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventListResult;
import org.eclipse.kapua.service.device.registry.event.DeviceEventRepository;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DeviceEventService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceEventServiceImpl
        implements DeviceEventService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceEventServiceImpl.class);

    private static final int MAX_RETRY = 3;
    private static final double MAX_WAIT = 200d;

    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final DeviceRepository deviceRepository;
    private final DeviceEventFactory entityFactory;
    private final DeviceEventRepository repository;

    @Inject
    public DeviceEventServiceImpl(
            AuthorizationService authorizationService,
            TxManager txManager,
            DeviceRepository deviceRepository,
            DeviceEventFactory entityFactory,
            DeviceEventRepository deviceEventRepository) {
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.deviceRepository = deviceRepository;
        this.entityFactory = entityFactory;
        this.repository = deviceEventRepository;
    }

    // Operations

    @Override
    public DeviceEvent create(DeviceEventCreator deviceEventCreator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(deviceEventCreator, "deviceEventCreator");
        ArgumentValidator.notNull(deviceEventCreator.getScopeId(), "deviceEventCreator.scopeId");
        ArgumentValidator.notNull(deviceEventCreator.getDeviceId(), "deviceEventCreator.deviceId");
        ArgumentValidator.notNull(deviceEventCreator.getReceivedOn(), "deviceEventCreator.receivedOn");
        ArgumentValidator.notEmptyOrNull(deviceEventCreator.getResource(), "deviceEventCreator.eventType");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_EVENT, Actions.write, deviceEventCreator.getScopeId()));
        return txManager.execute(tx -> {
            // Check that device exists
            final Device device = deviceRepository.findForUpdate(tx, deviceEventCreator.getScopeId(), deviceEventCreator.getDeviceId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Device.TYPE, deviceEventCreator.getDeviceId()));

            // Create the event
            DeviceEvent newEvent = entityFactory.newEntity(deviceEventCreator.getScopeId());
            newEvent.setDeviceId(device.getId());
            newEvent.setReceivedOn(deviceEventCreator.getReceivedOn());
            newEvent.setSentOn(deviceEventCreator.getSentOn());
            newEvent.setResource(deviceEventCreator.getResource());
            newEvent.setAction(deviceEventCreator.getAction());
            newEvent.setResponseCode(deviceEventCreator.getResponseCode());
            newEvent.setEventMessage(deviceEventCreator.getEventMessage());
            newEvent.setPosition(deviceEventCreator.getPosition());

            final DeviceEvent created = repository.create(tx, newEvent);
            device.setLastEventId(created.getId());
            //Do not call update explicitly, the transaction ending will automatically update the entity
            //            deviceRepository.update(tx, device, device);
            return newEvent;
        });

    }

    @Override
    public DeviceEvent find(KapuaId scopeId, KapuaId entityId)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(entityId, "entityId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_EVENT, Actions.read, scopeId));

        return txManager.execute(tx -> repository.find(tx, scopeId, entityId))
                .orElse(null);
    }

    @Override
    public DeviceEventListResult query(KapuaQuery query)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_EVENT, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> repository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_EVENT, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> repository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId deviceEventId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(deviceEventId, "deviceEvent.id");
        ArgumentValidator.notNull(scopeId, "deviceEvent.scopeId");

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_EVENT, Actions.delete, scopeId));

        txManager.execute(tx -> repository.delete(tx, scopeId, deviceEventId));
    }
}
