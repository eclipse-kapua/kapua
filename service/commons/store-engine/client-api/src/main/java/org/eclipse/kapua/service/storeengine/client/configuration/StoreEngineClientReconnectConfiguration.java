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
package org.eclipse.kapua.service.storeengine.client.configuration;

/**
 * The {@link StoreEngineClientReconnectConfiguration} definition.
 * <p>
 * It contains values for configuring reconnection properties.
 * It contains default values to ease the usage of the class.
 *
 * @since 1.3.0
 */
public class StoreEngineClientReconnectConfiguration {

    private int reconnectDelay = 30000;

    /**
     * Gets the reconnect delay after a connection lost with Elasticsearch.
     * <p>
     * Default value: 30000
     *
     * @return The reconnect delay after a connection lost with Elasticsearch
     * @since 1.3.0
     */
    public int getReconnectDelay() {
        return reconnectDelay;
    }

    /**
     * Sets the reconnect delay after a connection lost with Elasticsearch
     *
     * @param reconnectDelay The reconnect delay to wait
     * @return This {@link StoreEngineClientReconnectConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public StoreEngineClientReconnectConfiguration setReconnectDelay(int reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
        return this;
    }
}
