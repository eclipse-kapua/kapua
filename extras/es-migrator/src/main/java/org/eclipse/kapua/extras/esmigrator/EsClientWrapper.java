/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.extras.esmigrator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsClientWrapper implements AutoCloseable {

    private final RestHighLevelClient client;
    private final int batchSize;
    private final int taskPollingInterval;

    private static final Logger LOGGER = LoggerFactory.getLogger(EsClientWrapper.class.getName());

    private static final String DELETING_INDEX = "Deleting index {}";
    private static final String REINDEXING = "Reindexing {} to {}";

    private static final String INDEX_NUMBER_OF_SHARDS = "index.number_of_shards";
    private static final String INDEX_NUMBER_OF_REPLICAS = "index.number_of_replicas";
    private static final String INDEX_REFRESH_INTERVAL = "index.refresh_interval";

    private final EsClusterDescriptor esClusterDescriptor;

    public EsClientWrapper(EsClusterDescriptor esClusterDescriptor, int esSocketTimeout, int batchSize, int taskPollingInterval, boolean esIgnoreSslCertificate) {
        this.esClusterDescriptor = esClusterDescriptor;
        String scheme = esClusterDescriptor.isEsClusterSsl() ? "https://" : "http://";
        RestClientBuilder restClientBuilder = RestClient
                .builder(esClusterDescriptor.getEsClusterNodes()
                                            .stream()
                                            .map(clusterNodeAddress -> {
                                                String clusterNodeFullAddress = scheme + clusterNodeAddress;
                                                LOGGER.debug("{} added to nodes list", clusterNodeFullAddress);
                                                return HttpHost.create(clusterNodeFullAddress);
                                            })
                                            .toArray(HttpHost[]::new))
                .setHttpClientConfigCallback(builder -> {
                    if (StringUtils.isNotBlank(esClusterDescriptor.getUsername())) {
                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esClusterDescriptor.getUsername(),
                                                                                                          esClusterDescriptor.getPassword()));
                        builder.disableAuthCaching().setDefaultCredentialsProvider(credentialsProvider);
                    }
                    if (esIgnoreSslCertificate) {
                        builder.setSSLHostnameVerifier((hostname, session) -> true);
                    }
                    return builder;
                })
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setSocketTimeout(esSocketTimeout));

        client = new RestHighLevelClient(restClientBuilder);
        this.batchSize = batchSize;
        this.taskPollingInterval = taskPollingInterval;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    public MainResponse info() throws IOException {
        return client.info(RequestOptions.DEFAULT);
    }

    public boolean indicesExists(String... indicesNames) throws IOException, ElasticsearchException {
        return Arrays.stream(indicesNames).anyMatch(index -> {
            try {
                return client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
            } catch (IOException | ElasticsearchException exception) {
                LOGGER.warn("Error while checking index {} existence: {}", index, MigratorUtils.getExceptionMessageOrName(exception));
                return false;
            }
        });
    }

    public JsonNode getIndices(String indexQuery) throws IOException, ElasticsearchException {
        Request indicesRequest = new Request(HttpGet.METHOD_NAME, "/" + indexQuery + "?format=json&include_type_name=true");
        Response indicesResponse = client.getLowLevelClient().performRequest(indicesRequest);
        return MigratorUtils.getObjectMapper().readValue(EntityUtils.toString(indicesResponse.getEntity()), JsonNode.class);
    }

    public void createIndex(String index, Map<String, Object> sourceMap) throws IOException, ElasticsearchException {
        createIndex(index, MigratorUtils.getObjectMapper().writer().writeValueAsString(sourceMap));
    }

    public void createIndex(String index, String mapping) throws IOException, ElasticsearchException {
        int indexesShardNumber = esClusterDescriptor.getIndicesShardNumber();
        int indexesReplicaNumber = esClusterDescriptor.getIndicesReplicaNumber();
        String indexesRefreshInterval = esClusterDescriptor.getIndicesRefreshInterval();
        Settings settings = Settings.builder()
                                    .put(INDEX_NUMBER_OF_SHARDS, indexesShardNumber)
                                    .put(INDEX_NUMBER_OF_REPLICAS, indexesReplicaNumber)
                                    .put(INDEX_REFRESH_INTERVAL, indexesRefreshInterval)
                                    .build();
        CreateIndexRequest firstCreateIndexRequest = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(mapping, XContentType.JSON);
        LOGGER.debug("Creating index {} - shards: '{}' - replicas: '{}' - refresh: '{}'", index, indexesShardNumber, indexesReplicaNumber, indexesRefreshInterval);
        String createIndexRequestBody = firstCreateIndexRequest.mappings().utf8ToString();
        LOGGER.debug("Creating index {}", index);
        LOGGER.trace("Creating index {}, body {}", index, createIndexRequestBody);

        client.indices().create(firstCreateIndexRequest, RequestOptions.DEFAULT);
    }

    public void closeIndex(String index) throws IOException, ElasticsearchException {
        CloseIndexRequest request = new CloseIndexRequest(index);
        client.indices().close(request, RequestOptions.DEFAULT);
    }

    public void deleteIndex(String index) throws IOException, ElasticsearchException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        LOGGER.debug(DELETING_INDEX, index);
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    public boolean isIndexOpen(String indexName) throws IOException, ElasticsearchException {
        Request catIndicesRequest = new Request(HttpGet.METHOD_NAME, "/_cat/indices/" + indexName + "?format=json");
        Response catIndicesResponse = client.getLowLevelClient().performRequest(catIndicesRequest);
        ArrayNode catIndicesArray = MigratorUtils.getObjectMapper().readValue(EntityUtils.toString(catIndicesResponse.getEntity()), ArrayNode.class);
        return catIndicesArray.get(0).get("status").asText().equals("open");
    }

    public long count(String index) throws IOException, ElasticsearchException {
        CountRequest countRequest = new CountRequest(index);
        return client.count(countRequest, RequestOptions.DEFAULT).getCount();
    }

    public ReindexTaskResult reindexWithTask(String sourceIndex, String sourceDocType, String destIndex, String destDocType) {
        LOGGER.debug(REINDEXING, sourceIndex, destIndex);
        String taskId;
        try {
            taskId = submitReindexTask(sourceIndex, sourceDocType, destIndex, destDocType);
        } catch (IOException | ElasticsearchException exception) {
            String message = String.format("Error submitting reindex task from index %s to index %s: %s", sourceIndex, destIndex, MigratorUtils.getExceptionMessageOrName(exception));
            LOGGER.warn(message);
            try {
                deleteIndex(destIndex);
            } catch (IOException | ElasticsearchException innerExcetpion) {
                LOGGER.warn("Error deleting index {}: {}", destIndex, MigratorUtils.getExceptionMessageOrName(exception));
            }
            return new ReindexTaskResult(false, message);
        }

        LOGGER.debug("Reindex task from index {} to index {} submitted, task id: {}", sourceIndex, destIndex, taskId);

        boolean reindexComplete = false;
        while (!reindexComplete) {
            try {
                JsonNode getTaskResponseJsonNode = getTaskInfo(taskId);
                if (getTaskResponseJsonNode.get("completed").asBoolean()) {
                    String message = String.format("Reindex task %s completed, status: %n%s", taskId, getTaskResponseJsonNode.toPrettyString());
                    LOGGER.trace(message);
                    reindexComplete = true;
                    ArrayNode failuresNode = getTaskResponseJsonNode.get("response").withArray("failures");
                    if (failuresNode.size() > 0) {
                        StringBuilder errorMessageStringBuilder = new StringBuilder(String.format("Error during reindex from index %s to index %s. Failures: ", sourceIndex, destIndex));
                        for (JsonNode failureNode : failuresNode) {
                            JsonNode causeNode = failureNode.get("cause");
                            errorMessageStringBuilder.append(causeNode.get("type").asText()).append(": ").append(causeNode.get("reason").asText()).append("; ");
                        }
                        errorMessageStringBuilder.delete(errorMessageStringBuilder.lastIndexOf("; "), errorMessageStringBuilder.length());
                        String errorMessage = errorMessageStringBuilder.toString();
                        LOGGER.warn(errorMessage);
                        deleteIndex(destIndex);
                        return new ReindexTaskResult(false, message);
                    }
                } else {
                    String message = String.format("Reindex task %s in progress, current status: %n%s", taskId, getTaskResponseJsonNode.toPrettyString());
                    LOGGER.trace(message);
                    Thread.sleep(taskPollingInterval);
                }
            } catch (InterruptedException interruptedException) {
                String errorMessage = String.format("Error while waiting for Task %s to complete: %s", taskId, interruptedException.getMessage());
                LOGGER.warn(errorMessage, errorMessage);
                Thread.currentThread().interrupt();
                return new ReindexTaskResult(false, errorMessage);
            } catch (IOException | ElasticsearchException exception) {
                String errorMessage = String.format("Error during reindex task %s management: %s", taskId, MigratorUtils.getExceptionMessageOrName(exception));
                LOGGER.warn(errorMessage, errorMessage);
                return new ReindexTaskResult(false, errorMessage);
            }
        }
        LOGGER.debug("Reindex complete: {} on {}", sourceIndex, destIndex);
        return new ReindexTaskResult(true, null);
    }

    public String submitReindexTask(String sourceIndex, String sourceDocType, String destIndex, String destDocType) throws IOException, ElasticsearchException {
        return submitReindexTask(sourceIndex, sourceDocType, destIndex, destDocType, null);
    }

    public String submitReindexTask(String sourceIndex, String sourceDocType, String destIndex, String destDocType, Script script) throws IOException, ElasticsearchException {
        ReindexRequest reindexRequest = new ReindexRequest()
                .setSourceBatchSize(batchSize)
                .setSourceIndices(sourceIndex)
                .setSourceDocTypes(sourceDocType)
                .setDestIndex(destIndex)
                .setDestDocType(destDocType)
                .setRefresh(true);
        if (script != null) {
            reindexRequest.setScript(script);
        }
        return client.submitReindexTask(reindexRequest, RequestOptions.DEFAULT).getTask();
    }

    public JsonNode getTaskInfo(String taskId) throws IOException, ElasticsearchException {
        Request getTaskRequest = new Request(HttpGet.METHOD_NAME, String.format("/_tasks/%s", taskId));
        Response getTaskResponse = client.getLowLevelClient().performRequest(getTaskRequest);
        return MigratorUtils.getObjectMapper().readValue(EntityUtils.toString(getTaskResponse.getEntity()), JsonNode.class);
    }

    public GetMappingsResponse getMappings(String... index) throws IOException, ElasticsearchException {
        GetMappingsRequest getMappingsRequest = new GetMappingsRequest().indices(index);
        return client.indices().getMapping(getMappingsRequest, RequestOptions.DEFAULT);
    }

}
