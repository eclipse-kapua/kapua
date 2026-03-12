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
package org.eclipse.kapua.service.elasticsearch.client.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientWrapper;

/**
 * The {@link ElasticsearchClientConfiguration} used to configure an instance of a {@link ElasticsearchClientWrapper}
 *
 * @since 1.3.0
 */
public class ElasticsearchClientConfiguration {

    private String moduleName;
    private String providerClassName;
    private String clusterName;
    private List<ElasticsearchNode> nodes;
    private String username;
    private String password;
    private Optional<Integer> numberOfIOThreads;

    private ElasticsearchClientReconnectConfiguration reconnectConfiguration;
    private ElasticsearchClientRequestConfiguration requestConfiguration;
    private ElasticsearchClientSslConfiguration sslConfiguration;

    /**
     * Gets the module name which is managing the {@link ElasticsearchClientWrapper} instance.
     *
     * @return The module name which is managing the {@link ElasticsearchClientWrapper} instance.
     * @since 1.3.0
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Sets the module name which is managing the {@link ElasticsearchClientWrapper} instance.
     *
     * @param moduleName
     *         The module name which is managing the {@link ElasticsearchClientWrapper} instance.
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
     * @return This {@link ElasticsearchClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientConfiguration setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    /**
     * Gets the {@link List} of {@link ElasticsearchNode}s.
     *
     * @return The {@link List} of {@link ElasticsearchNode}s.
     * @since 1.3.0
     */
    public List<ElasticsearchNode> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }

        return nodes;
    }

    /**
     * Adds a new {@link ElasticsearchNode} to the {@link List} {@link ElasticsearchNode}s already configured.
     * <p>
     * Shortcut method for:
     * <pre>
     *     getNodes().add(new ElasticsearchNode(address, port));
     * </pre>
     *
     * @param address
     *         The host of the Elasticsearch node
     * @param port
     *         The port of the Elasticsearch node
     * @return This {@link ElasticsearchClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientConfiguration addNode(String address, int port) {
        getNodes().add(new ElasticsearchNode(address, port));
        return this;
    }

    /**
     * Sets the {@link List} of {@link ElasticsearchNode}s.
     *
     * @param nodes
     *         The {@link List} of {@link ElasticsearchNode}s.
     * @return This {@link ElasticsearchClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientConfiguration setNodes(List<ElasticsearchNode> nodes) {
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
     * @return This {@link ElasticsearchClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientConfiguration setUsername(String username) {
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
     * @return This {@link ElasticsearchClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Gets the {@link ElasticsearchClientReconnectConfiguration}.
     *
     * @return The {@link ElasticsearchClientReconnectConfiguration}.
     * @since 1.3.0
     */
    public ElasticsearchClientReconnectConfiguration getReconnectConfiguration() {
        if (reconnectConfiguration == null) {
            reconnectConfiguration = new ElasticsearchClientReconnectConfiguration();
        }

        return reconnectConfiguration;
    }

    /**
     * Sets the {@link ElasticsearchClientReconnectConfiguration}.
     *
     * @param reconnectConfiguration
     *         The {@link ElasticsearchClientReconnectConfiguration}.
     * @return This {@link ElasticsearchClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientConfiguration setReconnectConfiguration(ElasticsearchClientReconnectConfiguration reconnectConfiguration) {
        this.reconnectConfiguration = reconnectConfiguration;
        return this;
    }

    /**
     * Gets the {@link ElasticsearchClientReconnectConfiguration}.
     *
     * @return the {@link ElasticsearchClientReconnectConfiguration}.
     * @since 1.3.0
     */
    public ElasticsearchClientRequestConfiguration getRequestConfiguration() {
        if (requestConfiguration == null) {
            requestConfiguration = new ElasticsearchClientRequestConfiguration();
        }

        return requestConfiguration;
    }

    /**
     * Sets the {@link ElasticsearchClientReconnectConfiguration}.
     *
     * @param requestConfiguration
     *         the {@link ElasticsearchClientReconnectConfiguration}.
     * @return This {@link ElasticsearchClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientConfiguration setRequestConfiguration(ElasticsearchClientRequestConfiguration requestConfiguration) {
        this.requestConfiguration = requestConfiguration;
        return this;
    }

    /**
     * Gets the {@link ElasticsearchClientSslConfiguration}
     *
     * @return The {@link ElasticsearchClientSslConfiguration}
     * @since 1.3.0
     */
    public ElasticsearchClientSslConfiguration getSslConfiguration() {
        if (sslConfiguration == null) {
            sslConfiguration = new ElasticsearchClientSslConfiguration();
        }

        return sslConfiguration;
    }

    /**
     * Sets the {@link ElasticsearchClientSslConfiguration}
     *
     * @param sslConfiguration
     *         The {@link ElasticsearchClientSslConfiguration}
     * @return This {@link ElasticsearchClientConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientConfiguration setSslConfiguration(ElasticsearchClientSslConfiguration sslConfiguration) {
        this.sslConfiguration = sslConfiguration;
        return this;
    }

    public Optional<Integer> getNumberOfIOThreads() {
        return this.numberOfIOThreads;
    }

    public ElasticsearchClientConfiguration setNumberOfIOThreads(Integer numberOfIOThreads) {
        this.numberOfIOThreads = Optional.ofNullable(numberOfIOThreads)
                .filter(i -> i > 0);
        return this;
    }
}
