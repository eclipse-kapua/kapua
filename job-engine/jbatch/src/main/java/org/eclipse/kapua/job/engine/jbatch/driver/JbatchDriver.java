/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.jbatch.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.commons.wrappers.JobContextPropertyNames;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.CannotBuildJobDefDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.CannotCleanJobDefFileDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.CannotCreateTmpDirDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.CannotWriteJobDefFileDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.CleanJobDataDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.ExecutionNotFoundDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.ExecutionNotRunningDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.JbatchDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.JobExecutionIsRunningDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.exception.JobStartingDriverException;
import org.eclipse.kapua.job.engine.jbatch.driver.utils.JobDefinitionBuildUtils;
import org.eclipse.kapua.job.engine.jbatch.persistence.JPAPersistenceManagerImpl;
import org.eclipse.kapua.job.engine.jbatch.setting.JobEngineSettingKeys;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.jbatch.container.jsl.ExecutionElement;
import com.ibm.jbatch.container.jsl.ModelSerializerFactory;
import com.ibm.jbatch.container.servicesmanager.ServicesManagerImpl;
import com.ibm.jbatch.jsl.model.JSLJob;
import com.ibm.jbatch.jsl.model.Step;

/**
 * Driver class for Java Batch API
 *
 * @since 1.0.0
 */
public class JbatchDriver {

    private static final Logger LOG = LoggerFactory.getLogger(JbatchDriver.class);

    private static final String JBATCH_EXECUTION_ID = "JBATCH_EXECUTION_ID";

    private final JobOperator jobOperator;

    private final JobExecutionService jobExecutionService;

    private final JobStepService jobStepService;
    private final JobStepFactory jobStepFactory;

    @Inject
    public JbatchDriver(JobOperator jobOperator, JobExecutionService jobExecutionService, JobStepService jobStepService, JobStepFactory jobStepFactory,
            JobStepDefinitionService jobStepDefinitionService, JobDefinitionBuildUtils jobDefinitionBuildUtils) {
        this.jobOperator = jobOperator;
        this.jobExecutionService = jobExecutionService;
        this.jobStepService = jobStepService;
        this.jobStepFactory = jobStepFactory;
        this.jobStepDefinitionService = jobStepDefinitionService;
        this.jobDefinitionBuildUtils = jobDefinitionBuildUtils;
    }

    private final JobStepDefinitionService jobStepDefinitionService;
    private final JobDefinitionBuildUtils jobDefinitionBuildUtils;

    /**
     * Builds the jBatch job name from the {@link Job#getScopeId()} and the {@link Job#getId()}.
     * <p>
     * Format is: job-{scopeIdShort}-{jobIdShort}
     *
     * @param scopeId
     *         The scopeId of the {@link Job}
     * @param jobId
     *         The id of the {@link Job}
     * @return The jBatch {@link Job} name
     * @since 1.0.0
     */
    public static String getJbatchJobName(@NotNull KapuaId scopeId, @NotNull KapuaId jobId) {
        return String.format("job-%s-%s", scopeId.toCompactId(), jobId.toCompactId());
    }

