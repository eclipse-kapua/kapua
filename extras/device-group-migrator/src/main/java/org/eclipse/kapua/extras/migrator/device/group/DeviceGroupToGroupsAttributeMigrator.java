/*******************************************************************************
 * Copyright (c) 2026, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.extras.migrator.device.group;

import com.google.common.collect.Sets;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceGroupToGroupsAttributeMigrator {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceGroupToGroupsAttributeMigrator.class);

    private DeviceRegistryService deviceRegistryService;

    protected DeviceGroupToGroupsAttributeMigrator(String persistenceUnitName , KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        this.deviceRegistryService = new DeviceGroupToGroupsMigratorServiceImpl(
            jpaTxManagerFactory.create(persistenceUnitName),
            new DeviceGroupToGroupsMigratorJpaRepository(new KapuaJpaRepositoryConfiguration())
        );
    }

    public void migrate() throws KapuaException {

        DeviceQuery deviceQuery = new DeviceQueryMigratorImpl();
        deviceQuery.setScopeId(KapuaId.ANY);

        deviceQuery.setPredicate(
            deviceQuery.attributePredicate(DeviceAttributes.GROUP_ID, new Object(), AttributePredicate.Operator.NOT_NULL)
        );

        DeviceListResult devices = deviceRegistryService.query(deviceQuery);

        LOG.info("Found {} Devices that have a Group assigned", devices.getSize());

        try {
            LOG.info("Migrating Device.groupId to Device.groupIds...");

            for (Device device : devices.getItems()) {
                LOG.info("    Migrating Device {}/{}...", device.getId(), device.getClientId());

                device.setGroupIds(Sets.newHashSet(device.getGroupId()));

                deviceRegistryService.update(device);
            }

            LOG.info("Migrating Device.groupId to Device.groupIds... DONE!");
        }
        catch (Exception e) {
            LOG.info("Migrating Device.groupId to Device.groupIds... ERROR!", e);
            throw e;
        }
    }
}
