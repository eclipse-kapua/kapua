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
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.job.engine.app.web.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.eclipse.kapua.commons.rest.model.errors.CleanJobDataExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalNullArgumentExceptionInfo;
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
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ThrowableInfo;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.commons.model.JobStepPropertiesOverrides;
import org.eclipse.kapua.job.engine.commons.model.JobTargetSublist;
import org.eclipse.kapua.service.authentication.AuthenticationXmlRegistry;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceConfiguration;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
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
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOptions;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.device.management.packages.model.install.DevicePackageInstallOptions;
import org.eclipse.kapua.service.device.management.packages.model.install.DevicePackageInstallRequest;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallOptions;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallRequest;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobQuery;
import org.eclipse.kapua.service.job.JobXmlRegistry;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.eclipse.kapua.service.job.execution.JobExecutionListResult;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionXmlRegistry;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepQuery;
import org.eclipse.kapua.service.job.step.JobStepXmlRegistry;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetQuery;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.TriggerListResult;
import org.eclipse.kapua.service.scheduler.trigger.TriggerQuery;
import org.eclipse.kapua.service.scheduler.trigger.TriggerXmlRegistry;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;

/**
 * @deprecated since 2.1.0 - rely on autodiscovery. Leaving this here for comparison
 */
public class JobEngineJAXBContextProvider implements JAXBContextProvider {

    private JAXBContext jaxbContext;

    @Override
    public JAXBContext getJAXBContext() {
        if (jaxbContext != null) {
            return jaxbContext;
        }
        try {
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);

            jaxbContext = JAXBContextFactory.createContext(new Class[] {
                    // REST API exception models
                    ThrowableInfo.class,
                    ExceptionInfo.class,

                    SubjectUnauthorizedExceptionInfo.class,

                    EntityNotFoundExceptionInfo.class,
                    IllegalArgumentExceptionInfo.class,
                    IllegalNullArgumentExceptionInfo.class,

                    // Job Engine
                    JobStartOptions.class,
                    JobTargetSublist.class,
                    JobStepPropertiesOverrides.class,

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

                    // Authentication
                    AuthenticationXmlRegistry.class,
                    AccessToken.class,

                    // Device Management Keystore
                    DeviceKeystores.class,
                    DeviceKeystore.class,
                    DeviceKeystoreCertificate.class,
                    DeviceKeystoreItems.class,
                    DeviceKeystoreItem.class,
                    DeviceKeystoreItemQuery.class,
                    DeviceKeystoreKeypair.class,
                    DeviceKeystoreCSRInfo.class,
                    DeviceKeystoreCSR.class,
                    DeviceKeystoreXmlRegistry.class,

                    // Jobs
                    Job.class,
                    JobQuery.class,
                    JobXmlRegistry.class,

                    JobStep.class,
                    JobStepListResult.class,
                    JobStepQuery.class,
                    JobStepXmlRegistry.class,
                    JobStepProperty.class,

                    JobExecution.class,
                    JobExecutionListResult.class,
                    JobExecutionQuery.class,
                    JobExecutionXmlRegistry.class,

                    JobTarget.class,
                    JobTargetListResult.class,
                    JobTargetQuery.class,
                    JobExecutionXmlRegistry.class,

                    JobTargetSublist.class,

                    DeviceCommandInput.class,
                    DevicePackageDownloadRequest.class,
                    DevicePackageDownloadOptions.class,
                    DevicePackageInstallRequest.class,
                    DevicePackageInstallOptions.class,
                    DevicePackageUninstallRequest.class,
                    DevicePackageUninstallOptions.class,
                    DeviceAssets.class,
                    DeviceConfiguration.class,

                    Trigger.class,
                    TriggerListResult.class,
                    TriggerQuery.class,
                    TriggerXmlRegistry.class,

                    KuraDeviceConfiguration.class
            }, properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jaxbContext;
    }

}
