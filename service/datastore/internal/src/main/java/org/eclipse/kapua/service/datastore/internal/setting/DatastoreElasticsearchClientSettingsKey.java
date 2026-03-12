/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.datastore.internal.setting;

import org.eclipse.kapua.commons.setting.SettingKey;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientWrapper;

/**
 * Datastore {@link ElasticsearchClientWrapper} setting keys.
 *
 * @since 1.3.0
 */
public enum DatastoreElasticsearchClientSettingsKey implements SettingKey {

    /**
     * The name of the module which is managing the {@link ElasticsearchClientWrapper}.
     *
     * @since 1.3.0
     */
    MODULE("datastore.elasticsearch.module"),
    /**
     * Elasticsearch cluster name.
     *
     * @since 1.3.0
     */
    CLUSTER("datastore.elasticsearch.cluster"),
    /**
     * Elasticsearch nodes list.
     *
     * @since 1.3.0
     */
    NODES("datastore.elasticsearch.nodes"),
    /**
     * Elasticsearch Username.
     *
     * @since 1.3.0.
     */
    USERNAME("datastore.elasticsearch.username"),
    /**
     * Elasticsearch Password
     *
     * @since 1.3.0
     */
    PASSWORD("datastore.elasticsearch.password"),
    /**
     * Wait between client reconnection task executions.
     *
     * @since 1.3.0
     */
    CLIENT_RECONNECTION_WAIT_BETWEEN_EXECUTIONS("datastore.elasticsearch.client.reconnection_wait_between_exec"), //TODO: this is not used, should we allow to set a value for this?
    /**
     * Request query timeout.
     *
     * @since 1.3.0
     */
    REQUEST_QUERY_TIMEOUT("datastore.elasticsearch.request.query.timeout"),
    /**
     * Request scroll timeout.
     *
     * @since 1.3.0
     */
    REQUEST_SCROLL_TIMEOUT("datastore.elasticsearch.request.scroll.timeout"),
    /**
     * Elasticsearch max retry attempt (when a timeout occurred in the rest call).
     *
     * @since 1.3.0
     */
    REQUEST_RETRY_MAX("datastore.elasticsearch.request.retry.max"),
    /**
     * Elasticsearch max wait time between retry attempt (in milliseconds)
     *
     * @since 1.3.0
     */
    REQUEST_RETRY_WAIT("datastore.elasticsearch.request.retry.wait"),
    /**
     * Enable Elasticsearch client ssl connection (at the present only the rest client supports it)
     *
     * @since 1.3.0
     */
    SSL_ENABLED("datastore.elasticsearch.ssl.enabled"),

    /**
     * Elastichsearch client key store password (at the present only the rest client supports it).
     *
     * @since 1.3.0
     */
    SSL_KEYSTORE_PASSWORD("datastore.elasticsearch.ssl.keystore.password"),
    /**
     * Elastichsearch client key store path (at the present only the rest client supports it).
     *
     * @since 1.3.0
     */
    SSL_KEYSTORE_PATH("datastore.elasticsearch.ssl.keystore.path"),
    /**
     * Set the keystore type.
     *
     * @since 1.3.0
     */
    SSL_KEYSTORE_TYPE("datastore.elasticsearch.ssl.keystore.type"),
    /**
     * Elastichsearch client trust store path (at the present only the rest client supports it)
     *
     * @since 1.3.0
     */
    SSL_TRUSTSTORE_PATH("datastore.elasticsearch.ssl.truststore.path"),
    /**
     * Elastichsearch client trust store password (at the present only the rest client supports it)
     *
     * @since 1.3.0
     */
    SSL_TRUSTSTORE_PASSWORD("datastore.elasticsearch.ssl.truststore.password"),
    /**
     * Elastichsearch client number of IO threads. Leave &lt;=0 to use the default
     *
     * @since 2.1.0
     */
    NUMBER_OF_IO_THREADS("datastore.elasticsearch.numberOfIOThreads"),
    /**
     * Elastichsearch client request connection timeout in milliseconds. Leave &lt;0 to use the default
     *
     * @since 2.1.0
     */
    REQUEST_CONNECTION_TIMEOUT_MILLIS("datastore.elasticsearch.request.connection.timeout.millis"),
    /**
     * Elastichsearch client request socket timeout in milliseconds. Leave &lt;0 to use the default
     *
     * @since 2.1.0
     */
    REQUEST_SOCKET_TIMEOUT_MILLIS("datastore.elasticsearch.request.socket.timeout.millis");

    /**
     * The key value in the configuration resources.
     *
     * @since 1.3.0
     */
    private final String key;

    /**
     * Set up the {@code enum} with the key value provided
     *
     * @param key
     *         The value mapped by this {@link Enum} value
     * @since 1.0.0
     */
    DatastoreElasticsearchClientSettingsKey(String key) {
        this.key = key;
    }

    /**
     * Gets the key for this {@link DatastoreElasticsearchClientSettingsKey}
     *
     * @since 1.0.0
     */
    @Override
    public String key() {
        return key;
    }
}
