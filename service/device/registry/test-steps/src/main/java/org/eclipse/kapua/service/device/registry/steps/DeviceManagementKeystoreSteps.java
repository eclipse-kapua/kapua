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
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.steps;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSetting;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.service.device.management.keystore.DeviceKeystoreManagementService;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSR;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSRInfo;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCertificate;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItem;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItemQuery;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItems;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreKeypair;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystores;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.junit.Assert;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Singleton
public class DeviceManagementKeystoreSteps extends TestBase {

    private static final String KEYSTORES = "keystores";
    private static final String KEYSTORES_ITEMS = "keystoreItems";
    private static final String KEYSTORES_ITEM = "keystoreItem";
    private static final String DEVICE_KEYSTORE_KEYPAIR = "deviceKeystoreKeypair";
    private static final String DEVICE_KEYSTORE_CSR = "deviceKeystoreCSR";

    private DeviceRegistryService deviceRegistryService;

    private DeviceKeystoreManagementService deviceKeystoreManagementService;
    private BrokerSetting brokerSettings = KapuaLocator.getInstance().getComponent(BrokerSetting.class);

    /**
     * Scenario scoped step data.
     */
    @Inject
    public DeviceManagementKeystoreSteps(StepData stepData) {
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
        deviceKeystoreManagementService = locator.getService(DeviceKeystoreManagementService.class);
    }

