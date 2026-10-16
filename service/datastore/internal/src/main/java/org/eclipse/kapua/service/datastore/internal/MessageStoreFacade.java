/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.datastore.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.metric.MetricServiceFactory;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.internal.device.data.KapuaDataChannelImpl;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.exception.DatastoreDisabledException;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreChannel;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageField;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreMediator;
import org.eclipse.kapua.service.datastore.internal.mediator.Metric;
import org.eclipse.kapua.service.datastore.internal.model.DataIndexBy;
import org.eclipse.kapua.service.datastore.internal.model.DatastoreMessageImpl;
import org.eclipse.kapua.service.datastore.internal.model.MessageListResultImpl;
import org.eclipse.kapua.service.datastore.internal.model.MessageUniquenessCheck;
import org.eclipse.kapua.service.datastore.internal.model.query.ChannelInfoQueryImpl;
import org.eclipse.kapua.service.datastore.internal.model.query.ClientInfoQueryImpl;
import org.eclipse.kapua.service.datastore.internal.model.query.MessageQueryImpl;
import org.eclipse.kapua.service.datastore.internal.model.query.MetricInfoQueryImpl;
import org.eclipse.kapua.service.datastore.internal.model.query.predicate.ChannelMatchPredicateImpl;
import org.eclipse.kapua.service.datastore.internal.schema.ChannelInfoSchema;
import org.eclipse.kapua.service.datastore.internal.schema.ClientInfoSchema;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.internal.schema.Metadata;
import org.eclipse.kapua.service.datastore.internal.schema.MetricInfoSchema;
import org.eclipse.kapua.service.datastore.internal.schema.SchemaUtil;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.elasticsearch.client.exception.QueryMappingException;
import org.eclipse.kapua.service.elasticsearch.client.model.InsertRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.InsertResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.ResultList;
import org.eclipse.kapua.service.elasticsearch.client.model.TypeDescriptor;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdFactory;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.predicate.IdsPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

/**
 * Message store facade
 *
 * @since 1.0.0
 */
public final class MessageStoreFacade extends AbstractRegistryFacade {

    private static final Logger LOG = LoggerFactory.getLogger(MessageStoreFacade.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final StorableIdFactory STORABLE_ID_FACTORY = LOCATOR.getFactory(StorableIdFactory.class);
    private static final StorablePredicateFactory STORABLE_PREDICATE_FACTORY = LOCATOR.getFactory(StorablePredicateFactory.class);

    private final Counter metricMessagesAlreadyInTheDatastoreCount;

    private final MessageStoreMediator mediator;

    private static final String QUERY = "query";
    private static final String QUERY_SCOPE_ID = "query.scopeId";
    private static final String SCOPE_ID = "scopeId";

    /**
     * Constructs the message store facade
     *
     * @param confProvider
     * @param mediator
     * @since 1.0.0
     */
    public MessageStoreFacade(ConfigurationProvider confProvider, MessageStoreMediator mediator) {
        super(confProvider);

        this.mediator = mediator;

        MetricsService metricService = MetricServiceFactory.getInstance();
        metricMessagesAlreadyInTheDatastoreCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_MESSAGES, DataStoreDriverMetrics.METRIC_ALREADY_IN_THE_DATASTORE, DataStoreDriverMetrics.METRIC_COUNT);
    }

