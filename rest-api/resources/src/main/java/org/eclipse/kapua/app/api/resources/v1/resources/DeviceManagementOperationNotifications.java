/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationAttributes;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationListResult;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationQuery;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationService;
import org.eclipse.kapua.service.device.registry.Device;

import com.google.common.base.Strings;

@Path("{scopeId}/devices/{deviceId}/operations/{operationId}/notifications")
public class DeviceManagementOperationNotifications extends AbstractKapuaResource {

    @Inject
    public ManagementOperationNotificationService managementOperationNotificationService;
    @Inject
    public ManagementOperationNotificationFactory managementOperationNotificationFactory;

    /**
     * Gets the {@link ManagementOperationNotification} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param operationId
     *         The id of the {@link Device} in which to search results
     * @param resource
     *         The resource of the {@link ManagementOperationNotification} in which to search results
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link ManagementOperationNotificationListResult} of all the ManagementOperationNotifications associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ManagementOperationNotificationListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @PathParam("operationId") EntityId operationId,
            @QueryParam("resource") String resource,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        ManagementOperationNotificationQuery query = managementOperationNotificationFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(query.attributePredicate(ManagementOperationNotificationAttributes.OPERATION_ID, operationId));

        if (!Strings.isNullOrEmpty(resource)) {
            andPredicate.and(query.attributePredicate(ManagementOperationNotificationAttributes.RESOURCE, resource));
        }

        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }

        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);
        query.setAskTotalCount(askTotalCount);

        return query(scopeId, deviceId, operationId, query);
    }

    /**
     * Queries the results with the given {@link ManagementOperationNotificationQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param operationId
     *         The id of the {@link Device} in which to search results
     * @param query
     *         The {@link ManagementOperationNotificationQuery} to use to filter results.
     * @return The {@link ManagementOperationNotificationListResult} of all the result matching the given {@link ManagementOperationNotificationQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public ManagementOperationNotificationListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @PathParam("operationId") EntityId operationId,
            ManagementOperationNotificationQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        andPredicate.and(query.attributePredicate(ManagementOperationNotificationAttributes.OPERATION_ID, operationId));
        query.setPredicate(andPredicate);

        return managementOperationNotificationService.query(query);
    }

    /**
     * Counts the results with the given {@link ManagementOperationNotificationQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param operationId
     *         The id of the {@link Device} in which to search results
     * @param query
     *         The {@link ManagementOperationNotificationQuery} to use to filter results.
     * @return The count of all the result matching the given {@link ManagementOperationNotificationQuery} parameter.
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
            @PathParam("deviceId") EntityId deviceId,
            @PathParam("operationId") EntityId operationId,
            ManagementOperationNotificationQuery query) throws KapuaException {
        query.setScopeId(scopeId);
        query.setPredicate(query.attributePredicate(ManagementOperationNotificationAttributes.OPERATION_ID, operationId));

        return new CountResult(managementOperationNotificationService.count(query));
    }

    /**
     * Returns the ManagementOperationNotification specified by the "ManagementOperationNotificationId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link ManagementOperationNotification}.
     * @param operationId
     *         The {@link Device} id of the request {@link ManagementOperationNotification}.
     * @param managementOperationNotificationId
     *         The id of the requested ManagementOperationNotification.
     * @return The requested ManagementOperationNotification object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{managementOperationNotificationId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ManagementOperationNotification find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @PathParam("operationId") EntityId operationId,
            @PathParam("managementOperationNotificationId") EntityId managementOperationNotificationId) throws KapuaException {
        //TODO: #LAYER_VIOLATION - findFirst should be resolved in bottom layer
        ManagementOperationNotificationQuery query = managementOperationNotificationFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate(
                query.attributePredicate(ManagementOperationNotificationAttributes.OPERATION_ID, operationId),
                query.attributePredicate(KapuaEntityAttributes.ENTITY_ID, managementOperationNotificationId)
        );

        query.setPredicate(andPredicate);
        query.setOffset(0);
        query.setLimit(1);

        ManagementOperationNotificationListResult results = managementOperationNotificationService.query(query);

        return returnNotNullEntity(results.getFirstItem(), ManagementOperationNotification.TYPE, managementOperationNotificationId);
    }

    /**
     * Deletes the ManagementOperationNotification specified by the "ManagementOperationNotificationId" path parameter.
     *
     * @param operationId
     *         The id of the Device in which to delete the ManagementOperation
     * @param managementOperationNotificationId
     *         The id of the ManagementOperationNotification to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{managementOperationNotificationId}")
    public Response deleteManagementOperationNotification(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceId") EntityId deviceId,
            @PathParam("operationId") EntityId operationId,
            @PathParam("managementOperationNotificationId") EntityId managementOperationNotificationId) throws KapuaException {
        managementOperationNotificationService.delete(scopeId, managementOperationNotificationId);

        return returnNoContent();
    }
}
