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
 *******************************************************************************/
package org.eclipse.kapua.app.console.module.device.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSetting;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSettingKeys;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtConfigComponent;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtConfigParameter;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtConfigParameter.GwtConfigParameterType;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.management.bundles.GwtBundle;
import org.eclipse.kapua.app.console.module.device.shared.model.management.bundles.GwtBundleInfo;
import org.eclipse.kapua.app.console.module.device.shared.model.management.command.GwtDeviceCommandInput;
import org.eclipse.kapua.app.console.module.device.shared.model.management.command.GwtDeviceCommandOutput;
import org.eclipse.kapua.app.console.module.device.shared.model.management.configurations.GwtDeviceConfigurationStoreSettings;
import org.eclipse.kapua.app.console.module.device.shared.model.management.configurations.GwtSnapshot;
import org.eclipse.kapua.app.console.module.device.shared.model.management.packages.GwtDeploymentPackage;
import org.eclipse.kapua.app.console.module.device.shared.model.management.packages.GwtPackageDownloadOperation;
import org.eclipse.kapua.app.console.module.device.shared.model.management.packages.GwtPackageInstallRequest;
import org.eclipse.kapua.app.console.module.device.shared.model.management.packages.GwtPackageOperation;
import org.eclipse.kapua.app.console.module.device.shared.model.management.packages.GwtPackageUninstallRequest;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceManagementService;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTicon;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.model.config.metatype.Password;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.management.command.DeviceCommandFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;
import org.eclipse.kapua.service.device.management.configuration.store.DeviceConfigurationStoreFactory;
import org.eclipse.kapua.service.device.management.configuration.store.DeviceConfigurationStoreService;
import org.eclipse.kapua.service.device.management.configuration.store.settings.DeviceConfigurationStoreEnablementPolicy;
import org.eclipse.kapua.service.device.management.configuration.store.settings.DeviceConfigurationStoreSettings;
import org.eclipse.kapua.service.device.management.packages.DevicePackageFactory;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfo;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfos;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.FileType;
import org.eclipse.kapua.service.device.management.packages.model.download.AdvancedPackageDownloadOptions;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOperation;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadStatus;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallRequest;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshot;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshotManagementService;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshots;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;

/**
 * The server side implementation of the Device RPC service.
 */
public class GwtDeviceManagementServiceImpl extends KapuaRemoteServiceServlet implements GwtDeviceManagementService {

    private static final long serialVersionUID = -1391026997499175151L;

    private static final Logger LOG = LoggerFactory.getLogger(GwtDeviceManagementServiceImpl.class);

    private static final String PASSWORD_PLACEHOLDER = "You won't know this secret! :P";

    private static final ConsoleSetting CONSOLE_SETTING = ConsoleSetting.getInstance();

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final DeviceBundleManagementService BUNDLE_MANAGEMENT_SERVICE = LOCATOR.getService(DeviceBundleManagementService.class);

    private static final DeviceCommandManagementService COMMAND_MANAGEMENT_SERVICE = LOCATOR.getService(DeviceCommandManagementService.class);
    private static final DeviceCommandFactory DEVICE_COMMAND_FACTORY = LOCATOR.getFactory(DeviceCommandFactory.class);

    private static final DeviceConfigurationManagementService CONFIGURATION_MANAGEMENT_SERVICE = LOCATOR.getService(DeviceConfigurationManagementService.class);
    private static final DeviceConfigurationFactory DEVICE_CONFIGURATION_FACTORY = LOCATOR.getFactory(DeviceConfigurationFactory.class);

    private static final DeviceConfigurationStoreService DEVICE_CONFIGURATION_STORE_SERVICE = LOCATOR.getService(DeviceConfigurationStoreService.class);

    private static final DeviceConfigurationStoreFactory DEVICE_CONFIGURATION_STORE_FACTORY = LOCATOR.getFactory(DeviceConfigurationStoreFactory.class);

    private static final DevicePackageManagementService PACKAGE_MANAGEMENT_SERVICE = LOCATOR.getService(DevicePackageManagementService.class);
    private static final DevicePackageFactory DEVICE_PACKAGE_FACTORY = LOCATOR.getFactory(DevicePackageFactory.class);

    private static final DeviceSnapshotManagementService SNAPSHOT_MANAGEMENT_SERVICE = LOCATOR.getService(DeviceSnapshotManagementService.class);

