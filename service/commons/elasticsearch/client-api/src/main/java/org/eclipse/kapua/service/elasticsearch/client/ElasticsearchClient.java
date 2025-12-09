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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchClientConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientInitializationException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientUnavailableException;
import org.eclipse.kapua.service.elasticsearch.client.model.BulkUpdateRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.BulkUpdateResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.IndexRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.IndexResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.InsertRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.InsertResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.ResultList;
import org.eclipse.kapua.service.elasticsearch.client.model.UpdateRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.UpdateResponse;

import java.io.Closeable;

/**
 * Elasticsearch client definition.
 * <p>
 * It defines the methods (crud and utilities) to be exposed to the caller.
 * The Elasticsearch client implementation should provide a static init method and a static getInstance method that return the already initialized client instance.
 *
 * @since 1.0.0
 */
public interface ElasticsearchClient<C extends Closeable> {

    /**
     * Initializes the underlying Elasticsearch connection.
     *
     * @since 1.0.0
     */
    void init() throws ClientInitializationException;

    /**
     * Closes the underlying Elasticsearch connection.
     *
     * @throws ClientUnavailableException If the {@link ElasticsearchClient} was not initialized and this is invoked.
     * @since 1.0.0
     */
    void close() throws ClientUnavailableException;

    /**
     * Gets the org.elasticsearch.Client.
     *
     * @return The org.elasticsearch.Client.
     * @since 1.3.0
     */
    C getClient();

    /**
     * Sets the org.elasticsearch.Client to use with {@link ElasticsearchClient}.
     *
     * @param client The org.elasticsearch.Client
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    ElasticsearchClient<C> withClient(C client);

    /**
     * Gets the {@link ElasticsearchClientConfiguration}.
     *
     * @return The {@link ElasticsearchClientConfiguration}.
     * @since 1.3.0
     */
    ElasticsearchClientConfiguration getClientConfiguration();

    /**
     * Sets the {@link ElasticsearchClientConfiguration} to use with the {@link ElasticsearchClient}.
     *
     * @param clientConfiguration The {@link ElasticsearchClientConfiguration}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    ElasticsearchClient<C> withClientConfiguration(ElasticsearchClientConfiguration clientConfiguration);

    /**
     * Gets the {@link ModelContext}
     *
     * @return The {@link ModelContext}
     * @since 1.3.0
     */
    ModelContext getModelContext();

    /**
     * Sets the {@link ModelContext}.
     *
     * @param modelContext The {@link ModelContext}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    ElasticsearchClient<C> withModelContext(ModelContext modelContext);

    /**
     * Gets the {@link QueryConverter}
     *
     * @return The {@link QueryConverter}
     * @since 1.3.0
     */
    QueryConverter getModelConverter();

    /**
     * Sets the {@link QueryConverter}.
     *
     * @param modelConverter The {@link QueryConverter}.
     * @return Itself, to chain invocations.
     * @since 1.3.0
     */
    ElasticsearchClient<C> withModelConverter(QueryConverter modelConverter);

    /**
     * Inserts a document.
     *
     * @param insertRequest The {@link InsertRequest} to perform.
     * @return The {@link InsertResponse} from Elasticsearch.
     * @throws ClientException if error occurs while inserting document.
     * @since 1.0.0
     */
    InsertResponse insert(InsertRequest insertRequest) throws ClientException;

    /**
     * Upserts a document.
     *
     * @param updateRequest The {@link UpdateRequest} to perform.
     * @return the {@link UpdateResponse} from Elasticsearch
     * @throws ClientException if error occurs while upserting document.
     * @since 1.0.0
     */
    UpdateResponse upsert(UpdateRequest updateRequest) throws ClientException;

    /**
     * Bulk upserts.
     *
     * @param bulkUpdateRequest The {@link BulkUpdateRequest} to perform.
     * @return the {@link BulkUpdateResponse} from Elasticsearch
     * @throws ClientException if error occurs while upserting documents.
     * @since 1.0.0
     */
    BulkUpdateResponse upsert(BulkUpdateRequest bulkUpdateRequest) throws ClientException;