    @When("Keystores are requested")
    public void keystoreRequested() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceKeystores deviceKeystores = deviceKeystoreManagementService.getKeystores(device.getScopeId(), device.getId(), null);
                List<DeviceKeystore> keystores = deviceKeystores.getKeystores();
                stepData.put(KEYSTORES, keystores);
            }
        }
    }

    @Then("Keystores are received")
    public void keystoreReceived() {
        List<DeviceKeystore> deviceKeystores = (List<DeviceKeystore>) stepData.get(KEYSTORES);
        Assert.assertNotNull(deviceKeystores);
    }

    @Then("Keystores are {long}")
    public void keystoreAreSize(long size) {
        List<DeviceKeystore> keystores = (List<DeviceKeystore>) stepData.get(KEYSTORES);
        Assert.assertEquals(size, keystores.size());
    }

    @Then("Keystores has Keystore named {string} is present")
    public void keystoreNamedIsPresent(String keystoreName) {
        List<DeviceKeystore> keystores = (List<DeviceKeystore>) stepData.get(KEYSTORES);
        DeviceKeystore keystore = keystores.stream().filter(k -> k.getId().equals(keystoreName)).findAny().orElse(null);
        Assert.assertNotNull(keystore);
    }

    @Then("Keystores has Keystore named {string} has type {string}")
    public void keystoreNamedHasType(String keystoreName, String keystoreType) {
        List<DeviceKeystore> keystores = (List<DeviceKeystore>) stepData.get(KEYSTORES);
        DeviceKeystore keystore = keystores.stream().filter(k -> k.getId().equals(keystoreName)).findAny().orElse(null);
        Assert.assertNotNull(keystore);
        Assert.assertEquals(keystoreType, keystore.getKeystoreType());
    }

    @Then("Keystores has Keystore named {string} has size {int}")
    public void keystoreNamedHasSize(String keystoreName, Integer keystoreSize) {
        List<DeviceKeystore> keystores = (List<DeviceKeystore>) stepData.get(KEYSTORES);
        DeviceKeystore keystore = keystores.stream().filter(k -> k.getId().equals(keystoreName)).findAny().orElse(null);
        Assert.assertNotNull(keystore);
        Assert.assertEquals(keystoreSize, keystore.getSize());
    }

    @When("All Keystore Items are requested")
    public void requestKeystoreItems() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceKeystoreItems keystoreItems = deviceKeystoreManagementService.getKeystoreItems(device.getScopeId(), device.getId(), null);
                List<DeviceKeystoreItem> keystoreItemList = keystoreItems.getKeystoreItems();
                stepData.put(KEYSTORES_ITEMS, keystoreItemList);
            }
        }
    }

    @When("Keystore Items with alias {string} are requested")
    public void requestKeystoreItemsByAlias(String alias) throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceKeystoreItemQuery query = new DeviceKeystoreItemQuery();
                query.setAlias(alias);
                DeviceKeystoreItems keystoreItems = deviceKeystoreManagementService.getKeystoreItems(device.getScopeId(), device.getId(), query, null);
                List<DeviceKeystoreItem> keystoreItemList = keystoreItems.getKeystoreItems();
                stepData.put(KEYSTORES_ITEMS, keystoreItemList);
            }
        }
    }

    @When("Keystore Items with keystore id {string} are requested")
    public void requestKeystoreItemsByKeystoreId(String keystoreId) throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceKeystoreItemQuery query = new DeviceKeystoreItemQuery();
                query.setKeystoreId(keystoreId);
                DeviceKeystoreItems keystoreItems = deviceKeystoreManagementService.getKeystoreItems(device.getScopeId(), device.getId(), query, null);
                List<DeviceKeystoreItem> keystoreItemList = keystoreItems.getKeystoreItems();
                stepData.put(KEYSTORES_ITEMS, keystoreItemList);
            }
        }
    }

    @Then("Keystore Items are received")
    public void keystoreItemsReceived() {
        List<DeviceKeystoreItem> deviceKeystoreItems = (List<DeviceKeystoreItem>) stepData.get(KEYSTORES_ITEMS);
        Assert.assertNotNull(deviceKeystoreItems);
    }

    @Then("Keystore Items are {long}")
    public void keystoreItemsAreSize(long size) {
        List<DeviceKeystoreItem> deviceKeystoreItems = (List<DeviceKeystoreItem>) stepData.get(KEYSTORES_ITEMS);
        Assert.assertEquals(size, deviceKeystoreItems.size());
    }

    @Then("Keystore Items has Item with alias {string} is present")
    public void keystoreItemsWithAliasIsPresent(String keystoreItemAlias) {
        List<DeviceKeystoreItem> deviceKeystoreItems = (List<DeviceKeystoreItem>) stepData.get(KEYSTORES_ITEMS);
        DeviceKeystoreItem keystoreItem = deviceKeystoreItems.stream().filter(k -> k.getAlias().equals(keystoreItemAlias)).findAny().orElse(null);
        Assert.assertNotNull(keystoreItem);
    }

    @Then("Keystore Items has Item with alias {string} is not present")
    public void keystoreItemsWithAliasIsNotPresent(String keystoreItemAlias) {
        List<DeviceKeystoreItem> deviceKeystoreItems = (List<DeviceKeystoreItem>) stepData.get(KEYSTORES_ITEMS);
        DeviceKeystoreItem keystoreItem = deviceKeystoreItems.stream().filter(k -> k.getAlias().equals(keystoreItemAlias)).findAny().orElse(null);
        Assert.assertNull(keystoreItem);
    }

    @Then("Keystore Items has Item with alias {string} has type {string}")
    public void keystoreItemsWithAliasHasType(String keystoreName, String keystoreType) {
        List<DeviceKeystoreItem> keystoreItems = (List<DeviceKeystoreItem>) stepData.get(KEYSTORES_ITEMS);
        DeviceKeystoreItem keystoreItem = keystoreItems.stream().filter(k -> k.getAlias().equals(keystoreName)).findAny().orElse(null);
        Assert.assertNotNull(keystoreItem);
        Assert.assertEquals(keystoreType, keystoreItem.getItemType());
    }

    @When("Keystore Item with keystore id {string} and alias {string} is requested")
    public void requestKeystoreItemRequested(String keystoreId, String alias) throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceKeystoreItem keystoreItem = deviceKeystoreManagementService.getKeystoreItem(device.getScopeId(), device.getId(), keystoreId, alias, null);
                stepData.put(KEYSTORES_ITEM, keystoreItem);
            }
        }
    }

    @Then("Keystore Item is received")
    public void keystoreItemIsReceived() {
        DeviceKeystoreItem deviceKeystoreItem = (DeviceKeystoreItem) stepData.get(KEYSTORES_ITEM);
        Assert.assertNotNull(deviceKeystoreItem);
    }

    @Then("Keystore Item matches expected")
    public void keystoreItemMatchesExpected() {
        DeviceKeystoreItem deviceKeystoreItem = (DeviceKeystoreItem) stepData.get(KEYSTORES_ITEM);
        Assert.assertNotNull(deviceKeystoreItem);
        Assert.assertEquals("HttpsKeystore", deviceKeystoreItem.getKeystoreId());
        Assert.assertEquals("localhost", deviceKeystoreItem.getAlias());
        Assert.assertEquals(new Integer(2048), deviceKeystoreItem.getSize());
        Assert.assertEquals("PRIVATE_KEY", deviceKeystoreItem.getItemType());
        Assert.assertNotNull(deviceKeystoreItem.getCertificateChain());
    }

    @When("I install a Keystore Certificate with alias {string}")
    public void installKeystoreCertificateWithAlias(String alias) throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceKeystoreCertificate deviceKeystoreCertificate = new DeviceKeystoreCertificate();
                deviceKeystoreCertificate.setAlias(alias);
                deviceKeystoreCertificate.setKeystoreId("SSLKeystore");
                deviceKeystoreCertificate.setCertificate("-----BEGIN CERTIFICATE-----\n" +
                        "MIIFVzCCBD+gAwIBAgISA38CzQctm3+HkSyZPnDL8TFsMA0GCSqGSIb3DQEBCwUA\n" +
                        "MEoxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MSMwIQYDVQQD\n" +
                        "ExpMZXQncyBFbmNyeXB0IEF1dGhvcml0eSBYMzAeFw0xOTA3MTkxMDIxMTdaFw0x\n" +
                        "OTEwMTcxMDIxMTdaMBsxGTAXBgNVBAMTEG1xdHQuZWNsaXBzZS5vcmcwggEiMA0G\n" +
                        "CSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDQnt6ZBEZ/vDG0JLqVB45lO6xlLazt\n" +
                        "YpEqZlGBket6PtjUGLdE2XivTpjtUkERS1cvPBqT1DH/yEZ1CU7iT/gfZtZotR0c\n" +
                        "qEMogSGkmrN1sAV6Eb+xGT3sPm1WFeKZqKdzAScdULoweUgwbNXa9kAB1uaSYBTe\n" +
                        "cq2ynfxBKWL/7bVtoeXUOyyaiIxVPTYz5XgpjSUB+9ML/v/+084XhIKA/avGPOSi\n" +
                        "RHOB+BsqTGyGhDgAHF+CDrRt8U1preS9AKXUvZ0aQL+djV8Y5nXPQPR8c2wplMwL\n" +
                        "5W/YMrM/dBm64vclKQLVPyEPqMOLMqcf+LkfQi6WOH+JByJfywAlme6jAgMBAAGj\n" +
                        "ggJkMIICYDAOBgNVHQ8BAf8EBAMCBaAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsG\n" +
                        "AQUFBwMCMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFHc+PmokFlx8Fh/0Lob125ef\n" +
                        "fLNyMB8GA1UdIwQYMBaAFKhKamMEfd265tE5t6ZFZe/zqOyhMG8GCCsGAQUFBwEB\n" +
                        "BGMwYTAuBggrBgEFBQcwAYYiaHR0cDovL29jc3AuaW50LXgzLmxldHNlbmNyeXB0\n" +
                        "Lm9yZzAvBggrBgEFBQcwAoYjaHR0cDovL2NlcnQuaW50LXgzLmxldHNlbmNyeXB0\n" +
                        "Lm9yZy8wGwYDVR0RBBQwEoIQbXF0dC5lY2xpcHNlLm9yZzBMBgNVHSAERTBDMAgG\n" +
                        "BmeBDAECATA3BgsrBgEEAYLfEwEBATAoMCYGCCsGAQUFBwIBFhpodHRwOi8vY3Bz\n" +
                        "LmxldHNlbmNyeXB0Lm9yZzCCAQMGCisGAQQB1nkCBAIEgfQEgfEA7wB2AHR+2oMx\n" +
                        "rTMQkSGcziVPQnDCv/1eQiAIxjc1eeYQe8xWAAABbAn2/p8AAAQDAEcwRQIhAIBl\n" +
                        "IZC2ZCMDs7bkBQN79xNO84VFpe7bQcMeaqHsQH9jAiAYV5kdZBgl17M5RB44NQ+y\n" +
                        "Y/WOF1PWOrNrP3XdeEo7HAB1ACk8UZZUyDlluqpQ/FgH1Ldvv1h6KXLcpMMM9OVF\n" +
                        "R/R4AAABbAn2/o4AAAQDAEYwRAIgNYxfY0bjRfjhXjjAgyPRSLKq4O5tWTd2W4mn\n" +
                        "CpE3aCYCIGeKPyuuo9tvHbyVKF4bsoN76FmnOkdsYE0MCKeKkUOkMA0GCSqGSIb3\n" +
                        "DQEBCwUAA4IBAQCB0ykl1N2U2BMhzFo6dwrECBSFO+ePV2UYGrb+nFunWE4MMKBb\n" +
                        "dyu7dj3cYRAFCM9A3y0H967IcY+h0u9FgZibmNs+y/959wcbr8F1kvgpVKDb1FGs\n" +
                        "cuEArADQd3X+4TMM+IeIlqbGVXv3mYPrsP78LmUXkS7ufhMXsD5GSbSc2Zp4/v0o\n" +
                        "3bsJz6qwzixhqg30tf6siOs9yrpHpPnDnbRrahbwnYTpm6JP0lK53GeFec4ckNi3\n" +
                        "zT5+hEVOZ4JYPb3xVXkzIjSWmnDVbwC9MFtRaER9MhugKmiAp8SRLbylD0GKOhSB\n" +
                        "2BDf6JrzhIddKxQ75KgMZE6FQaC3Bz1DFyrj\n" +
                        "-----END CERTIFICATE-----");
                deviceKeystoreManagementService.createKeystoreCertificate(device.getScopeId(), device.getId(), deviceKeystoreCertificate, null);
            }
        }
    }

    @When("I install a Keystore Keypair with alias {string}")
    public void installKeystoreKeypairWithAlias(String alias) throws Exception {
        for (KuraDevice kuraDevice : (List<KuraDevice>) stepData.get(KURA_DEVICES)) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceKeystoreKeypair deviceKeystoreKeypair = new DeviceKeystoreKeypair();
                deviceKeystoreKeypair.setAlias(alias);
                deviceKeystoreKeypair.setKeystoreId("SSLKeystore");
                deviceKeystoreKeypair.setAlgorithm("RSA");
                deviceKeystoreKeypair.setSize(4096);
                deviceKeystoreKeypair.setSignatureAlgorithm("SHA256withRSA");
                deviceKeystoreKeypair.setAttributes("CN=Let's Encrypt Authority X3,O=Let's Encrypt,C=US");
                deviceKeystoreManagementService.createKeystoreKeypair(device.getScopeId(), device.getId(), deviceKeystoreKeypair, null);
                stepData.put(DEVICE_KEYSTORE_KEYPAIR, deviceKeystoreKeypair);
            }
        }
    }

    @Then("Keystore Items has Item with alias {string} that matches the installed certificate")
    public void keystoreItemsWithAliasMatchesCertificate(String keystoreItemAlias) {
        List<DeviceKeystoreItem> deviceKeystoreItems = (List<DeviceKeystoreItem>) stepData.get(KEYSTORES_ITEMS);
        DeviceKeystoreItem keystoreItem = deviceKeystoreItems.stream().filter(k -> k.getAlias().equals(keystoreItemAlias)).findAny().orElse(null);
        Assert.assertNotNull(keystoreItem);
        Assert.assertEquals("SSLKeystore", keystoreItem.getKeystoreId());
        Assert.assertEquals("CN=mqtt.eclipse.org", keystoreItem.getSubjectDN());
        Assert.assertEquals(1, keystoreItem.getSubjectAN().size());
        Assert.assertEquals("CN=Let's Encrypt Authority X3,O=Let's Encrypt,C=US", keystoreItem.getIssuer());
        Assert.assertNotNull(keystoreItem.getNotBefore());
        Assert.assertEquals(1563531677000L, keystoreItem.getNotBefore().getTime());
        Assert.assertNotNull(keystoreItem.getNotAfter());
        Assert.assertEquals(1571307677000L, keystoreItem.getNotAfter().getTime());
        Assert.assertEquals("SHA256withRSA", keystoreItem.getAlgorithm());
        Assert.assertEquals(new Integer(2048), keystoreItem.getSize());
        Assert.assertEquals("TRUSTED_CERTIFICATE", keystoreItem.getItemType());
    }

    @Then("Keystore Items has Item with alias {string} that matches the installed keypair")
    public void keystoreItemsWithAliasMatchesKeypair(String keystoreItemAlias) {
        List<DeviceKeystoreItem> deviceKeystoreItems = (List<DeviceKeystoreItem>) stepData.get(KEYSTORES_ITEMS);
        DeviceKeystoreItem keystoreItem = deviceKeystoreItems.stream().filter(k -> k.getAlias().equals(keystoreItemAlias)).findAny().orElse(null);
        Assert.assertNotNull(keystoreItem);
        DeviceKeystoreKeypair keystoreKeypair = (DeviceKeystoreKeypair) stepData.get(DEVICE_KEYSTORE_KEYPAIR);
        Assert.assertNotNull(keystoreKeypair);
        Assert.assertEquals(keystoreKeypair.getKeystoreId(), keystoreItem.getKeystoreId());
        Assert.assertEquals(keystoreKeypair.getAlgorithm(), keystoreItem.getAlgorithm());
        Assert.assertEquals(keystoreKeypair.getSize(), keystoreItem.getSize());
        Assert.assertEquals("PRIVATE_KEY", keystoreItem.getItemType());
    }

    @When("I delete a Keystore Item from keystore {string} with alias {string}")
    public void deleteKeystoreItemWithAlias(String keystoreId, String alias) throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                deviceKeystoreManagementService.deleteKeystoreItem(device.getScopeId(), device.getId(), keystoreId, alias, null);
            }
        }
    }

    @When("I send a Certificate Signing Request for Keystore Item with keystore {string} and alias {string}")
    public void sendCertificateSigningRequestFor(String keystoreId, String alias) throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceKeystoreCSRInfo deviceKeystoreCSRInfo = new DeviceKeystoreCSRInfo();
                deviceKeystoreCSRInfo.setKeystoreId(keystoreId);
                deviceKeystoreCSRInfo.setAlias(alias);
                deviceKeystoreCSRInfo.setSignatureAlgorithm("SHA256withRSA");
                deviceKeystoreCSRInfo.setAttributes("CN=Kura, OU=IoT, O=Eclipse, C=US");
                DeviceKeystoreCSR deviceKeystoreCSR = deviceKeystoreManagementService.createKeystoreCSR(device.getScopeId(), device.getId(), deviceKeystoreCSRInfo, null);
                stepData.put(DEVICE_KEYSTORE_CSR, deviceKeystoreCSR);
            }
        }
    }

    @Then("The Certificate Signing Request is received")
    public void keystoreCertificateSigningRequestIsReceived() {
        DeviceKeystoreCSR deviceKeystoreCSR = (DeviceKeystoreCSR) stepData.get(DEVICE_KEYSTORE_CSR);
        Assert.assertNotNull(deviceKeystoreCSR);
    }

    @Then("The Certificate Signing Request matches expected")
    public void keystoreCertificateSigningRequestMatchesExpected() {
        DeviceKeystoreCSR deviceKeystoreCSR = (DeviceKeystoreCSR) stepData.get(DEVICE_KEYSTORE_CSR);
        Assert.assertNotNull(deviceKeystoreCSR);
        Assert.assertEquals("-----BEGIN CERTIFICATE REQUEST-----\n" +
                "MIICgTCCAWkCAQAwPDELMAkGA1UEBhMCVVMxEDAOBgNVBAoTB0VjbGlwc2UxDDAK\n" +
                "BgNVBAsTA0lvVDENMAsGA1UEAxMES3VyYTCCASIwDQYJKoZIhvcNAQEBBQADggEP\n" +
                "ADCCAQoCggEBAKpmnJeOJ7wczIMj3nUe+qxAtfJaXhUJkGy+bQuEfSEKRhA9QXAT\n" +
                "bt6N5alSj9mHb0OcOESBdUEr8vt28d5qHyHUUJ3yOJH3qURGO3He8yqLuUmgMgdK\n" +
                "Dtp5bGFy5ltW/F+ASB8vJlX2jaC/Tybq8KjPTzVeEIilyQ9LDQMLmH7l+WklkpsK\n" +
                "LZHF+2fATJK7HISijozZiVfk8EFi5JXbGo9VFlKouwTU3V2NVY9f4cIftPb5pNs2\n" +
                "lEL+ZkAuaPksHzkI0z+bPwR4+tlMTxgcQE25r7fPK3FYEuOugSV8zGghI1dBDAHx\n" +
                "eHYVpduJPhz7RtdVw3x7eM7I1C2IrmfHaP0CAwEAAaAAMA0GCSqGSIb3DQEBCwUA\n" +
                "A4IBAQAC8rvMaHZ+7szRm490O0nOj2wC0yngvciyBvCqEiKGmlOjeXxJAVjTG+r6\n" +
                "tXe6Jce9weIRdbI0HHVWkNVBX7Z0xjuD/SjrXOKjx1gm1DTbkp97OTBXuPhuiNXq\n" +
                "Ihvy/j0P/yFRAUP+YRkV6N5OE76fUst/VHUvMWbEEnH9qPGYmSwV4yBgsSRiL4km\n" +
                "84uuNDaILuCuYqTMtfoPSrfcILrKMfmPRvNE5DNDbk/BsR33zyBXCjnd+/P61sKo\n" +
                "VSn6maFDBHcZP2jkBOBr8QmW8jt3oR9qWX5LXBpEHkmki8cy6FEhUOGZIuPAd8Rj\n" +
                "PfZ8kKHpraMQuOeg0ZsZcZzlZsa8\n" +
                "-----END CERTIFICATE REQUEST-----\n", deviceKeystoreCSR.getSigningRequest());
    }
}
