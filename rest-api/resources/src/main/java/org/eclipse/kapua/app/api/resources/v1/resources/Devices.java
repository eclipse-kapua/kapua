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
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.tag.Tag;

import com.google.common.base.Strings;

@Path("{scopeId}/devices")
public class Devices extends AbstractKapuaResource {

    @Inject
    public DeviceRegistryService deviceService;
    @Inject
    public DeviceFactory deviceFactory;

    /**
     * Gets the {@link Device} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param tagId
     *         The id of the {@link Tag} in which to search results
     * @param clientId
     *         The id of the {@link Device} in which to search results
     * @param connectionStatus
     *         The {@link DeviceConnectionStatus} in which to search results
     * @param matchTerm
     *         A term to be matched in at least one of the configured fields of this entity
     * @param fetchAttributes
     *         Additional attributes to be returned. Allowed values: connection, lastEvent
     * @param askTotalCount
     *         Ask for the total count of the matched entities in the result
     * @param sortParam
     *         The name of the parameter that will be used as a sorting key
     * @param sortDir
     *         The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link DeviceListResult} of all the devices associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("tagId") EntityId tagId,
            @QueryParam("clientId") String clientId,
            @QueryParam("status") DeviceConnectionStatus connectionStatus,
            @QueryParam("matchTerm") String matchTerm,
            @QueryParam("fetchAttributes") List<String> fetchAttributes,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        DeviceQuery query = new DeviceQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (tagId != null) {
            andPredicate.and(query.attributePredicate(DeviceAttributes.TAG_IDS, tagId));
        }
        if (!Strings.isNullOrEmpty(clientId)) {
            andPredicate.and(query.attributePredicate(DeviceAttributes.CLIENT_ID, clientId));
        }
        if (connectionStatus != null) {
            andPredicate.and(query.attributePredicate(DeviceAttributes.CONNECTION_STATUS, connectionStatus));
        }
        if (matchTerm != null && !matchTerm.isEmpty()) {
            andPredicate.and(query.matchPredicate(matchTerm));
        }

        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setPredicate(andPredicate);
        query.setFetchAttributes(fetchAttributes);
        query.setOffset(offset);
        query.setLimit(limit);
        query.setAskTotalCount(askTotalCount);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link DeviceQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link DeviceQuery} to use to filter results.
     * @return The {@link DeviceListResult} of all the result matching the given {@link DeviceQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public DeviceListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            DeviceQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return deviceService.query(query);
    }

    /**
     * Counts the results with the given {@link DeviceQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link DeviceQuery} to use to filter results.
     * @return The count of all the result matching the given {@link DeviceQuery} parameter.
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
            DeviceQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(deviceService.count(query));
    }

    /**
     * Creates a new Device based on the information provided in DeviceCreator parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link Device}
     * @param deviceCreator
     *         Provides the information for the new Device to be created.
     * @return The newly created Device object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            DeviceCreator deviceCreator) throws KapuaException {
        deviceCreator.setScopeId(scopeId);

        return returnCreated(deviceService.create(deviceCreator));
    }

    /**
     * Returns the Device specified by the "deviceId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link Device}.
     * @param deviceId
     *         The id of the requested Device.
     * @return The requested Device object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{deviceId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Device find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId) throws KapuaException {
        Device device = deviceService.find(scopeId, deviceId);

        return returnNotNullEntity(device, Device.TYPE, deviceId);
    }

    /**
     * Updates the Device based on the information provided in the Device parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested Device.
     * @param deviceId
     *         The id of the requested {@link Device}
     * @param device
     *         The modified Device whose attributed need to be updated.
     * @return The updated device.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @PUT
    @Path("{deviceId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Device update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            Device device) throws KapuaException {
        device.setScopeId(scopeId);
        device.setId(deviceId);

        return deviceService.update(device);
    }

    /**
     * Deletes the Device specified by the "deviceId" path parameter.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link Device}.
     * @param deviceId
     *         The id of the Device to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{deviceId}")
    public Response deleteDevice(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId) throws KapuaException {
        deviceService.delete(scopeId, deviceId);

        return returnNoContent();
    }
}
