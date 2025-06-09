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
package org.eclipse.kapua.job.engine.jbatch.listener;

import java.util.TimerTask;

import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.jbatch.setting.JobEngineSetting;
import org.eclipse.kapua.job.engine.jbatch.setting.JobEngineSettingKeys;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecution;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionAttributes;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionFactory;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionListResult;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionService;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionStatus;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueuedJobExecutionCheckTask extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(QueuedJobExecutionCheckTask.class);
    private final JobEngineSetting jobEngineSetting;
    private final JobEngineService jobEngineService;
    private final QueuedJobExecutionService queuedJobExecutionService;
    private final QueuedJobExecutionFactory queuedJobExecutionFactory;
    private final KapuaId scopeId;
    private final KapuaId jobId;
    private final KapuaId jobExecutionId;

    protected QueuedJobExecutionCheckTask(
            JobEngineSetting jobEngineSetting,
            JobEngineService jobEngineService,
            QueuedJobExecutionService queuedJobExecutionService,
            QueuedJobExecutionFactory queuedJobExecutionFactory,
            KapuaId scopeId,
            KapuaId jobId,
            KapuaId jobExecutionId) {
        this.jobEngineSetting = jobEngineSetting;
        this.jobEngineService = jobEngineService;
        this.queuedJobExecutionService = queuedJobExecutionService;
        this.queuedJobExecutionFactory = queuedJobExecutionFactory;
        this.scopeId = scopeId;
        this.jobId = jobId;
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public void run() {
        LOG.info("Checking Job Execution queue for: {}...", jobExecutionId);

        try {
            KapuaQuery query = new KapuaQuery(scopeId);

            query.setPredicate(
                    new AndPredicate(
                            new AttributePredicate<>(QueuedJobExecutionAttributes.JOB_ID, jobId),
                            new AttributePredicate<>(QueuedJobExecutionAttributes.WAIT_FOR_JOB_EXECUTION_ID, jobExecutionId)
                    )
            );

            QueuedJobExecutionListResult queuedJobExecutions = KapuaSecurityUtils.doPrivileged(() -> queuedJobExecutionService.query(query));

            int i = 0;
            int failedToResumeExecution = 0;
            for (QueuedJobExecution qje : queuedJobExecutions.getItems()) {
                Thread.sleep(jobEngineSetting.getInt(JobEngineSettingKeys.JOB_ENGINE_QUEUE_PROCESSING_RUN_DELAY));

                LOG.info("Resuming Job Execution ({}/{}): {}...", ++i, queuedJobExecutions.getSize(), qje.getJobExecutionId());

                try {
                    KapuaSecurityUtils.doPrivileged(() -> jobEngineService.resumeJobExecution(qje.getScopeId(), qje.getJobId(), qje.getJobExecutionId()));

                    qje.setStatus(QueuedJobExecutionStatus.PROCESSED);
                    KapuaSecurityUtils.doPrivileged(() -> queuedJobExecutionService.update(qje));
                } catch (Exception e) {
                    LOG.error("Resuming Job Execution ({}/{}): {}... ERROR!", i, queuedJobExecutions.getSize(), qje.getJobExecutionId(), e);
                    failedToResumeExecution++;

                    qje.setStatus(QueuedJobExecutionStatus.FAILED_TO_RESUME);
                    KapuaSecurityUtils.doPrivileged(() -> queuedJobExecutionService.update(qje));
                    continue;
                }

                LOG.info("Resuming Job Execution ({}/{}): {}... DONE!", i, queuedJobExecutions.getSize(), qje.getJobExecutionId());
            }

            LOG.info("Checking Job Execution queue for: {}... DONE! Queued job failed to resume: {}.", jobExecutionId, failedToResumeExecution);
        } catch (Exception e) {
            LOG.error("Checking Job Execution queue for: {}... ERROR!", jobExecutionId, e);
        }
    }
}
