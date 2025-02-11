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
package org.eclipse.kapua.service.elasticsearch.client;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.elasticsearch.client.model.BulkUpdateRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.BulkUpdateResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.IndexRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.IndexResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.Response;
import org.eclipse.kapua.service.elasticsearch.client.model.ResultList;
import org.eclipse.kapua.service.elasticsearch.client.model.UpdateRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.UpdateResponse;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.StorableListResult;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.StorableQuery;
import org.eclipse.kapua.service.storable.model.query.predicate.IdsPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.repository.StorableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class ElasticsearchRepository<
        T extends Storable,
        L extends StorableListResult<T>,
        Q extends StorableQuery> implements StorableRepository<T, L, Q> {

    protected final ElasticsearchClientProvider elasticsearchClientProviderInstance;
    private final Class<T> clazz;
    protected final StorablePredicateFactory storablePredicateFactory;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final LocalCache<String, Boolean> indexUpserted;
    private final Function<KapuaId, Q> querySupplier;
    private final Supplier<L> listSupplier;

    protected abstract String indexResolver(KapuaId scopeId);

    protected abstract JsonNode getIndexSchema() throws MappingException;

    protected abstract ObjectNode getMappingSchema(String idxName) throws MappingException;

    protected abstract StorableId idExtractor(T storable);

    protected ElasticsearchRepository(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            Class<T> clazz,
            StorablePredicateFactory storablePredicateFactory,
            LocalCache<String, Boolean> indexesCache,
            Function<KapuaId, Q> querySupplier,
            Supplier<L> listSupplier) {
        this.elasticsearchClientProviderInstance = elasticsearchClientProviderInstance;
        this.storablePredicateFactory = storablePredicateFactory;
        this.clazz = clazz;
        this.indexUpserted = indexesCache;
        this.querySupplier = querySupplier;
        this.listSupplier = listSupplier;
    }

    protected ElasticsearchRepository(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            Class<T> clazz,
            StorablePredicateFactory storablePredicateFactory,
            Function<KapuaId, Q> querySupplier,
            Supplier<L> listSupplier) {
        this.elasticsearchClientProviderInstance = elasticsearchClientProviderInstance;
        this.storablePredicateFactory = storablePredicateFactory;
        this.clazz = clazz;
        this.indexUpserted = new LocalCache<>(0, null);
        this.querySupplier = querySupplier;
        this.listSupplier = listSupplier;
    }

    @Override
    public T find(KapuaId scopeId, StorableId id) {
        return doFind(scopeId, indexResolver(scopeId), id);
    }

    protected T doFind(KapuaId scopeId, String indexName, StorableId id) {
        try {
            final Q idsQuery = querySupplier.apply(scopeId);
            idsQuery.setLimit(1);

            final IdsPredicate idsPredicate = storablePredicateFactory.newIdsPredicate();
            idsPredicate.addId(id);
            idsQuery.setPredicate(idsPredicate);

            synchIndex(indexName);
            final T res;
            res = (T) elasticsearchClientProviderInstance.getElasticsearchClient().<T>find(indexName, idsQuery, clazz);
            return res;
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    private void synchIndex(String indexName) {
        if (!indexUpserted.containsKey(indexName)) {
            synchronized (clazz) {
                doUpsertIndex(indexName);
                indexUpserted.put(indexName, true);
            }
        }
    }

    @Override
    public L query(Q query) {
        try {
            final String indexName = indexResolver(query.getScopeId());
            synchIndex(indexName);
            final ResultList<T> partialResult = elasticsearchClientProviderInstance.getElasticsearchClient().query(indexName, query, clazz);
            final L res = listSupplier.get();
            res.addItems(partialResult.getResult());
            res.setTotalCount(partialResult.getTotalCount());
            setLimitExceed(query, partialResult.getTotalHitsExceedsCount(), res);
            return res;
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Storable> void setLimitExceed(StorableQuery query, boolean hitsExceedsTotalCount, StorableListResult<T> list) {
        int offset = query.getOffset() != null ? query.getOffset() : 0;
        if (query.getLimit() != null) {
            if (hitsExceedsTotalCount || //pre-condition: there are more than 10k documents in ES && query limit is <= 10k
                    list.getTotalCount() > offset + query.getLimit()) {
                list.setLimitExceeded(true);
            }
        }
    }

    @Override
    public long count(Q query) {
        try {
            final String indexName = indexResolver(query.getScopeId());
            synchIndex(indexName);

            return elasticsearchClientProviderInstance.getElasticsearchClient().count(indexName, query);
        } catch (ClientException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(KapuaId scopeId, StorableId id) {
        final String indexName = indexResolver(scopeId);
        doDelete(indexName, id);
    }

    protected void doDelete(String indexName, StorableId id) {
        try {
            synchIndex(indexName);

            elasticsearchClientProviderInstance.getElasticsearchClient().delete(indexName, id.toString());
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Q query) {
        try {
            elasticsearchClientProviderInstance.getElasticsearchClient().deleteByQuery(indexResolver(query.getScopeId()), query);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String upsert(String itemId, T item) {
        try {
            final String indexName = indexResolver(item.getScopeId());
            synchIndex(indexName);

            final UpdateRequest request = new UpdateRequest(itemId.toString(), indexName, item);
            final UpdateResponse upsertResponse;
            upsertResponse = elasticsearchClientProviderInstance.getElasticsearchClient().upsert(request);
            final String responseId = upsertResponse.getId();
            logger.debug("Upsert  successfully executed [{}, {} - {}]", indexName, itemId, responseId);
            return responseId;
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> upsert(List<T> items) {
        try {
            final List<UpdateRequest> requests = items.stream()
                    .map(storableItem -> new UpdateRequest(
                            idExtractor(storableItem).toString(),
                            indexResolver(storableItem.getScopeId()),
                            storableItem))
                    .collect(Collectors.toList());
            requests.stream().map(r -> r.getStorable().getScopeId()).collect(Collectors.toSet())
                    .forEach(scopeId -> {
                        final String indexName = indexResolver(scopeId);
                        synchIndex(indexName);
                    });
            final BulkUpdateRequest bulkUpdateRequest = new BulkUpdateRequest();
            bulkUpdateRequest.setRequest(requests);
            final BulkUpdateResponse updateResponse = elasticsearchClientProviderInstance
                    .getElasticsearchClient()
                    .upsert(bulkUpdateRequest);
            return updateResponse.getResponse()
                    .stream()
                    .peek(response -> {
                        String index = response.getIndex();
                        String id = response.getId();
                        logger.debug("Upsert successfully executed [{}, {}]", index, id);
                    }).map(Response::getId)
                    .collect(Collectors.toSet());
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doUpsertIndex(String indexName) {
        final ElasticsearchClient elasticsearchClient;
        try {
            elasticsearchClient = elasticsearchClientProviderInstance.getElasticsearchClient();
            // Check existence of the kapua internal indexes
            IndexResponse indexExistsResponse = elasticsearchClient.isIndexExists(new IndexRequest(indexName));
            if (!indexExistsResponse.isIndexExists()) {
                elasticsearchClient.createIndex(indexName, getMappingSchema(indexName));
                logger.info("Index created: {}", indexExistsResponse);
                elasticsearchClient.putMapping(indexName, getIndexSchema());
            }
        } catch (ClientException | MappingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refreshAllIndexes() {
        try {
            this.indexUpserted.invalidateAll();
            elasticsearchClientProviderInstance.getElasticsearchClient().refreshAllIndexes();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshIndex(String indexExp) {
        try {
            this.indexUpserted.invalidateAll();
            elasticsearchClientProviderInstance.getElasticsearchClient().refreshIndex(indexExp);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllIndexes() {
        try {
            this.indexUpserted.invalidateAll();
            elasticsearchClientProviderInstance.getElasticsearchClient().deleteAllIndexes();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteIndexes(String indexExp) {
        try {
            this.indexUpserted.invalidateAll();
            elasticsearchClientProviderInstance.getElasticsearchClient().deleteIndexes(indexExp);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

}
