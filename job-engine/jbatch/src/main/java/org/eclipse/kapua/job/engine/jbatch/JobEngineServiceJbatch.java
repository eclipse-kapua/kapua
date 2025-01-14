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
package org.eclipse.kapua.job.engine.jbatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.exception.CleanJobDataException;
import org.eclipse.kapua.job.engine.exception.JobCheckRunningException;
import org.eclipse.kapua.job.engine.exception.JobInvalidTargetException;
import org.eclipse.kapua.job.engine.exception.JobMissingStepException;
import org.eclipse.kapua.job.engine.exception.JobMissingTargetException;
import org.eclipse.kapua.job.engine.exception.JobNotRunningException;
import org.eclipse.kapua.job.engine.exception.JobResumingException;
import org.eclipse.kapua.job.engine.exception.JobRunningException;
import org.eclipse.kapua.job.engine.exception.JobStartingException;
import org.eclipse.kapua.job.engine.exception.JobStoppingException;
import org.eclipse.kapua.job.engine.jbatch.driver.JbatchDriver;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetService;

@Singleton
public class JobEngineServiceJbatch implements JobEngineService {

    @Inject
    private AuthorizationService authorizationService;
    @Inject
    private PermissionFactory permissionFactory;
    @Inject
    private JobService jobService;
    @Inject
    private JobExecutionService jobExecutionService;
    @Inject
    private JobStepService jobStepService;
    @Inject
    private JobStepFactory jobStepFactory;
    @Inject
    private JobTargetService jobTargetService;
    @Inject
    private JobTargetFactory jobTargetFactory;
    @Inject
    private JbatchDriver jbatchDriver;
    private static final String JOB_EXECUTION_ID = "jobExecutionId";

    @Override
    public void startJob(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        startJob(scopeId, jobId, new JobStartOptionsImpl());
    }

