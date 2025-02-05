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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.translator.kura.kapua;

import java.math.BigInteger;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraBundleInfo;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackage;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackages;
import org.eclipse.kapua.service.device.call.kura.model.deploy.PackageMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseCode;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageResponseChannel;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageResponseMessage;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageResponsePayload;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfo;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfos;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadStatus;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;

/**
 * {@link org.eclipse.kapua.translator.Translator} implementation from {@link KuraResponseMessage} to {@link PackageResponseMessage}
 *
 * @since 1.0.0
 */
public class TranslatorAppPackageKuraKapua extends AbstractSimpleTranslatorResponseKuraKapua<PackageResponseChannel, PackageResponsePayload, PackageResponseMessage> {

    @Inject
    public TranslatorAppPackageKuraKapua(DeviceManagementSetting deviceManagementSetting) {
        super(deviceManagementSetting, PackageResponseMessage.class, PackageResponsePayload.class);
    }

    @Override
    protected PackageResponseChannel translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException {
        try {
            translatorKuraKapuaUtils.validateKuraResponseChannel(kuraResponseChannel, PackageMetrics.APP_ID, PackageMetrics.APP_VERSION);

            return new PackageResponseChannel();
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraResponseChannel);
        }
    }

    @Override
    protected PackageResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        PackageResponsePayload responsePayload = super.translatePayload(kuraResponsePayload);

        try {
            KuraResponseCode responseCode = kuraResponsePayload.getResponseCode();

            Map<String, Object> metrics = kuraResponsePayload.getMetrics();
            if (!KuraResponseCode.INTERNAL_ERROR.equals(responseCode)) {
                if (metrics.get(PackageMetrics.APP_METRIC_PACKAGE_OPERATION_ID.getName()) != null) {
                    responsePayload.setPackageDownloadOperationId(new KapuaEid(new BigInteger(metrics.get(PackageMetrics.APP_METRIC_PACKAGE_OPERATION_ID.getName()).toString())));

                    if (metrics.get(PackageMetrics.APP_METRIC_PACKAGE_DOWNLOAD_STATUS.getName()) != null) {
                        DevicePackageDownloadStatus status;

                        String kuraStatus = (String) metrics.get(PackageMetrics.APP_METRIC_PACKAGE_DOWNLOAD_STATUS.getName());
                        switch (kuraStatus) {
                        case "IN_PROGRESS":
                            status = DevicePackageDownloadStatus.IN_PROGRESS;
                            break;
                        case "FAILED":
                            status = DevicePackageDownloadStatus.FAILED;
                            break;
                        case "COMPLETED":
                        case "ALREADY DONE":
                            status = DevicePackageDownloadStatus.COMPLETED;
                            break;
                        default:
                            throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD, null, kuraStatus);
                        }
                        responsePayload.setPackageDownloadOperationStatus(status);
                    }

                    responsePayload.setPackageDownloadOperationSize((Integer) metrics.get(PackageMetrics.APP_METRIC_PACKAGE_DOWNLOAD_SIZE.getName()));
                    responsePayload.setPackageDownloadOperationProgress((Integer) metrics.get(PackageMetrics.APP_METRIC_PACKAGE_DOWNLOAD_PROGRESS.getName()));
                } else {
                    responsePayload.setPackageDownloadOperationStatus(DevicePackageDownloadStatus.NONE);
                }

                if (kuraResponsePayload.hasBody()) {
                    KuraDeploymentPackages kuraDeploymentPackages = readXmlBodyAs(kuraResponsePayload.getBody(), KuraDeploymentPackages.class);

                    responsePayload.setDeploymentPackages(translate(kuraDeploymentPackages));
                }
            } else {
                if (kuraResponsePayload.hasBody()) {
                    String errorMessage = new String(kuraResponsePayload.getBody());

                    responsePayload.setExceptionMessage(errorMessage);
                }
            }

            // Return Kapua Payload
            return responsePayload;
        } catch (InvalidPayloadException ipe) {
            throw ipe;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }

    private DevicePackages translate(KuraDeploymentPackages kuraDeploymentPackages) {
        DevicePackages deviceDeploymentPackages = new DevicePackages();

        KuraDeploymentPackage[] deploymentPackageArray = kuraDeploymentPackages.getDeploymentPackages();
        for (KuraDeploymentPackage deploymentPackage : deploymentPackageArray) {
            DevicePackage deviceDeploymentPackage = new DevicePackage();
            deviceDeploymentPackage.setName(deploymentPackage.getName());
            deviceDeploymentPackage.setVersion(deploymentPackage.getVersion());

            DevicePackageBundleInfos devicePackageBundleInfos = deviceDeploymentPackage.getBundleInfos();
            KuraBundleInfo[] bundleInfoArray = deploymentPackage.getBundleInfos();
            for (KuraBundleInfo bundleInfo : bundleInfoArray) {
                DevicePackageBundleInfo devicePackageBundleInfo = new DevicePackageBundleInfo();
                devicePackageBundleInfo.setName(bundleInfo.getName());
                devicePackageBundleInfo.setVersion(bundleInfo.getVersion());

                // Add the new DevicePackageBundleInfo object to the corresponding list
                devicePackageBundleInfos.getBundlesInfos().add(devicePackageBundleInfo);
            }

            // Add the new DeviceDeploymentPackage object to the corresponding list
            deviceDeploymentPackages.getPackages().add(deviceDeploymentPackage);
        }

        return deviceDeploymentPackages;
    }
}
