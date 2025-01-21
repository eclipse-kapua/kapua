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
package org.eclipse.kapua.service.datastore.internal;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.MessageStoreFactory;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.mediator.Metric;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.MessageSchema;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.SchemaKeys;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.elasticsearch.client.exception.DatamodelMappingException;
import org.eclipse.kapua.service.elasticsearch.client.model.InsertRequest;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MessageElasticsearchRepository extends DatastoreElasticSearchRepositoryBase<DatastoreMessage, MessageListResult, MessageQuery> implements MessageRepository {

    private final DatastoreUtils datastoreUtils;
    private final LocalCache<String, Map<String, Metric>> metricsByIndex;

    @Inject
    public MessageElasticsearchRepository(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            MessageStoreFactory messageStoreFactory,
            StorablePredicateFactory storablePredicateFactory,
            DatastoreSettings datastoreSettings,
            DatastoreUtils datastoreUtils,
            DatastoreCacheManager datastoreCacheManager) {
        super(elasticsearchClientProviderInstance,
                DatastoreMessage.class,
                messageStoreFactory,
                storablePredicateFactory,
                datastoreSettings,
                scopeId -> new MessageQuery(scopeId),
                () -> new MessageListResult());
        this.datastoreUtils = datastoreUtils;
        metricsByIndex = datastoreCacheManager.getMetadataCache();
    }

    @Override
    protected JsonNode getIndexSchema() {
        try {
            return getMessageTypeSchema();
        } catch (MappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected StorableId idExtractor(DatastoreMessage storable) {
        return storable.getDatastoreId();
    }

    @Override
    protected String indexResolver(KapuaId scopeId) {
        return datastoreUtils.getDataIndexName(scopeId);
    }

    protected String indexResolver(KapuaId scopeId, Long time) {
        final String indexingWindowOption = datastoreSettings.getString(DatastoreSettingsKey.INDEXING_WINDOW_OPTION, DatastoreUtils.INDEXING_WINDOW_OPTION_WEEK);
        return datastoreUtils.getDataIndexName(scopeId, time, indexingWindowOption);
    }

    /**
     * Create and return the Json representation of the message schema
     *
     * @return
     * @throws MappingException
     * @since 1.0.0
     */
    public static JsonNode getMessageTypeSchema() throws MappingException {
        ObjectNode messageNode = MappingUtils.newObjectNode();
        {
            ObjectNode sourceMessage = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_ENABLED, true) });
            messageNode.set(SchemaKeys.KEY_SOURCE, sourceMessage);

            ObjectNode propertiesNode = MappingUtils.newObjectNode();
            {
                ObjectNode messageId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(MessageSchema.MESSAGE_ID, messageId);

                ObjectNode messageTimestamp = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                propertiesNode.set(MessageSchema.MESSAGE_TIMESTAMP, messageTimestamp);

                ObjectNode messageReceivedOn = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                propertiesNode.set(MessageSchema.MESSAGE_RECEIVED_ON, messageReceivedOn);

                ObjectNode messageIp = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_IP) });
                propertiesNode.set(MessageSchema.MESSAGE_IP_ADDRESS, messageIp);

                ObjectNode messageScopeId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(MessageSchema.MESSAGE_SCOPE_ID, messageScopeId);

                ObjectNode messageDeviceId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(MessageSchema.MESSAGE_DEVICE_ID, messageDeviceId);

                ObjectNode messageClientId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(MessageSchema.MESSAGE_CLIENT_ID, messageClientId);

                ObjectNode messageChannel = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(MessageSchema.MESSAGE_CHANNEL, messageChannel);

                ObjectNode messageCapturedOn = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                propertiesNode.set(MessageSchema.MESSAGE_CAPTURED_ON, messageCapturedOn);

                ObjectNode messageSentOn = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                propertiesNode.set(MessageSchema.MESSAGE_SENT_ON, messageSentOn);

                ObjectNode positionNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_OBJECT), new KeyValueEntry(SchemaKeys.KEY_ENABLED, true),
                                new KeyValueEntry(SchemaKeys.KEY_DYNAMIC, false) });

                ObjectNode positionPropertiesNode = MappingUtils.newObjectNode();
                {
                    ObjectNode messagePositionPropLocation = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_GEO_POINT) });
                    positionPropertiesNode.set(MessageSchema.MESSAGE_POS_LOCATION, messagePositionPropLocation);

                    ObjectNode messagePositionPropAlt = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DOUBLE) });
                    positionPropertiesNode.set(MessageSchema.MESSAGE_POS_ALT, messagePositionPropAlt);

                    ObjectNode messagePositionPropPrec = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DOUBLE) });
                    positionPropertiesNode.set(MessageSchema.MESSAGE_POS_PRECISION, messagePositionPropPrec);

                    ObjectNode messagePositionPropHead = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DOUBLE) });
                    positionPropertiesNode.set(MessageSchema.MESSAGE_POS_HEADING, messagePositionPropHead);

                    ObjectNode messagePositionPropSpeed = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DOUBLE) });
                    positionPropertiesNode.set(MessageSchema.MESSAGE_POS_SPEED, messagePositionPropSpeed);

                    ObjectNode messagePositionPropTime = MappingUtils.newObjectNode(
                            new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                    positionPropertiesNode.set(MessageSchema.MESSAGE_POS_TIMESTAMP, messagePositionPropTime);

                    ObjectNode messagePositionPropSat = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_INTEGER) });
                    positionPropertiesNode.set(MessageSchema.MESSAGE_POS_SATELLITES, messagePositionPropSat);

                    ObjectNode messagePositionPropStat = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_INTEGER) });
                    positionPropertiesNode.set(MessageSchema.MESSAGE_POS_STATUS, messagePositionPropStat);
                }
                positionNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, positionPropertiesNode);

                propertiesNode.set(SchemaKeys.FIELD_NAME_POSITION, positionNode);
            }
            messageNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, propertiesNode);

            ObjectNode messageMetrics = MappingUtils.newObjectNode(
                    new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_OBJECT), new KeyValueEntry(SchemaKeys.KEY_ENABLED, true),
                            new KeyValueEntry(SchemaKeys.KEY_DYNAMIC, true) });
            propertiesNode.set(MessageSchema.MESSAGE_METRICS, messageMetrics);

            ObjectNode messageBody = MappingUtils.newObjectNode(
                    new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_BINARY), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_FALSE) });
            propertiesNode.set(MessageSchema.MESSAGE_BODY, messageBody);
        }
        return messageNode;
    }

    @Override
    public DatastoreMessage find(KapuaId scopeId, StorableId storableId, long time) {
        return this.doFind(scopeId, indexResolver(scopeId, time), storableId);
    }

    /**
     * Store a message
     *
     * @throws ClientException
     */
    @Override
    public String store(DatastoreMessage messageToStore, Map<String, Metric> metrics) throws ClientException {
        final Long messageTime = Optional.ofNullable(messageToStore.getTimestamp())
                .map(date -> date.getTime())
                .orElse(null);

        final String indexName = indexResolver(messageToStore.getScopeId(), messageTime);

        if (!metricsByIndex.containsKey(indexName)) {
            synchronized (DatastoreMessage.class) {
                doUpsertIndex(indexName);
                doUpsertMappings(indexName, metrics);
                metricsByIndex.put(indexName, metrics);
            }
        } else {
            final Map<String, Metric> newMetrics = getMessageMappingDiffs(metricsByIndex.get(indexName), metrics);
            synchronized (DatastoreMessage.class) {
                doUpsertMappings(indexName, metrics);
                metricsByIndex.get(indexName).putAll(newMetrics);
            }
        }
        final InsertRequest insertRequest = new InsertRequest(idExtractor(messageToStore).toString(), indexName, messageToStore);
        return elasticsearchClientProviderInstance.getElasticsearchClient().insert(insertRequest).getId();
    }

    private Map<String, Metric> getMessageMappingDiffs(Map<String, Metric> currentMetrics, Map<String, Metric> newMetrics) {
        final Map<String, Metric> newEntries = newMetrics.entrySet()
                .stream()
                .filter(kv -> !currentMetrics.containsKey(kv.getKey()))
                .collect(Collectors.toMap(kv -> kv.getKey(), kv -> kv.getValue()));
        return newEntries;
    }

    private void doUpsertMappings(String index, Map<String, Metric> esMetrics) {
        try {
            if (esMetrics.isEmpty()) {
                return;
            }
            final ObjectNode metricsMapping = getNewMessageMappingsBuilder(esMetrics);
            logger.trace("Sending dynamic message mappings: {}", metricsMapping);
            elasticsearchClientProviderInstance.getElasticsearchClient().putMapping(index, metricsMapping);
        } catch (ClientException | MappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(KapuaId scopeId, StorableId id, long time) {
        super.doDelete(indexResolver(scopeId, time), id);
    }

    /**
     * @param esMetrics
     * @return
     * @throws DatamodelMappingException
     * @throws KapuaException
     * @since 1.0.0
     */
    private ObjectNode getNewMessageMappingsBuilder(Map<String, Metric> esMetrics) throws MappingException {
        if (esMetrics == null || esMetrics.isEmpty()) {
            return null;
        }
        // metrics mapping container (to be added to message mapping)
        ObjectNode typeNode = MappingUtils.newObjectNode(); // root
        ObjectNode typePropertiesNode = MappingUtils.newObjectNode(); // properties
        ObjectNode metricsNode = MappingUtils.newObjectNode(); // metrics
        ObjectNode metricsPropertiesNode = MappingUtils.newObjectNode(); // properties (metric properties)
        typeNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, typePropertiesNode);
        typePropertiesNode.set(SchemaKeys.FIELD_NAME_METRICS, metricsNode);
        metricsNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, metricsPropertiesNode);

        // metrics mapping
        ObjectNode metricMapping;
        for (Map.Entry<String, Metric> esMetric : esMetrics.entrySet()) {
            Metric metric = esMetric.getValue();
            metricMapping = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_DYNAMIC, SchemaKeys.VALUE_TRUE) });

            ObjectNode metricMappingPropertiesNode = MappingUtils.newObjectNode(); // properties (inside metric name)
            ObjectNode valueMappingNode;

            switch (metric.getType()) {
            case SchemaKeys.TYPE_STRING:
                valueMappingNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                break;
            case SchemaKeys.TYPE_DATE:
                valueMappingNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                break;
            default:
                valueMappingNode = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, metric.getType()) });
                break;
            }

            metricMappingPropertiesNode.set(datastoreUtils.getClientMetricFromAcronym(metric.getType()), valueMappingNode);
            metricMapping.set(SchemaKeys.FIELD_NAME_PROPERTIES, metricMappingPropertiesNode);
            metricsPropertiesNode.set(metric.getName(), metricMapping);
        }
        return typeNode;
    }

    @Override
    public void refreshAllIndexes() {
        super.refreshIndex(datastoreUtils.getDataIndexName(KapuaId.ANY));
        this.metricsByIndex.invalidateAll();
    }

    @Override
    public void deleteAllIndexes() {
        super.deleteIndexes(datastoreUtils.getDataIndexName(KapuaId.ANY));
        this.metricsByIndex.invalidateAll();
    }

    @Override
    public void deleteIndexes(String indexExp) {
        super.deleteIndexes(indexExp);
        this.metricsByIndex.invalidateAll();
    }
}
