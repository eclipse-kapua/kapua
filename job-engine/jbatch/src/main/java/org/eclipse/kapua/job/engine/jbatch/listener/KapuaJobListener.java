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
package org.eclipse.kapua.job.engine.jbatch.listener;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.commons.logger.JobLogger;
import org.eclipse.kapua.job.engine.commons.model.JobTargetSublist;
import org.eclipse.kapua.job.engine.commons.wrappers.JobContextWrapper;
import org.eclipse.kapua.job.engine.exception.JobAlreadyRunningException;
import org.eclipse.kapua.job.engine.jbatch.driver.JbatchDriver;
import org.eclipse.kapua.job.engine.jbatch.setting.JobEngineSetting;
import org.eclipse.kapua.job.engine.jbatch.setting.JobEngineSettingKeys;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecution;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionCreator;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionFactory;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionService;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionStatus;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionCreator;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionListResult;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetQuery;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.batch.api.listener.AbstractJobListener;
import javax.batch.api.listener.JobListener;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

/**
 * {@link JobListener} implementations.
 * <p>
 * Listener for all {@link Job}.
 *
 * @since 1.0.0
 */
public class KapuaJobListener extends AbstractJobListener implements JobListener {

    private static final Logger LOG = LoggerFactory.getLogger(KapuaJobListener.class);
    private static final String JBATCH_EXECUTION_ID = "JBATCH_EXECUTION_ID";

    @Inject
    private JobEngineSetting jobEngineSetting;
    @Inject
    private JobExecutionService jobExecutionService;
    @Inject
    private JobExecutionFactory jobExecutionFactory;
    @Inject
    private JobTargetService jobTargetService;
    @Inject
    private JobTargetFactory jobTargetFactory;
    @Inject
    private QueuedJobExecutionService queuedJobExecutionService;
    @Inject
    private QueuedJobExecutionFactory queuedJobExecutionFactory;
    @Inject
    private JobContext jobContext;
    @Inject
    private QueuedJobExecutionCheckTaskFactory queuedJobExecutionCheckTaskFactory;
    @Inject
    private JbatchDriver jbatchDriver;

