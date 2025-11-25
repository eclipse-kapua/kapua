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

import java.util.Map;

import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.service.datastore.internal.mediator.Metric;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;

import com.google.inject.Inject;

/**
 * Datastore cache manager.<br> It keeps informations about channels, metrics and clients to speed up the store operation and avoid time consuming unnecessary operations.
 *
 * @since 1.0.0
 */
public class DatastoreCacheManager {

    private final LocalCache<String, Map<String, Metric>> schemaCache;
    private final LocalCache<String, Boolean> channelsCache;
    private final LocalCache<String, Boolean> metricsCache;
    private final LocalCache<String, Boolean> clientsCache;
    private final LocalCache<String, Boolean> indexCache;

    @Inject
    public DatastoreCacheManager(DatastoreSettings datastoreSettings) {
        final int sizeMaxMetadata = datastoreSettings.getInt(DatastoreSettingsKey.CONFIG_CACHE_METADATA_LOCAL_SIZE_MAXIMUM);

        clientsCache = new LocalCache<>(datastoreSettings.getClientCacheConfig(), false);
        channelsCache = new LocalCache<>(datastoreSettings.getChannelsCacheConfig(), false);
        metricsCache = new LocalCache<>(datastoreSettings.getMetricsCacheConfig(), false);

        schemaCache = new LocalCache<>(sizeMaxMetadata, null);
        indexCache = new LocalCache<>(sizeMaxMetadata, null);
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
     * Get the index informations cache
     *
     * @return
     * @since 2.0.0
     */
    public LocalCache<String, Boolean> getIndexCache() {
        return indexCache;
    }

    /**
     * Get the metadata informations cache
     *
     * @return
     * @since 1.0.0
     */
    public LocalCache<String, Map<String, Metric>> getMetadataCache() {
        return schemaCache;
    }
}
