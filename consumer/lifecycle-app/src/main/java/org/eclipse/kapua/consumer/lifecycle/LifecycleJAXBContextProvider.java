/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.consumer.lifecycle;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.rest.model.IsJobRunningMultipleResponse;
import org.eclipse.kapua.commons.rest.model.IsJobRunningResponse;
import org.eclipse.kapua.commons.rest.model.MultipleJobIdRequest;
import org.eclipse.kapua.commons.rest.model.errors.CleanJobDataExceptionInfo;
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
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordCreator;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordListResult;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordQuery;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreXmlRegistry;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTdesignate;
import org.eclipse.kapua.model.config.metatype.KapuaTicon;
import org.eclipse.kapua.model.config.metatype.KapuaTmetadata;
import org.eclipse.kapua.model.config.metatype.KapuaTobject;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.model.config.metatype.KapuaTscalar;
import org.eclipse.kapua.service.device.call.kura.model.bundle.KuraBundle;
import org.eclipse.kapua.service.device.call.kura.model.bundle.KuraBundles;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceComponentConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraBundleInfo;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackage;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackages;
import org.eclipse.kapua.service.device.call.kura.model.snapshot.KuraSnapshotIds;
import org.eclipse.kapua.service.device.management.asset.DeviceAsset;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallRequest;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshot;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshots;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import java.util.HashMap;
import java.util.Map;

public class LifecycleJAXBContextProvider implements JAXBContextProvider {

    private static final Logger LOG = LoggerFactory.getLogger(LifecycleJAXBContextProvider.class);

    private JAXBContext context;

    @Override
    public JAXBContext getJAXBContext() throws KapuaException {
        if (context == null) {
            Class<?>[] classes = new Class<?>[]{

                    // Kapua Service Configuration + Device Management Configuration
                    KapuaTmetadata.class,
                    KapuaTocd.class,
                    KapuaTad.class,
                    KapuaTicon.class,
                    KapuaTscalar.class,
                    KapuaToption.class,
                    KapuaTdesignate.class,
                    KapuaTobject.class,

                    // Kapua Service Event
                    ServiceEvent.class,
                    EventStoreRecordCreator.class,
                    EventStoreRecordListResult.class,
                    EventStoreRecordQuery.class,
                    EventStoreXmlRegistry.class,

                    // TODO EXT-CAMEL only for test remove when jobs will be defined in their own container
                    // Job Engine
                    JobStartOptions.class,
                    IsJobRunningResponse.class,
                    IsJobRunningMultipleResponse.class,
                    MultipleJobIdRequest.class,

                    // Job Engine Exception Info
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

                    // Device Management Command
                    DeviceCommandInput.class,
                    DeviceCommandOutput.class,

                    // Device Management Configuration
                    DeviceConfiguration.class,
                    DeviceComponentConfiguration.class,
                    KuraDeviceComponentConfiguration.class,
                    KuraDeviceConfiguration.class,

                    // Device Management Asset
                    DeviceAsset.class,
                    DeviceAssets.class,

                    // Device Management Bundles
                    DeviceBundle.class,
                    DeviceBundles.class,
                    KuraBundle.class,
                    KuraBundles.class,

                    // Device Management Packages
                    DevicePackage.class,
                    DevicePackages.class,
                    DevicePackageDownloadRequest.class,
                    DevicePackageUninstallRequest.class,
                    KuraDeploymentPackage.class,
                    KuraDeploymentPackages.class,
                    KuraBundleInfo.class,

                    // Device Management Snapshot
                    DeviceSnapshots.class,
                    DeviceSnapshot.class,
                    KuraSnapshotIds.class
            };
            try {
                Map<String, Object> properties = new HashMap<>(1);
                properties.put(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);

                context = JAXBContextFactory.createContext(classes, properties);
                LOG.debug("Default JAXB context initialized!");
            } catch (Exception e) {
                throw KapuaException.internalError(e, "Error creating JAXBContext!");
            }
        }
        return context;
    }
}
