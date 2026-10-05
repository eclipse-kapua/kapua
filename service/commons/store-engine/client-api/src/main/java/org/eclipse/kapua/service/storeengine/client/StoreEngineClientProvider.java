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
package org.eclipse.kapua.service.storeengine.client;

import org.eclipse.kapua.service.storeengine.client.configuration.StoreEngineClientConfiguration;
import org.eclipse.kapua.service.storeengine.client.exception.ClientClosingException;
import org.eclipse.kapua.service.storeengine.client.exception.ClientProviderInitException;
import org.eclipse.kapua.service.storeengine.client.exception.ClientUnavailableException;

/**
 * {@link StoreEngineClientWrapper} wrapper definition.
 *
 * @param <C> {@link StoreEngineClientWrapper} type.
 * @since 1.0.0
 */
public interface StoreEngineClientProvider<C extends StoreEngineClientWrapper> {

    /**
     * Initializes the {@link StoreEngineClientProvider}.
     * <p>
     * The init methods can be called more than once in order to reinitialize the underlying datastore connection.
     * It the datastore was already initialized this method close the old one before initializing the new one.
     *
     * @return Itself, to chain invocations.
     * @throws ClientProviderInitException in case of error while initializing {@link StoreEngineClientProvider}
     * @since 1.3.0
     */
    StoreEngineClientProvider<C> init() throws ClientProviderInitException;

    /**
     * Closes the {@link StoreEngineClientProvider} and all {@link StoreEngineClientWrapper}s
     *
     * @throws ClientClosingException in case of error while closing the client.
     * @since 1.0.0
     */
    void close() throws ClientClosingException;

    /**
     * Sets the {@link StoreEngineClientConfiguration} to use to instantiate and manage the {@link StoreEngineClientWrapper}.
     *
     * @param storeEngineClientConfiguration The {@link StoreEngineClientConfiguration}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    StoreEngineClientProvider<C> withClientConfiguration(StoreEngineClientConfiguration storeEngineClientConfiguration);

    /**
     * Sets the {@link ModelContext} to use in the {@link StoreEngineClientWrapper}.
     *
     * @param modelContext The {@link StoreEngineClientConfiguration}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    StoreEngineClientProvider<C> withModelContext(ModelContext modelContext);

    /**
     * Sets the {@link QueryConverter} to use in the {@link StoreEngineClientWrapper}/
     *
     * @param queryConverter The {@link QueryConverter}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    StoreEngineClientProvider<C> withModelConverter(QueryConverter queryConverter);


    /**
     * Gets an initialized {@link StoreEngineClientWrapper} instance.
     *
     * @return An initialized {@link StoreEngineClientWrapper} instance.
     * @throws ClientUnavailableException if the client has not being initialized.
     * @since 1.0.0
     */
    C getDeviceStoreClient() throws ClientUnavailableException, ClientProviderInitException;
}
