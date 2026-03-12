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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.elasticsearch.client.rest;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.eclipse.kapua.commons.util.log.ConfigurationPrinter;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.ModelContext;
import org.eclipse.kapua.service.elasticsearch.client.QueryConverter;
import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchClientConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchClientSslConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchNode;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientInitializationException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientProviderInitException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientUnavailableException;
import org.eclipse.kapua.service.elasticsearch.client.utils.InetAddressParser;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

/**
 * {@link ElasticsearchClientProvider} REST implementation.
 * <p>
 * Instantiates, in a Singleton fashion, and manages the {@link RestElasticsearchClientWrapper}.
 *
 * @since 1.0.0
 */
public class RestElasticsearchClientProvider implements ElasticsearchClientProvider<RestElasticsearchClientWrapper> {

    static final Logger LOG = LoggerFactory.getLogger(RestElasticsearchClientProvider.class);

    private static final String PROVIDER_CANNOT_CLOSE_CLIENT_MSG = "Cannot close ElasticSearch REST client. Client is already closed or not initialized";

    private RestElasticsearchClientWrapper restElasticsearchClientWrapper;

    private ElasticsearchClientConfiguration elasticsearchClientConfiguration;
    private ModelContext modelContext;
    private QueryConverter modelConverter;
    private MetricsEsClient metrics;
    private volatile boolean initialized;

    @Inject
    public RestElasticsearchClientProvider(MetricsEsClient metricsEsClient) {
        this.metrics = metricsEsClient;
    }

    @Override
    public RestElasticsearchClientProvider init() throws ClientProviderInitException {
        if (initialized) {
            return this;
        }
        synchronized (RestElasticsearchClientProvider.class) {
            if (initialized) { //this check is needed, in addition to the same above, to avoid multiple initializations with multi-threading
                return this;
            }
            if (elasticsearchClientConfiguration == null) {
                throw new ClientProviderInitException("Client configuration not defined");
            }
            if (modelContext == null) {
                throw new ClientProviderInitException("Model context not defined");
            }
            if (modelConverter == null) {
                throw new ClientProviderInitException("Model converter not defined");
            }

            // Print Configurations
            ConfigurationPrinter configurationPrinter =
                    ConfigurationPrinter
                            .create()
                            .withLogger(LOG)
                            .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                            .withTitle("Elasticsearch REST Provider Configuration")
                            .addParameter("Module Name", getClientConfiguration().getModuleName())
                            .addParameter("Cluster Name", getClientConfiguration().getClusterName());

            try {
                configurationPrinter.openSection("Nodes");

                int nodesIndex = 1;
                for (ElasticsearchNode node : getClientConfiguration().getNodes()) {
                    configurationPrinter
                            .openSection("# " + nodesIndex++)
                            .addParameter("Host", node.getAddress())
                            .addParameter("Port", node.getPort())
                            .closeSection();
                }
            } finally {
                configurationPrinter.closeSection();
            }

            // SSL Configuration
            try {
                configurationPrinter
                        .openSection("SSL Layer")
                        .addParameter("Is Enabled", getClientSslConfiguration().isEnabled());

                if (getClientSslConfiguration().isEnabled()) {
                    configurationPrinter
                            .addParameter("Key Store Path", getClientSslConfiguration().getKeyStorePath())
                            .addParameter("Key Store Type", getClientSslConfiguration().getKeyStoreType())
                            .addParameter("Key Store Password", Strings.isNullOrEmpty(getClientSslConfiguration().getKeyStorePassword()) ? "No" : "Yes")
                            .addParameter("Trust Store Path", getClientSslConfiguration().getTrustStorePath())
                            .addParameter("Trust Store Password", Strings.isNullOrEmpty(getClientSslConfiguration().getTrustStorePassword()) ? "No" : "Yes");
                }

            } finally {
                configurationPrinter.closeSection();
            }

            // Other configurations
            configurationPrinter
                    .addParameter("Model Context", modelContext)
                    .addParameter("Model Converter", modelConverter)
                    .printLog();

            // Init Kapua Elasticsearch Client
            try {
                initClient();
            } catch (Exception e) {
                throw new ClientProviderInitException(e, "Cannot init ElasticsearchClientWrapper");
            }

            // Start a reconnect task - commented because actually not needed now, maybe useful in the future
//            reconnectExecutorTask = Executors.newScheduledThreadPool(1);
//            reconnectExecutorTask.scheduleWithFixedDelay(() -> {
//                try {
//                    reconnectClientTask(this::initClient);
//                } catch (Exception e) {
//                    LOG.info(">>> Initializing Elasticsearch REST client... Connecting... Error: {}", e.getMessage(), e);
//                }
//            }, getClientReconnectConfiguration().getReconnectDelay(), getClientReconnectConfiguration().getReconnectDelay(), TimeUnit.MILLISECONDS);
            initialized = true;
            return this;
        }
    }

