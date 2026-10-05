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
package org.eclipse.kapua.plugin.devicestorage.provider.elasticsearch;

import java.util.function.UnaryOperator;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.eclipse.kapua.service.storeengine.client.exception.ClientInitializationException;
import org.eclipse.kapua.service.storeengine.client.rest.lowlevel.StoreEngineClient;
import org.eclipse.kapua.service.storeengine.client.rest.lowlevel.StoreEngineClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 * {@link StoreEngineClientBuilder} backed by the Elasticsearch low-level REST client.
 *
 * @since 2.1.0
 */
public class ElasticsearchStoreEngineClientBuilder implements StoreEngineClientBuilder {

    public static final String ID = "elasticsearch";

    private RestClientBuilder restClientBuilder;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getVendorName() {
        return "Elasticsearch";
    }

    @Override
    public StoreEngineClientBuilder initializeAndSetHosts(HttpHost[] hosts) {
        restClientBuilder = RestClient.builder(hosts);
        return this;
    }

    @Override
    public StoreEngineClientBuilder setHttpClientConfigCallback(UnaryOperator<HttpAsyncClientBuilder> callback) throws ClientInitializationException {
        if (restClientBuilder == null) {
            throw new ClientInitializationException("RestClientBuilder is not initialized yet. Call initializeAndSetHosts() first.");
        }
        restClientBuilder.setHttpClientConfigCallback(callback::apply);
        return this;
    }

    @Override
    public StoreEngineClientBuilder setRequestConfigCallback(UnaryOperator<RequestConfig.Builder> callback) throws ClientInitializationException {
        if (restClientBuilder == null) {
            throw new ClientInitializationException("RestClientBuilder is not initialized yet. Call initializeAndSetHosts() first.");
        }
        restClientBuilder.setRequestConfigCallback(callback::apply);
        return this;
    }

    @Override
    public StoreEngineClient build() throws ClientInitializationException {
        if (restClientBuilder == null) {
            throw new ClientInitializationException("RestClientBuilder is not initialized yet. Call initializeAndSetHosts() first.");
        }
        return new ElasticsearchStoreEngineClient(restClientBuilder.build());
    }
}
