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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.MatchPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountAttributes;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountService;

import com.google.common.base.Strings;

@Path("{scopeId}/accounts")
public class Accounts extends AbstractKapuaResource {

    @Inject
    public AccountService accountService;
    @Inject
    public AccountFactory accountFactory;

    /**
     * Gets the {@link Account} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param name
     *         The {@link Account} name in which to search results.
     * @param recursive
     *         The {@link Account} name in which to search results.
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
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AccountListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId, //
            @QueryParam("name") String name, //
            @QueryParam("matchTerm") String matchTerm,
            @QueryParam("recursive") boolean recursive, //
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("offset") @DefaultValue("0") int offset, //
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {

        if (recursive) {
            return accountService.findChildrenRecursively(scopeId);
        }

        final KapuaQuery query = new KapuaQuery(scopeId);
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

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link KapuaQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link KapuaQuery} to use to filter results.
     * @return The {@link AccountListResult} of all the result matching the given {@link KapuaQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public AccountListResult query(
            @PathParam("scopeId") ScopeId scopeId, //
            KapuaQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return accountService.query(query);
    }

    /**
     * Counts the results with the given {@link KapuaQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to count results.
     * @param query
     *         The {@link KapuaQuery} to use to filter results.
     * @return The count of all the result matching the given {@link KapuaQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Path("_count")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId, //
            KapuaQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(accountService.count(query));
    }

    /**
     * Creates a new Account based on the information provided in AccountCreator parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link org.eclipse.kapua.service.account.Account}
     * @param accountCreator
     *         Provides the information for the new {@link org.eclipse.kapua.service.account.Account} to be created.
     * @return The newly created {@link org.eclipse.kapua.service.account.Account} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId, //
            AccountCreator accountCreator) throws KapuaException {
        accountCreator.setScopeId(scopeId);

        return returnCreated(accountService.create(accountCreator));
    }
}
