/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleAttributes;
import org.eclipse.kapua.service.authorization.group.GroupRoleCreator;
import org.eclipse.kapua.service.authorization.group.GroupRoleFactory;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.group.GroupRoleQuery;
import org.eclipse.kapua.service.user.group.UserGroupRoleService;

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

@Path("{scopeId}/userGroups/{userGroupId}/roles")
public class UserGroupRoles extends AbstractKapuaResource {

    @Inject
    public UserGroupRoleService userGroupRoleService;
    @Inject
    public GroupRoleFactory groupRoleFactory;

    /**
     * Gets the {@link GroupRole} list in the scope.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param offset  The result set offset.
     * @param limit   The result set limit.
     * @return The {@link GroupRoleListResult} of all the groupRoles associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public GroupRoleListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        GroupRoleQuery query = groupRoleFactory.newQuery(scopeId);

        query.setPredicate(query.attributePredicate(GroupRoleAttributes.GROUP_ID, userGroupId));

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, userGroupId, query);
    }

    /**
     * Queries the results with the given {@link GroupRoleQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link GroupRoleQuery} to use to filter results.
     * @return The {@link GroupRoleListResult} of all the result matching the given {@link GroupRoleQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public GroupRoleListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            GroupRoleQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        query.setPredicate(query.attributePredicate(GroupRoleAttributes.GROUP_ID, userGroupId));

        return userGroupRoleService.query(query);
    }

    /**
     * Counts the results with the given {@link GroupRoleQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link GroupRoleQuery} to use to filter results.
     * @return The count of all the result matching the given {@link GroupRoleQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            GroupRoleQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        query.setPredicate(query.attributePredicate(GroupRoleAttributes.GROUP_ID, userGroupId));

        return new CountResult(userGroupRoleService.count(query));
    }

    /**
     * Creates a new GroupRole based on the information provided in GroupRoleCreator
     * parameter.
     *
     * @param scopeId           The {@link ScopeId} in which to create the {@link GroupRole}.
     * @param groupRoleCreator Provides the information for the new GroupRole to be created.
     * @return The newly created {@link GroupRole} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            GroupRoleCreator groupRoleCreator) throws KapuaException {
        groupRoleCreator.setScopeId(scopeId);
        groupRoleCreator.setGroupId(userGroupId);

        return returnCreated(userGroupRoleService.create(groupRoleCreator));
    }

    /**
     * Returns the GroupRole specified by the "uswrRoleId" path parameter.
     *
     * @param scopeId      The {@link ScopeId} of the requested {@link GroupRole}.
     * @param groupRoleId The id of the requested {@link GroupRole}.
     * @return The requested {@link GroupRole} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{groupRoleId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public GroupRole find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            @PathParam("groupRoleId") EntityId groupRoleId) throws KapuaException {
        GroupRoleQuery query = groupRoleFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(GroupRoleAttributes.GROUP_ID, userGroupId),
                query.attributePredicate(GroupRoleAttributes.ROLE_ID, groupRoleId)
        );

        query.setPredicate(andPredicate);
        query.setOffset(0);
        query.setLimit(1);

        GroupRoleListResult results = userGroupRoleService.query(query);

        return returnNotNullEntity(results.getFirstItem(), GroupRole.TYPE, groupRoleId);
    }

    /**
     * Deletes the GroupRole specified by the "groupRoleId" path parameter.
     *
     * @param groupRoleId The id of the GroupRole to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{groupRoleId}")
    public Response delete(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            @PathParam("groupRoleId") EntityId groupRoleId) throws KapuaException {
        userGroupRoleService.delete(scopeId, groupRoleId);

        return returnNoContent();
    }
}