    /**
     * Before starting the actual {@link org.eclipse.kapua.service.job.Job} processing, create the {@link JobExecution} to track progress and
     * check if there are other {@link JobExecution}s running with the same {@link JobExecution#getTargetIds()}.
     * <p>
     * If there are {@link JobExecution} running with matching {@link JobTargetSublist} the current  {@link JobExecution} is stopped.
     * According to the {@link JobStartOptions#getEnqueue()} parameter, the {@link JobExecution} can be:
     * <ul>
     * <li>if ({@code enqueue} = {@code false}) then {@link JobAlreadyRunningException} is {@code throw}n </li>
     * <li>if ({@code enqueue} = {@code true}) then a new {@link QueuedJobExecution} is created</li>
     * </ul>
     */
    @Override
    public void beforeJob() throws Exception {
        JobContextWrapper jobContextWrapper = new JobContextWrapper(jobContext);

        JobLogger jobLogger = jobContextWrapper.getJobLogger();
        jobLogger.setClassLog(LOG);

        jobLogger.info("Running before job...");
        jobLogger.info("Run configuration:");
        jobLogger.info("\tTarget count:                 {}", jobContextWrapper.getTargetSublist().size() != 0 ? jobContextWrapper.getTargetSublist().size() : "all");
        jobLogger.info("\tReset step index:             {}", jobContextWrapper.getResetStepIndex());
        jobLogger.info("\tFrom step index:              {}", jobContextWrapper.getFromStepIndex() != null ? jobContextWrapper.getFromStepIndex() : 0);
        jobLogger.info("\tResuming job execution:       {}", jobContextWrapper.getResumedJobExecutionId() != null ? jobContextWrapper.getResumedJobExecutionId() : "none");
        jobLogger.info("\tEnqueue:                      {}", jobContextWrapper.getEnqueue());

        if (jobContextWrapper.getJobStepPropertiesOverrides().isEmpty()) {
            jobLogger.info("\tStep Properties Overrides:    none");
        } else {
            jobLogger.info("\tStep Properties Overrides:    ");
            jobContextWrapper
                    .getJobStepPropertiesOverrides()
                    .getJobStepPropertiesOverrides()
                    .forEach(jsp -> jobLogger.info("\t - {}: {}", jsp.getName(), jsp.getPropertyValue()));
        }

        JobExecution jobExecution;
        if (jobContextWrapper.getResumedJobExecutionId() != null) {
            jobLogger.info("Resuming job execution...");
            try {
                jobExecution = KapuaSecurityUtils.doPrivileged(() -> jobExecutionService.find(jobContextWrapper.getScopeId(), jobContextWrapper.getResumedJobExecutionId()));
            } catch (Exception e) {
                jobLogger.error(e, "Resuming job execution... ERROR!");
                throw e;
            }
            jobLogger.info("Resuming job execution... DONE!");
        } else {
            jobLogger.info("Creating job execution...");
            try {
                jobExecution = createJobExecution(
                        jobContextWrapper.getScopeId(),
                        jobContextWrapper.getJobId(),
                        jobContextWrapper.getTargetSublist(),
                        jobContextWrapper.getExecutionId());
            } catch (Exception e) {
                jobLogger.error(e, "Creating job execution... ERROR!");
                throw e;
            }
            jobLogger.info("Creating job execution... DONE!");
        }

        jobLogger.setJobExecutionId(jobExecution.getId());

        jobContextWrapper.setKapuaExecutionId(jobExecution.getId());

        JobExecution runningJobExecution = getAnotherJobExecutionRunning(
                jobExecution.getScopeId(),
                jobExecution.getJobId(),
                jobExecution.getId(),
                jobContextWrapper.getJobName(),
                jobExecution.getTargetIds());

        if (runningJobExecution != null) {

            if (jobContextWrapper.getEnqueue()) {
                jobLogger.warn("Another execution is running! Enqueuing this execution...");

                QueuedJobExecution queuedJobExecution;
                try {
                    queuedJobExecution = enqueueJobExecution(
                            jobExecution.getScopeId(),
                            jobExecution.getJobId(),
                            jobExecution.getId(),
                            runningJobExecution.getId());
                } catch (Exception e) {
                    jobLogger.error(e, "Another execution is running! Enqueuing this execution... ERROR!");
                    throw e;
                }

                KapuaSecurityUtils.doPrivileged(() -> jbatchDriver.stopJob(jobExecution.getScopeId(), jobExecution.getJobId(), jobExecution.getId()));
                jobLogger.warn("Another execution is running! Stopping and enqueuing this execution... DONE! EnqueuedJob id : {}", queuedJobExecution.getJobId());

            } else {
                jobLogger.error("Another execution is running! Aborting this execution...");
                throw new JobAlreadyRunningException(jobExecution.getScopeId(), jobExecution.getJobId(), jobExecution.getId(), jobExecution.getTargetIds());
            }
        }

        if (jobContextWrapper.getResetStepIndex()) {
            int resetToStepIndex = jobContextWrapper.getFromStepIndex() != null ? jobContextWrapper.getFromStepIndex() : 0;

            try {
                jobLogger.info("Resetting {} targets to step index: {}...", jobExecution.getTargetIds().size(), resetToStepIndex);

                for (KapuaId jobTargetId : jobExecution.getTargetIds()) {
                    JobTarget jobTarget = KapuaSecurityUtils.doPrivileged(() -> jobTargetService.find(jobExecution.getScopeId(), jobTargetId));

                    jobTarget.setStepIndex(resetToStepIndex);
                    jobTarget.setStatus(JobTargetStatus.PROCESS_AWAITING);
                    jobTarget.setStatusMessage(null);
                    jobTarget.setException(null);

                    KapuaSecurityUtils.doPrivileged(() -> jobTargetService.update(jobTarget));
                }
            } catch (KapuaException e) {
                jobLogger.error(e, "Resetting {} targets to step index: {}... ERROR!", jobExecution.getTargetIds().size(), resetToStepIndex);
            }
            jobLogger.info("Resetting {} targets to step index: {}... DONE!", jobExecution.getTargetIds().size(), resetToStepIndex);
        } else {
            for (KapuaId jobTargetId : jobExecution.getTargetIds()) {
                JobTarget jobTarget = KapuaSecurityUtils.doPrivileged(() -> jobTargetService.find(jobExecution.getScopeId(), jobTargetId));
                    if (jobTarget.getStatus().equals(JobTargetStatus.PROCESS_FAILED)) {
                        jobTarget.setStatus(JobTargetStatus.PROCESS_AWAITING); //TODO: TO CHANGE ONLY WHEN STEP IS SYNCRONOUS, OTHERWISE BUG
                        jobTarget.setStatusMessage(null);
                        jobTarget.setException(null);
                        KapuaSecurityUtils.doPrivileged(() -> jobTargetService.update(jobTarget));
                    }
            }
        }

        jobLogger.info("Running before job... DONE!");
    }

