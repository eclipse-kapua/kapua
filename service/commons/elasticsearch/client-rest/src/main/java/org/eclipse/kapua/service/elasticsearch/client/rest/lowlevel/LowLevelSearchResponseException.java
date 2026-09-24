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

import java.io.IOException;

/**
 * Thrown by {@link DeviceStoreClient#performRequest(DeviceStoreClientRequest)} when the underlying client reports a non-2xx response as an exception rather than
 * returning it, carrying the {@link DeviceStoreClientResponse} that caused it.
 *
 * @since 2.1.0
 */
public class LowLevelSearchResponseException extends IOException {

    private final DeviceStoreClientResponse response;

    public LowLevelSearchResponseException(DeviceStoreClientResponse response, Throwable cause) {
        super(cause);
        this.response = response;
    }

    public DeviceStoreClientResponse getResponse() {
        return response;
    }
}
