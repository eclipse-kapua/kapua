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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOption;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionService;
import org.eclipse.kapua.service.authentication.credential.mfa.shiro.MfaOptionCreatorImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/users")
@Tag(name = "Users - Mfa")
public class UsersMfa extends AbstractKapuaResource {

    @Inject
    public MfaOptionService mfaOptionService;

    /**
     * Creates a new {@link MfaOption} for the user specified by the "userId" path parameter.
     *
     * @param scopeId The {@link ScopeId} in which to create the {@link MfaOption}
     * @param userId  The {@link EntityId} of the User to which the {@link MfaOption} belongs
     * @return The newly created {@link MfaOption} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     * @deprecated since 2.0.0 - use POST {scopeId}/user/mfa instead (see {@link UserMfa})
     */
    @POST
    @Path("{userId}/mfa")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Deprecated
    @Operation(
        summary = "Create a new MfaOption",
        description = "This resource is deprecated and will be removed in future releases. Please use POST /{scopeId}/user/mfa instead."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The MfaOption that has just been created",
            content = @Content(schema = @Schema(implementation = MfaOption.class))
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Response createMfa(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId) throws KapuaException {
        return returnCreated(mfaOptionService.create(new MfaOptionCreatorImpl(scopeId, userId)));
    }

    /**
     * Returns the {@link MfaOption} of the user specified by the "userId" path parameter.
     *
     * @param scopeId The {@link ScopeId} of the requested {@link MfaOption}
     * @param userId  The {@link EntityId} of the User to which the {@link MfaOption} belongs
     * @return The requested {@link MfaOption} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @GET
    @Path("{userId}/mfa")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get the MfaOption of this User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired MfaOption",
            content = @Content(schema = @Schema(implementation = MfaOption.class))
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
    public MfaOption findMfa(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId) throws KapuaException {
        MfaOption mfaOption = mfaOptionService.findByUserId(scopeId, userId);
        if (mfaOption == null) {
            throw new KapuaEntityNotFoundException(MfaOption.TYPE, "MfaOption");  // TODO: not sure "MfaOption" it's the best value to return here
        }

        return mfaOption;
    }

    /**
     * Deletes the {@link MfaOption} of the user specified by the "userId" path parameter.
     *
     * @param scopeId The {@link ScopeId} of the requested {@link MfaOption}
     * @param userId  The {@link EntityId} of the User to which the {@link MfaOption} belongs
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @DELETE
    @Path("{userId}/mfa")
    @Operation(summary = "Delete a single MfaOption")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The MfaOption has been deleted"
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
    public Response deleteMfa(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId) throws KapuaException {
        mfaOptionService.deleteByUserId(scopeId, userId);

        return returnNoContent();
    }

    /**
     * Disable trusted machine for a given {@link MfaOption}.
     *
     * @param scopeId The ScopeId of the requested {@link MfaOption}.
     * @param userId  The {@link EntityId} of the User to which the {@link MfaOption} belongs
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @DELETE
    @Path("{userId}/mfa/disableTrust")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Disable trusted machine for a MfaOption")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The trusted machine has been disabled for the User"
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
    public Response disableTrust(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId) throws KapuaException {
        mfaOptionService.disableTrustByUserId(scopeId, userId);

        return returnNoContent();
    }
}
