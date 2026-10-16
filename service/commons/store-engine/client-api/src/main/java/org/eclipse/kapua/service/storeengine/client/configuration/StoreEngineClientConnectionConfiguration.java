/*******************************************************************************
 * Copyright (c) 2020, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.storeengine.client.configuration;

import java.util.Optional;

/**
 * The {@link StoreEngineClientConnectionConfiguration} contains configurations
 * to customize the behavior of the connection pool manager of the ES rest client.
 * @since 2.1.0
 */
public class StoreEngineClientConnectionConfiguration {
    private Optional<Integer> numberOfIOThreads;
    private Optional<Integer> maxTotalConnections;
    private Optional<Integer> defaultMaxConnectionsPerRoute;

    /**
     * Gets the number of I/O dispatch threads to be used by the I/O reactor.
     * 
     * @return The number of I/O dispatch threads to be used by the I/O reactor.
     * @since 2.1.0
     */
    public Optional<Integer> getNumberOfIOThreads() {
        return this.numberOfIOThreads;
    }

    /**
     * Sets the number of I/O dispatch threads to be used by the I/O reactor.
     * 
     * @param numberOfIOThreads The number of I/O dispatch threads to be used by the I/O reactor.
     * @return This {@link StoreEngineClientConnectionConfiguration} to chain method invocation.
     * @since 2.1.0
     */
    public StoreEngineClientConnectionConfiguration setNumberOfIOThreads(Optional<Integer> numberOfIOThreads) {
        this.numberOfIOThreads = numberOfIOThreads
                .filter(i -> i > 0);
        return this;
    }

    /**
     * Gets the total number of connections available in the connection pool manager.
     * 
     * @return The total number of connections available in the connection pool manager.
     * @since 2.1.0
     */
    public Optional<Integer> getMaxTotal() {
        return this.maxTotalConnections;
    }

    /**
     * Sets the total number of connections available in the connection pool manager.
     * 
     * @param maxTotalConnections The total number of connections available in the connection pool manager.
     * @return This {@link StoreEngineClientConnectionConfiguration} to chain method invocation.
     * @since 2.1.0
     */
    public StoreEngineClientConnectionConfiguration setMaxTotal(Optional<Integer> maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections
                .filter(i -> i > 0);
        return this;
    }

    /**
     * Gets the default max number of connections per route in the connection pool manager.
     * 
     * @return The default max number of connections per route in the connection pool manager.
     * @since 2.1.0
     */
    public Optional<Integer> getDefaultMaxPerRoute() {
        return this.defaultMaxConnectionsPerRoute;
    }

    /**
     * Sets the default max number of connections per route in the connection pool manager.
     * 
     * @param defaultMaxConnectionsPerRoute The default max number of connections per route in the connection pool manager.
     * @return This {@link StoreEngineClientConnectionConfiguration} to chain method invocation.
     * @since 2.1.0
     */
    public StoreEngineClientConnectionConfiguration setDefaultMaxPerRoute(Optional<Integer> defaultMaxConnectionsPerRoute) {
        this.defaultMaxConnectionsPerRoute = defaultMaxConnectionsPerRoute
                .filter(i -> i > 0);
        return this;
    }

}
