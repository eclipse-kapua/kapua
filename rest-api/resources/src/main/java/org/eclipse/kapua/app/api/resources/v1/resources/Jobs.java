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
import org.eclipse.kapua.commons.jersey.rest.model.CountResult;
import org.eclipse.kapua.commons.jersey.rest.model.EntityId;
import org.eclipse.kapua.commons.jersey.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobCreator;
import org.eclipse.kapua.service.job.JobFactory;
import org.eclipse.kapua.service.job.JobListResult;
import org.eclipse.kapua.service.job.JobQuery;
import org.eclipse.kapua.service.job.JobService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{scopeId}/jobs")
public class Jobs extends AbstractKapuaResource {

    @Inject
    public JobService jobService;
    @Inject
    public JobFactory jobFactory;

    /**
     * Gets the {@link Job} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param name
     *         The {@link Job} name to filter results
     * @param sortParam
     *         The name of the parameter that will be used as a sorting key
     * @param sortDir
     *         The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param askTotalCount
     *         Ask for the total count of the matched entities in the result
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link JobListResult} of all the jobs associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public JobListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("name") String name,
            @QueryParam("matchTerm") String matchTerm,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        JobQuery query = jobFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (!Strings.isNullOrEmpty(name)) {
            andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.NAME, name));
        }
        if (matchTerm != null && !matchTerm.isEmpty()) {
            andPredicate.and(query.matchPredicate(matchTerm));
        }
        query.setPredicate(andPredicate);

        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link JobQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link JobQuery} to use to filter results.
     * @return The {@link JobListResult} of all the result matching the given {@link JobQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public JobListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            JobQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return jobService.query(query);
    }

    /**
     * Counts the results with the given {@link JobQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link JobQuery} to use to filter results.
     * @return The count of all the result matching the given {@link JobQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId,
            JobQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(jobService.count(query));
    }

    /**
     * Returns the Job specified by the "jobId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link Job}.
     * @param jobId
     *         The id of the requested Job.
     * @return The requested Job object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{jobId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Job find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId) throws KapuaException {
        Job job = jobService.find(scopeId, jobId);

        return returnNotNullEntity(job, Job.TYPE, jobId);
    }

    /**
     * Creates a new {@link Job} based on the information provided in {@link JobCreator} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link Job}
     * @param jobCreator
     *         Provides the information for the new {@link Job} to be created.
     * @return The newly created {@link Job} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            JobCreator jobCreator) throws KapuaException {
        jobCreator.setScopeId(scopeId);

        return returnCreated(jobService.create(jobCreator));
    }

    /**
     * Updates the Job based on the information provided in the Job parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link Job}.
     * @param jobId
     *         The id of the requested {@link Job}
     * @param job
     *         The modified Job whose attributed need to be updated.
     * @return The updated job.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @PUT
    @Path("{jobId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Job update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            Job job) throws KapuaException {
        job.setScopeId(scopeId);
        job.setId(jobId);

        return jobService.update(job);
    }

    /**
     * Deletes the Job specified by the "jobId" path parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link Job}.
     * @param jobId
     *         The id of the Job to be deleted.
     * @return HTTP 201 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @DELETE
    @Path("{jobId}")
    public Response deleteJob(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @QueryParam("forced") @DefaultValue("false") Boolean forced)
            throws KapuaException {

        if (forced) {
            jobService.deleteForced(scopeId, jobId);
        } else {
            jobService.delete(scopeId, jobId);
        }

        return returnNoContent();
    }

}
