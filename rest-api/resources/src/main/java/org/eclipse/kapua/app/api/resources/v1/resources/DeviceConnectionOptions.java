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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOption;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/deviceconnections/{connectionId}/options")
@Tag(name = "Device Connection")
public class DeviceConnectionOptions extends AbstractKapuaResource {

    @Inject
    public DeviceConnectionOptionService deviceConnectionOptionsService;

    /**
     * Returns the {@link DeviceConnectionOption} specified by the given parameters.
     *
     * @param scopeId      The {@link ScopeId} of the requested {@link DeviceConnectionOption}.
     * @param connectionId The {@link DeviceConnectionOption} id of the request
     *                     {@link DeviceConnectionOption}.
     * @return The requested {@link DeviceConnectionOption} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get the option for a Connection")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The Options of the desired Connection",
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
    public DeviceConnectionOption find(
        @Parameter(description = "The ID of the Scope where to perform the operation.")
        @PathParam("scopeId") ScopeId scopeId,
        @Parameter(description = "The ID of the Connection on which to perform the operation")
        @PathParam("connectionId") EntityId connectionId) throws KapuaException {
        DeviceConnectionOption deviceConnectionOptions = deviceConnectionOptionsService.find(scopeId, connectionId);

        return returnNotNullEntity(deviceConnectionOptions, DeviceConnectionOption.TYPE, connectionId);
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
    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Update the option for a Connection")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The updated Options of the desired Connection",
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
    public DeviceConnectionOption update(
        @Parameter(description = "The ID of the Scope where to perform the operation.")
        @PathParam("scopeId") ScopeId scopeId,
        @Parameter(description = "The ID of the Connection on which to perform the operation")
        @PathParam("connectionId") EntityId deviceConnectionId,
        DeviceConnectionOption deviceConnectionOptions)
            throws KapuaException {

        deviceConnectionOptions.setScopeId(scopeId);
        deviceConnectionOptions.setId(deviceConnectionId);

        return deviceConnectionOptionsService.update(deviceConnectionOptions);
    }

}
