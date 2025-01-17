/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.packages.job;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.job.engine.JobEngineFactory;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.commons.operation.AbstractDeviceTargetProcessor;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperation;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationCreator;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationService;
import org.eclipse.kapua.service.device.management.message.notification.NotifyStatus;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRegistryService;
import org.eclipse.kapua.service.job.operation.TargetProcessor;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;

/**
 * {@link AbstractDevicePackageTargetProcessor} for {@link DevicePackageManagementService} operations.
 *
 * @since 1.1.0
 */
public abstract class AbstractDevicePackageTargetProcessor extends AbstractDeviceTargetProcessor implements TargetProcessor {

    @Inject
    DeviceManagementOperationRegistryService deviceManagementOperationRegistryService;
    @Inject
    JobDeviceManagementOperationService jobDeviceManagementOperationService;
    @Inject
    JobDeviceManagementOperationFactory jobDeviceManagementOperationFactory;
    @Inject
    JobEngineService jobEngineService;
    @Inject
    JobEngineFactory jobEngineFactory;

    protected void createJobDeviceManagementOperation(KapuaId scopeId, KapuaId jobId, JobTarget jobTarget, KapuaId operationId) throws KapuaException {
        // Save the jobId-deviceManagementOperationId pair to track resuming
        JobDeviceManagementOperationCreator jobDeviceManagementOperationCreator = new JobDeviceManagementOperationCreator(scopeId);
        jobDeviceManagementOperationCreator.setJobId(jobId);
        jobDeviceManagementOperationCreator.setDeviceManagementOperationId(operationId);

        JobDeviceManagementOperation jobDeviceManagementOperation = KapuaSecurityUtils.doPrivileged(() -> jobDeviceManagementOperationService.create(jobDeviceManagementOperationCreator));
        // Check if the operation has already COMPLETED/FAILED
        DeviceManagementOperation deviceManagementOperation = KapuaSecurityUtils.doPrivileged(() -> deviceManagementOperationRegistryService.find(scopeId, operationId));

        if (deviceManagementOperation == null) {
            throw new KapuaEntityNotFoundException(DeviceManagementOperation.TYPE, operationId);
        }

        if (deviceManagementOperation.getEndedOn() != null && deviceManagementOperation.getEndedOn().before(jobDeviceManagementOperation.getCreatedOn())) {
            JobTargetStatus jobTargetStatus;
            if (NotifyStatus.COMPLETED.equals(deviceManagementOperation.getStatus())) {
                jobTargetStatus = JobTargetStatus.NOTIFIED_COMPLETION;
            } else if (NotifyStatus.FAILED.equals(deviceManagementOperation.getStatus())) {
                jobTargetStatus = JobTargetStatus.PROCESS_FAILED;
            } else {
                return;
            }

            jobTarget.setStatus(jobTargetStatus);
            // If PROCESS_FAILED no need to continue the JobTarget processing
            if (JobTargetStatus.PROCESS_FAILED.equals(jobTarget.getStatus())) {
                return;
            }
            // Enqueue the job
            JobStartOptions jobStartOptions = jobEngineFactory.newJobStartOptions();
            jobStartOptions.addTargetIdToSublist(jobTarget.getId());
            jobStartOptions.setFromStepIndex(jobTarget.getStepIndex());
            jobStartOptions.setEnqueue(true);

            KapuaSecurityUtils.doPrivileged(() -> jobEngineService.startJob(scopeId, jobDeviceManagementOperation.getJobId(), jobStartOptions));
        }
    }

}
