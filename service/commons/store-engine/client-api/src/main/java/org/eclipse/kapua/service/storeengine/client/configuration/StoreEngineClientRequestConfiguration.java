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
package org.eclipse.kapua.service.storeengine.client.configuration;

import java.util.Optional;

import org.eclipse.kapua.service.storeengine.client.model.Request;

/**
 * The {@link StoreEngineClientRequestConfiguration} definition.
 * <p>
 * It contains values for configuring request properties. It contains default values to ease the usage of the class.
 */
public class StoreEngineClientRequestConfiguration {

    private int requestRetryAttemptMax = 3;
    private int requestRetryAttemptWait = 2500;

    private int queryTimeout = 15000;
    private int scrollTimeout = 60000;
    private Optional<Integer> connectionTimeoutMillis = Optional.empty();
    private Optional<Integer> socketTimeoutMillis = Optional.empty();

    /**
     * Gets the number of maximum attempts to retry a {@link Request}.
     * <p>
     * Default value: 3
     *
     * @return The number of maximum attempts to retry a {@link Request}.
     * @since 1.3.0
     */
    public int getRequestRetryAttemptMax() {
        return requestRetryAttemptMax;
    }

    /**
     * Sets the number of maximum attempts to retry a {@link Request}.
     *
     * @param requestRetryAttemptMax
     *         The number of maximum attempts to retry a {@link Request}.
     * @return This {@link StoreEngineClientRequestConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientRequestConfiguration setRequestRetryAttemptMax(int requestRetryAttemptMax) {
        this.requestRetryAttemptMax = requestRetryAttemptMax;
        return this;
    }

    /**
     * Gets the wait time between {@link Request} retries.
     * <p>
     * Default value: 2500
     *
     * @return The wait time between {@link Request} retries.
     * @since 1.3.0
     */
    public int getRequestRetryAttemptWait() {
        return requestRetryAttemptWait;
    }

    /**
     * Sets the wait time between {@link Request} retries.
     *
     * @param requestRetryAttemptWait
     *         The wait time between {@link Request} retries.
     * @return This {@link StoreEngineClientRequestConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientRequestConfiguration setRequestRetryAttemptWait(int requestRetryAttemptWait) {
        this.requestRetryAttemptWait = requestRetryAttemptWait;
        return this;
    }

    /**
     * Gets the query {@link Request} timeout.
     * <p>
     * Default value: 15000
     *
     * @return The query {@link Request} timeout.
     * @since 1.3.0
     */
    public int getQueryTimeout() {
        return queryTimeout;
    }

    /**
     * Sets the query {@link Request} timeout.
     *
     * @param queryTimeout
     *         The query {@link Request} timeout.
     * @return This {@link StoreEngineClientRequestConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientRequestConfiguration setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
        return this;
    }

    /**
     * Gets the scroll {@link Request} timeout.
     * <p>
     * Default value: 60000
     *
     * @return The scroll {@link Request} timeout.
     * @since 1.3.0
     */
    public int getScrollTimeout() {
        return scrollTimeout;
    }

    /**
     * Sets the scroll {@link Request} timeout.
     *
     * @param scrollTimeout
     *         The scroll {@link Request} timeout.
     * @return This {@link StoreEngineClientRequestConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientRequestConfiguration setScrollTimeout(int scrollTimeout) {
        this.scrollTimeout = scrollTimeout;
        return this;
    }

    /**
     * Gets the {@link Request} connection timeout. https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/7.17/_timeouts.html
     * <p>
     * Default value: 5000 ms
     *
     * @return The query {@link Request} connection timeout.
     * @since 2.1.0
     */
    public Optional<Integer> getConnectionTimeoutMillis() {
        return connectionTimeoutMillis;
    }

    /**
     * Gets the {@link Request} socket timeout. https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/7.17/_timeouts.html
     * <p>
     * Default value: 3000 ms
     *
     * @return The query {@link Request} connection timeout.
     * @since 2.1.0
     */
    public Optional<Integer> getSocketTimeoutMillis() {
        return socketTimeoutMillis;
    }

    /**
     * Sets the scroll {@link Request} connection timeout. https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/7.17/_timeouts.html.
     *
     * @param connectionTimeoutMillis
     *         The scroll {@link Request} connection timeout.
     * @return This {@link StoreEngineClientRequestConfiguration} to chain method invocation.
     * @since 2.1.0
     */
    public StoreEngineClientRequestConfiguration setConnectionTimeoutMillis(Integer connectionTimeoutMillis) {
        this.connectionTimeoutMillis = Optional.ofNullable(connectionTimeoutMillis).filter(i -> i >= 0);
        return this;
    }

    /**
     * Sets the scroll {@link Request} socket timeout. https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/7.17/_timeouts.html.
     *
     * @param socketTimeoutMillis
     *         The scroll {@link Request} socket timeout.
     * @return This {@link StoreEngineClientRequestConfiguration} to chain method invocation.
     * @since 2.1.0
     */
    public StoreEngineClientRequestConfiguration setSocketTimeoutMillis(Integer socketTimeoutMillis) {
        this.socketTimeoutMillis = Optional.ofNullable(socketTimeoutMillis).filter(i -> i >= 0);
        return this;
    }
}
