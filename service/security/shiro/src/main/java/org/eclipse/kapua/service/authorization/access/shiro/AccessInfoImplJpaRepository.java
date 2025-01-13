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
package org.eclipse.kapua.service.authorization.access.shiro;

import java.util.Optional;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoAttributes;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.authorization.access.AccessInfoRepository;
import org.eclipse.kapua.storage.TxContext;

public class AccessInfoImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<AccessInfo, AccessInfoImpl, AccessInfoListResult>
        implements AccessInfoRepository {

    public AccessInfoImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(AccessInfoImpl.class, AccessInfo.TYPE, () -> new AccessInfoListResult(), jpaRepoConfig);
    }

    @Override
    public Optional<AccessInfo> findByUserId(TxContext txContext, KapuaId scopeId, KapuaId userId) throws KapuaException {
        return doFindByField(txContext, scopeId, AccessInfoAttributes.USER_ID, userId);
    }
}
