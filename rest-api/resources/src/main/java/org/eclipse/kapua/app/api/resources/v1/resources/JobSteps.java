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
import org.eclipse.kapua.commons.rest.model.CountResult;
import org.eclipse.kapua.commons.rest.model.EntityId;
import org.eclipse.kapua.commons.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepCreator;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepQuery;
import org.eclipse.kapua.service.job.step.JobStepService;

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

@Path("{scopeId}/jobs/{jobId}/steps")
public class JobSteps extends AbstractKapuaResource {

    @Inject
    public JobStepService jobStepService;
    @Inject
    public JobStepFactory jobStepFactory;

    /**
     * Gets the {@link JobStep} list for a given {@link Job}.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param jobId
     *         The {@link Job} id to filter results
     * @param name
     *         The name of the {@link JobStep} to filter result
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
     * @return The {@link JobStepListResult} of all the jobs jobSteps associated to the current selected job.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public JobStepListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @QueryParam("name") String name,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {

        JobStepQuery query = jobStepFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(query.attributePredicate(JobStepAttributes.JOB_ID, jobId));

        if (!Strings.isNullOrEmpty(name)) {
            andPredicate = andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.NAME, name));
        }

        query.setPredicate(andPredicate);

        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, jobId, query);
    }

    /**
     * Queries the results with the given {@link JobStepQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link JobStepQuery} to use to filter results.
     * @return The {@link JobStepListResult} of all the result matching the given {@link JobStepQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public JobStepListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            JobStepQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        QueryPredicate predicate;
        if (query.getPredicate() != null) {
            predicate = query.getPredicate();
        } else {
            predicate = query.attributePredicate(JobStepAttributes.JOB_ID, jobId);
        }
        query.setPredicate(predicate);
        return jobStepService.query(query);
    }

    /**
     * Counts the results with the given {@link JobStepQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link JobStepQuery} to use to filter results.
     * @return The count of all the result matching the given {@link JobStepQuery} parameter.
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
            @PathParam("jobId") EntityId jobId,
            JobStepQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        query.setPredicate(query.attributePredicate(JobStepAttributes.JOB_ID, jobId));

        return new CountResult(jobStepService.count(query));
    }

    /**
     * Returns the Job specified by the "jobId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link Job}.
     * @param jobId
     *         The id of the requested Job.
     * @param stepId
     *         The id of the requested JobStep.
     * @return The requested Job object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{stepId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public JobStep find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @PathParam("stepId") EntityId stepId) throws KapuaException {
        return jobStepService.find(scopeId, stepId);
    }

    /**
     * Creates a new {@link JobStep} based on the information provided in {@link JobStepCreator} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link JobStep}
     * @param jobId
     *         The ID of the {@link Job} to attach the {@link JobStep} to
     * @param jobStepCreator
     *         Provides the information for the new {@link JobStep} to be created.
     * @return The newly created {@link JobStep} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            JobStepCreator jobStepCreator) throws KapuaException {
        jobStepCreator.setScopeId(scopeId);
        jobStepCreator.setJobId(jobId);
        return returnCreated(jobStepService.create(jobStepCreator));
    }

    /**
     * Updates a new {@link JobStep} based on the information provided in {@link JobStep} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link JobStep}
     * @param jobId
     *         The ID of the {@link Job} to attach the {@link JobStep} to
     * @param jobStep
     *         Provides the information for the new {@link JobStep} to be created.
     * @param jobStepId
     *         The ID of the {@link JobStep} to be updated
     * @return The newly created {@link JobStep} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @PUT
    @Path("{stepId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public JobStep update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @PathParam("stepId") EntityId jobStepId,
            JobStep jobStep) throws KapuaException {
        jobStep.setScopeId(scopeId);
        jobStep.setJobId(jobId);
        jobStep.setId(jobStepId);
        return jobStepService.update(jobStep);
    }

    /**
     * Deletes the JobStep specified by the "stepId" path parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link JobStep}.
     * @param stepId
     *         The id of the JobStep to be deleted.
     * @return HTTP 201 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @DELETE
    @Path("{stepId}")
    public Response deleteJobStep(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("stepId") EntityId stepId) throws KapuaException {
        jobStepService.delete(scopeId, stepId);
        return returnNoContent();
    }

}