    /**
     * Closes the {@link RestElasticsearchClientProvider}.
     * <p>
     * It takes care of closing the {@link RestClient}.
     *
     * @since 1.0.0
     */
    @Override
    public void close() {
        synchronized (RestElasticsearchClientProvider.class) {
            try {
                closeClient();
            } catch (IOException e) {
                LOG.warn(PROVIDER_CANNOT_CLOSE_CLIENT_MSG, e);
            }
        }
    }

    /**
     * Closes the {@link RestClient}.
     * <p>
     *
     * @throws IOException
     *         see {@link RestClient#close()} javadoc.
     * @since 1.0.0
     */
    private void closeClient() throws IOException {
//        if (reconnectExecutorTask != null) {
//            reconnectExecutorTask.shutdown();
//
//            try {
//                reconnectExecutorTask.awaitTermination(getClientReconnectConfiguration().getReconnectDelay(), TimeUnit.MILLISECONDS);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            } finally {
//                reconnectExecutorTask.shutdownNow();
//            }
//
//            reconnectExecutorTask = null;
//        }

        if (restElasticsearchClientWrapper != null) {
            try {
                restElasticsearchClientWrapper.close();
            } finally {
                restElasticsearchClientWrapper = null;
            }
        }
    }

    /**
     * The {@link Callable} that connects (and reconnects) the {@link RestClient}.
     *
     * @param initClientMethod
     *         The {@link Callable} that connects (and reconnects) the {@link RestClient}.
     * @throws Exception
     *         if the given {@link Callable} throws {@link Exception}.
     * @since 1.0.0
     * Commented because actually not needed now, maybe useful in the future. It was referencing an internalElasticsearchRestClient that was never used in the codebase
     */
//    private void reconnectClientTask(Callable<RestClient> initClientMethod) throws Exception {
//        if (internalElasticsearchRestClient == null) {
//            synchronized (RestElasticsearchClientProvider.class) {
//                if (internalElasticsearchRestClient == null) {
//                    metrics.getClientReconnectCall().inc();
//
//                    LOG.info(">>> Initializing Elasticsearch REST client... Connecting...");
//                    internalElasticsearchRestClient = initClientMethod.call();
//                    LOG.info(">>> Initializing Elasticsearch REST client... Connecting... DONE!");
//                }
//            }
//        }
//    }

