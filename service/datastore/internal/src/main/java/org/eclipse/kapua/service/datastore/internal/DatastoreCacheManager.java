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

import org.eclipse.kapua.commons.cache.ExpiryPolicy;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;
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
    private final LocalCache<String, MessageStoreConfiguration> storeConfigurationCache;

    @Inject
    public DatastoreCacheManager(DatastoreSettings datastoreSettings) {
        final int sizeMaxMetadata = datastoreSettings.getInt(DatastoreSettingsKey.CONFIG_CACHE_METADATA_LOCAL_SIZE_MAXIMUM);

        clientsCache = new LocalCache<>(datastoreSettings.getClientCacheConfig(), false);
        channelsCache = new LocalCache<>(datastoreSettings.getChannelsCacheConfig(), false);
        metricsCache = new LocalCache<>(datastoreSettings.getMetricsCacheConfig(), false);

        schemaCache = new LocalCache<>(sizeMaxMetadata, null);

        final Optional<Integer> configurationSizeMax = datastoreSettings.getInteger(DatastoreSettingsKey.CONFIG_CACHE_CONFIGURATION_LOCAL_SIZE_MAXIMUM);
        final Optional<Integer> configurationExpireAfter = datastoreSettings.getInteger(DatastoreSettingsKey.CONFIG_CACHE_CONFIGURATION_LOCAL_EXPIRE_AFTER);
        storeConfigurationCache = new LocalCache<>(
                configurationSizeMax.filter(val -> val > 0).orElse(1000),
                configurationExpireAfter.filter(val -> val >0).orElse(60),
                ExpiryPolicy.MODIFIED, null);
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
    public LocalCache<String, Map<String, Metric>> getMetadataCache() {
        return schemaCache;
    }

    /**
     * Get the message store configurations cache
     *
     * @return
     * @since 1.6.16
     */
   public LocalCache<String, MessageStoreConfiguration> getStoreConfigurationCache() {
        return storeConfigurationCache;
    }
}
