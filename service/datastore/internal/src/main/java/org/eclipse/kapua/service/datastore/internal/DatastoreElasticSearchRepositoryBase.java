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

import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchRepository;
import org.eclipse.kapua.service.elasticsearch.client.SchemaKeys;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.StorableListResult;
import org.eclipse.kapua.service.storable.model.query.StorableQuery;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;
import org.eclipse.kapua.service.storable.repository.StorableRepository;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class DatastoreElasticSearchRepositoryBase<
        T extends Storable,
        L extends StorableListResult<T>,
        Q extends StorableQuery>
        extends ElasticsearchRepository<T, L, Q>
        implements StorableRepository<T, L, Q> {

    protected final DatastoreSettings datastoreSettings;

    protected DatastoreElasticSearchRepositoryBase(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            Class<T> clazz,
            StorablePredicateFactory storablePredicateFactory,
            LocalCache<String, Boolean> indexesCache,
            DatastoreSettings datastoreSettings,
            Function<KapuaId, Q> querySupplier,
            Supplier<L> listSupplier) {
        super(elasticsearchClientProviderInstance, clazz, storablePredicateFactory,
                indexesCache, querySupplier, listSupplier);
        this.datastoreSettings = datastoreSettings;
    }

    protected DatastoreElasticSearchRepositoryBase(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            Class<T> clazz,
            StorablePredicateFactory storablePredicateFactory, DatastoreSettings datastoreSettings,
            Function<KapuaId, Q> querySupplier,
            Supplier<L> listSupplier) {
        super(elasticsearchClientProviderInstance, clazz, storablePredicateFactory, querySupplier, listSupplier);
        this.datastoreSettings = datastoreSettings;
    }

    /**
     * @param idxName
     * @return
     * @throws org.eclipse.kapua.service.storable.exception.MappingException
     * @since 1.0.0
     */
    @Override
    protected ObjectNode getMappingSchema(String idxName) throws MappingException {
        String idxRefreshInterval = String.format("%ss", datastoreSettings.getLong(DatastoreSettingsKey.INDEX_REFRESH_INTERVAL));
        Integer idxShardNumber = datastoreSettings.getInt(DatastoreSettingsKey.INDEX_SHARD_NUMBER, 1);
        Integer idxReplicaNumber = datastoreSettings.getInt(DatastoreSettingsKey.INDEX_REPLICA_NUMBER, 0);

        ObjectNode rootNode = MappingUtils.newObjectNode();
        ObjectNode settingsNode = MappingUtils.newObjectNode();
        ObjectNode refreshIntervalNode = MappingUtils.newObjectNode(new KeyValueEntry[] {
                new KeyValueEntry(SchemaKeys.KEY_REFRESH_INTERVAL, idxRefreshInterval),
                new KeyValueEntry(SchemaKeys.KEY_SHARD_NUMBER, idxShardNumber),
                new KeyValueEntry(SchemaKeys.KEY_REPLICA_NUMBER, idxReplicaNumber) });
        settingsNode.set(SchemaKeys.KEY_INDEX, refreshIntervalNode);
        rootNode.set(SchemaKeys.KEY_SETTINGS, settingsNode);
        logger.info("Creating index for '{}' - refresh: '{}' - shards: '{}' replicas: '{}': ", idxName, idxRefreshInterval, idxShardNumber, idxReplicaNumber);
        return rootNode;
    }

}
