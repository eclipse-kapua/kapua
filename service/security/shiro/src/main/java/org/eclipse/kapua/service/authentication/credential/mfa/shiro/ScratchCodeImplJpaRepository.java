/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaEntityJpaRepository;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCode;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeListResult;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeRepository;
import org.eclipse.kapua.storage.TxContext;

public class ScratchCodeImplJpaRepository
        extends KapuaEntityJpaRepository<ScratchCode, ScratchCodeImpl, ScratchCodeListResult>
        implements ScratchCodeRepository {

    public ScratchCodeImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(ScratchCodeImpl.class, ScratchCode.TYPE, () -> new ScratchCodeListResult(), jpaRepoConfig);
    }

    @Override
    public ScratchCodeListResult findByMfaOptionId(TxContext tx, KapuaId scopeId, KapuaId mfaOptionId) throws KapuaException {
        final ScratchCodeListResult res = listSupplier.get();
        res.addItems(doFindAllByField(tx, scopeId, ScratchCodeImpl_.MFA_OPTION_ID, mfaOptionId));
        return res;
    }
}
