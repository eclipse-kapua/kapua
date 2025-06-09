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
package org.eclipse.kapua.service.authorization.domain.shiro;

import java.util.Optional;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaEntityJpaRepository;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntity_;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainListResult;
import org.eclipse.kapua.service.authorization.domain.DomainRepository;
import org.eclipse.kapua.storage.TxContext;

public class DomainImplJpaRepository
        extends KapuaEntityJpaRepository<Domain, DomainImpl, DomainListResult>
        implements DomainRepository {

    public DomainImplJpaRepository(KapuaJpaRepositoryConfiguration configuration) {
        super(DomainImpl.class, Domain.TYPE, () -> new DomainListResult(), configuration);
    }

    @Override
    public Optional<Domain> findByName(TxContext txContext, KapuaId scopeId, String name) throws KapuaException {
        return doFindByField(txContext, scopeId, AbstractKapuaNamedEntity_.NAME, name);
    }

}