    /**
     * Finds by query.
     * <p>
     * This method returns the first result found that matches the query.
     *
     * @param index          The index to look for.
     * @param query          The query to perform.
     * @param clazz          The expected {@link Object#getClass()}
     * @return The first result found that matches the query.
     * @throws ClientException if error occurs while querying.
     * @since 1.0.0
     */
    <T> T find(String index, Object query, Class<T> clazz) throws ClientException;

    /**
     * Finds by query.
     *
     * @param index          The index to look for.
     * @param query          The query to perform.
     * @param clazz          The expected {@link Object#getClass()}
     * @return The {@link ResultList} that matches the query.
     * @throws ClientException if error occurs while querying.
     * @since 1.0.0
     */
    <T> ResultList<T> query(String index, Object query, Class<T> clazz) throws ClientException;

    /**
     * Counts by query.
     *
     * @param index          The index to look for.
     * @param query          The query to perform.
     * @return The number of matching results.
     * @throws ClientException if error occurs while counting.
     * @since 1.0.0
     */
    long count(String index, Object query) throws ClientException;

    /**
     * Deletes by id.
     *
     * @param index          The index to delete.
     * @param id             The id to delete
     * @throws ClientException if error occurs while deleting.
     * @since 1.0.0
     */
    void delete(String index, String id) throws ClientException;

    /**
     * Deletes by query.
     *
     * @param index          The index to delete.
     * @param query          The query to perform.
     * @throws ClientException if error occurs while deleting.
     * @since 1.0.0
     */
    void deleteByQuery(String index, Object query) throws ClientException;
    // Indexes / mappings section

    /**
     * Checks if the index exists.
     *
     * @param indexRequest The {@link IndexRequest} to perform.
     * @return The {@link IndexResponse} from Elasticsearch
     * @throws ClientException if error occurs while checking.
     * @since 1.0.0
     */
    IndexResponse isIndexExists(IndexRequest indexRequest) throws ClientException;

    /**
     * Creates the index.
     *
     * @param indexName     The index name to create.
     * @param indexSettings The index settings.
     * @throws ClientException if error occurs while inserting.
     * @since 1.0.0
     */
    void createIndex(String indexName, ObjectNode indexSettings) throws ClientException;

    /**
     * Checks if the mapping exists.
     *
     * @param index The index to look for.
     * @return {@code true} if mapping exists, {@code false} otherwise
     * @throws ClientException if error occurs while checking.
     * @since 1.0.0
     */
    boolean isMappingExists(String index) throws ClientException;

    /**
     * Puts the mapping.
     *
     * @param index          The index to put.
     * @param mapping        The mapping to put.
     * @throws ClientException if error occurs while putting.
     * @since 1.0.0
     */
    void putMapping(String index, JsonNode mapping) throws ClientException;

    /**
     * Forces the Elasticsearch to refresh the indexes.
     *
     * @throws ClientException if error occurs while refreshing.
     * @since 1.0.0
     */
    void refreshAllIndexes() throws ClientException;

    /**
     * Forces the Elasticsearch to refresh a specific index.
     *
     * @throws ClientException if error occurs while refreshing.
     * @since 2.1.0
     */
    void refreshIndex(String index) throws ClientException;

    /**
     * Deletes all indexes.
     *
     * <b>
     * WARNING!
     * This call will erase the whole database!
     * Be careful using it! :)
     * </b>
     *
     * @throws ClientException if error occurs while deleting.
     * @since 1.0.0
     */
    void deleteAllIndexes() throws ClientException;

    /**
     * Deletes the specified indexes.
     *
     * <b>
     * WARNING!
     * Once dropped the indexes cannot be restored!
     * Be careful using it! :)
     * </b>
     *
     * @param indexes the names to be deleted.
     * @throws ClientException if error occurs while deleting.
     * @since 1.0.0
     */
    void deleteIndexes(String... indexes) throws ClientException;

    /**
     * Finds indexes by prefix.
     *
     * @param indexRequest The {@link IndexRequest} to perform.
     * @return The {@link IndexResponse} from Elasticsearch.
     * @throws ClientException if error occurs while finding.
     * @since 1.0.0
     */
    IndexResponse findIndexes(IndexRequest indexRequest) throws ClientException;
}
