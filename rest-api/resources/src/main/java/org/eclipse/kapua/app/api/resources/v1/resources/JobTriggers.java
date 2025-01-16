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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.TriggerAttributes;
import org.eclipse.kapua.service.scheduler.trigger.TriggerCreator;
import org.eclipse.kapua.service.scheduler.trigger.TriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.TriggerListResult;
import org.eclipse.kapua.service.scheduler.trigger.TriggerQuery;
import org.eclipse.kapua.service.scheduler.trigger.TriggerService;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/jobs/{jobId}/triggers")
@Tag(name = "Job - Trigger")
public class JobTriggers extends AbstractKapuaResource {

    @Inject
    public TriggerService triggerService;
    @Inject
    public TriggerFactory triggerFactory;

    /**
     * Gets the {@link Trigger} list for a given {@link Job}.
     *
     * @param scopeId       The {@link ScopeId} in which to search results.
     * @param jobId         The {@link Job} id to filter results
     * @param name          The name of the {@link Trigger} to filter result
     * @param sortParam     The name of the parameter that will be used as a sorting key
     * @param sortDir       The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param askTotalCount Ask for the total count of the matched entities in the result
     * @param offset        The result set offset.
     * @param limit         The result set limit.
     * @return The {@link TriggerListResult} of all the jobs triggers associated to the current selected job.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the Jobs Triggers")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Jobs Triggers available in the Scope",
            content = @Content(schema = @Schema(implementation = TriggerListResult.class))
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
    public TriggerListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            @Parameter(description = "The job trigger name to filter results")
            @QueryParam("name") String name,
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

        TriggerQuery query = triggerFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(returnJobIdPredicate(jobId, query));

        if (!Strings.isNullOrEmpty(name)) {
            andPredicate = andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.NAME, name));
        }

        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setAskTotalCount(askTotalCount);
        query.setPredicate(andPredicate);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, jobId, query);
    }

    /**
     * Queries the results with the given {@link TriggerQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link TriggerQuery} to use to filter results.
     * @return The {@link TriggerListResult} of all the result matching the given {@link TriggerQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the Jobs Triggers")
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
    public TriggerListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            TriggerQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        QueryPredicate predicate;
        if (query.getPredicate() != null) {
            predicate = query.getPredicate();
        } else {
            predicate = returnJobIdPredicate(jobId, query);
        }
        query.setPredicate(predicate);
        return triggerService.query(query);
    }

    /**
     * Counts the results with the given {@link TriggerQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link TriggerQuery} to use to filter results.
     * @return The count of all the result matching the given {@link TriggerQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Jobs Triggers")
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
            TriggerQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        query.setPredicate(returnJobIdPredicate(jobId, query));

        return new CountResult(triggerService.count(query));
    }

    /**
     * Returns the Job specified by the "jobId" path parameter.
     *
     * @param scopeId   The {@link ScopeId} of the requested {@link Job}.
     * @param jobId     The id of the requested Job.
     * @param triggerId The id of the requested Trigger.
     * @return The requested Job object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{triggerId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a single Job trigger")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired Job trigger",
            content = @Content(schema = @Schema(implementation = Trigger.class))
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
    public Trigger find(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            @Parameter(description = "The ID of the Job Trigger on which to perform the operation")
            @PathParam("triggerId") EntityId triggerId) throws KapuaException {
        TriggerQuery triggerQuery = triggerFactory.newQuery(scopeId);
        triggerQuery.setPredicate(triggerQuery.andPredicate(
                returnJobIdPredicate(jobId, triggerQuery),
                triggerQuery.attributePredicate(KapuaEntityAttributes.ENTITY_ID, triggerId)
        ));
        triggerQuery.setOffset(0);
        triggerQuery.setLimit(1);
        TriggerListResult triggerListResult = triggerService.query(triggerQuery);

        return returnNotNullEntity(triggerListResult.getFirstItem(), Trigger.TYPE, triggerId);
    }

    private AndPredicate returnJobIdPredicate(KapuaId jobId, TriggerQuery query) {
        AttributePredicate<String> kapuaPropertyNameAttributePredicate = query.attributePredicate(TriggerAttributes.TRIGGER_PROPERTIES_NAME, "jobId");
        AttributePredicate<String> kapuaPropertyValueAttributePredicate = query.attributePredicate(TriggerAttributes.TRIGGER_PROPERTIES_VALUE, jobId.toCompactId());
        AttributePredicate<String> kapuaPropertyTypeAttributePredicate = query.attributePredicate(TriggerAttributes.TRIGGER_PROPERTIES_TYPE, KapuaId.class.getName());

        return query.andPredicate(
                kapuaPropertyNameAttributePredicate,
                kapuaPropertyValueAttributePredicate,
                kapuaPropertyTypeAttributePredicate
        );
    }

    /**
     * Creates a new {@link Trigger} based on the information provided in {@link TriggerCreator}
     * parameter.
     *
     * @param scopeId        The {@link ScopeId} in which to create the {@link Trigger}
     * @param triggerCreator Provides the information for the new {@link Trigger} to be created.
     * @param jobId          The ID of the {@link Job} to attach the {@link Trigger} to
     * @return The newly created {@link Trigger} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Create a new Job Trigger")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The Job Trigger that has just been created",
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
            TriggerCreator triggerCreator) throws KapuaException {
        triggerCreator.setScopeId(scopeId);
        List<TriggerProperty> triggerProperties = triggerCreator.getTriggerProperties();
        if (triggerProperties == null) {
            triggerProperties = new ArrayList<>();
            triggerCreator.setTriggerProperties(triggerProperties);
        }
        triggerProperties.removeIf(triggerProperty -> Arrays.stream(new String[]{"scopeId", "jobId"}).anyMatch(propertyToRemove -> propertyToRemove.equals(triggerProperty.getName())));
        triggerProperties.add(triggerFactory.newTriggerProperty("scopeId", KapuaId.class.getCanonicalName(), scopeId.toCompactId()));
        triggerProperties.add(triggerFactory.newTriggerProperty("jobId", KapuaId.class.getCanonicalName(), jobId.toCompactId()));
        return returnCreated(triggerService.create(triggerCreator));
    }

    /**
     * Updates a {@link Trigger} based on the information provided in the provided {@link Trigger}
     * parameter.
     *
     * @param scopeId   The {@link ScopeId} in which to create the {@link Trigger}
     * @param triggerId The ID of the {@link Trigger} to update
     * @param trigger   Provides the information for the new {@link Trigger} to be updated.
     * @param jobId     The ID of the {@link Job} to attach the {@link Trigger} to
     * @return The updated {@link Trigger} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */

    @PUT
    @Path("{triggerId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Update a single Job Trigger")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the updated Job Trigger",
            content = @Content(schema = @Schema(implementation = Trigger.class))
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
    public Trigger update(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job on which to perform the operation")
            @PathParam("jobId") EntityId jobId,
            @Parameter(description = "The ID of the Job Trigger on which to perform the operation")
            @PathParam("triggerId") EntityId triggerId,
            @Parameter(description = "An object containing the new properties for the Job Trigger to update")
            Trigger trigger) throws KapuaException {
        List<TriggerProperty> triggerProperties = trigger.getTriggerProperties();
        if (triggerProperties == null) {
            triggerProperties = new ArrayList<>();
            trigger.setTriggerProperties(triggerProperties);
        }
        triggerProperties.removeIf(triggerProperty -> Arrays.stream(new String[]{"scopeId", "jobId"}).anyMatch(propertyToRemove -> propertyToRemove.equals(triggerProperty.getName())));
        triggerProperties.add(triggerFactory.newTriggerProperty("scopeId", KapuaId.class.getCanonicalName(), scopeId.toCompactId()));
        triggerProperties.add(triggerFactory.newTriggerProperty("jobId", KapuaId.class.getCanonicalName(), jobId.toCompactId()));
        trigger.setScopeId(scopeId);
        trigger.setId(triggerId);
        return triggerService.update(trigger);
    }

    /**
     * Deletes the Trigger specified by the "triggerId" path parameter.
     *
     * @param scopeId   The ScopeId of the requested {@link Trigger}.
     * @param triggerId The id of the Trigger to be deleted.
     * @return HTTP 201 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @DELETE
    @Path("{triggerId}")
    @Operation(summary = "Delete a single Job Trigger")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The Job Trigger has been deleted"
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
    public Response deleteTrigger(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Job Trigger on which to perform the operation")
            @PathParam("triggerId") EntityId triggerId) throws KapuaException {
        triggerService.delete(scopeId, triggerId);

        return returnNoContent();
    }

}
