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
import org.eclipse.kapua.service.elasticsearch.client.DeviceStoreClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.ModelContext;
import org.eclipse.kapua.service.elasticsearch.client.QueryConverter;
import org.eclipse.kapua.service.elasticsearch.client.configuration.DeviceStoreClientConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchClientSslConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchNode;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientInitializationException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientProviderInitException;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientUnavailableException;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClient;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClientBuilder;
import org.eclipse.kapua.service.elasticsearch.client.utils.InetAddressParser;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * {@link DeviceStoreClientProvider} REST implementation.
 * <p>
 * Instantiates, in a Singleton fashion, and manages the {@link RestDeviceStoreClientWrapper}.
 *
 * @since 1.0.0
 */
public class RestDeviceStoreClientProvider implements DeviceStoreClientProvider<RestDeviceStoreClientWrapper> {

    static final Logger LOG = LoggerFactory.getLogger(RestDeviceStoreClientProvider.class);

    private static final String PROVIDER_CANNOT_CLOSE_CLIENT_MSG = "Cannot close ElasticSearch REST client. Client is already closed or not initialized";

    private List<RestDeviceStoreClientWrapper> restDeviceStoreClientWrappers;

    private DeviceStoreClientConfiguration deviceStoreClientConfiguration;
    private ModelContext modelContext;
    private QueryConverter modelConverter;
    private MetricsEsClient metrics;
    private final DeviceStoreClientBuilder deviceStoreClientBuilder;
    private volatile boolean initialized;
    private volatile boolean closed = true;
    private AtomicInteger nextClientIndex = new AtomicInteger(0);

    @Inject
    public RestDeviceStoreClientProvider(MetricsEsClient metricsEsClient, DeviceStoreClientBuilder deviceStoreClientBuilder) {
        this.metrics = metricsEsClient;
        this.deviceStoreClientBuilder = deviceStoreClientBuilder;
    }

