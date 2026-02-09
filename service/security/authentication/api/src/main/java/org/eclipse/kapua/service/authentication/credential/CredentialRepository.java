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
package org.eclipse.kapua.service.authentication.credential;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.storage.KapuaUpdatableEntityRepository;
import org.eclipse.kapua.storage.TxContext;

/**
 * {@link Credential} {@link KapuaUpdatableEntityRepository} definition.
 *
 * @since 2.0.0
 */
public interface CredentialRepository
        extends KapuaUpdatableEntityRepository<Credential, CredentialListResult> {


    /**
     * Gets the {@link Credential}s filtered by the User#getId.
     *
     * @param txContext The {@link TxContext}
     * @param scopeId The {@link Credential#getScopeId()}
     * @param userId The User#getId
     * @return The {@link CredentialListResult}
     * @throws KapuaException
     * @since 2.0.0
     */
    CredentialListResult findByUserId(TxContext txContext, KapuaId scopeId, KapuaId userId) throws KapuaException;
}
