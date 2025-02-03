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
package org.eclipse.kapua.service.device.management.inventory.job.definition;

import com.beust.jcommander.internal.Lists;
import org.eclipse.kapua.service.device.management.inventory.job.DeviceContainerStopTargetProcessor;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepPropertyRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepType;
import org.eclipse.kapua.service.job.step.definition.device.management.TimeoutJobStepPropertyRecord;

public class DeviceContainerStopJobStepDefinition extends JobStepDefinitionRecord {

    public DeviceContainerStopJobStepDefinition() {
        super(null,
                "Container Stop",
                "Execute request to stop a container to the target devices of the Job",
                JobStepType.TARGET,
                null,
                DeviceContainerStopTargetProcessor.class.getName(),
                null,
                Lists.newArrayList(
                        new JobStepPropertyRecord(
                                DeviceContainerPropertyKeys.CONTAINER_INPUT,
                                "XML/JSON that defines the container to be stopped",
                                DeviceInventoryContainer.class.getName(),
                                null,
                                "{\n" +
                                        "  \"name\": \"docker_container_1\",\n" +
                                        "  \"version\": \"nginx:latest\",\n" +
                                        "  \"containerType\": \"DOCKER\",\n" +
                                        "  \"state\": \"ACTIVE\"\n" +
                                        "}",
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