    /**
     * Initializes the {@link RestClient} as per {@link ElasticsearchClientConfiguration}.
     *
     * @return The initialized {@link RestClient}.
     * @throws ClientInitializationException
     *         if any {@link Exception} occurs while {@link RestClient} initialization.
     * @since 1.0.0
     */
    private RestClient initClient() throws ClientInitializationException {

        ElasticsearchClientConfiguration clientConfiguration = getClientConfiguration();

        if (clientConfiguration.getNodes().isEmpty()) {
            throw new ClientInitializationException("No Elasticsearch nodes are configured");
        }

        boolean sslEnabled = clientConfiguration.getSslConfiguration().isEnabled();
        LOG.info("ES Rest Client - SSL Layer: {}", (sslEnabled ? "Enabled" : "Disabled "));
        List<HttpHost> hosts;
        try {
            hosts = InetAddressParser
                    .parseAddresses(clientConfiguration.getNodes())
                    .stream()
                    .peek(inetSocketAddress -> LOG.info("Evaluating address: {}", inetSocketAddress))
                    .filter(inetSocketAddress -> {
                        if (inetSocketAddress == null) {
                            LOG.warn("Null Inet Socket Address! Skipping...");
                            return false;
                        }
                        return true;
                    }).filter(inetSocketAddress -> {
                        if (inetSocketAddress.getAddress() == null) {
                            LOG.warn("Invalid Inet Socket Address! Skipping...");
                            return false;
                        }
                        return true;
                    }).filter(inetSocketAddress -> {
                        if (inetSocketAddress.getHostName() == null) {
                            LOG.warn("Invalid Inet Socket hostname! Skipping...");
                            return false;
                        }
                        return true;
                    })
                    .map(inetSocketAddress -> {
                                LOG.info("Inet Socket Address: {}", inetSocketAddress);
                                return new HttpHost(
                                        inetSocketAddress.getAddress(),
                                        inetSocketAddress.getHostName(),
                                        inetSocketAddress.getPort(),
                                        (sslEnabled ? "https" : "http"));
                            }
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ClientInitializationException(e, "Error while parsing node addresses!");
        }

        // Init internal Elasticsearch client
        RestClientBuilder restClientBuilder = RestClient.builder(hosts.toArray(new HttpHost[0]));
        SSLContext sslContext = null;
        if (sslEnabled) {
            try {
                SSLContextBuilder sslBuilder = SSLContexts.custom();
                initKeyStore(sslBuilder);
                initTrustStore(sslBuilder);

                sslContext = sslBuilder.build();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new ClientInitializationException(e, "Failed to build SSLContext");
            }
        }

        String username = getClientConfiguration().getUsername();
        String password = getClientConfiguration().getPassword();
        CredentialsProvider credentialsProvider = null;
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }

        //issue #
        final SSLContext finalSslContext = sslContext;
        final CredentialsProvider finalCredentialsProvider = credentialsProvider;
        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> customizeHttpClient(httpClientBuilder, finalSslContext, finalCredentialsProvider));
        //        restClientBuilder.setFailureListener(new RestElasticsearchFailureListener());
        restClientBuilder.setRequestConfigCallback(
                requestConfigBuilder -> {
                    clientConfiguration.getRequestConfiguration().getConnectionTimeoutMillis().ifPresent(timout -> requestConfigBuilder.setConnectTimeout(timout));
                    clientConfiguration.getRequestConfiguration().getSocketTimeoutMillis().ifPresent(timout -> requestConfigBuilder.setSocketTimeout(timout));
                    return requestConfigBuilder;
                });
        org.elasticsearch.client.RestClient esRestClientWrapped = restClientBuilder.build();

        // Init Kapua Elasticsearch Client
        restElasticsearchClientWrapper = new RestElasticsearchClientWrapper(metrics);
        restElasticsearchClientWrapper
                .withClientConfiguration(clientConfiguration)
                .withModelContext(modelContext)
                .withModelConverter(modelConverter)
                .withClient(esRestClientWrapped)
                .init();

