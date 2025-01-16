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
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.model.SetResult;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionAttributes;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionListResult;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOption;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/deviceconnections")
@Tag(name = "Device Connection")
public class DeviceConnections extends AbstractKapuaResource {

    @Inject
    public DeviceConnectionFactory deviceConnectionFactory;
    @Inject
    public DeviceConnectionService deviceConnectionService;

    /**
     * Gets the {@link DeviceConnection} list in the scope.
     *
     * @param scopeId  The {@link ScopeId} in which to search results.
     * @param clientId The id of the {@link Device} in which to search results
     * @param status   The {@link DeviceConnectionStatus} in which to search results
     * @param offset   The result set offset.
     * @param limit    The result set limit.
     * @return The {@link DeviceConnectionListResult} of all the deviceConnections associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "The list of the Connections available in the Scope")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the AccessInfo objects available in the Scope",
            content = @Content(schema = @Schema(implementation = DeviceConnectionListResult.class))
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
    public DeviceConnectionListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The client id to filter results")
            @QueryParam("clientId") String clientId,
            @Parameter(description = "The client ip to filter results")
            @QueryParam("clientIp") String clientIp,
            @Parameter(description = "The connection protocol to filter results")
            @QueryParam("protocol") String protocol,
            @Parameter(description = "The connection status to filter results")
            @QueryParam("status") DeviceConnectionStatus status,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        DeviceConnectionQuery query = deviceConnectionFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (!Strings.isNullOrEmpty(clientId)) {
            andPredicate.and(query.attributePredicate(DeviceConnectionAttributes.CLIENT_ID, clientId));
        }
        if (!Strings.isNullOrEmpty(clientIp)) {
            andPredicate.and(query.attributePredicate(DeviceConnectionAttributes.CLIENT_IP, clientIp));
        }
        if (!Strings.isNullOrEmpty(protocol)) {
            andPredicate.and(query.attributePredicate(DeviceConnectionAttributes.PROTOCOL, protocol));
        }
        if (status != null) {
            andPredicate.and(query.attributePredicate(DeviceConnectionAttributes.STATUS, status));
        }
        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link DeviceConnectionQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link DeviceConnectionQuery} to use to filter results.
     * @return The {@link DeviceConnectionListResult} of all the result matching the given {@link DeviceConnectionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the Connections")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
            content = @Content(schema = @Schema(implementation = DeviceConnectionListResult.class))
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
    public DeviceConnectionListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            DeviceConnectionQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return deviceConnectionService.query(query);
    }

    /**
     * Counts the results with the given {@link DeviceConnectionQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link DeviceConnectionQuery} to use to filter results.
     * @return The count of all the result matching the given {@link DeviceConnectionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Connections")
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
            DeviceConnectionQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(deviceConnectionService.count(query));
    }

    /**
     * Returns the DeviceConnection specified by the "deviceConnectionId" path parameter.
     *
     * @param scopeId            The {@link ScopeId} of the requested {@link DeviceConnection}.
     * @param deviceConnectionId The id of the requested DeviceConnection.
     * @return The requested DeviceConnection object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{deviceConnectionId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a single Connection")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired AccessInfo",
            content = @Content(schema = @Schema(implementation = DeviceConnection.class))
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
    public DeviceConnection find(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Connection on which to perform the operation")
            @PathParam("deviceConnectionId") EntityId deviceConnectionId) throws KapuaException {
        DeviceConnection deviceConnection = deviceConnectionService.find(scopeId, deviceConnectionId);

        return returnNotNullEntity(deviceConnection, DeviceConnection.TYPE, deviceConnectionId);
    }

    @GET
    @Path("_availableAuth")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all available Device Connection authentication adapters")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of available Device Connection authentication adapters",
            content = @Content(schema = @Schema(implementation = SetResult.class, example = "{\"items\": [\"USER_PASS\"]}"))
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
    public SetResult getAvailableAuthAdapter() {
        return new SetResult(deviceConnectionService.getAvailableAuthTypes());
    }

    /**
     * Request that the DeviceConnection specified by the "deviceConnectionId" is disconnected from the broker.
     *
     * @param scopeId            The {@link ScopeId} of the requested {@link DeviceConnection}.
     * @param deviceConnectionId The id of the requested DeviceConnection.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.0.0
     */
    @POST
    @Path("{deviceConnectionId}/_disconnect")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Request that the specified Connection is disconnected from the broker")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The disconnection request was sent to the broker",
            content = @Content(schema = @Schema(implementation = DeviceConnectionOption.class))
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
    public Response disconnect(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Connection on which to perform the operation")
            @PathParam("deviceConnectionId") EntityId deviceConnectionId) throws KapuaException {
        deviceConnectionService.disconnect(scopeId, deviceConnectionId);
        return returnNoContent();
    }
}
