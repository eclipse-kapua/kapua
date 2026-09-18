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

import org.apache.http.HttpEntity;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClientResponse;
import org.elasticsearch.client.Response;

/**
 * {@link DeviceStoreClientResponse} backed by the Elasticsearch low-level REST client.
 *
 * @since 2.1.0
 */
class ElasticsearchDeviceStoreClientResponse implements DeviceStoreClientResponse {

    private final Response response;

    ElasticsearchDeviceStoreClientResponse(Response response) {
        this.response = response;
    }

    @Override
    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public String getReasonPhrase() {
        return response.getStatusLine().getReasonPhrase();
    }

    @Override
    public HttpEntity getEntity() {
        return response.getEntity();
    }
}
