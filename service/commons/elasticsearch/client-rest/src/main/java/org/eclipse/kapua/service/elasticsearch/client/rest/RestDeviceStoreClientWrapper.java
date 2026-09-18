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
package org.eclipse.kapua.service.elasticsearch.client.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.eclipse.kapua.commons.util.RandomUtils;
import org.eclipse.kapua.service.elasticsearch.client.DeviceStoreClientWrapper;
import org.eclipse.kapua.service.elasticsearch.client.ModelContext;
import org.eclipse.kapua.service.elasticsearch.client.QueryConverter;
import org.eclipse.kapua.service.elasticsearch.client.SchemaKeys;
import org.eclipse.kapua.service.elasticsearch.client.configuration.DeviceStoreClientConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientActionResponseException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientCommunicationException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientErrorCodes;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientInitializationException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientInternalError;
import org.eclipse.kapua.service.elasticsearch.client.model.BulkUpdateRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.BulkUpdateResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.IndexRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.IndexResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.InsertRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.InsertResponse;
import org.eclipse.kapua.service.elasticsearch.client.model.ResultList;
import org.eclipse.kapua.service.elasticsearch.client.model.UpdateRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.UpdateResponse;
import org.eclipse.kapua.service.elasticsearch.client.rest.exception.RequestEntityWriteError;
import org.eclipse.kapua.service.elasticsearch.client.rest.exception.ResponseEntityReadError;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClient;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClientRequest;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClientResponse;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.LowLevelSearchResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * Client implementation based on Elasticsearch rest client.
 * <p>
 * The Elasticsearch client provider is instantiated as singleton.
 *
 * @since 1.0.0
 */
public class RestDeviceStoreClientWrapper implements DeviceStoreClientWrapper<DeviceStoreClient> {

    private static final Logger LOG = LoggerFactory.getLogger(RestDeviceStoreClientWrapper.class);

    private DeviceStoreClient wrappedClient;
    private DeviceStoreClientConfiguration clientConfiguration;
    private ModelContext modelContext;
    private QueryConverter modelConverter;

    private static final Random RANDOM = RandomUtils.getInstance();
    private static final String MSG_EMPTY_ERROR = "Empty error message";

