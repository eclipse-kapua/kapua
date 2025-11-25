/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.MessageStoreFactory;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.mediator.Metric;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageElasticsearchRepository extends DatastoreElasticSearchRepositoryBase<DatastoreMessage, MessageListResult, MessageQuery> implements MessageRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ClientInfoRegistryFacadeImpl.class);
    private final DatastoreUtils datastoreUtils;
    private final LocalCache<String, Map<String, Metric>> metricsByIndex;
    private final Object metadataUpdateSync = new Object();

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
                datastoreCacheManager.getIndexCache(),
                datastoreSettings);
        this.datastoreUtils = datastoreUtils;
        metricsByIndex = datastoreCacheManager.getMetadataCache();
    }

    @Override
    protected JsonNode getIndexSchema() {
        try {
            return MessageSchema.getMessageTypeSchema();
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
            synchronized (metadataUpdateSync) {
                doUpsertIndex(indexName);
                doUpsertMappings(indexName, metrics);
                metricsByIndex.put(indexName, metrics);
            }
        } else {
            final Map<String, Metric> newMetrics = getMessageMappingDiffs(metricsByIndex.get(indexName), metrics);
            if(newMetrics != null && newMetrics.size() > 0) {
                synchronized (metadataUpdateSync) {
                    doUpsertMappings(indexName, metrics);
                    metricsByIndex.get(indexName).putAll(newMetrics);
                }
            } else {
                LOG.debug("No mapping updates for index {}", indexName);
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
            metricMapping = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_DYNAMIC, SchemaKeys.VALUE_TRUE)});

            ObjectNode metricMappingPropertiesNode = MappingUtils.newObjectNode(); // properties (inside metric name)
            ObjectNode valueMappingNode;

            switch (metric.getType()) {
                case SchemaKeys.TYPE_STRING:
                    valueMappingNode = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                    break;
                case SchemaKeys.TYPE_DATE:
                    valueMappingNode = MappingUtils.newObjectNode(
                            new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                    break;
                default:
                    valueMappingNode = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, metric.getType())});
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
        super.refreshAllIndexes();
        this.metricsByIndex.invalidateAll();
    }

    @Override
    public void deleteAllIndexes() {
        super.deleteAllIndexes();
        this.metricsByIndex.invalidateAll();
    }

    @Override
    public void deleteIndexes(String indexExp) {
        super.deleteIndexes(indexExp);
        this.metricsByIndex.invalidateAll();
    }
}
