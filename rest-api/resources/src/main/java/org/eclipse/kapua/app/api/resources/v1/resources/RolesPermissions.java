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
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionAttributes;
import org.eclipse.kapua.service.authorization.role.RolePermissionCreator;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionQuery;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/roles/{roleId}/permissions")
@Tag(name = "Role")
public class RolesPermissions extends AbstractKapuaResource {

    @Inject
    public RolePermissionService rolePermissionService;
    @Inject
    public RolePermissionFactory rolePermissionFactory;

    /**
     * Gets the {@link RolePermission} list in the scope.
     *
     * @param scopeId   The {@link ScopeId} in which to search results.
     * @param roleId    The id of the {@link Role} in which to search results.
     * @param domain    The domain name to filter results.
     * @param action    The action to filter results.
     * @param sortParam The name of the parameter that will be used as a sorting key
     * @param sortDir   The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param offset    The result set offset.
     * @param limit     The result set limit.
     * @return The {@link RolePermissionListResult} of all the rolePermissions associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the Role Permission")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Role Permission available in the Scope",
            content = @Content(schema = @Schema(implementation = RolePermissionListResult.class))
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
    public RolePermissionListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Role on which to perform the operation")
            @PathParam("roleId") EntityId roleId,
            @Parameter(description = "The domain name to filter results")
            @QueryParam("name") String domain,
            @Parameter(description = "The action to filter results")
            @QueryParam("action") Actions action,
            @Parameter(description = "If true, the total count of the entities matching the query will be included in the result set")
            @QueryParam("askTotalCount") boolean askTotalCount,
            @Parameter(description = "The name of the parameter that will be used as a sorting key")
            @QueryParam("sortParam") String sortParam,
            @Parameter(description = "The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive")
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        RolePermissionQuery query = rolePermissionFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        query.setPredicate(query.attributePredicate(RolePermissionAttributes.ROLE_ID, roleId));
        if (!Strings.isNullOrEmpty(domain)) {
            andPredicate.and(query.attributePredicate(RolePermissionAttributes.PERMISSION_DOMAIN, domain));
        }
        if (action != null) {
            andPredicate.and(query.attributePredicate(RolePermissionAttributes.PERMISSION_ACTION, action));
        }
        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }
        query.setPredicate(andPredicate);

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, roleId, query);
    }

    /**
     * Queries the results with the given {@link RolePermissionQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param roleId  The {@link Role} id in which to search results.
     * @param query   The {@link RolePermissionQuery} to use to filter results.
     * @return The {@link RolePermissionListResult} of all the result matching the given {@link RolePermissionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the Roles Permissions")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
            content = @Content(schema = @Schema(implementation = RolePermissionListResult.class))
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
    public RolePermissionListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Role on which to perform the operation")
            @PathParam("roleId") EntityId roleId,
            RolePermissionQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        andPredicate.and(query.attributePredicate(RolePermissionAttributes.ROLE_ID, roleId));
        if (query.getPredicate() != null) {
            andPredicate.and(query.getPredicate());
        }
        query.setPredicate(andPredicate);

        return rolePermissionService.query(query);
    }

    /**
     * Counts the results with the given {@link RolePermissionQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to count results.
     * @param roleId  The {@link Role} id in which to count results.
     * @param query   The {@link RolePermissionQuery} to use to filter results.
     * @return The count of all the result matching the given {@link RolePermissionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Roles Permissions")
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
            @Parameter(description = "The ID of the Role on which to perform the operation")
            @PathParam("roleId") EntityId roleId,
            RolePermissionQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        query.setPredicate(query.attributePredicate(RolePermissionAttributes.ROLE_ID, roleId));

        return new CountResult(rolePermissionService.count(query));
    }

    /**
     * Creates a new RolePermission based on the information provided in RolePermissionCreator
     * parameter.
     *
     * @param scopeId               The {@link ScopeId} in which to create the {@link RolePermission}
     * @param roleId                The {@link Role} id in which to create the RolePermission.
     * @param rolePermissionCreator Provides the information for the new RolePermission to be created.
     * @return The newly created RolePermission object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Create a new Role Permission")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The Role Permission that has just been created",
            content = @Content(schema = @Schema(implementation = RolePermission.class))
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
            @Parameter(description = "The ID of the Role on which to perform the operation")
            @PathParam("roleId") EntityId roleId,
            RolePermissionCreator rolePermissionCreator) throws KapuaException {
        rolePermissionCreator.setScopeId(scopeId);
        rolePermissionCreator.setRoleId(roleId);

        return returnCreated(rolePermissionService.create(rolePermissionCreator));
    }

    /**
     * Returns the RolePermission specified by the "rolePermissionId" path parameter.
     *
     * @param scopeId          The {@link ScopeId} of the requested {@link RolePermission}.
     * @param roleId           The {@link Role} id of the requested {@link RolePermission}.
     * @param rolePermissionId The id of the requested RolePermission.
     * @return The requested RolePermission object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{rolePermissionId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a single Role Permission")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired Role Permission",
            content = @Content(schema = @Schema(implementation = RolePermission.class))
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
    public RolePermission find(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Role on which to perform the operation")
            @PathParam("roleId") EntityId roleId,
            @Parameter(description = "The ID of the RolePermission on which to perform the operation")
            @PathParam("rolePermissionId") EntityId rolePermissionId) throws KapuaException {
        RolePermissionQuery query = rolePermissionFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(RolePermissionAttributes.ROLE_ID, roleId),
                query.attributePredicate(KapuaEntityAttributes.ENTITY_ID, rolePermissionId)
        );

        query.setPredicate(andPredicate);
        query.setOffset(0);
        query.setLimit(1);

        RolePermissionListResult results = rolePermissionService.query(query);

        return returnNotNullEntity(results.getFirstItem(), RolePermission.TYPE, rolePermissionId);
    }

    /**
     * Deletes the RolePermission specified by the "rolePermissionId" path parameter.
     *
     * @param scopeId          The {@link ScopeId} of the {@link RolePermission} to delete.
     * @param roleId           The {@link Role} id of the {@link RolePermission} to delete.
     * @param rolePermissionId The id of the RolePermission to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{rolePermissionId}")
    @Operation(summary = "Delete a single Role Permission")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The Role Permission has been deleted"
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
    public Response deleteRolePermission(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Role on which to perform the operation")
            @PathParam("roleId") EntityId roleId,
            @Parameter(description = "The ID of the RolePermission on which to perform the operation")
            @PathParam("rolePermissionId") EntityId rolePermissionId) throws KapuaException {
        rolePermissionService.delete(scopeId, rolePermissionId);

        return returnNoContent();
    }
}
