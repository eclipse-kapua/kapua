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
package org.eclipse.kapua.service.device.management.command.job.definition;

import com.beust.jcommander.internal.Lists;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.job.DeviceCommandExecTargetProcessor;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepPropertyRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepType;
import org.eclipse.kapua.service.job.step.definition.device.management.TimeoutJobStepPropertyRecord;

/**
 * {@link JobStepDefinition} to perform {@link DeviceCommandManagementService#exec(KapuaId, KapuaId, DeviceCommandInput, Long)}.
 *
 * @since 2.0.0
 */
public class DeviceCommandExecJobStepDefinition extends JobStepDefinitionRecord {

    private static final long serialVersionUID = -6971341093191023791L;

    public DeviceCommandExecJobStepDefinition() {
        super(null,
                "Command Execution",
                "Request to execute a remote system command on the target devices of the Job",
                JobStepType.TARGET,
                null,
                DeviceCommandExecTargetProcessor.class.getName(),
                null,
                Lists.newArrayList(
                        new JobStepPropertyRecord(
                                DeviceCommandExecPropertyKeys.COMMAND_INPUT,
                                "XML or JSON string that defines the system command to be executed",
                                DeviceCommandInput.class.getName(),
                                null,
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<commandInput>\n    <command>ls</command>\n    <timeout>30000</timeout>\n    <runAsynch>false</runAsynch>\n</commandInput>",
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
