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
package org.eclipse.kapua.job.engine.commons.operation;

import org.eclipse.kapua.KapuaOptimisticLockingException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.job.engine.commons.logger.JobLogger;
import org.eclipse.kapua.job.engine.commons.wrappers.JobContextWrapper;
import org.eclipse.kapua.job.engine.commons.wrappers.JobTargetWrapper;
import org.eclipse.kapua.job.engine.commons.wrappers.StepContextWrapper;
import org.eclipse.kapua.model.id.KapuaIdFactory;
import org.eclipse.kapua.service.job.operation.TargetWriter;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import java.util.List;

/**
 * Default {@link TargetWriter} implementation.
 * <p>
 * All {@link org.eclipse.kapua.service.job.step.definition.JobStepDefinition} can use this {@link TargetWriter} implementation or extend or provide one on their own.
 *
 * @since 1.0.0
 */
public class DefaultTargetWriter extends AbstractItemWriter implements TargetWriter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTargetWriter.class);

    @Inject
    private JobTargetService jobTargetService;
    @Inject
    KapuaIdFactory kapuaIdFactory;
    @Inject
    JobContext jobContext;
    @Inject
    StepContext stepContext;

    @Override
    public void writeItems(List<Object> items) throws Exception {

        JobContextWrapper jobContextWrapper = new JobContextWrapper(jobContext);
        StepContextWrapper stepContextWrapper = new StepContextWrapper(kapuaIdFactory, stepContext);

        JobLogger jobLogger = jobContextWrapper.getJobLogger();
        jobLogger.setClassLog(LOG);

        int stepIndex = stepContextWrapper.getStepIndex();
        String stepName = stepContextWrapper.getKapuaStepName();

        jobLogger.info("Writing chunk results. Step:{} (index:{})...", stepName, stepIndex);

        for (Object item : items) {
            JobTargetWrapper processedWrappedJobTarget = (JobTargetWrapper) item;
            JobTarget processedJobTarget = processedWrappedJobTarget.getJobTarget();

            JobTarget jobTarget = KapuaSecurityUtils.doPrivileged(() -> jobTargetService.find(processedJobTarget.getScopeId(), processedJobTarget.getId()));

            if (jobTarget == null) {
                jobLogger.warn("Target {} has not been found. Likely the target or job has been deleted when it was running... Status was: {}", processedJobTarget.getId(), processedJobTarget.getStatus());
                continue;
            }

            jobTarget.setStepIndex(stepContextWrapper.getStepIndex());
            jobTarget.setStatus(processedJobTarget.getStatus());
            jobTarget.setStatusMessage(processedWrappedJobTarget.getProcessingException() != null ? processedWrappedJobTarget.getProcessingException().getMessage() : null);
            jobTarget.setOptlock(processedJobTarget.getOptlock());

            if (JobTargetStatus.PROCESS_OK.equals(jobTarget.getStatus())) {

                if (stepContextWrapper.getNextStepIndex() != null) {
                    jobTarget.setStepIndex(stepContextWrapper.getNextStepIndex());
                    jobTarget.setStatus(JobTargetStatus.PROCESS_AWAITING);
                } else {
                    jobTarget.setStatus(JobTargetStatus.PROCESS_OK);
                }
            }

            try {
                KapuaSecurityUtils.doPrivileged(() -> jobTargetService.update(jobTarget));
            } catch (KapuaOptimisticLockingException kole) {
                LOG.warn("Target {} has been updated by another component! Status was: {}. Error: {}", jobTarget.getId(), jobTarget.getStatus(), kole.getMessage());
            }
        }

        jobLogger.info("Writing chunk results. Step:{} (index:{})... DONE!", stepName, stepIndex);
    }
}