    /**
     * Starts a jBatch job with data sourced from the Kapua {@link Job} definition.
     * <p>
     * It builds the XML jBatch job definition using the {@link JSLJob} model definition. The generated XML is store in the {@link SystemUtils#getJavaIoTmpDir()} since the default configuration of
     * jBatch requires a path name to start the jBatch job
     *
     * @param scopeId
     *         The scopeId of the {@link Job}
     * @param jobId
     *         The id of the {@link Job}
     * @param jobStartOptions
     *         The {@link JobStartOptions} for this start {@link org.eclipse.kapua.service.job.Job} request.
     * @throws CannotBuildJobDefDriverException
     *         if the creation of the {@link JSLJob} fails
     * @throws CannotCreateTmpDirDriverException
     *         if the temp directory for storing the XML job definition file cannot be created
     * @throws CannotCleanJobDefFileDriverException
     *         if the XML job definition file cannot be deleted, when existing
     * @throws CannotWriteJobDefFileDriverException
     *         if the XML job definition file cannot be created and written in the tmp directory
     * @throws JobExecutionIsRunningDriverException
     *         if the jBatch job has another {@link JobExecution} running
     * @throws JobStartingDriverException
     *         if invoking {@link JobOperator#start(String, Properties)} throws an {@link Exception}
     * @since 1.0.0
     */
    public void startJob(@NotNull KapuaId scopeId, @NotNull KapuaId jobId, @NotNull JobStartOptions jobStartOptions)
            throws JbatchDriverException {

        String jobXmlDefinition;
        String jobName = JbatchDriver.getJbatchJobName(scopeId, jobId);
        try {
            KapuaQuery query = new KapuaQuery(scopeId);
            query.setPredicate(query.attributePredicate(JobStepAttributes.JOB_ID, jobId));

            JobStepListResult jobSteps = jobStepService.query(query);
            jobSteps.sort(Comparator.comparing(JobStep::getStepIndex));

            List<ExecutionElement> jslExecutionElements = new ArrayList<>();
            Iterator<JobStep> jobStepIterator = jobSteps.getItems().iterator();
            while (jobStepIterator.hasNext()) {
                //TODO: #LAYER_VIOLATION - N+1 query would be better handled at a lower layer

                JobStep jobStep = jobStepIterator.next();

                Step jslStep = new Step();
                JobStepDefinition jobStepDefinition = jobStepDefinitionService.find(KapuaId.ANY, jobStep.getJobStepDefinitionId());
                switch (jobStepDefinition.getStepType()) {
                case GENERIC:
                    jslStep.setBatchlet(JobDefinitionBuildUtils.buildGenericStep(jobStepDefinition));
                    break;
                case TARGET:
                    jslStep.setChunk(JobDefinitionBuildUtils.buildChunkStep(jobStepDefinition));
                    break;
                default:
                    throw new KapuaIllegalArgumentException(jobStepDefinition.getStepType().name(), "jobStepDefinition.stepType");
                }

                jslStep.setId("step-" + jobStep.getStepIndex());

                if (jobStepIterator.hasNext()) {
                    jslStep.setNextFromAttribute("step-" + (jobStep.getStepIndex() + 1));
                }

                jslStep.setProperties(JobDefinitionBuildUtils.buildStepProperties(jobStepDefinition, jobStep, jobStepIterator.hasNext(), jobStartOptions.getStepPropertiesOverrides()));

                jslExecutionElements.add(jslStep);
            }

            JSLJob jslJob = new JSLJob();
            jslJob.setRestartable("true");
            jslJob.setId(jobName);
            jslJob.setVersion("1.0");
            jslJob.setProperties(jobDefinitionBuildUtils.buildJobProperties(scopeId, jobId, jobStartOptions));
            jslJob.setListeners(jobDefinitionBuildUtils.buildListener());
            jslJob.getExecutionElements().addAll(jslExecutionElements);

            jobXmlDefinition = ModelSerializerFactory.createJobModelSerializer().serializeModel(jslJob);
        } catch (Exception e) {
            throw new CannotBuildJobDefDriverException(e, jobName);
        }
        // Retrieve temporary directory for job XML definition
        String tmpDirectory = SystemUtils.getJavaIoTmpDir().getAbsolutePath();
        File jobTempDirectory = new File(tmpDirectory, "kapua-job/" + scopeId.toCompactId());
        if (!jobTempDirectory.exists() && !jobTempDirectory.mkdirs()) {
            throw new CannotCreateTmpDirDriverException(jobName, jobTempDirectory.getAbsolutePath());
        }
        // Retrieve job XML definition file. Delete it if exist
        File jobXmlDefinitionFile = new File(jobTempDirectory, jobId.toCompactId().concat("-").concat(String.valueOf(System.nanoTime())).concat(".xml"));
        if (jobXmlDefinitionFile.exists() && !jobXmlDefinitionFile.delete()) {
            throw new CannotCleanJobDefFileDriverException(jobName, jobXmlDefinitionFile.getAbsolutePath());
        }

        try (FileOutputStream tmpStream = new FileOutputStream(jobXmlDefinitionFile)) {
            IOUtils.write(jobXmlDefinition, tmpStream);
        } catch (IOException e) {
            throw new CannotWriteJobDefFileDriverException(e, jobName, jobXmlDefinitionFile.getAbsolutePath());
        }
        // Start job
        try {
            jobOperator.start(jobXmlDefinitionFile.getAbsolutePath().replaceAll("\\.xml$", ""), new Properties());
        } catch (NoSuchJobExecutionException | NoSuchJobException | JobSecurityException e) {
            throw new JobStartingDriverException(e, jobName);
        }
    }