    @Override
    public RestDeviceStoreClientProvider init() throws ClientProviderInitException {
        if (!closed) {
            LOG.warn(deviceStoreClientBuilder.getVendorName() + " Elasticsearch rest client provider: closing the pool failed at a previous stage, trying to close before init.");
            close();
        }
        if (initialized && closed) {
            return this;
        }
        synchronized (RestDeviceStoreClientProvider.class) {
            if (!closed) {
                throw new ClientProviderInitException("Client pool not closed");
            }
            if (initialized) { //this check is needed, in addition to the same above, to avoid multiple initializations with multi-threading
                return this;
            }
            if (deviceStoreClientConfiguration == null) {
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
                            .withTitle(deviceStoreClientBuilder.getVendorName() + " REST Provider Configuration")
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
                int poolSize = deviceStoreClientConfiguration.getPoolSize();
                if (poolSize >= 1) {
                    LOG.info("Elasticsearch rest client provider: configured pool of size {}", poolSize);
                } else {
                    LOG.warn("Elasticsearch rest client provider: configured pool of size {} is invalid, set to default 1", poolSize);
                    poolSize = 1;
                }
                initClientPool(poolSize);
            } catch (Exception e) {
                try {
                    closeClientPool();
                } catch (IOException ioExc) {
                    LOG.warn(PROVIDER_CANNOT_CLOSE_CLIENT_MSG, ioExc);
                }
                throw new ClientProviderInitException(e, "Cannot init DeviceStoreClientWrapper");
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
            return this;
        }
    }

    /**
     * Closes the {@link RestDeviceStoreClientProvider}.
     * <p>
     * It takes care of closing the {@link DeviceStoreClient}.
     *
     * @since 1.0.0
     */
    @Override
    public void close() {
        synchronized (RestDeviceStoreClientProvider.class) {
            try {
                LOG.info("Elasticsearch rest client provider: closing pool");
                closeClientPool();
            } catch (IOException e) {
                LOG.warn(PROVIDER_CANNOT_CLOSE_CLIENT_MSG, e);
            }
        }
    }

    /**
     * Closes the {@link DeviceStoreClient} pool.
     * <p>
     *
     * @throws IOException
     *         see {@link DeviceStoreClient#close()} javadoc.
     * @since 2.0.0
     */
    private void closeClientPool() throws IOException {
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
        initialized = false;
        if (restDeviceStoreClientWrappers == null) {
            closed = true;
            return;
        }
        closed = false;
        for (int i = 0; i < restDeviceStoreClientWrappers.size(); i++) {
            if (restDeviceStoreClientWrappers.get(i) != null) {
                restDeviceStoreClientWrappers.get(i).close();
                restDeviceStoreClientWrappers.set(i, null);
            }
        }
        restDeviceStoreClientWrappers.clear();
        restDeviceStoreClientWrappers = null;
        closed = true;
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
//            synchronized (RestDeviceStoreClientProvider.class) {
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
     * Initializes the {@link DeviceStoreClient} pool as per {@link DeviceStoreClientConfiguration}.
     *
     * @return The initialized {@link DeviceStoreClient} pool.
     * @throws ClientInitializationException
     *         if any {@link Exception} occurs while {@link DeviceStoreClient} initialization.
     * @since 2.0.0
     */
    private void initClientPool(int poolSize) throws ClientInitializationException {
        initialized = false;
        restDeviceStoreClientWrappers = new ArrayList<>(poolSize);
        RestDeviceStoreClientWrapper clientPoolItem;
        for(int i=0; i < poolSize; i++) {
            clientPoolItem = createClientWrapper();
            clientPoolItem.init();
            restDeviceStoreClientWrappers.add(clientPoolItem);
        }
        initialized = true;
    }

    private RestDeviceStoreClientWrapper createClientWrapper() throws ClientInitializationException {

        DeviceStoreClientConfiguration clientConfiguration = getClientConfiguration();

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

        DeviceStoreClientBuilder restClientBuilder = deviceStoreClientBuilder.initializeAndSetHosts(hosts.toArray(new HttpHost[0]));
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

        DeviceStoreClient esRestClientWrapped;
        esRestClientWrapped = restClientBuilder.build();

        // Create Client Wrapper
        RestDeviceStoreClientWrapper wrapper = new RestDeviceStoreClientWrapper(metrics);
        wrapper.withClientConfiguration(clientConfiguration)
        .withModelContext(modelContext)
        .withModelConverter(modelConverter)
        .withClient(esRestClientWrapped);

        return wrapper;
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
    public DeviceStoreClientProvider<RestDeviceStoreClientWrapper> withClientConfiguration(DeviceStoreClientConfiguration deviceStoreClientConfiguration) {
        this.deviceStoreClientConfiguration = deviceStoreClientConfiguration;
        return this;
    }

    @Override
    public DeviceStoreClientProvider<RestDeviceStoreClientWrapper> withModelContext(ModelContext modelContext) {
        this.modelContext = modelContext;
        return this;
    }

    @Override
    public DeviceStoreClientProvider<RestDeviceStoreClientWrapper> withModelConverter(QueryConverter modelConverter) {
        this.modelConverter = modelConverter;
        return this;
    }

    @Override
    public RestDeviceStoreClientWrapper getDeviceStoreClient() throws ClientUnavailableException, ClientProviderInitException {
        this.init();
        // To evenly distribute requests among clients, calculate the index to return in a round robin style.
        int clientIndex = Math.abs(nextClientIndex.getAndAdd(1) % restDeviceStoreClientWrappers.size());
        LOG.debug("Elasticsearch client provider pool get ES client: assign index {} in a pool of {}", clientIndex, restDeviceStoreClientWrappers.size());
        RestDeviceStoreClientWrapper clientWrapper = restDeviceStoreClientWrappers.get(clientIndex);
        return clientWrapper;
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
     * Gets the {@link DeviceStoreClientConfiguration}.
     *
     * @return The {@link DeviceStoreClientConfiguration}.
     * @since 1.3.0
     */
    private DeviceStoreClientConfiguration getClientConfiguration() {
        return deviceStoreClientConfiguration;
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
