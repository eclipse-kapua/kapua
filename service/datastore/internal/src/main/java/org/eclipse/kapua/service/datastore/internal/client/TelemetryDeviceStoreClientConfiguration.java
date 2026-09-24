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

import org.eclipse.kapua.service.datastore.internal.setting.DeviceStoreClientSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreElasticsearchClientSettingsKey;
import org.eclipse.kapua.service.elasticsearch.client.configuration.DeviceStoreClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelemetryDeviceStoreClientConfiguration extends DeviceStoreClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(TelemetryDeviceStoreClientConfiguration.class);

    private final DeviceStoreClientSettings deviceStoreClientSettings = DeviceStoreClientSettings.getInstance();

    public TelemetryDeviceStoreClientConfiguration() {
        setModuleName(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.MODULE));

        setClusterName(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.CLUSTER));

        List<String> nodesSplitted = deviceStoreClientSettings.getList(String.class, DatastoreElasticsearchClientSettingsKey.NODES);
        for (String node : nodesSplitted) {
            String[] nodeSplitted = node.split(":");
            addNode(nodeSplitted[0], nodeSplitted.length == 2 ? Integer.parseInt(nodeSplitted[1]) : 9200);
        }

        setUsername(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.USERNAME));
        setPassword(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.PASSWORD));

        getRequestConfiguration().setQueryTimeout(deviceStoreClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_QUERY_TIMEOUT));
        getRequestConfiguration().setScrollTimeout(deviceStoreClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_SCROLL_TIMEOUT));
        getRequestConfiguration().setConnectionTimeoutMillis(deviceStoreClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_CONNECTION_TIMEOUT_MILLIS, -1));
        getRequestConfiguration().setSocketTimeoutMillis(deviceStoreClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_SOCKET_TIMEOUT_MILLIS, -1));
        getRequestConfiguration().setRequestRetryAttemptMax(deviceStoreClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_RETRY_MAX));
        getRequestConfiguration().setRequestRetryAttemptWait(deviceStoreClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_RETRY_WAIT));

        getSslConfiguration().setEnabled(deviceStoreClientSettings.getBoolean(DatastoreElasticsearchClientSettingsKey.SSL_ENABLED));
        getSslConfiguration().setKeyStoreType(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_TYPE));
        getSslConfiguration().setKeyStorePath(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_PATH));
        getSslConfiguration().setKeyStorePassword(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_PASSWORD));
        getSslConfiguration().setTrustStorePath(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_TRUSTSTORE_PATH));
        getSslConfiguration().setTrustStorePassword(deviceStoreClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_TRUSTSTORE_PASSWORD));

        setNumberOfIOThreads(deviceStoreClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.NUMBER_OF_IO_THREADS, 0));
        getReconnectConfiguration().setReconnectDelay(30000);

        setPoolSize(deviceStoreClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.POOL_SIZE));
    }

    public static DeviceStoreClientConfiguration getInstance() {
        return new TelemetryDeviceStoreClientConfiguration();
    }
}
