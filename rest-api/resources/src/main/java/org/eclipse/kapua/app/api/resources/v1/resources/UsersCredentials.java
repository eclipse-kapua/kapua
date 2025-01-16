/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialAttributes;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.credential.CredentialQuery;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.user.PasswordResetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/*
 @deprecated
 accidentally exposed under:
 /{scopeId}/user/{userId}/credentials
 instead of the desired*
 /{scopeId}/users/{userId}/credentials (notice the plural userS)
 Remove the match with /user/ in the next release
 */
@Path("/{scopeId}/user{plural:|s}/{userId}/credentials")
@Tag(name = "Users Credentials")
public class UsersCredentials extends AbstractKapuaResource {

    @Inject
    public CredentialService credentialService;
    @Inject
    public CredentialFactory credentialFactory;

    /**
     * Gets the {@link Credential} list in the scope.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param userId  The {@link EntityId} for which search results.
     * @param offset  The result set offset.
     * @param limit   The result set limit.
     * @return The {@link CredentialListResult} of all the credentials associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the Credentials for the User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Credentials available for the User",
            content = @Content(schema = @Schema(implementation = CredentialListResult.class))
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
    public CredentialListResult getAll(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        CredentialQuery query = credentialFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        andPredicate.and(query.attributePredicate(CredentialAttributes.USER_ID, userId));
        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);

        return credentialService.query(query);
    }


    /**
     * Counts the results with the given {@link CredentialQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to count results.
     * @param userId  The {@link EntityId} for which count results.
     * @param query   The {@link CredentialQuery} to use to filter results.
     * @return The count of all the result matching the given {@link CredentialQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Credentials for the User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The count of the available Entities",
            content = @Content(schema = @Schema(implementation = CountResult.class))
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
    public CountResult count(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId,
            CredentialQuery query) throws KapuaException {
        AndPredicate andPredicate = query.andPredicate();
        andPredicate.and(query.attributePredicate(CredentialAttributes.USER_ID, userId));
        query.setPredicate(andPredicate);

        return new CountResult(credentialService.count(query));
    }


    /**
     * Creates a new Credential based on the information provided in CredentialCreator
     * parameter.
     *
     * @param scopeId           The {@link ScopeId} in which to create the {@link Credential}.
     * @param userId            The {@link EntityId} for which create the {@link Credential}.
     * @param credentialCreator Provides the information for the new Credential to be created.
     * @return The newly created Credential object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Create a new Credential for the User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The Credential that has just been created",
            content = @Content(schema = @Schema(implementation = Credential.class))
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
    public Response create(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId,
            CredentialCreator credentialCreator) throws KapuaException {
        credentialCreator.setScopeId(scopeId);
        credentialCreator.setUserId(userId);

        return returnCreated(credentialService.create(credentialCreator));
    }

    /**
     * Reset the password for the specific user
     *
     * @param scopeId              The {@link ScopeId} of the {@link Credential} to reset.
     * @param userId               The {@link EntityId} for which to reset the password credential.
     * @param passwordResetRequest Request for resetting credential password
     * @return The updated credential.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.0.0
     * @deprecated since 2.0.0 - use POST POST /{scopeId}/users/{userId}/password/_reset instead (see {@link UsersCredentials})
     */
    @POST
    @Path("password/_reset")
    @Deprecated
    @Operation(
        summary = "Reset the password of a user",
        description = "Reset the password credential for the specified user, or creates one if there is none"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The updated or created Credential",
            content = @Content(schema = @Schema(implementation = Credential.class))
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
    public Credential unlockCredential(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId,
            PasswordResetRequest passwordResetRequest) throws KapuaException {
        return credentialService.adminResetUserPassword(scopeId, userId, passwordResetRequest);
    }
}
