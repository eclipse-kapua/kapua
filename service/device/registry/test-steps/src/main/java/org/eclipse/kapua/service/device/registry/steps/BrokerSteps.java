/*******************************************************************************
 * Copyright (c) 2017, 2024 Eurotech and/or its affiliates and others
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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSetting;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.service.device.management.asset.DeviceAsset;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannel;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetManagementService;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.asset.internal.DeviceAssetsImpl;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.management.command.DeviceCommandFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
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
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps used in integration scenarios with running MQTT broker and process of
 * registering mocked Kura device registering with Kapua and issuing basic administrative
 * commands on Mocked Kura.
 */
@Singleton
public class BrokerSteps extends TestBase {

    protected static final Logger logger = LoggerFactory.getLogger(BrokerSteps.class);

    /**
     * Embedded broker configuration file from classpath resources.
     */
    public static final String ACTIVEMQ_XML = "xbean:activemq.xml";

    /**
     * Device birth topic.
     */
    private String mqttBirth;

    /**
     * Device death topic.
     */
    private String mqttDc;

    /**
     * URI of mqtt broker.
     */
    private static final String BROKER_URI = "tcp://localhost:1883";

    private static final String DEVICE = "Device";
    private static final String DEVICE_CONNECTION = "DeviceConnection";
    private static final String DEVICE_CONNECTIONS = "DeviceConnections";

    private static final String PACKAGES = "packages";
    private static final String BUNDLES = "bundles";
    private static final String CONFIGURATIONS = "configurations";
    private static final String MQTT_TEST_CLIENT_NOT_FOUND = "Mqtt test client not found";

    /**
     * Access to device management service.
     */
    private DevicePackageManagementService devicePackageManagementService;

    /**
     * Device registry service for managing devices in DB.
     */
    private DeviceRegistryService deviceRegistryService;

    /**
     * Configuration service for kura devices.
     */
    private DeviceConfigurationManagementService deviceConfiguratiomManagementService;

    /**
     * Kura bundle management service.
     */
    private DeviceBundleManagementService deviceBundleManagementService;

    /**
     * Service for issuing commnads on Kura device.
     */
    private DeviceCommandManagementService deviceCommandManagementService;

    /**
     * Factory for creating commands sent to Kura.
     */
    private DeviceCommandFactory deviceCommandFactory;

    /**
     * Service for connecting devices.
     */
    private static DeviceConnectionService deviceConnectionService;
    private static DeviceAssetManagementService deviceAssetManagementService;
    private BrokerSetting brokerSettings = new BrokerSetting();
    /**
     * Client simulating Kura device
     */
    private ArrayList<KuraDevice> kuraDevices = new ArrayList<>();

    @Inject
    public BrokerSteps(StepData stepData) {
        super(stepData);
    }

    @After(value = "@setup")
    public void setServices() throws Exception {
        KapuaLocator locator = KapuaLocator.getInstance();

        devicePackageManagementService = locator.getService(DevicePackageManagementService.class);
        deviceRegistryService = locator.getService(DeviceRegistryService.class);
        deviceConfiguratiomManagementService = locator.getService(DeviceConfigurationManagementService.class);
        deviceBundleManagementService = locator.getService(DeviceBundleManagementService.class);
        deviceCommandManagementService = locator.getService(DeviceCommandManagementService.class);
        deviceCommandFactory = locator.getFactory(DeviceCommandFactory.class);
        deviceConnectionService = locator.getService(DeviceConnectionService.class);
        deviceAssetManagementService = locator.getService(DeviceAssetManagementService.class);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        beforeInternal(scenario);
    }

    private void beforeInternal(Scenario scenario) {
        updateScenario(scenario);
        stepData.put(KURA_DEVICES, kuraDevices);
        brokerSettings.resetInstance();
    }

    @After(value = "not (@setup or @teardown)", order = 10)
    public void afterScenario() {
        disconnectAll();
    }

    @When("I start the Kura Mock")
    public void startKuraMock() {
        if (!kuraDevices.isEmpty()) {
            // Cleanup of any leftover KuraMock, if any
            for (KuraDevice kuraDevice : kuraDevices) {
                kuraDevice.mqttClientDisconnect();
            }
            kuraDevices.clear();
        }

        KuraDevice kuraDevice = new KuraDevice();
        kuraDevice.mqttClientConnect();
        kuraDevices.add(kuraDevice);
    }

