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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.registry.Device;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/devices/{deviceId}/commands")
@Tag(name = "Device Management - Command")
public class DeviceManagementCommands extends AbstractKapuaResource {

    @Inject
    public DeviceCommandManagementService commandService;

    /**
     * Executes a remote command on a device and return the command output.
     * <p>
     * <p>
     * Example to list all files in the current working directory:
     * <p>
     *
     * <pre>
     * Client client = client();
     * WebResource apisWeb = client.resource(APIS_TEST_URL);
     * WebResource.Builder deviceCommandWebXml = apisWeb.path(&quot;devices&quot;)
     *         .path(s_clientId)
     *         .path(&quot;command&quot;)
     *         .accept(MediaType.APPLICATION_XML)
     *         .type(MediaType.APPLICATION_XML);
     *
     * DeviceCommandInput commandInput = new DeviceCommandInput();
     * commandInput.setCommand(&quot;ls&quot;);
     * commandInput.setArguments(new String[] { &quot;-l&quot;, &quot;-a&quot; });
     *
     * DeviceCommandOutput commandOutput = deviceCommandWebXml.post(DeviceCommandOutput.class, commandInput);
     * </pre>
     *
     * @param scopeId      The {@link ScopeId} of the {@link Device}.
     * @param deviceId     The {@link Device} ID.
     * @param timeout      The timeout of the command execution
     * @param commandInput The input command
     * @return The command output.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_execute")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Execute a Command")
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = UsernamePasswordCredentials.class),
            examples = {
                @ExampleObject(
                    name = "Single Argument",
                    description = "A command with a single arguments",
                    value = "{\"command\": \"ls\",\"arguments\": {\"argument\": \"-lisa\"},\"timeout\": 60000}"
                ),
                @ExampleObject(
                    name = "Multiple Argument",
                    description = "A command with multiple arguments",
                    value = "{\"command\": \"ls\",\"arguments\": {\"argument\": [\"-l\",\"-i\",\"-s\",\"-a\"]},\"timeout\": 60000}"
                ),
            }
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The output of the Command executed on a single Device",
            content = @Content(schema = @Schema(implementation = AccessInfoListResult.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = DeviceCommandOutput.class))
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
    public DeviceCommandOutput sendCommand(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceCommandInput commandInput) throws KapuaException {
        return commandService.exec(scopeId, deviceId, commandInput, timeout);
    }
}
