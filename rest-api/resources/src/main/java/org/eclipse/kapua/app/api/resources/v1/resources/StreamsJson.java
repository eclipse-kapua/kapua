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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.model.data.JsonKapuaDataMessage;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.app.api.resources.v1.resources.marker.JsonSerializationFixed;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataMessageFactory;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.stream.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see JsonSerializationFixed
 */
@Path("{scopeId}/streams")
@Tag(name = "Stream", description = "Publishes a fire-and-forget message to a topic composed of [account-name] / [client-id] / [semtantic-parts]")
public class StreamsJson extends AbstractKapuaResource implements JsonSerializationFixed {

    @Inject
    public KapuaDataMessageFactory kapuaDataMessageFactory;
    @Inject
    public StreamService streamService;

    /**
     * Publishes a fire-and-forget message to a topic composed of:
     * [account-name] / [client-id] / [semtantic-parts]
     * In such a schema, the parts are defined as follows:
     * <ul>
     * <li>account-name: the name of the current scope</li>
     * <li>client-id: from the "clientId" property in the body</li>
     * <li>semantic-parts: array of strings in the "channel" property in the body</li>
     * </ul>
     * For example, the following JSON body will publish on the &quot;kapua-sys/AA:BB:CC:DD:EE:FF/one/two/three&quot; topic:
     * <pre>
     * {
     *   "type": "kapuaDataMessage",
     *   "position": {
     *   "type": "kapuaPosition",
     *     "latitude": 0,
     *     "longitude": 0
     *   },
     *   "clientId": "AA:BB:CC:DD:EE:FF",
     *   "channel": {
     *     "type": "kapuaDataChannel",
     *     "semanticParts": ["one", "two", "three"]
     *   },
     *   "payload": {
     *     "type": "kapuaDataPayload",
     *     "metrics": [
     *         {
     *           "valueType": "string",
     *           "value": "aaa",
     *           "name": "metric-1"
     *         },
     *         {
     *           "valueType": "string",
     *           "value": "bbb",
     *           "name": "metric-2"
     *         }
     *     ]
     *   }
     * }
     * </pre>
     *
     * @param scopeId
     * @param timeout
     * @param jsonKapuaDataMessage
     * @return
     * @throws KapuaException
     */
    @POST
    @Path("messages")
    @Consumes({MediaType.APPLICATION_JSON})
    @Operation(summary = "Update a single Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The message has been sent successfully",
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Response publish(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            JsonKapuaDataMessage jsonKapuaDataMessage) throws KapuaException {

        KapuaDataMessage kapuaDataMessage = kapuaDataMessageFactory.newKapuaDataMessage();

        kapuaDataMessage.setId(jsonKapuaDataMessage.getId());
        kapuaDataMessage.setScopeId(scopeId);
        kapuaDataMessage.setDeviceId(jsonKapuaDataMessage.getDeviceId());
        kapuaDataMessage.setClientId(jsonKapuaDataMessage.getClientId());
        kapuaDataMessage.setReceivedOn(jsonKapuaDataMessage.getReceivedOn());
        kapuaDataMessage.setSentOn(jsonKapuaDataMessage.getSentOn());
        kapuaDataMessage.setCapturedOn(jsonKapuaDataMessage.getCapturedOn());
        kapuaDataMessage.setPosition(jsonKapuaDataMessage.getPosition());
        kapuaDataMessage.setChannel(jsonKapuaDataMessage.getChannel());

        KapuaDataPayload kapuaDataPayload = kapuaDataMessageFactory.newKapuaDataPayload();

        if (jsonKapuaDataMessage.getPayload() != null) {
            kapuaDataPayload.setBody(jsonKapuaDataMessage.getPayload().getBody());

            jsonKapuaDataMessage.getPayload().getMetrics().forEach(
                    jsonMetric -> {
                        String name = jsonMetric.getName();
                        Object value = ObjectValueConverter.fromString(jsonMetric.getValue(), jsonMetric.getValueType());

                        kapuaDataPayload.getMetrics().put(name, value);
                    });
        }

        kapuaDataMessage.setPayload(kapuaDataPayload);

        kapuaDataMessage.setScopeId(scopeId);
        streamService.publish(kapuaDataMessage, timeout);
        return returnNoContent();
    }
}
