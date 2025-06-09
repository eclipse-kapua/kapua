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
package org.eclipse.kapua.service.device.registry.internal;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.service.device.registry.common.DeviceValidation;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DeviceRegistryService} implementation.
 *
 * @since 1.0.0
 */
public class DeviceRegistryServiceImpl
        extends KapuaConfigurableServiceBase
        implements DeviceRegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistryServiceImpl.class);
    private final DeviceRepository deviceRepository;
    private final DeviceFactory entityFactory;
    private final GroupQueryHelper groupQueryHelper;
    private final EventStorer eventStorer;
    private final DeviceValidation deviceValidation;

    public DeviceRegistryServiceImpl(
            ServiceConfigurationManager serviceConfigurationManager,
            AuthorizationService authorizationService,
            TxManager txManager,
            DeviceRepository deviceRepository,
            DeviceFactory entityFactory,
            GroupQueryHelper groupQueryHelper,
            EventStorer eventStorer, DeviceValidation deviceValidation) {
        super(txManager, serviceConfigurationManager, Domains.DEVICE, authorizationService);
        this.deviceRepository = deviceRepository;
        this.entityFactory = entityFactory;
        this.groupQueryHelper = groupQueryHelper;
        this.eventStorer = eventStorer;
        this.deviceValidation = deviceValidation;
    }

    @Override
    public Device create(DeviceCreator deviceCreator)
            throws KapuaException {
        deviceValidation.validateCreatePreconditions(deviceCreator);

        return txManager.execute(tx -> {
                    // Check entity limit
                    serviceConfigurationManager.checkAllowedEntities(tx, deviceCreator.getScopeId(), "Devices");
                    // Check duplicate clientId
                    DeviceQuery query = new DeviceQuery(deviceCreator.getScopeId());
                    query.setPredicate(query.attributePredicate(DeviceAttributes.CLIENT_ID, deviceCreator.getClientId()));
                    //TODO: check whether this is anywhere efficient
                    if (deviceRepository.count(tx, query) > 0) {
                        throw new KapuaDuplicateNameException(deviceCreator.getClientId());
                    }

                    final Device device = entityFactory.newEntity(deviceCreator.getScopeId());
                    device.setGroupId(deviceCreator.getGroupId());
                    device.setClientId(deviceCreator.getClientId());
                    device.setStatus(deviceCreator.getStatus());
                    device.setDisplayName(deviceCreator.getDisplayName());
                    device.setSerialNumber(deviceCreator.getSerialNumber());
                    device.setModelId(deviceCreator.getModelId());
                    device.setModelName(deviceCreator.getModelName());
                    device.setImei(deviceCreator.getImei());
                    device.setImsi(deviceCreator.getImsi());
                    device.setIccid(deviceCreator.getIccid());
                    device.setBiosVersion(deviceCreator.getBiosVersion());
                    device.setFirmwareVersion(deviceCreator.getFirmwareVersion());
                    device.setOsVersion(deviceCreator.getOsVersion());
                    device.setJvmVersion(deviceCreator.getJvmVersion());
                    device.setOsgiFrameworkVersion(deviceCreator.getOsgiFrameworkVersion());
                    device.setApplicationFrameworkVersion(deviceCreator.getApplicationFrameworkVersion());
                    device.setConnectionInterface(deviceCreator.getConnectionInterface());
                    device.setConnectionIp(deviceCreator.getConnectionIp());
                    device.setApplicationIdentifiers(deviceCreator.getApplicationIdentifiers());
                    device.setAcceptEncoding(deviceCreator.getAcceptEncoding());
                    device.setCustomAttribute1(deviceCreator.getCustomAttribute1());
                    device.setCustomAttribute2(deviceCreator.getCustomAttribute2());
                    device.setCustomAttribute3(deviceCreator.getCustomAttribute3());
                    device.setCustomAttribute4(deviceCreator.getCustomAttribute4());
                    device.setCustomAttribute5(deviceCreator.getCustomAttribute5());
                    device.setExtendedProperties(deviceCreator.getExtendedProperties());
                    device.setTagIds(deviceCreator.getTagIds());

                    device.setConnectionId(deviceCreator.getConnectionId());
                    device.setLastEventId(deviceCreator.getLastEventId());
                    // Do create
                    return deviceRepository.create(tx, device);
                },
                eventStorer::accept);
    }

    @Override
    public Device update(Device device)
            throws KapuaException {
        // Do update
        return txManager.execute(tx -> {
                    deviceValidation.validateUpdatePreconditions(tx, device);
                    return deviceRepository.update(tx, device);
                },
                eventStorer::accept);
    }

    @Override
    public Device find(KapuaId scopeId, KapuaId entityId)
            throws KapuaException {
        // Do find
        return txManager.execute(tx -> {
                    deviceValidation.validateFindPreconditions(tx, scopeId, entityId);
                    return deviceRepository.find(tx, scopeId, entityId);
                })
                .orElse(null);
    }

    @Override
    public Device findByClientId(KapuaId scopeId, String clientId) throws KapuaException {
        deviceValidation.validateFindByClientIdPreconditions(scopeId, clientId);
        // Check cache and/or do find
        return txManager.execute(tx -> deviceRepository.findByClientId(tx, scopeId, clientId))
                .orElse(null);
    }

    @Override
    public DeviceListResult query(KapuaQuery query)
            throws KapuaException {
        deviceValidation.validateQueryPreconditions(query);

        // Do query
        return txManager.execute(tx -> {
            groupQueryHelper.handleKapuaQueryGroupPredicate(query, Domains.DEVICE, DeviceAttributes.GROUP_ID);
            return deviceRepository.query(tx, query);
        });
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        deviceValidation.validateCountPreconditions(query);

        // Do count
        return txManager.execute(tx -> {
            groupQueryHelper.handleKapuaQueryGroupPredicate(query, Domains.DEVICE, DeviceAttributes.GROUP_ID);
            return deviceRepository.count(tx, query);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId deviceId) throws KapuaException {

        // Do delete
        txManager.execute(
                tx -> {
                    deviceValidation.validateDeletePreconditions(tx, scopeId, deviceId);
                    return deviceRepository.delete(tx, scopeId, deviceId);
                },
                eventStorer::accept);
    }

    //@ListenServiceEvent(fromAddress="account")
    //@ListenServiceEvent(fromAddress="authorization")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }
        LOGGER.info("DeviceRegistryService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("group".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteDeviceByGroupId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        } else if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteDeviceByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }
    // Private methods

    private void deleteDeviceByGroupId(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        DeviceQuery query = new DeviceQuery(scopeId);
        query.setPredicate(query.attributePredicate(DeviceAttributes.GROUP_ID, groupId));

        txManager.<Void>execute(tx -> {
            DeviceListResult devicesToDelete = deviceRepository.query(tx, query);

            for (Device d : devicesToDelete.getItems()) {
                d.setGroupId(null);
                deviceRepository.update(tx, d);
            }
            return null;
        });
    }

    private void deleteDeviceByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        DeviceQuery query = new DeviceQuery(accountId);

        txManager.<Void>execute(tx -> {
            DeviceListResult devicesToDelete = deviceRepository.query(tx, query);

            for (Device d : devicesToDelete.getItems()) {
                deviceRepository.delete(tx, d);
            }
            return null;
        });
    }
}