    /**
     * Close the {@link JobExecution} setting the {@link JobExecution#getEndedOn()}.
     */
    @Override
    public void afterJob() throws Exception {
        JobContextWrapper jobContextWrapper = new JobContextWrapper(jobContext);
        JobLogger jobLogger = jobContextWrapper.getJobLogger();

        jobLogger.info("Running after job...");

        KapuaId kapuaExecutionId = jobContextWrapper.getKapuaExecutionId();
        if (kapuaExecutionId == null) {
            LOG.error("Cannot update job execution (internal reference [{}]). Cannot find 'executionId' in JobContext", jobContextWrapper.getExecutionId());
            // Don't send any exception to prevent the job engine to set the job exit status as failed!
        } else {
            JobExecution jobExecution = KapuaSecurityUtils.doPrivileged(() -> jobExecutionService.find(jobContextWrapper.getScopeId(), kapuaExecutionId));

            if (jobExecution != null) {
                jobExecution.setLog(jobLogger.flush());
                jobExecution.setEndedOn(new Date());

                KapuaSecurityUtils.doPrivileged(() -> jobExecutionService.update(jobExecution));

                checkQueuedJobExecutions(
                        jobContextWrapper.getScopeId(),
                        jobContextWrapper.getJobId(),
                        jobContextWrapper.getKapuaExecutionId());
            } else {
                LOG.warn("Cannot find job execution with id: {}. This is likely to happen with the Job has been forcibly deleted.", kapuaExecutionId);
            }
        }
        jobLogger.info("Running after job... DONE!");
    }

    /**
     * Creates the {@link JobExecution} from the data in the {@link JobContextWrapper}.
     * <p>
     * If the {@link org.eclipse.kapua.service.job.Job} has started without a defined set of {@link JobStartOptions#getTargetIdSublist()}
     * all {@link org.eclipse.kapua.service.job.targets.JobTarget}s will be added to the {@link JobExecution#getTargetIds()}.
     *
     * @param scopeId           The {@link Job#getScopeId()}
     * @param jobId             The {@link Job#getId()}
     * @param jobTargetSublist  The {@link JobStartOptions#getTargetIdSublist()} of the targeted items
     * @param jBatchExecutionId The {@link JobContext#getExecutionId()}
     * @return The newly created {@link JobExecution}
     * @throws KapuaException If any error happens during the processing
     * @since 1.1.0
     */
    private JobExecution createJobExecution(KapuaId scopeId, KapuaId jobId, JobTargetSublist jobTargetSublist, Long jBatchExecutionId) throws KapuaException {

        Properties jobExecutionProperties = new Properties();
        jobExecutionProperties.put(JBATCH_EXECUTION_ID, Long.toString(jBatchExecutionId));

        JobExecutionCreator jobExecutionCreator = jobExecutionFactory.newCreator(scopeId);

        jobExecutionCreator.setJobId(jobId);
        jobExecutionCreator.setStartedOn(new Date());
        jobExecutionCreator.setEntityAttributes(jobExecutionProperties);

        if (jobTargetSublist.isEmpty()) {
            JobTargetQuery jobTargetQuery = jobTargetFactory.newQuery(scopeId);

            jobTargetQuery.setPredicate(
                    jobTargetQuery.andPredicate(
                            jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_ID, jobId)
                    )
            );

            JobTargetListResult jobTargets = KapuaSecurityUtils.doPrivileged(() -> jobTargetService.query(jobTargetQuery));

            Set<KapuaId> targetIds = new HashSet<>();
            jobTargets.getItems().forEach(jt -> targetIds.add(jt.getId()));

            jobExecutionCreator.setTargetIds(new HashSet<>(targetIds));
        } else {
            jobExecutionCreator.setTargetIds(jobTargetSublist.getTargetIds());
        }

