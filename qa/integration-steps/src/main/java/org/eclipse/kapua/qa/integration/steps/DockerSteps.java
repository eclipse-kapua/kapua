/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.NetworkConfig;
import com.spotify.docker.client.messages.NetworkCreation;
import com.spotify.docker.client.messages.PortBinding;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.runtime.java.guice.ScenarioScoped;
import org.apache.activemq.command.BrokerInfo;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ScenarioScoped
public class DockerSteps {

    private static final Logger logger = LoggerFactory.getLogger(DockerSteps.class);

    private static final String NETWORK_PREFIX = "kapua-net";

    private DockerClient docker;

    private NetworkConfig networkConfig;

    private String networkId;

    private boolean debug;

    private List<String> envVar;

    private Map<String, String> containerMap;

    public Map<String, Integer> portMap;

    public Map<String, BrokerInfo> brokerMap;

    private ContainerConfig dbContainerConfig;

    private StepData stepData;

    private static final String ALL_IP = "0.0.0.0";

    @Inject
    public DockerSteps(StepData stepData) {

        this.stepData = stepData;
        containerMap = new HashMap<>();
    }

    @Given("^Enable debug$")
    public void enableDebug() {
        this.debug = true;
    }

    @Given("^Disable debug$")
    public void disableDebug() {
        this.debug = false;
    }

    @Before
    public void setupDockerClient() {
        logger.info("Creating docker client.");
        try {
            docker = DefaultDockerClient.fromEnv().build();
        } catch (DockerCertificateException e) {
            logger.error("Could not connect to docker.");
            throw new RuntimeException("Cannot initialize docker client!", e);
        }
    }

    @Given("^Create mqtt \"(.*)\" client for broker \"(.*)\" on port (\\d+) with user \"(.*)\" and pass \"(.*)\"$")
    public void createMqttClient(String clientId, String broker, int port, String user, String pass) {
        try {
            BrokerClient client = new BrokerClient(broker, port, clientId, user, pass);
            stepData.put(clientId, client);
        } catch (MqttException e) {
            logger.error("Error creating mqtt client with id " + clientId, e);
        }
    }

