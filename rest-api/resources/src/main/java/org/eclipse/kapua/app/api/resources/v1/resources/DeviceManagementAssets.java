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
import javax.ws.rs.PUT;
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
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannel;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetManagementService;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;
import org.eclipse.kapua.service.device.management.asset.store.settings.DeviceAssetStoreSettings;
import org.eclipse.kapua.service.device.registry.Device;

@Path("{scopeId}/devices/{deviceId}/assets")
public class DeviceManagementAssets extends AbstractKapuaResource {

    @Inject
    public DeviceAssetManagementService deviceManagementAssetService;
    @Inject
    public DeviceAssetStoreService deviceAssetStoreService;

    /**
     * Returns the list of all the Assets configured on the device.
     *
     * @param scopeId
     *         The {@link ScopeId} of the {@link Device}.
     * @param deviceId
     *         The id of the device
     * @param timeout
     *         The timeout of the operation in milliseconds
     * @return The list of Assets
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceAssets get(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        return get(scopeId, deviceId, timeout, new DeviceAssets());
    }

    /**
     * Returns the list of all the Assets configured on the device filtered by the {@link DeviceAssets} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the {@link Device}.
     * @param deviceId
     *         The id of the device
     * @param timeout
     *         The timeout of the operation in milliseconds
     * @return The list of Assets
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceAssets get(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceAssets deviceAssetFilter) throws KapuaException {
        return deviceManagementAssetService.get(scopeId, deviceId, deviceAssetFilter, timeout);
    }

    /**
     * Reads {@link DeviceAssetChannel}s values available on the device filtered by the {@link DeviceAssets} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the {@link Device}.
     * @param deviceId
     *         The id of the device
     * @param timeout
     *         The timeout of the operation in milliseconds
     * @return The list of Assets
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_read")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceAssets read(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceAssets deviceAssetFilter) throws KapuaException {
        return deviceManagementAssetService.read(scopeId, deviceId, deviceAssetFilter, timeout);
    }

    /**
     * Writes {@link DeviceAssetChannel}s configured on the device filtered by the {@link DeviceAssets} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the {@link Device}.
     * @param deviceId
     *         The id of the device
     * @param timeout
     *         The timeout of the operation in milliseconds
     * @return The list of Assets
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_write")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceAssets write(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceAssets deviceAssetFilter) throws KapuaException {
        return deviceManagementAssetService.write(scopeId, deviceId, deviceAssetFilter, timeout);
    }

    @GET
    @Path("_settings")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceAssetStoreSettings getSettings(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId)
            throws KapuaException {
        return deviceAssetStoreService.getApplicationSettings(scopeId, deviceId);
    }

    @PUT
    @Path("_settings")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response postSettings(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            DeviceAssetStoreSettings deviceAssetStoreSettings) throws KapuaException {
        deviceAssetStoreService.setApplicationSettings(scopeId, deviceId, deviceAssetStoreSettings);

        return returnNoContent();
    }

}
