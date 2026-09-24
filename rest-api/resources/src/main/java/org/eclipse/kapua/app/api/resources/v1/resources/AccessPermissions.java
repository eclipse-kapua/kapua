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
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessPermissionCreator;
import org.eclipse.kapua.service.authorization.access.AccessPermissionFactory;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionQuery;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;

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
 * {@link AccessPermission} REST API resource.
 *
 * @since 1.0.0
 */
@Path("{scopeId}/accessinfos/{accessInfoId}/permissions")
public class AccessPermissions extends AbstractKapuaResource {

    @Inject
    public AccessPermissionService accessPermissionService;
    @Inject
    public AccessPermissionFactory accessPermissionFactory;

    /**
     * Gets the {@link AccessPermission} list in the scope.
     *
     * @param scopeId      The {@link ScopeId} in which to search results.
     * @param accessInfoId The optional {@link AccessInfo} id to filter results.
     * @param offset       The result set offset.
     * @param limit        The result set limit.
     * @param sortParam    The name of the parameter that will be used as a sorting key
     * @param sortDir      The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @return The {@link AccessPermissionListResult} of all the {@link AccessPermission}s associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public AccessPermissionListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        AccessPermissionQuery query = accessPermissionFactory.newQuery(scopeId);

        query.setPredicate(query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfoId));
        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, accessInfoId, query);
    }

    /**
     * Queries the {@link AccessPermission}s with the given {@link AccessPermissionQuery} parameter.
     *
     * @param scopeId      The {@link ScopeId} in which to search results.
     * @param accessInfoId The {@link AccessInfo} id in which to search results.
     * @param query        The {@link AccessPermissionQuery} to use to filter results.
     * @return The {@link AccessPermissionListResult} of all the result matching the given {@link AccessPermissionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public AccessPermissionListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            AccessPermissionQuery query) throws KapuaException {

        query.setScopeId(scopeId);

        query.setPredicate(query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfoId));

        return accessPermissionService.query(query);
    }

    /**
     * Counts the {@link AccessPermission}s with the given {@link AccessPermissionQuery} parameter.
     *
     * @param scopeId      The {@link ScopeId} in which to count results.
     * @param accessInfoId The {@link AccessInfo} id in which to count results.
     * @param query        The {@link AccessPermissionQuery} to use to filter count results.
     * @return The count of all the result matching the given {@link AccessPermissionQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            AccessPermissionQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        query.setPredicate(query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfoId));

        return new CountResult(accessPermissionService.count(query));
    }

    /**
     * Creates a new {@link AccessPermission} based on the information provided in {@link AccessPermissionCreator}
     * parameter.
     *
     * @param scopeId                 The {@link ScopeId} in which to create the AccessPermission.
     * @param accessInfoId            The {@link AccessInfo} id in which to create the AccessPermission.
     * @param accessPermissionCreator Provides the information for the new {@link AccessPermission} to be created.
     * @return The newly created {@link AccessPermission} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            AccessPermissionCreator accessPermissionCreator) throws KapuaException {
        accessPermissionCreator.setScopeId(scopeId);
        accessPermissionCreator.setAccessInfoId(accessInfoId);

        return returnCreated(accessPermissionService.create(accessPermissionCreator));
    }

    /**
     * Returns the AccessPermission specified by the "accessPermissionId" path parameter.
     *
     * @param scopeId            The {@link ScopeId} of the requested {@link AccessPermission}.
     * @param accessInfoId       The {@link AccessInfo} id of the requested {@link AccessPermission}.
     * @param accessPermissionId The id of the requested AccessPermission.
     * @return The requested AccessPermission object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{accessPermissionId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public AccessPermission find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            @PathParam("accessPermissionId") EntityId accessPermissionId) throws KapuaException {
        AccessPermissionQuery query = accessPermissionFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfoId),
                query.attributePredicate(KapuaEntityAttributes.ENTITY_ID, accessPermissionId)
        );

        query.setPredicate(andPredicate);
        query.setOffset(0);
        query.setLimit(1);

        AccessPermissionListResult results = accessPermissionService.query(query);

        return returnNotNullEntity(results.getFirstItem(), AccessPermission.TYPE, accessPermissionId);
    }

    /**
     * Deletes the {@link AccessPermission} specified by the "accessPermissionId" path parameter.
     *
     * @param scopeId            The {@link ScopeId} of the {@link AccessPermission} to delete.
     * @param accessInfoId       The {@link AccessInfo} id of the {@link AccessPermission} to delete.
     * @param accessPermissionId The id of the AccessPermission to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{accessPermissionId}")
    public Response deleteAccessPermission(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            @PathParam("accessPermissionId") EntityId accessPermissionId) throws KapuaException {

        try {
            find(scopeId,accessInfoId,accessPermissionId);
        } catch (KapuaEntityNotFoundException e) {
            throw e;
        }

        accessPermissionService.delete(scopeId, accessPermissionId);

        return returnNoContent();
    }
}
