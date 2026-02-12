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

import com.google.inject.Singleton;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSetting;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.Password;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.wire.DeviceWiresManagementService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.junit.Assert;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Singleton
public class DeviceManagamentWiresSteps extends TestBase {

    private static final String WIRES_ITEMS = "wires_items";

    private DeviceRegistryService deviceRegistryService;
    private DeviceWiresManagementService deviceWiresManagementService;
    private BrokerSetting brokerSettings = KapuaLocator.getInstance().getComponent(BrokerSetting.class);

    @Inject
    public DeviceManagamentWiresSteps(StepData stepData) {
        super(stepData);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    @After(value = "@setup")
    public void setServices() {
        brokerSettings.resetInstance();
        KapuaLocator locator = KapuaLocator.getInstance();
        deviceRegistryService = locator.getService(DeviceRegistryService.class);
        deviceWiresManagementService = locator.getService(DeviceWiresManagementService.class);
    }

    // Wire graph
    @When("Wire Graph is requested")
    public void wireGraphRequested() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceConfiguration deviceConfiguration = deviceWiresManagementService.get(device.getScopeId(), device.getId(), null);
                stepData.put(WIRES_ITEMS, deviceConfiguration);
            }
        }
    }

    @When("Last received Wire graph is uploaded and no exception is thrown")
    public void wireGraphUploaded() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceConfiguration lastReceivedWireGraph = ((DeviceConfiguration)stepData.get(WIRES_ITEMS));
                deviceWiresManagementService.put(device.getScopeId(), device.getId(), lastReceivedWireGraph, null); //exception would be thrown if Kura device did not receive the put request
            }
        }
    }

    @When("Wire graph is deleted and no exception is thrown")
    public void wireGraphDeleted() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                deviceWiresManagementService.del(device.getScopeId(), device.getId(), null); //exception would be thrown if Kura device did not receive the delete request
            }
        }
    }

    @Then("Wire component configurations are received")
    public void graphConfigurationsReceived() {
        List<DeviceComponentConfiguration> componentConfigurations = ((DeviceConfiguration)stepData.get(WIRES_ITEMS)).getComponentConfigurations();
        Assert.assertNotNull(componentConfigurations);
    }

    @Then("Component configurations are {long}")
    public void configurationSizeIs(long size) {
        List<DeviceComponentConfiguration> componentConfigurations = ((DeviceConfiguration)stepData.get(WIRES_ITEMS)).getComponentConfigurations();
        Assert.assertEquals(size, componentConfigurations.size());
    }

    @Then("There is a component configurations with id {string} and a property named {string} with value {string}")
    public void componentConfigurationIdNameValue(String id, String propertyName, String propertyValue) {
        List<DeviceComponentConfiguration> componentConfigurations = ((DeviceConfiguration)stepData.get(WIRES_ITEMS)).getComponentConfigurations();
        DeviceComponentConfiguration componentConfiguration = componentConfigurations.stream().filter(c -> c.getId().equals(id)).findAny().orElse(null);
        Map<String, Object> properties = componentConfiguration.getProperties();
        Assert.assertNotNull(componentConfiguration);
        Assert.assertNotNull(properties);
        Assert.assertTrue(properties.containsKey(propertyName));
        Assert.assertEquals(propertyValue, properties.get(propertyName));
    }

    @Then("There is a component configurations with id {string} and a password property named {string} with value {string}")
    public void componentConfigurationIdPasswordValue(String id, String propertyName, String propertyValue) {
        List<DeviceComponentConfiguration> componentConfigurations = ((DeviceConfiguration)stepData.get(WIRES_ITEMS)).getComponentConfigurations();
        DeviceComponentConfiguration componentConfiguration = componentConfigurations.stream().filter(c -> c.getId().equals(id)).findAny().orElse(null);
        Map<String, Object> properties = componentConfiguration.getProperties();
        Assert.assertNotNull(componentConfiguration);
        Assert.assertNotNull(properties);
        Assert.assertTrue(properties.containsKey(propertyName));

        Password password = (Password) properties.get(propertyName); //Would have failed if it was not a password
        Password inputPassword = new Password(propertyValue);
        Assert.assertEquals(password.toString(), inputPassword.toString());
    }

    @Then("There is a component configurations with id {string} and a array property named {string} which contains the value {string}")
    public void componentConfigurationIdArrayValue(String id, String propertyName, String propertyValue) {
        List<DeviceComponentConfiguration> componentConfigurations = ((DeviceConfiguration)stepData.get(WIRES_ITEMS)).getComponentConfigurations();
        DeviceComponentConfiguration componentConfiguration = componentConfigurations.stream().filter(c -> c.getId().equals(id)).findAny().orElse(null);
        Map<String, Object> properties = componentConfiguration.getProperties();
        Assert.assertNotNull(componentConfiguration);
        Assert.assertNotNull(properties);
        Assert.assertTrue(properties.containsKey(propertyName));

        String[] array = (String[]) properties.get(propertyName); //Would have failed if it was not an array
        Assert.assertTrue(array.length > 0);
        Assert.assertEquals(propertyValue, array[0]);
    }

}
