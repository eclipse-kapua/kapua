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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.kapua.service.storeengine.client.StoreEngineClientWrapper;

/**
 * The {@link StoreEngineClientConfiguration} used to configure an instance of a {@link StoreEngineClientWrapper}
 *
 * @since 1.3.0
 */
public class StoreEngineClientConfiguration {

    private String moduleName;
    private String clusterName;
    private List<StoreEngineNode> nodes;
    private String username;
    private String password;
    private Optional<Integer> numberOfIOThreads;
    private int poolSize;

    private StoreEngineClientReconnectConfiguration reconnectConfiguration;
    private StoreEngineClientRequestConfiguration requestConfiguration;
    private StoreEngineClientSslConfiguration sslConfiguration;

    /**
     * Gets the module name which is managing the {@link StoreEngineClientWrapper} instance.
     *
     * @return The module name which is managing the {@link StoreEngineClientWrapper} instance.
     * @since 1.3.0
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Sets the module name which is managing the {@link StoreEngineClientWrapper} instance.
     *
     * @param moduleName
     *         The module name which is managing the {@link StoreEngineClientWrapper} instance.
     * @since 1.3.0
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Gets the Elasticsearch cluster name to use.
     *
     * @return The Elasticsearch cluster name to use.
     * @since 1.3.0
     */
    public String getClusterName() {
        return clusterName;
    }

    /**
     * Sets the Elasticsearch cluster name to use.
     *
     * @param clusterName
     *         The Elasticsearch cluster name to use.
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientConfiguration setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    /**
     * Gets the {@link List} of {@link StoreEngineNode}s.
     *
     * @return The {@link List} of {@link StoreEngineNode}s.
     * @since 1.3.0
     */
    public List<StoreEngineNode> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }

        return nodes;
    }

    /**
     * Adds a new {@link StoreEngineNode} to the {@link List} {@link StoreEngineNode}s already configured.
     * <p>
     * Shortcut method for:
     * <pre>
     *     getNodes().add(new StoreEngineNode(address, port));
     * </pre>
     *
     * @param address
     *         The host of the Elasticsearch node
     * @param port
     *         The port of the Elasticsearch node
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientConfiguration addNode(String address, int port) {
        getNodes().add(new StoreEngineNode(address, port));
        return this;
    }

    /**
     * Sets the {@link List} of {@link StoreEngineNode}s.
     *
     * @param nodes
     *         The {@link List} of {@link StoreEngineNode}s.
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientConfiguration setNodes(List<StoreEngineNode> nodes) {
        this.nodes = nodes;
        return this;
    }

    /**
     * Gets the username used to anthenticate into Elasticsearch.
     *
     * @return The username used to anthenticate into Elasticsearch.
     * @since 1.3.0
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username used to authenticate into Elasticsearch.
     * <p>
     * Optional.
     *
     * @param username
     *         The username used to authenticate into Elasticsearch.
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientConfiguration setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Gets the password used to authenticate into Elasticsearch.
     *
     * @return The password used to authenticate into Elasticsearch.
     * @since 1.3.0
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password used to authenticate into Elasticsearch.
     * <p>
     * Optional.
     *
     * @param password
     *         The password used to authenticate into Elasticsearch.
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Gets the {@link StoreEngineClientReconnectConfiguration}.
     *
     * @return The {@link StoreEngineClientReconnectConfiguration}.
     * @since 1.3.0
     */
    public StoreEngineClientReconnectConfiguration getReconnectConfiguration() {
        if (reconnectConfiguration == null) {
            reconnectConfiguration = new StoreEngineClientReconnectConfiguration();
        }

        return reconnectConfiguration;
    }

    /**
     * Sets the {@link StoreEngineClientReconnectConfiguration}.
     *
     * @param reconnectConfiguration
     *         The {@link StoreEngineClientReconnectConfiguration}.
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientConfiguration setReconnectConfiguration(StoreEngineClientReconnectConfiguration reconnectConfiguration) {
        this.reconnectConfiguration = reconnectConfiguration;
        return this;
    }

    /**
     * Gets the {@link StoreEngineClientReconnectConfiguration}.
     *
     * @return the {@link StoreEngineClientReconnectConfiguration}.
     * @since 1.3.0
     */
    public StoreEngineClientRequestConfiguration getRequestConfiguration() {
        if (requestConfiguration == null) {
            requestConfiguration = new StoreEngineClientRequestConfiguration();
        }

        return requestConfiguration;
    }

    /**
     * Sets the {@link StoreEngineClientReconnectConfiguration}.
     *
     * @param requestConfiguration
     *         the {@link StoreEngineClientReconnectConfiguration}.
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientConfiguration setRequestConfiguration(StoreEngineClientRequestConfiguration requestConfiguration) {
        this.requestConfiguration = requestConfiguration;
        return this;
    }

    /**
     * Gets the {@link StoreEngineClientSslConfiguration}
     *
     * @return The {@link StoreEngineClientSslConfiguration}
     * @since 1.3.0
     */
    public StoreEngineClientSslConfiguration getSslConfiguration() {
        if (sslConfiguration == null) {
            sslConfiguration = new StoreEngineClientSslConfiguration();
        }

        return sslConfiguration;
    }

    /**
     * Sets the {@link StoreEngineClientSslConfiguration}
     *
     * @param sslConfiguration
     *         The {@link StoreEngineClientSslConfiguration}
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientConfiguration setSslConfiguration(StoreEngineClientSslConfiguration sslConfiguration) {
        this.sslConfiguration = sslConfiguration;
        return this;
    }

    public Optional<Integer> getNumberOfIOThreads() {
        return this.numberOfIOThreads;
    }

    public StoreEngineClientConfiguration setNumberOfIOThreads(Integer numberOfIOThreads) {
        this.numberOfIOThreads = Optional.ofNullable(numberOfIOThreads)
                .filter(i -> i > 0);
        return this;
    }

    /**
     * Gets the size of the Elasticsearch client pool.
     *
     * @return The size of the Elasticsearch client pool.
     * @since 1.6.0
     */
    public int getPoolSize() {
        return poolSize;
    }

    /**
     * Sets the size of the Elasticsearch client pool.
     *
     * @param poolSize 
     *         The size of the Elasticsearch client pool.
     * @return This {@link StoreEngineClientConfiguration} to chain method invocation.
     * @since 1.6.0
     */
    public StoreEngineClientConfiguration setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }
}
