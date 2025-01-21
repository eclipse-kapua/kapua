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

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.kapua.app.api.core.model.DateParam;
import org.eclipse.kapua.app.api.core.model.MetricType;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.model.StorableEntityId;
import org.eclipse.kapua.app.api.core.model.data.JsonDatastoreMessage;
import org.eclipse.kapua.app.api.core.model.data.JsonKapuaDataMessage;
import org.eclipse.kapua.app.api.core.model.data.JsonMessageListResult;
import org.eclipse.kapua.app.api.core.model.data.JsonMessageQuery;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.app.api.resources.v1.resources.marker.JsonSerializationFixed;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataMessageFactory;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.datastore.MessageStoreFactory;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.storable.model.query.SortDirection;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.XmlAdaptedSortField;

/**
 * @see JsonSerializationFixed
 */
@Path("{scopeId}/data/messages")
public class DataMessagesJson extends AbstractKapuaResource implements JsonSerializationFixed {

    @Inject
    public KapuaDataMessageFactory kapuaDataMessageFactory;
    @Inject
    public MessageStoreFactory messageStoreFactory;
    @Inject
    public MessageStoreService messageStoreService;
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
    @Produces({ MediaType.APPLICATION_JSON })
    public <V extends Comparable<V>> JsonMessageListResult simpleQueryJson(@PathParam("scopeId") ScopeId scopeId,
            @QueryParam("clientId") List<String> clientIds,
            @QueryParam("channel") String channel,
            @QueryParam("strictChannel") boolean strictChannel,
            @QueryParam("startDate") DateParam startDateParam,
            @QueryParam("endDate") DateParam endDateParam,
            @QueryParam("metricName") String metricName,
            @QueryParam("metricType") String metricType,
            @QueryParam("metricMin") String metricMinValue,
            @QueryParam("metricMax") String metricMaxValue,
            @QueryParam("sortDir") @DefaultValue("DESC") SortDirection sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit)
            throws KapuaException {
        MetricType<V> internalMetricType = new MetricType<>(metricType);
        MessageQuery query = DataMessages.parametersToQuery(datastorePredicateFactory, messageStoreFactory, scopeId, clientIds, channel, strictChannel, startDateParam, endDateParam, metricName,
                internalMetricType, metricMinValue, metricMaxValue, sortDir, offset, limit);
        query.setScopeId(scopeId);
        final MessageListResult result = messageStoreService.query(query);

        List<JsonDatastoreMessage> jsonDatastoreMessages = new ArrayList<>();
        result.getItems().forEach(m -> jsonDatastoreMessages.add(new JsonDatastoreMessage(m)));
        JsonMessageListResult jsonResult = new JsonMessageListResult();
        jsonResult.addItems(jsonDatastoreMessages);
        jsonResult.setTotalCount(result.getTotalCount());
        jsonResult.setLimitExceeded(result.isLimitExceeded());
        return jsonResult;
    }

    /**
     * Stores a new Message under the account of the currently connected user. In this case, the provided message will only be stored in the back-end database and it will not be forwarded to the
     * message broker.
     *
     * @param jsonKapuaDataMessage
     *         The {@link KapuaDataMessage } to be stored
     * @return an {@link StorableEntityId} object encapsulating the response from the datastore
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response storeMessageJson(@PathParam("scopeId") ScopeId scopeId,
            JsonKapuaDataMessage jsonKapuaDataMessage)
            throws KapuaException {

        KapuaDataMessage kapuaDataMessage = kapuaDataMessageFactory.newKapuaDataMessage();

        kapuaDataMessage.setId(jsonKapuaDataMessage.getId());
        kapuaDataMessage.setScopeId(scopeId);
        kapuaDataMessage.setDeviceId(jsonKapuaDataMessage.getDeviceId());
        kapuaDataMessage.setClientId(jsonKapuaDataMessage.getClientId());
        kapuaDataMessage.setReceivedOn(jsonKapuaDataMessage.getReceivedOn());
        kapuaDataMessage.setSentOn(jsonKapuaDataMessage.getSentOn());
        kapuaDataMessage.setCapturedOn(jsonKapuaDataMessage.getCapturedOn());
        kapuaDataMessage.setPosition(jsonKapuaDataMessage.getPosition());
        kapuaDataMessage.setChannel(jsonKapuaDataMessage.getChannel());

        KapuaDataPayload kapuaDataPayload = kapuaDataMessageFactory.newKapuaDataPayload();

        if (jsonKapuaDataMessage.getPayload() != null) {
            kapuaDataPayload.setBody(jsonKapuaDataMessage.getPayload().getBody());

            jsonKapuaDataMessage.getPayload().getMetrics().forEach(
                    jsonMetric -> {
                        String name = jsonMetric.getName();
                        Object value = ObjectValueConverter.fromString(jsonMetric.getValue(), jsonMetric.getValueType());

                        kapuaDataPayload.getMetrics().put(name, value);
                    });
        }
        kapuaDataMessage.setPayload(kapuaDataPayload);
        kapuaDataMessage.setScopeId(scopeId);
        return returnCreated(new StorableEntityId(messageStoreService.store(kapuaDataMessage).toString()));
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
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public JsonMessageListResult queryJson(@PathParam("scopeId") ScopeId scopeId,
            JsonMessageQuery query)
            throws KapuaException {
        query.setScopeId(scopeId);

        MessageListResult result = messageStoreService.query(convertQuery(query));

        List<JsonDatastoreMessage> jsonDatastoreMessages = new ArrayList<>();
        result.getItems().forEach(m -> jsonDatastoreMessages.add(new JsonDatastoreMessage(m)));

        JsonMessageListResult jsonResult = new JsonMessageListResult();
        jsonResult.addItems(jsonDatastoreMessages);
        jsonResult.setLimitExceeded(result.isLimitExceeded());
        jsonResult.setTotalCount(result.getTotalCount());
        return jsonResult;
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
    @Produces({ MediaType.APPLICATION_JSON })
    public JsonDatastoreMessage findJson(@PathParam("scopeId") ScopeId scopeId,
            @PathParam("datastoreMessageId") StorableEntityId datastoreMessageId)
            throws KapuaException {
        DatastoreMessage datastoreMessage = returnNotNullEntity(messageStoreService.find(scopeId, datastoreMessageId, StorableFetchStyle.SOURCE_FULL));

        JsonDatastoreMessage jsonDatastoreMessage = new JsonDatastoreMessage(datastoreMessage);

        return returnNotNullEntity(jsonDatastoreMessage);
    }

    private MessageQuery convertQuery(JsonMessageQuery query) {
        MessageQuery messageQuery = new MessageQuery(query.getScopeId());
        messageQuery.setAskTotalCount(query.isAskTotalCount());
        messageQuery.setFetchAttributes(query.getFetchAttributes());
        messageQuery.setFetchStyle(query.getFetchStyle());
        messageQuery.setLimit(query.getLimit());
        messageQuery.setOffset(query.getOffset());
        messageQuery.setPredicate(query.getPredicate());

        List<SortField> sortFields = new ArrayList<>();
        if (query.getSortFields() != null) {
            for (XmlAdaptedSortField xmlAdaptedSortField : query.getSortFields()) {
                sortFields.add(SortField.of(xmlAdaptedSortField.getField(), xmlAdaptedSortField.getDirection()));
            }
        }
        messageQuery.setSortFields(sortFields);
        return messageQuery;
    }
}
