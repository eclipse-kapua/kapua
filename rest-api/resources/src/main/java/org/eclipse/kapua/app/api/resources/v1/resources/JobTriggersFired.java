/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.rest.model.CountResult;
import org.eclipse.kapua.commons.rest.model.EntityId;
import org.eclipse.kapua.commons.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerAttributes;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerListResult;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerQuery;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerService;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerStatus;

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

@Path("{scopeId}/jobs/{jobId}/triggers/{triggerId}/fired")
public class JobTriggersFired extends AbstractKapuaResource {

    @Inject
    public FiredTriggerService firedTriggerService;
    @Inject
    public FiredTriggerFactory firedTriggerFactory;

    /**
     * Gets the {@link Trigger} list for a given {@link Job}.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param jobId
     *         The {@link Job} id to filter results.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @param askTotalCount
     *         Whether or not to fetch the total count of elements.
     * @return The {@link FiredTriggerListResult} of all the jobs triggers associated to the current selected job.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public FiredTriggerListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @PathParam("triggerId") EntityId triggerId,
            @QueryParam("status") FiredTriggerStatus status,
            @QueryParam("askTotalCount") @DefaultValue("false") boolean askTotalCount,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {

        FiredTriggerQuery query = firedTriggerFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(FiredTriggerAttributes.TRIGGER_ID, triggerId)
        );

        if (status != null) {
            andPredicate.and(query.attributePredicate(FiredTriggerAttributes.STATUS, status));
        }

        query.setPredicate(andPredicate);

        query.setSortCriteria(query.fieldSortCriteria(FiredTriggerAttributes.FIRED_ON, SortOrder.DESCENDING));
        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, jobId, triggerId, query);
    }

    /**
     * Queries the results with the given {@link FiredTriggerQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link FiredTriggerQuery} to use to filter results.
     * @return The {@link FiredTriggerListResult} of all the result matching the given {@link FiredTriggerQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public FiredTriggerListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @PathParam("triggerId") EntityId triggerId,
            FiredTriggerQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(FiredTriggerAttributes.TRIGGER_ID, triggerId)
        );

        if (query.getPredicate() != null) {
            andPredicate.and(query.getPredicate());
        }

        query.setPredicate(andPredicate);

        return firedTriggerService.query(query);
    }

    /**
     * Counts the results with the given {@link FiredTriggerQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link FiredTriggerQuery} to use to filter results.
     * @return The count of all the result matching the given {@link FiredTriggerQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Path("_count")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @PathParam("triggerId") EntityId triggerId,
            FiredTriggerQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(FiredTriggerAttributes.TRIGGER_ID, triggerId)
        );

        if (query.getPredicate() != null) {
            andPredicate.and(query.getPredicate());
        }

        query.setPredicate(andPredicate);

        return new CountResult(firedTriggerService.count(query));
    }
}
