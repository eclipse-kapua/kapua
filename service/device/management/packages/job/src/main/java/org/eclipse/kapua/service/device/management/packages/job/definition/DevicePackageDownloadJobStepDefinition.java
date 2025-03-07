/*******************************************************************************
 * Copyright (c) 2024, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.packages.job.definition;

import com.beust.jcommander.internal.Lists;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.job.DevicePackageDownloadTargetProcessor;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOptions;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepPropertyRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepType;
import org.eclipse.kapua.service.job.step.definition.device.management.TimeoutJobStepPropertyRecord;

/**
 * {@link JobStepDefinition} to perform {@link DevicePackageManagementService#downloadExec(KapuaId, KapuaId, DevicePackageDownloadRequest, DevicePackageDownloadOptions)}
 *
 * @since 2.0.0
 */
public class DevicePackageDownloadJobStepDefinition extends JobStepDefinitionRecord {

    private static final long serialVersionUID = 867888593933132944L;

    public DevicePackageDownloadJobStepDefinition() {
        super(null,
                "Package Download / Install",
                "Execute request to download/install a deployment package to the target devices of the Job",
                JobStepType.TARGET,
                null,
                DevicePackageDownloadTargetProcessor.class.getName(),
                null,
                Lists.newArrayList(
                        new JobStepPropertyRecord(
                                DevicePackageDownloadPropertyKeys.PACKAGE_DOWNLOAD_REQUEST,
                                "XML or JSON string that defines the package download request sent",
                                DevicePackageDownloadRequest.class.getName(),
                                null,
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<downloadRequest>\n   <uri>http://download.eclipse.org/kura/releases/3.2.0/org.eclipse.kura.demo.heater_1.0.300.dp</uri>\n   <name>heater</name>\n   <version>1.0.300</version>\n   <install>true</install>\n</downloadRequest>",
                                Boolean.TRUE,
                                Boolean.FALSE,
                                null,
                                null,
                                null,
                                null,
                                null),
                        new TimeoutJobStepPropertyRecord()
                )
        );
    }
}
