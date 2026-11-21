/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.translator;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;

import java.util.Objects;

/**
 * {@link Translator} that needs to resolve {@link Account} ids from {@link Account} names (see {@link #findAccountIdByName(String)}).
 * <p>
 * {@link Account}s are cached in a {@link LocalCache} which can be shared among {@link Translator}s.
 *
 * @param <FROM_M> {@link Message} type from which {@link #translate(Message)}.
 * @param <TO_M>   {@link Message} type to which {@link #translate(Message)}.
 * @since 2.1.0
 */
public abstract class AbstractAccountResolvingTranslator<FROM_M extends Message, TO_M extends Message> extends Translator<FROM_M, TO_M> {

    private final AccountService accountService;
    private final LocalCache<String, Account> accountsByNameCache;

    /**
     * Constructor.
     *
     * @param accountService      The {@link AccountService} used to find the {@link Account}s that are not cached.
     * @param accountsByNameCache The {@link LocalCache} of {@link Account}s by {@link Account#getName()}, shared among {@link Translator}s.
     *                            If {@code null}, {@link Account}s are not cached and {@link #findAccountIdByName(String)} always calls the {@link AccountService}.
     * @since 2.1.0
     */
    protected AbstractAccountResolvingTranslator(AccountService accountService, LocalCache<String, Account> accountsByNameCache) {
        this.accountService = Objects.requireNonNull(accountService, "accountService");
        this.accountsByNameCache = accountsByNameCache;
    }

    /**
     * Gets the {@link Account#getId()} of the {@link Account} with the given {@link Account#getName()}.
     * <p>
     * {@link Account}s are cached in the {@link LocalCache} given to {@link #AbstractAccountResolvingTranslator(AccountService, LocalCache)}:
     * <ul>
     *     <li>Cached {@link Account}s are returned without invoking the {@link AccountService}, so permissions are checked only when the {@link Account} is not cached.</li>
     *     <li>Cached {@link Account}s are not invalidated when updated or deleted: a deleted and recreated {@link Account} can be resolved to its old id until its entry expires.</li>
     *     <li>Not found {@link Account}s are never cached.</li>
     * </ul>
     *
     * @param accountName The {@link Account#getName()}.
     * @return The {@link Account#getId()}, or {@code null} if no {@link Account} with the given name exists.
     * @throws KapuaException If the {@link AccountService} fails to find the {@link Account}.
     * @since 2.1.0
     */
    protected KapuaId findAccountIdByName(String accountName) throws KapuaException {
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
        Account account = accountsByNameCache != null ? accountsByNameCache.get(accountName) : null;
        if (account == null) {
            account = accountService.findByName(accountName);
            if (account == null) {
                return null;
            }

            if (accountsByNameCache != null) {
                accountsByNameCache.put(accountName, account);
            }
        }
        return account.getId();
    }
}