    /**
     * Stops completely the jBatch job.
     * <p>
     * First invokes the {@link JobOperator#stop(long)} on the running execution, which stop the running execution. Secondly, according to the {@link JobEngineSettingKeys#JOB_ENGINE_STOP_WAIT_CHECK}
     * value, it waits asynchronously the complete stop of the job to be able to invoke {@link JobOperator#abandon(long)} which terminate the jBatch Job.
     * <p>
     * A jBatch job cannot be resumed after this method is invoked on it.
     *
     * @param scopeId
     *         The scopeId of the {@link Job}
     * @param jobId
     *         The id of the {@link Job}
     * @param toStopJobExecutionId
     *         The optional {@link org.eclipse.kapua.service.job.execution.JobExecution#getId()} to stop.
     * @throws ExecutionNotFoundDriverException
     *         when there isn't a corresponding job execution in jBatch tables
     * @throws ExecutionNotRunningDriverException
     *         when the corresponding job execution is not running.
     * @since 1.0.0
     */
    public void stopJob(@NotNull KapuaId scopeId, @NotNull KapuaId jobId, KapuaId toStopJobExecutionId) throws JbatchDriverException, KapuaException {

        String jobName = getJbatchJobName(scopeId, jobId);
        // Check running
        List<JobExecution> runningExecutions = getRunningJobExecutions(scopeId, jobId);
        if (runningExecutions.isEmpty()) {
            throw new ExecutionNotRunningDriverException(jobName);
        }
        // Filter execution to stop
        if (toStopJobExecutionId != null) {
            org.eclipse.kapua.service.job.execution.JobExecution je = jobExecutionService.find(scopeId, toStopJobExecutionId);

            long toStopJbatchExecutionId = Long.parseLong((String) je.getEntityAttributes().get(JBATCH_EXECUTION_ID));

            runningExecutions = runningExecutions.stream().filter(re -> re.getExecutionId() == toStopJbatchExecutionId).collect(Collectors.toList());
        }
        // Do stop
        try {
            runningExecutions.forEach((runningExecution -> {
                jobOperator.stop(runningExecution.getExecutionId());
            }));
        } catch (NoSuchJobExecutionException e) {
            throw new ExecutionNotFoundDriverException(e, jobName);
        } catch (JobExecutionNotRunningException e) {
            throw new ExecutionNotRunningDriverException(e, jobName);
        }
    }

    public void resumeJob(@NotNull KapuaId scopeId, @NotNull KapuaId jobId, @NotNull KapuaId toResumeJobExecutionId) throws JbatchDriverException, KapuaException {

        String jobName = getJbatchJobName(scopeId, jobId);
        // Get list
        List<JobExecution> stoppedJobExecutions = getJobExecutions(scopeId, jobId);
        if (stoppedJobExecutions.isEmpty()) {
            throw new ExecutionNotFoundDriverException(jobName);
        }
        // Filter execution to resume
        org.eclipse.kapua.service.job.execution.JobExecution je = jobExecutionService.find(scopeId, toResumeJobExecutionId);

        long toResumeJbatchExecutionId = Long.parseLong((String) je.getEntityAttributes().get(JBATCH_EXECUTION_ID));

        stoppedJobExecutions = stoppedJobExecutions.stream().filter(re -> re.getExecutionId() == toResumeJbatchExecutionId).collect(Collectors.toList());
        // Do stop
        try {
            stoppedJobExecutions.forEach((stoppedExecution -> {
                Properties properties = new Properties();

                properties.setProperty(JobContextPropertyNames.RESUMED_KAPUA_EXECUTION_ID, toResumeJobExecutionId.toCompactId());

                jobOperator.restart(stoppedExecution.getExecutionId(), properties);
            }));
        } catch (NoSuchJobExecutionException e) {
            throw new ExecutionNotFoundDriverException(e, jobName);
        } catch (JobExecutionNotRunningException e) {
            throw new ExecutionNotRunningDriverException(e, jobName);
        }
    }

