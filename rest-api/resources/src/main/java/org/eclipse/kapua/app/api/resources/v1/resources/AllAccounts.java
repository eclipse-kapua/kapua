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
package org.eclipse.kapua.app.api.resources.v1.resources;

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.MatchPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.account.AccountAttributes;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountQuery;
import org.eclipse.kapua.service.account.AccountService;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Path("accounts")
public class AllAccounts extends AbstractKapuaResource {

    @Inject
    public AccountService accountService;
    @Inject
    public AccountFactory accountFactory;

    /**
     * Gets the {@link org.eclipse.kapua.service.account.Account} list in the scope.
     *
     * @param name
     *         The {@link org.eclipse.kapua.service.account.Account} name in which to search results.
     * @param sortParam
     *         The name of the parameter that will be used as a sorting key
     * @param sortDir
     *         The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link AccountListResult} of all the accounts associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AccountListResult simpleQuery( //TODO: lot of common code with other accounts query method, refactor
            @QueryParam("name") String name, //
            @QueryParam("matchTerm") String matchTerm,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("offset") @DefaultValue("0") int offset, //
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {

        AccountQuery allAccountsQuery = accountFactory.newQuery(null);
        allAccountsQuery.setAskTotalCount(askTotalCount);

        AndPredicate andPredicate = allAccountsQuery.andPredicate();
        if (!Strings.isNullOrEmpty(name)) {
            andPredicate.and(allAccountsQuery.attributePredicate(KapuaNamedEntityAttributes.NAME, name));
        }
        if (!Strings.isNullOrEmpty(sortParam)) {
            allAccountsQuery.setSortCriteria(allAccountsQuery.fieldSortCriteria(sortParam, sortDir));
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
        allAccountsQuery.setPredicate(andPredicate);

        allAccountsQuery.setOffset(offset);
        allAccountsQuery.setLimit(limit);

        return accountService.query(allAccountsQuery);
    }
}
