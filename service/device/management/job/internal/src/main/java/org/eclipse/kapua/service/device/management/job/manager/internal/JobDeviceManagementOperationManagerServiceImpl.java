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
package org.eclipse.kapua.service.device.management.job.manager.internal;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.job.engine.JobEngineFactory;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperation;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationAttributes;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationListResult;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationService;
import org.eclipse.kapua.service.device.management.job.manager.JobDeviceManagementOperationManagerService;
import org.eclipse.kapua.service.device.management.message.notification.NotifyStatus;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationProperty;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRegistryService;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link JobDeviceManagementOperationManagerService} implementation.
 *
 * @since 1.1.0
 */
@Singleton
public class JobDeviceManagementOperationManagerServiceImpl implements JobDeviceManagementOperationManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(JobDeviceManagementOperationManagerService.class);

    private final DeviceManagementOperationRegistryService deviceManagementOperationRegistryService;
    private final JobDeviceManagementOperationService jobDeviceManagementOperationService;
    private final JobDeviceManagementOperationFactory jobDeviceManagementOperationFactory;
    private final JobEngineService jobEngineService;
    private final JobEngineFactory jobEngineFactory;
    private final JobTargetService jobTargetService;
    private final JobTargetFactory jobTargetFactory;

    @Inject
    public JobDeviceManagementOperationManagerServiceImpl(
            DeviceManagementOperationRegistryService deviceManagementOperationRegistryService,
            JobDeviceManagementOperationService jobDeviceManagementOperationService,
            JobDeviceManagementOperationFactory jobDeviceManagementOperationFactory,
            JobEngineService jobEngineService,
            JobEngineFactory jobEngineFactory,
            JobTargetService jobTargetService,
            JobTargetFactory jobTargetFactory) {
        this.deviceManagementOperationRegistryService = deviceManagementOperationRegistryService;
        this.jobDeviceManagementOperationService = jobDeviceManagementOperationService;
        this.jobDeviceManagementOperationFactory = jobDeviceManagementOperationFactory;
        this.jobEngineService = jobEngineService;
        this.jobEngineFactory = jobEngineFactory;
        this.jobTargetService = jobTargetService;
        this.jobTargetFactory = jobTargetFactory;
    }

    @Override
    public void processJobTargetOnNotification(KapuaId scopeId, KapuaId operationId, Date updateOn, String resource, NotifyStatus status) throws KapuaException {
        if (NotifyStatus.RUNNING.equals(status)) {
            return;
        }

        DeviceManagementOperation deviceManagementOperation = getDeviceManagementOperation(scopeId, operationId);
        // UGLY 'DEPLOY-V2'-related part
        if (checkLastNotification(deviceManagementOperation, status, resource)) {
            return;
        }
        // Update the job target
        JobDeviceManagementOperation jobDeviceManagementOperation;
        try {
            jobDeviceManagementOperation = getJobDeviceManagementOperation(scopeId, operationId);
        } catch (KapuaEntityNotFoundException kenfe) {
            LOG.warn("The operationId {} does not match any Job. Likely this is run interactively using a DeviceManagementService ", operationId);
            return;
        }

        KapuaQuery jobTargetQuery = new KapuaQuery(scopeId);
        jobTargetQuery.setPredicate(
                jobTargetQuery.andPredicate(
                        jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_ID, jobDeviceManagementOperation.getJobId()),
                        jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_TARGET_ID, deviceManagementOperation.getDeviceId())
                )
        );

        short attempts = 0;
        short limit = 3;
        boolean failed;
        JobTarget jobTarget = null;
        do {
            try {
                JobTargetListResult jobTargets = jobTargetService.query(jobTargetQuery);
                jobTarget = jobTargets.getFirstItem();

                if (jobTarget == null) {
                    LOG.warn("JobTarget with targetId {} for Job {} not found! This is something strange that happened and needs some checking! Reference JobDeviceManagementOperation: {}",
                            deviceManagementOperation.getDeviceId(), jobDeviceManagementOperation.getJobId(), jobDeviceManagementOperation.getId());
                    return;
                }

                switch (status) {
                case COMPLETED:
                    jobTarget.setStatus(JobTargetStatus.NOTIFIED_COMPLETION);
                    break;
                case FAILED:
                    jobTarget.setStatus(JobTargetStatus.PROCESS_FAILED);
                    break;
                case STALE:
                default:
                    break;
                }

                jobTargetService.update(jobTarget);
                failed = false;
            } catch (Exception e) {
                failed = true;
                attempts++;

                if (jobTarget == null) {
                    throw e;
                }

                if (attempts >= limit) {
                    LOG.warn("Update JobTarget {} with status {}...  FAILED! ({}/{}) Throwing error: {}...", jobTarget.getId(), jobTarget.getStatus(), attempts, limit, e.getMessage());
                    throw e;
                } else {
                    LOG.warn("Update JobTarget {} with status {}...  FAILED! ({}/{}) Retrying...", jobTarget.getId(), jobTarget.getStatus(), attempts, limit);
                }
            }
        } while (failed);
        // If PROCESS_FAILED no need to continue the JobTarget processing
        if (JobTargetStatus.PROCESS_FAILED.equals(jobTarget.getStatus())) {
            return;
        }
        // Start the job
        JobStartOptions jobStartOptions = jobEngineFactory.newJobStartOptions();
        jobStartOptions.addTargetIdToSublist(jobTarget.getId());
        jobStartOptions.setFromStepIndex(jobTarget.getStepIndex());
        jobStartOptions.setEnqueue(true);

        jobEngineService.startJob(scopeId, jobDeviceManagementOperation.getJobId(), jobStartOptions);
    }

    /**
     * This fixes the double {@link NotifyStatus#COMPLETED} {@link ManagementOperationNotification} set from Kura when performing a Device package download with the 'install' flag is set to
     * {@code true}.
     * <p>
     * If this is not the last {@link ManagementOperationNotification} the processing must stop.
     *
     * @param deviceManagementOperation
     *         The current {@link DeviceManagementOperation} which the {@link ManagementOperationNotification} refers to.
     * @param status
     *         The {@link ManagementOperationNotification} {@link NotifyStatus}.
     * @param resource
     *         The {@link ManagementOperationNotification} resource.
     * @return {@code true} if this is the last {@link ManagementOperationNotification} for the {@link DeviceManagementOperation}, {@code false} otherwise.
     * @since 1.1.0
     */
    private boolean checkLastNotification(DeviceManagementOperation deviceManagementOperation, NotifyStatus status, String resource) {
        boolean isLastNotification = true;
        if (!NotifyStatus.FAILED.equals(status)) {
            for (DeviceManagementOperationProperty ip : deviceManagementOperation.getInputProperties()) {
                if (ip.getName().equals("kapua.package.download.install")) {
                    if (resource.equals("download")) {
                        isLastNotification = !Boolean.parseBoolean(ip.getPropertyValue());
                    }
                    break;
                }
            }
        }

        if (!isLastNotification) {
            return true;
        }
        return false;
    }

    /**
     * Gets the {@link JobDeviceManagementOperation} associated with the given {@link DeviceManagementOperation#getOperationId()}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} of the {@link JobDeviceManagementOperation}.
     * @param operationId
     *         The {@link DeviceManagementOperation#getOperationId()} to match.
     * @return The matched {@link JobDeviceManagementOperation}
     * @throws KapuaEntityNotFoundException
     *         if there is no {@link JobDeviceManagementOperation} with the given {@code operationId}.
     * @throws KapuaException
     *         If something goes bad.
     * @since 1.1.0
     */
    private JobDeviceManagementOperation getJobDeviceManagementOperation(KapuaId scopeId, KapuaId operationId) throws KapuaException {

        DeviceManagementOperation deviceManagementOperation = getDeviceManagementOperation(scopeId, operationId);

        KapuaQuery query = new KapuaQuery(scopeId);
        query.setPredicate(query.attributePredicate(JobDeviceManagementOperationAttributes.DEVICE_MANAGEMENT_OPERATION_ID, deviceManagementOperation.getId()));

        JobDeviceManagementOperationListResult operations = jobDeviceManagementOperationService.query(query);
        JobDeviceManagementOperation jobDeviceManagementOperation = operations.getFirstItem();

        if (jobDeviceManagementOperation == null) {
            throw new KapuaEntityNotFoundException(JobDeviceManagementOperation.TYPE, operationId);
        }

        return jobDeviceManagementOperation;
    }

    /**
     * Gets the {@link DeviceManagementOperation} that matches the given {@code operationId}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} of the {@link DeviceManagementOperation}.
     * @param operationId
     *         The {@link DeviceManagementOperation#getOperationId()} to match.
     * @return The matched {@link DeviceManagementOperation}.
     * @throws KapuaEntityNotFoundException
     *         if there is no {@link DeviceManagementOperation} with the given {@code operationId}.
     * @throws KapuaException
     *         If something goes bad.
     * @since 1.1.0
     */
    private DeviceManagementOperation getDeviceManagementOperation(KapuaId scopeId, KapuaId operationId) throws KapuaException {
        DeviceManagementOperation deviceManagementOperation = deviceManagementOperationRegistryService.findByOperationId(scopeId, operationId);

        if (deviceManagementOperation == null) {
            throw new KapuaEntityNotFoundException(DeviceManagementOperation.TYPE, operationId);
        }

        return deviceManagementOperation;
    }
}
