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
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.registry.Device;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/devices/{deviceId}/bundles")
@Tag(name = "Device Management - Bundle")
public class DeviceManagementBundles extends AbstractKapuaResource {

    @Inject
    public DeviceBundleManagementService bundleService;

    /**
     * Returns the list of all the Bundles installed on the device.
     *
     * @param scopeId   The {@link ScopeId} of the {@link Device}.
     * @param deviceId  The id of the device
     * @param sortParam The name of the parameter that will be used as a sorting key
     * @param sortDir   The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param timeout   The timeout of the operation in milliseconds
     * @return The list of Bundles
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get the Bundles installed on a single Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Bundles installed on a single Device",
            content = @Content(schema = @Schema(implementation = DeviceBundles.class))
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
    public DeviceBundles get(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The name of the parameter that will be used as a sorting key")
            @QueryParam("sortParam") String sortParam,
            @Parameter(description = "The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.")
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        DeviceBundles deviceBundles = bundleService.get(scopeId, deviceId, timeout);
        if (!Strings.isNullOrEmpty(sortParam)) {
            deviceBundles.getBundles().sort((b1, b2) -> sortBundles(sortParam, sortDir, b1, b2));
        }
        return deviceBundles;
    }

    private int sortBundles(String sortParam, SortOrder sortDir, DeviceBundle b1, DeviceBundle b2) {
        switch (sortParam.toUpperCase()) {
            default:
            case "ID":
                return (sortDir == SortOrder.DESCENDING ? (int) (b2.getId() - b1.getId()) : (int) (b1.getId() - b2.getId()));
            case "NAME":
                return (sortDir == SortOrder.DESCENDING ? b2.getName().compareToIgnoreCase(b1.getName()) : b1.getName().compareToIgnoreCase(b2.getName()));
            case "STATE":
                return (sortDir == SortOrder.DESCENDING ? b2.getState().compareToIgnoreCase(b1.getState()) : b1.getState().compareToIgnoreCase(b2.getState()));
            case "VERSION":
                return (sortDir == SortOrder.DESCENDING ? b2.getVersion().compareToIgnoreCase(b1.getVersion()) : b1.getVersion().compareToIgnoreCase(b2.getVersion()));
        }
    }

    /**
     * Starts the bundle
     *
     * @param scopeId  The {@link ScopeId} of the {@link Device}.
     * @param deviceId The {@link Device} ID.
     * @param bundleId the ID of the bundle to start
     * @param timeout  The timeout of the operation in milliseconds
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("{bundleId}/_start")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Start the desired Bundle on a single Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The Bundle has been successfully started"
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
    public Response start(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The ID of the Bundle on which to perform the operation")
            @PathParam("bundleId") String bundleId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        bundleService.start(scopeId, deviceId, bundleId, timeout);

        return returnNoContent();
    }

    /**
     * Stops the bundle
     *
     * @param deviceId The {@link Device} ID.
     * @param bundleId the ID of the bundle to stop
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("{bundleId}/_stop")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "top the desired Bundle on a single Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The Bundle has been successfully stopped"
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
    public Response stop(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The ID of the Bundle on which to perform the operation")
            @PathParam("bundleId") String bundleId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        bundleService.stop(scopeId, deviceId, bundleId, timeout);

        return returnNoContent();
    }
}
