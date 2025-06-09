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

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.JpaAwareTxContext;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.storage.TxContext;

import com.google.common.collect.Lists;

public class DeviceImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<Device, DeviceImpl, DeviceListResult>
        implements DeviceRepository {

    public DeviceImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(DeviceImpl.class, Device.TYPE, () -> new DeviceListResult(), jpaRepoConfig);
    }

    @Override
    public Optional<Device> findByClientId(TxContext tx, KapuaId scopeId, String clientId) throws KapuaException {
        DeviceQuery query = new DeviceQuery(scopeId);
        query.setPredicate(query.attributePredicate(DeviceAttributes.CLIENT_ID, clientId));
        query.setFetchAttributes(Lists.newArrayList(DeviceAttributes.CONNECTION, DeviceAttributes.LAST_EVENT));
        return Optional.ofNullable(this.query(tx, query).getFirstItem());
    }

    @Override
    public Optional<Device> findForUpdate(TxContext tx, KapuaId scopeId, KapuaId deviceId) {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(tx);
        final Optional<Device> device = doFind(em, scopeId, deviceId, LockModeType.PESSIMISTIC_WRITE);
        return device;
    }
}
