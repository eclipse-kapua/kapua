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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.storage.KapuaEntityRepository;
import org.eclipse.kapua.storage.TxContext;

public class UsedEntitiesCounterImpl<
        E extends KapuaEntity,
        C extends KapuaEntityCreator<E>,
        L extends KapuaListResult<E>,
        Q extends KapuaQuery,
        F extends KapuaEntityFactory<E, C>
        > implements UsedEntitiesCounter {

    private final F factory;
    private final KapuaEntityRepository<E, L> entityRepository;

    public UsedEntitiesCounterImpl(F factory,
            KapuaEntityRepository<E, L> entityRepository) {
        this.factory = factory;
        this.entityRepository = entityRepository;
    }

    @Override
    public long countEntitiesInScope(TxContext tx, KapuaId scopeId) throws KapuaException {
        final KapuaQuery query = new KapuaQuery(scopeId);
        // Do count
        return entityRepository.count(tx, query);
    }
}