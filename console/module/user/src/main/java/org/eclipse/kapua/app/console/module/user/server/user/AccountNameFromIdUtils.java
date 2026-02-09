/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.user.server.user;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountAttributes;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountQuery;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Utils to resolve {@link Account#getId()} into respective {@link Account#getName()}.
 *
 * @since 2.1.0
 */
public class AccountNameFromIdUtils {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final AccountService ACCOUNT_SERVICE = LOCATOR.getService(AccountService.class);
    private static final AccountFactory ACCOUNT_FACTORY = LOCATOR.getFactory(AccountFactory.class);

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    private AccountNameFromIdUtils() {
    }

    /**
     * Creates a map of {@link Account#getId()} and {@link Account#getName()} starting from a {@link KapuaListResult} of {@link GroupPermission}s
     *
     * @param groupPermissions The {@link KapuaListResult} of {@link Account}s from which to extract the {@link Account#getId()} to resolve
     * @return A {@link Map} of {@link Account#getId()} associated with the {@link Account#getName()}.
     * @throws Exception
     * @since 2.1.0
     */
    public static Map<KapuaId, String> resolveAccountNamesFrom(GroupPermissionListResult groupPermissions) throws Exception {
        Set<KapuaId> accountIds = new HashSet<KapuaId>();

        for (GroupPermission groupPermission : groupPermissions.getItems()) {
            accountIds.add(groupPermission.getPermission().getTargetScopeId());
        }

        return resolveAccountNameFromIds(accountIds);
    }

    /**
     * Creates a map of {@link Account#getId()} and {@link Account#getName()} starting from a {@link KapuaListResult} of {@link Account}s
     *
     * @param accounts The {@link KapuaListResult} of {@link Account}s from which to extract the {@link Account#getId()} to resolve
     * @return A {@link Map} of {@link Account#getId()} associated with the {@link Account#getName()}.
     * @throws Exception
     * @since 2.1.0
     */
    public static Map<KapuaId, String> resolveAccountNamesFrom(AccountListResult accounts) throws Exception {
        Set<KapuaId> accountIds = new HashSet<KapuaId>();

        for (Account account : accounts.getItems()) {
            accountIds.add(account.getId());
        }

        return resolveAccountNameFromIds(accountIds);
    }

    /**
     * Creates a map of {@link Account#getId()} and {@link Account#getName()} starting from a {@link Set} of {@link Account#getId()}
     *
     * @param accountIds The {@link Set} of {@link Account#getId()}s to resolve
     * @return A {@link Map} of {@link Account#getId()} associated with the {@link Account#getName()}.
     * @throws KapuaException
     * @since 2.1.0
     */
    public static HashMap<KapuaId, String> resolveAccountNameFromIds(final Set<KapuaId> accountIds) throws KapuaException {
        // Query to find matching ids
        AccountListResult accounts = KapuaSecurityUtils.doPrivileged(new Callable<AccountListResult>() {

            @Override
            public AccountListResult call() throws Exception {
                AccountQuery accountQuery = ACCOUNT_FACTORY.newQuery(KapuaId.ANY);

                accountQuery.setPredicate(
                        accountQuery.attributePredicate(AccountAttributes.ENTITY_ID, accountIds)
                );

                return ACCOUNT_SERVICE.query(accountQuery);
            }
        });

        // Produce map with id and account name association
        HashMap<KapuaId, String> idAndAccountNameMap = new HashMap<KapuaId, String>();
        for (Account account : accounts.getItems()) {
            idAndAccountNameMap.put(account.getId(), account.getName());
        }

        // Return the map
        return idAndAccountNameMap;
    }
}
