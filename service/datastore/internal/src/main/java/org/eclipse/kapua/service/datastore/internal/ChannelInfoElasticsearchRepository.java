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
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoSchema;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.SchemaKeys;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChannelInfoElasticsearchRepository extends DatastoreElasticSearchRepositoryBase<ChannelInfo, ChannelInfoListResult, ChannelInfoQuery> implements ChannelInfoRepository {

    private final DatastoreUtils datastoreUtils;

    @Inject
    protected ChannelInfoElasticsearchRepository(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            StorablePredicateFactory storablePredicateFactory,
            DatastoreSettings datastoreSettings,
            DatastoreUtils datastoreUtils,
            DatastoreCacheManager datastoreCacheManager) {
        super(elasticsearchClientProviderInstance,
                ChannelInfo.class,
                storablePredicateFactory,
                datastoreCacheManager.getChannelsCache(),
                datastoreSettings,
                scopeId -> new ChannelInfoQuery(scopeId),
                () -> new ChannelInfoListResult());
        this.datastoreUtils = datastoreUtils;
    }

    @Override
    protected StorableId idExtractor(ChannelInfo storable) {
        return storable.getId();
    }

    @Override
    protected String indexResolver(KapuaId scopeId) {
        return datastoreUtils.getChannelIndexName(scopeId);
    }

    @Override
    protected JsonNode getIndexSchema() throws MappingException {
        return getChannelTypeSchema();
    }

    /**
     * Create and return the Json representation of the channel info schema
     *
     * @return
     * @throws MappingException
     * @since 1.0.0
     */
    public static JsonNode getChannelTypeSchema() throws MappingException {
        ObjectNode channelNode = MappingUtils.newObjectNode();

        {
            ObjectNode sourceChannel = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_ENABLED, true) });
            channelNode.set(SchemaKeys.KEY_SOURCE, sourceChannel);

            ObjectNode propertiesNode = MappingUtils.newObjectNode();
            {
                ObjectNode channelScopeId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(ChannelInfoSchema.CHANNEL_SCOPE_ID, channelScopeId);

                ObjectNode channelClientId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(ChannelInfoSchema.CHANNEL_CLIENT_ID, channelClientId);

                ObjectNode channelName = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(ChannelInfoSchema.CHANNEL_NAME, channelName);

                ObjectNode channelTimestamp = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                propertiesNode.set(ChannelInfoSchema.CHANNEL_TIMESTAMP, channelTimestamp);

                ObjectNode channelMessageId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(ChannelInfoSchema.CHANNEL_MESSAGE_ID, channelMessageId);
            }
            channelNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, propertiesNode);
        }
        return channelNode;
    }

    @Override
    public void refreshAllIndexes() {
        super.refreshIndex(datastoreUtils.getChannelIndexName(KapuaId.ANY));
    }

    @Override
    public void deleteAllIndexes() {
        super.deleteIndexes(datastoreUtils.getChannelIndexName(KapuaId.ANY));
    }
}
