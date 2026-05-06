/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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

import com.google.inject.Singleton;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementService;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundleAction;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundles;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainerAction;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainers;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImage;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImages;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventoryItem;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackage;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackages;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackage;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackages;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.junit.Assert;

import javax.inject.Inject;
import java.util.List;

@Singleton
public class DeviceManagementInventorySteps extends TestBase {

    private static final String INVENTORY_ITEMS = "inventory_inventory";
    private static final String INVENTORY_BUNDLES = "inventory_bundles";
    private static final String INVENTORY_CONTAINERS = "inventory_container";
    private static final String INVENTORY_IMAGES = "inventory_images";
    private static final String INVENTORY_SYSTEM_PACKAGES = "inventory_system_packages";
    private static final String INVENTORY_DEPLOYMENT_PACKAGES = "inventory_deployment_packages";

    private DeviceRegistryService deviceRegistryService;

    private DeviceInventoryManagementService deviceInventoryManagementService;

    @Inject
    public DeviceManagementInventorySteps(StepData stepData) {
        super(stepData);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();
        deviceRegistryService = locator.getService(DeviceRegistryService.class);
        deviceInventoryManagementService = locator.getService(DeviceInventoryManagementService.class);
    }

    // /inventory
    @When("Inventory Items are requested")
    public void inventoryItemsRequested() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceInventory deviceInventory = deviceInventoryManagementService.getInventory(device.getScopeId(), device.getId(), null);
                List<DeviceInventoryItem> inventoryItems = deviceInventory.getInventoryItems();
                stepData.put(INVENTORY_ITEMS, inventoryItems);
            }
        }
    }

    @Then("Inventory Items are received")
    public void inventoryItemsReceived() {
        List<DeviceInventoryItem> inventoryItems = (List<DeviceInventoryItem>) stepData.get(INVENTORY_ITEMS);
        Assert.assertNotNull(inventoryItems);
    }

    @Then("Inventory Items are {long}")
    public void inventoryItemsAreSize(long size) {
        List<DeviceInventoryItem> inventoryItems = (List<DeviceInventoryItem>) stepData.get(INVENTORY_ITEMS);
        Assert.assertEquals(size, inventoryItems.size());
    }

    @Then("Inventory Items has Item named {string} is present")
    public void inventoryItemNamedIsPresent(String inventoryItemName) {
        List<DeviceInventoryItem> inventoryItems = (List<DeviceInventoryItem>) stepData.get(INVENTORY_ITEMS);
        DeviceInventoryItem inventoryItem = inventoryItems.stream().filter(i -> i.getName().equals(inventoryItemName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryItem);
    }

    @Then("Inventory Items has Item named {string} has type {string}")
    public void inventoryItemNamedHasType(String inventoryItemName, String inventoryItemType) {
        List<DeviceInventoryItem> inventoryItems = (List<DeviceInventoryItem>) stepData.get(INVENTORY_ITEMS);
        DeviceInventoryItem inventoryItem = inventoryItems.stream().filter(i -> i.getName().equals(inventoryItemName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryItem);
        Assert.assertEquals(inventoryItemType, inventoryItem.getItemType());
    }

    @Then("Inventory Items has Item named {string} has version {string}")
    public void inventoryItemNamedHasVersion(String inventoryItemName, String version) {
        List<DeviceInventoryItem> inventoryItems = (List<DeviceInventoryItem>) stepData.get(INVENTORY_ITEMS);
        DeviceInventoryItem inventoryItem = inventoryItems.stream().filter(i -> i.getName().equals(inventoryItemName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryItem);
        Assert.assertEquals(version, inventoryItem.getVersion());
    }

    // /bundles
    @When("Inventory Bundles are requested")
    public void inventoryBundlesRequested() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceInventoryBundles deviceInventoryBundles = deviceInventoryManagementService.getBundles(device.getScopeId(), device.getId(), null);
                List<DeviceInventoryBundle> inventoryBundles = deviceInventoryBundles.getInventoryBundles();
                stepData.put(INVENTORY_BUNDLES, inventoryBundles);
            }
        }
    }

    @Then("Inventory Bundles are received")
    public void inventoryBundlesReceived() {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        Assert.assertNotNull(inventoryBundles);
    }

    @Then("Inventory Bundles are {long}")
    public void inventoryBundlesAreSize(long size) {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        Assert.assertEquals(size, inventoryBundles.size());
    }

    @Then("Inventory Bundles has Bundle named {string} is present")
    public void inventoryBundleNamedIsPresent(String inventoryBundleName) {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        DeviceInventoryBundle inventoryBundle = inventoryBundles.stream().filter(b -> b.getName().equals(inventoryBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
    }

    @Then("Inventory Bundles has Bundle named {string} has id {string}")
    public void inventoryBundleNamedHasId(String inventoryBundleName, String inventoryBundleId) {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        DeviceInventoryBundle inventoryBundle = inventoryBundles.stream().filter(b -> b.getName().equals(inventoryBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
        Assert.assertEquals(inventoryBundleId, inventoryBundle.getId());
    }

    @Then("Inventory Bundles has Bundle named {string} has version {string}")
    public void inventoryBundleNamedHasVersion(String inventoryBundleName, String inventoryBundleVersion) {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        DeviceInventoryBundle inventoryBundle = inventoryBundles.stream().filter(b -> b.getName().equals(inventoryBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
        Assert.assertEquals(inventoryBundleVersion, inventoryBundle.getVersion());
    }

    @Then("Inventory Bundles has Bundle named {string} has status {string}")
    public void inventoryBundleNamedHasStatus(String inventoryBundleName, String inventoryBundleStatus) {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        DeviceInventoryBundle inventoryBundle = inventoryBundles.stream().filter(b -> b.getName().equals(inventoryBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
        Assert.assertEquals(inventoryBundleStatus, inventoryBundle.getStatus());
    }

    @Then("Inventory Bundles has Bundle named {string} is signed {string}")
    public void inventoryBundleNamedIsSigned(String inventoryBundleName, String inventoryBundleSigned) {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        DeviceInventoryBundle inventoryBundle = inventoryBundles.stream().filter(b -> b.getName().equals(inventoryBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
        Assert.assertEquals(Boolean.valueOf(inventoryBundleSigned), inventoryBundle.getSigned());
    }

    @Then("I start Inventory Bundle named {string}")
    public void inventoryBundleNamedStart(String inventoryBundleName) throws KapuaException {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        DeviceInventoryBundle inventoryBundle = inventoryBundles.stream().filter(b -> b.getName().equals(inventoryBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);

        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                deviceInventoryManagementService.execBundle(device.getScopeId(), device.getId(), inventoryBundle, DeviceInventoryBundleAction.START, null);
            }
        }
    }

    @Then("I stop Inventory Bundle named {string}")
    public void inventoryBundleNamedStop(String inventoryBundleName) throws KapuaException {
        List<DeviceInventoryBundle> inventoryBundles = (List<DeviceInventoryBundle>) stepData.get(INVENTORY_BUNDLES);
        DeviceInventoryBundle inventoryBundle = inventoryBundles.stream().filter(b -> b.getName().equals(inventoryBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);

        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                deviceInventoryManagementService.execBundle(device.getScopeId(), device.getId(), inventoryBundle, DeviceInventoryBundleAction.STOP, null);
            }
        }
    }

    // /containers
    @When("Inventory Containers are requested")
    public void inventoryContainersRequested() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceInventoryContainers deviceInventoryContainers = deviceInventoryManagementService.getContainers(device.getScopeId(), device.getId(), null);
                List<DeviceInventoryContainer> inventoryContainers = deviceInventoryContainers.getInventoryContainers();
                stepData.put(INVENTORY_CONTAINERS, inventoryContainers);
            }
        }
    }

    @Then("Inventory Containers are received")
    public void inventoryContainersReceived() {
        List<DeviceInventoryContainer> inventoryContainers = (List<DeviceInventoryContainer>) stepData.get(INVENTORY_CONTAINERS);
        Assert.assertNotNull(inventoryContainers);
    }

    @Then("Inventory Containers are {long}")
    public void inventoryContainersAreSize(long size) {
        List<DeviceInventoryContainer> inventoryContainers = (List<DeviceInventoryContainer>) stepData.get(INVENTORY_CONTAINERS);
        Assert.assertEquals(size, inventoryContainers.size());
    }

    @Then("Inventory Containers has Container named {string} is present")
    public void inventoryContainerNamedIsPresent(String inventoryContainerName) {
        List<DeviceInventoryContainer> inventoryContainers = (List<DeviceInventoryContainer>) stepData.get(INVENTORY_CONTAINERS);
        DeviceInventoryContainer inventoryContainer = inventoryContainers.stream().filter(c -> c.getName().equals(inventoryContainerName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryContainer);
    }

    @Then("Inventory Containers has Container named {string} has version {string}")
    public void inventoryContainerNamedHasVersion(String inventoryContainerName, String inventoryContainerVersion) {
        List<DeviceInventoryContainer> inventoryContainers = (List<DeviceInventoryContainer>) stepData.get(INVENTORY_CONTAINERS);
        DeviceInventoryContainer inventoryContainer = inventoryContainers.stream().filter(c -> c.getName().equals(inventoryContainerName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryContainer);
        Assert.assertEquals(inventoryContainerVersion, inventoryContainer.getVersion());
    }

    @Then("Inventory Containers has Container named {string} has type {string}")
    public void inventoryContainerNamedHasType(String inventoryContainerName, String inventoryContainerType) {
        List<DeviceInventoryContainer> inventoryContainers = (List<DeviceInventoryContainer>) stepData.get(INVENTORY_CONTAINERS);
        DeviceInventoryContainer inventoryContainer = inventoryContainers.stream().filter(c -> c.getName().equals(inventoryContainerName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryContainer);
        Assert.assertEquals(inventoryContainerType, inventoryContainer.getContainerType());
    }

    @Then("I start Inventory Container named {string}")
    public void inventoryContainerNamedStart(String inventoryContainerName) throws KapuaException {
        List<DeviceInventoryContainer> inventoryContainers = (List<DeviceInventoryContainer>) stepData.get(INVENTORY_CONTAINERS);
        DeviceInventoryContainer inventoryContainer = inventoryContainers.stream().filter(b -> b.getName().equals(inventoryContainerName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryContainer);

        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                deviceInventoryManagementService.execContainer(device.getScopeId(), device.getId(), inventoryContainer, DeviceInventoryContainerAction.START, null);
            }
        }
    }

    @Then("I stop Inventory Container named {string}")
    public void inventoryContainerNamedStop(String inventoryContainerName) throws KapuaException {
        List<DeviceInventoryContainer> inventoryContainers = (List<DeviceInventoryContainer>) stepData.get(INVENTORY_CONTAINERS);
        DeviceInventoryContainer inventoryContainer = inventoryContainers.stream().filter(b -> b.getName().equals(inventoryContainerName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryContainer);

        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                deviceInventoryManagementService.execContainer(device.getScopeId(), device.getId(), inventoryContainer, DeviceInventoryContainerAction.STOP, null);
            }
        }
    }

    // /images
    @When("Inventory Images are requested")
    public void inventoryImagesRequested() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceInventoryImages deviceInventoryImages = deviceInventoryManagementService.getImages(device.getScopeId(), device.getId(), null);
                List<DeviceInventoryImage> inventoryImages = deviceInventoryImages.getInventoryImages();
                stepData.put(INVENTORY_IMAGES, inventoryImages);
            }
        }
    }

    @Then("Inventory Images are received")
    public void inventoryImagesReceived() {
        List<DeviceInventoryImage> inventoryImages = (List<DeviceInventoryImage>) stepData.get(INVENTORY_IMAGES);
        Assert.assertNotNull(inventoryImages);
    }

    @Then("Inventory Images are {long}")
    public void inventoryImagesAreSize(long size) {
        List<DeviceInventoryImage> inventoryImages = (List<DeviceInventoryImage>) stepData.get(INVENTORY_IMAGES);
        Assert.assertEquals(size, inventoryImages.size());
    }

    @Then("Inventory Images has Image named {string} is present")
    public void inventoryImageNamedIsPresent(String inventoryImageName) {
        List<DeviceInventoryImage> inventoryImages = (List<DeviceInventoryImage>) stepData.get(INVENTORY_IMAGES);
        DeviceInventoryImage inventoryImage = inventoryImages.stream().filter(i -> i.getName().equals(inventoryImageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryImage);
    }

    @Then("Inventory Images has Image named {string} has version {string}")
    public void inventoryImageNamedHasVersion(String inventoryImageName, String inventoryImageVersion) {
        List<DeviceInventoryImage> inventoryImages = (List<DeviceInventoryImage>) stepData.get(INVENTORY_IMAGES);
        DeviceInventoryImage inventoryImage = inventoryImages.stream().filter(i -> i.getName().equals(inventoryImageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryImage);
        Assert.assertEquals(inventoryImageVersion, inventoryImage.getVersion());
    }

    @Then("Inventory Images has Image named {string} has type {string}")
    public void inventoryImageNamedHasType(String inventoryImageName, String inventoryImageType) {
        List<DeviceInventoryImage> inventoryImages = (List<DeviceInventoryImage>) stepData.get(INVENTORY_IMAGES);
        DeviceInventoryImage inventoryImage = inventoryImages.stream().filter(i -> i.getName().equals(inventoryImageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryImage);
        Assert.assertEquals(inventoryImageType, inventoryImage.getImageType());
    }

    @Then("I delete Inventory Image named {string}")
    public void inventoryImagedNamedDelete(String inventoryImageName) throws KapuaException {
        List<DeviceInventoryImage> inventoryImages = (List<DeviceInventoryImage>) stepData.get(INVENTORY_IMAGES);
        DeviceInventoryImage inventoryImage = inventoryImages.stream().filter(b -> b.getName().equals(inventoryImageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryImage);

        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                deviceInventoryManagementService.deleteImage(device.getScopeId(), device.getId(), inventoryImage, null);
            }
        }
    }

    // /systemPackages
    @When("Inventory System Packages are requested")
    public void inventorySystemPackagesRequested() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceInventorySystemPackages deviceSystemPackages = deviceInventoryManagementService.getSystemPackages(device.getScopeId(), device.getId(), null);
                List<DeviceInventorySystemPackage> systemPackages = deviceSystemPackages.getSystemPackages();
                stepData.put(INVENTORY_SYSTEM_PACKAGES, systemPackages);
            }
        }
    }

    @Then("Inventory System Packages are received")
    public void inventorySystemPackagesReceived() {
        List<DeviceInventorySystemPackage> inventorySystemPackages = (List<DeviceInventorySystemPackage>) stepData.get(INVENTORY_SYSTEM_PACKAGES);
        Assert.assertNotNull(inventorySystemPackages);
    }

    @Then("Inventory System Packages are {long}")
    public void inventorySystemPackagesAreSize(long size) {
        List<DeviceInventorySystemPackage> inventorySystemPackages = (List<DeviceInventorySystemPackage>) stepData.get(INVENTORY_SYSTEM_PACKAGES);
        Assert.assertEquals(size, inventorySystemPackages.size());
    }

    @Then("Inventory System Packages has System Package named {string} is present")
    public void inventorySystemPackageNamedIsPresent(String inventorySystemPackageName) {
        List<DeviceInventorySystemPackage> inventorySystemPackages = (List<DeviceInventorySystemPackage>) stepData.get(INVENTORY_SYSTEM_PACKAGES);
        DeviceInventorySystemPackage inventorySystemPackage = inventorySystemPackages.stream().filter(k -> k.getName().equals(inventorySystemPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventorySystemPackage);
    }

    @Then("Inventory System Packages has System Package named {string} has version {string}")
    public void inventorySystemPackageHasVersion(String inventorySystemPackageName, String inventorySystemPackageVersion) {
        List<DeviceInventorySystemPackage> inventorySystemPackages = (List<DeviceInventorySystemPackage>) stepData.get(INVENTORY_SYSTEM_PACKAGES);
        DeviceInventorySystemPackage inventorySystemPackage = inventorySystemPackages.stream().filter(p -> p.getName().equals(inventorySystemPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventorySystemPackage);
        Assert.assertEquals(inventorySystemPackageVersion, inventorySystemPackage.getVersion());
    }

    @Then("Inventory System Packages has System Package named {string} has type {string}")
    public void inventorySystemPackageHasType(String inventorySystemPackageName, String inventorySystemPackageType) {
        List<DeviceInventorySystemPackage> inventorySystemPackages = (List<DeviceInventorySystemPackage>) stepData.get(INVENTORY_SYSTEM_PACKAGES);
        DeviceInventorySystemPackage inventorySystemPackage = inventorySystemPackages.stream().filter(p -> p.getName().equals(inventorySystemPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventorySystemPackage);
        Assert.assertEquals(inventorySystemPackageType, inventorySystemPackage.getPackageType());
    }

    // /deploymentPackages
    @When("Inventory Deployment Packages are requested")
    public void inventoryDeploymentPackagesRequested() throws Exception {
        List<KuraDevice> kuraDevices = (List<KuraDevice>) stepData.get(KURA_DEVICES);
        for (KuraDevice kuraDevice : kuraDevices) {
            Device device = deviceRegistryService.findByClientId(SYS_SCOPE_ID, kuraDevice.getClientId());
            if (device != null) {
                DeviceInventoryPackages deviceDeploymentPackages = deviceInventoryManagementService.getDeploymentPackages(device.getScopeId(), device.getId(), null);
                List<DeviceInventoryPackage> inventoryPackages = deviceDeploymentPackages.getPackages();
                stepData.put(INVENTORY_DEPLOYMENT_PACKAGES, inventoryPackages);
            }
        }
    }

    @Then("Inventory Deployment Packages are received")
    public void inventoryDeploymentPackagesReceived() {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        Assert.assertNotNull(inventoryPackages);
    }

    @Then("Inventory Deployment Packages are {long}")
    public void inventoryDeploymentPackagesAreSize(long size) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        Assert.assertEquals(size, inventoryPackages.size());
    }

    @Then("Inventory Deployment Packages has Deployment Package named {string} is present")
    public void inventoryDeploymentPackageNamedIsPresent(String inventoryDeploymentPackageName) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        DeviceInventoryPackage inventoryPackage = inventoryPackages.stream().filter(d -> d.getName().equals(inventoryDeploymentPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryPackage);
    }

    @Then("Inventory Deployment Packages has Deployment Package named {string} has version {string}")
    public void inventoryDeploymentPackageHasVersion(String inventoryDeploymentPackageName, String inventoryDeploymentPackageVersion) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        DeviceInventoryPackage inventoryPackage = inventoryPackages.stream().filter(d -> d.getName().equals(inventoryDeploymentPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryPackage);
        Assert.assertEquals(inventoryDeploymentPackageVersion, inventoryPackage.getVersion());
    }

    @Then("Inventory Deployment Packages has Deployment Package named {string} has {int} bundles")
    public void inventoryDeploymentPackageHasBundles(String inventoryDeploymentPackageName, int inventoryDeploymentPackageBundlesSize) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        DeviceInventoryPackage inventoryPackage = inventoryPackages.stream().filter(d -> d.getName().equals(inventoryDeploymentPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryPackage);
        Assert.assertNotNull(inventoryPackage.getPackageBundles());
        Assert.assertEquals(inventoryDeploymentPackageBundlesSize, inventoryPackage.getPackageBundles().size());
    }

    @Then("Inventory Deployment Packages has Deployment Package named {string} has bundle named {string}")
    public void inventoryDeploymentPackageHasBundleNamed(String inventoryDeploymentPackageName, String inventoryDeploymentPackageBundleName) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        DeviceInventoryPackage inventoryPackage = inventoryPackages.stream().filter(d -> d.getName().equals(inventoryDeploymentPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryPackage);
        Assert.assertNotNull(inventoryPackage.getPackageBundles());

        DeviceInventoryBundle inventoryBundle = inventoryPackage.getPackageBundles().stream().filter(b -> b.getName().equals(inventoryDeploymentPackageBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
    }

    @Then("Inventory Deployment Packages has Deployment Package named {string} has bundle named {string} with id {string}")
    public void inventoryDeploymentPackageHasBundleNamedWithId(String inventoryDeploymentPackageName, String inventoryDeploymentPackageBundleName, String inventoryDeploymentPackageBundleId) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        DeviceInventoryPackage inventoryPackage = inventoryPackages.stream().filter(d -> d.getName().equals(inventoryDeploymentPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryPackage);
        Assert.assertNotNull(inventoryPackage.getPackageBundles());

        DeviceInventoryBundle inventoryBundle = inventoryPackage.getPackageBundles().stream().filter(b -> b.getName().equals(inventoryDeploymentPackageBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
        Assert.assertEquals(inventoryDeploymentPackageBundleId, inventoryBundle.getId());
    }

    @Then("Inventory Deployment Packages has Deployment Package named {string} has bundle named {string} with version {string}")
    public void inventoryDeploymentPackageHasBundleNamedWithVersion(String inventoryDeploymentPackageName, String inventoryDeploymentPackageBundleName, String inventoryDeploymentPackageBundleVersion) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        DeviceInventoryPackage inventoryPackage = inventoryPackages.stream().filter(d -> d.getName().equals(inventoryDeploymentPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryPackage);
        Assert.assertNotNull(inventoryPackage.getPackageBundles());

        DeviceInventoryBundle inventoryBundle = inventoryPackage.getPackageBundles().stream().filter(b -> b.getName().equals(inventoryDeploymentPackageBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
        Assert.assertEquals(inventoryDeploymentPackageBundleVersion, inventoryBundle.getVersion());
    }

    @Then("Inventory Deployment Packages has Deployment Package named {string} has bundle named {string} with state {string}")
    public void inventoryDeploymentPackageHasBundleNamedWithState(String inventoryDeploymentPackageName, String inventoryDeploymentPackageBundleName, String inventoryDeploymentPackageBundleState) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        DeviceInventoryPackage inventoryPackage = inventoryPackages.stream().filter(d -> d.getName().equals(inventoryDeploymentPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryPackage);
        Assert.assertNotNull(inventoryPackage.getPackageBundles());

        DeviceInventoryBundle inventoryBundle = inventoryPackage.getPackageBundles().stream().filter(b -> b.getName().equals(inventoryDeploymentPackageBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
        Assert.assertEquals(inventoryDeploymentPackageBundleState, inventoryBundle.getStatus());
    }

    @Then("Inventory Deployment Packages has Deployment Package named {string} has bundle named {string} with signed {string}")
    public void inventoryDeploymentPackageHasBundleNamedWithSignature(String inventoryDeploymentPackageName, String inventoryDeploymentPackageBundleName, String inventoryDeploymentPackageBundleSigned) {
        List<DeviceInventoryPackage> inventoryPackages = (List<DeviceInventoryPackage>) stepData.get(INVENTORY_DEPLOYMENT_PACKAGES);
        DeviceInventoryPackage inventoryPackage = inventoryPackages.stream().filter(d -> d.getName().equals(inventoryDeploymentPackageName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryPackage);
        Assert.assertNotNull(inventoryPackage.getPackageBundles());

        DeviceInventoryBundle inventoryBundle = inventoryPackage.getPackageBundles().stream().filter(b -> b.getName().equals(inventoryDeploymentPackageBundleName)).findAny().orElse(null);
        Assert.assertNotNull(inventoryBundle);
        Assert.assertEquals(Boolean.valueOf(inventoryDeploymentPackageBundleSigned), inventoryBundle.getSigned());
    }
}
