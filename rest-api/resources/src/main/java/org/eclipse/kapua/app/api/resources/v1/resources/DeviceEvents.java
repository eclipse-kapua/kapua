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

import org.eclipse.kapua.KapuaEntityNotFoundException;
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
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventAttributes;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventListResult;
import org.eclipse.kapua.service.device.registry.event.DeviceEventQuery;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/devices/{deviceId}/events")
@Tag(name = "Device Event")
public class DeviceEvents extends AbstractKapuaResource {

    @Inject
    public DeviceEventService deviceEventService;
    @Inject
    public DeviceEventFactory deviceEventFactory;
    @Inject
    public DeviceRegistryService deviceRegistryService;

    /**
     * Gets the {@link DeviceEvent} list in the scope.
     *
     * @param scopeId       The {@link ScopeId} in which to search results.
     * @param deviceId      The id of the {@link Device} in which to search results
     * @param resource      The resource of the {@link DeviceEvent} in which to search results
     * @param sortParam     The name of the parameter that will be used as a sorting key
     * @param sortDir       The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param askTotalCount Ask for the total count of the matched entities in the result
     * @param offset        The result set offset.
     * @param limit         The result set limit.
     * @return The {@link DeviceEventListResult} of all the deviceEvents associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Device Events available in the Scope",
            content = @Content(schema = @Schema(implementation = AccessInfoListResult.class))
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
    public DeviceEventListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The resource of the DeviceEvent in which to search results")
            @QueryParam("resource") String resource,
            @Parameter(description = "The start date to filter the results.")
            @QueryParam("startDate") DateParam startDateParam,
            @Parameter(description = "The end date to filter the results.")
            @QueryParam("endDate") DateParam endDateParam,
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
        DeviceEventQuery query = deviceEventFactory.newQuery(scopeId);

        if (deviceRegistryService.find(scopeId, deviceId) == null) {
            throw new KapuaEntityNotFoundException(Device.TYPE, deviceId);
        }

        AndPredicate andPredicate = query.andPredicate(query.attributePredicate(DeviceEventAttributes.DEVICE_ID, deviceId));

        if (!Strings.isNullOrEmpty(resource)) {
            andPredicate.and(query.attributePredicate(DeviceEventAttributes.RESOURCE, resource));
        }

        if (startDateParam != null) {
            andPredicate.and(query.attributePredicate(DeviceEventAttributes.RECEIVED_ON, startDateParam.getDate(), Operator.GREATER_THAN_OR_EQUAL));
        }
        if (endDateParam != null) {
            andPredicate.and(query.attributePredicate(DeviceEventAttributes.RECEIVED_ON, endDateParam.getDate(), Operator.LESS_THAN_OR_EQUAL));
        }

        query.setPredicate(andPredicate);

        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }
        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, deviceId, query);
    }

    /**
     * Queries the results with the given {@link DeviceEventQuery} parameter.
     *
     * @param scopeId  The {@link ScopeId} in which to search results.
     * @param deviceId The id of the {@link Device} in which to search results
     * @param query    The {@link DeviceEventQuery} to use to filter results.
     * @return The {@link DeviceEventListResult} of all the result matching the given {@link DeviceEventQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the Device Event")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
            content = @Content(schema = @Schema(implementation = DeviceEventListResult.class))
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
    public DeviceEventListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            DeviceEventQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        if (deviceRegistryService.find(scopeId, deviceId) == null) {
            throw new KapuaEntityNotFoundException(Device.TYPE, deviceId);
        }

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(DeviceEventAttributes.DEVICE_ID, deviceId),
                query.getPredicate()
        );

        query.setPredicate(andPredicate);

        return deviceEventService.query(query);
    }

    /**
     * Counts the results with the given {@link DeviceEventQuery} parameter.
     *
     * @param scopeId  The {@link ScopeId} in which to search results.
     * @param deviceId The id of the {@link Device} in which to search results
     * @param query    The {@link DeviceEventQuery} to use to filter results.
     * @return The count of all the result matching the given {@link DeviceEventQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Device Events")
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
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            DeviceEventQuery query) throws KapuaException {

        if (deviceRegistryService.find(scopeId, deviceId) == null) {
            throw new KapuaEntityNotFoundException(Device.TYPE, deviceId);
        }

        query.setScopeId(scopeId);
        query.setPredicate(query.attributePredicate(DeviceEventAttributes.DEVICE_ID, deviceId));

        return new CountResult(deviceEventService.count(query));
    }

    /**
     * Returns the DeviceEvent specified by the "deviceEventId" path parameter.
     *
     * @param scopeId       The {@link ScopeId} of the requested {@link DeviceEvent}.
     * @param deviceId      The {@link Device} id of the request {@link DeviceEvent}.
     * @param deviceEventId The id of the requested DeviceEvent.
     * @return The requested DeviceEvent object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{deviceEventId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a single DeviceEvent")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired Device Event",
            content = @Content(schema = @Schema(implementation = DeviceEvent.class))
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
    public DeviceEvent find(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The ID of the Device Event on which to perform the operation")
            @PathParam("deviceEventId") EntityId deviceEventId) throws KapuaException {

        if (deviceRegistryService.find(scopeId, deviceId) == null) {
            throw new KapuaEntityNotFoundException(Device.TYPE, deviceId);
        }

        DeviceEventQuery query = deviceEventFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(DeviceEventAttributes.DEVICE_ID, deviceId),
                query.attributePredicate(KapuaEntityAttributes.ENTITY_ID, deviceEventId)
        );

        query.setPredicate(andPredicate);
        query.setOffset(0);
        query.setLimit(1);

        DeviceEventListResult results = deviceEventService.query(query);

        return returnNotNullEntity(results.getFirstItem(), DeviceEvent.TYPE, deviceEventId);
    }

    /**
     * Deletes the DeviceEvent specified by the "deviceEventId" path parameter.
     *
     * @param deviceId      The id of the Device in which to delete the event
     * @param deviceEventId The id of the DeviceEvent to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{deviceEventId}")
    @Operation(summary = "Delete a single DeviceEvent")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The Device Event has been deleted"
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
    public Response deleteDeviceEvent(@Parameter(description = "The ID of the Scope where to perform the operation.")
                                      @PathParam("scopeId") ScopeId scopeId,
                                      @Parameter(description = "The ID of the Device on which to perform the operation")
                                      @PathParam("deviceId") EntityId deviceId,
                                      @Parameter(description = "The ID of the Device Event on which to perform the operation")
                                      @PathParam("deviceEventId") EntityId deviceEventId) throws KapuaException {

        if (deviceRegistryService.find(scopeId, deviceId) == null) {
            throw new KapuaEntityNotFoundException(Device.TYPE, deviceId);
        }

        deviceEventService.delete(scopeId, deviceEventId);

        return returnNoContent();
    }
}
