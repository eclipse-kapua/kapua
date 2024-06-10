/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.CountResult;
import org.eclipse.kapua.commons.rest.model.EntityId;
import org.eclipse.kapua.commons.rest.model.ScopeId;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoAttributes;
import org.eclipse.kapua.service.endpoint.EndpointInfoCreator;
import org.eclipse.kapua.service.endpoint.EndpointInfoFactory;
import org.eclipse.kapua.service.endpoint.EndpointInfoListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;

import com.google.common.base.Strings;

@Path("{scopeId}/endpointInfos")
public class EndpointInfos extends AbstractKapuaResource {

    @Inject
    public EndpointInfoService endpointInfoService;
    @Inject
    public EndpointInfoFactory endpointInfoFactory;

    /**
     * Gets the {@link EndpointInfo} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param usage
     *         The {@link EndpointInfo} usage to filter results
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link EndpointInfoListResult} of all the endpointInfos associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public EndpointInfoListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("usage") String usage,
            @QueryParam("endpointType") @DefaultValue(EndpointInfo.ENDPOINT_TYPE_RESOURCE) String endpointType,
            @QueryParam("matchTerm") String matchTerm,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        EndpointInfoQuery query = endpointInfoFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (!Strings.isNullOrEmpty(usage)) {
            andPredicate.and(query.attributePredicate(EndpointInfoAttributes.USAGES, endpointInfoFactory.newEndpointUsage(usage)));
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

        return query(scopeId, endpointType, query);
    }

    /**
     * Queries the results with the given {@link EndpointInfoQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link EndpointInfoQuery} to use to filter results.
     * @return The {@link EndpointInfoListResult} of all the result matching the given {@link EndpointInfoQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public EndpointInfoListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("endpointType") @DefaultValue(EndpointInfo.ENDPOINT_TYPE_RESOURCE) String endpointType,
            EndpointInfoQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return endpointInfoService.query(query, endpointType);
    }

    /**
     * Counts the results with the given {@link EndpointInfoQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link EndpointInfoQuery} to use to filter results.
     * @return The count of all the result matching the given {@link EndpointInfoQuery} parameter.
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
            @QueryParam("endpointType") @DefaultValue(EndpointInfo.ENDPOINT_TYPE_RESOURCE) String endpointType,
            EndpointInfoQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(endpointInfoService.count(query, endpointType));
    }

    /**
     * Creates a new EndpointInfo based on the information provided in EndpointInfoCreator parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link EndpointInfo}
     * @param endpointInfoCreator
     *         Provides the information for the new {@link EndpointInfo} to be created.
     * @return The newly created {@link EndpointInfo} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            EndpointInfoCreator endpointInfoCreator) throws KapuaException {
        endpointInfoCreator.setScopeId(scopeId);

        return returnCreated(endpointInfoService.create(endpointInfoCreator));
    }

    /**
     * Returns the EndpointInfo specified by the "endpointInfoId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link EndpointInfo}.
     * @param endpointInfoId
     *         The id of the requested EndpointInfo.
     * @return The requested EndpointInfo object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @GET
    @Path("{endpointInfoId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public EndpointInfo find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("endpointInfoId") EntityId endpointInfoId) throws KapuaException {
        EndpointInfo endpointInfo = endpointInfoService.find(scopeId, endpointInfoId);

        return returnNotNullEntity(endpointInfo, EndpointInfo.TYPE, endpointInfoId);
    }

    /**
     * Updates the EndpointInfo based on the information provided in the EndpointInfo parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link EndpointInfo}.
     * @param endpointInfoId
     *         The id of the requested {@link EndpointInfo}
     * @param endpointInfo
     *         The modified EndpointInfo whose attributed need to be updated.
     * @return The updated endpointInfo.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @PUT
    @Path("{endpointInfoId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public EndpointInfo update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("endpointInfoId") EntityId endpointInfoId,
            EndpointInfo endpointInfo) throws KapuaException {
        endpointInfo.setScopeId(scopeId);
        endpointInfo.setId(endpointInfoId);

        return endpointInfoService.update(endpointInfo);
    }

    /**
     * Deletes the EndpointInfo specified by the "endpointInfoId" path parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link EndpointInfo}.
     * @param endpointInfoId
     *         The id of the EndpointInfo to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @DELETE
    @Path("{endpointInfoId}")
    public Response deleteEndpointInfo(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("endpointInfoId") EntityId endpointInfoId) throws KapuaException {
        endpointInfoService.delete(scopeId, endpointInfoId);

        return returnNoContent();
    }
}
