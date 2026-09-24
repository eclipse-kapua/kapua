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

import org.apache.http.HttpEntity;

/**
 * Vendor-agnostic view of the response object returned by the underlying low-level REST client, be it the Elasticsearch or the another one.
 *
 * @since 2.1.0
 */
public interface DeviceStoreClientResponse {

    int getStatusCode();

    String getReasonPhrase();

    HttpEntity getEntity();
}
