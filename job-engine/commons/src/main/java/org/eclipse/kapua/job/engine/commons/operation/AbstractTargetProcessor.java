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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.job.engine.commons.logger.JobLogger;
import org.eclipse.kapua.job.engine.commons.wrappers.JobContextWrapper;
import org.eclipse.kapua.job.engine.commons.wrappers.JobTargetWrapper;
import org.eclipse.kapua.job.engine.commons.wrappers.StepContextWrapper;
import org.eclipse.kapua.model.id.KapuaIdFactory;
import org.eclipse.kapua.service.job.operation.TargetProcessor;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

/**
 * {@link TargetProcessor} {@code abstract} implementation.
 * <p>
 * All {@link org.eclipse.kapua.service.job.step.definition.JobStepDefinition} must provide their own implementation of the {@link TargetProcessor}
 * containing the actual processing logic of the {@link JobTarget}
 *
 * @since 1.0.0
 */
public abstract class AbstractTargetProcessor implements TargetProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTargetProcessor.class);

    protected JobContextWrapper jobContextWrapper;
    protected StepContextWrapper stepContextWrapper;
    @Inject
    KapuaIdFactory kapuaIdFactory;

    @Override
    public final Object processItem(Object item) throws Exception {
        JobTargetWrapper wrappedJobTarget = (JobTargetWrapper) item;

        initProcessing(wrappedJobTarget);

        JobLogger jobLogger = jobContextWrapper.getJobLogger();
        jobLogger.setClassLog(LOG);

        JobTarget jobTarget = wrappedJobTarget.getJobTarget();
        jobLogger.info("Processing target:{} (id:{})", getTargetDisplayName(jobTarget), jobTarget.getId().toCompactId());
        try {
            processTarget(jobTarget);

            jobTarget.setStatus(getCompletedStatus(jobTarget));

            jobLogger.info("Processing target:{} (id:{}) - DONE!", getTargetDisplayName(jobTarget), jobTarget.getId().toCompactId());
        } catch (Exception e) {
            logErrorToJobLogger(stepContextWrapper.getKapuaStepName(), jobLogger, jobTarget, e);
            jobTarget.setStatus(getFailedStatus(jobTarget));
            wrappedJobTarget.setProcessingException(e);
        }

        return wrappedJobTarget;
    }


    /**
     * Prints an error message into the given job logger
     *
     * @param stepName  The name of the step that fails
     * @param jobLogger The {@link JobLogger} where to print the error message
     * @param jobTarget The {@link JobTarget} that was being processed
     * @param e         The exception that occurred
     * @throws KapuaException
     */
    protected void logErrorToJobLogger(String stepName, JobLogger jobLogger, JobTarget jobTarget, Exception e) throws KapuaException {
        jobLogger.error(e, "Executing {} on device: {} (id: {}) - ", stepName, getTargetDisplayName(jobTarget), jobTarget.getId().toCompactId());
    }


    protected abstract String getTargetDisplayName(JobTarget jobTarget) throws KapuaException;

    /**
     * Actions before {@link #processTarget(JobTarget)} invocation.
     *
     * @param wrappedJobTarget The current {@link JobTargetWrapper}
     * @since 1.1.0
     */
    protected abstract void initProcessing(JobTargetWrapper wrappedJobTarget);

    /**
     * Action of the actual processing of the {@link JobTarget}.
     *
     * @param jobTarget The current {@link JobTarget}
     * @throws KapuaException in case of exceptions during the processing.
     * @since 1.0.0
     */
    protected abstract void processTarget(JobTarget jobTarget) throws KapuaException;

    protected JobTargetStatus getCompletedStatus(JobTarget jobTarget) {
        return JobTargetStatus.PROCESS_OK;
    }

    protected JobTargetStatus getFailedStatus(JobTarget jobTarget) {
        return JobTargetStatus.PROCESS_FAILED;
    }

    /**
     * Sets {@link #jobContextWrapper} and {@link #stepContextWrapper} wrapping the given {@link JobContext} and the {@link StepContext}.
     *
     * @param jobContext  The {@code inject}ed {@link JobContext}.
     * @param stepContext The {@code inject}ed {@link StepContext}.
     * @since 1.0.0
     */
    protected void setContext(JobContext jobContext, StepContext stepContext) {
        jobContextWrapper = new JobContextWrapper(jobContext);
        stepContextWrapper = new StepContextWrapper(kapuaIdFactory, stepContext);
    }
}
