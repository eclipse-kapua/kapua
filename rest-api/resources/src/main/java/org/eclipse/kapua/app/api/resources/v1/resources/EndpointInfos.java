/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoAttributes;
import org.eclipse.kapua.service.endpoint.EndpointInfoCreator;
import org.eclipse.kapua.service.endpoint.EndpointInfoFactory;
import org.eclipse.kapua.service.endpoint.EndpointInfoListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/endpointInfos")
@Tag(name = "Endpoint Info")
public class EndpointInfos extends AbstractKapuaResource {

    @Inject
    public EndpointInfoService endpointInfoService;
    @Inject
    public EndpointInfoFactory endpointInfoFactory;

    /**
     * Gets the {@link EndpointInfo} list in the scope.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param usage   The {@link EndpointInfo} usage to filter results
     * @param offset  The result set offset.
     * @param limit   The result set limit.
     * @return The {@link EndpointInfoListResult} of all the endpointInfos associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the EndpointInfos")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the EndpointInfos available in the Scope",
            content = @Content(schema = @Schema(implementation = EndpointInfoListResult.class))
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
    public EndpointInfoListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The endpointInfo usage to filter results")
            @QueryParam("usage") String usage,
            @Parameter(description = "The type of the Endpoints to query for")
            @QueryParam("endpointType") @DefaultValue(EndpointInfo.ENDPOINT_TYPE_RESOURCE) String endpointType,
            @Parameter(description = "A term to match on different fields. Every entity whose at least one of the specified fields starts with this value will be matched.\n" +
                                         "Matches on the following fields:\n- SCHEMA\n- DNS")
            @QueryParam("matchTerm") String matchTerm,
            @Parameter(description = "If true, the total count of the entities matching the query will be included in the result set")
            @QueryParam("askTotalCount") boolean askTotalCount,
            @Parameter(description = "The name of the parameter that will be used as a sorting key")
            @QueryParam("sortParam") String sortParam,
            @Parameter(description = "The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive (except for \"clientId\" parameter).")
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        EndpointInfoQuery query = endpointInfoFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (!Strings.isNullOrEmpty(usage)) {
            andPredicate.and(query.attributePredicate(EndpointInfoAttributes.USAGES, endpointInfoFactory.newEndpointUsage(usage)));
        }
        if (matchTerm != null && !matchTerm.isEmpty()) {
            andPredicate.and(query.matchPredicate(matchTerm));
        }
        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }
        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);
        query.setAskTotalCount(askTotalCount);

        return query(scopeId, endpointType, query);
    }

    /**
     * Queries the results with the given {@link EndpointInfoQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link EndpointInfoQuery} to use to filter results.
     * @return The {@link EndpointInfoListResult} of all the result matching the given {@link EndpointInfoQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the EndpointInfos")
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
    public EndpointInfoListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The type of the Endpoints to query for")
            @QueryParam("endpointType") @DefaultValue(EndpointInfo.ENDPOINT_TYPE_RESOURCE) String endpointType,
            EndpointInfoQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return endpointInfoService.query(query, endpointType);
    }

    /**
     * Counts the results with the given {@link EndpointInfoQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link EndpointInfoQuery} to use to filter results.
     * @return The count of all the result matching the given {@link EndpointInfoQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the EndpointInfo")
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
            @Parameter(description = "The type of the Endpoints to query for")
            @QueryParam("endpointType") @DefaultValue(EndpointInfo.ENDPOINT_TYPE_RESOURCE) String endpointType,
            EndpointInfoQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(endpointInfoService.count(query, endpointType));
    }

    /**
     * Creates a new EndpointInfo based on the information provided in EndpointInfoCreator
     * parameter.
     *
     * @param scopeId             The {@link ScopeId} in which to create the {@link EndpointInfo}
     * @param endpointInfoCreator Provides the information for the new {@link EndpointInfo} to be created.
     * @return The newly created {@link EndpointInfo} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Create a new EndpointInfo")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The EndpointInfo that has just been created",
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
    public Response create(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            EndpointInfoCreator endpointInfoCreator) throws KapuaException {
        endpointInfoCreator.setScopeId(scopeId);

        return returnCreated(endpointInfoService.create(endpointInfoCreator));
    }

    /**
     * Returns the EndpointInfo specified by the "endpointInfoId" path parameter.
     *
     * @param scopeId        The {@link ScopeId} of the requested {@link EndpointInfo}.
     * @param endpointInfoId The id of the requested EndpointInfo.
     * @return The requested EndpointInfo object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{endpointInfoId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a single EndpointInfo")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired EndpointInfo",
            content = @Content(schema = @Schema(implementation = Device.class))
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
    public EndpointInfo find(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the EndpointInfo on which to perform the operation")
            @PathParam("endpointInfoId") EntityId endpointInfoId) throws KapuaException {
        EndpointInfo endpointInfo = endpointInfoService.find(scopeId, endpointInfoId);

        return returnNotNullEntity(endpointInfo,EndpointInfo.TYPE, endpointInfoId);
    }

    /**
     * Updates the EndpointInfo based on the information provided in the EndpointInfo parameter.
     *
     * @param scopeId        The ScopeId of the requested {@link EndpointInfo}.
     * @param endpointInfoId The id of the requested {@link EndpointInfo}
     * @param endpointInfo   The modified EndpointInfo whose attributed need to be updated.
     * @return The updated endpointInfo.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @PUT
    @Path("{endpointInfoId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Update a single EndpointInfo")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the updated EndpointInfo",
            content = @Content(schema = @Schema(implementation = Device.class))
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
    public EndpointInfo update(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the EndpointInfo on which to perform the operation")
            @PathParam("endpointInfoId") EntityId endpointInfoId,
            EndpointInfo endpointInfo) throws KapuaException {
        endpointInfo.setScopeId(scopeId);
        endpointInfo.setId(endpointInfoId);

        return endpointInfoService.update(endpointInfo);
    }

    /**
     * Deletes the EndpointInfo specified by the "endpointInfoId" path parameter.
     *
     * @param scopeId        The ScopeId of the requested {@link EndpointInfo}.
     * @param endpointInfoId The id of the EndpointInfo to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @DELETE
    @Path("{endpointInfoId}")
    @Operation(summary = "Delete a single EndpointInfo")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The EndpointInfo has been deleted"
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
    public Response deleteEndpointInfo(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the EndpointInfo on which to perform the operation")
            @PathParam("endpointInfoId") EntityId endpointInfoId) throws KapuaException {
        endpointInfoService.delete(scopeId, endpointInfoId);

        return returnNoContent();
    }
}
