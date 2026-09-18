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

import java.util.function.UnaryOperator;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientInitializationException;

/**
 * Vendor-agnostic view of the low-level REST client builder, be it the Elasticsearch or another one.
 * <p>
 * The callbacks are expressed in terms of Apache HttpComponents types since both vendors' builders customize the very same underlying HTTP client, unlike
 * {@link DeviceStoreClient}/{@link DeviceStoreClientRequest}/{@link DeviceStoreClientResponse} which each vendor forked into its own package.
 *
 * @since 2.1.0
 */
public interface DeviceStoreClientBuilder {

    /**
     * Stable, machine-readable identifier for this vendor (e.g. {@code "elasticsearch"}, or for example {@code "opensearch"}).
     * <p>
     * Matched, case-insensitively, against a {@code *.client.engine} setting to pick which implementation to use out of the {@link java.util.Set} of all
     * {@link DeviceStoreClientBuilder}s contributed on the classpath. Unlike {@link #getVendorName()}, this value is a contract other code matches against and
     * must stay stable across releases.
     *
     * @since 2.1.0
     */
    String getId();

    /**
     * Human-readable vendor name, used for logging/diagnostics only.
     */
    String getVendorName();

    DeviceStoreClientBuilder initializeAndSetHosts(HttpHost[] hosts) throws ClientInitializationException;

    DeviceStoreClientBuilder setHttpClientConfigCallback(UnaryOperator<HttpAsyncClientBuilder> callback) throws ClientInitializationException;

    DeviceStoreClientBuilder setRequestConfigCallback(UnaryOperator<RequestConfig.Builder> callback) throws ClientInitializationException;

    DeviceStoreClient build() throws ClientInitializationException;
}