    @When("I start the Kura Mock to connect to broker {string}, clientId {string}, username {string} and password {string}")
    public void startKuraMock(String brokerUri, String clientId, String username, String password) {
        if (!kuraDevices.isEmpty()) {
            // Cleanup of any leftover KuraMock, if any
            for (KuraDevice kuraDevice : kuraDevices) {
                kuraDevice.mqttClientDisconnect();
            }
            kuraDevices.clear();
        }

        KuraDevice kuraDevice = new KuraDevice(brokerUri, clientId, username, password);
        kuraDevice.mqttClientConnect();
        kuraDevices.add(kuraDevice);
    }

    @When("I restart the Kura Mock")
    public void restartKuraMock() {
        if (!kuraDevices.isEmpty()) {
            for (KuraDevice kuraDevice : kuraDevices) {
                kuraDevice.mqttClientSetup();
                kuraDevice.mqttClientConnect();
            }
        }
    }

    @When("I get the KuraMock device(s) after {int} seconds")
    public void getKuraMockDevice(int seconds) throws Exception {
        ArrayList<Device> deviceList = new ArrayList<>();
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device;
            int loop = 0;
            do {
                device = deviceRegistryService.findByClientId(getCurrentScopeId(), kuraDevice.getClientId());
                if (device == null) {
                    Thread.sleep(1000);
                }
            }
            while (device == null && loop++ < seconds);
            if (device != null) {
                deviceList.add(device);
                stepData.put(DEVICE, device);
            }
        }
        stepData.put("DeviceList", deviceList);
    }

    @When("KuraMock is disconnected")
    public void kuraMockDisconnected() throws Exception {
        deviceDeathMessage();

        for (KuraDevice kuraDevice : kuraDevices) {
            kuraDevice.mqttClientDisconnect();
        }
    }

    @When("Device birth message is sent")
    public void deviceBirthMessage() throws Exception {
        for (KuraDevice kuraDevice : kuraDevices) {
            mqttBirth = "$EDC/kapua-sys/" + kuraDevice.getClientId() + "/MQTT/BIRTH";
            kuraDevice.sendMessageFromFile(mqttBirth, 0, false, "/mqtt/rpione3_MQTT_BIRTH.mqtt");
        }
    }

    @And("Device birth message is sent using account name {string}")
    public void deviceBirthMessage(String accountName) throws Exception {
        for (KuraDevice kuraDevice : kuraDevices) {
            mqttBirth = "$EDC/" + accountName + "/" + kuraDevice.getClientId() + "/MQTT/BIRTH";
            kuraDevice.sendMessageFromFile(mqttBirth, 0, false, "/mqtt/rpione3_MQTT_BIRTH.mqtt");
        }
    }

    /**
     * Checks that the {@link Device} with the given {@link Device#getClientId()} has {@link DeviceConnection#getStatus()} {@link DeviceConnectionStatus#CONNECTED} within the given time.
     *
     * @param clientId The {@link Device#getClientId()} to check
     * @param waitSeconds The max time of wait in seconds
     * @throws Exception
     * @since 1.2.0
     */
    @When("Device {string} is connected within {int}s")
    public void deviceConnected(String clientId, int waitSeconds) throws Exception {

        long now = System.currentTimeMillis();
        while ((System.currentTimeMillis() - now) < (waitSeconds * 1000L)){
            Device kuraDevice = deviceRegistryService.findByClientId(getCurrentScopeId(), clientId);

            if (kuraDevice != null &&
                    kuraDevice.getConnection() != null &&
                    DeviceConnectionStatus.CONNECTED.equals(kuraDevice.getConnection().getStatus())) {
                return;
            }

            TimeUnit.MILLISECONDS.sleep(100);
        }

        Assert.fail("Device " + clientId + "not connected within " + waitSeconds + "s");
    }

    @When("Device(s) (is/are) connected within {int} second(s)")
    public void deviceConnected(int timeout) throws Exception {
        try {
            deviceBirthMessage();
            boolean checkDone = false;
            while (!checkDone && timeout-- > 0) {
                checkDone = true;
                logger.info("Device(s) status countdown check: {}", timeout);
                for (KuraDevice kuraDevice : kuraDevices) {
                    Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
                    boolean deviceStatusCheck = device != null &&
                            device.getConnection() != null &&
                            DeviceConnectionStatus.CONNECTED.equals(device.getConnection().getStatus());
                    checkDone = checkDone && deviceStatusCheck;
                }
                if (!checkDone) {
                    Thread.sleep(1000);
                }
            }
            Assert.assertTrue("Bad Device(s) status (expected CONNECTED)", checkDone);
            logger.info("Device(s) status check: {} DONE", timeout);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("Device death message is sent")
    public void deviceDeathMessage() throws Exception {
        for (KuraDevice kuraDevice : kuraDevices) {
            mqttDc = "$EDC/kapua-sys/" + kuraDevice.getClientId() + "/MQTT/DC";
            kuraDevice.sendMessageFromFile(mqttDc, 0, false, "/mqtt/rpione3_MQTT_DC.mqtt");
        }
    }

    @When("Device is disconnected")
    public void deviceDisconnected() throws Exception {
        deviceDeathMessage();
    }

    @When("Packages are requested")
    public void requestPackages() throws Exception {
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DevicePackages deploymentPackages = devicePackageManagementService.getInstalled(device.getScopeId(),
                        device.getId(), null);
                List<DevicePackage> packages = deploymentPackages.getPackages();
                stepData.put(PACKAGES, packages);
            }
        }
    }

    @Then("Packages are received")
    public void packagesReceived() {
        List<DevicePackage> packages = (List<DevicePackage>) stepData.get(PACKAGES);
        if (packages != null) {
            Assert.assertEquals(1, packages.size());
        }
    }

    @Then("Number of received packages is {long}")
    public void checkNumberOfReceivedDevicePackages(long number) {
        List<DevicePackage> receivedPackages = (List<DevicePackage>) stepData.get(PACKAGES);
        Assert.assertEquals(number, receivedPackages.size());
    }

    @Then("Package named {string} with version {string} is received")
    public void assertPackage(String packageName, String version) {
        final DevicePackage pkg = findPackageByNameAndVersion(packageName, version);
        Assert.assertEquals(packageName, pkg.getName());
        Assert.assertEquals(version, pkg.getVersion());
    }

    private DevicePackage findPackageByNameAndVersion(String packageSymbolicName, String version) {
        List<DevicePackage> savedPackages = (List<DevicePackage>) stepData.get(PACKAGES);
        List<DevicePackage> packages = savedPackages.stream()
                .filter(bundle -> bundle.getName().equals(packageSymbolicName))
                .filter(bundle -> bundle.getVersion().equals(version))
                .collect(Collectors.toList());
        if (packages.isEmpty()) {
            Assert.fail(String.format("Package %s/%s is not present", packageSymbolicName, version));
        }
        if (packages.size() > 1) {
            Assert.fail(String.format("There is more than one entry for bundle %s/%s", packageSymbolicName, version));
        }
        return packages.get(0);
    }

    @When("Bundles are requested")
    public void requestBundles() throws Exception {
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            Assert.assertNotNull(device);
            DeviceBundles deviceBundles = deviceBundleManagementService.get(device.getScopeId(), device.getId(), null);
            List<DeviceBundle> bundles = deviceBundles.getBundles();
            stepData.put(BUNDLES, bundles);
        }
    }

    @Then("Bundles are received")
    public void bundlesReceived() {
        List<DeviceBundle> bundles = (List<DeviceBundle>) stepData.get(BUNDLES);
        Assert.assertEquals(137, bundles.size());
    }

    @When("A bundle named {string} with id {long} and version {string} is present and {string}")
    public void bundleIsPresent(String bundleSymbolicName, long id, String version, String state) {
        DeviceBundle bundle = findBundleByNameAndVersion(bundleSymbolicName, version);
        Assert.assertEquals(id, bundle.getId());
        Assert.assertEquals(state, bundle.getState());
    }

    private DeviceBundle findBundleByNameAndVersion(final String bundleSymbolicName, final String version) {
        List<DeviceBundle> savedBundles = (List<DeviceBundle>) stepData.get(BUNDLES);
        List<DeviceBundle> bundles = savedBundles.stream()
                .filter(bundle -> bundle.getName().equals(bundleSymbolicName))
                .filter(bundle -> bundle.getVersion().equals(version))
                .collect(Collectors.toList());
        if (bundles.isEmpty()) {
            Assert.fail(String.format("Bundle %s/%s is not present", bundleSymbolicName, version));
        }
        if (bundles.size() > 1) {
            Assert.fail(String.format("There is more than one entry for bundle %s/%s", bundleSymbolicName, version));
        }
        return bundles.get(0);
    }

    private DeviceAsset findAssetByName(final String assetSymbolicName) {
        List<DeviceAsset> savedAssets = (List<DeviceAsset>) stepData.get("assets");
        List<DeviceAsset> assets = savedAssets.stream()
                .filter(asset -> asset.getName().equals(assetSymbolicName))
                .collect(Collectors.toList());
        if (assets.isEmpty()) {
            Assert.fail(String.format("Asset %s is not present", assetSymbolicName));
        }
        if (assets.size() > 1) {
            Assert.fail(String.format("There is more than one entry for asset %s", assetSymbolicName));
        }
        return assets.get(0);
    }

    @When("Configuration is requested")
    public void requestConfiguration() throws Exception {
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            Assert.assertNotNull(device);
            DeviceConfiguration deviceConfiguration = deviceConfiguratiomManagementService.get(device.getScopeId(), device.getId(), null, null, null);
            stepData.put(CONFIGURATIONS, deviceConfiguration);
        }
    }

    @When("A Configuration named {string} has property {string} with value {string}")
    public void checkConfiguration(String configurationName, String configurationKey, String configurationValue) {
        DeviceComponentConfiguration configuration = findConfigurationByNameAndValue(configurationName, configurationKey, configurationValue);
        Assert.assertEquals(configurationName, configuration.getDefinition().getId());
        Assert.assertTrue(configuration.getProperties().containsKey(configurationKey));
        Assert.assertTrue(configuration.getProperties().get(configurationKey).toString().equals(configurationValue));
    }

    private DeviceComponentConfiguration findConfigurationByNameAndValue(final String configurationName, final String configurationKey, final String configurationValue) {
        DeviceConfiguration deviceConfiguration = (DeviceConfiguration) stepData.get(CONFIGURATIONS);
        List<DeviceComponentConfiguration> configurations = deviceConfiguration.getComponentConfigurations().stream()
                .filter(configuration -> configuration.getDefinition().getId().equals(configurationName))
                .filter(configuration -> configuration.getProperties().containsKey(configurationKey)
                        && configuration.getProperties().get(configurationKey).toString().equals(configurationValue))
                .collect(Collectors.toList());
        if (configurations.isEmpty()) {
            Assert.fail(String.format("Configuration %s with value %s for property %s is not present", configurationName, configurationValue, configurationKey));
        }
        if (configurations.size() > 1) {
            Assert.fail(String.format("There is more than one entry for configuration %s", configurationName));
        }
        return configurations.get(0);
    }

    @Then("Configuration is received")
    public void configurationReceived() {
        DeviceConfiguration configurations = (DeviceConfiguration) stepData.get(CONFIGURATIONS);
        Assert.assertEquals(17, configurations.getComponentConfigurations().size());
    }

    @When("Command {string} is executed")
    public void executeCommand(String command) throws Exception {
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            DeviceCommandInput commandInput = deviceCommandFactory.newCommandInput();
            commandInput.setCommand(command);
            commandInput.setRunAsynch(false);
            commandInput.setTimeout(0);
            DeviceCommandOutput deviceCommandOutput = deviceCommandManagementService.exec(device.getScopeId(),
                    device.getId(), commandInput, null);
            Integer commandExitCode = deviceCommandOutput.getExitCode();
            stepData.put("commandExitCode", commandExitCode);
        }
    }

    @Then("Exit code {int} is received")
    public void configurationReceived(int expectedExitCode) {
        Integer commandExitCode = (Integer) stepData.get("commandExitCode");
        Assert.assertEquals(expectedExitCode, commandExitCode.intValue());
    }

    @Then("Device is connected with {string} server host")
    public void deviceWithServerIp(String serverIp) {
        DeviceConnection deviceConn = null;
        stepData.put("ExceptionCaught", false);
        try {
            for (KuraDevice kuraDevice : kuraDevices) {
                deviceConn = deviceConnectionService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            }
        } catch (KapuaException ex) {
            stepData.put("ExceptionCaught", true);
            return;
        }
        Assert.assertEquals(serverIp, deviceConn.getServerIp());
    }


    @When("Client with name {string} with client id {string} user {string} password {string} is connected")
    public void clientConnect(String clientName, String clientId, String user, String password) throws Exception {
        MqttClient mqttClient = null;
        MqttConnectOptions clientOpts = new MqttConnectOptions();
        primeException();
        try {
            mqttClient = new MqttClient(BROKER_URI, clientId, new MemoryPersistence());
            clientOpts.setUserName(user);
            clientOpts.setPassword(password.toCharArray());
            mqttClient.connect(clientOpts);
            mqttClient.setCallback(new TestClientCallback(clientId));
        } catch (MqttException e) {
            verifyException(e);
        }
        stepData.put(clientName, mqttClient);
    }

    @When("topic {string} content {string} is published by client named {string}")
    public void publishMessageByClient(String topic, String content, String clientName) throws Exception {
        MqttClient mqttClient = (MqttClient) stepData.get(clientName);
        byte[] payload = Files.readAllBytes(Paths.get(getClass().getResource(content).toURI()));

        if (mqttClient == null) {
            throw new Exception(MQTT_TEST_CLIENT_NOT_FOUND);
        }
        mqttClient.publish(topic, payload, 0, false);
    }

    @Then("Client named {string} is connected")
    public void clientConnected(String clientName) throws Exception {
        MqttClient mqttClient = (MqttClient) stepData.get(clientName);
        if (mqttClient == null) {
            throw new Exception(MQTT_TEST_CLIENT_NOT_FOUND);
        }
        Assert.assertEquals(true, mqttClient.isConnected());
    }

    @Then("Client named {string} is not connected")
    public void clientNotConnected(String clientName) throws Exception {
        MqttClient mqttClient = (MqttClient) stepData.get(clientName);
        if (mqttClient == null) {
            throw new Exception(MQTT_TEST_CLIENT_NOT_FOUND);
        }
        Assert.assertEquals(false, mqttClient.isConnected());
    }

    @Then("Disconnect client with name {string}")
    public void disconnectClient(String clientName) throws Exception {
        MqttClient mqttClient = (MqttClient) stepData.get(clientName);
        if (mqttClient == null) {
            throw new Exception(MQTT_TEST_CLIENT_NOT_FOUND);
        }
        try {
            mqttClient.disconnect();
            mqttClient.close();
        } catch (Exception e) {
            // Exception eaten on purpose.
            // Disconnect is for sanity only.
        }
    }

    @When("I Force Disconnect connection with client id {string}")
    public void disconnectConnectionWithClientId(String clientId) throws Exception {
        DeviceConnection deviceConnection = deviceConnectionService.findByClientId(SYS_SCOPE_ID, clientId);
        Assert.assertNotNull(deviceConnection);
        logger.info("Found device connection for clientId {} - scope: {}, id: {}", clientId, deviceConnection.getScopeId(), deviceConnection.getId());

        KapuaSecurityUtils.doPrivileged(() -> {
            deviceConnectionService.disconnect(deviceConnection.getScopeId(), deviceConnection.getId());
        });

    }

    @Then("Device(s) status is {string} within {int} second(s) for client id {string}")
    public void deviceStatusIs(String expectedStatus, int timeout, String clientId) throws Exception {
        String currentStatus = null;
        while(!expectedStatus.equals(currentStatus) && timeout-->0) {
            Thread.sleep(1000);

            logger.info("Device(s) status countdown check: {}", timeout);
            DeviceConnection deviceConnection = deviceConnectionService.findByClientId(SYS_SCOPE_ID, clientId);
            Assert.assertNotNull("Found deviceConnection for clientId " + clientId, deviceConnection);
            logger.info("Device: {} - connection status: {}", deviceConnection.getClientId(), deviceConnection.getStatus());
            currentStatus = deviceConnection.getStatus().toString();
        }
        Assert.assertEquals("Unexpected device status", expectedStatus, currentStatus);
        logger.info("Device(s) status check: {} DONE", timeout);
    }

    @Then("Device(s) status is {string} within {int} second(s)")
    public void deviceStatusIs(String deviceStatus, int timeout) throws Exception {
        boolean checkDone = false;
        while (!checkDone && timeout-- > 0) {
            checkDone = true;
            logger.info("Device(s) status countdown check: {}", timeout);
            for (KuraDevice kuraDevice : kuraDevices) {
                DeviceConnection deviceConn = deviceConnectionService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
                logger.info("Device: {} - {} - {} - connection status: {}", kuraDevice.getClientId(), kuraDevice.isConnected(), kuraDevice.mqttClientInstanceId(), deviceConn.getStatus());
                boolean deviceStatusCheck = deviceStatus.equals(deviceConn.getStatus().toString());
                checkDone = checkDone && deviceStatusCheck;
            }
            if (!checkDone) {
                Thread.sleep(1000);
            }
        }
        Assert.assertTrue("Bad Device(s) status (expected " + deviceStatus + ")", checkDone);
        logger.info("Device(s) status check: {} DONE", timeout);
    }

    @And("I add {int} devices to Kura Mock")
    public void iAddDeviceToKuraMock(int numberOfDevices) {
        if (!kuraDevices.isEmpty()) {
            kuraDevices.clear();
        }
        for (int i = 0; i < numberOfDevices; i++) {
            String clientId = "device" + i;
            KuraDevice kuraDevice = new KuraDevice();
            kuraDevice.addMoreThanOneDeviceToKuraMock(clientId);
            kuraDevice.mqttClientConnect();
            kuraDevices.add(kuraDevice);
        }
    }

    @And("Device assets are requested")
    public void deviceAssetsAreRequested() throws Exception {
        DeviceAssets deviceAssets = new DeviceAssetsImpl();
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            Assert.assertNotNull(device);
            DeviceAssets deviceAsset = deviceAssetManagementService.read(device.getScopeId(), device.getId(), deviceAssets, null);
            List<DeviceAsset> assets = deviceAsset.getAssets();
            stepData.put("assets", assets);
        }
    }

    @And("Asset with name {string} and channel with name {string} and value {int} are received")
    public void assetWithNameAndChannelWithNameAndValueAreReceived(String assetName, String channelName, int channelValue) throws Throwable {
        DeviceAsset asset = findAssetByName(assetName);
        Assert.assertEquals(assetName, asset.getName());
        for (DeviceAssetChannel deviceAssetChannel : asset.getChannels()) {
            Assert.assertEquals(channelName, deviceAssetChannel.getName());
            Assert.assertEquals(channelValue, deviceAssetChannel.getValue());
        }
    }

    @And("Packages are requested and {int} package(s) (is/are) received")
    public void packagesAreRequestedAndPackageIsReceived(int numberOfPackages) throws Exception {
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DevicePackages deploymentPackages = devicePackageManagementService.getInstalled(device.getScopeId(),
                        device.getId(), null);
                List<DevicePackage> packages = deploymentPackages.getPackages();
                stepData.put(PACKAGES, packages);

                Assert.assertEquals(numberOfPackages, packages.size());
            }
        }
    }

    private void disconnectAll() {
        for (KuraDevice kuraDevice : kuraDevices) {
            if (kuraDevice != null) {
                kuraDevice.mqttClientDisconnect();
            }
        }
        kuraDevices.clear();
    }

    class TestClientCallback implements MqttCallback {

        String clientId;

        public TestClientCallback(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public void connectionLost(Throwable cause) {
            logger.info("Client {} lost connection: {}", clientId, cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            logger.info("Message arrived for client {}: {}", clientId, topic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }

    }
}
