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
package org.eclipse.kapua.service.account;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaNamedEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

/**
 * {@link Account} {@link KapuaEntityService}.
 *
 * @since 1.0.0
 */
public interface AccountService extends KapuaEntityService<Account, AccountCreator>,
        KapuaUpdatableEntityService<Account>,
        KapuaNamedEntityService<Account>,
        KapuaConfigurableService {

    /**
     * Updates the given {@link KapuaUpdatableEntity}.
     *
     * @return The updated {@link KapuaUpdatableEntity}.
     * @throws KapuaException
     * @since 1.0.0
     */
    Account updateCurrentAccount(@NotNull CurrentAccountUpdateRequest request) throws KapuaException;

    /**
     * Updates the given {@link KapuaUpdatableEntity}.
     *
     * @return The updated {@link KapuaUpdatableEntity}.
     * @throws KapuaException
     * @since 1.0.0
     */
    Account updateChildAccount(KapuaId accountId, @NotNull AccountUpdateRequest request) throws KapuaException;

    /**
     * Finds the {@link Account} by the {@link Account#getId()}.
     *
     * @param id
     *         The {@link Account#getId()}.
     * @return The {@link Account} found or {@code null}.
     * @throws KapuaException
     */
    Account find(@NotNull KapuaId id) throws KapuaException;

    @Override
    AccountListResult query(@NotNull KapuaQuery query) throws KapuaException;

    /**
     * Returns an {@link AccountListResult} of direct children {@link Account}s of the given {@link Account#getId()}.
     *
     * @param scopeId
     *         The {@link Account#getId()}.
     * @return The {@link AccountListResult} of direct children {@link Account}s.
     * @throws KapuaException
     */
    AccountListResult findChildrenRecursively(@NotNull KapuaId scopeId) throws KapuaException;

    AccountStatus deriveAccountStatus(KapuaId accountId) throws KapuaException;

    AccountStatus deriveAccountStatus(Account account) throws KapuaException;
}
