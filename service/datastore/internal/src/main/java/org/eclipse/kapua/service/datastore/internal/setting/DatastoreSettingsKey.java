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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

/**
 * Datastore setting keys.
 *
 * @since 1.0
 */
public enum DatastoreSettingsKey implements SettingKey {

    /**
     * Local cache expire time (default value is no specific cache value is defined)
     */
    CONFIG_CACHE_LOCAL_EXPIRE_AFTER("datastore.cache.local.expire.after"),
    /**
     * Channels Local cache expire time. Overrides the default value if specified, can be omitted to use the value of CONFIG_CACHE_LOCAL_EXPIRE_AFTER.
     */
    CONFIG_CHANNELS_CACHE_LOCAL_EXPIRE_AFTER("datastore.cache.channels.local.expire.after"),
    /**
     * Clients Local cache expire time. Overrides the default value if specified, can be omitted to use the value of CONFIG_CACHE_LOCAL_EXPIRE_AFTER.
     */
    CONFIG_CLIENTS_CACHE_LOCAL_EXPIRE_AFTER("datastore.cache.clients.local.expire.after"),
    /**
     * Metrics Local cache expire time. Overrides the default value if specified, can be omitted to use the value of CONFIG_CACHE_LOCAL_EXPIRE_AFTER.
     */
    CONFIG_METRICS_CACHE_LOCAL_EXPIRE_AFTER("datastore.cache.metrics.local.expire.after"),
    /**
     * Channels Local cache expire strategy (either MODIFIED or TOUCHED). If omitted, MODIFIED will be assumed.
     */
    CONFIG_CHANNELS_CACHE_LOCAL_EXPIRE_STRATEGY("datastore.cache.channels.local.expire.strategy"),
    /**
     * Clients Local cache expire strategy (either MODIFIED or TOUCHED). If omitted, MODIFIED will be assumed.
     */
    CONFIG_CLIENTS_CACHE_LOCAL_EXPIRE_STRATEGY("datastore.cache.clients.local.expire.strategy"),
    /**
     * Metrics Local cache expire strategy (either MODIFIED or TOUCHED). If omitted, MODIFIED will be assumed.
     */
    CONFIG_METRICS_CACHE_LOCAL_EXPIRE_STRATEGY("datastore.cache.metrics.local.expire.strategy"),
    /**
     * Local cache maximum size (default value is no specific cache value is defined)
     */
    CONFIG_CACHE_LOCAL_SIZE_MAXIMUM("datastore.cache.local.size.maximum"),
    /**
     * Channels Local cache expire time. Overrides the default value if specified, can be omitted to use the value of CONFIG_CACHE_LOCAL_SIZE_MAXIMUM.
     */
    CONFIG_CHANNELS_CACHE_LOCAL_SIZE_MAXIMUM("datastore.cache.channels.local.size.maximum"),
    /**
     * Clients Local cache expire time. Overrides the default value if specified, can be omitted to use the value of CONFIG_CACHE_LOCAL_SIZE_MAXIMUM.
     */
    CONFIG_CLIENTS_CACHE_LOCAL_SIZE_MAXIMUM("datastore.cache.clients.local.size.maximum"),
    /**
     * Metrics Local cache expire time. Overrides the default value if specified, can be omitted to use the value of CONFIG_CACHE_LOCAL_SIZE_MAXIMUM.
     */
    CONFIG_METRICS_CACHE_LOCAL_SIZE_MAXIMUM("datastore.cache.metrics.local.size.maximum"),
    /**
     * Channels behaviour switch - when the cache does not know a specific channel, should the system ask ES about it (set to true, default) or just send an upsert for non-cached channles (set to
     * false)?
     */
    CONFIG_CHANNELS_CACHE_LOCAL_FETCH_FROM_SOURCE_BEFORE_UPSERT("datastore.cache.channels.local.fetchBeforeUpsert"),
    /**
     * Client behaviour switch - when the cache does not know a specific client, should the system ask ES about it (set to true, default) or just send an upsert for non-cached clients (set to
     * false)?
     */
    CONFIG_CLIENTS_CACHE_LOCAL_FETCH_FROM_SOURCE_BEFORE_UPSERT("datastore.cache.clients.local.fetchBeforeUpsert"),
    /**
     * Metrics behaviour switch - when the cache does not know a specific metric, should the system ask ES about it (set to true, default) or just send an upsert for non-cached metrics (set to
     * false)?
     */
    CONFIG_METRICS_CACHE_LOCAL_FETCH_FROM_SOURCE_BEFORE_UPSERT("datastore.cache.metrics.local.fetchBeforeUpsert"),
    /**
     * Channels behaviour switch - when the cache does not know a specific channel, should the system ask other upserts to wait until the non-cached channel to finish its upsert (set to true, 
     * default) or just let it go (set to false)? Asking to wait increase the chances the other upserts are resolved by the cache.
     */
    CONFIG_CHANNELS_CACHE_LOCAL_SYNCH_BEFORE_UPSERT("datastore.cache.channels.local.synchBeforeUpsert"),
    /**
     * Client behaviour switch - when the cache does not know a specific client, should the system ask other upserts to wait until the non-cached client to finish its upsert (set to true, default) 
     * or just let it go (set to false)? Asking to wait increase the chances the other upserts are resolved by the cache.
     */
    CONFIG_CLIENTS_CACHE_LOCAL_SYNCH_BEFORE_UPSERT("datastore.cache.clients.local.synchBeforeUpsert"),
    /**
     * Metadata cache maximum size (default value is no specific cache value is defined)
     */
    CONFIG_CACHE_METADATA_LOCAL_SIZE_MAXIMUM("datastore.cache.metadata.local.size.maximum"),
    /**
     * Enable datastore timing profile
     */
    CONFIG_DATA_STORAGE_ENABLE_TIMING_PROFILE("datastore.enableTimingProfile"),
    /**
     * Datastore timing profile threshold
     */
    CONFIG_DATA_STORAGE_TIMING_PROFILE_THRESHOLD("datastore.timingProfileThreshold"),
    /**
     * Elasticsearch index refresh interval (the data is available for a search operation only if it is indexed)
     */
    INDEX_REFRESH_INTERVAL("datastore.index.refresh_interval"),
    /**
     * Shards number
     */
    INDEX_SHARD_NUMBER("datastore.index.number_of_shards"),
    /**
     * Replicas count
     */
    INDEX_REPLICA_NUMBER("datastore.index.number_of_replicas"),
    /**
     * Elasticsearch index refresh interval (the data is available for a search operation only if it is indexed)
     */
    CONFIG_MAX_ENTRIES_ON_DELETE("datastore.delete.max_entries_on_delete"),
    /**
     * Elasticsearch index name system-wide prefix
     */
    INDEX_PREFIX("datastore.index.prefix"),
    /**
     * Elasticsearch index width. Allowed values: "week", "day", "hour"
     */
    INDEXING_WINDOW_OPTION("datastore.index.window"),
    /**
     * Disables the entire Datastore feature
     */
    DISABLE_DATASTORE("datastore.disable"),
    /**
     * Elasticsearch limit+offset maximum value
     */
    MAX_RESULT_WINDOW_VALUE("datastore.max_result_window");

    private String key;

    private DatastoreSettingsKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
