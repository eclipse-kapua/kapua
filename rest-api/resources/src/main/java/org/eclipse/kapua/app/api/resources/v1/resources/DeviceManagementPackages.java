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
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.app.api.core.settings.KapuaApiCoreSetting;
import org.eclipse.kapua.app.api.core.settings.KapuaApiCoreSettingKeys;
import org.eclipse.kapua.commons.rest.model.EntityId;
import org.eclipse.kapua.commons.rest.model.ScopeId;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.packages.DevicePackageFactory;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOptions;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallOptions;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallRequest;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRegistryService;
import org.eclipse.kapua.service.device.registry.Device;

@Path("{scopeId}/devices/{deviceId}/packages")
public class DeviceManagementPackages extends AbstractKapuaResource {

    private final Boolean responseLegacyMode = KapuaLocator.getInstance().getComponent(KapuaApiCoreSetting.class)
            .getBoolean(KapuaApiCoreSettingKeys.API_DEVICE_MANAGEMENT_PACKAGE_RESPONSE_LEGACY_MODE, false);

    @Inject
    public DevicePackageManagementService devicePackageManagementService;
    @Inject
    public DevicePackageFactory devicePackageFactory;
    @Inject
    public DeviceManagementOperationRegistryService deviceManagementOperationRegistryService;

    /**
     * Returns the list of all the packages installed on the device.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param deviceId
     *         The id of the device
     * @param timeout
     *         The timeout of the operation
     * @return The list of packages installed.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DevicePackages get(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        return devicePackageManagementService.getInstalled(scopeId, deviceId, timeout);
    }

    /**
     * Download and optionally installs a package into the device.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param deviceId
     *         The {@link Device} ID.
     * @param timeout
     *         The timeout of the operation
     * @param packageDownloadRequest
     *         Mandatory object with all the informations needed to download and install a package
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_download")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response download(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            @QueryParam("legacy") @DefaultValue("false") boolean legacy,
            DevicePackageDownloadRequest packageDownloadRequest)
            throws KapuaException {
        DevicePackageDownloadOptions options = devicePackageFactory.newPackageDownloadOptions();
        options.setTimeout(timeout);

        KapuaId deviceManagementOperationId = devicePackageManagementService.downloadExec(scopeId, deviceId, packageDownloadRequest, options);

        DeviceManagementOperation deviceManagementOperation = deviceManagementOperationRegistryService.find(scopeId, deviceManagementOperationId);

        return responseLegacyMode || legacy ? returnNoContent() : returnOk(deviceManagementOperation);
    }

    /**
     * Uninstalls a package into the device.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param deviceId
     *         The {@link Device} ID.
     * @param timeout
     *         The timeout of the operation
     * @param packageUninstallRequest
     *         Mandatory object with all the informations needed to uninstall a package
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_uninstall")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response uninstall(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            @QueryParam("legacy") @DefaultValue("false") boolean legacy,
            DevicePackageUninstallRequest packageUninstallRequest) throws KapuaException {
        DevicePackageUninstallOptions options = devicePackageFactory.newPackageUninstallOptions();
        options.setTimeout(timeout);

        KapuaId deviceManagementOperationId = devicePackageManagementService.uninstallExec(scopeId, deviceId, packageUninstallRequest, options);

        DeviceManagementOperation deviceManagementOperation = deviceManagementOperationRegistryService.find(scopeId, deviceManagementOperationId);

        return responseLegacyMode || legacy ? returnNoContent() : returnOk(deviceManagementOperation);
    }

}
