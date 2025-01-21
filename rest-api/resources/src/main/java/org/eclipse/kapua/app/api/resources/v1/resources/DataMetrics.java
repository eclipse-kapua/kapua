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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.model.StorableEntityId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.service.datastore.MetricInfoFactory;
import org.eclipse.kapua.service.datastore.MetricInfoRegistryService;
import org.eclipse.kapua.service.datastore.internal.model.query.predicate.ChannelMatchPredicateImpl;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.MetricInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoField;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.ChannelMatchPredicate;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.OrPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.TermPredicate;

import com.google.common.base.Strings;

@Path("{scopeId}/data/metrics")
public class DataMetrics extends AbstractKapuaResource {

    @Inject
    public MetricInfoRegistryService metricInfoRegistryService;
    @Inject
    public MetricInfoFactory metricInfoFactory;
    @Inject
    public DatastorePredicateFactory datastorePredicateFactory;

    /**
     * Gets the {@link MetricInfo} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param clientIds
     *         The client id(s) to filter results.
     * @param channel
     *         The channel id to filter results. It allows '#' wildcard in last channel level
     * @param name
     *         The metric name to filter results
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link MetricInfoListResult} of all the metricInfos associated to the current selected scope.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MetricInfoListResult simpleQuery(@PathParam("scopeId") ScopeId scopeId,
            @QueryParam("clientId") List<String> clientIds,
            @QueryParam("channel") String channel,
            @QueryParam("name") String name,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit)
            throws KapuaException {
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        final List<StorablePredicate> clientPredicates = Optional.ofNullable(clientIds)
                .orElse(new ArrayList<>())
                .stream()
                .filter(v -> !Strings.isNullOrEmpty(v))
                .map(clientId -> datastorePredicateFactory.newTermPredicate(MetricInfoField.CLIENT_ID, clientId))
                .collect(Collectors.toList());

        if (!clientPredicates.isEmpty()) {
            final OrPredicate orPredicate = datastorePredicateFactory.newOrPredicate();
            orPredicate.setPredicates(clientPredicates);
            andPredicate.addPredicate(orPredicate);
        }

        if (!Strings.isNullOrEmpty(channel)) {
            ChannelMatchPredicate channelPredicate = new ChannelMatchPredicateImpl(channel);
            andPredicate.getPredicates().add(channelPredicate);
        }

        if (!Strings.isNullOrEmpty(name)) {
            TermPredicate clientIdPredicate = datastorePredicateFactory.newTermPredicate(MetricInfoField.NAME_FULL, name);
            andPredicate.getPredicates().add(clientIdPredicate);
        }

        MetricInfoQuery query = new MetricInfoQuery(scopeId);
        query.setPredicate(andPredicate);
        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link MetricInfoQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link MetricInfoQuery} to used to filter results.
     * @return The {@link MetricInfoListResult} of all the result matching the given {@link MetricInfoQuery} parameter.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public MetricInfoListResult query(@PathParam("scopeId") ScopeId scopeId,
            MetricInfoQuery query)
            throws KapuaException {
        query.setScopeId(scopeId);
        query.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());
        return metricInfoRegistryService.query(query);
    }

    /**
     * Counts the results with the given {@link MetricInfoQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link MetricInfoQuery} to used to filter results.
     * @return The count of all the result matching the given {@link MetricInfoQuery} parameter.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public CountResult count(@PathParam("scopeId") ScopeId scopeId,
            MetricInfoQuery query)
            throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(metricInfoRegistryService.count(query));
    }

    /**
     * Returns the MetricInfo specified by the "metricInfoId" path parameter.
     *
     * @param metricInfoId
     *         The id of the requested MetricInfo.
     * @return The requested MetricInfo object.
     */
    @GET
    @Path("{metricInfoId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MetricInfo find(@PathParam("scopeId") ScopeId scopeId,
            @PathParam("metricInfoId") StorableEntityId metricInfoId)
            throws KapuaException {
        MetricInfo metricInfo = metricInfoRegistryService.find(scopeId, metricInfoId);

        return returnNotNullEntity(metricInfo);
    }
}