    private final ObjectMapper objectMapper;
    private static final String CLIENT_HITS_MAX_VALUE_EXCEEDED = "Total hits exceeds integer max value";
    private static final String QUERY_CONVERTED_QUERY = "Query - converted query: '{}'";
    private static final String COUNT_CONVERTED_QUERY = "Count - converted query: '{}'";
    private final MetricsEsClient metricsEsClient;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    @Inject
    public RestDeviceStoreClientWrapper(MetricsEsClient metricsEsClient) {
        this.metricsEsClient = metricsEsClient;
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public void init() throws ClientInitializationException {
        if (getClientConfiguration() == null) {
            throw new ClientInitializationException("Client configuration not defined");
        }
        if (getModelContext() == null) {
            throw new ClientInitializationException("Missing model context");
        }
        if (getModelConverter() == null) {
            throw new ClientInitializationException("Missing model converter");
        }
    }

    @Override
    public void close() {
        if (wrappedClient != null) {
            try {
                wrappedClient.close();
            } catch (IOException e) {
                LOG.error("Error closing client", e);
            }
        }
    }

    // ------- BUILDER METHODS - START

    @Override
    public DeviceStoreClient getClient() {
        return this.wrappedClient;
    }

    @Override
    public DeviceStoreClientWrapper<DeviceStoreClient> withClient(DeviceStoreClient client) {
       this.wrappedClient = client;
       return this;
    }

    @Override
    public DeviceStoreClientConfiguration getClientConfiguration() {
        return this.clientConfiguration;
    }

    @Override
    public DeviceStoreClientWrapper<DeviceStoreClient> withClientConfiguration(DeviceStoreClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        return this;
    }

    @Override
    public ModelContext getModelContext() {
        return this.modelContext;
    }

    @Override
    public DeviceStoreClientWrapper<DeviceStoreClient> withModelContext(ModelContext modelContext) {
        this.modelContext = modelContext;
        return this;
    }

    @Override
    public QueryConverter getModelConverter() {
        return this.modelConverter;
    }

    @Override
    public DeviceStoreClientWrapper<DeviceStoreClient> withModelConverter(QueryConverter modelConverter) {
        this.modelConverter = modelConverter;
        return this;
    }

    // // ------- BUILDER METHODS - END

    @Override
    public InsertResponse insert(InsertRequest insertRequest) throws ClientException {
        Map<String, Object> insertRequestStorableMap = getModelContext().marshal(insertRequest.getStorable());
        LOG.debug("Insert - converted object: '{}'", insertRequestStorableMap);

        String json = writeRequestFromMap(insertRequestStorableMap);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_PUT, ElasticsearchResourcePaths.insertType(insertRequest));
        request.setJsonEntity(json);
        DeviceStoreClientResponse insertResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), insertRequest.getIndex(), "INSERT");

        if (isRequestSuccessful(insertResponse)) {
            JsonNode responseNode = readResponseAsJsonNode(insertResponse);

            String id = responseNode.get(ElasticsearchKeywords.KEY_DOC_ID).asText();
            String index = responseNode.get(ElasticsearchKeywords.KEY_DOC_INDEX).asText();
            return new InsertResponse(id, index);
        } else {
            throw buildExceptionFromUnsuccessfulResponse("Insert", insertResponse);
        }
    }

    @Override
    public UpdateResponse upsert(UpdateRequest updateRequest) throws ClientException {
        Map<String, Object> updateRequestStorableMap = getModelContext().marshal(updateRequest.getStorable());

        Map<String, Object> updateRequestMap = new HashMap<>();
        updateRequestMap.put(ElasticsearchKeywords.KEY_DOC, updateRequestStorableMap);
        updateRequestMap.put(ElasticsearchKeywords.KEY_DOC_AS_UPSERT, true);
        LOG.debug("Upsert - converted object: '{}'", updateRequestMap);

        String json = writeRequestFromMap(updateRequestMap);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_POST, ElasticsearchResourcePaths.upsert(updateRequest.getIndex(), updateRequest.getId()));
        request.setJsonEntity(json);
        DeviceStoreClientResponse updateResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), updateRequest.getIndex(), "UPSERT");

        if (isRequestSuccessful(updateResponse)) {
            JsonNode responseNode = readResponseAsJsonNode(updateResponse);

            String id = responseNode.get(ElasticsearchKeywords.KEY_DOC_ID).asText();
            String index = responseNode.get(ElasticsearchKeywords.KEY_DOC_INDEX).asText();
            return new UpdateResponse(id, index);
        } else {
            throw buildExceptionFromUnsuccessfulResponse("Update", updateResponse);
        }
    }

    @Override
    public BulkUpdateResponse upsert(BulkUpdateRequest bulkUpdateRequest) throws ClientException {
        StringBuilder bulkOperation = new StringBuilder();

        for (UpdateRequest upsertRequest : bulkUpdateRequest.getRequest()) {
            Map<String, Object> storableMap = getModelContext().marshal(upsertRequest.getStorable());

            bulkOperation.append("{ \"update\": {\"_id\": \"")
                    .append(upsertRequest.getId())
                    .append("\", \"_index\": \"")
                    .append(upsertRequest.getIndex())
                    .append("\"}\n");

            bulkOperation.append("{ \"doc\": ");
            bulkOperation.append(writeRequestFromMap(storableMap));
            bulkOperation.append(", \"doc_as_upsert\": true }\n");
        }
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_POST, ElasticsearchResourcePaths.getBulkPath());
        request.setJsonEntity(bulkOperation.toString());
        DeviceStoreClientResponse updateResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), "multi-index", "UPSERT BULK");

        if (isRequestSuccessful(updateResponse)) {
            JsonNode responseNode = readResponseAsJsonNode(updateResponse);

            ArrayNode items = (ArrayNode) responseNode.get(ElasticsearchKeywords.KEY_ITEMS);
            BulkUpdateResponse bulkResponse = new BulkUpdateResponse();
            for (JsonNode item : items) {
                JsonNode jsonNode = item.get(ElasticsearchKeywords.KEY_UPDATE);
                if (jsonNode != null) {
                    JsonNode idNode = jsonNode.get(ElasticsearchKeywords.KEY_DOC_ID);
                    String metricId = null;
                    if (idNode != null) {
                        metricId = idNode.asText();
                    }

                    String indexName = jsonNode.get(ElasticsearchKeywords.KEY_DOC_INDEX).asText();
                    int responseCode = jsonNode.get(ElasticsearchKeywords.KEY_STATUS).asInt();
                    if (!isRequestSuccessful(responseCode)) {
                        JsonNode failureNode = jsonNode.get(ElasticsearchKeywords.KEY_RESULT);
                        String failureMessage = MSG_EMPTY_ERROR;
                        if (failureNode != null) {
                            failureMessage = failureNode.asText();
                        }
                        String reason = jsonNode.at("/error/reason").asText();
                        if (StringUtils.isNotBlank(reason)) {
                            failureMessage = reason;
                        }
                        bulkResponse.add(new UpdateResponse(metricId, indexName, failureMessage));
                        LOG.info("Upsert failed [{}, {}]", indexName, failureMessage);
                        continue;
                    }
                    bulkResponse.add(new UpdateResponse(metricId, indexName));
                    LOG.debug("Upsert on channel metric successfully executed [{}, {}]", indexName, metricId);
                } else {
                    throw new ClientInternalError("Empty JSON response from upsert");
                }
            }
            return bulkResponse;
        } else {
            throw buildExceptionFromUnsuccessfulResponse("Upsert", updateResponse);
        }
    }

    @Override
    public <T> T find(String index, Object query, Class<T> clazz) throws ClientException {
        ResultList<T> result = query(index, query, clazz);

        return result.getResult().isEmpty() ? null : result.getResult().get(0);
    }

    @Override
    public <T> ResultList<T> query(String index, Object query, Class<T> clazz) throws ClientException {
        JsonNode queryJsonNode = getModelConverter().convertQuery(query);
        LOG.debug(QUERY_CONVERTED_QUERY, queryJsonNode);

        String json = writeRequestFromJsonNode(queryJsonNode);

        long totalCount = 0;
        ArrayNode resultsNode = null;
        String totalRelation = null;

        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_GET, ElasticsearchResourcePaths.search(index));
        request.setJsonEntity(json);
        DeviceStoreClientResponse queryResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), index, "QUERY");

        if (isRequestSuccessful(queryResponse)) {
            JsonNode responseNode = readResponseAsJsonNode(queryResponse);
            JsonNode hitsNode = responseNode.path(ElasticsearchKeywords.KEY_HITS);

            totalCount = hitsNode.path(ElasticsearchKeywords.KEY_TOTAL).path(ElasticsearchKeywords.KEY_VALUE).asLong();
            totalRelation = hitsNode.path(ElasticsearchKeywords.KEY_TOTAL).path(ElasticsearchKeywords.KEY_RELATION).asText();
            if (totalCount > Integer.MAX_VALUE) {
                throw new ClientException(ClientErrorCodes.ACTION_ERROR, CLIENT_HITS_MAX_VALUE_EXCEEDED);
            }
            resultsNode = ((ArrayNode) hitsNode.get(ElasticsearchKeywords.KEY_HITS));
        } else if (isRequestCauseOfConcern(queryResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Query", queryResponse);
        }

        ResultList<T> resultList = new ResultList<>(totalCount);
        if (totalRelation != null) {
            resultList.setTotalHitsExceedsCount(!totalRelation.equals("eq"));
        }
        Object queryFetchStyle = getModelConverter().getFetchStyle(query);
        if (resultsNode != null && !resultsNode.isEmpty()) {
            for (JsonNode result : resultsNode) {
                Map<String, Object> object = objectMapper.convertValue(result.get(SchemaKeys.KEY_SOURCE), Map.class);

                String id = result.get(ElasticsearchKeywords.KEY_DOC_ID).asText();
                String docIndex = result.get(ElasticsearchKeywords.KEY_DOC_INDEX).asText();

                object.put(ModelContext.TYPE_DESCRIPTOR_KEY, docIndex);
                object.put(getModelContext().getIdKeyName(), id);
                object.put(QueryConverter.QUERY_FETCH_STYLE_KEY, queryFetchStyle);

                resultList.add(getModelContext().unmarshal(clazz, object));
            }
        }
        return resultList;
    }

    @Override
    public long count(String index, Object query) throws ClientException {
        JsonNode queryJsonNode = getModelConverter().convertCountQuery(query);

        LOG.debug(COUNT_CONVERTED_QUERY, queryJsonNode);

        String json = writeRequestFromJsonNode(queryJsonNode);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_GET, ElasticsearchResourcePaths.count(index));
        request.setJsonEntity(json);
        DeviceStoreClientResponse queryResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), index, "COUNT");

        long totalCount = 0;
        if (isRequestSuccessful(queryResponse)) {
            JsonNode responseNode = readResponseAsJsonNode(queryResponse);

            totalCount = responseNode.path(ElasticsearchKeywords.KEY_COUNT).asLong();
            if (totalCount > Integer.MAX_VALUE) {
                throw new ClientException(ClientErrorCodes.ACTION_ERROR, CLIENT_HITS_MAX_VALUE_EXCEEDED);
            }
        } else if (isRequestCauseOfConcern(queryResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Count", queryResponse);
        }

        return totalCount;
    }

    @Override
    public void delete(String index, String id) throws ClientException {
        LOG.debug("Delete - id: '{}'", id);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_DELETE, ElasticsearchResourcePaths.id(index, id));
        DeviceStoreClientResponse deleteResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), index, ElasticsearchKeywords.ACTION_DELETE);

        if (!isRequestSuccessful(deleteResponse) &&
                !isRequestNotFound(deleteResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Delete", deleteResponse);
        }
    }

    @Override
    public void deleteByQuery(String index, Object query) throws ClientException {
        JsonNode queryJsonNode = getModelConverter().convertDeleteQuery(query);

        LOG.debug(QUERY_CONVERTED_QUERY, queryJsonNode);

        String json = writeRequestFromJsonNode(queryJsonNode);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_POST, ElasticsearchResourcePaths.deleteByQuery(index));
        request.setJsonEntity(json);
        DeviceStoreClientResponse deleteResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), index, "DELETE BY QUERY");

        if (!isRequestSuccessful(deleteResponse) &&
                isRequestCauseOfConcern(deleteResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Delete by query", deleteResponse);
        }
    }

    @Override
    public IndexResponse isIndexExists(IndexRequest indexRequest) throws ClientException {
        LOG.debug("Index exists - index name: '{}'", indexRequest.getIndex());
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_HEAD, ElasticsearchResourcePaths.index(indexRequest.getIndex()));
        DeviceStoreClientResponse isIndexExistsResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), indexRequest.getIndex(), "INDEX EXIST");

        if (isRequestSuccessful(isIndexExistsResponse)) {
            return new IndexResponse(true);
        } else if (isRequestNotFound(isIndexExistsResponse)) {
            return new IndexResponse(false);
        } else {
            throw buildExceptionFromUnsuccessfulResponse("Index exists", isIndexExistsResponse);
        }
    }

    @Override
    public IndexResponse findIndexes(IndexRequest indexRequest) throws ClientException {
        LOG.debug("Find indexes - index prefix: '{}'", indexRequest.getIndex());
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_GET, ElasticsearchResourcePaths.findIndex(indexRequest.getIndex()));
        request.addParameter("pretty", "true");
        DeviceStoreClientResponse findIndexResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), indexRequest.getIndex(), "INDEX EXIST");

        if (isRequestSuccessful(findIndexResponse)) {
            try {
                return new IndexResponse(EntityUtils.toString(findIndexResponse.getEntity()).split("\n"));
            } catch (ParseException | IOException e) {
                throw new ClientInternalError(e, "Cannot convert the indexes list");
            }
        } else if (isRequestNotFound(findIndexResponse)) {
            return new IndexResponse(null);
        } else {
            throw buildExceptionFromUnsuccessfulResponse("Find indexes", findIndexResponse);
        }
    }

    @Override
    public void createIndex(String indexName, ObjectNode indexSettings) throws ClientException {
        LOG.debug("Create index - object: '{}'", indexSettings);

        String json = writeRequestFromJsonNode(indexSettings);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_PUT, ElasticsearchResourcePaths.index(indexName));
        request.setJsonEntity(json);
        DeviceStoreClientResponse createIndexResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), indexName, "CREATE INDEX");

        if (!isRequestSuccessful(createIndexResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Create index", createIndexResponse);
        }
    }

    @Override
    public boolean isMappingExists(String index) throws ClientException {
        LOG.debug("Mapping exists - mapping name: '{}'", index);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_GET, ElasticsearchResourcePaths.mapping(index));
        DeviceStoreClientResponse isMappingExistsResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), index, "MAPPING EXIST");

        if (isRequestSuccessful(isMappingExistsResponse)) {
            return true;
        } else if (isRequestNotFound(isMappingExistsResponse)) {
            return false;
        } else {
            throw buildExceptionFromUnsuccessfulResponse("Mapping exists", isMappingExistsResponse);
        }
    }

    @Override
    public void putMapping(String index, JsonNode mapping) throws ClientException {
        LOG.debug("Create mapping - object: '{}, index: {}", mapping, index);

        String json = writeRequestFromJsonNode(mapping);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_PUT, ElasticsearchResourcePaths.mapping(index));
        request.setJsonEntity(json);
        DeviceStoreClientResponse createMappingResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), index, "PUT MAPPING");

        if (!isRequestSuccessful(createMappingResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Create mapping", createMappingResponse);
        }
    }

    @Override
    public void refreshAllIndexes() throws ClientException {
        LOG.debug("Refresh all indexes");
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_POST, ElasticsearchResourcePaths.refreshAllIndexes());
        DeviceStoreClientResponse refreshIndexResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), ElasticsearchKeywords.INDEX_ALL, "REFRESH INDEX");

        if (!isRequestSuccessful(refreshIndexResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Refresh all indexes", refreshIndexResponse);
        }
    }

    public void refreshIndex(String index) throws ClientException {
        LOG.debug("Refresh index: {}", index);
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_POST, ElasticsearchResourcePaths.refreshIndex(index));
        DeviceStoreClientResponse refreshIndexResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), index, "REFRESH INDEX");

        if (!isRequestSuccessful(refreshIndexResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Refresh indexes", refreshIndexResponse);
        }
    }

    @Override
    public void deleteAllIndexes() throws ClientException {
        LOG.debug("Delete all indexes");
        DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_DELETE, ElasticsearchResourcePaths.index("_all"));
        DeviceStoreClientResponse deleteIndexResponse = restCallTimeoutHandler(() -> getClient().performRequest(request), ElasticsearchKeywords.INDEX_ALL, "DELETE INDEX");

        if (!isRequestSuccessful(deleteIndexResponse)) {
            throw buildExceptionFromUnsuccessfulResponse("Delete all indexes", deleteIndexResponse);
        }
    }

    @Override
    public void deleteIndexes(String... indexes) throws ClientException {
        LOG.debug("Delete indexes");
        for (String index : indexes) {
            LOG.debug("Delete index: {}", index);
            DeviceStoreClientRequest request = getClient().newRequest(ElasticsearchKeywords.ACTION_DELETE, ElasticsearchResourcePaths.index(index));
            DeviceStoreClientResponse deleteIndexResponse = restCallTimeoutHandler(() -> {
                LOG.debug("Deleting index: {}", index);
                return getClient().performRequest(request);
            }, index, "DELETE INDEX");

            // for that call the deleteIndexResponse=null case could be considered as good response since if an index doesn't exist (404) the delete could be considered successful.
            // the deleteIndexResponse is null also if the error is due to a bad index request (400) but this error, except if there is an application bug, shouldn't never happen.
            if (isRequestNotFound(deleteIndexResponse)) {
                LOG.debug("Deleting index: {} - index does not exist", index);
            } else if (!isRequestSuccessful(deleteIndexResponse)) {
                throw buildExceptionFromUnsuccessfulResponse("Delete indexes", deleteIndexResponse);
            }

            LOG.debug("Deleting index: {} - index deleted", index);
        }
    }

    private DeviceStoreClientResponse restCallTimeoutHandler(Callable<DeviceStoreClientResponse> restAction, String index, String operationName) throws ClientException {
        int retryCount = 0;
        try {
            do {
                try {
                    return restAction.call();
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof TimeoutException) {
                        metricsEsClient.getTimeoutRetry().inc();
                        if (retryCount < getClientConfiguration().getRequestConfiguration().getRequestRetryAttemptMax() - 1) {
                            try {
                                Thread.sleep((long) (getClientConfiguration().getRequestConfiguration().getRequestRetryAttemptWait() * (0.5 + RANDOM.nextFloat() / 2)));
                            } catch (InterruptedException e1) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    } else {
                        throw e;
                    }
                }
            } while (++retryCount <= getClientConfiguration().getRequestConfiguration().getRequestRetryAttemptMax());

        } catch (LowLevelSearchResponseException responseException) {
            LOG.warn("Elasticsearch Response with code {} for on index {} while performing {}. Follows stacktrace.", responseException.getResponse().getStatusCode(), index, operationName, responseException);
            return responseException.getResponse();
        } catch (Exception e) {
            throw new ClientInternalError(e, "Error in handling REST timeout handler");
        }
        metricsEsClient.getTimeoutRetryLimitReached().inc();

        throw new ClientCommunicationException();
    }

    /**
     * Checks if the given {@link DeviceStoreClientResponse#getStatusCode()} is a HTTP 2xx code.
     *
     * @param response The {@link DeviceStoreClientResponse} to check.
     * @return {@code true} if {@link DeviceStoreClientResponse#getStatusCode()} is a 2xx HTTP code, {@code false} otherwise.
     * @since 1.0.0
     */
    private boolean isRequestSuccessful(@NotNull DeviceStoreClientResponse response) {
        return isRequestSuccessful(response.getStatusCode());
    }

    /**
     * Checks if the given response code is a HTTP 2xx code.
     *
     * @param responseCode The response code to check.
     * @return {@code true} if response code is a 2xx HTTP code, {@code false} otherwise.
     * @since 1.0.0
     */
    private boolean isRequestSuccessful(int responseCode) {
        return (200 <= responseCode && responseCode <= 299);
    }

    /**
     * Checks if the given {@link DeviceStoreClientResponse#getStatusCode()} is a HTTP 400 code.
     *
     * @param response The {@link DeviceStoreClientResponse} to check.
     * @return {@code true} if {@link DeviceStoreClientResponse#getStatusCode()} is a 400 HTTP code, {@code false} otherwise.
     * @since 1.3.0
     */
    private boolean isRequestBadRequest(@NotNull DeviceStoreClientResponse response) {
        return isRequestBadRequest(response.getStatusCode());
    }

    /**
     * Checks if the given response code is a HTTP 400 code.
     *
     * @param responseCode The response code to check.
     * @return {@code true} if reponse code is a 204 HTTP code, {@code false} otherwise.
     * @since 1.3.0
     */
    private boolean isRequestBadRequest(int responseCode) {
        return (400 == responseCode);
    }


    /**
     * Checks if the given {@link DeviceStoreClientResponse#getStatusCode()} is a HTTP 404 code.
     *
     * @param response The {@link DeviceStoreClientResponse} to check.
     * @return {@code true} if {@link DeviceStoreClientResponse#getStatusCode()} is a 404 HTTP code, {@code false} otherwise.
     * @since 1.3.0
     */
    private boolean isRequestNotFound(@NotNull DeviceStoreClientResponse response) {
        return isRequestNotFound(response.getStatusCode());
    }

    /**
     * Checks if the given response code is a HTTP 404 code.
     *
     * @param responseCode The response code to check.
     * @return {@code true} if response code is a 404 HTTP code, {@code false} otherwise.
     * @since 1.3.0
     */
    private boolean isRequestNotFound(int responseCode) {
        return (404 == responseCode);
    }

    private boolean isRequestNotParsed(@NotNull DeviceStoreClientResponse response) throws ClientException {
        JsonNode responseNode = readResponseAsJsonNode(response);
        return responseNode.path("error").path("type").asText().equals("parsing_exception");
    }

    /**
     * In some cases, a query for a given index doesn't work properly because ES missed to refresh internal indexes, and does not yet "recognize" the requested index
     * In such cases, it returns either a 404 or 400 error code BUT for the latter the exception type, in the response, is not a parsing_exception
     * @param response The response code to check.
     * @return {@code false} iff the above condition holds, case in which we don't want to propagate an exception
     * @since 2.1.0
     */
    private boolean isRequestCauseOfConcern(@NotNull DeviceStoreClientResponse response) throws ClientException {
        if (isRequestBadRequest(response)) {
            return isRequestNotParsed(response);
        } else {
            return !isRequestNotFound(response);
        }
    }

    /**
     * Builds a {@link ClientActionResponseException} from the {@link DeviceStoreClientResponse} trying to get the reason from it.
     *
     * @param action   The action that was performed
     * @param response The {@link DeviceStoreClientResponse} from Elasticsearch
     * @return The {@link ClientActionResponseException} to throw.
     * @since 1.3.0
     */
    private ClientException buildExceptionFromUnsuccessfulResponse(@NotNull String action, @NotNull DeviceStoreClientResponse response) {
        return new ClientActionResponseException(action, response.getReasonPhrase(), String.valueOf(response.getStatusCode()));
    }

    private JsonNode readResponseAsJsonNode(@NotNull DeviceStoreClientResponse response) throws ResponseEntityReadError {
        try {
            return objectMapper.readTree(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            throw new ResponseEntityReadError(e);
        }
    }

    private String writeRequestFromJsonNode(@NotNull JsonNode jsonNode) throws RequestEntityWriteError {
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RequestEntityWriteError(e);
        }
    }

    private String writeRequestFromMap(@NotNull Map<String, Object> storableMap) throws RequestEntityWriteError {
        try {
            return objectMapper.writeValueAsString(storableMap);
        } catch (JsonProcessingException e) {
            throw new RequestEntityWriteError(e);
        }
    }

}
