/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.packages.internal;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.IdGenerator;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.type.ObjectTypeConverter;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.management.commons.AbstractDeviceManagementTransactionalServiceImpl;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallBuilder;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.management.message.notification.NotifyStatus;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.internal.setting.PackageManagementServiceSetting;
import org.eclipse.kapua.service.device.management.packages.internal.setting.PackageManagementServiceSettingKeys;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageAppProperties;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestChannel;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestMessage;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestPayload;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageResource;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageResponseMessage;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageResponsePayload;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.download.AdvancedPackageDownloadOptions;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOperation;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOptions;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.device.management.packages.model.install.DevicePackageInstallOperation;
import org.eclipse.kapua.service.device.management.packages.model.install.DevicePackageInstallOptions;
import org.eclipse.kapua.service.device.management.packages.model.install.DevicePackageInstallRequest;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallOperation;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallOptions;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallRequest;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationCreator;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationProperty;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

/**
 * {@link DevicePackageManagementService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DevicePackageManagementServiceImpl extends AbstractDeviceManagementTransactionalServiceImpl implements DevicePackageManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(DevicePackageManagementServiceImpl.class);

    private final PackageManagementServiceSetting packageManagementServiceSetting;

    private final DeviceManagementOperationRegistryService deviceManagementOperationRegistryService;
    private final DeviceManagementOperationFactory deviceManagementOperationFactory;

    private static final String SCOPE_ID = "scopeId";
    private static final String DEVICE_ID = "deviceId";

    @Inject
    public DevicePackageManagementServiceImpl(
            TxManager txManager,
            AuthorizationService authorizationService,
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService,
            DeviceManagementOperationRegistryService deviceManagementOperationRegistryService,
            DeviceManagementOperationFactory deviceManagementOperationFactory,
            PackageManagementServiceSetting packageManagementServiceSetting) {
        super(txManager,
                authorizationService,
                deviceEventService,
                deviceEventFactory,
                deviceRegistryService);
        this.deviceManagementOperationRegistryService = deviceManagementOperationRegistryService;
        this.deviceManagementOperationFactory = deviceManagementOperationFactory;
        this.packageManagementServiceSetting = packageManagementServiceSetting;
    }
    // Installed

    @Override
    public DevicePackages getInstalled(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        PackageRequestChannel packageRequestChannel = new PackageRequestChannel();
        packageRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        packageRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        packageRequestChannel.setMethod(KapuaMethod.READ);
        packageRequestChannel.setPackageResource(null);

        PackageRequestPayload packageRequestPayload = new PackageRequestPayload();

        PackageRequestMessage packageRequestMessage = new PackageRequestMessage();
        packageRequestMessage.setScopeId(scopeId);
        packageRequestMessage.setDeviceId(deviceId);
        packageRequestMessage.setCapturedOn(new Date());
        packageRequestMessage.setPayload(packageRequestPayload);
        packageRequestMessage.setChannel(packageRequestChannel);

        // Build request
        DeviceCallBuilder<PackageRequestChannel, PackageRequestPayload, PackageRequestMessage, PackageResponseMessage> packageDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(packageRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        PackageResponseMessage responseMessage;
        try {
            responseMessage = packageDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DevicePackages {} for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, packageRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDevicePackages().orElse(new DevicePackages()));
    }
    // Download

    @Override
    public KapuaId downloadExec(KapuaId scopeId, KapuaId deviceId, DevicePackageDownloadRequest packageDownloadRequest, Long timeout) throws KapuaException {
        DevicePackageDownloadOptions packageDownloadOptions = new DevicePackageDownloadOptions();
        packageDownloadOptions.setTimeout(timeout);

        return downloadExec(scopeId, deviceId, packageDownloadRequest, packageDownloadOptions);
    }

    @Override
    public KapuaId downloadExec(KapuaId scopeId, KapuaId deviceId, DevicePackageDownloadRequest packageDownloadRequest, DevicePackageDownloadOptions packageDownloadOptions) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(packageDownloadRequest, "packageDownloadRequest");
        ArgumentValidator.notNull(packageDownloadRequest.getUri(), "packageDownloadRequest.uri");
        ArgumentValidator.notNull(packageDownloadRequest.getName(), "packageDownloadRequest.name");
        ArgumentValidator.notNull(packageDownloadRequest.getVersion(), "packageDownloadRequest.version");
        ArgumentValidator.notNull(packageDownloadOptions, "packageDownloadOptions");
        try {
            packageDownloadRequest.getUri().toURL();
        } catch (MalformedURLException | IllegalArgumentException ignored) {
            throw new KapuaIllegalArgumentException("packageDownloadRequest.uri", packageDownloadRequest.getUri().toString());
        }
        verifyOverflowPackageFields(packageDownloadRequest);

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        // Generate requestId
        KapuaId operationId = new KapuaEid(IdGenerator.generate());
        // Prepare the request
        PackageRequestChannel packageRequestChannel = new PackageRequestChannel();
        packageRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        packageRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        packageRequestChannel.setMethod(KapuaMethod.EXECUTE);
        packageRequestChannel.setPackageResource(PackageResource.DOWNLOAD);

        PackageRequestPayload packageRequestPayload = new PackageRequestPayload();

        // Basic options
        packageRequestPayload.setOperationId(operationId);
        packageRequestPayload.setPackageDownloadURI(packageDownloadRequest.getUri());
        packageRequestPayload.setPackageDownloadName(packageDownloadRequest.getName());
        packageRequestPayload.setPackageDownloadVersion(packageDownloadRequest.getVersion());
        packageRequestPayload.setPackageDownloadUsername(packageDownloadRequest.getUsername());
        packageRequestPayload.setPackageDownloadPassword(packageDownloadRequest.getPassword());
        packageRequestPayload.setPackageDownloadFileHash(packageDownloadRequest.getFileHash());
        packageRequestPayload.setPackageDownloadFileType(packageDownloadRequest.getFileType());
        packageRequestPayload.setPackageDownloadnstall(packageDownloadRequest.getInstall());
        packageRequestPayload.setReboot(packageDownloadRequest.getReboot());
        packageRequestPayload.setRebootDelay(packageDownloadRequest.getRebootDelay());

        // Advanced ones
        AdvancedPackageDownloadOptions advancedOptions = packageDownloadRequest.getAdvancedOptions();

        packageRequestPayload.setPackageDownloadRestart(advancedOptions.getRestart());
        packageRequestPayload.setPackageDownloadBlockSize(MoreObjects.firstNonNull(advancedOptions.getBlockSize(),
                packageManagementServiceSetting.getInt(PackageManagementServiceSettingKeys.PACKAGE_MANAGEMENT_SERVICE_SETTING_DOWDLOAD_DEFAULT_BLOCK_SIZE)));
        packageRequestPayload.setPackageDownloadBlockDelay(MoreObjects.firstNonNull(advancedOptions.getBlockDelay(),
                packageManagementServiceSetting.getInt(PackageManagementServiceSettingKeys.PACKAGE_MANAGEMENT_SERVICE_SETTING_DOWDLOAD_DEFAULT_BLOCK_DELAY)));
        packageRequestPayload.setPackageDownloadBlockTimeout(MoreObjects.firstNonNull(advancedOptions.getBlockTimeout(),
                packageManagementServiceSetting.getInt(PackageManagementServiceSettingKeys.PACKAGE_MANAGEMENT_SERVICE_SETTING_DOWDLOAD_DEFAULT_BLOCK_TIMEOUT)));
        packageRequestPayload.setPackageDownloadBlockSize(MoreObjects.firstNonNull(advancedOptions.getNotifyBlockSize(),
                packageManagementServiceSetting.getInt(PackageManagementServiceSettingKeys.PACKAGE_MANAGEMENT_SERVICE_SETTING_DOWDLOAD_DEFAULT_NOTIFY_BLOCK_SIZE)));
        packageRequestPayload.setPackageDownloadInstallVerifierURI(advancedOptions.getInstallVerifierURI());

        // Message
        PackageRequestMessage packageRequestMessage = new PackageRequestMessage();
        packageRequestMessage.setScopeId(scopeId);
        packageRequestMessage.setDeviceId(deviceId);
        packageRequestMessage.setCapturedOn(new Date());
        packageRequestMessage.setPayload(packageRequestPayload);
        packageRequestMessage.setChannel(packageRequestChannel);
        // Create device management operation
        KapuaId deviceManagementOperationId = createManagementOperation(scopeId, deviceId, operationId, packageRequestMessage);

        // Build request
        DeviceCallBuilder<PackageRequestChannel, PackageRequestPayload, PackageRequestMessage, PackageResponseMessage> packageDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(packageRequestMessage)
                        .withTimeoutOrDefault(packageDownloadOptions.getTimeout());

        // Do exec
        PackageResponseMessage responseMessage;
        try {
            responseMessage = packageDeviceCallBuilder.send();
        } catch (Exception e) {
            closeManagementOperation(scopeId, operationId);
            LOG.error("Error while executing DevicePackageDownloadRequest {} for Device {}. Error: {}", packageDownloadRequest.getUri(), deviceId, e.getMessage(), e);
            throw e;
        }
        // Create event
        createDeviceEvent(scopeId, deviceId, packageRequestMessage, responseMessage);
        // Check response
        try {
            checkResponseAcceptedOrThrowError(responseMessage);
        } catch (Exception e) {
            closeManagementOperation(scopeId, operationId, responseMessage);
            throw e;
        }
        // Return operation id
        return deviceManagementOperationId;
    }

    @Override
    public DevicePackageDownloadOperation downloadStatus(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        PackageRequestChannel packageRequestChannel = new PackageRequestChannel();
        packageRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        packageRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        packageRequestChannel.setMethod(KapuaMethod.READ);
        packageRequestChannel.setPackageResource(PackageResource.DOWNLOAD);

        PackageRequestPayload packageRequestPayload = new PackageRequestPayload();

        PackageRequestMessage packageRequestMessage = new PackageRequestMessage();
        packageRequestMessage.setScopeId(scopeId);
        packageRequestMessage.setDeviceId(deviceId);
        packageRequestMessage.setCapturedOn(new Date());
        packageRequestMessage.setPayload(packageRequestPayload);
        packageRequestMessage.setChannel(packageRequestChannel);

        // Build request
        DeviceCallBuilder<PackageRequestChannel, PackageRequestPayload, PackageRequestMessage, PackageResponseMessage> packageDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(packageRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        PackageResponseMessage responseMessage;
        try {
            responseMessage = packageDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DevicePackageDownloadOperation for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, packageRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage, () -> {
            PackageResponsePayload responsePayload = responseMessage.getPayload();

            DevicePackageDownloadOperation downloadOperation = new DevicePackageDownloadOperation();
            downloadOperation.setId(responsePayload.getPackageDownloadOperationId());
            downloadOperation.setStatus(responsePayload.getPackageDownloadOperationStatus());
            downloadOperation.setSize(responsePayload.getPackageDownloadOperationSize());
            downloadOperation.setProgress(responsePayload.getPackageDownloadOperationProgress());

            return downloadOperation;
        });
    }

    @Override
    public void downloadStop(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        // Prepare the request
        PackageRequestChannel packageRequestChannel = new PackageRequestChannel();
        packageRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        packageRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        packageRequestChannel.setMethod(KapuaMethod.DELETE);
        packageRequestChannel.setPackageResource(PackageResource.DOWNLOAD);

        PackageRequestPayload packageRequestPayload = new PackageRequestPayload();

        PackageRequestMessage packageRequestMessage = new PackageRequestMessage();
        packageRequestMessage.setScopeId(scopeId);
        packageRequestMessage.setDeviceId(deviceId);
        packageRequestMessage.setCapturedOn(new Date());
        packageRequestMessage.setPayload(packageRequestPayload);
        packageRequestMessage.setChannel(packageRequestChannel);

        // Build request
        DeviceCallBuilder<PackageRequestChannel, PackageRequestPayload, PackageRequestMessage, PackageResponseMessage> packageDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(packageRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do del
        PackageResponseMessage responseMessage;
        try {
            responseMessage = packageDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while stopping DevicePackageDownloadOperation for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, packageRequestMessage, responseMessage);
        // Check response
        checkResponseAcceptedOrThrowError(responseMessage);
    }
    // Install

    @Override
    public KapuaId installExec(KapuaId scopeId, KapuaId deviceId, DevicePackageInstallRequest packageInstallRequest, Long timeout) throws KapuaException {
        DevicePackageInstallOptions packageInstallOptions = new DevicePackageInstallOptions();
        packageInstallOptions.setTimeout(timeout);

        return installExec(scopeId, deviceId, packageInstallRequest, packageInstallOptions);
    }

    @Override
    public KapuaId installExec(KapuaId scopeId, KapuaId deviceId, DevicePackageInstallRequest deployInstallRequest, DevicePackageInstallOptions packageInstallOptions) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(deployInstallRequest, "deployInstallRequest");
        ArgumentValidator.notNull(packageInstallOptions, "packageInstallOptions");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        // Generate requestId
        KapuaId operationId = new KapuaEid(IdGenerator.generate());
        // Prepare the request
        PackageRequestChannel packageRequestChannel = new PackageRequestChannel();
        packageRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        packageRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        packageRequestChannel.setMethod(KapuaMethod.EXECUTE);
        packageRequestChannel.setPackageResource(PackageResource.INSTALL);

        PackageRequestPayload packageRequestPayload = new PackageRequestPayload();
        packageRequestPayload.setOperationId(operationId);
        packageRequestPayload.setPackageDownloadName(deployInstallRequest.getName());
        packageRequestPayload.setPackageDownloadVersion(deployInstallRequest.getVersion());
        packageRequestPayload.setReboot(deployInstallRequest.isReboot());
        packageRequestPayload.setRebootDelay(deployInstallRequest.getRebootDelay());

        PackageRequestMessage packageRequestMessage = new PackageRequestMessage();
        packageRequestMessage.setScopeId(scopeId);
        packageRequestMessage.setDeviceId(deviceId);
        packageRequestMessage.setCapturedOn(new Date());
        packageRequestMessage.setPayload(packageRequestPayload);
        packageRequestMessage.setChannel(packageRequestChannel);
        // Create device management operation
        KapuaId deviceManagementOperationId = createManagementOperation(scopeId, deviceId, operationId, packageRequestMessage);

        // Build request
        DeviceCallBuilder<PackageRequestChannel, PackageRequestPayload, PackageRequestMessage, PackageResponseMessage> packageDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(packageRequestMessage)
                        .withTimeoutOrDefault(packageInstallOptions.getTimeout());

        // Do get
        PackageResponseMessage responseMessage;
        try {
            responseMessage = packageDeviceCallBuilder.send();
        } catch (Exception e) {
            closeManagementOperation(scopeId, operationId);
            LOG.error("Error while executing DevicePackageInstallRequest {} for Device {}. Error: {}", deployInstallRequest.getName(), deviceId, e.getMessage(), e);
            throw e;
        }
        // Create event
        createDeviceEvent(scopeId, deviceId, packageRequestMessage, responseMessage);
        // Check response
        try {
            checkResponseAcceptedOrThrowError(responseMessage);
        } catch (Exception e) {
            closeManagementOperation(scopeId, operationId, responseMessage);
            throw e;
        }
        // Return operation id
        return deviceManagementOperationId;
    }

    @Override
    public DevicePackageInstallOperation installStatus(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        PackageRequestChannel packageRequestChannel = new PackageRequestChannel();
        packageRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        packageRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        packageRequestChannel.setMethod(KapuaMethod.READ);
        packageRequestChannel.setPackageResource(PackageResource.INSTALL);

        PackageRequestPayload packageRequestPayload = new PackageRequestPayload();

        PackageRequestMessage packageRequestMessage = new PackageRequestMessage();
        packageRequestMessage.setScopeId(scopeId);
        packageRequestMessage.setDeviceId(deviceId);
        packageRequestMessage.setCapturedOn(new Date());
        packageRequestMessage.setPayload(packageRequestPayload);
        packageRequestMessage.setChannel(packageRequestChannel);

        // Build request
        DeviceCallBuilder<PackageRequestChannel, PackageRequestPayload, PackageRequestMessage, PackageResponseMessage> packageDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(packageRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        PackageResponseMessage responseMessage;
        try {
            responseMessage = packageDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DevicePackageInstallOperation for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, packageRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDevicePackageInstallOperation().orElse(new DevicePackageInstallOperation()));
    }
    // Uninstall

    @Override
    public KapuaId uninstallExec(KapuaId scopeId, KapuaId deviceId, DevicePackageUninstallRequest packageUninstallRequest, Long timeout) throws KapuaException {
        DevicePackageUninstallOptions packageUninstallOptions = new DevicePackageUninstallOptions();
        packageUninstallOptions.setTimeout(timeout);

        return uninstallExec(scopeId, deviceId, packageUninstallRequest, packageUninstallOptions);
    }

    @Override
    public KapuaId uninstallExec(KapuaId scopeId, KapuaId deviceId, DevicePackageUninstallRequest packageUninstallRequest, DevicePackageUninstallOptions packageUninstallOptions)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        ArgumentValidator.notNull(packageUninstallRequest, "packageUninstallRequest");
        ArgumentValidator.notNull(packageUninstallOptions, "packageUninstallOptions");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.write, scopeId));
        // Generate requestId
        KapuaId operationId = new KapuaEid(IdGenerator.generate());
        // Prepare the request
        PackageRequestChannel packageRequestChannel = new PackageRequestChannel();
        packageRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        packageRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        packageRequestChannel.setMethod(KapuaMethod.EXECUTE);
        packageRequestChannel.setPackageResource(PackageResource.UNINSTALL);

        PackageRequestPayload packageRequestPayload = new PackageRequestPayload();
        packageRequestPayload.setOperationId(operationId);
        packageRequestPayload.setPackageUninstallName(packageUninstallRequest.getName());
        packageRequestPayload.setPackageUninstallVersion(packageUninstallRequest.getVersion());
        packageRequestPayload.setReboot(packageUninstallRequest.isReboot());
        packageRequestPayload.setRebootDelay(packageUninstallRequest.getRebootDelay());

        PackageRequestMessage packageRequestMessage = new PackageRequestMessage();
        packageRequestMessage.setScopeId(scopeId);
        packageRequestMessage.setDeviceId(deviceId);
        packageRequestMessage.setCapturedOn(new Date());
        packageRequestMessage.setPayload(packageRequestPayload);
        packageRequestMessage.setChannel(packageRequestChannel);
        // Create device management operation
        KapuaId deviceManagementOperationId = createManagementOperation(scopeId, deviceId, operationId, packageRequestMessage);

        // Build request
        DeviceCallBuilder<PackageRequestChannel, PackageRequestPayload, PackageRequestMessage, PackageResponseMessage> packageDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(packageRequestMessage)
                        .withTimeoutOrDefault(packageUninstallOptions.getTimeout());

        // Do uninstall
        PackageResponseMessage responseMessage;
        try {
            responseMessage = packageDeviceCallBuilder.send();
        } catch (Exception e) {
            closeManagementOperation(scopeId, operationId);
            LOG.error("Error while executing DevicePackageUninstallRequest {} for Device {}. Error: {}", packageUninstallRequest.getName(), deviceId, e.getMessage(), e);
            throw e;
        }
        // Create event
        createDeviceEvent(scopeId, deviceId, packageRequestMessage, responseMessage);
        // Check response
        try {
            checkResponseAcceptedOrThrowError(responseMessage);
        } catch (Exception e) {
            closeManagementOperation(scopeId, operationId, responseMessage);
            throw e;
        }
        // Return operation id
        return deviceManagementOperationId;
    }

    @Override
    public DevicePackageUninstallOperation uninstallStatus(KapuaId scopeId, KapuaId deviceId, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(deviceId, DEVICE_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, Actions.read, scopeId));
        // Prepare the request
        PackageRequestChannel packageRequestChannel = new PackageRequestChannel();
        packageRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        packageRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        packageRequestChannel.setMethod(KapuaMethod.READ);
        packageRequestChannel.setPackageResource(PackageResource.UNINSTALL);

        PackageRequestPayload packageRequestPayload = new PackageRequestPayload();

        PackageRequestMessage packageRequestMessage = new PackageRequestMessage();
        packageRequestMessage.setScopeId(scopeId);
        packageRequestMessage.setDeviceId(deviceId);
        packageRequestMessage.setCapturedOn(new Date());
        packageRequestMessage.setPayload(packageRequestPayload);
        packageRequestMessage.setChannel(packageRequestChannel);

        // Build request
        DeviceCallBuilder<PackageRequestChannel, PackageRequestPayload, PackageRequestMessage, PackageResponseMessage> packageDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(packageRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do get
        PackageResponseMessage responseMessage;
        try {
            responseMessage = packageDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while getting DevicePackageUninstallOperation for Device {}. Error: {}", deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, packageRequestMessage, responseMessage);
        // Check response
        return checkResponseAcceptedOrThrowError(responseMessage, () -> responseMessage.getPayload().getDevicePackageUninstallOperation().orElse(new DevicePackageUninstallOperation()));
    }

    private void verifyOverflowPackageFields(DevicePackageDownloadRequest packageDownloadRequest) throws KapuaIllegalArgumentException {
        ArgumentValidator.lengthRange(packageDownloadRequest.getUri().toString(), null, 2048, "packageDownloadRequest.uri");
        ArgumentValidator.lengthRange(packageDownloadRequest.getName(), null, 256, "packageDownloadRequest.name");
        ArgumentValidator.lengthRange(packageDownloadRequest.getVersion(), null, 256, "packageDownloadRequest.version");
    }

    // Private methods

    private List<DeviceManagementOperationProperty> extractInputProperties(KapuaRequestMessage<?, ?> requestMessage) {

        List<DeviceManagementOperationProperty> inputProperties = new ArrayList<>();
        Map<String, Object> properties = requestMessage.getPayload().getMetrics();

        properties.forEach((k, v) -> {
            if (v != null) {
                inputProperties.add(
                        deviceManagementOperationFactory.newStepProperty(
                                k,
                                ObjectTypeConverter.toString(v.getClass()),
                                ObjectValueConverter.toString(v))
                );
            }
        });

        return inputProperties;
    }

    // Device Management Operations
    protected KapuaId createManagementOperation(KapuaId scopeId, KapuaId deviceId, KapuaId operationId, KapuaRequestMessage<?, ?> requestMessage) throws KapuaException {

        final DeviceManagementOperationCreator deviceManagementOperationCreator = new DeviceManagementOperationCreator(scopeId);
        deviceManagementOperationCreator.setDeviceId(deviceId);
        deviceManagementOperationCreator.setOperationId(operationId);
        deviceManagementOperationCreator.setStartedOn(new Date());
        deviceManagementOperationCreator.setAppId(requestMessage.getChannel().getAppName().getValue());
        deviceManagementOperationCreator.setAction(requestMessage.getChannel().getMethod());
        deviceManagementOperationCreator.setResource(!requestMessage.getChannel().getSemanticParts().isEmpty() ? requestMessage.getChannel().getSemanticParts().get(0) : null);
        deviceManagementOperationCreator.setStatus(NotifyStatus.RUNNING);
        deviceManagementOperationCreator.setInputProperties(extractInputProperties(requestMessage));

        final DeviceManagementOperation deviceManagementOperation = KapuaSecurityUtils.doPrivileged(() ->
                deviceManagementOperationRegistryService.create(deviceManagementOperationCreator));

        return deviceManagementOperation.getId();
    }

    protected void closeManagementOperation(KapuaId scopeId, KapuaId operationId) throws KapuaException {
        closeManagementOperation(scopeId, operationId, null);
    }

    protected void closeManagementOperation(KapuaId scopeId, KapuaId operationId, KapuaResponseMessage<?, ?> responseMessageMessage) throws KapuaException {
        if (responseMessageMessage != null) {
            deviceManagementOperationRegistryService.updateStatus(scopeId,
                    operationId,
                    responseMessageMessage.getResponseCode().isAccepted() ? NotifyStatus.COMPLETED : NotifyStatus.FAILED,
                    responseMessageMessage.getReceivedOn());
        } else {
            deviceManagementOperationRegistryService.updateStatus(scopeId,
                    operationId,
                    NotifyStatus.FAILED,
                    new Date());
        }
    }
}
