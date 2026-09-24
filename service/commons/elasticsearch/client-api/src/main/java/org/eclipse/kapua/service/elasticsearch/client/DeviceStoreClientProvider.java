/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.elasticsearch.client;

import org.eclipse.kapua.service.elasticsearch.client.configuration.DeviceStoreClientConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientClosingException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientProviderInitException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientUnavailableException;

/**
 * {@link DeviceStoreClientWrapper} wrapper definition.
 *
 * @param <C> {@link DeviceStoreClientWrapper} type.
 * @since 1.0.0
 */
public interface DeviceStoreClientProvider<C extends DeviceStoreClientWrapper> {

    /**
     * Initializes the {@link DeviceStoreClientProvider}.
     * <p>
     * The init methods can be called more than once in order to reinitialize the underlying datastore connection.
     * It the datastore was already initialized this method close the old one before initializing the new one.
     *
     * @return Itself, to chain invocations.
     * @throws ClientProviderInitException in case of error while initializing {@link DeviceStoreClientProvider}
     * @since 1.3.0
     */
    DeviceStoreClientProvider<C> init() throws ClientProviderInitException;

    /**
     * Closes the {@link DeviceStoreClientProvider} and all {@link DeviceStoreClientWrapper}s
     *
     * @throws ClientClosingException in case of error while closing the client.
     * @since 1.0.0
     */
    void close() throws ClientClosingException;

    /**
     * Sets the {@link DeviceStoreClientConfiguration} to use to instantiate and manage the {@link DeviceStoreClientWrapper}.
     *
     * @param deviceStoreClientConfiguration The {@link DeviceStoreClientConfiguration}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    DeviceStoreClientProvider<C> withClientConfiguration(DeviceStoreClientConfiguration deviceStoreClientConfiguration);

    /**
     * Sets the {@link ModelContext} to use in the {@link DeviceStoreClientWrapper}.
     *
     * @param modelContext The {@link DeviceStoreClientConfiguration}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    DeviceStoreClientProvider<C> withModelContext(ModelContext modelContext);

    /**
     * Sets the {@link QueryConverter} to use in the {@link DeviceStoreClientWrapper}/
     *
     * @param queryConverter The {@link QueryConverter}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    DeviceStoreClientProvider<C> withModelConverter(QueryConverter queryConverter);


    /**
     * Gets an initialized {@link DeviceStoreClientWrapper} instance.
     *
     * @return An initialized {@link DeviceStoreClientWrapper} instance.
     * @throws ClientUnavailableException if the client has not being initialized.
     * @since 1.0.0
     */
    C getDeviceStoreClient() throws ClientUnavailableException, ClientProviderInitException;
}