    /**
     * Store a message
     *
     * @param message
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public StorableId store(KapuaMessage<?, ?> message, String messageId, boolean newInsert)
            throws KapuaIllegalArgumentException,
            DatastoreDisabledException,
            ConfigurationException,
            ClientException, MappingException {
        ArgumentValidator.notNull(message, "message");
        ArgumentValidator.notNull(message.getScopeId(), SCOPE_ID);
        ArgumentValidator.notNull(message.getReceivedOn(), "receivedOn");
        ArgumentValidator.notNull(messageId, "messageId");

        // Define data TTL
        if (!isDatastoreServiceEnabled(message.getScopeId())) {
            throw new DatastoreDisabledException(message.getScopeId());
        }

        Date capturedOn = message.getCapturedOn();
        // Overwrite timestamp if necessary
        // Use the account service plan to determine whether we will give
        // precede to the device time
        MessageStoreConfiguration accountServicePlan = getConfigurationFromCache(message.getScopeId());
        long indexedOn = KapuaDateUtils.getKapuaSysDate().toEpochMilli();
        if (DataIndexBy.DEVICE_TIMESTAMP.equals(accountServicePlan.getDataIndexBy())) {
            if (capturedOn != null) {
                indexedOn = capturedOn.getTime();
            } else {
                LOG.debug("The account is set to use, as date indexing, the device timestamp but the device timestamp is null! Current system date will be used to indexing the message by date!");
            }
        }
        // Extract schema metadata
        Metadata schemaMetadata = mediator.getMetadata(message.getScopeId(), indexedOn);

        Date indexedOnDate = new Date(indexedOn);
        String indexName = schemaMetadata.getDataIndexName();
        TypeDescriptor typeDescriptor = new TypeDescriptor(indexName, MessageSchema.MESSAGE_TYPE_NAME);

        if (!newInsert && !MessageUniquenessCheck.NONE.equals(accountServicePlan.getMessageUniquenessCheck())) {
            DatastoreMessage datastoreMessage = MessageUniquenessCheck.FULL.equals(accountServicePlan.getMessageUniquenessCheck()) ?
                find(message.getScopeId(), STORABLE_ID_FACTORY.newStorableId(messageId), StorableFetchStyle.SOURCE_SELECT) :
                find(message.getScopeId(), indexName, STORABLE_ID_FACTORY.newStorableId(messageId), StorableFetchStyle.SOURCE_SELECT);
            if (datastoreMessage != null) {
                LOG.debug("Message with datatstore id '{}' already found", messageId);
                metricMessagesAlreadyInTheDatastoreCount.inc();
                return STORABLE_ID_FACTORY.newStorableId(messageId);
            }
        }

        // Save message (the big one)
        DatastoreMessage messageToStore = convertTo(message, messageId);
        messageToStore.setTimestamp(indexedOnDate);
        InsertRequest insertRequest = new InsertRequest(messageToStore.getDatastoreId().toString(), typeDescriptor, messageToStore);
        // Possibly update the schema with new metric mappings
        Map<String, Metric> metrics = new HashMap<>();
        if (message.getPayload() != null && message.getPayload().getMetrics() != null && !message.getPayload().getMetrics().isEmpty()) {

            Map<String, Object> messageMetrics = message.getPayload().getMetrics();
            for (Map.Entry<String, Object> messageMetric : messageMetrics.entrySet()) {
                String metricName = DatastoreUtils.normalizeMetricName(messageMetric.getKey());
                String clientMetricType = DatastoreUtils.getClientMetricFromType(messageMetric.getValue().getClass());
                Metric metric = new Metric(metricName, clientMetricType);

                // each metric is potentially a dynamic field so report it a new mapping
                String mappedName = DatastoreUtils.getMetricValueQualifier(metricName, clientMetricType);
                metrics.put(mappedName, metric);
            }
        }
        try {
            mediator.onUpdatedMappings(message.getScopeId(), indexedOn, metrics);
        } catch (KapuaException e) {
            e.printStackTrace();
        }

        InsertResponse insertResponse = getElasticsearchClient().insert(insertRequest);
        messageToStore.setDatastoreId(STORABLE_ID_FACTORY.newStorableId(insertResponse.getId()));

        //MessageInfo messageInfo = getConfigProvider().getInfo(message.getScopeId());
        mediator.onAfterMessageStore(null, messageToStore);

        return STORABLE_ID_FACTORY.newStorableId(insertResponse.getId());
    }

    /**
     * Delete message by identifier.<br>
     * <b>Be careful using this function since it doesn't guarantee the datastore consistency.<br>
     * It just deletes the message by id without checking the consistency of the registries.</b>
     *
     * @param scopeId
     * @param id
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public void delete(KapuaId scopeId, StorableId id)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException {
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(id, "id");

        if (!isDatastoreServiceEnabled(scopeId)) {
            LOG.debug("Storage not enabled for account {}, return", scopeId);
            return;
        }

        // get the index by finding the object by id
        DatastoreMessage messageToBeDeleted = find(scopeId, id, StorableFetchStyle.FIELDS);
        if (messageToBeDeleted != null) {
            Metadata schemaMetadata = null;
            try {
                schemaMetadata = mediator.getMetadata(scopeId, messageToBeDeleted.getTimestamp().getTime());
            } catch (KapuaException e) {
                e.printStackTrace();
            }
            String indexName = schemaMetadata.getDataIndexName();
            TypeDescriptor typeDescriptor = new TypeDescriptor(indexName, MessageSchema.MESSAGE_TYPE_NAME);
            getElasticsearchClient().delete(typeDescriptor, id.toString());
        } else {
            LOG.warn("Cannot find the message to be deleted. scopeId: '{}' - id: '{}'", scopeId, id);
        }
        // otherwise no message to be deleted found
    }

    /**
     * Find message by identifier
     *
     * @param scopeId
     * @param id
     * @param fetchStyle
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws QueryMappingException
     * @throws ClientException
     */
    public DatastoreMessage find(KapuaId scopeId, StorableId id, StorableFetchStyle fetchStyle) throws KapuaIllegalArgumentException, ClientException {
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        return find(scopeId, SchemaUtil.getDataIndexName(scopeId), id, fetchStyle);
    }

