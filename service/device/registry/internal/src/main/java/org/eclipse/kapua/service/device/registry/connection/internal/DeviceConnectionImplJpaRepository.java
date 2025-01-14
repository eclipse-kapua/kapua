/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.registry.connection.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionAttributes;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionListResult;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionRepository;
import org.eclipse.kapua.storage.TxContext;

public class DeviceConnectionImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<DeviceConnection, DeviceConnectionImpl, DeviceConnectionListResult>
        implements DeviceConnectionRepository {

    public DeviceConnectionImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(DeviceConnectionImpl.class, DeviceConnection.TYPE, () -> new DeviceConnectionListResult(), jpaRepoConfig);
    }

    @Override
    public long countByClientId(TxContext tx, KapuaId scopeId, String clientId) throws KapuaException {
        final KapuaQuery query = new DeviceConnectionQuery(scopeId);
        query.setPredicate(query.attributePredicate(DeviceConnectionAttributes.CLIENT_ID, clientId));
        return this.count(tx, query);
    }
}
