/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.service.device.registry.common.DeviceValidation;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

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
    private final DeviceFactory deviceFactory;
    private final GroupQueryHelper groupQueryHelper;
    private final EventStorer eventStorer;
    private final DeviceValidation deviceValidation;

    public DeviceRegistryServiceImpl(
            TxManager txManager,
            ServiceConfigurationManager serviceConfigurationManager,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DeviceFactory deviceFactory,
            DeviceValidation deviceValidation,
            DeviceRepository deviceRepository,
            GroupQueryHelper groupQueryHelper,
            EventStorer eventStorer
    ) {
        super(
            txManager,
            serviceConfigurationManager,
            Domains.DEVICE,
            authorizationService,
            permissionFactory
    );

        this.deviceRepository = deviceRepository;
        this.deviceFactory = deviceFactory;
        this.groupQueryHelper = groupQueryHelper;
        this.eventStorer = eventStorer;
        this.deviceValidation = deviceValidation;
    }

    @Override
    public Device create(DeviceCreator deviceCreator) throws KapuaException {
        // Handle legacy DeviceCreator.groupIds usage
        handleLegacyCreatorGroupIdAttribute(deviceCreator);

        // Validate precondition
        deviceValidation.validateCreatePreconditions(deviceCreator);

        // Do create
        return txManager.execute(
            tx -> {
                // Validate in-transaction conditions
                deviceValidation.validateCreateInTransaction(tx, deviceCreator);

                // Create Device
                Device device = deviceFactory.newEntity(deviceCreator.getScopeId());
                device.setGroupId(deviceCreator.getGroupId());
                device.setGroupIds(deviceCreator.getGroupIds());
                device.setTagIds(deviceCreator.getTagIds());
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

                device.setConnectionId(deviceCreator.getConnectionId());
                device.setLastEventId(deviceCreator.getLastEventId());

                // Persist
                return deviceRepository.create(tx, device);
            },
            eventStorer::accept
        );
    }

    @Override
    public Device update(Device device) throws KapuaException {

        // Validate precondition
        deviceValidation.validateUpdatePreconditions(device);

        // Do update
        return txManager.execute(
            tx -> {
                // Handle legacy Device.groupIds usage
                handleLegacyCreatorGroupIdAttribute(tx, device);

                // Validate in-transaction conditions
                deviceValidation.validateUpdateInTransaction(tx, device);

                // Update
                return deviceRepository.update(tx, device);
            },
            eventStorer::accept
        );
    }

    @Override
    public Device find(KapuaId scopeId, KapuaId deviceId) throws KapuaException {

        // Validate precondition
        deviceValidation.validateFindPreconditions(scopeId, deviceId);

        // Do find
        Device device = txManager
                .execute(tx -> deviceRepository.find(tx, scopeId, deviceId))
                .orElse(null);

        // Validate post conditions`
        deviceValidation.validateFindByFieldPostconditions(device);

        // Return result
        return device;
    }

    @Override
    public Device findByClientId(KapuaId scopeId, String clientId) throws KapuaException {

        // Validate precondition
        deviceValidation.validateFindByClientIdPreconditions(scopeId, clientId);

        // Do find
        Device device = txManager
            .execute(tx -> deviceRepository.findByClientId(tx, scopeId, clientId))
            .orElse(null);

        // Validate post conditions
        deviceValidation.validateFindByFieldPostconditions(device);

        // Return result
        return device;
    }

    @Override
    public DeviceListResult query(KapuaQuery query) throws KapuaException {

        // Validate precondition
        deviceValidation.validateQueryPreconditions(query);

        // Do query
        return txManager.execute(tx -> {
            // Apply groups predicates
            groupQueryHelper.handleKapuaQueryGroupPredicate(query, Domains.DEVICE, DeviceAttributes.GROUP_IDS);

            // Query
            return deviceRepository.query(tx, query);
        });
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {

        // Validate precondition
        deviceValidation.validateCountPreconditions(query);

        // Do count
        return txManager.execute(tx -> {
            // Apply groups predicates
            groupQueryHelper.handleKapuaQueryGroupPredicate(query, Domains.DEVICE, DeviceAttributes.GROUP_IDS);

            // Count
            return deviceRepository.count(tx, query);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId deviceId) throws KapuaException {

        // Validate precondition
        deviceValidation.validateDeletePreconditions(scopeId, deviceId);

        // Do delete
        txManager.execute(
            tx -> {
                // Validate in-transaction conditions
                deviceValidation.validateDeleteInTransaction(tx, scopeId, deviceId);

                // Delete
                return deviceRepository.delete(tx, scopeId, deviceId);
            },
            eventStorer::accept
        );
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

    //
    // Private methods
    //

    /**
     * Handles legacy use of {@link DeviceCreator#getGroupId()}.
     *
     * @param deviceCreator The source {@link DeviceCreator}
     * @throws KapuaIllegalArgumentException If both {@link DeviceCreator#getGroupIds()} and {@link DeviceCreator#getGroupIds()} are used.
     * @since 6.1.0
     */
    private static void handleLegacyCreatorGroupIdAttribute(DeviceCreator deviceCreator) throws KapuaIllegalArgumentException {
        ArgumentValidator.notNull(deviceCreator, "deviceCreator");

        if (deviceCreator.getGroupId() != null && !deviceCreator.getGroupIds().isEmpty()) {
            throw new KapuaIllegalArgumentException("deviceCreator.groupId", deviceCreator.getGroupId().toString());
        }

        if (deviceCreator.getGroupId() != null) {
            deviceCreator.setGroupIds(Sets.newHashSet(deviceCreator.getGroupId()));
        }
    }

    /**
     * Handles legacy use of {@link Device#getGroupId()}.
     *
     * @param device The source {@link Device}
     * @since 6.1.0
     */
    private void handleLegacyCreatorGroupIdAttribute(TxContext txContext, Device device) throws KapuaIllegalArgumentException, KapuaEntityNotFoundException {
        ArgumentValidator.notNull(device, "device");

        Optional<Device> optionalCurrentDevice = deviceRepository.find(txContext, device.getScopeId(), device.getId());

        Device currentDevice = optionalCurrentDevice.orElseThrow(() -> new KapuaEntityNotFoundException(Device.TYPE, device.getId()));

        if (
            Objects.equals(device.getGroupId(), currentDevice.getGroupId()) &&
            CollectionUtils.isEqualCollection(device.getGroupIds(), currentDevice.getGroupIds())
        ) {
            // No changes to the values. Nothing to do
            return;
        }

        if (
            Objects.equals(device.getGroupId(), currentDevice.getGroupId()) &&
            !CollectionUtils.isEqualCollection(device.getGroupIds(), currentDevice.getGroupIds())
        ) {
            // Device.groupIds has been changed. User has already migrated therefore we can remove value for Device.groupId
            device.setGroupId(null);
            return;
        }

        if (
            !Objects.equals(device.getGroupId(), currentDevice.getGroupId()) &&
            CollectionUtils.isEqualCollection(device.getGroupIds(), currentDevice.getGroupIds())
        ) {
            // Device.groupId has been changed. User is still using legacy Device.groupId and we need to reflect that change into Device.groupIds
            if (device.getGroupId() == null) {
                // Device.groupId has been removed
                device.setGroupIds(Collections.emptySet());
            }
            else {
                // Device.groupId has been added
                device.setGroupIds(Sets.newHashSet(device.getGroupId()));
            }
            return;
        }

        // If both have been changed, this is a mixed usage, so it is not allowed.
        throw new KapuaIllegalArgumentException("device.groupId", device.getGroupId() != null ? device.getGroupId().toString() : null);
    }

    private void deleteDeviceByGroupId(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        DeviceQuery query = deviceFactory.newQuery(scopeId);
        query.setPredicate(query.attributePredicate(DeviceAttributes.GROUP_ID, groupId));

        txManager.<Void>execute(tx -> {
            DeviceListResult devicesToDelete = deviceRepository.query(tx, query);

            for (Device device : devicesToDelete.getItems()) {
                device.setGroupId(null);
                device.getGroupIds().remove(new KapuaEid(groupId));
                deviceRepository.update(tx, device);
            }
            return null;
        });
    }

    private void deleteDeviceByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        DeviceQuery query = deviceFactory.newQuery(accountId);

        txManager.<Void>execute(tx -> {
            DeviceListResult devicesToDelete = deviceRepository.query(tx, query);

            for (Device d : devicesToDelete.getItems()) {
                deviceRepository.delete(tx, d);
            }
            return null;
        });
    }
}
