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
package org.eclipse.kapua.service.storeengine.client.rest.lowlevel;

import java.io.IOException;

/**
 * Thrown by {@link StoreEngineClient#performRequest(StoreEngineClientRequest)} when the underlying client reports a non-2xx response as an exception rather than
 * returning it, carrying the {@link StoreEngineClientResponse} that caused it.
 *
 * @since 2.1.0
 */
public class LowLevelSearchResponseException extends IOException {

    private final StoreEngineClientResponse response;

    public LowLevelSearchResponseException(StoreEngineClientResponse response, Throwable cause) {
        super(cause);
        this.response = response;
    }

    public StoreEngineClientResponse getResponse() {
        return response;
    }
}
