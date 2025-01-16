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
import java.util.Date;
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
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.DateParam;
import org.eclipse.kapua.app.api.core.model.MetricType;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.model.StorableEntityId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.datastore.MessageStoreFactory;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.internal.mediator.ChannelInfoField;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageField;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.elasticsearch.client.model.InsertResponse;
import org.eclipse.kapua.service.storable.model.query.SortDirection;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.OrPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicate;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/data/messages")
@Tag(name = "Data Message")
public class DataMessages extends AbstractKapuaResource {

    @Inject
    public MessageStoreService messageStoreService;
    @Inject
    public MessageStoreFactory messageStoreFactory;
    @Inject
    public DatastorePredicateFactory datastorePredicateFactory;

    /**
     * Gets the {@link DatastoreMessage} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param clientIds
     *         The client id(s) to filter results.
     * @param channel
     *         The channel id to filter results. It allows '#' wildcard in last channel level.
     * @param strictChannel
     *         Restrict the search only to this channel ignoring its children. Only meaningful if channel is set.
     * @param startDateParam
     *         The start date to filter the results. Must come before endDate parameter.
     * @param endDateParam
     *         The end date to filter the results. Must come after startDate parameter
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link MessageListResult} of all the datastoreMessages associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML })
    @Operation(summary = "Query the Data Messages")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
            content = @Content(schema = @Schema(implementation = MessageListResult.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public <V extends Comparable<V>> MessageListResult simpleQuery(@PathParam("scopeId") ScopeId scopeId,
       @Parameter(description = "The ClientID to use as a filter for messages")
       @QueryParam("clientId") List<String> clientIds,
       @Parameter(description = "The Channel to use as a filter for messages")
       @QueryParam("channel") String channel,
       @Parameter(description = "Restrict the search only to this channel ignoring its children. Only meaningful if channel is set")
       @QueryParam("strictChannel") boolean strictChannel,
       @Parameter(description = "The start date to filter the results. Must come before endDate parameter")
       @QueryParam("startDate") DateParam startDateParam,
       @Parameter(description = "The end date to filter the results. Must come after startDate parameter")
       @QueryParam("endDate") DateParam endDateParam,
       @Parameter(description = "The metric name to filter results. If filled, `metricType`, `metricMinValue` and `metricMaxValue` are also required")
       @QueryParam("metricName") String metricName,
       @Parameter(description = "The metric type to filter results")
       @QueryParam("metricType") String metricType,
       @Parameter(description = "The minimum metric value to filter results")
       @QueryParam("metricMin") String metricMinValue,
       @Parameter(description = "The maximum metric value to filter results")
       @QueryParam("metricMax") String metricMaxValue,
       @Parameter(description = "The sort direction. Can be ascending or descending (default)." + "\n" +
                                    "Available values : ASC, DESC")
       @QueryParam("sortDir") @DefaultValue("DESC") SortDirection sortDir,
       @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
       @QueryParam("offset") @DefaultValue("0") int offset,
       @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
       @QueryParam("limit") @DefaultValue("50") int limit)
            throws KapuaException {
        MetricType<V> internalMetricType = new MetricType<>(metricType);
        MessageQuery query = parametersToQuery(datastorePredicateFactory, messageStoreFactory, scopeId, clientIds, channel, strictChannel, startDateParam, endDateParam, metricName, internalMetricType,
                metricMinValue, metricMaxValue, sortDir, offset, limit);

        return query(scopeId, query);
    }

    /**
     * Stores a new Message under the account of the currently connected user. In this case, the provided message will only be stored in the back-end database and it will not be forwarded to the
     * message broker.
     *
     * @param message
     *         The {@link KapuaDataMessage } to be stored
     * @return an {@link InsertResponse} object encapsulating the response from the datastore
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_XML })
    @Operation(summary = "Store a new Message")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The ID of the new DataMessage",
            content = @Content(schema = @Schema(
                name = "dataMessageInsertResponse",
                example = "{\"id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\"}"
            ))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Response storeMessage(@PathParam("scopeId") ScopeId scopeId,
            KapuaDataMessage message)
            throws KapuaException {
        message.setScopeId(scopeId);
        return returnCreated(new StorableEntityId(messageStoreService.store(message).toString()));
    }

    /**
     * Queries the results with the given {@link MessageQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link MessageQuery} to used to filter results.
     * @return The {@link MessageListResult} of all the result matching the given {@link MessageQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML })
    public MessageListResult query(@PathParam("scopeId") ScopeId scopeId,
            MessageQuery query)
            throws KapuaException {
        query.setScopeId(scopeId);

        return messageStoreService.query(query);
    }

    /**
     * Counts the results with the given {@link MessageQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link MessageQuery} to used to filter results.
     * @return The count of all the result matching the given {@link MessageQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Operation(summary = "Query the Data Messages")
    public CountResult count(@PathParam("scopeId") ScopeId scopeId,
            MessageQuery query)
            throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(messageStoreService.count(query));
    }

    /**
     * Returns the DatastoreMessage specified by the "datastoreMessageId" path parameter.
     *
     * @param datastoreMessageId
     *         The id of the requested DatastoreMessage.
     * @return The requested DatastoreMessage object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{datastoreMessageId}")
    @Produces({ MediaType.APPLICATION_XML })
    public DatastoreMessage find(@PathParam("scopeId") ScopeId scopeId,
            @PathParam("datastoreMessageId") StorableEntityId datastoreMessageId)
            throws KapuaException {
        DatastoreMessage datastoreMessage = messageStoreService.find(scopeId, datastoreMessageId, StorableFetchStyle.SOURCE_FULL);

        return returnNotNullEntity(datastoreMessage);
    }

    //TODO: move this logic within the service, or at least in a collaborator shared with DataMessagesJson
    protected static <V extends Comparable<V>> MessageQuery parametersToQuery(
            DatastorePredicateFactory datastorePredicateFactory,
            MessageStoreFactory messageStoreFactory,
            ScopeId scopeId,
            List<String> clientIds,
            String channel,
            boolean strictChannel,
            DateParam startDateParam,
            DateParam endDateParam,
            String metricName,
            MetricType<V> metricType,
            String metricMinValue,
            String metricMaxValue,
            SortDirection sortDir,
            int offset,
            int limit) throws KapuaIllegalNullArgumentException {
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        final List<StorablePredicate> clientPredicates = Optional.ofNullable(clientIds)
                .orElse(new ArrayList<>())
                .stream()
                .filter(v -> !Strings.isNullOrEmpty(v))
                .map(clientId -> datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, clientId))
                .collect(Collectors.toList());

        if (!clientPredicates.isEmpty()) {
            final OrPredicate orPredicate = datastorePredicateFactory.newOrPredicate();
            orPredicate.setPredicates(clientPredicates);
            andPredicate.addPredicate(orPredicate);
        }
        if (!Strings.isNullOrEmpty(channel)) {
            andPredicate.getPredicates().add(getChannelPredicate(datastorePredicateFactory, channel, strictChannel));
        }

        Date startDate = startDateParam != null ? startDateParam.getDate() : null;
        Date endDate = endDateParam != null ? endDateParam.getDate() : null;
        if (startDate != null || endDate != null) {
            RangePredicate timestampPredicate = datastorePredicateFactory.newRangePredicate(ChannelInfoField.TIMESTAMP, startDate, endDate);
            andPredicate.getPredicates().add(timestampPredicate);
        }

        if (!Strings.isNullOrEmpty(metricName)) {
            andPredicate.getPredicates().add(getMetricPredicate(datastorePredicateFactory, metricName, metricType, metricMinValue, metricMaxValue));
        }

        MessageQuery query = messageStoreFactory.newQuery(scopeId);
        query.setPredicate(andPredicate);
        query.setOffset(offset);
        query.setLimit(limit);

        List<SortField> sort = new ArrayList<>();
        sort.add(SortField.of(MessageSchema.MESSAGE_TIMESTAMP, sortDir));
        query.setSortFields(sort);
        return query;
    }

    private static StorablePredicate getChannelPredicate(DatastorePredicateFactory datastorePredicateFactory, String channel, boolean strictChannel) {
        StorablePredicate channelPredicate;
        if (strictChannel) {
            channelPredicate = datastorePredicateFactory.newTermPredicate(ChannelInfoField.CHANNEL, channel);
        } else {
            channelPredicate = datastorePredicateFactory.newChannelMatchPredicate(channel);
        }
        return channelPredicate;
    }

    private static <V extends Comparable<V>> StorablePredicate getMetricPredicate(DatastorePredicateFactory datastorePredicateFactory, String metricName, MetricType<V> metricType,
            String metricMinValue, String metricMaxValue) throws KapuaIllegalNullArgumentException {
        if (metricMinValue == null && metricMaxValue == null) {
            Class<V> type = metricType != null ? metricType.getType() : null;
            return datastorePredicateFactory.newMetricExistsPredicate(metricName, type);
        } else {
            if (metricType == null) {
                throw new KapuaIllegalNullArgumentException("metricType");
            }
            V minValue = (V) ObjectValueConverter.fromString(metricMinValue, metricType.getType());
            V maxValue = (V) ObjectValueConverter.fromString(metricMaxValue, metricType.getType());

            return datastorePredicateFactory.newMetricPredicate(metricName, metricType.getType(), minValue, maxValue);
        }
    }
}
