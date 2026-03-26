/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.storage.TxManager;

import javax.inject.Singleton;
import java.util.Map;

/**
 * {@link DeviceRegistryService} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class DeviceGroupToGroupsMigratorServiceImpl implements DeviceRegistryService {
    private final TxManager txManager;
    private final DeviceRepository deviceRepository;

    public DeviceGroupToGroupsMigratorServiceImpl(TxManager txManager, DeviceRepository deviceRepository) {
        this.txManager = txManager;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Device update(Device device) throws KapuaException {
        return txManager.execute(tx -> deviceRepository.update(tx, device));
    }

    @Override
    public DeviceListResult query(KapuaQuery query) throws KapuaException {
        return txManager.execute(tx -> deviceRepository.query(tx, query));
    }

    @Override
    public Device findByClientId(KapuaId scopeId, String clientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        return txManager.execute(tx -> deviceRepository.count(tx, query));
    }

    // Unsupported methods
    @Override
    public Device create(DeviceCreator deviceCreator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Device find(KapuaId scopeId, KapuaId deviceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId deviceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KapuaTocd getConfigMetadata(KapuaId scopeId) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setConfigValues(KapuaId scopeId, KapuaId parentId, Map<String, Object> values) throws KapuaException {
        throw new UnsupportedOperationException();
    }
}
