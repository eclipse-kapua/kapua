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
package org.eclipse.kapua.service.authentication.token.shiro;

import java.util.Optional;

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenAttributes;
import org.eclipse.kapua.service.authentication.token.AccessTokenListResult;
import org.eclipse.kapua.service.authentication.token.AccessTokenRepository;
import org.eclipse.kapua.storage.TxContext;

public class AccessTokenImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<AccessToken, AccessTokenImpl, AccessTokenListResult>
        implements AccessTokenRepository {

    public AccessTokenImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(AccessTokenImpl.class, AccessToken.TYPE, () -> new AccessTokenListResult(), jpaRepoConfig);
    }

    @Override
    public Optional<AccessToken> findByTokenId(TxContext tx, String tokenId) {
        return doFindByField(tx, KapuaId.ANY, AccessTokenAttributes.TOKEN_IDENTIFIER, tokenId);
    }
}
