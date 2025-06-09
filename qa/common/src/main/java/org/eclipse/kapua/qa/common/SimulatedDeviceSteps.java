/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.qa.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.kura.simulator.GatewayConfiguration;
import org.eclipse.kapua.kura.simulator.MqttAsyncTransport;
import org.eclipse.kapua.kura.simulator.Simulator;
import org.eclipse.kapua.kura.simulator.app.Application;
import org.eclipse.kapua.kura.simulator.app.annotated.AnnotatedApplication;
import org.eclipse.kapua.kura.simulator.app.command.SimpleCommandApplication;
import org.eclipse.kapua.kura.simulator.app.deploy.SimpleDeployApplication;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfo;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfos;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOperation;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadStatus;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Singleton
public class SimulatedDeviceSteps {

    private final Logger logger = LoggerFactory.getLogger(SimulatedDeviceSteps.class);

    private final long defaultRequestTimeout = Duration.ofSeconds(Integer.getInteger("org.eclipse.kapua.service.device.registry.steps.timeoutBundleOperation", 5)).toMillis();
    private final Duration defaultPeriod = Duration.ofSeconds(1);
    private final Map<String, List<AutoCloseable>> closables = new HashMap<>();
    private List<DeviceBundle> bundles;
    private List<DevicePackage> packages;
    private ScheduledExecutorService downloadExecutor;
    private final SimulatedDevice currentDevice;
    private String nodeUri;
    private final Session session;

    @Inject
    public SimulatedDeviceSteps(SimulatedDevice currentDevice, Session session) {
        this.currentDevice = currentDevice;
        this.session = session;
    }

