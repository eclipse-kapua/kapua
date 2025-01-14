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
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupService;

import com.google.common.base.Strings;

@Path("{scopeId}/groups")
public class Groups extends AbstractKapuaResource {

    @Inject
    public GroupService groupService;
    @Inject
    public GroupFactory groupFactory;

    /**
     * Gets the {@link Group} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param name
     *         The {@link Group} name to filter results
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link GroupListResult} of all the groups associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public GroupListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("name") String name,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("matchTerm") String matchTerm,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        final GroupQuery query = new GroupQuery(scopeId);

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

        query.setOffset(offset);
        query.setLimit(limit);
        query.setAskTotalCount(askTotalCount);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link GroupQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link GroupQuery} to use to filter results.
     * @return The {@link GroupListResult} of all the result matching the given {@link GroupQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public GroupListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            GroupQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return groupService.query(query);
    }

    /**
     * Counts the results with the given {@link GroupQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link GroupQuery} to use to filter results.
     * @return The count of all the result matching the given {@link GroupQuery} parameter.
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
            GroupQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(groupService.count(query));
    }

    /**
     * Creates a new Group based on the information provided in GroupCreator parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link Group}
     * @param groupCreator
     *         Provides the information for the new {@link Group} to be created.
     * @return The newly created {@link Group} object.
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            GroupCreator groupCreator) throws KapuaException {
        groupCreator.setScopeId(scopeId);

        return returnCreated(groupService.create(groupCreator));
    }

    /**
     * Returns the Group specified by the "groupId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link Group}.
     * @param groupId
     *         The id of the requested Group.
     * @return The requested Group object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{groupId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Group find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("groupId") EntityId groupId) throws KapuaException {
        Group group = groupService.find(scopeId, groupId);

        return returnNotNullEntity(group, Group.TYPE, groupId);
    }

    /**
     * Updates the Group based on the information provided in the Group parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link Group}.
     * @param groupId
     *         The id of the requested {@link Group}
     * @param group
     *         The modified Group whose attributed need to be updated.
     * @return The updated group.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @PUT
    @Path("{groupId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Group update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("groupId") EntityId groupId,
            Group group) throws KapuaException {
        group.setScopeId(scopeId);
        group.setId(groupId);

        return groupService.update(group);
    }

    /**
     * Deletes the Group specified by the "groupId" path parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link Group}.
     * @param groupId
     *         The id of the Group to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{groupId}")
    public Response deleteGroup(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("groupId") EntityId groupId) throws KapuaException {
        groupService.delete(scopeId, groupId);

        return returnNoContent();
    }
}
