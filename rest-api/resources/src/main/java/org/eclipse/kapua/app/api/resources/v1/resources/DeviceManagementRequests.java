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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.app.api.resources.v1.resources;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.web.rest.model.EntityId;
import org.eclipse.kapua.commons.web.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.request.DeviceRequestManagementService;
import org.eclipse.kapua.service.device.management.request.message.request.GenericRequestMessage;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseMessage;
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

@Path("{scopeId}/devices/{deviceId}/requests")
public class DeviceManagementRequests extends AbstractKapuaResource {

    @Inject
    public DeviceRequestManagementService requestService;

    /**
     * Sends a request message to a device. This call is generally used to perform remote management of resources attached to the device such sensors and registries.
     *
     * @param scopeId
     *         The {@link ScopeId} of the {@link Device}.
     * @param deviceId
     *         The {@link Device} ID.
     * @param timeout
     *         The timeout of the request execution
     * @param requestMessage
     *         The input request
     * @return The response output.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_XML })
    public GenericResponseMessage sendRequest(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            GenericRequestMessage requestMessage) throws KapuaException {
        requestMessage.setScopeId(scopeId);
        requestMessage.setDeviceId(deviceId);

        return requestService.exec(scopeId, deviceId, requestMessage, timeout);
    }
}
