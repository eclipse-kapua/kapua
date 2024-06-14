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

import com.google.common.base.Strings;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.web.rest.model.EntityId;
import org.eclipse.kapua.commons.web.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.registry.Device;

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

@Path("{scopeId}/devices/{deviceId}/bundles")
public class DeviceManagementBundles extends AbstractKapuaResource {

    @Inject
    public DeviceBundleManagementService bundleService;

    /**
     * Returns the list of all the Bundles installed on the device.
     *
     * @param scopeId
     *         The {@link ScopeId} of the {@link Device}.
     * @param deviceId
     *         The id of the device
     * @param sortParam
     *         The name of the parameter that will be used as a sorting key
     * @param sortDir
     *         The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param timeout
     *         The timeout of the operation in milliseconds
     * @return The list of Bundles
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceBundles get(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
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
     * @param scopeId
     *         The {@link ScopeId} of the {@link Device}.
     * @param deviceId
     *         The {@link Device} ID.
     * @param bundleId
     *         the ID of the bundle to start
     * @param timeout
     *         The timeout of the operation in milliseconds
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("{bundleId}/_start")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response start(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @PathParam("bundleId") String bundleId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        bundleService.start(scopeId, deviceId, bundleId, timeout);

        return returnNoContent();
    }

    /**
     * Stops the bundle
     *
     * @param deviceId
     *         The {@link Device} ID.
     * @param bundleId
     *         the ID of the bundle to stop
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("{bundleId}/_stop")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response stop(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @PathParam("bundleId") String bundleId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        bundleService.stop(scopeId, deviceId, bundleId, timeout);

        return returnNoContent();
    }
}
