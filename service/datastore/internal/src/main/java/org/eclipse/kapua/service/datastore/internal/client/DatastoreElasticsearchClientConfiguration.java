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
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.kapua.service.datastore.internal.setting.DatastoreElasticsearchClientSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreElasticsearchClientSettingsKey;
import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatastoreElasticsearchClientConfiguration extends ElasticsearchClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DatastoreElasticsearchClientConfiguration.class);

    private final DatastoreElasticsearchClientSettings elasticsearchClientSettings = DatastoreElasticsearchClientSettings.getInstance();

    public DatastoreElasticsearchClientConfiguration() {
        setModuleName(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.MODULE));

        setClusterName(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.CLUSTER));

        List<String> nodesSplitted = elasticsearchClientSettings.getList(String.class, DatastoreElasticsearchClientSettingsKey.NODES);
        for (String node : nodesSplitted) {
            String[] nodeSplitted = node.split(":");
            addNode(nodeSplitted[0], nodeSplitted.length == 2 ? Integer.parseInt(nodeSplitted[1]) : 9200);
        }

        setUsername(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.USERNAME));
        setPassword(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.PASSWORD));

        getRequestConfiguration().setQueryTimeout(elasticsearchClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_QUERY_TIMEOUT));
        getRequestConfiguration().setScrollTimeout(elasticsearchClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_SCROLL_TIMEOUT));
        getRequestConfiguration().setConnectionTimeoutMillis(elasticsearchClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_CONNECTION_TIMEOUT_MILLIS, -1));
        getRequestConfiguration().setSocketTimeoutMillis(elasticsearchClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_SOCKET_TIMEOUT_MILLIS, -1));
        getRequestConfiguration().setRequestRetryAttemptMax(elasticsearchClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_RETRY_MAX));
        getRequestConfiguration().setRequestRetryAttemptWait(elasticsearchClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.REQUEST_RETRY_WAIT));

        getSslConfiguration().setEnabled(elasticsearchClientSettings.getBoolean(DatastoreElasticsearchClientSettingsKey.SSL_ENABLED));
        getSslConfiguration().setKeyStoreType(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_TYPE));
        getSslConfiguration().setKeyStorePath(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_PATH));
        getSslConfiguration().setKeyStorePassword(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_KEYSTORE_PASSWORD));
        getSslConfiguration().setTrustStorePath(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_TRUSTSTORE_PATH));
        getSslConfiguration().setTrustStorePassword(elasticsearchClientSettings.getString(DatastoreElasticsearchClientSettingsKey.SSL_TRUSTSTORE_PASSWORD));
        getReconnectConfiguration().setReconnectDelay(30000);

        // NUMBER_OF_IO_THREADS has been deprecated in favour of ASYNC_CONN_NUMBER_OF_IO_THREADS. If the latter is defined it will take
        // preference otherwise the old one will be picked is any. When NUMBER_OF_IO_THREADS will be removed the only one
        // parameter to look at will be ASYNC_CONN_NUMBER_OF_IO_THREADS
        Optional<Integer> numberOfThreads;
        try {
            numberOfThreads = elasticsearchClientSettings.getInteger(DatastoreElasticsearchClientSettingsKey.ASYNC_CONN_NUMBER_OF_IO_THREADS);
        } catch (NoSuchElementException e) {
            numberOfThreads = Optional.of(elasticsearchClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.NUMBER_OF_IO_THREADS, 0));
        }
        getAsyncConnConfiguration().setNumberOfIOThreads(numberOfThreads);
        getAsyncConnConfiguration().setMaxTotal(elasticsearchClientSettings.getInteger(DatastoreElasticsearchClientSettingsKey.ASYNC_CONN_MAX_TOTAL_CONN));
        getAsyncConnConfiguration().setDefaultMaxPerRoute(elasticsearchClientSettings.getInteger(DatastoreElasticsearchClientSettingsKey.ASYNC_CONN_DEFAULT_MAX_CONN_PER_ROUTE));
        setPoolSize(elasticsearchClientSettings.getInt(DatastoreElasticsearchClientSettingsKey.POOL_SIZE));
    }

    public static ElasticsearchClientConfiguration getInstance() {
        return new DatastoreElasticsearchClientConfiguration();
    }
}
