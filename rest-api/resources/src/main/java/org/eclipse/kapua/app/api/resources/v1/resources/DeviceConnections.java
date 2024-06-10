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
import org.eclipse.kapua.commons.rest.model.CountResult;
import org.eclipse.kapua.commons.rest.model.EntityId;
import org.eclipse.kapua.commons.rest.model.ScopeId;
import org.eclipse.kapua.commons.rest.model.SetResult;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionAttributes;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionListResult;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{scopeId}/deviceconnections")
public class DeviceConnections extends AbstractKapuaResource {

    @Inject
    public DeviceConnectionFactory deviceConnectionFactory;
    @Inject
    public DeviceConnectionService deviceConnectionService;

    /**
     * Gets the {@link DeviceConnection} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param clientId
     *         The id of the {@link Device} in which to search results
     * @param status
     *         The {@link DeviceConnectionStatus} in which to search results
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link DeviceConnectionListResult} of all the deviceConnections associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceConnectionListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("clientId") String clientId,
            @QueryParam("clientIp") String clientIp,
            @QueryParam("protocol") String protocol,
            @QueryParam("status") DeviceConnectionStatus status,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        DeviceConnectionQuery query = deviceConnectionFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (!Strings.isNullOrEmpty(clientId)) {
            andPredicate.and(query.attributePredicate(DeviceConnectionAttributes.CLIENT_ID, clientId));
        }
        if (!Strings.isNullOrEmpty(clientIp)) {
            andPredicate.and(query.attributePredicate(DeviceConnectionAttributes.CLIENT_IP, clientIp));
        }
        if (!Strings.isNullOrEmpty(protocol)) {
            andPredicate.and(query.attributePredicate(DeviceConnectionAttributes.PROTOCOL, protocol));
        }
        if (status != null) {
            andPredicate.and(query.attributePredicate(DeviceConnectionAttributes.STATUS, status));
        }
        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link DeviceConnectionQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link DeviceConnectionQuery} to use to filter results.
     * @return The {@link DeviceConnectionListResult} of all the result matching the given {@link DeviceConnectionQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public DeviceConnectionListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            DeviceConnectionQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return deviceConnectionService.query(query);
    }

    /**
     * Counts the results with the given {@link DeviceConnectionQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link DeviceConnectionQuery} to use to filter results.
     * @return The count of all the result matching the given {@link DeviceConnectionQuery} parameter.
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
            DeviceConnectionQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(deviceConnectionService.count(query));
    }

    /**
     * Returns the DeviceConnection specified by the "deviceConnectionId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link DeviceConnection}.
     * @param deviceConnectionId
     *         The id of the requested DeviceConnection.
     * @return The requested DeviceConnection object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{deviceConnectionId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceConnection find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceConnectionId") EntityId deviceConnectionId) throws KapuaException {
        DeviceConnection deviceConnection = deviceConnectionService.find(scopeId, deviceConnectionId);

        return returnNotNullEntity(deviceConnection, DeviceConnection.TYPE, deviceConnectionId);
    }

    /**
     * Request that the DeviceConnection specified by the "deviceConnectionId" is disconnected from the broker.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link DeviceConnection}.
     * @param deviceConnectionId
     *         The id of the requested DeviceConnection.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.0.0
     */
    @POST
    @Path("{deviceConnectionId}/_disconnect")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response disconnect(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceConnectionId") EntityId deviceConnectionId) throws KapuaException {
        deviceConnectionService.disconnect(scopeId, deviceConnectionId);
        return returnNoContent();
    }

    @GET
    @Path("_availableAuth")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SetResult getAvailableAuthAdapter() {
        return new SetResult(deviceConnectionService.getAvailableAuthTypes());
    }
}
