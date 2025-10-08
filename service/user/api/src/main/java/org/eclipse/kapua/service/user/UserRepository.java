/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.storage.KapuaNamedEntityRepository;
import org.eclipse.kapua.storage.TxContext;

import java.util.Optional;

public interface UserRepository extends
        KapuaNamedEntityRepository<User, UserListResult> {

    Optional<User> findById(TxContext txContext, KapuaId userId);

    Optional<User> findByExternalId(TxContext txContext, final String externalId);

    Optional<User> findByExternalUsername(TxContext txContext, final String externalUsername);

}
