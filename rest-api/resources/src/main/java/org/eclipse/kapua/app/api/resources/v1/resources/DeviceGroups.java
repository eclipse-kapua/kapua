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
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupAttributes;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupCreator;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupFactory;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupListResult;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupQuery;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupService;

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

@Path("{scopeId}/deviceGroups")
public class DeviceGroups extends AbstractKapuaResource {

    @Inject
    public DeviceGroupService deviceGroupService;
    @Inject
    public DeviceGroupFactory deviceGroupFactory;

    /**
     * Gets the {@link DeviceGroup} list in the scope.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param name    The {@link DeviceGroup} name to filter results
     * @param offset  The result set offset.
     * @param limit   The result set limit.
     * @return The {@link DeviceGroupListResult} of all the device groups associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DeviceGroupListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("tagId") EntityId tagId,
            @QueryParam("name") String name,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        DeviceGroupQuery query = deviceGroupFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (tagId != null) {
            andPredicate.and(query.attributePredicate(DeviceGroupAttributes.TAG_IDS, tagId));
        }
        if (!Strings.isNullOrEmpty(name)) {
            andPredicate.and(query.attributePredicate(DeviceGroupAttributes.NAME, name));
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
     * Queries the results with the given {@link DeviceGroupQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link DeviceGroupQuery} to use to filter results.
     * @return The {@link DeviceGroupListResult} of all the result matching the given {@link DeviceGroupQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DeviceGroupListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            DeviceGroupQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return deviceGroupService.query(query);
    }

    /**
     * Counts the results with the given {@link DeviceGroupQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link DeviceGroupQuery} to use to filter results.
     * @return The count of all the result matching the given {@link DeviceGroupQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId,
            DeviceGroupQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(deviceGroupService.count(query));
    }

    /**
     * Creates a new DeviceGroup based on the information provided in DeviceGroupCreator
     * parameter.
     *
     * @param scopeId      The {@link ScopeId} in which to create the {@link DeviceGroup}
     * @param deviceGroupCreator Provides the information for the new {@link DeviceGroup} to be created.
     * @return The newly created {@link DeviceGroup} object.
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            DeviceGroupCreator deviceGroupCreator) throws KapuaException {
        deviceGroupCreator.setScopeId(scopeId);

        return returnCreated(deviceGroupService.create(deviceGroupCreator));
    }

    /**
     * Returns the DeviceGroup specified by the "deviceGroupId" path parameter.
     *
     * @param scopeId The {@link ScopeId} of the requested {@link DeviceGroup}.
     * @param deviceGroupId The id of the requested DeviceGroup.
     * @return The requested DeviceGroup object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{deviceGroupId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DeviceGroup find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceGroupId") EntityId deviceGroupId) throws KapuaException {
        DeviceGroup deviceGroup = deviceGroupService.find(scopeId, deviceGroupId);

        return returnNotNullEntity(deviceGroup, DeviceGroup.TYPE, deviceGroupId);
    }

    /**
     * Updates the DeviceGroup based on the information provided in the DeviceGroup parameter.
     *
     * @param scopeId The ScopeId of the requested {@link DeviceGroup}.
     * @param deviceGroupId The id of the requested {@link DeviceGroup}
     * @param deviceGroup   The modified Group whose attributed need to be updated.
     * @return The updated device group.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @PUT
    @Path("{deviceGroupId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DeviceGroup update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceGroupId") EntityId deviceGroupId,
            DeviceGroup deviceGroup)
        throws KapuaException {

        deviceGroup.setScopeId(scopeId);
        deviceGroup.setId(deviceGroupId);

        return deviceGroupService.update(deviceGroup);
    }

    /**
     * Deletes the DeviceGroup specified by the "deviceGroupId" path parameter.
     *
     * @param scopeId The ScopeId of the requested {@link DeviceGroup}.
     * @param deviceGroupId The id of the DeviceGroup to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{deviceGroupId}")
    public Response delete(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("deviceGroupId") EntityId deviceGroupId) throws KapuaException {
        deviceGroupService.delete(scopeId, deviceGroupId);

        return returnNoContent();
    }
}
