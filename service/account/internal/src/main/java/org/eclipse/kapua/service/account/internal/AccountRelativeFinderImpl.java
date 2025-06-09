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
package org.eclipse.kapua.service.account.internal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.AccountRelativeFinder;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountService;

public class AccountRelativeFinderImpl implements AccountRelativeFinder, KapuaService {

    private final AccountService accountService;

    @Inject
    public AccountRelativeFinderImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public AccountListResult findChildren(KapuaId scopeId, Optional<KapuaId> excludeTargetScopeId) throws KapuaException {
        final KapuaQuery childAccountsQuery = new KapuaQuery(scopeId);
        // Exclude the scope that is under config update
        if (excludeTargetScopeId.isPresent()) {
            childAccountsQuery.setPredicate(
                    childAccountsQuery.attributePredicate(
                            KapuaEntityAttributes.ENTITY_ID,
                            excludeTargetScopeId.get(),
                            AttributePredicate.Operator.NOT_EQUAL)
            );
        }

        return KapuaSecurityUtils.doPrivileged(() -> accountService.query(childAccountsQuery));
    }

    @Override
    public List<KapuaId> findParentIds(KapuaId accountId) throws KapuaException {
        Account account = KapuaSecurityUtils.doPrivileged(() -> accountService.find(accountId));

        if (account == null || account.getParentAccountPath() == null) {
            return Collections.emptyList();
        }

        ArrayList<KapuaId> parentAccountIds = new ArrayList<KapuaId>();

        String[] splitIds = account.getParentAccountPath().split("/");
        String accountIdStr = accountId.getId().toString();

        // Iterate in reverse order to get parent first, then grandparent, etc
        for (int i = splitIds.length - 1; i >= 0; i--) {
            String id = splitIds[i];

            if (id != null && !id.isEmpty() && !id.equals(accountIdStr)) {
                parentAccountIds.add(new KapuaEid(new BigInteger(id)));
            }
        }

        return parentAccountIds;
    }

    @Override
    public KapuaId findParentId(KapuaId accountId) throws KapuaException {
        Account account = KapuaSecurityUtils.doPrivileged(() -> accountService.find(accountId));

        if(account == null){
            throw new KapuaEntityNotFoundException(Account.TYPE, accountId);
        }

        return account.getScopeId();
    }
}