        return esRestClientWrapped;
    }

    private HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder, SSLContext sslContext, CredentialsProvider credentialsProvider) {
        try {
            if (credentialsProvider != null) {
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }

            DefaultConnectingIOReactor ioReactor = getDefaultConnectingIOReactor();
            if (sslContext != null) {
                //we need to set SSL context inside the connectionManager because it hides SSL settings that are set directly to the builder
                SSLIOSessionStrategy s = new SSLIOSessionStrategy(sslContext);
                RegistryBuilder<SchemeIOSessionStrategy> rb = RegistryBuilder.create();
                rb.register("https", s).register("http", NoopIOSessionStrategy.INSTANCE);
                httpClientBuilder.setConnectionManager(new PoolingNHttpClientConnectionManager(ioReactor, rb.build()));
            } else {
                httpClientBuilder.setConnectionManager(new PoolingNHttpClientConnectionManager(ioReactor));
            }
        } catch (IOReactorException e) {
            throw new RuntimeException(e);
        }

        return httpClientBuilder;
    }

    @Override
    public ElasticsearchClientProvider<RestElasticsearchClientWrapper> withClientConfiguration(ElasticsearchClientConfiguration elasticsearchClientConfiguration) {
        this.elasticsearchClientConfiguration = elasticsearchClientConfiguration;
        return this;
    }

    @Override
    public ElasticsearchClientProvider<RestElasticsearchClientWrapper> withModelContext(ModelContext modelContext) {
        this.modelContext = modelContext;
        return this;
    }

    @Override
    public ElasticsearchClientProvider<RestElasticsearchClientWrapper> withModelConverter(QueryConverter modelConverter) {
        this.modelConverter = modelConverter;
        return this;
    }

    @Override
    public RestElasticsearchClientWrapper getElasticsearchClient() throws ClientUnavailableException, ClientProviderInitException {
        this.init();
        if (restElasticsearchClientWrapper == null) {
            throw new ClientUnavailableException("Client not initialized");
        }

        return restElasticsearchClientWrapper;
    }
    // Private methods

    /**
     * Gets the {@link DefaultConnectingIOReactor}.
     * This thing is motivated by this https://github.com/eclipse/kapua/pull/3564 (specifically, this issue: https://github.com/elastic/elasticsearch/issues/49124)
     * Maybe in the future, with future ES versions, the problem will be fixed and this won't be needed anymore
     * @return The {@link DefaultConnectingIOReactor}.
     * @since 1.3.0
     */
    private DefaultConnectingIOReactor getDefaultConnectingIOReactor() throws IOReactorException {
        final DefaultConnectingIOReactor ioReactor;
        final Optional<Integer> numberOfIOThreads = getClientConfiguration().getNumberOfIOThreads();
        if (numberOfIOThreads.isPresent()) {
            ioReactor = new DefaultConnectingIOReactor(
                    IOReactorConfig.custom().setIoThreadCount(
                            numberOfIOThreads.get()
                    ).build()
            );
        } else {
            ioReactor = new DefaultConnectingIOReactor();
        }
        ioReactor.setExceptionHandler(new IOReactorExceptionHandler() {

            @Override
            public boolean handle(IOException e) {
                metrics.getException().inc();
                LOG.warn("IOReactor encountered a checked exception: {}", e.getMessage(), e);
                //return true to note this exception as handled, it will not be re-thrown
                return true;
            }

            @Override
            public boolean handle(RuntimeException e) {
                metrics.getRuntimeException().inc();
                LOG.warn("IOReactor encountered a runtime exception: {}", e.getMessage(), e);
                //return true to note this exception as handled, it will not be re-thrown
                return true;
            }
        });
        return ioReactor;
    }

    /**
     * Gets the {@link ElasticsearchClientConfiguration}.
     *
     * @return The {@link ElasticsearchClientConfiguration}.
     * @since 1.3.0
     */
    private ElasticsearchClientConfiguration getClientConfiguration() {
        return elasticsearchClientConfiguration;
    }

    /**
     * Gets the {@link ElasticsearchClientSslConfiguration}.
     * <p>
     * Shortcut for {@link #getClientConfiguration()#getClientSslConfiguration()}
     *
     * @return The {@link ElasticsearchClientSslConfiguration}
     * @since 1.3.0
     */
    private ElasticsearchClientSslConfiguration getClientSslConfiguration() {
        return getClientConfiguration().getSslConfiguration();
    }

    /**
     * Gets the {@link ElasticsearchClientReconnectConfiguration}.
     * <p>
     * Shortcut for {@link #getClientConfiguration()#getClientReconnectConfiguration()}
     *
     * @return The {@link ElasticsearchClientReconnectConfiguration}
     * @since 1.3.0
     */
