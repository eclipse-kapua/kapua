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
 *     Eurotech
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.LoginCredentials;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.user.User;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps for testing Access Control List functionality on Broker service.
 */
@Singleton
public class AclSteps extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(AclSteps.class);

    public static final int BROKER_START_WAIT_MILLIS = 5000;

    private static final String SYS_USERNAME = "kapua-sys";

    private static final String SYS_PASSWORD = "kapua-password";

    protected KapuaLocator locator;

    private static final String ACCOUNT = "acme";
    private static final String MAIL = "john@acme.org";
    private static final String ORG = "ACME Corp.";
    private static final String NAME = "luise";

    /**
     * Mqtt device for listening and sending data from/to broker
     */
    private static MqttDevice mqttDevice;

    /**
     * Topic / value pair containing message that was received from broker by client.
     */
    private static Map<String, Map<String, String>> clientMqttMessage;

    /**
     * Topic / value pair containing message that was received from broker by listener.
     */
    private static Map<String, String> listenerMqttMessage;

    private static AuthenticationService authenticationService;
    /**
     * Helper for creating Accoutn, User and other artifacts needed in tests.
     */
    private static AclCreator aclCreator;

    @Inject
    public AclSteps(StepData stepData) {
        super(stepData);
    }

    @After(value = "@setup")
    public void setServices() {
        locator = KapuaLocator.getInstance();
        authenticationService = locator.getService(AuthenticationService.class);

        mqttDevice = new MqttDevice();
        clientMqttMessage = new HashMap<>();
        listenerMqttMessage = new HashMap<>();

        aclCreator = new AclCreator();
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    @Given("string {string} is published to topic {string} with client {string}")
    public void clientPublishString(String payload, String topic, String clientId) {
        mqttDevice.mqttClientPublishString(clientId, payload, topic, clientMqttMessage, listenerMqttMessage);
    }

    @Given("Mqtt Device is started")
    public void startMqttDevice() throws KapuaException {
        mqttDevice.mqttSubscriberConnect();
        // Wait for broker to start
        waitInMillis(BROKER_START_WAIT_MILLIS);
        // Login with system user
        String passwd = SYS_PASSWORD;
        LoginCredentials credentials = new UsernamePasswordCredentials(SYS_USERNAME, passwd);
        authenticationService.login(credentials);
    }

    @When("Mqtt Device is stoped")
    public void stopMqttDevice() throws KapuaException {
        mqttDevice.mqttClientsDisconnect();
        mqttDevice.mqttSubscriberDisconnect();
        // Logout system user
        authenticationService.logout();
    }

    @Given("broker with clientId {string} and user {string} and password {string} is listening on topic {string}")
    public void connectClientToBroker(String clientId, String userName, String password, String topicFilter) throws Exception {
        try {
            primeException();
            mqttDevice.mqttClientConnect(clientId, userName, password, topicFilter);
        } catch (MqttException mqtte) {
            verifyException(mqtte);
        }
    }

    @Given("Connect client with clientId {string} and user {string} and password {string} and keep into device group {string}")
    public void connectClientToBrokerAndKeepIntoGroup(String clientId, String userName, String password, String deviceGroup) throws Exception {
        MqttClient mqttClient = new MqttClient(MqttDevice.BROKER_URI, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setPassword(password.toCharArray());
        options.setUserName(userName);
        options.setAutomaticReconnect(false);
        mqttClient.setCallback(new MqttCallback() {

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

            @Override
            public void connectionLost(Throwable cause) {
                logger.info("Connection lost!", cause);
            }
        });
        List<MqttClient> mqttClientList = (List<MqttClient>) stepData.get("Paho_" + deviceGroup);
        if (mqttClientList == null) {
            mqttClientList = new ArrayList<>();
            stepData.put("Paho_" + deviceGroup, mqttClientList);
        }
        mqttClientList.add(mqttClient);
        mqttClient.connect(options);
    }

    @Then("Clients from group {string} are connected")
    public void checkClientConnected(String deviceGroup) throws Exception {
        List<MqttClient> mqttDeviceList = (List<MqttClient>) stepData.get("Paho_" + deviceGroup);
        if (mqttDeviceList != null) {
            mqttDeviceList.forEach(mqttDevice -> Assert.assertTrue("Client " + mqttDevice.getClientId() + " should be connected!", mqttDevice.isConnected()));
        }
    }

    @Given("clients are disconnected")
    public void disconnectClientsFromBroker() {
        mqttDevice.mqttClientsDisconnect();
    }

    @Then("Broker receives string {string} on topic {string} within {int} second(s)")
    public void brokerReceivesStringOnTopic(String payload, String topic, int timeout) throws InterruptedException {
        int executions = 0;
        while (executions++ < timeout) {
            if (brokerReceivesStringOnTopicInternal(payload, topic, false)) {
                return;
            }
            Thread.sleep(1000);
        }
        brokerReceivesStringOnTopicInternal(payload, topic, true);
    }

    private boolean brokerReceivesStringOnTopicInternal(String payload, String topic, boolean timeout) {
        boolean result = false;
        if ((listenerMqttMessage != null) && (listenerMqttMessage.size() == 1)) {
            String message = listenerMqttMessage.get(topic);
            if (timeout) {
                Assert.assertEquals(payload, message);
            } else {
                result = payload.equals(message);
            }
        } else {
            if (timeout) {
                Assert.fail("Message not received by broker.");
            }
        }
        return result;
    }

    @Then("Broker doesn't receive string {string} on topic {string}")
    public void brokerDoesntReceiveStringOnTopic(String payload, String topic) {
        if ((listenerMqttMessage != null) && (listenerMqttMessage.size() >= 1)) {
            String message = listenerMqttMessage.get(topic);
            Assert.assertNotEquals(payload, message);
        }
    }

    @Then("client {string} receives string {string} on topic {string} within {int} second(s)")
    public void iReceiveStringOnTopic(String clientId, String payload, String topic, int timeout) throws InterruptedException {
        int executions = 0;
        while (executions++ < timeout) {
            if (iReceiveStringOnTopicInternal(clientId, payload, topic, false)) {
                return;
            }
            Thread.sleep(1000);
        }
        iReceiveStringOnTopicInternal(clientId, payload, topic, true);
    }

    private boolean iReceiveStringOnTopicInternal(String clientId, String payload, String topic, boolean timeout) {
        Map<String, String> messages = clientMqttMessage.get(clientId);
        boolean result = false;
        if ((messages != null) && (messages.size() >= 1)) {
            String message = messages.get(topic);
            if (timeout) {
                Assert.assertEquals(payload, message);
            } else {
                result = payload.equals(message);
            }
        } else {
            if (timeout) {
                // TODO log (or append in the failure message) this error in a better way
                Assert.fail("Message not received by broker." + (listenerMqttMessage != null && listenerMqttMessage.size() > 0 ? listenerMqttMessage.get(0) : " NULL"));
            }
        }
        return result;
    }

    @Then("client {string} doesn't receive string {string} on topic {string}")
    public void clientDoesntReceiveStringOnTopic(String clientId, String payload, String topic) {
        Map<String, String> messages = clientMqttMessage.get(clientId);
        if ((messages != null) && (messages.size() >= 1)) {
            String message = messages.get(topic);
            Assert.assertNotEquals(payload, message);
        }
    }

    @And("broker account and user are created")
    public void createBrokerAccountAndUser() throws Exception {
        createBrokerAccountAndUser(ACCOUNT, ORG, MAIL, NAME);
    }

    @And("broker account {string} with organization {string} and email {string} and user {string} are created")
    public void createBrokerAccountAndUser(String accountName, String organization, String email, String userName) throws Exception {
        Account account = aclCreator.createAccount(accountName, ORG, MAIL);
        User user = aclCreator.createUser(account, userName);
        aclCreator.attachUserCredentials(account, user);
        aclCreator.attachBrokerPermissions(account, user);
    }

    @And("device account and user are created")
    public void createDeviceAccountAndUser() throws Exception {
        Account account = aclCreator.createAccount(ACCOUNT, ORG, MAIL);
        User user = aclCreator.createUser(account, NAME);
        aclCreator.attachUserCredentials(account, user);
        aclCreator.attachDevicePermissions(account, user);
    }

    @And("data view account and user are created")
    public void createDataViewAccountAndUser() throws Exception {
        Account account = aclCreator.createAccount(ACCOUNT, ORG, MAIL);
        User user = aclCreator.createUser(account, NAME);
        aclCreator.attachUserCredentials(account, user);
        aclCreator.attachDataViewPermissions(account, user);
    }

    @And("data manage account and user are created")
    public void createDataManageAccountAndUser() throws Exception {
        Account account = aclCreator.createAccount(ACCOUNT, ORG, MAIL);
        User user = aclCreator.createUser(account, NAME);
        aclCreator.attachUserCredentials(account, user);
        aclCreator.attachDataManagePermissions(account, user);
    }

    @And("other broker account and user are created")
    public void createOtherBrokerAccountAndUser() throws Exception {
        Account account = aclCreator.createAccount("domino", "Domino Corp.", "lisa@domino.org");
        User user = aclCreator.createUser(account, "domina");
        aclCreator.attachUserCredentials(account, user);
        aclCreator.attachBrokerPermissions(account, user);
    }

    @Then("exception is thrown")
    public void exceptionIsThrown() {
        Exception e = (Exception) stepData.get("exception");
        Assert.assertNotNull("Exception expected!", e);
    }

    @Then("exception is not thrown")
    public void exceptionIsNotThrown() {
        Exception e = (Exception) stepData.get("exception");
        Assert.assertNull("Exception not expected!", e);
    }

    /**
     * Simple wait implementation.
     *
     * @param millis
     *         milli seconds
     */
    private void waitInMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

}
