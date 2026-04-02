/*******************************************************************************
 * Copyright (c) 2026, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.storage.TxContext;

import java.util.Optional;

public class DeviceGroupToGroupsMigratorJpaRepository
        extends KapuaUpdatableEntityJpaRepository<Device, DeviceMigratorImpl, DeviceListResult>
        implements DeviceRepository {

    public DeviceGroupToGroupsMigratorJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(DeviceMigratorImpl.class, Device.TYPE, DeviceListResultMigratorImpl::new, jpaRepoConfig);
    }

    @Override
    public Optional<Device> find(TxContext tx, KapuaId scopeId, KapuaId entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Device create(TxContext tx, Device entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Device delete(TxContext tx, KapuaId scopeId, KapuaId entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Device> findByClientId(TxContext tx, KapuaId scopeId, String clientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Device> findForUpdate(TxContext tx, KapuaId scopeId, KapuaId deviceId) {
        throw new UnsupportedOperationException();
    }
}
