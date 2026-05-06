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
package org.eclipse.kapua.app.api.core.resources;

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.MatchPredicate;
import org.eclipse.kapua.service.account.AccountAttributes;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountQuery;
import org.eclipse.kapua.service.account.AccountService;

import java.util.Arrays;
import java.util.List;

/**
 * @since 2.0.0
 */
public class AccountAbstractKapuaResource extends AbstractKapuaResource {

    protected AccountListResult queryAccounts(
            ScopeId scopeId,
            String name,
            String matchTerm,
            String sortParam,
            SortOrder sortDir,
            boolean askTotalCount,
            int offset,
            int limit,
            AccountFactory factory,
            AccountService service) throws KapuaException {

        AccountQuery query = factory.newQuery(scopeId);
        query.setAskTotalCount(askTotalCount);

        AndPredicate andPredicate = query.andPredicate();
        if (!Strings.isNullOrEmpty(name)) {
            andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.NAME, name));
        }
        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }
        if (matchTerm != null && !matchTerm.isEmpty()) {
            andPredicate.and(new MatchPredicate<String>() {

                @Override
                public List<String> getAttributeNames() {
                    return Arrays.asList(AccountAttributes.NAME, AccountAttributes.ORGANIZATION_NAME, AccountAttributes.CONTACT_NAME, AccountAttributes.ORGANIZATION_EMAIL);
                }

                @Override
                public String getMatchTerm() {
                    return matchTerm;
                }
            });
        }
        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);

        return service.query(query);
    }


}
