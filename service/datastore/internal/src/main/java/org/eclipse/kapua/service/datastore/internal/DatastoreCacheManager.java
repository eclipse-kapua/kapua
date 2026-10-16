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

import java.util.Optional;

import org.eclipse.kapua.commons.cache.ExpiryPolicy;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;
import org.eclipse.kapua.service.datastore.internal.schema.Metadata;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;

/**
 * Datastore cache manager.<br> It keeps informations about channels, metrics and clients to speed up the store operation and avoid time consuming unnecessary operations.
 *
 * @since 1.0.0
 */
public class DatastoreCacheManager {

    private static final DatastoreCacheManager INSTANCE = new DatastoreCacheManager();

    private final LocalCache<String, Metadata> schemaCache;
    private final LocalCache<String, Boolean> channelsCache;
    private final LocalCache<String, Boolean> metricsCache;
    private final LocalCache<String, Boolean> clientsCache;
    private final LocalCache<KapuaId, MessageStoreConfiguration> configurationCache;


    private DatastoreCacheManager() {
        final DatastoreSettings config = DatastoreSettings.getInstance();
        final int sizeMaxMetadata = config.getInt(DatastoreSettingsKey.CONFIG_CACHE_METADATA_LOCAL_SIZE_MAXIMUM);

        clientsCache = new LocalCache<>(config.getClientCacheConfig(), false);
        channelsCache = new LocalCache<>(config.getChannelsCacheConfig(), false);
        metricsCache = new LocalCache<>(config.getMetricsCacheConfig(), false);

        schemaCache = new LocalCache<>(sizeMaxMetadata, null);

        final Optional<Integer> configurationSizeMax = config.getInteger(DatastoreSettingsKey.CONFIG_CACHE_CONFIGURATION_LOCAL_SIZE_MAXIMUM);
        final Optional<Integer> configurationExpireAfter = config.getInteger(DatastoreSettingsKey.CONFIG_CACHE_CONFIGURATION_LOCAL_EXPIRE_AFTER);
        configurationCache = new LocalCache<>(
                configurationSizeMax.filter(val -> val > 0).orElse(0),
                configurationExpireAfter.filter(val -> val > 0).orElse(0),
                ExpiryPolicy.MODIFIED, null);
    }

    /**
     * Get the cache manager instance
     *
     * @return
     * @since 1.0.0
     */
    public static DatastoreCacheManager getInstance() {
        return INSTANCE;
    }

    /**
     * Get the channels informations cache
     *
     * @return
     * @since 1.0.0
     */
    public LocalCache<String, Boolean> getChannelsCache() {
        return channelsCache;
    }

    /**
     * Get the metrics informations cache
     *
     * @return
     * @since 1.0.0
     */
    public LocalCache<String, Boolean> getMetricsCache() {
        return metricsCache;
    }

    /**
     * Get the clients informations cache
     *
     * @return
     * @since 1.0.0
     */
    public LocalCache<String, Boolean> getClientsCache() {
        return clientsCache;
    }

    /**
     * Get the metadata informations cache
     *
     * @return
     * @since 1.0.0
     */
    public LocalCache<String, Metadata> getMetadataCache() {
        return schemaCache;
    }

    /**
     * Get the message store configurations cache
     *
     * @return
     */
    public LocalCache<KapuaId, MessageStoreConfiguration> getConfigurationCache() {
        return configurationCache;
    }
}
