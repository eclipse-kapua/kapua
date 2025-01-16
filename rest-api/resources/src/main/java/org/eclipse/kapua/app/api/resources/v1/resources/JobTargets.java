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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionListResult;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.job.targets.JobTargetCreator;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetQuery;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/jobs/{jobId}/targets")
@Tag(name = "Job - Target")
public class JobTargets extends AbstractKapuaResource {

    @Inject
    public JobTargetService jobTargetService;
    @Inject
    public JobExecutionService jobExecutionService;
    @Inject
    public JobTargetFactory jobTargetFactory;
    @Inject
    public JobExecutionFactory jobExecutionFactory;

    /**
     * Gets the {@link JobTarget} list for a given {@link Job}.
     *
     * @param scopeId       The {@link ScopeId} in which to search results.
     * @param jobId         The {@link Job} id to filter results
     * @param sortParam     The name of the parameter that will be used as a sorting key
     * @param sortDir       The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param askTotalCount Ask for the total count of the matched entities in the result
     * @param offset        The result set offset.
     * @param limit         The result set limit.
     * @return The {@link JobTargetListResult} of all the jobs targets associated to the current selected job.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the Jobs targets")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Jobs targets available in the Scope",
            content = @Content(schema = @Schema(implementation = JobTargetListResult.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public JobTargetListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            @Parameter(description = "The status of the job target")
            @QueryParam("status") JobTargetStatus status,
            @Parameter(description = "The name of the parameter that will be used as a sorting key")
            @QueryParam("sortParam") String sortParam,
            @Parameter(description = "The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.")
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @Parameter(description = "If true, the total count of the entities matching the query will be included in the result set")
            @QueryParam("askTotalCount") boolean askTotalCount,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        JobTargetQuery query = jobTargetFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(query.attributePredicate(JobExecutionAttributes.JOB_ID, jobId));

        if (status != null) {
            andPredicate.and(query.attributePredicate(JobTargetAttributes.STATUS, status));
        }

        query.setPredicate(andPredicate);

        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return jobTargetService.query(query);
    }

    /**
     * Queries the results with the given {@link JobTargetQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link JobTargetQuery} to use to filter results.
     * @return The {@link JobTargetListResult} of all the result matching the given {@link JobTargetQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the Jobs Targets")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
            content = @Content(schema = @Schema(implementation = DeviceListResult.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public JobTargetListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            JobTargetQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        query.setPredicate(query.attributePredicate(JobTargetAttributes.JOB_ID, jobId));
        return jobTargetService.query(query);
    }

    /**
     * Counts the results with the given {@link JobTargetQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link JobTargetQuery} to use to filter results.
     * @return The count of all the result matching the given {@link JobTargetQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Jobs Targets")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The count of the available Entities",
            content = @Content(schema = @Schema(implementation = CountResult.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public CountResult count(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            JobTargetQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        query.setPredicate(query.attributePredicate(JobTargetAttributes.JOB_ID, jobId));

        return new CountResult(jobTargetService.count(query));
    }

    /**
     * Returns the Job specified by the "jobId" path parameter.
     *
     * @param scopeId  The {@link ScopeId} of the requested {@link Job}.
     * @param jobId    The id of the requested Job.
     * @param targetId The id of the requested JobTarget.
     * @return The requested Job object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{targetId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JobTarget find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @PathParam("targetId") EntityId targetId) throws KapuaException {
        JobTargetQuery jobTargetQuery = jobTargetFactory.newQuery(scopeId);
        jobTargetQuery.setPredicate(jobTargetQuery.andPredicate(
                jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_ID, jobId),
                jobTargetQuery.attributePredicate(KapuaEntityAttributes.ENTITY_ID, targetId)
        ));
        jobTargetQuery.setOffset(0);
        jobTargetQuery.setLimit(1);
        JobTargetListResult jobTargetListResult = jobTargetService.query(jobTargetQuery);

        return returnNotNullEntity(jobTargetListResult.getFirstItem(), JobTarget.TYPE, targetId);
    }

    @GET
    @Path("{targetId}/executions")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the Executions in which a single Job Target was involved")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "A list of all the Executions in which a single Job Target was involved",
            content = @Content(schema = @Schema(implementation = JobExecutionListResult.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "The desired entity could not be found",
            content = @Content(schema = @Schema(implementation = EntityNotFoundExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public JobExecutionListResult executionsByTarget(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            @Parameter(description = "The entity ID of the on which perform the operations")
            @PathParam("targetId") EntityId targetId,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        JobExecutionQuery jobExecutionQuery = jobExecutionFactory.newQuery(scopeId);
        jobExecutionQuery.setPredicate(jobExecutionQuery.attributePredicate(JobExecutionAttributes.TARGET_IDS, new KapuaId[]{targetId}));
        JobExecutionListResult jobExecutionListResult = jobExecutionService.query(jobExecutionQuery);

        jobExecutionQuery.setOffset(offset);
        jobExecutionQuery.setLimit(limit);

        return jobExecutionListResult;
    }

    /**
     * Creates a new {@link JobTarget} based on the information provided in {@link JobTargetCreator}
     * parameter.
     *
     * @param scopeId          The {@link ScopeId} in which to create the {@link JobTarget}
     * @param jobId            The ID of the {@link Job} to attach the {@link JobTarget} to
     * @param jobTargetCreator Provides the information for the new {@link JobTarget} to be created.
     * @return The newly created {@link JobTarget} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Create a new Job Target")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The Job Target that has just been created",
            content = @Content(schema = @Schema(implementation = Job.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Response create(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            JobTargetCreator jobTargetCreator) throws KapuaException {
        jobTargetCreator.setScopeId(scopeId);
        jobTargetCreator.setJobId(jobId);

        return returnCreated(jobTargetService.create(jobTargetCreator));
    }

    /**
     * Deletes the JobTarget specified by the "targetId" path parameter.
     *
     * @param scopeId  The ScopeId of the requested {@link JobTarget}.
     * @param targetId The id of the JobTarget to be deleted.
     * @return HTTP 201 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */

    @DELETE
    @Path("{targetId}")
    @Operation(summary = "Delete a single Job Target")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The Job Target has been deleted"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "The desired entity could not be found",
            content = @Content(schema = @Schema(implementation = EntityNotFoundExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Response deleteJobTarget(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("targetId") EntityId targetId) throws KapuaException {
        jobTargetService.delete(scopeId, targetId);

        return returnNoContent();
    }

}
