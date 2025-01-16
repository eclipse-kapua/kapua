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
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/users")
@Tag(name = "User")
public class Users extends AbstractKapuaResource {

    @Inject
    public UserService userService;
    @Inject
    public UserFactory userFactory;

    /**
     * Gets the {@link User} list in the scope.
     *
     * @param scopeId       The {@link ScopeId} in which to search results.
     * @param name          The {@link User} name in which to search results.
     * @param sortParam     The name of the parameter that will be used as a sorting key
     * @param sortDir       The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param matchTerm     A term to be matched in at least one of the configured fields of this entity
     * @param askTotalCount Ask for the total count of the matched entities in the result
     * @param offset        The result set offset.
     * @param limit         The result set limit.
     * @return The {@link UserListResult} of all the users associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the Users")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Users available in the Scope",
            content = @Content(schema = @Schema(implementation = UserListResult.class))
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
    public UserListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The user name to filter results")
            @QueryParam("name") String name,
            @Parameter(description = "A term to match on different fields. Every entity whose at least one of the specified fields starts with this" +
                                         "value will be matched. Matches on the following fields:\n" +
                                         "\n" + "* NAME\n" + "* EMAIL\n" + "* PHONE_NUMBER\n" +
                                         "* DISPLAY_NAME\n" + "* EXTERNAL_ID\n" + "* DESCRIPTION\n" +
                                         "* FIRMWARE_VERSION\n" + "* OS_VERSION\n" + "* JVM_VERSION\n" +
                                         "* EXTERNAL_USERNAME\n")
            @QueryParam("matchTerm") String matchTerm,
            @Parameter(description = "The name of the parameter that will be used as a sorting key")
            @QueryParam("sortParam") String sortParam,
            @Parameter(description = "The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.")
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @Parameter(description = "If true, the total count of the entities matching the query will be included in the result set")
            @QueryParam("askTotalCount") boolean askTotalCount,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        UserQuery query = userFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (!Strings.isNullOrEmpty(name)) {
            andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.NAME, name));
        }
        if (matchTerm != null && !matchTerm.isEmpty()) {
            andPredicate.and(query.matchPredicate(matchTerm));
        }
        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setPredicate(andPredicate);

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link UserQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link UserQuery} to use to filter results.
     * @return The {@link UserListResult} of all the result matching the given {@link UserQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the Users")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
            content = @Content(schema = @Schema(implementation = UserListResult.class))
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
    public UserListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            UserQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return userService.query(query);
    }

    /**
     * Counts the results with the given {@link UserQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to count results.
     * @param query   The {@link UserQuery} to use to filter results.
     * @return The count of all the result matching the given {@link UserQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Users")
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
            UserQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(userService.count(query));
    }

    /**
     * Creates a new User based on the information provided in UserCreator
     * parameter.
     *
     * @param scopeId     The {@link ScopeId} in which to create the {@link User}
     * @param userCreator Provides the information for the new User to be created.
     * @return The newly created User object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Create a new User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The User that has just been created",
            content = @Content(schema = @Schema(implementation = User.class))
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
            @PathParam("scopeId") ScopeId scopeId,
            UserCreator userCreator) throws KapuaException {
        userCreator.setScopeId(scopeId);

        return returnCreated(userService.create(userCreator));
    }

    /**
     * Returns the User specified by the "userId" path parameter.
     *
     * @param scopeId The {@link ScopeId} of the requested {@link User}.
     * @param userId  The id of the requested User.
     * @return The requested User object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{userId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a single User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired User",
            content = @Content(schema = @Schema(implementation = User.class))
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
    public User find(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId) throws KapuaException {
        User user = userService.find(scopeId, userId);

        return returnNotNullEntity(user, User.TYPE, userId);
    }

    /**
     * Updates the User based on the information provided in the User parameter.
     *
     * @param scopeId The ScopeId of the requested {@link User}.
     * @param userId  The id of the requested {@link User}
     * @param user    The modified User whose attributed need to be updated.
     * @return The updated user.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @PUT
    @Path("{userId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Update a single User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the updated User",
            content = @Content(schema = @Schema(implementation = User.class))
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
    public User update(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId,
            @Parameter(description = "An object containing the new properties for the User to update")
            User user) throws KapuaException {
        user.setScopeId(scopeId);
        user.setId(userId);

        return userService.update(user);
    }

    /**
     * Deletes the User specified by the "userId" path parameter.
     *
     * @param scopeId The ScopeId of the requested {@link User}.
     * @param userId  The id of the User to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{userId}")
    @Operation(summary = "Delete a single User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The User has been deleted"
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
    public Response deleteUser(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the User on which to perform the operation")
            @PathParam("userId") EntityId userId) throws KapuaException {
        userService.delete(scopeId, userId);

        return returnNoContent();
    }
}
