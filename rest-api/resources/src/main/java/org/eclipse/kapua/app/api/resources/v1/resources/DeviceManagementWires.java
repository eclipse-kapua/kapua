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
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.wire.DeviceWiresManagementService;
import org.eclipse.kapua.service.device.registry.Device;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{scopeId}/devices/{deviceId}/graph")
public class DeviceManagementWires extends AbstractKapuaResource {

    @Inject
    public DeviceWiresManagementService wiresManagementService;

    /**
     * Returns the wire graph of the device.
     *
     * @param scopeId  The {@link ScopeId} of the {@link Device}.
     * @param deviceId The id of the device
     * @param timeout  The timeout of the operation in milliseconds
     * @return The requested configurations
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.1.0
     */
    @GET
    @Path("snapshot")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DeviceConfiguration get(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        return wiresManagementService.get(scopeId, deviceId, timeout);
    }

    /**
     * Updates the wire grap configuration of a {@link Device}
     *
     * @param scopeId             The {@link ScopeId} of the {@link Device}.
     * @param deviceId            The id of the device
     * @param timeout             The timeout of the operation in milliseconds
     * @param wireGraphConfiguration The configuration to send to the {@link Device}
     * @return The {@link Response} of the operation
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.1.0
     */
    @PUT
    @Path("snapshot")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceConfiguration wireGraphConfiguration) throws KapuaException {
        wiresManagementService.put(scopeId, deviceId, wireGraphConfiguration, timeout);
        return returnNoContent();
    }

    /**
     * Deletes the wire graph configuration of a {@link Device}
     *
     * @param scopeId             The {@link ScopeId} of the {@link Device}.
     * @param deviceId            The id of the device
     * @param timeout             The timeout of the operation in milliseconds
     * @return The {@link Response} of the operation
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.1.0
     */
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response delete(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        wiresManagementService.del(scopeId, deviceId, timeout);
        return returnNoContent();
    }







}
