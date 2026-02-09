/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionAttributes;
import org.eclipse.kapua.service.authorization.group.GroupPermissionCreator;
import org.eclipse.kapua.service.authorization.group.GroupPermissionFactory;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionQuery;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupPermissionService;

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

/**
 * {@link User} {@link GroupPermission} REST API resource.
 *
 * @since 2.1.0
 */
@Path("{scopeId}/userGroups/{userGroupId}/permissions")
public class UserGroupPermissions extends AbstractKapuaResource {

    @Inject
    public UserGroupPermissionService userGroupPermissionService;
    @Inject
    public GroupPermissionFactory groupPermissionFactory;

    /**
     * Gets the {@link GroupPermission} list in the scope.
     *
     * @param scopeId      The {@link ScopeId} in which to search results.
     * @param userGroupId  The optional {@link UserGroup} id to filter results.
     * @param offset       The result set offset.
     * @param limit        The result set limit.
     * @param sortParam    The name of the parameter that will be used as a sorting key
     * @param sortDir      The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @return The {@link GroupPermissionListResult} of all the {@link GroupPermission}s associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public GroupPermissionListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        GroupPermissionQuery query = groupPermissionFactory.newQuery(scopeId);

        query.setPredicate(query.attributePredicate(GroupPermissionAttributes.GROUP_ID, userGroupId));
        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, userGroupId, query);
    }

    /**
     * Queries the {@link GroupPermission}s with the given {@link GroupPermissionQuery} parameter.
     *
     * @param scopeId      The {@link ScopeId} in which to search results.
     * @param userGroupId The {@link UserGroup} id in which to search results.
     * @param query        The {@link GroupPermissionQuery} to use to filter results.
     * @return The {@link GroupPermissionListResult} of all the result matching the given {@link GroupPermissionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public GroupPermissionListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            GroupPermissionQuery query) throws KapuaException {

        query.setScopeId(scopeId);

        query.setPredicate(query.attributePredicate(GroupPermissionAttributes.GROUP_ID, userGroupId));

        return userGroupPermissionService.query(query);
    }

    /**
     * Counts the {@link GroupPermission}s with the given {@link GroupPermissionQuery} parameter.
     *
     * @param scopeId      The {@link ScopeId} in which to count results.
     * @param userGroupId The {@link org.eclipse.kapua.service.user.group.UserGroup} id in which to count results.
     * @param query        The {@link GroupPermissionQuery} to use to filter count results.
     * @return The count of all the result matching the given {@link GroupPermissionQuery} parameter.
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
            GroupPermissionQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        query.setPredicate(query.attributePredicate(GroupPermissionAttributes.GROUP_ID, userGroupId));

        return new CountResult(userGroupPermissionService.count(query));
    }

    /**
     * Creates a new {@link GroupPermission} based on the information provided in {@link GroupPermissionCreator}
     * parameter.
     *
     * @param scopeId                 The {@link ScopeId} in which to create the GroupPermission.
     * @param userGroupId            The {@link UserGroup} id in which to create the GroupPermission.
     * @param groupPermissionCreator Provides the information for the new {@link GroupPermission} to be created.
     * @return The newly created {@link GroupPermission} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            GroupPermissionCreator groupPermissionCreator) throws KapuaException {
        groupPermissionCreator.setScopeId(scopeId);
        groupPermissionCreator.setGroupId(userGroupId);

        return returnCreated(userGroupPermissionService.create(groupPermissionCreator));
    }

    /**
     * Returns the GroupPermission specified by the "groupPermissionId" path parameter.
     *
     * @param scopeId            The {@link ScopeId} of the requested {@link GroupPermission}.
     * @param userGroupId       The {@link UserGroup} id of the requested {@link GroupPermission}.
     * @param groupPermissionId The id of the requested GroupPermission.
     * @return The requested GroupPermission object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{groupPermissionId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public GroupPermission find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            @PathParam("groupPermissionId") EntityId groupPermissionId) throws KapuaException {
        GroupPermissionQuery query = groupPermissionFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(GroupPermissionAttributes.GROUP_ID, userGroupId),
                query.attributePredicate(KapuaEntityAttributes.ENTITY_ID, groupPermissionId)
        );

        query.setPredicate(andPredicate);
        query.setOffset(0);
        query.setLimit(1);

        GroupPermissionListResult results = userGroupPermissionService.query(query);

        return returnNotNullEntity(results.getFirstItem(), GroupPermission.TYPE, groupPermissionId);
    }

    /**
     * Deletes the {@link GroupPermission} specified by the "groupPermissionId" path parameter.
     *
     * @param scopeId            The {@link ScopeId} of the {@link GroupPermission} to delete.
     * @param userGroupId       The {@link UserGroup} id of the {@link GroupPermission} to delete.
     * @param groupPermissionId The id of the GroupPermission to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{groupPermissionId}")
    public Response delete(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            @PathParam("groupPermissionId") EntityId groupPermissionId) throws KapuaException {
        userGroupPermissionService.delete(scopeId, groupPermissionId);

        return returnNoContent();
    }
}