    /**
     * Checks whether or not the {@link Job} identified by the parametersis in a running status.
     * <p>
     * jBatch {@link Job} running statuses are listed in {@link JbatchJobRunningStatuses}
     *
     * @param scopeId
     *         The scopeId of the {@link Job}
     * @param jobId
     *         The id of the {@link Job}
     * @return {@code true} if the jBatch {@link Job} is running, {@code false} otherwise.
     * @since 1.0.0
     */
    public boolean isRunningJob(@NotNull KapuaId scopeId, @NotNull KapuaId jobId) {
        return !getRunningJobExecutions(scopeId, jobId).isEmpty();
    }

    /**
     * Deletes jBatch internal data for the given {@link Job} id.
     * <p>
     * The cleanup is required when deleting a {@link Job} to avoid stale data to be left in the jBatch tables.
     *
     * @param scopeId
     *         The {@link Job#getScopeId()}
     * @param jobId
     *         The {@link Job#getId()}
     * @throws CleanJobDataDriverException
     *         if the cleanup produces an error
     * @since 1.0.0
     */
    public void cleanJobData(@NotNull KapuaId scopeId, @NotNull KapuaId jobId) throws CleanJobDataDriverException {
        String jobName = getJbatchJobName(scopeId, jobId);
        try {
            ((JPAPersistenceManagerImpl) ServicesManagerImpl.getInstance().getPersistenceManagerService()).purgeByName(jobName);
        } catch (Exception ex) {
            throw new CleanJobDataDriverException(ex, jobName);
        }
    }

    // Private methods
    private List<JobExecution> getRunningJobExecutions(@NotNull KapuaId scopeId, @NotNull KapuaId jobId) {
        return getJobExecutions(scopeId, jobId).stream().filter(je -> JbatchJobRunningStatuses.getStatuses().contains(je.getBatchStatus())).collect(Collectors.toList());
    }

    private List<JobExecution> getJobExecutions(@NotNull KapuaId scopeId, @NotNull KapuaId jobId) {
        String jobName = getJbatchJobName(scopeId, jobId);

        // Get all JobInstances with this name
        List<JobInstance> jobInstances;
        try {
            jobInstances = jobOperator.getJobInstances(jobName, 0, Integer.MAX_VALUE);
        } catch (NoSuchJobException nsje) {
            LOG.warn("Error while getting JobInstance by name: {}. Exception: {}: {}", jobName, nsje.getClass().getSimpleName(), nsje.getMessage());
            return Collections.emptyList();
        } catch (NullPointerException npe) {
            LOG.error("Unexpected NPE!", npe);
            return Collections.emptyList();
        }

        // For each JobInstance get its JobExecutions
        List<JobExecution> jobExecutions = new ArrayList<>();
        for (JobInstance ji : jobInstances) {
            try {
                jobExecutions.addAll(getJbatchJobExecutions(ji));
            } catch (NoSuchJobInstanceException nsjie) {
                LOG.warn("Error while getting JobExecutions by JobInstance: {}. Exception: {}: {}. Continuing with other JobInstances", ji.getInstanceId(), nsjie.getClass().getSimpleName(),
                        nsjie.getMessage());
            } catch (NullPointerException npe) {
                LOG.error("Unexpected NPE!", npe);
            }
        }

        return jobExecutions;
    }

    private List<JobExecution> getJbatchJobExecutions(@NotNull JobInstance jobInstance) {
        try {
            return jobOperator.getJobExecutions(jobInstance);
        } catch (NoSuchJobInstanceException nsjie) {
            LOG.warn("Error while getting JobExecutions by JobInstance: {}. Exception {}: {}. Ignoring exception...", jobInstance.getInstanceId(), nsjie.getClass().getSimpleName(),
                    nsjie.getMessage());
            // This exception is thrown when there is no job instance, this means that the job never run before
            // So we can ignore it and return an empty `List<>`
        }

        return Collections.emptyList();
    }
}
