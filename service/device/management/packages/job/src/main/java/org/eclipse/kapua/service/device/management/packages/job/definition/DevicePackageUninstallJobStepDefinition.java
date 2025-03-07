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
import org.eclipse.kapua.service.device.management.packages.job.DevicePackageUninstallTargetProcessor;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallOptions;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallRequest;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepPropertyRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepType;
import org.eclipse.kapua.service.job.step.definition.device.management.TimeoutJobStepPropertyRecord;

/**
 * {@link JobStepDefinition} to perform {@link DevicePackageManagementService#uninstallExec(KapuaId, KapuaId, DevicePackageUninstallRequest, DevicePackageUninstallOptions)}
 *
 * @since 2.0.0
 */
public class DevicePackageUninstallJobStepDefinition extends JobStepDefinitionRecord {

    private static final long serialVersionUID = -3580721817244804960L;

    public DevicePackageUninstallJobStepDefinition() {
        super(null,
                "Package Uninstall",
                "Execute request to uninstall  a deployment package to the target devices of the Job",
                JobStepType.TARGET,
                null,
                DevicePackageUninstallTargetProcessor.class.getName(),
                null,
                Lists.newArrayList(
                        new JobStepPropertyRecord(
                                DevicePackageUninstallPropertyKeys.PACKAGE_UNINSTALL_REQUEST,
                                "XML or JSON string that defines the package uninstall request sent",
                                DevicePackageUninstallRequest.class.getName(),
                                null,
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<uninstallRequest>\n   <name>heater</name>\n   <version>1.0.300</version>\n   <reboot>true</reboot>\n   <rebootDelay>30000</rebootDelay>\n</uninstallRequest>",
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
