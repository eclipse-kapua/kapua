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
package org.eclipse.kapua.service.user.internal;

import java.util.Optional;

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaNamedEntityJpaRepository;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserAttributes;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserRepository;
import org.eclipse.kapua.storage.TxContext;

public class UserImplJpaRepository
        extends KapuaNamedEntityJpaRepository<User, UserImpl, UserListResult>
        implements UserRepository {

    public UserImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(UserImpl.class, User.TYPE, () -> new UserListResult(), jpaRepoConfig);
    }

    @Override
    public Optional<User> findByExternalId(TxContext txContext, String externalId) {
        return doFindByField(txContext, KapuaId.ANY, UserAttributes.EXTERNAL_ID, externalId);
    }

    @Override
    public Optional<User> findByExternalUsername(TxContext txContext, String externalUsername) {
        return doFindByField(txContext, KapuaId.ANY, UserAttributes.EXTERNAL_USERNAME, externalUsername);
    }

}
