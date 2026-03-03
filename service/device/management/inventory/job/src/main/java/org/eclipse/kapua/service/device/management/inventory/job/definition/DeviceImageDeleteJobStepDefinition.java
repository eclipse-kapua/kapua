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
import org.eclipse.kapua.service.device.management.inventory.job.DeviceImageDeleteTargetProcessor;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepPropertyRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepType;
import org.eclipse.kapua.service.job.step.definition.device.management.TimeoutJobStepPropertyRecord;

public class DeviceImageDeleteJobStepDefinition extends JobStepDefinitionRecord {

    public DeviceImageDeleteJobStepDefinition() {
        super(null,
                "Image Delete",
                "Execute request to delete an image to the target devices of the Job",
                JobStepType.TARGET,
                null,
                DeviceImageDeleteTargetProcessor.class.getName(),
                null,
                Lists.newArrayList(
                        new JobStepPropertyRecord(
                                DeviceContainerPropertyKeys.IMAGE_NAME,
                                "Name of the image to be deleted",
                                String.class.getName(),
                                null,
                                "nginx",
                                Boolean.TRUE,
                                Boolean.FALSE,
                                null,
                                null,
                                null,
                                null,
                                null),
                        new JobStepPropertyRecord(
                                DeviceContainerPropertyKeys.IMAGE_VERSION,
                                "Version of the image to be deleted",
                                String.class.getName(),
                                null,
                                "latest",
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
