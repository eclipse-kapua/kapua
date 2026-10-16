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
import org.eclipse.kapua.service.storeengine.client.rest.lowlevel.StoreEngineClientResponse;
import org.elasticsearch.client.Response;

/**
 * {@link StoreEngineClientResponse} backed by the Elasticsearch low-level REST client.
 *
 * @since 2.1.0
 */
class ElasticsearchStoreEngineClientResponse implements StoreEngineClientResponse {

    private final Response response;

    ElasticsearchStoreEngineClientResponse(Response response) {
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