        return KapuaSecurityUtils.doPrivileged(() -> jobExecutionService.create(jobExecutionCreator));
    }

    /**
     * Checks if there are other {@link JobExecution}s running in this moment.
     * <p>
     * First it checks for an execution running from the {@link BatchRuntime}.
     * This will return the jBatch execution ids that are currently active.
     * <p>
     * If 0, no {@link JobExecution} is currently running, returns {@code null}.
     * <p>
     * If greater than 0, it checks if the running {@link JobExecution} has a subset of {@link org.eclipse.kapua.service.job.targets.JobTarget}s compatible with the current {@link JobTargetSublist}.
     * If the current {@link JobTargetSublist} doesn't match {@link org.eclipse.kapua.service.job.targets.JobTarget}s of any other running JobExecution, returns the current running {@link JobExecution}.
     * <p>
     * In other all other cases returns {@code null}.
     *
     * @param scopeId               The current {@link JobExecution#getScopeId()}.
     * @param jobId                 The current {@link JobExecution#getJobId()}.
     * @param currentJobExecutionId The current {@link JobExecution#getId()}.
     * @param jobName               The current {@link JobContextWrapper#getJobName()}.
     * @param jobTargetIdSubset     The current {@link JobExecution#getTargetIds()} }.
     * @return The other running {@link JobExecution} or {@code null} if there is no other running {@link JobExecution}.
     * @throws KapuaException If any error happens during the processing.
     * @since 1.1.0
     */
    private JobExecution getAnotherJobExecutionRunning(KapuaId scopeId, KapuaId jobId, KapuaId currentJobExecutionId, String jobName, Set<KapuaId> jobTargetIdSubset) throws KapuaException {
        List<Long> runningExecutionsIds = BatchRuntime.getJobOperator().getRunningExecutions(jobName);
        if (runningExecutionsIds.size() > 1) {

            JobExecutionQuery jobExecutionQuery = jobExecutionFactory.newQuery(scopeId);

            jobExecutionQuery.setPredicate(
                    jobExecutionQuery.andPredicate(
                            jobExecutionQuery.attributePredicate(JobExecutionAttributes.JOB_ID, jobId),
                            jobExecutionQuery.attributePredicate(JobExecutionAttributes.ENTITY_ID, currentJobExecutionId, AttributePredicate.Operator.NOT_EQUAL),
                            jobExecutionQuery.attributePredicate(JobExecutionAttributes.ENDED_ON, null),
                            jobExecutionQuery.attributePredicate(JobExecutionAttributes.TARGET_IDS, jobTargetIdSubset)
                    )
            );

            jobExecutionQuery.setSortCriteria(jobExecutionQuery.fieldSortCriteria(JobExecutionAttributes.STARTED_ON, SortOrder.ASCENDING));

            JobExecutionListResult jobExecutions = KapuaSecurityUtils.doPrivileged(() -> jobExecutionService.query(jobExecutionQuery));

            return jobExecutions.getFirstItem();
        }

        return null;
    }

    /**
     * Creates a new {@link QueuedJobExecution} when the current {@link JobExecution} cannot be started
     * due to another running {@link JobExecution} and the {@link JobStartOptions#getEnqueue()} is {@code true}.
     *
     * @param scopeId               The current {@link JobExecution#getScopeId()}
     * @param jobId                 The current {@link JobExecution#getJobId()}
     * @param jobExecutionId        The current {@link JobExecution#getId()}
     * @param runningJobExecutionId The current running {@link JobExecution#getId()}
     * @return The newly created {@link QueuedJobExecution}
     * @throws KapuaException If any error happens during the processing.
     */
    private QueuedJobExecution enqueueJobExecution(KapuaId scopeId, KapuaId jobId, KapuaId jobExecutionId, KapuaId runningJobExecutionId) throws KapuaException {

        QueuedJobExecutionCreator queuedJobExecutionCreator = queuedJobExecutionFactory.newCreator(scopeId);
        queuedJobExecutionCreator.setJobId(jobId);
        queuedJobExecutionCreator.setJobExecutionId(jobExecutionId);
        queuedJobExecutionCreator.setWaitForJobExecutionId(runningJobExecutionId);
        queuedJobExecutionCreator.setStatus(QueuedJobExecutionStatus.QUEUED);

        return KapuaSecurityUtils.doPrivileged(() -> queuedJobExecutionService.create(queuedJobExecutionCreator));
    }

    private void checkQueuedJobExecutions(KapuaId scopeId, KapuaId jobId, KapuaId jobExecutionId) {

        String timerName = new StringBuilder()
                .append(KapuaJobListener.class.getSimpleName())
                .append("-DelayedCheckTimer-")
                .append(jobExecutionId)
                .toString();

        Timer queuedCheckTimer = new Timer(timerName, true);

        queuedCheckTimer.schedule(queuedJobExecutionCheckTaskFactory.create(scopeId, jobId, jobExecutionId), jobEngineSetting.getLong(JobEngineSettingKeys.JOB_ENGINE_QUEUE_CHECK_DELAY));
    }
}