    @Before
    public void beforeScenario(final Scenario scenario) throws Exception {
        downloadExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("DownloadSimulator"));
    }

    @After
    public void cleanup() {
        try {
            Suppressed.closeAll(closables.values().stream().flatMap(Collection::stream));
        } catch (final Exception e) {
            //suppress stack trace
            logger.warn("Error during closing of resources. This may probably be Paho complaining about already closed connections: Error: {}", e.getMessage());
        }
        closables.clear();

        downloadExecutor.shutdown();
    }

    @Given("The account name is {string} and the client ID is {string}")
    public void setClientId(String accountName, String clientId) {
        currentDevice.setAccountName(accountName);
        currentDevice.setClientId(clientId);
    }

    @Given("The broker URI is {string}")
    public void setNodeUri(String nodeUri) {
        this.nodeUri = nodeUri;
    }

    @Given("My credentials are username {string} and password {string}")
    public void setUsernamePasswordCredentials(String username, String password) {
        session.setCredentials(new UsernamePasswordCredentials(username, password));
    }

    @When("I start the simulator")
    public void startSimulator() throws Exception {
        currentDevice.started();

        final GatewayConfiguration configuration = new GatewayConfiguration(nodeUri, currentDevice.getAccountName(), currentDevice.getClientId());

        final Set<Application> apps = new HashSet<>();
        apps.add(new SimpleCommandApplication(s -> String.format("Command '%s' not found", s)));
        apps.add(AnnotatedApplication.build(new SimpleDeployApplication(downloadExecutor)));
        apps.addAll(currentDevice.getMockApplications().values());

        try (Starting starting = new Starting()) {
            final MqttAsyncTransport transport = new MqttAsyncTransport(configuration);
            starting.add(transport);
            final Simulator simulator = new Simulator(configuration, transport, apps);
            starting.add(simulator);

            closables.put("simulator/" + currentDevice.getClientId(), starting.started());
        }

    }

    @When("I stop the simulator")
    public void stopSimulator() {
        Suppressed.closeAll(closables.remove("simulator/" + currentDevice.getClientId()));
        currentDevice.stopped();
    }

    @Then("Device {string} for account {string} is registered after {int} seconds")
    public void deviceIsRegistered(final String clientId, final String accountName, final int timeout) throws Exception {
        Wait.assertFor("Wait for connection state to become " + DeviceConnectionStatus.CONNECTED, Duration.ofSeconds(timeout), defaultPeriod, () -> {
            session.withLogin(() -> {
                assertConnectionStatus(clientId, accountName, DeviceConnectionStatus.CONNECTED);
            });
        });
    }

    @Then("Device {string} for account {string} is not registered after {int} seconds")
    public void deviceIsNotRegistered(final String clientId, final String accountName, final int timeout) throws Exception {
        Wait.assertFor("Wait for connection state to become " + DeviceConnectionStatus.DISCONNECTED, Duration.ofSeconds(timeout), defaultPeriod, () -> {
            session.withLogin(() -> {
                assertConnectionStatus(clientId, accountName, DeviceConnectionStatus.DISCONNECTED);
            });
        });
    }

    @Then("I expect the device to report the applications")
    public void checkApplications(final List<String> applications) throws Exception {
        session.withLogin(() -> {
            With.withUserAccount(currentDevice.getAccountName(), account -> {
                With.withDevice(account, currentDevice.getClientId(), device -> {
                    final Set<String> apps = new HashSet<>(Arrays.asList(device.getApplicationIdentifiers().split(",")));
                    Assert.assertEquals(new HashSet<>(applications), apps);
                });
            });
        });
    }

    @Then("The device should report simulator device information")
    public void checkDeviceInformation() throws Exception {
        final String clientId = currentDevice.getClientId();
        session.withLogin(() -> {
            With.withUserAccount(currentDevice.getAccountName(), account -> {
                With.withDevice(account, clientId, device -> {

                    Assert.assertNotNull(device);

                    Assert.assertEquals("Kura Simulator (Display Name)", device.getDisplayName());
                    Assert.assertEquals("Kura Simulator (Model Name)", device.getModelName());
                    Assert.assertEquals("kura-simulator-" + clientId, device.getModelId());
                    // Assert.assertEquals("ksim-part-123456-" + clientId, device.getPartNumber());
                    Assert.assertEquals("ksim-serial-123456-" + clientId, device.getSerialNumber());
                    // Assert.assertEquals( "1", device.getAvailableProcessors () );
                    // Assert.assertEquals( "640", device.getTotalMemory());
                    Assert.assertEquals("fw.v42", device.getFirmwareVersion());
                    Assert.assertEquals("bios.v42", device.getBiosVersion());
                    // Assert.assertEquals( "Kura Simulator (OS)", device.getOperatingSystem());
                    // Assert.assertEquals("ksim-os-v42", device.getOperatingSystemVersion());
                    // Assert.assertEquals("ksim-arch", device.getOperatingSystemArchitecture());
                    // Assert.assertEquals("Kura Simulator (Java)", device.getJvmName());
                    Assert.assertEquals("ksim-java-v42", device.getJvmVersion());
                    // Assert.assertEquals( "Kura Simulator (Java Profile)", device.getJvmProfile());
                    Assert.assertEquals("ksim-kura-v42", device.getApplicationFrameworkVersion());
                    // Assert.assertEquals("Kura Simulator (OSGi version)", device.getOsgiFrameworkName());
                    Assert.assertEquals("ksim-osgi-v42", device.getOsgiFrameworkVersion());
                });
            });
        });
    }

    @When("I start the bundle {string} with version {string}")
    public void startBundle(final String bundleSymbolicName, final String version) throws Exception {
        session.withLogin(() -> {
            With.withUserAccount(currentDevice.getAccountName(), account -> {
                With.withDevice(account, currentDevice.getClientId(), device -> {
                    DeviceBundle bundle = findBundle(bundleSymbolicName, version);
                    DeviceBundleManagementService service = KapuaLocator.getInstance().getService(DeviceBundleManagementService.class);
                    service.start(account.getId(), device.getId(), Long.toString(bundle.getId()), defaultRequestTimeout);
                });
            });
        });
    }

    @When("I stop the bundle {string} with version {string}")
    public void stopBundle(final String bundleSymbolicName, final String version) throws Exception {
        session.withLogin(() -> {
            With.withUserAccount(currentDevice.getAccountName(), account -> {
                With.withDevice(account, currentDevice.getClientId(), device -> {
                    DeviceBundle bundle = findBundle(bundleSymbolicName, version);
                    DeviceBundleManagementService service = KapuaLocator.getInstance().getService(DeviceBundleManagementService.class);
                    service.stop(account.getId(), device.getId(), Long.toString(bundle.getId()), defaultRequestTimeout);
                });
            });
        });
    }

    @When("I fetch the bundle states")
    public void fetchBundlesState() throws Exception {
        session.withLogin(() -> {
            With.withUserAccount(currentDevice.getAccountName(), account -> {
                With.withDevice(account, currentDevice.getClientId(), device -> {
                    DeviceBundleManagementService service = KapuaLocator.getInstance().getService(DeviceBundleManagementService.class);
                    DeviceBundles bundles = service.get(account.getId(), device.getId(), defaultRequestTimeout);

                    Assert.assertNotNull(bundles);

                    this.bundles = bundles.getBundles();
                });
            });
        });
    }

    @Then("The bundle {string} with version {string} is present and {string}")
    public void hasBundle(String bundleSymbolicName, String version, String state) throws KapuaException {
        DeviceBundle bundle = findBundle(bundleSymbolicName, version);

        Assert.assertEquals(state, bundle.getState());
    }

    private DeviceBundle findBundle(final String bundleSymbolicName, final String version) {
        List<DeviceBundle> bundles = this.bundles.stream()
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

    @When("I fetch the package states")
    public void fetchPackagesState() throws Exception {
        session.withLogin(() -> {
            With.withUserAccount(currentDevice.getAccountName(), account -> {
                With.withDevice(account, currentDevice.getClientId(), device -> {
                    final DevicePackageManagementService service = KapuaLocator.getInstance().getService(DevicePackageManagementService.class);

                    final DevicePackages packages = service.getInstalled(account.getId(), device.getId(), defaultRequestTimeout);

                    Assert.assertNotNull(packages);

                    this.packages = packages.getPackages();
                });
            });
        });
    }

    @Then("There must be no installed packages")
    public void assertNoPackages() {
        Assert.assertTrue(packages.isEmpty());
    }

    @Then("Package {string} with version {string} is installed and has {int} mock bundle(s)")
    public void assertPackage(String packageName, String version, int numberOfMockBundles) {
        final DevicePackage pkg = findPackage(packageName, version);

        // Assert.assertNotNull(pkg.getInstallDate());
        // Assert.assertTrue(Instant.now().isAfter(pkg.getInstallDate().toInstant()));

        final DevicePackageBundleInfos bundles = pkg.getBundleInfos();

        final List<DevicePackageBundleInfo> b = new ArrayList<>(bundles.getBundlesInfos());

        Assert.assertEquals(numberOfMockBundles, b.size());

        // sort

        Collections.sort(b, Comparator.comparing(DevicePackageBundleInfo::getName));

        // now test

        for (int i = 0; i < numberOfMockBundles; i++) {
            final DevicePackageBundleInfo bundle = b.get(i);
            Assert.assertEquals(String.format("%s.%s", packageName, i), bundle.getName());
        }
    }

    @When("I start to download package {string} with version {string} from {string}")
    public void downloadPackage(final String packageName, final String version, final String uri) throws Exception {
        session.withLogin(() -> {
            With.withUserAccount(currentDevice.getAccountName(), account -> {
                With.withDevice(account, currentDevice.getClientId(), device -> {
                    final DevicePackageManagementService service = KapuaLocator.getInstance().getService(DevicePackageManagementService.class);

                    final DevicePackageDownloadRequest request = new DevicePackageDownloadRequest();
                    request.setInstall(true);
                    request.setName(packageName);
                    request.setVersion(version);
                    request.setUri(Util.parseUri(uri));

                    service.downloadExec(account.getId(), device.getId(), request, defaultRequestTimeout);
                });
            });
        });
    }

    @Then("The download state changes to {string} in the next {int} second(s)")
    public void assertDownloadState(final String state, int waitSeconds) throws Exception {
        session.withLogin(() -> {
            With.withUserAccount(currentDevice.getAccountName(), account -> {
                With.withDevice(account, currentDevice.getClientId(), device -> {
                    final DevicePackageManagementService service = KapuaLocator.getInstance().getService(DevicePackageManagementService.class);
                    final DevicePackageDownloadStatus downloadState = DevicePackageDownloadStatus.valueOf(state);

                    Wait.waitFor("Download state change", Duration.ofSeconds(waitSeconds), Duration.ofMillis(500), () -> {
                        final DevicePackageDownloadOperation operation = service.downloadStatus(account.getId(), device.getId(), defaultRequestTimeout);
                        if (operation == null) {
                            return false;
                        }

                        return downloadState.equals(operation.getStatus());
                    });
                });
            });
        });
    }

    private DevicePackage findPackage(final String packageName, final String version) {
        final List<DevicePackage> packages = this.packages.stream()
                .filter(pkg -> pkg.getName().equals(packageName))
                .filter(pkg -> pkg.getVersion().equals(version))
                .collect(Collectors.toList());

        if (packages.isEmpty()) {
            Assert.fail(String.format("Package %s/%s is not present", packageName, version));
        }
        if (packages.size() > 1) {
            Assert.fail(String.format("There is more than one entry for package %s/%s", packageName, version));
        }

        return packages.get(0);
    }

    private void assertConnectionStatus(final String clientId, final String accountName, final DeviceConnectionStatus expectedState) throws Exception {
        final DeviceConnectionService service = KapuaLocator.getInstance().getService(DeviceConnectionService.class);
        final DeviceRegistryService registry = KapuaLocator.getInstance().getService(DeviceRegistryService.class);
        With.withUserAccount(accountName, account -> {

            final DeviceConnection result = service.findByClientId(account.getId(), clientId);
            final Device device = registry.findByClientId(account.getId(), clientId);

            Assert.assertNotNull(result);
            Assert.assertNotNull(device);
            Assert.assertEquals(clientId, result.getClientId());
            Assert.assertEquals(expectedState, result.getStatus());
        });
    }

}
