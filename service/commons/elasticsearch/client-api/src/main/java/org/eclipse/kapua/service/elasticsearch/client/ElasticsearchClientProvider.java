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

import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchClientConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientClosingException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientProviderInitException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientUnavailableException;

/**
 * {@link ElasticsearchClientWrapper} wrapper definition.
 *
 * @param <C> {@link ElasticsearchClientWrapper} type.
 * @since 1.0.0
 */
public interface ElasticsearchClientProvider<C extends ElasticsearchClientWrapper> {

    /**
     * Initializes the {@link ElasticsearchClientProvider}.
     * <p>
     * The init methods can be called more than once in order to reinitialize the underlying datastore connection.
     * It the datastore was already initialized this method close the old one before initializing the new one.
     *
     * @return Itself, to chain invocations.
     * @throws ClientProviderInitException in case of error while initializing {@link ElasticsearchClientProvider}
     * @since 1.3.0
     */
    ElasticsearchClientProvider<C> init() throws ClientProviderInitException;

    /**
     * Closes the {@link ElasticsearchClientProvider} and all {@link ElasticsearchClientWrapper}s
     *
     * @throws ClientClosingException in case of error while closing the client.
     * @since 1.0.0
     */
    void close() throws ClientClosingException;

    /**
     * Sets the {@link ElasticsearchClientConfiguration} to use to instantiate and manage the {@link ElasticsearchClientWrapper}.
     *
     * @param elasticsearchClientConfiguration The {@link ElasticsearchClientConfiguration}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    ElasticsearchClientProvider<C> withClientConfiguration(ElasticsearchClientConfiguration elasticsearchClientConfiguration);

    /**
     * Sets the {@link ModelContext} to use in the {@link ElasticsearchClientWrapper}.
     *
     * @param modelContext The {@link ElasticsearchClientConfiguration}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    ElasticsearchClientProvider<C> withModelContext(ModelContext modelContext);

    /**
     * Sets the {@link QueryConverter} to use in the {@link ElasticsearchClientWrapper}/
     *
     * @param queryConverter The {@link QueryConverter}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    ElasticsearchClientProvider<C> withModelConverter(QueryConverter queryConverter);


    /**
     * Gets an initialized {@link ElasticsearchClientWrapper} instance.
     *
     * @return An initialized {@link ElasticsearchClientWrapper} instance.
     * @throws ClientUnavailableException if the client has not being initialized.
     * @since 1.0.0
     */
    C getElasticsearchClient() throws ClientUnavailableException, ClientProviderInitException;
}
