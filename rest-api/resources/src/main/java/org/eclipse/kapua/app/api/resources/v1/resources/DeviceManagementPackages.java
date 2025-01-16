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
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.app.api.core.settings.KapuaApiCoreSetting;
import org.eclipse.kapua.app.api.core.settings.KapuaApiCoreSettingKeys;
import org.eclipse.kapua.commons.rest.model.errors.DeviceNotConnectedExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.access.AccessInfoQuery;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/devices/{deviceId}/packages")
@Tag(name = "Get the Packages installed on a single Device")
public class DeviceManagementPackages extends AbstractKapuaResource {

    private final Boolean responseLegacyMode = KapuaLocator.getInstance().getComponent(KapuaApiCoreSetting.class).getBoolean(KapuaApiCoreSettingKeys.API_DEVICE_MANAGEMENT_PACKAGE_RESPONSE_LEGACY_MODE, false);

    @Inject
    public DevicePackageManagementService devicePackageManagementService;
    @Inject
    public DevicePackageFactory devicePackageFactory;
    @Inject
    public DeviceManagementOperationRegistryService deviceManagementOperationRegistryService;

    /**
     * Returns the list of all the packages installed on the device.
     *
     * @param scopeId  The {@link ScopeId} in which to search results.
     * @param deviceId The id of the device
     * @param timeout  The timeout of the operation
     * @return The list of packages installed.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "The ID of the Scope where to perform the operation.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of Packages installed on a single Device",
            content = @Content(schema = @Schema(implementation = DevicePackages.class))
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public DevicePackages get(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        return devicePackageManagementService.getInstalled(scopeId, deviceId, timeout);
    }

    /**
     * Download and optionally installs a package into the device.
     *
     * @param scopeId                The {@link ScopeId} in which to search results.
     * @param deviceId               The {@link Device} ID.
     * @param timeout                The timeout of the operation
     * @param packageDownloadRequest Mandatory object with all the informations needed to download and install a package
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_download")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Download and install a Package")
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = AccessInfoQuery.class),
            examples = {
                @ExampleObject(
                    name = "Basic",
                    description = "A request with basic options",
                    value = "{\"uri\": \"https://download.eclipse.org/kura/releases/4.1.0/org.eclipse.kura.demo.heater_1.0.500.dp\",\"name\": \"heater\",\"version\": \"1.0.500\"}"
                ),
                @ExampleObject(
                    name = "Complete",
                    description = "A request with all available options used",
                    value = "{\n" +
                                "  \"uri\": \"https://download.eclipse.org/kura/releases/4.1.0/org.eclipse.kura.demo.heater_1.0.500.dp\",\n" +
                                "  \"name\": \"heater\",\n" + "  \"version\": \"1.0.500\",\n" +
                                "  \"username\": \"username\",\n" + "  \"password\": \"password\",\n" +
                                "  \"fileHash\": \"MD5:0d04154164145cd6b2167fdd457ed28f\",\n" +
                                "  \"fileType\": \"DEPLOYMENT_PACKAGE\",\n" + "  \"install\": true,\n" +
                                "  \"reboot\": false,\n" + "  \"rebootDelay\": 0,\n" + "  \"advancedOptions\": {\n" +
                                "    \"restart\": false,\n" + "    \"blockSize\": 128,\n" + "    \"blockDelay\": 0,\n" +
                                "    \"blockTimeout\": 5000,\n" + "    \"notifyBlockSize\": 256,\n" +
                                "    \"installVerifyURI\": \"https://download.eclipse.org/kura/releases/4.1.0/org.eclipse.kura.demo.heater_1.0.500.verifier.sh\"\n" +
                                "  }\n" + "}"
                )
            }
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The corresponding Device Management Operation to track the progress of the Device Package Download Request",
            content = @Content(schema = @Schema(implementation = DeviceManagementOperation.class))
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
            description = "The desired entity could not be found",
            content = @Content(schema = @Schema(implementation = EntityNotFoundExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
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
    public Response download(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            @Parameter(description = "")
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
     * @param scopeId                 The {@link ScopeId} in which to search results.
     * @param deviceId                The {@link Device} ID.
     * @param timeout                 The timeout of the operation
     * @param packageUninstallRequest Mandatory object with all the informations needed to uninstall a package
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_uninstall")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Uninstall a Package")
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = AccessInfoQuery.class),
            examples = {
                @ExampleObject(
                    name = "Basic",
                    description = "A request with only required properties",
                    value = "{\"name\": \"org.eclipse.kura.demo.heater\",\"version\": \"1.0.500\"}"
                ),
                @ExampleObject(
                    name = "Complete",
                    description = "A request with all properties",
                    value = "{\"name\": \"org.eclipse.kura.demo.heater\",\"version\": \"1.0.500\",\"reboot\": false,\"rebootDelay\": 0}"
                )
            }
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The corresponding Device Management Operation to track the progress of the Device Package Uninstall Request",
            content = @Content(schema = @Schema(implementation = DeviceManagementOperation.class))
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
            description = "The desired entity could not be found",
            content = @Content(schema = @Schema(implementation = EntityNotFoundExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
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
    public Response uninstall(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            @Parameter(description = "")
            @QueryParam("legacy") @DefaultValue("false") boolean legacy,
            DevicePackageUninstallRequest packageUninstallRequest) throws KapuaException {
        DevicePackageUninstallOptions options = devicePackageFactory.newPackageUninstallOptions();
        options.setTimeout(timeout);

        KapuaId deviceManagementOperationId = devicePackageManagementService.uninstallExec(scopeId, deviceId, packageUninstallRequest, options);

        DeviceManagementOperation deviceManagementOperation = deviceManagementOperationRegistryService.find(scopeId, deviceManagementOperationId);

        return responseLegacyMode || legacy ? returnNoContent() : returnOk(deviceManagementOperation);
    }

}
