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
package org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel;

import java.io.Closeable;
import java.io.IOException;

/**
 * Vendor-agnostic view of the low-level REST client, be it the Elasticsearch or another one.
 *
 * @since 2.1.0
 */
public interface DeviceStoreClient extends Closeable {

    DeviceStoreClientRequest newRequest(String method, String endpoint);

    /**
     * @throws LowLevelSearchResponseException
     *         if the underlying client reports a non-2xx response as an exception.
     */
    DeviceStoreClientResponse performRequest(DeviceStoreClientRequest request) throws IOException;
}
