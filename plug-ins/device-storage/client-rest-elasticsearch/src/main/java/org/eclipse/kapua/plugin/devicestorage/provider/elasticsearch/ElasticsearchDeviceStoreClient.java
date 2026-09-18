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

import java.io.IOException;

import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClient;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClientRequest;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClientResponse;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.LowLevelSearchResponseException;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

/**
 * {@link DeviceStoreClient} backed by the Elasticsearch low-level REST client.
 *
 * @since 2.1.0
 */
public class ElasticsearchDeviceStoreClient implements DeviceStoreClient {

    private final RestClient restClient;

    ElasticsearchDeviceStoreClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Escape hatch for callers that are known to depend on the Elasticsearch REST client directly (e.g. interop with {@code RestHighLevelClient}), rather than
     * going through {@link DeviceStoreClient}.
     *
     * @return The wrapped Elasticsearch {@link RestClient}.
     */
    public RestClient unwrap() {
        return restClient;
    }

    @Override
    public DeviceStoreClientRequest newRequest(String method, String endpoint) {
        return new ElasticsearchDeviceStoreClientRequest(new Request(method, endpoint));
    }

    @Override
    public DeviceStoreClientResponse performRequest(DeviceStoreClientRequest request) throws IOException {
        try {
            return new ElasticsearchDeviceStoreClientResponse(restClient.performRequest(((ElasticsearchDeviceStoreClientRequest) request).unwrap()));
        } catch (ResponseException e) {
            throw new LowLevelSearchResponseException(new ElasticsearchDeviceStoreClientResponse(e.getResponse()), e);
        }
    }

    @Override
    public void close() throws IOException {
        restClient.close();
    }
}
