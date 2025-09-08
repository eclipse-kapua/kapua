/*******************************************************************************
 * Copyright (c) 2018, 2025 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.app.api.resources.v1.resources.marker.JsonSerializationFixed;
import org.eclipse.kapua.app.api.core.model.DateParam;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.model.StorableEntityId;
import org.eclipse.kapua.app.api.core.model.data.JsonDatastoreMessage;
import org.eclipse.kapua.app.api.core.model.data.JsonKapuaDataMessage;
import org.eclipse.kapua.app.api.core.model.data.JsonMessageListResult;
import org.eclipse.kapua.app.api.core.model.data.JsonMessageQuery;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataMessageFactory;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.datastore.MessageStoreFactory;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.storable.model.query.SortDirection;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.XmlAdaptedSortField;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @see JsonSerializationFixed
 */
@Path("{scopeId}/data/messages")
public class DataMessagesJson extends AbstractKapuaResource implements JsonSerializationFixed {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final KapuaDataMessageFactory KAPUA_DATA_MESSAGE_FACTORY = LOCATOR.getFactory(KapuaDataMessageFactory.class);

    private static final MessageStoreFactory MESSAGE_STORE_FACTORY = LOCATOR.getFactory(MessageStoreFactory.class);

    private static final DataMessages DATA_MESSAGES = new DataMessages();

    /**
     * Gets the {@link DatastoreMessage} list in the scope.
     *
     * @param scopeId        The {@link ScopeId} in which to search results.
     * @param clientId       The client id to filter results.
     * @param channel        The channel id to filter results. It allows '#' wildcard in last channel level.
     * @param strictChannel  Restrict the search only to this channel ignoring its children. Only meaningful if channel is set.
     * @param startDateParam The start date to filter the results. Must come before endDate parameter.
     * @param endDateParam   The end date to filter the results. Must come after startDate parameter
     * @param offset         The result set offset.
     * @param limit          The result set limit.
     * @return The {@link MessageListResult} of all the datastoreMessages associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public <V extends Comparable<V>> JsonMessageListResult simpleQueryJson(@PathParam("scopeId") ScopeId scopeId,
                                                                           @QueryParam("clientId") String clientId,
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

        MessageListResult result = DATA_MESSAGES.simpleQuery(
                scopeId,
                clientId,
                channel,
                strictChannel,
                startDateParam,
                endDateParam,
                metricName,
                metricType,
                metricMinValue,
                metricMaxValue,
                sortDir,
                offset,
                limit);

        List<JsonDatastoreMessage> jsonDatastoreMessages = new ArrayList<>();
        result.getItems().forEach(m -> jsonDatastoreMessages.add(new JsonDatastoreMessage(m)));

        JsonMessageListResult jsonResult = new JsonMessageListResult();
        jsonResult.addItems(jsonDatastoreMessages);
        jsonResult.setTotalCount(result.getTotalCount());
        jsonResult.setLimitExceeded(result.isLimitExceeded());

        return jsonResult;
    }

    /**
     * Stores a new Message under the account of the currently connected user.
     * In this case, the provided message will only be stored in the back-end
     * database and it will not be forwarded to the message broker.
     *
     * @param jsonKapuaDataMessage The {@link KapuaDataMessage } to be stored
     * @return an {@link StorableEntityId} object encapsulating the response from
     * the datastore
     * @throws KapuaException Whenever something bad happens. See specific
     *                        {@link KapuaService} exceptions.
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response storeMessageJson(@PathParam("scopeId") ScopeId scopeId,
                                     JsonKapuaDataMessage jsonKapuaDataMessage)
            throws KapuaException {

        KapuaDataMessage kapuaDataMessage = KAPUA_DATA_MESSAGE_FACTORY.newKapuaDataMessage();

        kapuaDataMessage.setId(jsonKapuaDataMessage.getId());
        kapuaDataMessage.setScopeId(scopeId);
        kapuaDataMessage.setDeviceId(jsonKapuaDataMessage.getDeviceId());
        kapuaDataMessage.setClientId(jsonKapuaDataMessage.getClientId());
        kapuaDataMessage.setReceivedOn(jsonKapuaDataMessage.getReceivedOn());
        kapuaDataMessage.setSentOn(jsonKapuaDataMessage.getSentOn());
        kapuaDataMessage.setCapturedOn(jsonKapuaDataMessage.getCapturedOn());
        kapuaDataMessage.setPosition(jsonKapuaDataMessage.getPosition());
        kapuaDataMessage.setChannel(jsonKapuaDataMessage.getChannel());

        KapuaDataPayload kapuaDataPayload = KAPUA_DATA_MESSAGE_FACTORY.newKapuaDataPayload();

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

        return returnCreated(DATA_MESSAGES.storeMessage(scopeId, kapuaDataMessage));
    }

    /**
     * Queries the results with the given {@link MessageQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link MessageQuery} to used to filter results.
     * @return The {@link MessageListResult} of all the result matching the given {@link MessageQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JsonMessageListResult queryJson(@PathParam("scopeId") ScopeId scopeId,
                                           JsonMessageQuery query)
            throws KapuaException {
        query.setScopeId(scopeId);

        MessageListResult result = DATA_MESSAGES.query(scopeId, convertQuery(query));

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
     * @param datastoreMessageId The id of the requested DatastoreMessage.
     * @return The requested DatastoreMessage object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{datastoreMessageId}")
    @Produces({MediaType.APPLICATION_JSON})
    public JsonDatastoreMessage findJson(@PathParam("scopeId") ScopeId scopeId,
                                         @PathParam("datastoreMessageId") StorableEntityId datastoreMessageId)
            throws KapuaException {
        DatastoreMessage datastoreMessage = DATA_MESSAGES.find(scopeId, datastoreMessageId);

        JsonDatastoreMessage jsonDatastoreMessage = new JsonDatastoreMessage(datastoreMessage);

        return DATA_MESSAGES.returnNotNullEntity(jsonDatastoreMessage);
    }

    private MessageQuery convertQuery(JsonMessageQuery query) {
        MessageQuery messageQuery = MESSAGE_STORE_FACTORY.newQuery(query.getScopeId());
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