    /**
     * Find message by identifier
     *
     * @param scopeId
     * @param id
     * @param fetchStyle
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws QueryMappingException
     * @throws ClientException
     */
    public DatastoreMessage find(KapuaId scopeId, String indexName, StorableId id, StorableFetchStyle fetchStyle)
            throws KapuaIllegalArgumentException, ClientException {

        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(id, "id");
        ArgumentValidator.notNull(fetchStyle, "fetchStyle");

        MessageQueryImpl idsQuery = new MessageQueryImpl(scopeId);
        idsQuery.setLimit(1);

        IdsPredicate idsPredicate = STORABLE_PREDICATE_FACTORY.newIdsPredicate(MessageSchema.MESSAGE_TYPE_NAME);
        idsPredicate.addId(id);
        idsQuery.setPredicate(idsPredicate);

//        String indexName = SchemaUtil.getDataIndexName(scopeId);
        TypeDescriptor typeDescriptor = new TypeDescriptor(indexName, MessageSchema.MESSAGE_TYPE_NAME);
        return getElasticsearchClient().find(typeDescriptor, idsQuery, DatastoreMessage.class);
    }

    /**
     * Find messages matching the given query
     *
     * @param query
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws QueryMappingException
     * @throws ClientException
     */
    public MessageListResult query(MessageQuery query)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, returning empty result", query.getScopeId());
            return new MessageListResultImpl();
        }

        String dataIndexName = SchemaUtil.getDataIndexName(query.getScopeId());
        TypeDescriptor typeDescriptor = new TypeDescriptor(dataIndexName, MessageSchema.MESSAGE_TYPE_NAME);
        ResultList<DatastoreMessage> rl = getElasticsearchClient().query(typeDescriptor, query, DatastoreMessage.class);
        MessageListResult result = new MessageListResultImpl(rl);
        setLimitExceed(query, rl.getTotalHitsExceedsCount(), result);
        return result;
    }

    /**
     * Get messages count matching the given query
     *
     * @param query
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public long count(MessageQuery query)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, returning empty result", query.getScopeId());
            return 0;
        }

        String indexName = SchemaUtil.getDataIndexName(query.getScopeId());
        TypeDescriptor typeDescriptor = new TypeDescriptor(indexName, MessageSchema.MESSAGE_TYPE_NAME);
        return getElasticsearchClient().count(typeDescriptor, query);
    }

    /**
     * Delete messages count matching the given query.<br>
     * <b>Be careful using this function since it doesn't guarantee the datastore consistency.<br>
     * It just deletes the messages that matching the query without checking the consistency of the registries.</b>
     *
     * @param query
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public void delete(MessageQuery query)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, skipping delete", query.getScopeId());
            return;
        }

        String indexName = SchemaUtil.getDataIndexName(query.getScopeId());
        TypeDescriptor typeDescriptor = new TypeDescriptor(indexName, MessageSchema.MESSAGE_TYPE_NAME);
        getElasticsearchClient().deleteByQuery(typeDescriptor, query);
    }

    // TODO cache will not be reset from the client code it should be automatically reset
    // after some time.
    private void resetCache(KapuaId scopeId, KapuaId deviceId, String channel, String clientId) throws Exception {

        boolean isAnyClientId;
        boolean isClientToDelete = false;
        String semTopic;

        if (channel != null) {

            // determine if we should delete an client if topic = account/clientId/#
            isAnyClientId = isAnyClientId(channel);
            semTopic = channel;

            if (semTopic.isEmpty() && !isAnyClientId) {
                isClientToDelete = true;
            }
        } else {
            isClientToDelete = true;
        }

        // Find all topics
        String dataIndexName = SchemaUtil.getDataIndexName(scopeId);

        int pageSize = 1000;
        int offset = 0;
        long totalHits = 1;

        MetricInfoQueryImpl metricQuery = new MetricInfoQueryImpl(scopeId);
        metricQuery.setLimit(pageSize + 1);
        metricQuery.setOffset(offset);

        ChannelMatchPredicateImpl channelPredicate = new ChannelMatchPredicateImpl(MessageField.CHANNEL, channel);
        metricQuery.setPredicate(channelPredicate);

        // Remove metrics
        while (totalHits > 0) {
            TypeDescriptor typeDescriptor = new TypeDescriptor(dataIndexName, MetricInfoSchema.METRIC_TYPE_NAME);
            ResultList<MetricInfo> metrics = getElasticsearchClient().query(typeDescriptor, metricQuery, MetricInfo.class);

            totalHits = metrics.getTotalCount();
            LocalCache<String, Boolean> metricsCache = DatastoreCacheManager.getInstance().getMetricsCache();
            long toBeProcessed = totalHits > pageSize ? pageSize : totalHits;

            for (int i = 0; i < toBeProcessed; i++) {
                String id = metrics.getResult().get(i).getId().toString();
                if (metricsCache.get(id)) {
                    metricsCache.remove(id);
                }
            }

            if (totalHits > pageSize) {
                offset += pageSize + 1;
            }
        }
        LOG.debug("Removed cached channel metrics for: {}", channel);
        TypeDescriptor typeMetricDescriptor = new TypeDescriptor(dataIndexName, MetricInfoSchema.METRIC_TYPE_NAME);
        getElasticsearchClient().deleteByQuery(typeMetricDescriptor, metricQuery);
        LOG.debug("Removed channel metrics for: {}", channel);
        ChannelInfoQueryImpl channelQuery = new ChannelInfoQueryImpl(scopeId);
        channelQuery.setLimit(pageSize + 1);
        channelQuery.setOffset(offset);

        channelPredicate = new ChannelMatchPredicateImpl(MessageField.CHANNEL, channel);
        channelQuery.setPredicate(channelPredicate);

        // Remove channel
        offset = 0;
        totalHits = 1;
        while (totalHits > 0) {
            TypeDescriptor typeDescriptor = new TypeDescriptor(dataIndexName, ChannelInfoSchema.CHANNEL_TYPE_NAME);
            ResultList<ChannelInfo> channels = getElasticsearchClient().query(typeDescriptor, channelQuery, ChannelInfo.class);

            totalHits = channels.getTotalCount();
            LocalCache<String, Boolean> channelsCache = DatastoreCacheManager.getInstance().getChannelsCache();
            long toBeProcessed = totalHits > pageSize ? pageSize : totalHits;

            for (int i = 0; i < toBeProcessed; i++) {
                String id = channels.getResult().get(0).getId().toString();
                if (channelsCache.get(id)) {
                    channelsCache.remove(id);
                }
            }
            if (totalHits > pageSize) {
                offset += pageSize + 1;
            }
        }

        LOG.debug("Removed cached channels for: {}", channel);
        TypeDescriptor typeChannelDescriptor = new TypeDescriptor(dataIndexName, ChannelInfoSchema.CHANNEL_TYPE_NAME);
        getElasticsearchClient().deleteByQuery(typeChannelDescriptor, channelQuery);

        LOG.debug("Removed channels for: {}", channel);
        // Remove client
        if (isClientToDelete) {
            ClientInfoQueryImpl clientInfoQuery = new ClientInfoQueryImpl(scopeId);
            clientInfoQuery.setLimit(pageSize + 1);
            clientInfoQuery.setOffset(offset);

            channelPredicate = new ChannelMatchPredicateImpl(MessageField.CHANNEL, channel);
            clientInfoQuery.setPredicate(channelPredicate);
            offset = 0;
            totalHits = 1;
            while (totalHits > 0) {
                TypeDescriptor typeDescriptor = new TypeDescriptor(dataIndexName, ClientInfoSchema.CLIENT_TYPE_NAME);
                ResultList<ClientInfo> clients = getElasticsearchClient().query(typeDescriptor, clientInfoQuery, ClientInfo.class);

                totalHits = clients.getTotalCount();
                LocalCache<String, Boolean> clientsCache = DatastoreCacheManager.getInstance().getClientsCache();
                long toBeProcessed = totalHits > pageSize ? pageSize : totalHits;

                for (int i = 0; i < toBeProcessed; i++) {
                    String id = clients.getResult().get(i).getId().toString();
                    if (clientsCache.get(id)) {
                        clientsCache.remove(id);
                    }
                }
                if (totalHits > pageSize) {
                    offset += pageSize + 1;
                }
            }

            LOG.debug("Removed cached clients for: {}", channel);
            TypeDescriptor typeClientDescriptor = new TypeDescriptor(dataIndexName, ClientInfoSchema.CLIENT_TYPE_NAME);
            getElasticsearchClient().deleteByQuery(typeClientDescriptor, clientInfoQuery);

            LOG.debug("Removed clients for: {}", channel);
        }
    }

    // Utility methods

    /**
     * Check if the channel admit any client identifier (so if the channel has a specific wildcard in the second topic level).<br>
     * In the MQTT word this method return true if the topic starts with 'account/+/'.
     *
     * @param clientId
     * @return
     * @since 1.0.0
     */
    private boolean isAnyClientId(String clientId) {
        return DatastoreChannel.SINGLE_LEVEL_WCARD.equals(clientId);
    }

    /**
     * This constructor should be used for wrapping Kapua message into datastore message for insert purpose
     *
     * @param message
     */
    private DatastoreMessage convertTo(KapuaMessage<?, ?> message, String messageId) {
        KapuaDataChannel datastoreChannel = new KapuaDataChannelImpl();
        datastoreChannel.setSemanticParts(message.getChannel().getSemanticParts());

        DatastoreMessage datastoreMessage = new DatastoreMessageImpl();
        datastoreMessage.setCapturedOn(message.getCapturedOn());
        datastoreMessage.setChannel(datastoreChannel);
        datastoreMessage.setClientId(message.getClientId());
        datastoreMessage.setDeviceId(message.getDeviceId());
        datastoreMessage.setId(message.getId());
        datastoreMessage.setPayload(message.getPayload());
        datastoreMessage.setPosition(message.getPosition());
        datastoreMessage.setReceivedOn(message.getReceivedOn());
        datastoreMessage.setScopeId(message.getScopeId());
        datastoreMessage.setSentOn(message.getSentOn());

        // generate uuid
        datastoreMessage.setId(message.getId());
        datastoreMessage.setDatastoreId(STORABLE_ID_FACTORY.newStorableId(messageId));
        return datastoreMessage;
    }

    public void refreshAllIndexes() throws ClientException {
        getElasticsearchClient().refreshAllIndexes();
    }

    public void deleteAllIndexes() throws ClientException {
        getElasticsearchClient().deleteAllIndexes();
    }

    public void deleteIndexes(String indexExp) throws ClientException {
        getElasticsearchClient().deleteIndexes(indexExp);
    }
}