    @Given("^Connect to mqtt client \"(.*)\"$")
    public void connectMqttClient(String clientId) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        try {
            client.connect();
        } catch (MqttException e) {
            logger.error("Unable to connect to mqtt broker with client " + clientId, e);
            e.printStackTrace();
        }
    }

    @Given("^Disconnect mqtt client \"(.*)\"$")
    public void disconnectMqttClient(String clientId) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        try {
            client.disconnect();
        } catch (MqttException e) {
            logger.error("Unable to disconnect from mqtt broker with client " + clientId, e);
        }
    }

    @Given("^Subscribe mqtt client \"(.*)\" to topic \"(.*)\"$")
    public void subscribeMqttClient(String clientId, String topic) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        try {
            client.subscribe(topic, 1);
        } catch (MqttException e) {
            logger.error("Can not subscribe with client " + clientId);
        }
    }

    @Then("^Client \"(.*)\" has (\\d+) messages?.*$")
    public void clientCountMsg(String clientId, int numMsgs) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        int receivedMsgs = client.getRecivedMsgCnt();
        Assert.assertEquals(numMsgs, receivedMsgs);
    }

    @Given("^Publish string \"(.*)\" to topic \"(.*)\" as client \"(.*)\"")
    public void publishMqttClient(String message, String topic, String clientId) {
        BrokerClient client = (BrokerClient) stepData.get(clientId);
        try {
            client.publish(topic, 1, message);
        } catch (MqttException e) {
            logger.error("Can not publish to topic " + topic);
        }
    }

    @Given("^Create network$")
    public void createNetwork() throws DockerException, InterruptedException {
        networkConfig = NetworkConfig.builder().name(NETWORK_PREFIX).build();
        NetworkCreation networkCreation = docker.createNetwork(networkConfig);
        networkId = networkCreation.id();
    }

    @Given("^Remove network$")
    public void removeNetwork() throws DockerException, InterruptedException {
        docker.removeNetwork(networkId);
    }

    @Given("^Pull image \"(.*)\"$")
    public void pullImage(String image) throws DockerException, InterruptedException {
        docker.pull(image);
    }

    @Given("^List images by name \"(.*)\"$")
    public void listImages(String imageName) throws Exception {
        List<Image> images = docker.listImages(DockerClient.ListImagesParam.byName(imageName));
        if ((images != null) && (images.size() > 0)) {
            for (Image image : images) {
                logger.info("Image: " + image);
            }
        } else {
            logger.info("No docker images found.");
        }
    }

    @And("^Start DB container with name \"(.*)\"$")
    public void startDBContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting DB container...");
        ContainerConfig dbConfig = getDbContainerConfig();
        ContainerCreation dbContainerCreation = docker.createContainer(dbConfig, name);
        String containerId = dbContainerCreation.id();

        docker.startContainer(containerId);
        docker.connectToNetwork(containerId, networkId);
        containerMap.put("db", containerId);
        logger.info("DB container started: {}", containerId);
    }

    @And("^Start ES container with name \"(.*)\"$")
    public void startESContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting ES container...");
        ContainerConfig esConfig = getEsContainerConfig();
        ContainerCreation esContainerCreation = docker.createContainer(esConfig, name);
        String containerId = esContainerCreation.id();

        docker.startContainer(containerId);
        docker.connectToNetwork(containerId, networkId);
        containerMap.put("es", containerId);
        logger.info("ES container started: {}", containerId);
    }

    @And("^Start EventBroker container with name \"(.*)\"$")
    public void startEBContainer(String name) throws DockerException, InterruptedException {
        logger.info("Starting EventBroker container...");
        ContainerConfig ebConfig = getEventBrokerContainerConfig();
        ContainerCreation ebContainerCreation = docker.createContainer(ebConfig, name);
        String containerId = ebContainerCreation.id();

        docker.startContainer(containerId);
        docker.connectToNetwork(containerId, networkId);
        containerMap.put(name, containerId);
        logger.info("EventBroker container started: {}", containerId);
    }

    @And("^Start Message Broker container$")
    public void startEBContainer(List<BrokerConfigData> brokerConfigDataList) throws DockerException, InterruptedException {
        BrokerConfigData bcData = brokerConfigDataList.get(0);
        logger.info("Starting Message Broker container {}...", bcData.getName());
        ContainerConfig mbConfig = getBrokerContainerConfig(
                bcData.getBrokerAddress(), bcData.getBrokerIp(), bcData.getClusterName(), null,
                bcData.getMqttPort(), bcData.getMqttHostPort(), bcData.getMqttsPort(), bcData.getMqttsHostPort(),
                bcData.getWebPort(), bcData.getWebHostPort(), bcData.getDebugPort(), bcData.getDebugHostPort(),
                bcData.getBrokerInternalDebugPort(), bcData.getDockerImage());
        ContainerCreation mbContainerCreation = docker.createContainer(mbConfig, bcData.getName());
        String containerId = mbContainerCreation.id();

        docker.startContainer(containerId);
        docker.connectToNetwork(containerId, networkId);
        containerMap.put(bcData.getName(), containerId);
        logger.info("Message Broker {} container started: {}", bcData.getName(), containerId);
    }

    @Then("^Stop container with name \"(.*)\"$")
    public void stopContainer(String name) throws DockerException, InterruptedException {
        logger.info("Stopping container {}...", name);
        String containerId = containerMap.get(name);
        docker.stopContainer(containerId, 3);
        logger.info("Container {} stopped.", name);
    }

    @Then("^Remove container with name \"(.*)\"$")
    public void removeContainer(String name) throws DockerException, InterruptedException {
        logger.info("Removing container {}...", name);
        String containerId = containerMap.get(name);
        docker.removeContainer(containerId);
        logger.info("Container {} removed.", name);
    }

    /**
     * Creation of docker container configuration for broker.
     *
     * @param brokerAddr
     * @param brokerIp
     * @param clusterName
     * @param controlMessageForwarding
     * @param mqttPort                 mqtt port on docker
     * @param mqttHostPort             mqtt port on docker host
     * @param mqttsPort                mqtts port on docker
     * @param mqttsHostPort            mqtts port on docker host
     * @param webPort                  web port on docker
     * @param webHostPort              web port on docker host
     * @param debugPort                debug port on docker
     * @param debugHostPort            debug port on docker host
     * @param brokerInternalDebugPort
     * @param dockerImage              full name of image (e.g. "kapua/kapua-broker:1.6.14-SNAPSHOT")
     * @return Container configuration for specific boroker instance
     */
    private ContainerConfig getBrokerContainerConfig(String brokerAddr, String brokerIp,
                                                     String clusterName,
                                                     String controlMessageForwarding,
                                                     int mqttPort, int mqttHostPort,
                                                     int mqttsPort, int mqttsHostPort,
                                                     int webPort, int webHostPort,
                                                     int debugPort, int debugHostPort,
                                                     int brokerInternalDebugPort,
                                                     String dockerImage) {

        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort(ALL_IP, portBindings, mqttPort, mqttHostPort);
        addHostPort(ALL_IP, portBindings, mqttsPort, mqttsHostPort);
        addHostPort(ALL_IP, portBindings, webPort, webHostPort);
        addHostPort(ALL_IP, portBindings, debugPort, debugHostPort);

        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        List<String> envVars = Lists.newArrayList("commons.db.schema.update=true",
                "commons.db.connection.host=db",
                "commons.db.connection.port=3306",
                "datastore.elasticsearch.nodes=es:9200",
                "datastore.elasticsearch.provider=org.eclipse.kapua.service.elasticsearch.client.rest.RestElasticsearchClientProvider",
                "commons.eventbus.url=failover:(amqp://events-broker:5672)?jms.sendTimeout=1000",
                "certificate.jwt.private.key=file:///var/opt/activemq/key.pk8",
                "certificate.jwt.certificate=file:///var/opt/activemq/cert.pem",
                "CRYPTO_SECRET_KEY=kapuaTestsKey!!!",
                String.format("broker.ip=%s", brokerIp));
        if (envVar != null) {
            envVars.addAll(envVar);
        }

        if (debug) {
            envVars.add(String.format("ACTIVEMQ_DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=%s", brokerInternalDebugPort));
        }

        if (!Strings.isNullOrEmpty(clusterName)) {
            envVars.add(String.format("cluster.name=%s", clusterName));
        }

        if (!Strings.isNullOrEmpty(controlMessageForwarding)) {
            envVars.add(String.format("cluster.control_message_forwarding=%s", controlMessageForwarding));
        }

        String[] ports = {
                String.valueOf(mqttPort),
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
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        addHostPort(ALL_IP, portBindings, dbPort, dbPort);
        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        return ContainerConfig.builder()
                .hostConfig(hostConfig)
                .exposedPorts(String.valueOf(dbPort))
                .env(
                        "DATABASE=kapuadb",
                        "DB_USER=kapua",
                        "DB_PASSWORD=kapua",
                        "DB_PORT_3306_TCP_PORT=3306"
                )
                .image("kapua/kapua-sql:1.6.14-SNAPSHOT")
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
                .image("elasticsearch:5.4.0")
                .cmd(
                        "-Ecluster.name=kapua-datastore",
                        "-Ediscovery.type=single-node",
                        "-Etransport.host=0.0.0.0 ",
                        "-Etransport.ping_schedule=-1 ",
                        "-Etransport.tcp.connect_timeout=30s"
                )
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
                .image("kapua/kapua-events-broker:1.6.14-SNAPSHOT")
                .build();
    }

    /**
     * Add docker port to host port mapping.
     *
     * @param host         ip address of host
     * @param portBindings list ob bindings that gets updated
     * @param port         docker port
     * @param hostPort     port on host
     */
    private void addHostPort(String host, Map<String, List<PortBinding>> portBindings,
                             int port, int hostPort) {

        List<PortBinding> hostPorts = new ArrayList<>();
        hostPorts.add(PortBinding.of(host, hostPort));
        portBindings.put(String.valueOf(port), hostPorts);
    }

}
