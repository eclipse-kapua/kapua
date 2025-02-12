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
package org.eclipse.kapua.service.device.management.inventory.job.definition;

import com.beust.jcommander.internal.Lists;
import org.eclipse.kapua.service.device.management.inventory.job.DeviceContainerStartTargetProcessor;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepPropertyRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepType;
import org.eclipse.kapua.service.job.step.definition.device.management.TimeoutJobStepPropertyRecord;

public class DeviceContainerStartJobStepDefinition extends JobStepDefinitionRecord {

    public DeviceContainerStartJobStepDefinition() {
        super(null,
                "Container Start",
                "Execute request to start a container to the target devices of the Job",
                JobStepType.TARGET,
                null,
                DeviceContainerStartTargetProcessor.class.getName(),
                null,
                Lists.newArrayList(
                        new JobStepPropertyRecord(
                                DeviceContainerPropertyKeys.CONTAINER_NAME,
                                "Name of the container to be executed",
                                String.class.getName(),
                                null,
                                null,
                                Boolean.TRUE,
                                Boolean.FALSE,
                                null,
                                null,
                                null,
                                null,
                                null),
                        new JobStepPropertyRecord(
                                DeviceContainerPropertyKeys.CONTAINER_VERSION,
                                "Version of the container to be executed",
                                String.class.getName(),
                                null,
                                null,
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