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
package org.eclipse.kapua.service.authentication.credential.mfa.shiro;

import java.util.Optional;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOption;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionListResult;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionRepository;
import org.eclipse.kapua.storage.TxContext;

public class MfaOptionImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<MfaOption, MfaOptionImpl, MfaOptionListResult>
        implements MfaOptionRepository {

    public MfaOptionImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(MfaOptionImpl.class, MfaOption.TYPE, MfaOptionListResult::new, jpaRepoConfig);
    }

    @Override
    public Optional<MfaOption> findByUserId(TxContext tx, KapuaId scopeId, KapuaId userId) throws KapuaException {
        final KapuaEid userEid = KapuaEid.parseKapuaId(userId);
        return doFindByField(tx, scopeId, MfaOptionImpl_.USER_ID, userEid);
    }
}
