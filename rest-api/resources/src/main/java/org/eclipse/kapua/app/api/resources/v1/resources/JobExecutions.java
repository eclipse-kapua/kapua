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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.DateParam;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionListResult;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetQuery;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/jobs/{jobId}/executions")
@Tag(name = "Job - Execution")
public class JobExecutions extends AbstractKapuaResource {

    @Inject
    public JobExecutionService jobExecutionService;
    @Inject
    public JobTargetService jobTargetService;
    @Inject
    public JobExecutionFactory jobExecutionFactory;
    @Inject
    public JobTargetFactory jobTargetFactory;

    /**
     * Gets the {@link JobExecution} list for a given {@link Job}.
     *
     * @param scopeId       The {@link ScopeId} in which to search results.
     * @param jobId         The {@link Job} id to filter results
     * @param askTotalCount Ask for the total count of the matched entities in the result
     * @param sortParam     The name of the parameter that will be used as a sorting key
     * @param sortDir       The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param offset        The result set offset.
     * @param limit         The result set limit.
     * @return The {@link JobExecutionListResult} of all the jobs executions associated to the current selected job.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the Jobs Executions")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Executions of the Job",
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public JobExecutionListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            @Parameter(description = "The start date to filter the results.")
            @QueryParam("askTotalCount") boolean askTotalCount,
            @Parameter(description = "The start date to filter the results.")
            @QueryParam("startDate") DateParam startDateParam,
            @Parameter(description = "The end date to filter the results.")
            @QueryParam("endDate") DateParam endDateParam,
            @Parameter(description = "The name of the parameter that will be used as a sorting key")
            @QueryParam("sortParam") String sortParam,
            @Parameter(description = "The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.")
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        JobExecutionQuery query = jobExecutionFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(query.attributePredicate(JobExecutionAttributes.JOB_ID, jobId));

        if (startDateParam != null) {
            andPredicate.and(query.attributePredicate(JobExecutionAttributes.STARTED_ON, startDateParam.getDate(), AttributePredicate.Operator.GREATER_THAN_OR_EQUAL));
        }
        if (endDateParam != null) {
            andPredicate.and(query.attributePredicate(JobExecutionAttributes.ENDED_ON, endDateParam.getDate(), AttributePredicate.Operator.LESS_THAN_OR_EQUAL));
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
     * Queries the results with the given {@link JobExecutionQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link JobExecutionQuery} to use to filter results.
     * @return The {@link JobExecutionListResult} of all the result matching the given {@link JobExecutionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the Devices")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public JobExecutionListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            JobExecutionQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        final AndPredicate andPredicate = query.andPredicate(
            query.attributePredicate(JobExecutionAttributes.JOB_ID, jobId),
            query.getPredicate()
        );
        query.setPredicate(andPredicate);
        return jobExecutionService.query(query);
    }

    /**
     * Counts the results with the given {@link JobExecutionQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link JobExecutionQuery} to use to filter results.
     * @return The count of all the result matching the given {@link JobExecutionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Devices")
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
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            JobExecutionQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        query.setPredicate(query.attributePredicate(JobExecutionAttributes.JOB_ID, jobId));

        return new CountResult(jobExecutionService.count(query));
    }

    /**
     * Returns the Job specified by the "jobId" path parameter.
     *
     * @param scopeId     The {@link ScopeId} of the requested {@link Job}.
     * @param jobId       The id of the requested Job.
     * @param executionId The id of the requested JobExecution.
     * @return The requested Job object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{executionId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a single Job Execution")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired Job Execution",
            content = @Content(schema = @Schema(implementation = JobExecutions.class))
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
    public JobExecution find(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            @Parameter(description = "The ID of the Job Execution on which to perform the operation")
            @PathParam("executionId") EntityId executionId) throws KapuaException {
        JobExecutionQuery jobExecutionQuery = jobExecutionFactory.newQuery(scopeId);
        jobExecutionQuery.setPredicate(jobExecutionQuery.andPredicate(
                jobExecutionQuery.attributePredicate(JobExecutionAttributes.JOB_ID, jobId),
                jobExecutionQuery.attributePredicate(KapuaEntityAttributes.ENTITY_ID, executionId)
        ));
        jobExecutionQuery.setOffset(0);
        jobExecutionQuery.setLimit(1);
        JobExecutionListResult jobExecutionListResult = jobExecutionService.query(jobExecutionQuery);

        return returnNotNullEntity(jobExecutionListResult.getFirstItem(), JobExecution.TYPE, executionId);
    }

    @GET
    @Path("{executionId}/targets")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the targets involved in a single Job Execution")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the targets involved in a single Job Execution",
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
    public JobTargetListResult executionsByTarget(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            @Parameter(description = "The ID of the Job Execution on which to perform the operation")
            @PathParam("executionId") EntityId executionId,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        JobExecution jobExecution = jobExecutionService.find(scopeId, executionId);
        JobTargetQuery jobTargetQuery = jobTargetFactory.newQuery(scopeId);
        jobTargetQuery.setPredicate(jobTargetQuery.attributePredicate(KapuaEntityAttributes.ENTITY_ID, jobExecution.getTargetIds()));
        jobTargetQuery.setLimit(limit);
        jobTargetQuery.setOffset(offset);

        return jobTargetService.query(jobTargetQuery);
    }

}
