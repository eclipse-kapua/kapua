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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.rest.model.EntityId;
import org.eclipse.kapua.commons.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.registry.Device;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("{scopeId}/devices/{deviceId}/commands")
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
     * @param scopeId
     *         The {@link ScopeId} of the {@link Device}.
     * @param deviceId
     *         The {@link Device} ID.
     * @param timeout
     *         The timeout of the command execution
     * @param commandInput
     *         The input command
     * @return The command output.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_execute")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public DeviceCommandOutput sendCommand(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceCommandInput commandInput) throws KapuaException {
        return commandService.exec(scopeId, deviceId, commandInput, timeout);
    }
}
