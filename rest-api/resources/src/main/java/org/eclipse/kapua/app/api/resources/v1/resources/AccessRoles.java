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
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleAttributes;
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;

@Path("{scopeId}/accessinfos/{accessInfoId}/roles")
public class AccessRoles extends AbstractKapuaResource {

    @Inject
    public AccessRoleService accessRoleService;

    /**
     * Gets the {@link AccessRole} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link AccessRoleListResult} of all the accessRoles associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AccessRoleListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        final KapuaQuery query = new KapuaQuery(scopeId);

        query.setPredicate(query.attributePredicate(AccessRoleAttributes.ACCESS_INFO_ID, accessInfoId));

        query.setAskTotalCount(askTotalCount);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, accessInfoId, query);
    }

    /**
     * Queries the results with the given {@link KapuaQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link KapuaQuery} to use to filter results.
     * @return The {@link AccessRoleListResult} of all the result matching the given {@link KapuaQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public AccessRoleListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            KapuaQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        query.setPredicate(query.attributePredicate(AccessRoleAttributes.ACCESS_INFO_ID, accessInfoId));

        return accessRoleService.query(query);
    }

    /**
     * Counts the results with the given {@link KapuaQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link KapuaQuery} to use to filter results.
     * @return The count of all the result matching the given {@link KapuaQuery} parameter.
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
            @PathParam("accessInfoId") EntityId accessInfoId,
            KapuaQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        query.setPredicate(query.attributePredicate(AccessRoleAttributes.ACCESS_INFO_ID, accessInfoId));

        return new CountResult(accessRoleService.count(query));
    }

    /**
     * Creates a new AccessRole based on the information provided in AccessRoleCreator parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link AccessRole}.
     * @param accessRoleCreator
     *         Provides the information for the new AccessRole to be created.
     * @return The newly created {@link AccessRole} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            AccessRoleCreator accessRoleCreator) throws KapuaException {
        accessRoleCreator.setScopeId(scopeId);
        accessRoleCreator.setAccessInfoId(accessInfoId);

        return returnCreated(accessRoleService.create(accessRoleCreator));
    }

    /**
     * Returns the AccessRole specified by the "accessRoleId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link AccessRole}.
     * @param accessRoleId
     *         The id of the requested {@link AccessRole}.
     * @return The requested {@link AccessRole} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{accessRoleId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AccessRole find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            @PathParam("accessRoleId") EntityId accessRoleId) throws KapuaException {
        final KapuaQuery query = new KapuaQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(AccessRoleAttributes.ACCESS_INFO_ID, accessInfoId),
                query.attributePredicate(KapuaEntityAttributes.ENTITY_ID, accessRoleId)
        );

        query.setPredicate(andPredicate);
        query.setOffset(0);
        query.setLimit(1);

        AccessRoleListResult results = accessRoleService.query(query);

        return returnNotNullEntity(results.getFirstItem(), AccessRole.TYPE, accessRoleId);
    }

    /**
     * Deletes the AccessRole specified by the "accessRoleId" path parameter.
     *
     * @param accessRoleId
     *         The id of the AccessRole to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{accessRoleId}")
    public Response deleteAccessRole(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId,
            @PathParam("accessRoleId") EntityId accessRoleId) throws KapuaException {
        accessRoleService.delete(scopeId, accessRoleId);

        return returnNoContent();
    }
}
