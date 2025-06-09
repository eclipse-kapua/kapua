/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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

import javax.inject.Inject;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.MetricInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoSchema;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.SchemaKeys;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MetricInfoRepositoryImpl extends DatastoreElasticSearchRepositoryBase<MetricInfo, MetricInfoListResult, MetricInfoQuery> implements MetricInfoRepository {

    private final DatastoreUtils datastoreUtils;

    @Inject
    protected MetricInfoRepositoryImpl(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            StorablePredicateFactory storablePredicateFactory,
            DatastoreSettings datastoreSettings,
            DatastoreUtils datastoreUtils,
            DatastoreCacheManager datastoreCacheManager) {
        super(elasticsearchClientProviderInstance,
                MetricInfo.class,
                storablePredicateFactory,
                datastoreCacheManager.getMetricsCache(),
                datastoreSettings,
                scopeId -> new MetricInfoQuery(scopeId),
                () -> new MetricInfoListResult());
        this.datastoreUtils = datastoreUtils;
    }

    @Override
    protected JsonNode getIndexSchema() throws MappingException {
        return getMetricTypeSchema();
    }

    /**
     * Create and return the Json representation of the metric info schema
     *
     * @return
     * @throws MappingException
     * @since 1.0.0
     */
    public static JsonNode getMetricTypeSchema() throws MappingException {

        ObjectNode metricNode = MappingUtils.newObjectNode();

        ObjectNode sourceMetric = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_ENABLED, true) });
        metricNode.set(SchemaKeys.KEY_SOURCE, sourceMetric);

        ObjectNode propertiesNode = MappingUtils.newObjectNode();
        {
            ObjectNode metricAccount = MappingUtils.newObjectNode(
                    new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
            propertiesNode.set(MetricInfoSchema.METRIC_SCOPE_ID, metricAccount);

            ObjectNode metricClientId = MappingUtils.newObjectNode(
                    new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
            propertiesNode.set(MetricInfoSchema.METRIC_CLIENT_ID, metricClientId);

            ObjectNode metricChannel = MappingUtils.newObjectNode(
                    new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
            propertiesNode.set(MetricInfoSchema.METRIC_CHANNEL, metricChannel);

            ObjectNode metricMtrNode = MappingUtils.newObjectNode(
                    new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_OBJECT), new KeyValueEntry(SchemaKeys.KEY_ENABLED, true),
                            new KeyValueEntry(SchemaKeys.KEY_DYNAMIC, false) });
            ObjectNode metricMtrPropertiesNode = MappingUtils.newObjectNode();
            {
                ObjectNode metricMtrNameNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                metricMtrPropertiesNode.set(MetricInfoSchema.METRIC_MTR_NAME, metricMtrNameNode);

                ObjectNode metricMtrTypeNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                metricMtrPropertiesNode.set(MetricInfoSchema.METRIC_MTR_TYPE, metricMtrTypeNode);

                ObjectNode metricMtrValueNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                metricMtrPropertiesNode.set(MetricInfoSchema.METRIC_MTR_VALUE, metricMtrValueNode);

                ObjectNode metricMtrTimestampNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                metricMtrPropertiesNode.set(MetricInfoSchema.METRIC_MTR_TIMESTAMP, metricMtrTimestampNode);

                ObjectNode metricMtrMsgIdNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                metricMtrPropertiesNode.set(MetricInfoSchema.METRIC_MTR_MSG_ID, metricMtrMsgIdNode);
            }
            metricMtrNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, metricMtrPropertiesNode);

            propertiesNode.set(MetricInfoSchema.METRIC_MTR, metricMtrNode);
        }
        metricNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, propertiesNode);

        return metricNode;
    }

    @Override
    protected String indexResolver(KapuaId scopeId) {
        return datastoreUtils.getMetricIndexName(scopeId);
    }

    @Override
    protected StorableId idExtractor(MetricInfo storable) {
        return storable.getId();
    }

    @Override
    public void refreshAllIndexes() {
        super.refreshIndex(datastoreUtils.getMetricIndexName(KapuaId.ANY));
    }

    @Override
    public void deleteAllIndexes() {
        super.deleteIndexes(datastoreUtils.getMetricIndexName(KapuaId.ANY));
    }
}