    // Packages
    @Override
    public List<GwtDeploymentPackage> findDevicePackages(String scopeShortId, String deviceShortId)
            throws GwtKapuaException {
        List<GwtDeploymentPackage> gwtPkgs = new ArrayList<GwtDeploymentPackage>();
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceShortId);
            DevicePackages deploymentPackages = PACKAGE_MANAGEMENT_SERVICE.getInstalled(scopeId, deviceId, null);

            for (DevicePackage deploymentPackage : deploymentPackages.getPackages()) {
                GwtDeploymentPackage gwtPkg = new GwtDeploymentPackage();
                gwtPkg.setName(deploymentPackage.getName());
                gwtPkg.setVersion(deploymentPackage.getVersion());

                DevicePackageBundleInfos devicePackageBundleInfos = deploymentPackage.getBundleInfos();

                if (devicePackageBundleInfos != null) {
                    List<GwtBundleInfo> gwtBundleInfos = new ArrayList<GwtBundleInfo>();
                    for (DevicePackageBundleInfo bundleInfo : devicePackageBundleInfos.getBundlesInfos()) {

                        GwtBundleInfo gwtBundleInfo = new GwtBundleInfo();
                        gwtBundleInfo.setName(bundleInfo.getName());
                        gwtBundleInfo.setVersion(bundleInfo.getVersion());

                        gwtBundleInfos.add(gwtBundleInfo);
                    }
                    gwtPkg.setBundleInfos(gwtBundleInfos);
                }
                gwtPkgs.add(gwtPkg);
            }

        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
        return gwtPkgs;
    }

    @Override
    public void installPackage(GwtXSRFToken xsrfToken, GwtPackageInstallRequest gwtPackageInstallRequest) throws GwtKapuaException {
        // Check token
        checkXSRFToken(xsrfToken);
        // Do install
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtPackageInstallRequest.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(gwtPackageInstallRequest.getDeviceId());

            URI packageUri;
            try {
                packageUri = new URI(gwtPackageInstallRequest.getPackageURI());
            } catch (URISyntaxException e) {
                throw new GwtKapuaException(GwtKapuaErrorCode.PACKAGE_URI_SYNTAX_ERROR, e, e.getLocalizedMessage());
            }

            DevicePackageDownloadRequest packageDownloadRequest = DEVICE_PACKAGE_FACTORY.newPackageDownloadRequest();
            packageDownloadRequest.setUri(packageUri);
            packageDownloadRequest.setName(gwtPackageInstallRequest.getPackageName());
            packageDownloadRequest.setVersion(gwtPackageInstallRequest.getPackageVersion());
            packageDownloadRequest.setUsername(gwtPackageInstallRequest.getUsername());
            packageDownloadRequest.setPassword(gwtPackageInstallRequest.getPassword());
            packageDownloadRequest.setFileHash(gwtPackageInstallRequest.getFileHash());
            packageDownloadRequest.setFileType(FileType.valueOf(gwtPackageInstallRequest.getFileType()));
            packageDownloadRequest.setInstall(true); // Always install
            packageDownloadRequest.setReboot(gwtPackageInstallRequest.isReboot());
            packageDownloadRequest.setRebootDelay(gwtPackageInstallRequest.getRebootDelay());

            AdvancedPackageDownloadOptions advancedOptions = packageDownloadRequest.getAdvancedOptions();
            advancedOptions.setRestart(gwtPackageInstallRequest.getRestart());
            advancedOptions.setBlockSize(gwtPackageInstallRequest.getBlockSize());
            advancedOptions.setBlockDelay(gwtPackageInstallRequest.getBlockDelay());
            advancedOptions.setBlockTimeout(gwtPackageInstallRequest.getBlockTimeout());
            advancedOptions.setNotifyBlockSize(gwtPackageInstallRequest.getNotifyBlockSize());
            advancedOptions.setInstallVerifierURI(gwtPackageInstallRequest.getInstallVerifierURI());

            PACKAGE_MANAGEMENT_SERVICE.downloadExec(scopeId, deviceId, packageDownloadRequest, DEVICE_PACKAGE_FACTORY.newPackageDownloadOptions());
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public ListLoadResult<GwtPackageOperation> getDownloadOperations(String scopeShortId, String deviceShortId)
            throws GwtKapuaException {
        List<GwtPackageOperation> gwtDeviceOperations = new ArrayList<GwtPackageOperation>();
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceShortId);
            DevicePackageDownloadOperation downloadOperation = PACKAGE_MANAGEMENT_SERVICE.downloadStatus(scopeId, deviceId, null);

            if (!DevicePackageDownloadStatus.NONE.equals(downloadOperation.getStatus())) {
                GwtPackageDownloadOperation gwtDownloadOperation = new GwtPackageDownloadOperation();

                gwtDownloadOperation.setId(downloadOperation.getId().toCompactId());
                gwtDownloadOperation.setStatus(downloadOperation.getStatus().name());
                gwtDownloadOperation.setSize(downloadOperation.getSize());
                gwtDownloadOperation.setProgress(downloadOperation.getProgress());

                gwtDeviceOperations.add(gwtDownloadOperation);
            }

        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }

        return new BaseListLoadResult<GwtPackageOperation>(gwtDeviceOperations);
    }

    @Override
    public void uninstallPackage(GwtXSRFToken xsrfToken, GwtPackageUninstallRequest gwtPackageUninstallRequest) throws GwtKapuaException {
        // Check token
        checkXSRFToken(xsrfToken);
        // Do uninstall
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtPackageUninstallRequest.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(gwtPackageUninstallRequest.getDeviceId());

            DevicePackageUninstallRequest packageUninstallRequest = DEVICE_PACKAGE_FACTORY.newPackageUninstallRequest();
            packageUninstallRequest.setName(gwtPackageUninstallRequest.getPackageName());
            packageUninstallRequest.setVersion(gwtPackageUninstallRequest.getPackageVersion());
            packageUninstallRequest.setReboot(gwtPackageUninstallRequest.isReboot());
            packageUninstallRequest.setRebootDelay(gwtPackageUninstallRequest.getRebootDelay());

            PACKAGE_MANAGEMENT_SERVICE.uninstallExec(scopeId, deviceId, packageUninstallRequest, DEVICE_PACKAGE_FACTORY.newPackageUninstallOptions());
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }
    // Configurations

    @Override
    public List<GwtConfigComponent> findDeviceConfigurations(GwtDevice device)
            throws GwtKapuaException {
        List<GwtConfigComponent> gwtConfigs = new ArrayList<GwtConfigComponent>();
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(device.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(device.getId());
            // Get the configuration
            DeviceConfiguration deviceConfigurations = CONFIGURATION_MANAGEMENT_SERVICE.get(scopeId, deviceId, null, null, null);

            if (deviceConfigurations != null) {

                // sort the list alphabetically by service name
                List<DeviceComponentConfiguration> configs = deviceConfigurations.getComponentConfigurations();
                Collections.sort(configs, new Comparator<DeviceComponentConfiguration>() {

                    @Override
                    public int compare(DeviceComponentConfiguration arg0, DeviceComponentConfiguration arg1) {
                        String name0 = arg0.getId();
                        String name1 = arg1.getId();

                        if (name0.contains(".")) {
                            name0 = name0.substring(name0.lastIndexOf('.'));
                        }
                        if (name1.contains(".")) {
                            name1 = name1.substring(name1.lastIndexOf('.'));
                        }

                        return name0.compareTo(name1);
                    }
                });
                // Prepare results
                List<String> serviceIgnore = CONSOLE_SETTING.getList(String.class, ConsoleSettingKeys.DEVICE_CONFIGURATION_SERVICE_IGNORE);

                for (DeviceComponentConfiguration config : deviceConfigurations.getComponentConfigurations()) {

                    // ignore items we want to hide
                    if (serviceIgnore != null && serviceIgnore.contains(config.getId())) {
                        continue;
                    }

                    KapuaTocd ocd = config.getDefinition();
                    if (ocd != null) {
                        GwtConfigComponent gwtConfig = new GwtConfigComponent();
                        gwtConfig.setId(config.getId());
                        if (config.getProperties() != null && config.getProperties().get("service.factoryPid") != null) {
                            String componentName = config.getId().substring(config.getId().lastIndexOf('.') + 1);
                            gwtConfig.setName(componentName);
                        } else if (config.getId().indexOf('.') == -1) {
                            gwtConfig.setName(config.getId());
                        } else {
                            gwtConfig.setName(ocd.getName());
                        }
                        gwtConfig.setDescription(ocd.getDescription());
                        if (ocd.getIcon() != null && !ocd.getIcon().isEmpty()) {
                            KapuaTicon icon = ocd.getIcon().get(0);

                            checkIconResource(icon);

                            gwtConfig.setComponentIcon(icon.getResource());
                        }

                        List<GwtConfigParameter> gwtParams = new ArrayList<GwtConfigParameter>();
                        gwtConfig.setParameters(gwtParams);
                        for (KapuaTad ad : ocd.getAD()) {
                            if (ad != null) {
                                GwtConfigParameter gwtParam = new GwtConfigParameter();
                                gwtParam.setId(ad.getId());
                                gwtParam.setName(ad.getName());
                                gwtParam.setDescription(ad.getDescription());
                                gwtParam.setType(GwtConfigParameterType.fromString(ad.getType().value()));
                                gwtParam.setRequired(ad.isRequired());
                                gwtParam.setCardinality(ad.getCardinality());
                                if (ad.getOption() != null && !ad.getOption().isEmpty()) {
                                    Map<String, String> options = new HashMap<String, String>();
                                    for (KapuaToption option : ad.getOption()) {
                                        options.put(option.getLabel(), option.getValue());
                                    }
                                    gwtParam.setOptions(options);
                                }
                                gwtParam.setMin(ad.getMin());
                                gwtParam.setMax(ad.getMax());
                                Map<String, String> gwtEntries = new HashMap<String, String>();
                                for (Entry<QName, String> entry : ad.getOtherAttributes().entrySet()) {
                                    gwtEntries.put(entry.getKey().toString(), entry.getValue());
                                }
                                gwtParam.setOtherAttributes(gwtEntries);

                                if (config.getProperties() != null) {

                                    // handle the value based on the cardinality of the attribute
                                    int cardinality = ad.getCardinality();
                                    Object value = config.getProperties().get(ad.getId());
                                    if (value != null) {

                                        if (cardinality == 0 || cardinality == 1 || cardinality == -1) {
                                            gwtParam.setValue(GwtConfigParameterType.PASSWORD.equals(gwtParam.getType()) ? PASSWORD_PLACEHOLDER : value.toString());
                                        } else {
                                            // this could be an array value
                                            if (value instanceof Object[]) {
                                                Object[] objValues = (Object[]) value;
                                                List<String> strValues = new ArrayList<String>();
                                                for (Object v : objValues) {
                                                    if (v != null) {
                                                        strValues.add(v.toString());
                                                    }
                                                }
                                                gwtParam.setValues(strValues.toArray(new String[] {}));
                                            }
                                        }
                                    }
                                    gwtParams.add(gwtParam);
                                }
                            }
                        }
                        gwtConfigs.add(gwtConfig);
                    }
                }
            }
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
        return gwtConfigs;
    }

    @Override
    public void updateComponentConfiguration(GwtXSRFToken xsrfToken, GwtDevice gwtDevice, GwtConfigComponent gwtCompConfig)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);
        // Set name and properties
        DeviceComponentConfiguration compConfig = DEVICE_CONFIGURATION_FACTORY.newComponentConfigurationInstance(gwtCompConfig.getUnescapedComponentId());
        compConfig.setName(gwtCompConfig.getUnescapedComponentName());

        Map<String, Object> compProps = new HashMap<String, Object>();
        for (GwtConfigParameter gwtConfigParam : gwtCompConfig.getParameters()) {

            Object objValue;
            int cardinality = gwtConfigParam.getCardinality();
            if (cardinality == 0 || cardinality == 1 || cardinality == -1) {

                String strValue = gwtConfigParam.getValue();

                if (GwtConfigParameterType.PASSWORD.equals(gwtConfigParam.getType()) && PASSWORD_PLACEHOLDER.equals(strValue)) {
                    continue;
                }

                objValue = getObjectValue(gwtConfigParam, strValue);
            } else {

                String[] strValues = gwtConfigParam.getValues();
                objValue = getObjectValue(gwtConfigParam, strValues);
            }

            compProps.put(gwtConfigParam.getId(), objValue);
        }
        compConfig.setProperties(compProps);

        // execute the update
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(gwtDevice.getId());

            CONFIGURATION_MANAGEMENT_SERVICE.put(scopeId, deviceId, compConfig, null);
            // Add an additional delay after the configuration update
            // to give the time to the device to apply the received
            // configuration
            Thread.sleep(1000);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }
    // Configuration Store Settings
    // 

    @Override
    public boolean isStoreServiceEnabled(String scopeIdString, String deviceIdString) throws GwtKapuaException {
        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeIdString);
            KapuaId deviceId = GwtKapuaCommonsModelConverter.convertKapuaId(deviceIdString);

            return DEVICE_CONFIGURATION_STORE_SERVICE.isServiceEnabled(scopeId) && DEVICE_CONFIGURATION_STORE_SERVICE.isApplicationEnabled(scopeId, deviceId);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public GwtDeviceConfigurationStoreSettings getApplicationSettings(String scopeIdString, String deviceIdString) throws GwtKapuaException {
        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeIdString);
            KapuaId deviceId = GwtKapuaCommonsModelConverter.convertKapuaId(deviceIdString);
            DeviceConfigurationStoreSettings deviceConfigurationStoreSettings = null;
            GwtDeviceConfigurationStoreSettings gwtDeviceConfigurationStoreSettings = null;

            deviceConfigurationStoreSettings = DEVICE_CONFIGURATION_STORE_SERVICE.getApplicationSettings(scopeId, deviceId);
            gwtDeviceConfigurationStoreSettings = new GwtDeviceConfigurationStoreSettings();
            gwtDeviceConfigurationStoreSettings.setStoreEnablementPolicy(
                    GwtDeviceConfigurationStoreSettings.GwtDeviceConfigurationStoreEnablementPolicy.valueOf(deviceConfigurationStoreSettings.getEnablementPolicy().name()));
            return gwtDeviceConfigurationStoreSettings;
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public void setApplicationSettings(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, GwtDeviceConfigurationStoreSettings gwtDeviceConfigurationStoreSettings)
            throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeIdString);
            KapuaId deviceId = GwtKapuaCommonsModelConverter.convertKapuaId(deviceIdString);

            DeviceConfigurationStoreSettings deviceConfigurationStoreSettings = DEVICE_CONFIGURATION_STORE_FACTORY.newDeviceConfigurationStoreSettings();
            if (deviceConfigurationStoreSettings != null) {
                deviceConfigurationStoreSettings.setScopeId(scopeId);
                deviceConfigurationStoreSettings.setDeviceId(deviceId);
                deviceConfigurationStoreSettings.setEnablementPolicy(DeviceConfigurationStoreEnablementPolicy.valueOf(gwtDeviceConfigurationStoreSettings.getStoreEnablementPolicy()));
            }
            DEVICE_CONFIGURATION_STORE_SERVICE.setApplicationSettings(scopeId, deviceId, deviceConfigurationStoreSettings);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    // Snapshots
    @Override
    public ListLoadResult<GwtSnapshot> findDeviceSnapshots(GwtDevice gwtDevice)
            throws GwtKapuaException {
        List<GwtSnapshot> snapshots = new ArrayList<GwtSnapshot>();
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(gwtDevice.getId());
            // Execute the command
            DeviceSnapshots snapshotIds = SNAPSHOT_MANAGEMENT_SERVICE.get(scopeId, deviceId, null);

            // Sort them by most recent first
            Collections.sort(snapshotIds.getSnapshots(), new Comparator<DeviceSnapshot>() {

                @Override
                public int compare(DeviceSnapshot snapshotId0, DeviceSnapshot snapshotId1) {
                    return -1 * snapshotId0.getTimestamp().compareTo(snapshotId1.getTimestamp()); // Descending order
                }
            });

            for (DeviceSnapshot snapshot : snapshotIds.getSnapshots()) {
                Long timestamp = snapshot.getTimestamp();
                GwtSnapshot gwtSnapshot = new GwtSnapshot();
                gwtSnapshot.setCreatedOn(new Date(timestamp));
                snapshots.add(gwtSnapshot);
            }

        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }

        return new BaseListLoadResult<GwtSnapshot>(snapshots);
    }

    @Override
    public void rollbackDeviceSnapshot(GwtXSRFToken xsrfToken, GwtDevice gwtDevice, GwtSnapshot snapshot)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(gwtDevice.getId());

            SNAPSHOT_MANAGEMENT_SERVICE.rollback(scopeId, deviceId, String.valueOf(snapshot.getSnapshotId()), null);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    // Bundles
    @Override
    public ListLoadResult<GwtBundle> findBundles(GwtDevice device)
            throws GwtKapuaException {
        List<GwtBundle> pairs = new ArrayList<GwtBundle>();

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(device.getScopeId());
            KapuaId id = KapuaEid.parseCompactId(device.getId());
            // Get the bundles
            DeviceBundles bundles = BUNDLE_MANAGEMENT_SERVICE.get(scopeId, id, null);

            for (DeviceBundle bundle : bundles.getBundles()) {
                GwtBundle pair = new GwtBundle();
                pair.setId(String.valueOf(bundle.getId()));
                pair.setName(bundle.getName());
                pair.setStatus(toStateString(bundle));
                pair.setVersion(bundle.getVersion());

                pairs.add(pair);
            }
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }

        return new BaseListLoadResult<GwtBundle>(pairs);
    }

    @Override
    public void startBundle(GwtXSRFToken xsrfToken, GwtDevice device, GwtBundle gwtBundle)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(device.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(device.getId());

            BUNDLE_MANAGEMENT_SERVICE.start(scopeId, deviceId, String.valueOf(gwtBundle.getId()), null);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public void stopBundle(GwtXSRFToken xsrfToken, GwtDevice device, GwtBundle gwtBundle)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(device.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(device.getId());

            BUNDLE_MANAGEMENT_SERVICE.stop(scopeId, deviceId, String.valueOf(gwtBundle.getId()), null);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    // Command
    @Override
    public GwtDeviceCommandOutput executeCommand(GwtXSRFToken xsrfToken, GwtDevice gwtDevice, GwtDeviceCommandInput gwtCommandInput)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        GwtDeviceCommandOutput gwtCommandOutput = new GwtDeviceCommandOutput();
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseCompactId(gwtDevice.getId());
            // Execute the command
            StringTokenizer st = new StringTokenizer(gwtCommandInput.getCommand());
            int count = st.countTokens();

            String command = count > 0 ? st.nextToken() : null;
            String[] args = count > 1 ? new String[count - 1] : null;
            int i = 0;
            while (st.hasMoreTokens()) {
                args[i++] = st.nextToken();
            }

            DeviceCommandInput commandInput = DEVICE_COMMAND_FACTORY.newCommandInput();
            commandInput.setArguments(args);
            commandInput.setCommand(command);
            commandInput.setEnvironment(gwtCommandInput.getEnvironment());
            commandInput.setRunAsynch(gwtCommandInput.isRunAsynch() != null && gwtCommandInput.isRunAsynch());
            commandInput.setStdin(gwtCommandInput.getStdin());
            commandInput.setTimeout(gwtCommandInput.getTimeout() != null ? gwtCommandInput.getTimeout() : 0);
            commandInput.setWorkingDir(gwtCommandInput.getWorkingDir());
            commandInput.setBody(gwtCommandInput.getZipBytes());

            DeviceCommandOutput commandOutput = COMMAND_MANAGEMENT_SERVICE.exec(scopeId, deviceId, commandInput, null);

            if (commandOutput.getExceptionMessage() != null) {
                gwtCommandOutput.setExceptionMessage(commandOutput.getExceptionMessage().replace("\n", "<br>"));
            }
            if (commandOutput.getExceptionStack() != null) {
                gwtCommandOutput.setExceptionStack(commandOutput.getExceptionStack().replace("\n", "<br>"));
            }
            gwtCommandOutput.setExitCode(commandOutput.getExitCode());
            if (commandOutput.getStderr() != null) {
                gwtCommandOutput.setStderr(commandOutput.getStderr().replace("\n", "<br>"));
            }
            gwtCommandOutput.setStdout(commandOutput.getStdout());
            gwtCommandOutput.setTimedout(commandOutput.getHasTimedout());
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }

        return gwtCommandOutput;
    }

    // Private methods
    private String toStateString(DeviceBundle bundle) {
        String state = bundle.getState();
        if (state.equals("INSTALLED")) {
            return "bndInstalled";
        } else if (state.equals("RESOLVED")) {
            return "bndResolved";
        } else if (state.equals("STARTING")) {
            return "bndStarting";
        } else if (state.equals("ACTIVE")) {
            return "bndActive";
        } else if (state.equals("STOPPING")) {
            return "bndStopping";
        } else if (state.equals("UNINSTALLED")) {
            return "bndUninstalled";
        } else {
            return "bndUnknown";
        }
    }

    private Object getObjectValue(GwtConfigParameter gwtConfigParam, String strValue) {
        Object objValue = null;
        if (strValue != null) {
            GwtConfigParameterType gwtType = gwtConfigParam.getType();
            switch (gwtType) {
            case LONG:
                objValue = Long.parseLong(strValue);
                break;
            case DOUBLE:
                objValue = Double.parseDouble(strValue);
                break;
            case FLOAT:
                objValue = Float.parseFloat(strValue);
                break;
            case INTEGER:
                objValue = Integer.parseInt(strValue);
                break;
            case SHORT:
                objValue = Short.parseShort(strValue);
                break;
            case BYTE:
                objValue = Byte.parseByte(strValue);
                break;
            case BOOLEAN:
                objValue = Boolean.parseBoolean(strValue);
                break;
            case PASSWORD:
                objValue = new Password(strValue);
                break;
            case CHAR:
                objValue = strValue.charAt(0);
                break;
            case STRING:
                objValue = strValue;
                break;
            }
        }
        return objValue;
    }

    private Object[] getObjectValue(GwtConfigParameter gwtConfigParam, String[] defaultValues) {
        List<Object> values = new ArrayList<Object>();
        GwtConfigParameterType type = gwtConfigParam.getType();
        switch (type) {
        case BOOLEAN:
            for (String value : defaultValues) {
                values.add(Boolean.valueOf(value));
            }
            return values.toArray();

        case BYTE:
            for (String value : defaultValues) {
                values.add(Byte.valueOf(value));
            }
            return values.toArray();

        case CHAR:
            for (String value : defaultValues) {
                values.add(value.charAt(0));
            }
            return values.toArray();

        case DOUBLE:
            for (String value : defaultValues) {
                values.add(Double.valueOf(value));
            }
            return values.toArray();

        case FLOAT:
            for (String value : defaultValues) {
                values.add(Float.valueOf(value));
            }
            return values.toArray();

        case INTEGER:
            for (String value : defaultValues) {
                values.add(Integer.valueOf(value));
            }
            return values.toArray();

        case LONG:
            for (String value : defaultValues) {
                values.add(Long.valueOf(value));
            }
            return values.toArray();

        case SHORT:
            for (String value : defaultValues) {
                values.add(Short.valueOf(value));
            }
            return values.toArray();

        case PASSWORD:
            for (String value : defaultValues) {
                values.add(new Password(value));
            }
            return values.toArray();

        case STRING:
        default:
            return defaultValues;
        }
    }

    /**
     * Checks the source of the icon. The component config icon can be one of the well known icon (i.e. MqttDataTransport icon) as well as an icon loaded from external source with an HTTP link.
     * <p>
     * We need to filter HTTP link to protect the console page and also to have content always served from EC console. Otherwise browsers can alert the user that content is served from domain
     * different from *.everyware-cloud.com and over insicure connection.
     * <p>
     * To avoid this we will download the image locally on the server temporary directory and give back the page a token URL to get the file.
     *
     * @param icon
     *         The icon from the OCD of the component configuration.
     */
    private void checkIconResource(KapuaTicon icon) {
        ConsoleSetting config = ConsoleSetting.getInstance();

        String iconResource = icon.getResource();
        // Check if the resource is an HTTP URL or not
        if (iconResource != null &&
                (iconResource.toLowerCase().startsWith("http://") ||
                        iconResource.toLowerCase().startsWith("https://"))) {
            File tmpFile = null;

            try {
                LOG.info("Got configuration component icon from URL: {}", iconResource);
                // Tmp file name creation
                String systemTmpDir = System.getProperty("java.io.tmpdir");
                String iconResourcesTmpDir = config.getString(ConsoleSettingKeys.DEVICE_CONFIGURATION_ICON_FOLDER);
                String tmpFileName = Base64.encodeBase64String(MessageDigest.getInstance("MD5").digest(iconResource.getBytes("UTF-8")));

                // Conversions needed got security reasons!
                // On the file servlet we use the regex [0-9A-Za-z]{1,} to validate the given file id.
                // This validation prevents the caller of the file servlet to try to move out of the directory where the icons are stored.
                tmpFileName = tmpFileName.replaceAll("/", "a");
                tmpFileName = tmpFileName.replaceAll("\\+", "m");
                tmpFileName = tmpFileName.replaceAll("=", "z");
                // Tmp dir check and creation
                StringBuilder tmpDirPathSb = new StringBuilder().append(systemTmpDir);
                if (!systemTmpDir.endsWith("/")) {
                    tmpDirPathSb.append("/");
                }
                tmpDirPathSb.append(iconResourcesTmpDir);

                File tmpDir = new File(tmpDirPathSb.toString());
                if (!tmpDir.exists()) {
                    LOG.info("Creating tmp dir on path: {}", tmpDir);
                    tmpDir.mkdir();
                }
                // Tmp file check and creation
                tmpDirPathSb.append("/")
                        .append(tmpFileName);
                tmpFile = new File(tmpDirPathSb.toString());

                // Check date of modification to avoid caching forever
                if (tmpFile.exists()) {
                    long lastModifiedDate = tmpFile.lastModified();

                    long maxCacheTime = config.getLong(ConsoleSettingKeys.DEVICE_CONFIGURATION_ICON_CACHE_TIME);

                    if (System.currentTimeMillis() - lastModifiedDate > maxCacheTime) {
                        LOG.info("Deleting old cached file: {}", tmpFile);
                        tmpFile.delete();
                    }
                }

                // If file is not cached, download it.
                if (!tmpFile.exists()) {
                    // Url connection
                    URL iconUrl = new URL(iconResource);
                    URLConnection urlConnection = iconUrl.openConnection();
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.setReadTimeout(2000);

                    // Length check
                    String contentLengthString = urlConnection.getHeaderField("Content-Length");

                    long maxLength = config.getLong(ConsoleSettingKeys.DEVICE_CONFIGURATION_ICON_SIZE_MAX);

                    try {
                        Long contentLength = Long.parseLong(contentLengthString);
                        if (contentLength > maxLength) {
                            LOG.warn("Content lenght exceeded ({}/{}) for URL: {}", contentLength, maxLength, iconResource);
                            throw new IOException("Content-Length reported a length of " + contentLength + " which exceeds the maximum allowed size of " + maxLength);
                        }
                    } catch (NumberFormatException nfe) {
                        LOG.warn("Cannot get Content-Length header!");
                    }

                    LOG.info("Creating file: {}", tmpFile);
                    tmpFile.createNewFile();

                    // Icon download
                    InputStream is = urlConnection.getInputStream();
                    OutputStream os = new FileOutputStream(tmpFile);
                    byte[] buffer = new byte[4096];
                    try {
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            os.write(buffer, 0, len);

                            maxLength -= len;

                            if (maxLength < 0) {
                                LOG.warn("Maximum content lenght exceeded ({}) for URL: {}", maxLength, iconResource);
                                throw new IOException("Maximum content lenght exceeded (" + maxLength + ") for URL: " + iconResource);
                            }
                        }
                    } finally {
                        os.close();
                    }

                    LOG.info("Downloaded file: {}", tmpFile);

                    // Image metadata content checks
                    ImageFormat imgFormat = Imaging.guessFormat(tmpFile);

                    if (imgFormat.equals(ImageFormats.BMP) ||
                            imgFormat.equals(ImageFormats.GIF) ||
                            imgFormat.equals(ImageFormats.JPEG) ||
                            imgFormat.equals(ImageFormats.PNG)) {
                        LOG.info("Detected image format: {}", imgFormat.getName());
                    } else if (imgFormat.equals(ImageFormats.UNKNOWN)) {
                        LOG.error("Unknown file format for URL: {}", iconResource);
                        throw new IOException("Unknown file format for URL: " + iconResource);
                    } else {
                        LOG.error("Usupported file format ({}) for URL: {}", imgFormat, iconResource);
                        throw new IOException("Unknown file format for URL: {}" + iconResource);
                    }

                    LOG.info("Image validation passed for URL: {}", iconResource);
                } else {
                    LOG.info("Using cached file: {}", tmpFile);
                }
                // Injecting new URL for the icon resource
                String newResourceURL = new StringBuilder().append("img://console/file/icons?id=")
                        .append(tmpFileName)
                        .toString();

                LOG.info("Injecting configuration component icon: {}", newResourceURL);
                icon.setResource(newResourceURL);
            } catch (Exception e) {
                if (tmpFile != null && tmpFile.exists()) {
                    tmpFile.delete();
                }

                icon.setResource("Default");

                LOG.error("Error while checking component configuration icon. Using the default plugin icon.", e);
            }
        }
        // If not, all is fine.
    }

}
