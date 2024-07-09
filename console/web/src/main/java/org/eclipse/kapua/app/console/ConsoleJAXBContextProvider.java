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
package org.eclipse.kapua.app.console;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.rest.model.IsJobRunningResponse;
import org.eclipse.kapua.commons.rest.model.errors.CleanJobDataExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobAlreadyRunningExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobEngineExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobInvalidTargetExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobMissingStepExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobMissingTargetExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobNotRunningExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobResumingExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobRunningExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobScopedEngineExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobStartingExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.JobStoppingExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ThrowableInfo;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordCreator;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordListResult;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordQuery;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreXmlRegistry;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.job.engine.JobEngineXmlRegistry;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.commons.model.JobTargetSublist;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTicon;
import org.eclipse.kapua.model.config.metatype.KapuaTmetadata;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.service.device.call.kura.model.bundle.KuraBundle;
import org.eclipse.kapua.service.device.call.kura.model.bundle.KuraBundles;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceComponentConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraBundleInfo;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackage;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackages;
import org.eclipse.kapua.service.device.call.kura.model.inventory.KuraInventoryItem;
import org.eclipse.kapua.service.device.call.kura.model.inventory.KuraInventoryItems;
import org.eclipse.kapua.service.device.call.kura.model.inventory.bundles.KuraInventoryBundle;
import org.eclipse.kapua.service.device.call.kura.model.inventory.bundles.KuraInventoryBundles;
import org.eclipse.kapua.service.device.call.kura.model.inventory.containers.KuraInventoryContainer;
import org.eclipse.kapua.service.device.call.kura.model.inventory.containers.KuraInventoryContainers;
import org.eclipse.kapua.service.device.call.kura.model.inventory.packages.KuraInventoryPackage;
import org.eclipse.kapua.service.device.call.kura.model.inventory.packages.KuraInventoryPackages;
import org.eclipse.kapua.service.device.call.kura.model.inventory.system.KuraInventorySystemPackage;
import org.eclipse.kapua.service.device.call.kura.model.inventory.system.KuraInventorySystemPackages;
import org.eclipse.kapua.service.device.call.kura.model.snapshot.KuraSnapshotIds;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetXmlRegistry;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundles;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainers;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventoryItem;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventoryXmlRegistry;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackage;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackages;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackage;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackages;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSR;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSRInfo;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCertificate;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItem;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItemQuery;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItems;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreKeypair;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreXmlRegistry;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystores;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.device.management.packages.model.install.DevicePackageInstallRequest;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallRequest;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshots;
import org.eclipse.kapua.service.job.JobListResult;
import org.eclipse.kapua.service.job.JobQuery;
import org.eclipse.kapua.service.job.JobXmlRegistry;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;

public class ConsoleJAXBContextProvider implements JAXBContextProvider {

    private JAXBContext context;

    @Override
    public JAXBContext getJAXBContext() throws KapuaException {
        try {
            if (context == null) {
                Map<String, Object> properties = new HashMap<String, Object>(1);
                properties.put(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);

                context = JAXBContextFactory.createContext(new Class<?>[] {
                        // REST API exception models
                        ThrowableInfo.class,
                        ExceptionInfo.class,

                        // Jobs Exception Info
                        CleanJobDataExceptionInfo.class,
                        JobAlreadyRunningExceptionInfo.class,
                        JobEngineExceptionInfo.class,
                        JobScopedEngineExceptionInfo.class,
                        JobInvalidTargetExceptionInfo.class,
                        JobMissingStepExceptionInfo.class,
                        JobMissingTargetExceptionInfo.class,
                        JobNotRunningExceptionInfo.class,
                        JobResumingExceptionInfo.class,
                        JobRunningExceptionInfo.class,
                        JobStartingExceptionInfo.class,
                        JobStoppingExceptionInfo.class,

                        KuraDeviceComponentConfiguration.class,
                        KuraDeviceConfiguration.class,
                        KuraDeploymentPackage.class,
                        KuraDeploymentPackages.class,
                        KuraBundle.class,
                        KuraBundles.class,
                        KuraBundleInfo.class,

                        DeviceAssets.class,

                        DeviceBundle.class,
                        DeviceBundles.class,

                        DeviceCommandInput.class,
                        DeviceCommandOutput.class,

                        DeviceConfiguration.class,
                        DeviceComponentConfiguration.class,

                        // Device Management Inventory
                        DeviceInventory.class,
                        DeviceInventoryItem.class,
                        KuraInventoryItems.class,
                        KuraInventoryItem.class,
                        DeviceInventoryBundles.class,
                        DeviceInventoryBundle.class,
                        KuraInventoryBundles.class,
                        KuraInventoryBundle.class,
                        DeviceInventoryContainers.class,
                        DeviceInventoryContainer.class,
                        KuraInventoryContainers.class,
                        KuraInventoryContainer.class,
                        DeviceInventoryPackages.class,
                        DeviceInventoryPackage.class,
                        KuraInventoryPackages.class,
                        KuraInventoryPackage.class,
                        DeviceInventorySystemPackages.class,
                        DeviceInventorySystemPackage.class,
                        KuraInventorySystemPackages.class,
                        KuraInventorySystemPackage.class,
                        DeviceInventoryXmlRegistry.class,

                        // Device Management Keystore
                        DeviceKeystores.class,
                        DeviceKeystore.class,
                        DeviceKeystoreCertificate.class,
                        DeviceKeystoreItems.class,
                        DeviceKeystoreItem.class,
                        DeviceKeystoreItemQuery.class,
                        DeviceKeystoreCertificate.class,
                        DeviceKeystoreKeypair.class,
                        DeviceKeystoreCSRInfo.class,
                        DeviceKeystoreCSR.class,
                        DeviceKeystoreXmlRegistry.class,

                        // Device Management Packages
                        DevicePackageDownloadRequest.class,
                        DevicePackageInstallRequest.class,
                        DevicePackageUninstallRequest.class,
                        DevicePackages.class,

                        KuraSnapshotIds.class,
                        DeviceSnapshots.class,
                        KapuaTocd.class,
                        KapuaTad.class,
                        KapuaTicon.class,
                        KapuaToption.class,
                        KapuaTmetadata.class,

                        // Device Management Assets
                        DeviceAssets.class,
                        DeviceAssetXmlRegistry.class,

                        // Job
                        JobTargetSublist.class,
                        JobStartOptions.class,
                        IsJobRunningResponse.class,
                        JobListResult.class,
                        JobQuery.class,
                        JobXmlRegistry.class,
                        JobEngineXmlRegistry.class,

                        // KapuaEvent
                        ServiceEvent.class,
                        EventStoreRecordCreator.class,
                        EventStoreRecordListResult.class,
                        EventStoreRecordQuery.class,
                        EventStoreXmlRegistry.class,

                }, properties);
            }
            return context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
