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

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaOptimisticLockingException;
import org.eclipse.kapua.commons.service.internal.AbstractKapuaService;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceDomains;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventListResult;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.internal.DeviceEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * {@link DeviceEventService} implementation.
 *
 * @since 1.0.0
 */
@KapuaProvider
public class DeviceEventServiceImpl extends AbstractKapuaService implements DeviceEventService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceEventServiceImpl.class);

    private static final int MAX_RETRY = 3;
    private static final double MAX_WAIT = 200d;

    @Inject
    private AuthorizationService authorizationService;
    @Inject
    private PermissionFactory permissionFactory;
    @Inject
    private DeviceRegistryService deviceRegistryService;

    /**
     * Constructor
     */
    public DeviceEventServiceImpl() {
        super(DeviceEntityManagerFactory.getInstance());

        KapuaLocator locator = KapuaLocator.getInstance();

        authorizationService = locator.getService(AuthorizationService.class);
        permissionFactory = locator.getFactory(PermissionFactory.class);
        deviceRegistryService = locator.getService(DeviceRegistryService.class);
    }

    // Operations

    @Override
    public DeviceEvent create(DeviceEventCreator deviceEventCreator) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceEventCreator, "deviceEventCreator");
        ArgumentValidator.notNull(deviceEventCreator.getScopeId(), "deviceEventCreator.scopeId");
        ArgumentValidator.notNull(deviceEventCreator.getDeviceId(), "deviceEventCreator.deviceId");
        ArgumentValidator.notNull(deviceEventCreator.getReceivedOn(), "deviceEventCreator.receivedOn");
        ArgumentValidator.notEmptyOrNull(deviceEventCreator.getResource(), "deviceEventCreator.eventType");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceDomains.DEVICE_EVENT_DOMAIN, Actions.write, deviceEventCreator.getScopeId()));

        //
        // Check that device exists
        if (deviceRegistryService.find(deviceEventCreator.getScopeId(), deviceEventCreator.getDeviceId()) == null) {
            throw new KapuaEntityNotFoundException(Device.TYPE, deviceEventCreator.getDeviceId());
        }

        // Create the event
        DeviceEvent deviceEvent = entityManagerSession.doTransactedAction(entityManager -> DeviceEventDAO.create(entityManager, deviceEventCreator));

        updateLastEventOnDevice(deviceEvent);

        return deviceEvent;
    }

    @Override
    public DeviceEvent find(KapuaId scopeId, KapuaId entityId)
            throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(entityId, "entityId");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceDomains.DEVICE_EVENT_DOMAIN, Actions.read, scopeId));

        return entityManagerSession.doAction(em -> DeviceEventDAO.find(em, scopeId, entityId));
    }

    @Override
    public DeviceEventListResult query(KapuaQuery query)
            throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceDomains.DEVICE_EVENT_DOMAIN, Actions.read, query.getScopeId()));

        return entityManagerSession.doAction(em -> DeviceEventDAO.query(em, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceDomains.DEVICE_EVENT_DOMAIN, Actions.read, query.getScopeId()));

        return entityManagerSession.doAction(em -> DeviceEventDAO.count(em, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId deviceEventId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceEventId, "deviceEvent.id");
        ArgumentValidator.notNull(scopeId, "deviceEvent.scopeId");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceDomains.DEVICE_EVENT_DOMAIN, Actions.delete, scopeId));

        entityManagerSession.doTransactedAction(em -> {
            if (DeviceEventDAO.find(em, scopeId, deviceEventId) == null) {
                throw new KapuaEntityNotFoundException(DeviceEvent.TYPE, deviceEventId);
            }

            return DeviceEventDAO.delete(em, scopeId, deviceEventId);
        });
    }


    /**
     * Updates the {@link Device#getLastEventId()} with the given {@link DeviceEvent}.
     *
     * @param deviceEvent The {@link DeviceEvent} that needs to be set.
     * @throws KapuaException If {@link Device} does not exist or updating the entity causes an error that is not {@link KapuaOptimisticLockingException} which is ignored.
     * @since 1.2.0
     */
    private void updateLastEventOnDevice(DeviceEvent deviceEvent) throws KapuaException {
        int retry = 0;
        do {
            retry++;
            try {
                Device device = deviceRegistryService.find(deviceEvent.getScopeId(), deviceEvent.getDeviceId());

                if (device == null) {
                    throw new KapuaEntityNotFoundException(Device.TYPE, deviceEvent.getDeviceId());
                }

                if (device.getLastEvent() == null ||
                        device.getLastEvent().getReceivedOn().before(deviceEvent.getReceivedOn())) {
                    device.setLastEventId(deviceEvent.getId());
                    deviceRegistryService.update(device);
                }
                break;
            } catch (KapuaOptimisticLockingException e) {
                LOG.warn("Concurrent update for device: {} - Event id: {} Attempt: {}/{}. {}", deviceEvent.getDeviceId(), deviceEvent.getId(), retry, MAX_RETRY, retry < MAX_RETRY ? "Retrying..." : "Skipping update!");

                if (retry < MAX_RETRY) {
                    try {
                        Thread.sleep((long) (Math.random() * MAX_WAIT));
                    } catch (InterruptedException e1) {
                        LOG.warn("Error while waiting retry: {}", e.getMessage());
                    }
                }
            }
        }
        while (retry < MAX_RETRY);
    }

}
