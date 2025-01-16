/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.model.device.management.JsonGenericRequestMessage;
import org.eclipse.kapua.app.api.core.model.device.management.JsonGenericResponseMessage;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.app.api.resources.v1.resources.marker.JsonSerializationFixed;
import org.eclipse.kapua.commons.rest.model.errors.DeviceNotConnectedExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.request.DeviceRequestManagementService;
import org.eclipse.kapua.service.device.management.request.GenericRequestFactory;
import org.eclipse.kapua.service.device.management.request.message.request.GenericRequestMessage;
import org.eclipse.kapua.service.device.management.request.message.request.GenericRequestPayload;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseMessage;
import org.eclipse.kapua.service.device.registry.Device;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see JsonSerializationFixed
 */
@Path("{scopeId}/devices/{deviceId}/requests")
@Tag(name = "Device Management - Request")
public class DeviceManagementRequestsJson extends AbstractKapuaResource implements JsonSerializationFixed {

    @Inject
    public GenericRequestFactory genericRequestFactory;
    @Inject
    public DeviceRequestManagementService requestService;

    /**
     * Sends a request message to a device.
     * This call is generally used to perform remote management of resources
     * attached to the device such sensors and registries.
     *
     * @param scopeId                   The {@link ScopeId} of the {@link Device}.
     * @param deviceId                  The {@link Device} ID.
     * @param timeout                   The timeout of the request execution
     * @param jsonGenericRequestMessage The input request
     * @return The response output.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Execute a Command")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of Requests installed on a single Device"
            //content = @Content(schema = @Schema(implementation = RequestInp.class))
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
    public JsonGenericResponseMessage sendRequest(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            JsonGenericRequestMessage jsonGenericRequestMessage) throws KapuaException {

        GenericRequestMessage genericRequestMessage = genericRequestFactory.newRequestMessage();

        genericRequestMessage.setId(jsonGenericRequestMessage.getId());
        genericRequestMessage.setScopeId(scopeId);
        genericRequestMessage.setDeviceId(jsonGenericRequestMessage.getDeviceId());
        genericRequestMessage.setClientId(jsonGenericRequestMessage.getClientId());
        genericRequestMessage.setReceivedOn(jsonGenericRequestMessage.getReceivedOn());
        genericRequestMessage.setSentOn(jsonGenericRequestMessage.getSentOn());
        genericRequestMessage.setCapturedOn(jsonGenericRequestMessage.getCapturedOn());
        genericRequestMessage.setPosition(jsonGenericRequestMessage.getPosition());
        genericRequestMessage.setChannel(jsonGenericRequestMessage.getChannel());

        GenericRequestPayload kapuaDataPayload = genericRequestFactory.newRequestPayload();

        if (jsonGenericRequestMessage.getPayload() != null) {
            kapuaDataPayload.setBody(jsonGenericRequestMessage.getPayload().getBody());

            jsonGenericRequestMessage.getPayload().getMetrics().forEach(
                    jsonMetric -> {
                        String name = jsonMetric.getName();
                        Object value = ObjectValueConverter.fromString(jsonMetric.getValue(), jsonMetric.getValueType());

                        kapuaDataPayload.getMetrics().put(name, value);
                    });
        }

        genericRequestMessage.setPayload(kapuaDataPayload);
        genericRequestMessage.setScopeId(scopeId);
        genericRequestMessage.setDeviceId(deviceId);
        GenericResponseMessage genericResponseMessage = requestService.exec(scopeId, deviceId, genericRequestMessage, timeout);
        return new JsonGenericResponseMessage(genericResponseMessage);
    }
}
