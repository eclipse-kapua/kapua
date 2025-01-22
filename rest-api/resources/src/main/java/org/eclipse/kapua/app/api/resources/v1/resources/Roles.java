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

import java.util.List;

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
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleListResult;
import org.eclipse.kapua.service.authorization.role.RoleQuery;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;

import com.google.common.base.Strings;

@Path("{scopeId}/roles")
public class Roles extends AbstractKapuaResource {

    @Inject
    public RoleService roleService;
    @Inject
    public UserService userService;

    /**
     * Gets the {@link Role} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param name
     *         The {@link Role} name in which to search results.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link RoleListResult} of all the roles associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public RoleListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("name") String name,
            @QueryParam("matchTerm") String matchTerm,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        RoleQuery query = new RoleQuery(scopeId);

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
     * Queries the results with the given {@link RoleQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link RoleQuery} to use to filter results.
     * @return The {@link RoleListResult} of all the result matching the given {@link RoleQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public RoleListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            RoleQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return roleService.query(query);
    }

    /**
     * Counts the results with the given {@link RoleQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link RoleQuery} to use to filter results.
     * @return The count of all the result matching the given {@link RoleQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId,
            RoleQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(roleService.count(query));
    }

    /**
     * Creates a new Role based on the information provided in RoleCreator parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link Role}
     * @param roleCreator
     *         Provides the information for the new {@link Role} to be created.
     * @return The newly created {@link Role} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            RoleCreator roleCreator) throws KapuaException {
        roleCreator.setScopeId(scopeId);

        return returnCreated(roleService.create(roleCreator));
    }

    /**
     * Returns the Role specified by the "roleId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link Role}.
     * @param roleId
     *         The id of the requested {@link Role}.
     * @return The requested {@link Role} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{roleId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Role find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("roleId") EntityId roleId) throws KapuaException {
        Role role = roleService.find(scopeId, roleId);

        return returnNotNullEntity(role, Role.TYPE, roleId);
    }

    /**
     * Updates the Role based on the information provided in the Role parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link Role}.
     * @param roleId
     *         The id of the requested {@link Role}
     * @param role
     *         The modified Role whose attributed need to be updated.
     * @return The updated {@link Role}.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @PUT
    @Path("{roleId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Role update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("roleId") EntityId roleId,
            Role role) throws KapuaException {
        role.setScopeId(scopeId);
        role.setId(roleId);

        return roleService.update(role);
    }

    /**
     * Deletes the Role specified by the "roleId" path parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link Role}.
     * @param roleId
     *         The id of the Role to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{roleId}")
    public Response deleteRole(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("roleId") EntityId roleId) throws KapuaException {
        roleService.delete(scopeId, roleId);

        return returnNoContent();
    }

    /**
     * Gets all the {@link User}s for a given {@link Role}
     *
     * @param scopeId
     *         The ScopeId of the requested {@link Role}.
     * @param roleId
     *         The id of the Role to be deleted.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @param sortParam
     *         The name of the parameter that will be used as a sorting key for the users
     * @param sortDir
     *         The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @return An {@link UserListResult} containing the {@link User}s for the given {@link Role}
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.2.0
     */
    @GET
    @Path("{roleId}/users")
    public UserListResult usersForRole(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("roleId") EntityId roleId,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        List<KapuaId> usersIds = roleService.userIdsByRoleId(scopeId, roleId);

        UserQuery userQuery = new UserQuery(scopeId);
        userQuery.setPredicate(userQuery.attributePredicate(KapuaEntityAttributes.ENTITY_ID, usersIds));
        userQuery.setLimit(limit);
        userQuery.setOffset(offset);
        if (!Strings.isNullOrEmpty(sortParam)) {
            userQuery.setSortCriteria(userQuery.fieldSortCriteria(sortParam, sortDir));
        }
        return userService.query(userQuery);
    }

}
