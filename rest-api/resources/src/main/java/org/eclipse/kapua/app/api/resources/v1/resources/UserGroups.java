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
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.jersey.rest.model.CountResult;
import org.eclipse.kapua.commons.jersey.rest.model.EntityId;
import org.eclipse.kapua.commons.jersey.rest.model.ScopeId;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupAttributes;
import org.eclipse.kapua.service.user.group.UserGroupCreator;
import org.eclipse.kapua.service.user.group.UserGroupFactory;
import org.eclipse.kapua.service.user.group.UserGroupListResult;
import org.eclipse.kapua.service.user.group.UserGroupQuery;
import org.eclipse.kapua.service.user.group.UserGroupService;

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

@Path("{scopeId}/userGroups")
public class UserGroups extends AbstractKapuaResource {

    @Inject
    public UserGroupService userGroupService;
    @Inject
    public UserGroupFactory userGroupFactory;

    /**
     * Gets the {@link UserGroup} list in the scope.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param name    The {@link UserGroup} name to filter results
     * @param offset  The result set offset.
     * @param limit   The result set limit.
     * @return The {@link UserGroupListResult} of all the user groups associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public UserGroupListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("tagId") EntityId tagId,
            @QueryParam("name") String name,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        UserGroupQuery query = userGroupFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (tagId != null) {
            andPredicate.and(query.attributePredicate(UserGroupAttributes.TAG_IDS, tagId));
        }
        if (!Strings.isNullOrEmpty(name)) {
            andPredicate.and(query.attributePredicate(UserGroupAttributes.NAME, name));
        }
        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }
        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);
        query.setAskTotalCount(askTotalCount);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link UserGroupQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link UserGroupQuery} to use to filter results.
     * @return The {@link UserGroupListResult} of all the result matching the given {@link UserGroupQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public UserGroupListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            UserGroupQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return userGroupService.query(query);
    }

    /**
     * Counts the results with the given {@link UserGroupQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link UserGroupQuery} to use to filter results.
     * @return The count of all the result matching the given {@link UserGroupQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId,
            UserGroupQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(userGroupService.count(query));
    }

    /**
     * Creates a new UserGroup based on the information provided in UserGroupCreator
     * parameter.
     *
     * @param scopeId      The {@link ScopeId} in which to create the {@link UserGroup}
     * @param userGroupCreator Provides the information for the new {@link UserGroup} to be created.
     * @return The newly created {@link UserGroup} object.
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            UserGroupCreator userGroupCreator) throws KapuaException {
        userGroupCreator.setScopeId(scopeId);

        return returnCreated(userGroupService.create(userGroupCreator));
    }

    /**
     * Returns the UserGroup specified by the "userGroupId" path parameter.
     *
     * @param scopeId The {@link ScopeId} of the requested {@link UserGroup}.
     * @param userGroupId The id of the requested UserGroup.
     * @return The requested UserGroup object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{userGroupId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public UserGroup find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId) throws KapuaException {
        UserGroup userGroup = userGroupService.find(scopeId, userGroupId);

        return returnNotNullEntity(userGroup, UserGroup.TYPE, userGroupId);
    }

    /**
     * Updates the UserGroup based on the information provided in the UserGroup parameter.
     *
     * @param scopeId The ScopeId of the requested {@link UserGroup}.
     * @param userGroupId The id of the requested {@link UserGroup}
     * @param userGroup   The modified Group whose attributed need to be updated.
     * @return The updated user group.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @PUT
    @Path("{userGroupId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public UserGroup update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId,
            UserGroup userGroup)
        throws KapuaException {

        userGroup.setScopeId(scopeId);
        userGroup.setId(userGroupId);

        return userGroupService.update(userGroup);
    }

    /**
     * Deletes the UserGroup specified by the "userGroupId" path parameter.
     *
     * @param scopeId The ScopeId of the requested {@link UserGroup}.
     * @param userGroupId The id of the UserGroup to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{userGroupId}")
    public Response delete(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userGroupId") EntityId userGroupId) throws KapuaException {
        userGroupService.delete(scopeId, userGroupId);

        return returnNoContent();
    }
}
