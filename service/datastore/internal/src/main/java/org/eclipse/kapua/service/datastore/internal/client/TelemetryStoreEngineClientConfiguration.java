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
package org.eclipse.kapua.service.datastore.internal.client;

import java.util.List;

import org.eclipse.kapua.service.datastore.internal.setting.TelemetryStoreEngineClientSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreElasticsearchClientSettingsKey;
import org.eclipse.kapua.service.storeengine.client.configuration.StoreEngineClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelemetryStoreEngineClientConfiguration extends StoreEngineClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryStoreEngineClientConfiguration.class);

    private final TelemetryStoreEngineClientSettings telemetryStoreEngineClientSettings = TelemetryStoreEngineClientSettings.getInstance();

    public TelemetryStoreEngineClientConfiguration() {
        setModuleName(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.MODULE));

        setClusterName(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.CLUSTER));

        List<String> nodesSplitted = telemetryStoreEngineClientSettings.getList(String.class, DatastoreElasticsearchClientSettingsKey.NODES);
        for (String node : nodesSplitted) {
            String[] nodeSplitted = node.split(":");
            addNode(nodeSplitted[0], nodeSplitted.length == 2 ? Integer.parseInt(nodeSplitted[1]) : 9200);
        }

        setUsername(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.USERNAME));
        setPassword(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.PASSWORD));

        getRequestConfiguration().setQueryTimeout(telemetryStoreEngineClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_QUERY_TIMEOUT));
        getRequestConfiguration().setScrollTimeout(telemetryStoreEngineClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_SCROLL_TIMEOUT));
        getRequestConfiguration().setConnectionTimeoutMillis(telemetryStoreEngineClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_CONNECTION_TIMEOUT_MILLIS, -1));
        getRequestConfiguration().setSocketTimeoutMillis(telemetryStoreEngineClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_SOCKET_TIMEOUT_MILLIS, -1));
        getRequestConfiguration().setRequestRetryAttemptMax(telemetryStoreEngineClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_RETRY_MAX));
        getRequestConfiguration().setRequestRetryAttemptWait(telemetryStoreEngineClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_RETRY_WAIT));

        getSslConfiguration().setEnabled(telemetryStoreEngineClientSettings.getBoolean(DatastoreElasticsearchClientSettingsKey.SSL_ENABLED));
        getSslConfiguration().setKeyStoreType(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_TYPE));
        getSslConfiguration().setKeyStorePath(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_PATH));
        getSslConfiguration().setKeyStorePassword(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_PASSWORD));
        getSslConfiguration().setTrustStorePath(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_TRUSTSTORE_PATH));
        getSslConfiguration().setTrustStorePassword(telemetryStoreEngineClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_TRUSTSTORE_PASSWORD));

        setNumberOfIOThreads(telemetryStoreEngineClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.NUMBER_OF_IO_THREADS, 0));
        getReconnectConfiguration().setReconnectDelay(30000);

        setPoolSize(telemetryStoreEngineClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.POOL_SIZE));
    }

    public static StoreEngineClientConfiguration getInstance() {
        return new TelemetryStoreEngineClientConfiguration();
    }
}
