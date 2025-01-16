/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
import javax.ws.rs.DELETE;
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
import org.eclipse.kapua.app.api.resources.v1.resources.model.device.management.keystore.DeviceKeystoreCertificateInfo;
import org.eclipse.kapua.commons.rest.model.errors.DeviceNotConnectedExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.keystore.DeviceKeystoreManagementFactory;
import org.eclipse.kapua.service.device.management.keystore.DeviceKeystoreManagementService;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSR;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSRInfo;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCertificate;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItem;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItemQuery;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItems;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreKeypair;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystores;
import org.eclipse.kapua.service.device.registry.Device;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * {@link DeviceKeystoreManagementService} {@link AbstractKapuaResource}
 *
 * @since 1.5.0
 */
@Path("{scopeId}/devices/{deviceId}/keystore")
@Tag(name = "Device Management - Keystore")
public class DeviceManagementKeystores extends AbstractKapuaResource {

    @Inject
    public DeviceKeystoreManagementService deviceKeystoreManagementService;
    @Inject
    public DeviceKeystoreManagementFactory deviceKeystoreManagementFactory;

    /**
     * Gets the {@link DeviceKeystores} present on the {@link Device}.
     *
     * @param scopeId  The {@link Device#getScopeId()}.
     * @param deviceId The {@link Device#getId()}.
     * @param timeout  The timeout of the operation in milliseconds
     * @return The {@link DeviceKeystores}.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Get the keystores list from a single Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The keystores list from the Device",
            content = @Content(schema = @Schema(implementation = DeviceKeystores.class))
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
    public DeviceKeystores getKeystores(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {
        return deviceKeystoreManagementService.getKeystores(scopeId, deviceId, timeout);
    }

    /**
     * Gets the {@link DeviceKeystoreItems} present on the {@link Device}.
     *
     * @param scopeId  The {@link Device#getScopeId()}.
     * @param deviceId The {@link Device#getId()}.
     * @param timeout  The timeout of the operation in milliseconds
     * @return The {@link DeviceKeystoreItems}.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @GET
    @Path("items")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get the keystore items from a single Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The keystore items from the Device",
            content = @Content(schema = @Schema(implementation = DeviceKeystoreItems.class))
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
    public DeviceKeystoreItems getKeystoreItems(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The keystore id to filter the results.")
            @QueryParam("keystoreId") String keystoreId,
            @Parameter(description = "The alias to filter the results.")
            @QueryParam("alias") String alias,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {

        DeviceKeystoreItemQuery itemQuery = deviceKeystoreManagementFactory.newDeviceKeystoreItemQuery();
        itemQuery.setKeystoreId(keystoreId);
        itemQuery.setAlias(alias);

        return deviceKeystoreManagementService.getKeystoreItems(scopeId, deviceId, itemQuery, timeout);
    }

    /**
     * Gets the {@link DeviceKeystoreItem} present on the {@link Device}.
     *
     * @param scopeId  The {@link Device#getScopeId()}.
     * @param deviceId The {@link Device#getId()}.
     * @param timeout  The timeout of the operation in milliseconds
     * @return The {@link DeviceKeystoreItem}.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @GET
    @Path("item")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a keystore item from a single Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The keystore item from the Device",
            content = @Content(schema = @Schema(implementation = DeviceKeystoreItem.class))
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
    public DeviceKeystoreItem getKeystoreItem(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The keystore id to filter the results.")
            @QueryParam("keystoreId") String keystoreId,
            @Parameter(description = "The alias to filter the results.")
            @QueryParam("alias") String alias,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {

        return deviceKeystoreManagementService.getKeystoreItem(scopeId, deviceId, keystoreId, alias, timeout);
    }

    /**
     * Creates a {@link DeviceKeystoreCertificate} into the {@link Device}.
     *
     * @param scopeId                 The {@link Device#getScopeId()}.
     * @param deviceId                The {@link Device#getId()}.
     * @param keystoreCertificateInfo The {@link DeviceKeystoreCertificateInfo} to create.
     * @param timeout                 The timeout of the operation in milliseconds
     * @return HTTP {@link Response#noContent()} code.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Path("items/certificateInfo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Creates a certificate from the Certificate Info Service in a Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The certificate has been created into the device keystore"
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
    public Response createDeviceKeystoreCertificate(
            @Parameter(description = "The ID of the Scope where to perform the operation")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceKeystoreCertificateInfo keystoreCertificateInfo) throws KapuaException {

        deviceKeystoreManagementService.createKeystoreCertificate(
                scopeId,
                deviceId,
                keystoreCertificateInfo.getKeystoreId(),
                keystoreCertificateInfo.getAlias(),
                keystoreCertificateInfo.getCertificateInfoId(),
                timeout);

        return returnNoContent();
    }

    /**
     * Creates a {@link DeviceKeystoreCertificate} into the {@link Device}.
     *
     * @param scopeId             The {@link Device#getScopeId()}.
     * @param deviceId            The {@link Device#getId()}.
     * @param keystoreCertificate The {@link DeviceKeystoreCertificate} to create.
     * @param timeout             The timeout of the operation in milliseconds
     * @return HTTP {@link Response#noContent()} code.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Path("items/certificateRaw")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Creates a certificate in a Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The certificate has been created into the device keystore"
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
    public Response createDeviceKeystoreCertificate(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceKeystoreCertificate keystoreCertificate) throws KapuaException {

        deviceKeystoreManagementService.createKeystoreCertificate(scopeId, deviceId, keystoreCertificate, timeout);

        return returnNoContent();
    }


    /**
     * Creates a {@link DeviceKeystoreKeypair} into the {@link Device}.
     *
     * @param scopeId               The {@link Device#getScopeId()}.
     * @param deviceId              The {@link Device#getId()}.
     * @param deviceKeystoreKeypair The {@link DeviceKeystoreKeypair} to create.
     * @param timeout               The timeout of the operation in milliseconds
     * @return HTTP {@link Response#noContent()} code.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Path("items/keypair")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Creates a key pair in a Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The keypair has been created into the device keystore"
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
    public Response createDeviceKeystoreKeypair(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceKeystoreKeypair deviceKeystoreKeypair) throws KapuaException {

        deviceKeystoreManagementService.createKeystoreKeypair(scopeId, deviceId, deviceKeystoreKeypair, timeout);

        return returnNoContent();
    }

    /**
     * Sends a {@link DeviceKeystoreCSRInfo} into the {@link Device}.
     *
     * @param scopeId               The {@link Device#getScopeId()}.
     * @param deviceId              The {@link Device#getId()}.
     * @param deviceKeystoreCSRInfo The {@link DeviceKeystoreCSRInfo} to create.
     * @param timeout               The timeout of the operation in milliseconds
     * @return The {@link DeviceKeystoreCSR}.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @POST
    @Path("items/csr")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Request a certificate signing request from a Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The certificate signing request has been returned.",
            content = @Content(schema = @Schema(implementation = DeviceKeystoreCSR.class))
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
    public DeviceKeystoreCSR createDeviceKeystoreCSR(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout,
            DeviceKeystoreCSRInfo deviceKeystoreCSRInfo) throws KapuaException {

        return deviceKeystoreManagementService.createKeystoreCSR(scopeId, deviceId, deviceKeystoreCSRInfo, timeout);
    }

    /**
     * Gets the {@link DeviceKeystoreItem} present on the {@link Device}.
     *
     * @param scopeId  The {@link Device#getScopeId()}.
     * @param deviceId The {@link Device#getId()}.
     * @param timeout  The timeout of the operation in milliseconds
     * @return HTTP {@link Response#noContent()} code.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.5.0
     */
    @DELETE
    @Path("item")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Delete a keystore item from a single Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The keystore item has been deleted"
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
    public Response deleteKeystoreItem(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Device on which to perform the operation")
            @PathParam("deviceId") EntityId deviceId,
            @Parameter(description = "The keystore id of the item to delete.")
            @QueryParam("keystoreId") String keystoreId,
            @Parameter(description = "The alias of the item to delete.")
            @QueryParam("alias") String alias,
            @Parameter(description = "The timeout for the request in milliseconds")
            @QueryParam("timeout") @DefaultValue("30000") Long timeout) throws KapuaException {

        deviceKeystoreManagementService.deleteKeystoreItem(scopeId, deviceId, keystoreId, alias, timeout);

        return returnNoContent();
    }
}
