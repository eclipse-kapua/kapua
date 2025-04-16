/*******************************************************************************
 * Copyright (c) 2019, 2025 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.qa.integration.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.ListNetworksFilterParam;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.DockerClient.RemoveContainerParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.NetworkNotFoundException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.Network;
import com.spotify.docker.client.messages.NetworkConfig;
import com.spotify.docker.client.messages.NetworkCreation;
import com.spotify.docker.client.messages.PortBinding;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.eclipse.kapua.commons.core.ServiceModuleBundle;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.BasicSteps;
import org.eclipse.kapua.qa.common.DBHelper;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.integration.steps.utils.TestReadinessHttpConnection;
import org.eclipse.kapua.qa.integration.steps.utils.TestReadinessMqttBrokerConnection;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class DockerSteps {

    private static final Logger logger = LoggerFactory.getLogger(DockerSteps.class);

    private static final String NETWORK_PREFIX = "kapua-net";
    private static final String KAPUA_VERSION = "2.1.0-SNAPSHOT";
    private static final String ES_IMAGE = "elasticsearch:7.8.1";
    private static final String BROKER_IMAGE = "kapua-broker-artemis";
    private static final String LIFECYCLE_CONSUMER_IMAGE = "kapua-consumer-lifecycle";
    private static final String TELEMETRY_CONSUMER_IMAGE = "kapua-consumer-telemetry";
    private static final String AUTH_SERVICE_IMAGE = "kapua-service-authentication";
    private static final String API_IMAGE = "kapua-api";
    private static final List<String> DEFAULT_DEPLOYMENT_CONTAINERS_NAME;
    private static final List<String> DEFAULT_BASE_DEPLOYMENT_CONTAINERS_NAME;
    private static final int WAIT_COUNT = 120; //total wait time = 240 secs (120 * 2000ms)
    private static final long WAIT_STEP = 2000;
    private static final long WAIT_FOR_DB = 5000;
    private static final long WAIT_FOR_ES = 5000;
    private static final long WAIT_FOR_EVENTS_BROKER = 10000;
    private static final long WAIT_FOR_REST_API = 30000;
    private static final int HTTP_COMMUNICATION_TIMEOUT = 3000;

    private static final int JOB_ENGINE_PORT_CONTAINER = 8080;
    private static final int JOB_ENGINE_PORT_HOST = 8080;
    private static final String JOB_ENGINE_ADDRESS_EXTERNAL = "http://localhost:" + JOB_ENGINE_PORT_HOST;
    private static final long JOB_ENGINE_READY_CHECK_INTERVAL = 1000;
    private static final long JOB_ENGINE_READY_MAX_WAIT = 60000;

    private static final int MESSAGE_BROKER_PORT_MQTT_CONTAINER = 1883;
    private static final int MESSAGE_BROKER_PORT_MQTT_HOST = 1883;
    private static final int MESSAGE_BROKER_PORT_INTERNAL_CONTAINER = 1893;
    private static final int MESSAGE_BROKER_PORT_INTERNAL_HOST = 1893;
    private static final int MESSAGE_BROKER_PORT_MQTTS_CONTAINER = 8883;
    private static final int MESSAGE_BROKER_PORT_MQTTS_HOST = 8883;
    private static final int MESSAGE_BROKER_PORT_WS_CONTAINER = 8161;
    private static final int MESSAGE_BROKER_PORT_WS_HOST = 8161;
    private static final int MESSAGE_BROKER_PORT_DEBUG_CONTAINER = 5005;
    private static final int MESSAGE_BROKER_PORT_DEBUG_HOST = 5005;
    private static final String MESSAGE_BROKER_ADDRESS_EXTERNAL = "tcp://localhost:" + MESSAGE_BROKER_PORT_MQTT_HOST;
    private static final long MESSAGE_BROKER_READY_CHECK_INTERVAL = 1000;
    private static final long MESSAGE_BROKER_READY_MAX_WAIT = 60000;

    private static final int LIFECYCLE_HEALTH_CHECK_PORT = 8090;
    private static final int TElEMETRY_HEALTH_CHECK_PORT = 8091;
    private static final int AUTH_SERVICE_HEALTH_CHECK_PORT = 8092;

    private static final String LIFECYCLE_CHECK_WEB_APP = "lifecycle";
    private static final String TELEMETRY_CHECK_WEB_APP = "telemetry";
    private static final String AUTH_SERVICE_CHECK_WEB_APP = "authentication";

    private static final String LIFECYCLE_HEALTH_URL = String.format("http://localhost:%d/%s/health", LIFECYCLE_HEALTH_CHECK_PORT, LIFECYCLE_CHECK_WEB_APP);
    private static final String TELEMETRY_HEALTH_URL = String.format("http://localhost:%d/%s/health", TElEMETRY_HEALTH_CHECK_PORT, TELEMETRY_CHECK_WEB_APP);
    private static final String AUTH_SERVICE_HEALTH_URL = String.format("http://localhost:%d/%s/health", AUTH_SERVICE_HEALTH_CHECK_PORT, AUTH_SERVICE_CHECK_WEB_APP);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        DEFAULT_DEPLOYMENT_CONTAINERS_NAME = new ArrayList<>();
        DEFAULT_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.TELEMETRY_CONSUMER_CONTAINER_NAME);
        DEFAULT_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.LIFECYCLE_CONSUMER_CONTAINER_NAME);
        DEFAULT_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.AUTH_SERVICE_CONTAINER_NAME);
        DEFAULT_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.MESSAGE_BROKER_CONTAINER_NAME);
        DEFAULT_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.API_CONTAINER_NAME);

        DEFAULT_BASE_DEPLOYMENT_CONTAINERS_NAME = new ArrayList<>();
        DEFAULT_BASE_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.JOB_ENGINE_CONTAINER_NAME);
        DEFAULT_BASE_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.EVENTS_BROKER_CONTAINER_NAME);
        DEFAULT_BASE_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.ES_CONTAINER_NAME);
        DEFAULT_BASE_DEPLOYMENT_CONTAINERS_NAME.add(BasicSteps.DB_CONTAINER_NAME);
    }

    private boolean printContainerLogOnContainerExit;
    private boolean printImages;
    private NetworkConfig networkConfig;
    private String networkId;
    private boolean debug;
    private List<String> envVar;
    private Map<String, String> containerMap;
    public Map<String, Integer> portMap;

    private DBHelper database;
    private StepData stepData;

    private static final String ALL_IP = "0.0.0.0";


    @Inject
    public DockerSteps(StepData stepData, DBHelper database) {
        this.stepData = stepData;
        this.database = database;
        containerMap = new HashMap<>();
    }

    @Given("Enable debug")
    public void enableDebug() {
        this.debug = true;
    }

    @Given("Disable debug")
    public void disableDebug() {
        this.debug = false;
    }

    @Given("Create mqtt {string} client for broker {string} on port {int} with user {string} and pass {string}")
    public void createMqttClient(String clientId, String broker, int port, String user, String pass) {
        try {
            BrokerClient client = new BrokerClient(broker, port, clientId, user, pass);
            stepData.put(clientId, client);
        } catch (MqttException e) {
            logger.error("Error creating mqtt client with id " + clientId, e);
        }
    }

    @Given("Connect to mqtt client {string}")
    public void connectMqttClient(String clientId) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        try {
            client.connect();
        } catch (MqttException e) {
            logger.error("Unable to connect to mqtt broker with client " + clientId, e);
        }
    }

    @Given("Disconnect mqtt client {string}")
    public void disconnectMqttClient(String clientId) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        try {
            client.disconnect();
        } catch (MqttException e) {
            logger.error("Unable to disconnect from mqtt broker with client " + clientId, e);
        }
    }

    @Given("Subscribe mqtt client {string} to topic {string}")
    public void subscribeMqttClient(String clientId, String topic) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        try {
            client.subscribe(topic, 1);
        } catch (MqttException e) {
            logger.error("Can not subscribe with client " + clientId);
        }
    }

    @Then("Client {string} has {int} messages")
    public void clientCountMsg(String clientId, int numMsgs) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        int receivedMsgs = client.getRecivedMsgCnt();
        Assert.assertEquals(numMsgs, receivedMsgs);
    }

    @Given("Publish string {string} to topic {string} as client {string}")
    public void publishMqttClient(String message, String topic, String clientId) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        try {
            client.publish(topic, 1, message);
        } catch (MqttException e) {
            logger.error("Can not publish to topic " + topic);
        }
    }

    @Given("Start full docker environment")
    public void startFullDockerEnvironment() throws Exception {
        logger.info("Starting full docker environment...");
        stopFullDockerEnvironmentInternal();
        startBaseDockerEnvironmentInternal();
        try {
            startMessageBrokerContainer(BasicSteps.MESSAGE_BROKER_CONTAINER_NAME);
            waitMessageBrokerContainer(BasicSteps.MESSAGE_BROKER_CONTAINER_NAME);

            startAuthServiceContainer(BasicSteps.AUTH_SERVICE_CONTAINER_NAME);
            startLifecycleConsumerContainer(BasicSteps.LIFECYCLE_CONSUMER_CONTAINER_NAME);
            startTelemetryConsumerContainer(BasicSteps.TELEMETRY_CONSUMER_CONTAINER_NAME);
            logger.info("Starting full docker environment... DONE (waiting for containers to be ready)");
            //wait until services are ready
            int loops = 0;
            while (!areServicesReady()) {
                if (loops++ > WAIT_COUNT) {
                    throw new DockerException("Timeout waiting for cluster startup reached!");
                }
                synchronized (this) {
                    this.wait(WAIT_STEP);
                }
                logger.info("Consumers not ready after {}s... wait", (loops * WAIT_STEP / 1000));
            }
            logger.info("Consumers ready");
        } catch (Exception e) {
            logger.error("Error while starting full docker environment: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Starts Docker container requested.
     *
     * TODO: Add missing Telemetry mapping
     * TODO: Move to start multiple resources and check readiness of components in parallel
     *
     * @param dockerContainers The Docker containers to start
     * @throws Exception
     * @since 2.1.0
     */
    @Given("Start Docker environment with resources")
    public void startDockerEnvironmentWithResources(List<String> dockerContainers) throws Exception {
        cleanDockerEnvironmentInternal();

        pullImage(ES_IMAGE);

        createNetwork();

        // Start them
        for (String dockerContainer : dockerContainers) {
            switch (dockerContainer) {
                case "db": {
                    startDBContainer(BasicSteps.DB_CONTAINER_NAME);

                    // This is the only container that we need to wait to start before starting others
                    waitDBContainer(BasicSteps.DB_CONTAINER_NAME);
                } break;
                case "es": {
                    startESContainer(BasicSteps.ES_CONTAINER_NAME);
                    waitEsContainer(BasicSteps.ES_CONTAINER_NAME);
                } break;
                case "events-broker": {
                    startEventBrokerContainer(BasicSteps.EVENTS_BROKER_CONTAINER_NAME);
                    waitEventBrokerContainer(BasicSteps.EVENTS_BROKER_CONTAINER_NAME);
                } break;
                case "job-engine": {
                    startJobEngineContainer(BasicSteps.JOB_ENGINE_CONTAINER_NAME);
                    waitJobEngineContainer(BasicSteps.JOB_ENGINE_CONTAINER_NAME);
                } break;
                case "message-broker": {
                    startMessageBrokerContainer(BasicSteps.MESSAGE_BROKER_CONTAINER_NAME);
                    waitMessageBrokerContainer(BasicSteps.MESSAGE_BROKER_CONTAINER_NAME);
                } break;
                case "broker-auth-service": {
                    startAuthServiceContainer(BasicSteps.AUTH_SERVICE_CONTAINER_NAME);
                } break;
                case "consumer-lifecycle": {
                    startLifecycleConsumerContainer(BasicSteps.LIFECYCLE_CONSUMER_CONTAINER_NAME);
                } break;
                default:
                    throw new UnsupportedOperationException("Unknown container resource: " + dockerContainer);
            }
        }

        // Wait for them to be ready
        for (String dockerContainer : dockerContainers) {
            switch (dockerContainer) {
                case "db":
                case "es":
                case "events-broker":
                case "job-engine":
                case "message-broker": {
                    // Nothing to do.
                    // Waiting to refactor them
                } break;
                case "broker-auth-service": {
                    waitAuthServiceContainer(BasicSteps.AUTH_SERVICE_CONTAINER_NAME);
                } break;
                case "consumer-lifecycle": {
                    waitLifecycleConsumerContainer(BasicSteps.LIFECYCLE_CONSUMER_CONTAINER_NAME);
                } break;
                default:
                    throw new UnsupportedOperationException("Unknown container resource: " + dockerContainer);
            }
        }
    }

    /**
     * Stops all running Docker containers and removes the Docker network
     *
     * @throws Exception
     * @since 2.1.0
     */
    @Given("Stop Docker environment")
    public void cleanDockerEnvironmentInternal() throws Exception {
        removeContainers(
                Arrays.asList(
                        BasicSteps.TELEMETRY_CONSUMER_CONTAINER_NAME,
                        BasicSteps.LIFECYCLE_CONSUMER_CONTAINER_NAME,
                        BasicSteps.AUTH_SERVICE_CONTAINER_NAME,
                        BasicSteps.MESSAGE_BROKER_CONTAINER_NAME,
                        BasicSteps.JOB_ENGINE_CONTAINER_NAME,
                        BasicSteps.EVENTS_BROKER_CONTAINER_NAME,
                        BasicSteps.ES_CONTAINER_NAME,
                        BasicSteps.DB_CONTAINER_NAME
                )
        );

        removeNetwork();
    }

    @Given("Start base docker environment")
    public void startBaseDockerEnvironment() throws Exception {
        stopBaseDockerEnvironment();
        startBaseDockerEnvironmentInternal();
    }

    @Given("Service events are setup")
    public void startEventBus() throws Exception {
        ServiceModuleBundle serviceModuleBundle = KapuaLocator.getInstance().getComponent(ServiceModuleBundle.class);
        serviceModuleBundle.startup();
    }

    @Given("Service events are shutdown")
    public void stopEventBus() throws Exception {
        ServiceModuleBundle serviceModuleBundle = KapuaLocator.getInstance().getComponent(ServiceModuleBundle.class);
        serviceModuleBundle.shutdown();
    }

    private void startBaseDockerEnvironmentInternal() throws Exception {
        logger.info("Starting full docker environment...");
        try {
            pullImage(ES_IMAGE);
            removeNetwork();
            createNetwork();

            startDBContainer(BasicSteps.DB_CONTAINER_NAME);
            waitDBContainer(BasicSteps.DB_CONTAINER_NAME);

            startESContainer(BasicSteps.ES_CONTAINER_NAME);
            waitEsContainer(BasicSteps.ES_CONTAINER_NAME);

            startEventBrokerContainer(BasicSteps.EVENTS_BROKER_CONTAINER_NAME);
            waitEventBrokerContainer(BasicSteps.EVENTS_BROKER_CONTAINER_NAME);

            startJobEngineContainer(BasicSteps.JOB_ENGINE_CONTAINER_NAME);
            waitJobEngineContainer(BasicSteps.JOB_ENGINE_CONTAINER_NAME);

        } catch (Exception e) {
            logger.error("Error while starting base docker environment: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Given("start rest-API container and dependencies with auth token TTL {string}ms and refresh token TTL {string}ms and cors endpoint refresh interval {int}s")
    public void startApiDockerEnvironment(String tokenTTL, String refreshTokenTTL, int corsEndpointRefreshInterval) throws Exception {
        logger.info("Starting rest-api docker environment...");
        stopFullDockerEnvironmentInternal();
        try {
            removeNetwork();
            createNetwork();

            startDBContainer(BasicSteps.DB_CONTAINER_NAME);
            waitDBContainer(BasicSteps.DB_CONTAINER_NAME);

            startEventBrokerContainer(BasicSteps.EVENTS_BROKER_CONTAINER_NAME);
            waitEventBrokerContainer(BasicSteps.EVENTS_BROKER_CONTAINER_NAME);

            startJobEngineContainer(BasicSteps.JOB_ENGINE_CONTAINER_NAME);
            waitJobEngineContainer(BasicSteps.JOB_ENGINE_CONTAINER_NAME);

            startAPIContainer(BasicSteps.API_CONTAINER_NAME, tokenTTL, refreshTokenTTL, corsEndpointRefreshInterval);
            waitRestApiContainer(BasicSteps.JOB_ENGINE_CONTAINER_NAME);

        } catch (Exception e) {
            logger.error("Error while starting base docker environment: {}", e.getMessage(), e);
            throw e;
        }
    }

    private boolean areServicesReady() throws IOException {
        if (isServiceReady(LIFECYCLE_CHECK_WEB_APP) && isServiceReady(TELEMETRY_CHECK_WEB_APP) && isServiceReady(AUTH_SERVICE_CHECK_WEB_APP)) {
            return true;
        }
        return false;
    }

    private boolean isServiceReady(String type) throws IOException {
        URL serviceUrl = new URL(LIFECYCLE_HEALTH_URL);//lifecycle endpoint
        if (TELEMETRY_CHECK_WEB_APP.equals(type)) {
            serviceUrl = new URL(TELEMETRY_HEALTH_URL);//telemetry endpoint
        } else if (AUTH_SERVICE_CHECK_WEB_APP.equals(type)) {
            serviceUrl = new URL(AUTH_SERVICE_HEALTH_URL);//auth service endpoint
        }
        logger.debug("Querying {} consumer status for url: {}", type, serviceUrl);
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) serviceUrl.openConnection();
            conn.setConnectTimeout(HTTP_COMMUNICATION_TIMEOUT);
            conn.setReadTimeout(HTTP_COMMUNICATION_TIMEOUT);
            // Works with spring boot actuator servlet mappings
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

            int status = conn.getResponseCode();
            if (status == 200) {
                try (InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                     BufferedReader in = new BufferedReader(isr)) {
                    return isRunning(MAPPER.readValue(in, Map.class));
                }
            } else {
                logger.info("Querying {} consumer status for url: {} - ERROR", type, serviceUrl);
                return false;
            }
        } catch (IOException e) {
            //nothing to do
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    logger.warn("Cannot close HttpURLConnection", e.getMessage(), e);
                }
            }
        }
        return false;
    }

    private boolean isRunning(Map<String, Object> map) {
        if (map.get("status") != null && "UP".equals(map.get("status"))) {
            return true;
        }
        return false;
    }

    @Given("Stop full docker environment")
    public void stopFullDockerEnvironment() throws DockerException, InterruptedException, SQLException {
        stopFullDockerEnvironmentInternal();
    }

    @Given("Stop base docker environment")
    public void stopBaseDockerEnvironment() throws DockerException, InterruptedException, SQLException {
        stopFullDockerEnvironmentInternal();
    }

    private void stopFullDockerEnvironmentInternal() throws SQLException, DockerException, InterruptedException {
        database.dropAll();
        listAllImages("Stop full docker environment");
        printContainersNames("Print containers logs");
        printContainersLog(DEFAULT_DEPLOYMENT_CONTAINERS_NAME);
        printContainersLog(DEFAULT_BASE_DEPLOYMENT_CONTAINERS_NAME);
        printContainersNames("Remove base containers");
        removeContainers(DEFAULT_BASE_DEPLOYMENT_CONTAINERS_NAME);
        printContainersNames("Remove additional containers");
        removeContainers(DEFAULT_DEPLOYMENT_CONTAINERS_NAME);
        printContainersNames("Remove containers DONE");
        listAllImages("Stop full docker environment");
    }

    @Given("Create network")
    public void createNetwork() throws DockerException, InterruptedException {
        networkConfig = NetworkConfig.builder().name(NETWORK_PREFIX).build();
        NetworkCreation networkCreation = DockerUtil.getDockerClient().createNetwork(networkConfig);
        networkId = networkCreation.id();
    }

    @Given("Remove network")
    public void removeNetwork() throws DockerException, InterruptedException {
        List<Network> networkList = DockerUtil.getDockerClient().listNetworks(ListNetworksFilterParam.byNetworkName(NETWORK_PREFIX));
        if (networkList != null) {
            for (Network network : networkList) {
                String networkId = network.id();
                String networkName = network.name();
                logger.info("Removing network id {} - name {}...", networkId, networkName);
                try {
                    DockerUtil.getDockerClient().removeNetwork(networkId);
                } catch (NetworkNotFoundException e) {
                    //no error!
                    logger.warn("Cannot remove network id {}... network not found!", networkId);
                }
                logger.info("Removing network id {} - name {}... DONE", networkId, networkName);
            }
        }
    }

    @Given("Pull image {string}")
    public void pullImage(String image) throws DockerException, InterruptedException {
        DockerUtil.getDockerClient().pull(image);
    }

    @Given("List images by name {string}")
    public void listImages(String imageName) throws DockerException, InterruptedException {
        List<Image> images = DockerUtil.getDockerClient().listImages(DockerClient.ListImagesParam.byName(imageName));
        if ((images != null) && (images.size() > 0)) {
            for (Image image : images) {
                logger.info("Image: " + image);
            }
        } else {
            logger.info("No docker images found.");
        }
    }

    private void listAllImages(String description) throws DockerException, InterruptedException {
        if (printImages) {
            logger.info("Print images - {}", description);
            List<Image> images = DockerUtil.getDockerClient().listImages(DockerClient.ListImagesParam.allImages());
            int count = 0;
            if ((images != null) && (images.size() > 0)) {
                count = images.size();
                logger.info("ids:");
                for (Image image : images) {
                    if (filterImageToPrint(image)) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(image.id());
                        image.repoTags().forEach(value -> builder.append("\t").append(value));
                        logger.info("{}", builder.toString());
                    }
                }
            }
            logger.info("Print images ({}) DONE - {}", count, description);
        }
    }

    private boolean filterImageToPrint(Image image) {
        for (String tag : image.repoTags()) {
            String tagToLowerCase = tag.toLowerCase();
            if (tagToLowerCase.contains("kapua") || tagToLowerCase.contains("elasticsearch") || tagToLowerCase.contains("activemq") || tagToLowerCase.contains("artemis")) {
                return true;
            }
        }
        return false;
    }

    public void printContainersNames(String description) {
        logger.info("Print containers - {}", description);
        int count = 0;
        try {
            List<Container> containerList = DockerUtil.getDockerClient().listContainers(ListContainersParam.allContainers());
            count = containerList.size();
            containerList.forEach(container -> {
                container.names().forEach((containerName) -> logger.info("\t\t{}", containerName));
            });
        } catch (DockerException | InterruptedException e) {
            logger.warn("Cannot print container name for step '{}'", description, e);
        }
        logger.info("Print containers ({}) DONE - {}", count, description);
    }

    @And("Start DB container with name {string}")
    public void startDBContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting DB container...");
        ContainerConfig dbConfig = getDbContainerConfig();
        ContainerCreation dbContainerCreation = DockerUtil.getDockerClient().createContainer(dbConfig, name);
        String containerId = dbContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put("db", containerId);
        logger.info("DB container started: {}", containerId);
    }

    /**
     * Waits for the DB Docker container to be ready
     * @param name The DB Docker container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitDBContainer(String name) throws Exception{
        synchronized (this) {
            this.wait(WAIT_FOR_DB);
        }
    }

    @And("Start ES container with name {string}")
    public void startESContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting ES container...");
        ContainerConfig esConfig = getEsContainerConfig();
        ContainerCreation esContainerCreation = DockerUtil.getDockerClient().createContainer(esConfig, name);
        String containerId = esContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put("es", containerId);
        logger.info("ES container started: {}", containerId);
    }

    /**
     * Waits for the Elasticsearch Docker container to be ready
     *
     * @param name The Elasticsearch Docker container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitEsContainer(String name) throws Exception{
        synchronized (this) {
            this.wait(WAIT_FOR_ES);
        }
    }

    @And("Start Event Broker container with name {string}")
    public void startEventBrokerContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting EventBroker container...");
        ContainerConfig ebConfig = getEventBrokerContainerConfig();
        ContainerCreation ebContainerCreation = DockerUtil.getDockerClient().createContainer(ebConfig, name);
        String containerId = ebContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put(name, containerId);
        logger.info("EventBroker container started: {}", containerId);
    }

    /**
     * Waits for the Event Broker Docker container to be ready
     *
     * @param name The Event Broker Docker container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitEventBrokerContainer(String name) throws Exception{
        synchronized (this) {
            this.wait(WAIT_FOR_EVENTS_BROKER);
        }
    }

    @And("Start Job Engine container with name {string}")
    public void startJobEngineContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting Job Engine container {}...", name);
        ContainerConfig mbConfig = getJobEngineContainerConfig();
        ContainerCreation mbContainerCreation = DockerUtil.getDockerClient().createContainer(mbConfig, name);
        String containerId = mbContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put(name, containerId);
        logger.info("Job Engine {} container started: {}", name, containerId);
    }

    /**
     * Waits for the Job Engine Docker container to be ready
     *
     * @param name The Job Engine Docker container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitJobEngineContainer(String name) throws Exception{
        long now = System.currentTimeMillis();
        while (now + JOB_ENGINE_READY_MAX_WAIT > System.currentTimeMillis()) {
            if (isJobEngineContainerReady(name)) {
                logger.info("Job Engine ready in: ~{}ms", System.currentTimeMillis() - now);
                return;
            }

            logger.info("Job Engine not ready yet... Retrying in {}ms", JOB_ENGINE_READY_CHECK_INTERVAL);
            TimeUnit.MILLISECONDS.sleep(JOB_ENGINE_READY_CHECK_INTERVAL);
        }

        Assert.fail("Job Engine not ready within: " + JOB_ENGINE_READY_MAX_WAIT + "ms");
    }

    /**
     * Checks if the Job Engine Docker container is ready
     *
     * @param name The Job Engine Docker container name
     * @return {@code true} if is ready, {@code false} otherwise
     * @throws Exception
     * @since 2.1.0
     */
    private boolean isJobEngineContainerReady(String name) throws Exception {
        try (TestReadinessHttpConnection testReadinessHttpConnection = new TestReadinessHttpConnection(JOB_ENGINE_ADDRESS_EXTERNAL)){
            return testReadinessHttpConnection.isReady();
        }
        catch (Exception e) {
            // Ignoring...
        }

        return false;
    }

    @And("Start API container with name {string}")
    public void startAPIContainer(String name, String tokenTTL, String refreshTokenTTL, int corsEndpointRefreshInterval) throws DockerException, InterruptedException {
        logger.info("Starting API container...");
        ContainerConfig dbConfig = getApiContainerConfig(tokenTTL, refreshTokenTTL, corsEndpointRefreshInterval);
        ContainerCreation dbContainerCreation = DockerUtil.getDockerClient().createContainer(dbConfig, name);
        String containerId = dbContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put("api", containerId);
        logger.info("API container started: {}", containerId);
    }

    /**
     * Waits for the REST API Docker container to be ready
     *
     * @param name The REST API Docker container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitRestApiContainer(String name) throws Exception{
        synchronized (this) {
            this.wait(WAIT_FOR_REST_API);
        }
    }

    @And("Start Message Broker container with name {string}")
    public void startMessageBrokerContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting Message Broker container {}...", name);
        ContainerConfig mbConfig = getBrokerContainerConfig(
                name,
                MESSAGE_BROKER_PORT_MQTT_CONTAINER,
                MESSAGE_BROKER_PORT_MQTT_HOST,
                MESSAGE_BROKER_PORT_INTERNAL_CONTAINER,
                MESSAGE_BROKER_PORT_INTERNAL_HOST,
                MESSAGE_BROKER_PORT_MQTTS_CONTAINER,
                MESSAGE_BROKER_PORT_MQTTS_HOST,
                MESSAGE_BROKER_PORT_WS_CONTAINER,
                MESSAGE_BROKER_PORT_WS_HOST,
                MESSAGE_BROKER_PORT_DEBUG_CONTAINER,
                MESSAGE_BROKER_PORT_DEBUG_HOST,
                "kapua/" + BROKER_IMAGE + ":" + KAPUA_VERSION);
        ContainerCreation mbContainerCreation = DockerUtil.getDockerClient().createContainer(mbConfig, name);
        String containerId = mbContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put(name, containerId);
        logger.info("Message Broker {} container started: {}", name, containerId);
    }

    /**
     * Waits for the Message Broker Docker container to be ready
     *
     * @param name The Message Broker Docker container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitMessageBrokerContainer(String name) throws Exception{
        long now = System.currentTimeMillis();
        while (now + MESSAGE_BROKER_READY_MAX_WAIT > System.currentTimeMillis()) {
            if (isMessageBrokerContainerReady(name)) {
                logger.info("Message Broker ready in: ~{}ms", System.currentTimeMillis() - now);
                return;
            }

            logger.info("Message Broker not ready yet... Retrying in {}ms", MESSAGE_BROKER_READY_CHECK_INTERVAL);
            TimeUnit.MILLISECONDS.sleep(MESSAGE_BROKER_READY_CHECK_INTERVAL);
        }

        Assert.fail("Message Broker not ready within: " + MESSAGE_BROKER_READY_MAX_WAIT + "ms");
    }

    /**
     * Checks if the Message Broker Docker container is ready
     *
     * @param name The Message Broker Docker container name
     * @return {@code true} if is ready, {@code false} otherwise
     * @since 2.1.0
     */
    private boolean isMessageBrokerContainerReady(String name) {
        try (TestReadinessMqttBrokerConnection testReadinessConnection = new TestReadinessMqttBrokerConnection(MESSAGE_BROKER_ADDRESS_EXTERNAL)){
            return testReadinessConnection.isReady();
        }
        catch (Exception e) {
            // Ignoring...
        }

        return false;
    }

    @And("Start Auth service container with name {string}")
    public void startAuthServiceContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting Auth service container {}...", name);
        ContainerCreation mbContainerCreation = DockerUtil.getDockerClient().createContainer(getAuthServiceConfig(8080, 8092, 8001, 8003), name);
        String containerId = mbContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put(name, containerId);
        logger.info("Lifecycle Consumer {} container started: {}", name, containerId);
    }

    /**
     * Waits for the Auth Service Docker container to be ready
     *
     * @param name The Auth Service container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitAuthServiceContainer(String name) throws Exception{
        long timeout = System.currentTimeMillis();
        while (System.currentTimeMillis() - timeout < 30000) {
            if (isServiceReady(AUTH_SERVICE_CHECK_WEB_APP)) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    @And("Start LifecycleConsumer container with name {string}")
    public void startLifecycleConsumerContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting Lifecycle Consumer container {}...", name);
        ContainerCreation mbContainerCreation = DockerUtil.getDockerClient().createContainer(getLifecycleConsumerConfig(8080, 8090, 8001, 8001), name);
        String containerId = mbContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put(name, containerId);
        logger.info("Lifecycle Consumer {} container started: {}", name, containerId);
    }

    /**
     * Waits for the Lifecycle Consumer Docker container to be ready
     *
     * @param name The Lifecycle Consumer container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitLifecycleConsumerContainer(String name) throws Exception{
        long timeout = System.currentTimeMillis();
        while (System.currentTimeMillis() - timeout < 30000) {
            if (isServiceReady(LIFECYCLE_CHECK_WEB_APP)){
                break;
            }
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    @And("Start TelemetryConsumer container with name {string}")
    public void startTelemetryConsumerContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting Telemetry Consumer container {}...", name);
        ContainerCreation mbContainerCreation = DockerUtil.getDockerClient().createContainer(getTelemetryConsumerConfig(8080, 8091, 8001, 8002), name);
        String containerId = mbContainerCreation.id();

        DockerUtil.getDockerClient().startContainer(containerId);
        DockerUtil.getDockerClient().connectToNetwork(containerId, networkId);
        containerMap.put(name, containerId);
        logger.info("Telemetry Consumer {} container started: {}", name, containerId);
    }

    /**
     * Waits for the Telemetry Consumer Docker container to be ready
     *
     * @param name The Telemetry Consumer container name
     * @throws Exception
     * @since 2.1.0
     */
    private void waitTelemetryConsumerContainer(String name) throws Exception{
        long timeout = System.currentTimeMillis();
        while (System.currentTimeMillis() - timeout < 30000) {
            if (isServiceReady(TELEMETRY_CHECK_WEB_APP)) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    @Then("Stop container with name {string}")
    public void stopContainer(List<String> names) throws DockerException, InterruptedException {
        for (String name : names) {
            logger.info("Stopping container {}...", name);
            String containerId = containerMap.get(name);
            DockerUtil.getDockerClient().stopContainer(containerId, 3);
            logger.info("Container {} stopped.", name);
        }
    }

    @Then("Remove container with name {string}")
    public void removeContainers(List<String> names) {
        for (String name : names) {
            removeContainer(name);
            //search for images with / at the beginning
            removeContainer("/" + name);
        }
    }

    private void removeContainer(String name) {
        logger.info("Removing container {}...", name);
        List<Container> containers = null;
        try {
            containers = DockerUtil.getDockerClient().listContainers(ListContainersParam.filter("name", name));
            if (containers == null || containers.isEmpty()) {
                logger.info("Cannot remove container '{}'. (Container not found!)", name);
            } else {
                containers.forEach(container -> {
                    container.names().forEach((containerName) -> {
                        if (containerName.equals(name)) {
                            try {
                                DockerUtil.getDockerClient().removeContainer(container.id(), new RemoveContainerParam("force", "true"));
                                containerMap.remove(name);
                                logger.info("Container {} removed. (Container id: {})", name, container.id());
                                return;
                            } catch (DockerException | InterruptedException e) {
                                //test fails since the environment is not cleaned up
                                Assert.fail("Cannot remove container!");
                            }
                        }
                    });
                });
            }
        } catch (DockerException | InterruptedException e) {
            logger.warn("Cannot remove container for name '{}' (cannot filter running container list)", name, e);
        }
    }

    @Given("Print log for container with name {string}")
    public void printContainersLog(List<String> names) {
        if (printContainerLogOnContainerExit) {
            for (String name : names) {
                if (isPrintContainer(name)) {
                    printContainerLog(name);
                }
            }
        } else {
            logger.info("Print containers log in exit disabled.");
        }
    }

    private boolean isPrintContainer(String name) {
        return BasicSteps.JOB_ENGINE_CONTAINER_NAME.equals(name) ||
                BasicSteps.MESSAGE_BROKER_CONTAINER_NAME.equals(name);
    }

    private void printContainerLog(String name) {
        List<Container> containers = null;
        try {
            containers = DockerUtil.getDockerClient().listContainers(ListContainersParam.filter("name", name));
            if (containers == null || containers.isEmpty()) {
                logger.info("Cannot print container '{}' log. (Container not found!)", name);
            } else {
                containers.forEach(container -> {
                    try {
                        LogStream logStream = DockerUtil.getDockerClient().logs(container.id(), LogsParam.stdout(), LogsParam.stderr());
                        Logger brokerLogger = LoggerFactory.getLogger(name);
                        brokerLogger.info("\n===================================================\n START LOG FOR CONTAINER: {} (id: {})\n===================================================", name, container.id());
                        StringBuilder builder = new StringBuilder();
                        int i = 0;
                        while (logStream.hasNext()) {
                            builder.append(StandardCharsets.UTF_8.decode(logStream.next().content()).toString());
                            if (++i % 100 == 0) {
                                brokerLogger.info(builder.toString());
                                builder = new StringBuilder();
                            }
                        }
                        brokerLogger.info(builder.toString());
                        brokerLogger.info("\n---------------------------------------------------\n END LOG FOR CONTAINER: {} (id: {})\n---------------------------------------------------", name, container.id());
                    } catch (Exception e1) {
                        logger.warn("Cannot print container log for name/id '{}'/'{}'", name, container.id());
                    }
                });
            }
        } catch (DockerException | InterruptedException e) {
            logger.warn("Cannot print container log for name '{}' (cannot filter running container list)", name, e);
        }
    }

    /**
     * Creation of docker container configuration for broker.
     *
     * @param brokerIp
     * @param mqttPort      mqtt port on docker
     * @param mqttHostPort  mqtt port on docker host
     * @param mqttsPort     mqtts port on docker
     * @param mqttsHostPort mqtts port on docker host
     * @param webPort       web port on docker
     * @param webHostPort   web port on docker host
     * @param debugPort     debug port on docker
     * @param debugHostPort debug port on docker host
     *                      //     * @param brokerInternalDebugPort
     * @param dockerImage   full name of image (e.g. "kapua/kapua-broker:" + version)
     * @return Container configuration for specific boroker instance
     */
    private ContainerConfig getBrokerContainerConfig(String brokerIp,
                                                     int mqttPort,
                                                     int mqttHostPort,
                                                     int mqttInternalPort,
                                                     int mqttInternalHostPort,
                                                     int mqttsPort,
                                                     int mqttsHostPort,
                                                     int webPort,
                                                     int webHostPort,
                                                     int debugPort,
                                                     int debugHostPort,
                                                     String dockerImage) {

        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort(ALL_IP, portBindings, mqttPort, mqttHostPort);
        addHostPort(ALL_IP, portBindings, mqttInternalPort, mqttInternalHostPort);
        addHostPort(ALL_IP, portBindings, mqttsPort, mqttsHostPort);
        addHostPort(ALL_IP, portBindings, webPort, webHostPort);
        addHostPort(ALL_IP, portBindings, debugPort, debugHostPort);

        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        List<String> envVars = Lists.newArrayList("commons.db.schema.update=true",
                "commons.db.connection.host=db",
                "commons.db.connection.port=3306",
                "datastore.elasticsearch.nodes=es:9200",
                "commons.eventbus.url=amqp://events-broker:5672?jms.sendTimeout=1000",
                "certificate.jwt.private.key=file:///var/opt/activemq/key.pk8",
                "certificate.jwt.certificate=file:///var/opt/activemq/cert.pem",
                "CRYPTO_SECRET_KEY=kapuaTestsKey!!!",
                String.format("broker.host=%s", brokerIp));
        if (envVar != null) {
            envVars.addAll(envVar);
        }

        if (debug) {
//            envVars.add(String.format("ACTIVEMQ_DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=%s", debugPort));
            envVars.add(String.format("DEBUG_ARGS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:%s", debugPort));
        }

        String[] ports = {
                String.valueOf(mqttPort),
                String.valueOf(mqttInternalPort),
                String.valueOf(mqttsPort),
                String.valueOf(webPort),
                String.valueOf(debugPort)
        };

        return ContainerConfig.builder()
                .hostConfig(hostConfig)
                .exposedPorts(ports)
                .env(envVars)
                .image(dockerImage)
                .build();
    }

    /**
     * Creation of docker container configuration for H2 database.
     *
     * @return Container configuration for database instance.
     */
    private ContainerConfig getDbContainerConfig() {
        final int dbPort = 3306;
        final int dbPortConsole = 8181;
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort(ALL_IP, portBindings, dbPort, dbPort);
        addHostPort(ALL_IP, portBindings, dbPortConsole, dbPortConsole);
        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        String[] ports = {
                String.valueOf(dbPort),
                String.valueOf(dbPortConsole)
        };

        return ContainerConfig.builder()
                .hostConfig(hostConfig)
                .exposedPorts(ports)
                .env(
                        "DATABASE=kapuadb",
                        "DB_USER=kapua",
                        "DB_PASSWORD=kapua",
                        //uncomment this line to enable the H@ web console (WARNING enable it only for test and then disable it again!)
//                        "H2_WEB_OPTS=-web -webAllowOthers -webPort 8181",
                        "DB_PORT_3306_TCP_PORT=3306"
                )
                .image("kapua/kapua-sql:" + KAPUA_VERSION)
                .build();
    }

    private ContainerConfig getApiContainerConfig(String tokenTTL, String refreshTokenTTL, int corsEndpointRefreshInterval) {
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort(ALL_IP, portBindings, 8080, 8081);
        addHostPort(ALL_IP, portBindings, 8443, 8443);
        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        String[] ports = {
                String.valueOf(8080),
                String.valueOf(8443)
        };

        return ContainerConfig.builder()
                .hostConfig(hostConfig)
                .exposedPorts(ports)
                .env(
                        "CRYPTO_SECRET_KEY=kapuaTestsKey!!!",
                        "KAPUA_DISABLE_DATASTORE=false",
                        //now I set very little TTL access token to help me in the test scenarios
                        "AUTH_TOKEN_TTL=" + tokenTTL,
                        "REFRESH_AUTH_TOKEN_TTL=" + refreshTokenTTL,
                        "CORS_ENDPOINTS_REFRESH_INTERVAL=" + corsEndpointRefreshInterval,
                        "SWAGGER=true"
                )
                .image("kapua/" + API_IMAGE + ":" + KAPUA_VERSION)
                .build();
    }

    /**
     * Creation of docker container configuration for telemetry consumer.
     *
     * @return Container configuration for telemetry consumer.
     */
    private ContainerConfig getTelemetryConsumerConfig(int healthPort, int healthHostPort, int debugPort, int debugHostPort) {
        return getContainerConfig(TELEMETRY_CONSUMER_IMAGE, healthPort, healthHostPort, debugPort, debugHostPort);
    }

    /**
     * Creation of docker container configuration for lifecycle consumer.
     *
     * @return Container configuration for lifecycle consumer.
     */
    private ContainerConfig getLifecycleConsumerConfig(int healthPort, int healthHostPort, int debugPort, int debugHostPort) {
        return getContainerConfig(LIFECYCLE_CONSUMER_IMAGE, healthPort, healthHostPort, debugPort, debugHostPort);
    }

    /**
     * Creation of docker container configuration for auth service.
     *
     * @return Container configuration for auth service.
     */
    private ContainerConfig getAuthServiceConfig(int healthPort, int healthHostPort, int debugPort, int debugHostPort) {
        return getContainerConfig(AUTH_SERVICE_IMAGE, healthPort, healthHostPort, debugPort, debugHostPort);
    }

    /**
     * Creation of docker container configuration for external services/consumers.
     *
     * @return Container configuration external consumer with provided image name.
     */
    private ContainerConfig getContainerConfig(String imageName, int healthPort, int healthHostPort, int debugPort, int debugHostPort) {
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort("0.0.0.0", portBindings, healthPort, healthHostPort);
        addHostPort("0.0.0.0", portBindings, debugPort, debugHostPort);
        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        String[] ports = {
                String.valueOf(healthPort),
                String.valueOf(debugPort)
        };

        List<String> envVars = new ArrayList<>();
        envVars.add("commons.db.schema.update=true");
        envVars.add("BROKER_HOST=message-broker");
        envVars.add("CRYPTO_SECRET_KEY=kapuaTestsKey!!!");
        if (debug) {
            envVars.add(String.format("DEBUG_OPTS=-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:%s", debugPort));
        }

        return ContainerConfig.builder()
                .hostConfig(hostConfig)
                .exposedPorts(ports)
                .env(envVars)
                .image("kapua/" + imageName + ":" + KAPUA_VERSION)
                .build();
    }

    /**
     * Creation of docker container configuration for Elasticsearch.
     *
     * @return Container configuration for Elasticsearch instance.
     */
    private ContainerConfig getEsContainerConfig() {
        final int esPortRest = 9200;
        final int esPortNodes = 9300;
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort(ALL_IP, portBindings, esPortRest, esPortRest);
        addHostPort(ALL_IP, portBindings, esPortNodes, esPortNodes);
        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        return ContainerConfig.builder()
                .hostConfig(hostConfig)
                .exposedPorts(String.valueOf(esPortRest), String.valueOf(esPortNodes))
                .env(
                        "cluster.name=kapua-datastore",
                        "discovery.type=single-node",
                        "transport.host=0.0.0.0 ",
                        "transport.ping_schedule=-1 ",
                        "transport.tcp.connect_timeout=30s"
                )
                .image(ES_IMAGE)
                .build();
    }

    /**
     * Creation of docker container configuration for event broker.
     *
     * @return Container configuration for event broker instance.
     */
    private ContainerConfig getEventBrokerContainerConfig() {
        final int brokerPort = 5672;
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort(ALL_IP, portBindings, brokerPort, brokerPort);
        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        return ContainerConfig.builder()
                .hostConfig(hostConfig)
                .exposedPorts(String.valueOf(brokerPort))
                .image("kapua/kapua-events-broker:" + KAPUA_VERSION)
                .build();
    }

    /**
     * Creation of docker container configuration for job engine.
     *
     * @return Container configuration for job engine instance.
     */
    private ContainerConfig getJobEngineContainerConfig() {
        Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort(ALL_IP, portBindings, JOB_ENGINE_PORT_CONTAINER, JOB_ENGINE_PORT_HOST);
        HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        return ContainerConfig.builder()
                .hostConfig(hostConfig)
                .exposedPorts(String.valueOf(JOB_ENGINE_PORT_CONTAINER))
                .env(
                        "CRYPTO_SECRET_KEY=kapuaTestsKey!!!"
                )
                .image("kapua/kapua-job-engine:" + KAPUA_VERSION)
                .build();
    }

    /**
     * Add Docker port to host port mappings.
     *
     * @param host         IP address of host
     * @param portBindings {@link List} ob bindings that gets updated
     * @param port         Docker container port
     * @param hostPort     Port exposed on host
     *
     * @since 2.0.0
     */
    private void addHostPort(String host,
                             Map<String, List<PortBinding>> portBindings,
                             int port,
                             int hostPort) {
        List<PortBinding> hostPorts = new ArrayList<>();
        hostPorts.add(PortBinding.of(host, hostPort));
        portBindings.put(String.valueOf(port), hostPorts);
    }

}
