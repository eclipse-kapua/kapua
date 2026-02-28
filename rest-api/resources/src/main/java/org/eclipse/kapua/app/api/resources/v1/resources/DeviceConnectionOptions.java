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
import org.eclipse.kapua.commons.jersey.rest.model.EntityId;
import org.eclipse.kapua.commons.jersey.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOption;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("{scopeId}/deviceconnections/{connectionId}/options")
public class DeviceConnectionOptions extends AbstractKapuaResource {

    @Inject
    public DeviceConnectionOptionService deviceConnectionOptionsService;

    /**
     * Returns the {@link DeviceConnectionOption} specified by the given parameters.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link DeviceConnectionOption}.
     * @param connectionId
     *         The {@link DeviceConnectionOption} id of the request {@link DeviceConnectionOption}.
     * @return The requested {@link DeviceConnectionOption} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceConnectionOption find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("connectionId") EntityId connectionId) throws KapuaException {
        DeviceConnectionOption deviceConnectionOptions = deviceConnectionOptionsService.find(scopeId, connectionId);

        return returnNotNullEntity(deviceConnectionOptions, DeviceConnectionOption.TYPE, connectionId);
    }

    /**
     * Returns the DeviceConnection specified by the "deviceConnectionId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link DeviceConnection}.
     * @param deviceConnectionId
     *         The id of the requested DeviceConnection.
     * @return The requested DeviceConnection object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @PUT
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceConnectionOption update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("connectionId") EntityId deviceConnectionId,
            DeviceConnectionOption deviceConnectionOptions)
            throws KapuaException {

        deviceConnectionOptions.setScopeId(scopeId);
        deviceConnectionOptions.setId(deviceConnectionId);

        return deviceConnectionOptionsService.update(deviceConnectionOptions);
    }

}
