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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jersey.rest.model.CountResult;
import org.eclipse.kapua.commons.jersey.rest.model.EntityId;
import org.eclipse.kapua.commons.jersey.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoAttributes;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoListResult;
import org.eclipse.kapua.service.authorization.access.AccessInfoQuery;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.user.User;

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
 * {@link AccessInfo} REST API resource.
 *
 * @since 1.0.0
 */
@Path("{scopeId}/accessinfos")
public class AccessInfos extends AbstractKapuaResource {

    @Inject
    public AccessInfoService accessInfoService;
    @Inject
    public AccessInfoFactory accessInfoFactory;

    /**
     * Gets the {@link AccessInfo} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param userId
     *         The optional {@link User} id to filter results.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link AccessInfoListResult} of all the {@link AccessInfo}s associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AccessInfoListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("userId") EntityId userId,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {

        AccessInfoQuery query = accessInfoFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (userId != null) {
            andPredicate.and(query.attributePredicate(AccessInfoAttributes.USER_ID, userId));
        }
        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, query);
    }

    /**
     * Queries the {@link AccessInfo}s with the given {@link AccessInfoQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link AccessInfoQuery} to use to filter results.
     * @return The {@link AccessInfoListResult} of all the result matching the given {@link AccessInfoQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public AccessInfoListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            AccessInfoQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return accessInfoService.query(query);
    }

    /**
     * Counts the {@link AccessInfo}s with the given {@link AccessInfoQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to count results.
     * @param query
     *         The {@link AccessInfoQuery} to use to filter count results.
     * @return The count of all the result matching the given {@link AccessInfoQuery} parameter.
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
            AccessInfoQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(accessInfoService.count(query));
    }

    /**
     * Creates a new {@link AccessInfo} based on the information provided in {@link AccessInfoCreator} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link AccessInfo}.
     * @param accessInfoCreator
     *         Provides the information for the new {@link AccessInfo} to be created.
     * @return The newly created {@link AccessInfo} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId, //
            AccessInfoCreator accessInfoCreator) throws KapuaException {
        accessInfoCreator.setScopeId(scopeId);

        return returnCreated(accessInfoService.create(accessInfoCreator));
    }

    /**
     * Gets the {@link AccessInfo} specified by the "accessInfoId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link AccessInfo}.
     * @param accessInfoId
     *         The id of the requested {@link AccessInfo}.
     * @return The requested {@link AccessInfo} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{accessInfoId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AccessInfo find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId) throws KapuaException {
        AccessInfo accessInfo = accessInfoService.find(scopeId, accessInfoId);

        return returnNotNullEntity(accessInfo, AccessInfo.TYPE, accessInfoId);
    }

    /**
     * Deletes the {@link AccessInfo} specified by the "accessInfoId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the {@link AccessInfo} to be deleted.
     * @param accessInfoId
     *         The id of the {@link AccessInfo} to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{accessInfoId}")
    public Response deleteAccessInfo(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("accessInfoId") EntityId accessInfoId) throws KapuaException {
        accessInfoService.delete(scopeId, accessInfoId);

        return returnNoContent();
    }
}
