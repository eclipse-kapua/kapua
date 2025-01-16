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
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.DeviceNotConnectedExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshotManagementService;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshots;
import org.eclipse.kapua.service.device.registry.Device;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/devices/{deviceId}/snapshots")
@Tag(name = "Device Management - Snapshot")
public class DeviceManagementSnapshots extends AbstractKapuaResource {

    @Inject
    public DeviceSnapshotManagementService snapshotService;
    @Inject
    public DeviceConfigurationManagementService configurationService;

    /**
     * Returns the list of all the Snapshots available on the device.
     *
     * @param scopeId  The {@link ScopeId} of the {@link Device}.
     * @param deviceId The id of the device
     * @param timeout  The timeout of the operation
     * @return The list of Snapshot Ids.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all Snapshots")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of Snapshots available on a single Device",
            content = @Content(schema = @Schema(implementation = DeviceSnapshots.class))
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
            responseCode = "409",
            description = "A conflict in the request - the device is disconnected and the request cannot be accomplished",
            content = @Content(schema = @Schema(implementation = DeviceNotConnectedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public DeviceSnapshots get(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        return snapshotService.get(scopeId, deviceId, timeout);
    }

    /**
     * Updates the configuration of a device rolling back a given snapshot ID.
     *
     * @param scopeId    The {@link ScopeId} of the {@link Device}.
     * @param deviceId   The {@link Device} ID.
     * @param snapshotId the ID of the snapshot to rollback to.
     * @param timeout    The timeout of the operation
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("{snapshotId}/_rollback")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Get a single Snapshot")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Configurations related to the given Snapshot on a single Device",
            content = @Content(schema = @Schema(implementation = DeviceConfiguration.class))
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
            responseCode = "409",
            description = "A conflict in the request - the device is disconnected and the request cannot be accomplished",
            content = @Content(schema = @Schema(implementation = DeviceNotConnectedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Response rollback(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The ID of the Snapshot on which to perform the operation")
            @PathParam("snapshotId") String snapshotId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        snapshotService.rollback(scopeId, deviceId, snapshotId, timeout);

        return returnNoContent();
    }

    /**
     * Gets the configuration of a device given the snapshot ID.
     *
     * @param scopeId    The {@link ScopeId} of the {@link Device}.
     * @param deviceId   The {@link Device} ID.
     * @param snapshotId the ID of the snapshot to rollback to.
     * @param timeout    The timeout of the operation
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{snapshotId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Rollback to Snapshot")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The Snapshot has been applied"
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
            responseCode = "409",
            description = "A conflict in the request - the device is disconnected and the request cannot be accomplished",
            content = @Content(schema = @Schema(implementation = DeviceNotConnectedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public DeviceConfiguration download(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The ID of the Snapshot on which to perform the operation")
            @PathParam("snapshotId") String snapshotId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        return configurationService.get(scopeId, deviceId, snapshotId, null, timeout);
    }

}