//    private ElasticsearchClientReconnectConfiguration getClientReconnectConfiguration() {
//        return getClientConfiguration().getReconnectConfiguration();
//    }

    /**
     * Initializes the {@link KeyStore}  as per  {@link ElasticsearchClientSslConfiguration} with the given {@link SSLContextBuilder}.
     *
     * @param sslBuilder
     *         The {@link SSLContextBuilder} to use.
     * @throws ClientInitializationException
     *         if {@link KeyStore} cannot be initialized.
     * @since 1.0.0
     */
    private void initKeyStore(SSLContextBuilder sslBuilder) throws ClientInitializationException {
        ElasticsearchClientSslConfiguration sslConfiguration = getClientSslConfiguration();

        String keystorePath = sslConfiguration.getKeyStorePath();
        String keystorePassword = sslConfiguration.getKeyStorePassword();
        LOG.info("ES Rest Client - Keystore path: {}", StringUtils.isNotBlank(keystorePath) ? keystorePath : "None");

        if (StringUtils.isNotBlank(keystorePath)) {
            try {
                sslBuilder.loadKeyMaterial(loadKeyStore(keystorePath, keystorePassword), null);
            } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException lkme) {
                throw new ClientInitializationException(lkme, "Failed to init KeyStore");
            }
        }
    }

    /**
     * Initializes the {@link TrustStrategy} as per {@link ElasticsearchClientSslConfiguration} with the given {@link SSLContextBuilder}
     * <p>
     * Truststore 2 available configurations:
     * <ol>
     *     <li>Set the custom trust manager: if {@link ElasticsearchClientSslConfiguration#getTrustStorePath()} is defined</li>
     *     <li>Use the JVM default truststore: as fallback option</li>
     * </ol>
     *
     * @param sslBuilder 
     *          The {@link SSLContextBuilder} to use.
     * @throws ClientInitializationException 
     *          if {@link KeyStore} cannot be initialized.
     * @since 1.0.0
     */
    private void initTrustStore(SSLContextBuilder sslBuilder) throws ClientInitializationException {
        ElasticsearchClientSslConfiguration sslConfiguration = getClientSslConfiguration();

        String truststorePath = sslConfiguration.getTrustStorePath();
        String truststorePassword = sslConfiguration.getTrustStorePassword();
        LOG.info("ES Rest Client - Truststore path: {}", StringUtils.isNotBlank(truststorePath) ? truststorePath : "None");

        try {
            if (StringUtils.isNotBlank(truststorePath)) {
                sslBuilder.loadTrustMaterial(loadKeyStore(truststorePath, truststorePassword), null);
            } else {
                sslBuilder.loadTrustMaterial((TrustStrategy) null); //This will load JVM default truststore (with common root certificates in it). Useful for usual production deployment
            }
        } catch (NoSuchAlgorithmException | KeyStoreException ltme) {
            throw new ClientInitializationException(ltme, "Failed to init TrustStore");
        }
    }

    /**
     * Loads the {@link KeyStore}  as per {@link ElasticsearchClientSslConfiguration}.
     *
     * @param keystorePath
     *         The {@link KeyStore} path.
     * @param keystorePassword
     *         The {@link KeyStore} password.
     * @return The initialized {@link KeyStore}.
     * @throws ClientInitializationException
     *         if {@link KeyStore} cannot be loaded.
     * @since 1.0.0
     */
    private KeyStore loadKeyStore(String keystorePath, String keystorePassword) throws ClientInitializationException {
        try (InputStream is = Files.newInputStream(new File(keystorePath).toPath())) {
            KeyStore keystore = KeyStore.getInstance(getClientConfiguration().getSslConfiguration().getKeyStoreType());
            keystore.load(is, keystorePassword.toCharArray());
            return keystore;
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new ClientInitializationException(e, "Failed to load KeyStore");
        }
    }
}
