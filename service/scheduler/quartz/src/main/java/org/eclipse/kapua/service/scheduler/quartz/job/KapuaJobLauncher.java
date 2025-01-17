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
package org.eclipse.kapua.service.scheduler.quartz.job;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdFactory;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTrigger;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerCreator;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerService;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerStatus;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ths is the {@link Job} that triggers the {@link JobEngineService#startJob(KapuaId, KapuaId)}.
 * <p>
 * In case of successful start it creates a {@link FiredTrigger} to track that {@link JobEngineService#startJob(KapuaId, KapuaId)} was successful
 * <p>
 * In case of {@link Exception} from {@link JobEngineService#startJob(KapuaId, KapuaId)} it saves the {@link Exception#getMessage()} into the {@link FiredTrigger#getMessage()} to let the user be able
 * to solve the issue.
 *
 * @since 1.0.0
 */
public class KapuaJobLauncher implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaJobLauncher.class);

    KapuaLocator locator = KapuaLocator.getInstance();

    KapuaIdFactory kapuaIdFactory = locator.getFactory(KapuaIdFactory.class);

    JobService jobService = locator.getService(JobService.class);
    JobEngineService jobEngineService = locator.getService(JobEngineService.class);

    FiredTriggerService firedTriggerService = locator.getService(FiredTriggerService.class);
    FiredTriggerFactory firedTriggerFactory = locator.getFactory(FiredTriggerFactory.class);

    private KapuaId scopeId;
    private KapuaId jobId;
    private JobStartOptions jobStartOptions;

    /**
     * Constructor.
     * <p>
     * Required by {@link Job}
     *
     * @since 1.0.0
     */
    public KapuaJobLauncher() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            String triggerIdString = context.getTrigger().getKey().getName();
            KapuaId triggerId = kapuaIdFactory.newKapuaId(triggerIdString);

            KapuaSecurityUtils.doPrivileged(() -> {
                try {
                    org.eclipse.kapua.service.job.Job job = jobService.find(getScopeId(), getJobId());

                    if (job == null) {
                        throw new KapuaEntityNotFoundException(org.eclipse.kapua.service.job.Job.class.getName(), jobId);
                    }

                    if (jobStartOptions == null) {
                        jobEngineService.startJob(scopeId, jobId);
                    } else {
                        jobEngineService.startJob(scopeId, jobId, jobStartOptions);
                    }

                    createSuccessfulFiredTrigger(scopeId, triggerId, context.getFireTime());
                } catch (Exception startException) {
                    // This exception is only tracked in the FiredTrigger.message and logged for debug purposes but is not
                    // re-thrown since Exceptions are expected when invoking JobEngineService.start(...)
                    // i.e.: JobTargets/JobStep not defined.
                    createdUnsuccessfulFiredTrigger(scopeId, triggerId, context.getFireTime(), startException);
                    LOG.error("Failed process Job Start. scopeId: {} - jobId: {} - triggerId {}", scopeId, jobId, triggerId, startException);
                }
            });
        } catch (Exception exception) {
            throw new JobExecutionException("Cannot start job!", exception);
        }
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.job.Job#getScopeId()}.
     *
     * @return The {@link org.eclipse.kapua.service.job.Job#getScopeId()}.
     * @since 1.0.0
     */
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.job.Job#getScopeId()}.
     *
     * @param scopeId
     *         The {@link org.eclipse.kapua.service.job.Job#getScopeId()}.
     * @since 1.0.0
     */
    public void setScopeId(KapuaId scopeId) {
        this.scopeId = scopeId;
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.job.Job#getId()}.
     *
     * @return The {@link org.eclipse.kapua.service.job.Job#getId()}.
     * @since 1.0.0
     */
    public KapuaId getJobId() {
        return jobId;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.job.Job#getId()}.
     *
     * @param jobId
     *         The {@link org.eclipse.kapua.service.job.Job#getId()}.
     * @since 1.0.0
     */
    public void setJobId(KapuaId jobId) {
        this.jobId = jobId;
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.job.Job}'s {@link JobStartOptions}.
     *
     * @return The {@link org.eclipse.kapua.service.job.Job}'s {@link JobStartOptions}.
     * @since 1.0.0
     */
    public JobStartOptions getJobStartOptions() {
        return jobStartOptions;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.job.Job}'s {@link JobStartOptions}.
     *
     * @param jobStartOptions
     *         The {@link org.eclipse.kapua.service.job.Job}'s {@link JobStartOptions}.
     * @since 1.0.0
     */
    public void setJobStartOptions(JobStartOptions jobStartOptions) {
        this.jobStartOptions = jobStartOptions;
    }

    /**
     * Creates a {@link FiredTrigger} with {@link FiredTriggerStatus#FIRED}.
     *
     * @param scopeId
     *         The {@link Trigger#getScopeId()}.
     * @param triggerId
     *         The {@link Trigger#getId()}.
     * @param fireTime
     *         The actual {@link Trigger} fire time. Sourced from {@link JobExecutionContext#getFireTime()}.
     * @since 1.5.0
     */
    public void createSuccessfulFiredTrigger(@NotNull KapuaId scopeId, @NotNull KapuaId triggerId, @NotNull Date fireTime) {
        createFiredTrigger(scopeId, triggerId, fireTime, FiredTriggerStatus.FIRED, null);
    }

    /**
     * Creates a {@link FiredTrigger} with {@link FiredTriggerStatus#FAILED}.
     *
     * @param scopeId
     *         The {@link Trigger#getScopeId()}.
     * @param triggerId
     *         The {@link Trigger#getId()}.
     * @param fireTime
     *         The actual {@link Trigger} fire time. Sourced from {@link JobExecutionContext#getFireTime()}.
     * @param exception
     *         The {@link Exception} occurred while processing.
     * @since 1.5.0
     */
    public void createdUnsuccessfulFiredTrigger(@NotNull KapuaId scopeId, @NotNull KapuaId triggerId, @NotNull Date fireTime, @NotNull Exception exception) {
        createFiredTrigger(scopeId, triggerId, fireTime, FiredTriggerStatus.FAILED, exception);
    }

    /**
     * Creates a {@link FiredTrigger} with the given parameters.
     *
     * @param scopeId
     *         The {@link Trigger#getScopeId()}.
     * @param triggerId
     *         The {@link Trigger#getId()}.
     * @param fireTime
     *         The actual {@link Trigger} fire time. Sourced from {@link JobExecutionContext#getFireTime()}.
     * @param status
     *         The {@link FiredTriggerStatus} of the processing.
     * @param exception
     *         The {@link Exception} occurred while processing, if any.
     * @since 1.5.0
     */
    public void createFiredTrigger(@NotNull KapuaId scopeId, @NotNull KapuaId triggerId, @NotNull Date fireTime, @NotNull FiredTriggerStatus status, @Nullable Exception exception) {
        try {
            FiredTriggerCreator firedTriggerCreator = new FiredTriggerCreator(scopeId);
            firedTriggerCreator.setTriggerId(triggerId);
            firedTriggerCreator.setFiredOn(fireTime);
            firedTriggerCreator.setStatus(status);
            firedTriggerCreator.setMessage(exception != null ? exception.getMessage() : null);

            firedTriggerService.create(firedTriggerCreator);
        } catch (Exception e) {
            LOG.error("Failed create {} FiredTrigger for Trigger: {}", status, triggerId, e);
        }
    }
}