    @Override
    public void startJob(KapuaId scopeId, KapuaId jobId, JobStartOptions jobStartOptions) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
        ArgumentValidator.notNull(jobStartOptions, "jobStartOptions");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.execute, scopeId));
        // Check Job Existence
        Job job = jobService.find(scopeId, jobId);
        if (job == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
        }
        // Check job targets
        KapuaQuery jobTargetQuery = new KapuaQuery(scopeId);
        jobTargetQuery.setPredicate(jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_ID, jobId));
        if (jobTargetService.count(jobTargetQuery) <= 0) {
            throw new JobMissingTargetException(scopeId, jobId);
        }
        // Check job target sublist
        if (!jobStartOptions.getTargetIdSublist().isEmpty()) {
            jobTargetQuery.setPredicate(
                    jobTargetQuery.andPredicate(
                            jobTargetQuery.getPredicate(),
                            jobTargetQuery.attributePredicate(KapuaEntityAttributes.ENTITY_ID, jobStartOptions.getTargetIdSublist())
                    )
            );

            if (jobStartOptions.getTargetIdSublist().size() != jobTargetService.count(jobTargetQuery)) {
                throw new JobInvalidTargetException(scopeId, jobId, jobStartOptions.getTargetIdSublist());
            }
        }
        // Check job steps
        KapuaQuery jobStepQuery = new KapuaQuery(scopeId);
        jobStepQuery.setPredicate(jobStepQuery.attributePredicate(JobStepAttributes.JOB_ID, jobId));
        if (jobStepService.count(jobStepQuery) <= 0) {
            throw new JobMissingStepException(scopeId, jobId);
        }
        // Start the job
        try {
            jbatchDriver.startJob(scopeId, jobId, jobStartOptions);
        } catch (Exception e) {
            throw new JobStartingException(e, scopeId, jobId);
        }
    }

    @Override
    public boolean isRunning(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, scopeId));

        return internalIsRunning(scopeId, jobId);
    }

    @Override
    public Map<KapuaId, Boolean> isRunning(KapuaId scopeId, Set<KapuaId> jobIds) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, scopeId));

        Map<KapuaId, Boolean> isRunningMap = new HashMap<>();
        jobIds.forEach(jobId -> {
            try {
                ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
                isRunningMap.put(jobId, internalIsRunning(scopeId, jobId));
            } catch (KapuaException kapuaException) {
                // No other way to report an error?
                isRunningMap.put(jobId, null);
            }
        });
        return isRunningMap;
    }

    @Override
    public void stopJob(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.execute, scopeId));
        // Check existence
        Job job = jobService.find(scopeId, jobId);
        if (job == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
        }
        // Check job running
        if (!jbatchDriver.isRunningJob(scopeId, jobId)) {
            throw new JobNotRunningException(scopeId, jobId);
        }
        // Stop the job
        try {
            jbatchDriver.stopJob(scopeId, jobId, null);
        } catch (Exception e) {
            throw new JobStoppingException(e, scopeId, jobId);
        }
    }

    @Override
    public void stopJobExecution(KapuaId scopeId, KapuaId jobId, KapuaId jobExecutionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
        ArgumentValidator.notNull(jobExecutionId, JOB_EXECUTION_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.execute, scopeId));
        // Check existence
        Job job = jobService.find(scopeId, jobId);
        if (job == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
        }
        // Check execution existence
        JobExecution jobExecution = jobExecutionService.find(scopeId, jobExecutionId);
        if (jobExecution == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
        }
        // Check that JobExecution belongs to the Job
        if (!jobExecution.getJobId().equals(jobId)) {
            throw new KapuaIllegalArgumentException(JOB_EXECUTION_ID, jobExecutionId.toString());
        }
        // Stop the JobExecution
        try {
            jbatchDriver.stopJob(scopeId, jobId, jobExecutionId);
        } catch (Exception e) {
            throw new JobStoppingException(e, scopeId, jobId, jobExecutionId);
        }

    }

    @Override
    public void resumeJobExecution(KapuaId scopeId, KapuaId jobId, KapuaId jobExecutionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
        ArgumentValidator.notNull(jobExecutionId, JOB_EXECUTION_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.execute, scopeId));
        // Check existence
        Job job = jobService.find(scopeId, jobId);
        if (job == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
        }
        // Check execution existence
        JobExecution jobExecution = jobExecutionService.find(scopeId, jobExecutionId);

        if (jobExecution == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
        }
        // Check that JobExecution belongs to the Job
        if (!jobExecution.getJobId().equals(jobId)) {
            throw new KapuaIllegalArgumentException(JOB_EXECUTION_ID, jobExecutionId.toString());
        }
        // Resume the JobExecution
        try {
            jbatchDriver.resumeJob(scopeId, jobId, jobExecutionId);
        } catch (Exception e) {
            throw new JobResumingException(e, scopeId, jobId, jobExecutionId);
        }
    }

    @Override
    public void cleanJobData(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.delete, null));
        // Check existence
        Job job = jobService.find(scopeId, jobId);
        if (job == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
        }
        // Check job not running
        if (jbatchDriver.isRunningJob(scopeId, jobId)) {
            throw new JobRunningException(scopeId, jobId);
        }

        try {
            jbatchDriver.cleanJobData(scopeId, jobId);
        } catch (Exception ex) {
            throw new CleanJobDataException(ex, scopeId, jobId);
        }
    }
    // Private methods

    /**
     * Using the {@link JbatchDriver} checks whether the {@link Job} is running.
     *
     * @param scopeId
     *         The {@link Job#getScopeId()}.
     * @param jobId
     *         The {@link Job#getId()}.
     * @return {@code true} if {@link JbatchDriver} reports that is running, {@code false} otherwise.
     * @throws JobCheckRunningException
     *         if {@link Job} running status cannot be checked.
     * @throws KapuaEntityNotFoundException
     *         if {@link Job} does not exists.
     * @throws KapuaException
     *         if any other error occurs.
     * @since 1.5.0
     */
    private boolean internalIsRunning(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        // Check existence
        Job job = jobService.find(scopeId, jobId);
        if (job == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
        }
        // Do check running
        try {
            return jbatchDriver.isRunningJob(scopeId, jobId);
        } catch (Exception e) {
            throw new JobCheckRunningException(e, scopeId, jobId);
        }
    }
}
