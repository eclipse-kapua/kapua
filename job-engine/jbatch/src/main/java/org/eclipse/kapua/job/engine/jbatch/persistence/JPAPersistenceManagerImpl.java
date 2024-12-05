/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.jbatch.persistence;

import com.ibm.jbatch.container.context.impl.StepContextImpl;
import com.ibm.jbatch.container.exception.PersistenceException;
import com.ibm.jbatch.container.jobinstance.JobInstanceImpl;
import com.ibm.jbatch.container.jobinstance.JobOperatorJobExecution;
import com.ibm.jbatch.container.jobinstance.RuntimeFlowInSplitExecution;
import com.ibm.jbatch.container.jobinstance.RuntimeJobExecution;
import com.ibm.jbatch.container.jobinstance.StepExecutionImpl;
import com.ibm.jbatch.container.persistence.CheckpointData;
import com.ibm.jbatch.container.persistence.CheckpointDataKey;
import com.ibm.jbatch.container.services.IJobExecution;
import com.ibm.jbatch.container.services.IPersistenceManagerService;
import com.ibm.jbatch.container.services.impl.JDBCPersistenceManagerImpl;
import com.ibm.jbatch.container.status.JobStatus;
import com.ibm.jbatch.container.status.StepStatus;
import com.ibm.jbatch.spi.services.IBatchConfig;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.AbstractKapuaService;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaCheckpointData;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaCheckpointDataDAO;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaExecutionInstanceData;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaExecutionInstanceDataDAO;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaExecutionInstanceDataFields;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaJobInstanceData;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaJobInstanceDataDAO;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaJobStatus;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaJobStatusDAO;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaStepExecutionInstanceData;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaStepExecutionInstanceDataDAO;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaStepStatus;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaStepStatusDAO;
import org.eclipse.kapua.model.id.KapuaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is a custom implementation of the {@link IPersistenceManagerService} for jBatch which replace the default {@link JDBCPersistenceManagerImpl}.
 * <p>
 * This implementation is much more performant for many reasons, starting from using a DB connection pool and query that are precompiled by JPA using the {@link javax.persistence.NamedQueries}.
 * Entity have been optimized and code now skips some checks that {@link  JDBCPersistenceManagerImpl} performs since those checks are not required by our usage of jBatch.
 * This makes this implementation not compatible with the regular behaviour of the {@link JDBCPersistenceManagerImpl} but makes it fit our needing.
 * <p>
 * As a general overview if the jBatch tables they follow the schema below
 *
 * <pre>
 *     JobInstanceData (aka: JobInstace)
 *     |
 *     |-- has one --- JobStatus
 *     |-- has many -- ExecutionInstanceData (aka: JobExecution)
 *                    |
 *                    |-- has many -- StepExecutionInstanceData (aka: JobStepExecution)
 *                    |              |
 *                    |              |-- has one --- StepStatus
 *                    |
 *                    |-- has many -- CheckpointData
 * </pre>
 *
 * @since 1.2.0
 */
public class JPAPersistenceManagerImpl extends AbstractKapuaService implements IPersistenceManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(JPAPersistenceManagerImpl.class);

    public JPAPersistenceManagerImpl() {
        super(JbatchEntityManagerFactory.getInstance());
    }

    @Override
    public void init(IBatchConfig batchConfig) {
        LOG.info("jBatch persistence implementation initialized with no actions...");
    }

    @Override
    public void shutdown() {
        LOG.info("jBatch persistence implementation tear down with no actions...");
    }

    //
    // Checkpoint Data
    //
    @Override
    public void createCheckpointData(CheckpointDataKey checkpointDataKey, CheckpointData checkpointData) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaCheckpointDataDAO.create(em, checkpointDataKey, checkpointData));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateCheckpointData(CheckpointDataKey checkpointDataKey, CheckpointData checkpointData) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaCheckpointDataDAO.update(em, checkpointDataKey, checkpointData));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public CheckpointData getCheckpointData(CheckpointDataKey checkpointDataKey) {
        try {
            JpaCheckpointData jpaCheckpointData = entityManagerSession.doAction(em -> JpaCheckpointDataDAO.find(em, checkpointDataKey));
            return jpaCheckpointData != null ? jpaCheckpointData.toCheckpointData() : null;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    //
    // Job Instance Data
    //
    @Override
    public JobInstance createSubJobInstance(String name, String appTag) {
        return createJobInstance(name, appTag, null);
    }

    @Override
    public JobInstance createJobInstance(String name, String appTag, String jobXml) {
        try {
            JpaJobInstanceData jpaJobInstanceData = entityManagerSession.doTransactedAction(em -> JpaJobInstanceDataDAO.create(em, name, appTag, jobXml));
            return jpaJobInstanceData != null ? jpaJobInstanceData.toJobInstance() : null;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public int jobOperatorGetJobInstanceCount(String jobName) {
        return jobOperatorGetJobInstanceCount(jobName, null);
    }

    @Override
    public int jobOperatorGetJobInstanceCount(String jobName, String appTag) {
        try {
            return entityManagerSession.doAction(em -> JpaJobInstanceDataDAO.getJobInstanceCount(em, jobName, appTag));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Long> jobOperatorGetJobInstanceIds(String jobName, int offset, int limit) {
        return jobOperatorGetJobInstanceIds(jobName, null, offset, limit);
    }

    @Override
    public List<Long> jobOperatorGetJobInstanceIds(String jobName, String appTag, int offset, int limit) {
        try {
            return entityManagerSession.doAction(em -> JpaJobInstanceDataDAO.getJobInstanceIds(em, jobName, appTag, offset, limit));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Map<Long, String> jobOperatorGetExternalJobInstanceData() {
        try {
            return entityManagerSession.doAction(JpaJobInstanceDataDAO::getExternalJobInstanceData);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public String getJobCurrentTag(long jobInstanceId) {
        // We are currently not using app tags. They always default to "NOTSET".
        // It, at some point, we want to use them. This code below can be uncommented.
        //        try {
        //            JpaJobInstanceData jobInstanceData = entityManagerSession.onResult(em -> JpaJobInstanceDataDAO.find(em, jobInstanceId));
        //            return jobInstanceData != null ? jobInstanceData.getAppTag() : null;
        //        } catch (Exception e) {
        //            throw new PersistenceException(e);
        //        }

        return "NOTSET";
    }

    /**
     * Deletes {@link JpaJobInstanceData} by its {@link JpaJobInstanceData#getId()}.
     *
     * @param jobInstanceId The {@link JpaJobInstanceData#getId()} to delete.
     * @since 2.1.0
     */
    public void deleteJobInstanceData(long jobInstanceId) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaJobInstanceDataDAO.deleteById(em, jobInstanceId));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Deletes {@link JobInstance}s by name.
     * <p>
     * This relies on the foreign keys with {@code DELETE CASCADE} on jBatch tables to delete all data from all the other tables.
     * Relationship between those table are summarized in the {@link JPAPersistenceManagerImpl} javadoc.
     *
     * @param jobName The jBatch job name. See {@link org.eclipse.kapua.job.engine.jbatch.driver.JbatchDriver#getJbatchJobName(KapuaId, KapuaId)}
     * @return The number of records delete on the DB.
     * @since 1.2.0
     */
    public int purgeByName(String jobName) {
        try {
            return entityManagerSession.doTransactedAction(em -> JpaJobInstanceDataDAO.deleteByName(em, jobName));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    //
    // Job Status
    //

    @Override
    public JobStatus createJobStatus(long jobInstanceId) {
        try {
            JpaJobStatus jpaJobStatus = entityManagerSession.doTransactedAction(em -> JpaJobStatusDAO.create(em, jobInstanceId));
            return jpaJobStatus != null ? jpaJobStatus.toJobStatus() : null;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public JobStatus getJobStatus(long jobInstanceId) {
        try {
            JpaJobStatus jpaJobStatus = entityManagerSession.doAction(em -> JpaJobStatusDAO.find(em, jobInstanceId));

            JobStatus jobStatus;
            if (jpaJobStatus != null) {
                jobStatus = jpaJobStatus.toJobStatus();
            } else {
                jobStatus = new JobStatus(jobInstanceId);
            }

            if (jobStatus.getJobInstance() == null) {
                JobInstance jobInstance = entityManagerSession.doAction(em -> JpaJobInstanceDataDAO.find(em, jobInstanceId).toJobInstance());

                if (jobInstance == null) {
                    jobInstance = new JobInstanceImpl(jobInstanceId);
                }

                jobStatus.setJobInstance(jobInstance);
            }
            return jobStatus;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateJobStatus(long jobInstanceId, JobStatus jobStatus) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaJobStatusDAO.update(em, jobInstanceId, jobStatus));
        } catch (KapuaException e) {
            throw new PersistenceException(e);
        }
    }


    //
    // Execution Instance Data
    //
    @Override
    public RuntimeJobExecution createJobExecution(JobInstance jobInstance, Properties jobParameters, BatchStatus batchStatus) {
        try {
            JpaExecutionInstanceData jpaExecutionInstanceData = entityManagerSession.doTransactedAction(em -> JpaExecutionInstanceDataDAO.create(em, jobInstance.getInstanceId(), jobParameters, batchStatus, new Timestamp(new Date().getTime())));

            RuntimeJobExecution runtimeJobExecution = new RuntimeJobExecution(jobInstance, jpaExecutionInstanceData.getId());
            runtimeJobExecution.setBatchStatus(batchStatus.name());
            runtimeJobExecution.setCreateTime(jpaExecutionInstanceData.getCreateTime());
            runtimeJobExecution.setLastUpdateTime(jpaExecutionInstanceData.getUpdateTime());

            return runtimeJobExecution;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public RuntimeFlowInSplitExecution createFlowInSplitExecution(JobInstance jobInstance, BatchStatus batchStatus) {
        try {
            JpaExecutionInstanceData jpaExecutionInstanceData = entityManagerSession.doTransactedAction(em -> JpaExecutionInstanceDataDAO.create(em, jobInstance.getInstanceId(), null, batchStatus, new Timestamp(new Date().getTime())));

            RuntimeFlowInSplitExecution runtimeFlowInSplitExecution = new RuntimeFlowInSplitExecution(jobInstance, jpaExecutionInstanceData.getId());
            runtimeFlowInSplitExecution.setBatchStatus(batchStatus.name());
            runtimeFlowInSplitExecution.setCreateTime(jpaExecutionInstanceData.getCreateTime());
            runtimeFlowInSplitExecution.setLastUpdateTime(jpaExecutionInstanceData.getUpdateTime());

            return runtimeFlowInSplitExecution;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Timestamp jobOperatorQueryJobExecutionTimestamp(long jobExecutionId, TimestampType timestampType) {
        try {
            JpaExecutionInstanceDataFields selectField;

            switch (timestampType) {
                case CREATE:
                    selectField = JpaExecutionInstanceDataFields.CREATE_TIME;
                    break;
                case STARTED:
                    selectField = JpaExecutionInstanceDataFields.START_TIME;
                    break;
                case LAST_UPDATED:
                    selectField = JpaExecutionInstanceDataFields.UPDATE_TIME;
                    break;
                case END:
                    selectField = JpaExecutionInstanceDataFields.END_TIME;
                    break;
                default:
                    throw new IllegalArgumentException(timestampType.name());
            }

            return entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.getJobExecutionField(em, jobExecutionId, selectField));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public String jobOperatorQueryJobExecutionBatchStatus(long jobExecutionId) {
        try {
            return entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.<BatchStatus>getJobExecutionField(em, jobExecutionId, JpaExecutionInstanceDataFields.BATCH_STATUS)).name();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public String jobOperatorQueryJobExecutionExitStatus(long key) {
        try {
            return entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.getJobExecutionField(em, key, JpaExecutionInstanceDataFields.EXIT_STATUS));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public long getJobInstanceIdByExecutionId(long executionId) throws NoSuchJobExecutionException {
        return jobOperatorQueryJobExecutionJobInstanceId(executionId);
    }

    @Override
    public long jobOperatorQueryJobExecutionJobInstanceId(long key) throws NoSuchJobExecutionException {
        try {
            Long jobInstanceId = entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.getJobExecutionField(em, key, JpaExecutionInstanceDataFields.JOB_INSTANCE_ID));

            if (jobInstanceId != null) {
                return jobInstanceId;
            } else {
                throw new NoSuchJobExecutionException("Job Instance not found for Job Execution Id: " + key);
            }

        } catch (NoSuchJobExecutionException nsjee) {
            throw nsjee;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Properties getParameters(long jobExecutionId) throws NoSuchJobExecutionException {
        try {
            Properties jobParameters = entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.getJobExecutionField(em, jobExecutionId, JpaExecutionInstanceDataFields.PARAMETERS));

            if (jobParameters != null) {
                return jobParameters;
            } else {
                throw new NoSuchJobExecutionException("Job Instance not found for Job Execution Id: " + jobExecutionId);
            }
        } catch (NoSuchJobExecutionException nsjee) {
            throw nsjee;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateBatchStatusOnly(long executionInstanceDataId, BatchStatus batchStatus, Timestamp updatedOn) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaExecutionInstanceDataDAO.updateBatchStatus(em, executionInstanceDataId, batchStatus, updatedOn));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateWithFinalExecutionStatusesAndTimestamps(long executionInstanceDataId, BatchStatus batchStatus, String exitStatus, Timestamp endedOn) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaExecutionInstanceDataDAO.updateBatchStatusEnded(em, executionInstanceDataId, batchStatus, exitStatus, endedOn));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void markJobStarted(long executionInstanceDataId, Timestamp startedOn) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaExecutionInstanceDataDAO.updateBatchStatusStarted(em, executionInstanceDataId, startedOn));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public IJobExecution jobOperatorGetJobExecution(long jobExecutionId) {
        try {
            JpaExecutionInstanceData jpaExecutionInstanceData = entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.find(em, jobExecutionId));
            JpaJobInstanceData jpaJobInstanceData = entityManagerSession.doAction(em -> JpaJobInstanceDataDAO.find(em, jpaExecutionInstanceData.getJobInstanceId()));

            JobOperatorJobExecution jobExecution = jpaExecutionInstanceData.toJobExecution();
            jobExecution.setJobName(jpaJobInstanceData.getName());

            return jobExecution;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<IJobExecution> jobOperatorGetJobExecutions(long jobInstanceId) {
        try {
            List<JpaExecutionInstanceData> jpaExecutionInstanceDataResult = entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.getJobExecutions(em, jobInstanceId));
            return jpaExecutionInstanceDataResult.stream().map(JpaExecutionInstanceData::toJobExecution).collect(Collectors.toList());
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Set<Long> jobOperatorGetRunningExecutions(String jobName) {
        try {
            return entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.getJobRunningExecutions(em, jobName));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public JobStatus getJobStatusFromExecution(long jobExecutionId) {
        try {
            return entityManagerSession.doAction(em -> {
                JpaExecutionInstanceData jpaExecutionInstanceData = JpaExecutionInstanceDataDAO.find(em, jobExecutionId);

                JpaJobStatus jobStatus = JpaJobStatusDAO.find(em, jpaExecutionInstanceData.getJobInstanceId());

                return jobStatus.getObjAsJobStatus();
            });

        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public String getTagName(long jobExecutionId) {
        try {
            JpaJobInstanceData jpaJobInstanceData = entityManagerSession.doAction(em -> {
                JpaExecutionInstanceData jpaExecutionInstanceData = JpaExecutionInstanceDataDAO.find(em, jobExecutionId);
                return JpaJobInstanceDataDAO.find(em, jpaExecutionInstanceData.getJobInstanceId());
            });
            return jpaJobInstanceData.getAppTag();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public long getMostRecentExecutionId(long jobInstanceId) {
        try {
            JpaExecutionInstanceData jpaExecutionInstanceData = entityManagerSession.doAction(em -> JpaExecutionInstanceDataDAO.getMostRecentByJobInstance(em, jobInstanceId));
            return jpaExecutionInstanceData.getId();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    //
    // Step Execution Instance Data
    //
    @Override
    public StepExecutionImpl createStepExecution(long jobExecutionId, StepContextImpl stepContext) {
        try {
            JpaStepExecutionInstanceData jpaStepExecutionInstanceData = entityManagerSession.doTransactedAction(em -> JpaStepExecutionInstanceDataDAO.insert(em, jobExecutionId, stepContext));
            return jpaStepExecutionInstanceData != null ? jpaStepExecutionInstanceData.toStepExecution() : null;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateStepExecution(StepContextImpl stepContext) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaStepExecutionInstanceDataDAO.update(em, stepContext));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Map<String, StepExecution> getMostRecentStepExecutionsForJobInstance(long jobInstanceId) {
        try {
            return entityManagerSession.doAction(em -> JpaStepExecutionInstanceDataDAO.getExternalJobInstanceData(em, jobInstanceId));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<StepExecution> getStepExecutionsForJobExecution(long jobExecutionId) {
        try {
            return entityManagerSession.doAction(em -> JpaStepExecutionInstanceDataDAO.getStepExecutionsByJobExecution(em, jobExecutionId));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public StepExecution getStepExecutionByStepExecutionId(long stepExecId) {
        try {
            JpaStepExecutionInstanceData jpaStepExecutionInstanceData = entityManagerSession.doAction(em -> JpaStepExecutionInstanceDataDAO.find(em, stepExecId));
            return jpaStepExecutionInstanceData != null ? jpaStepExecutionInstanceData.toStepExecution() : null;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    //
    // Step Status
    //
    @Override
    public StepStatus createStepStatus(long stepExecutionId) {
        try {
            JpaStepStatus jpaStepStatus = entityManagerSession.doTransactedAction(em -> JpaStepStatusDAO.create(em, stepExecutionId));
            return jpaStepStatus != null ? jpaStepStatus.toStepStatus() : null;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public StepStatus getStepStatus(long jobInstanceId, String stepName) {
        try {
            JpaStepStatus jpaStepStatus = entityManagerSession.doAction(em -> JpaStepStatusDAO.getStepStatusByJobInstance(em, jobInstanceId, stepName));
            return jpaStepStatus != null ? jpaStepStatus.toStepStatus() : null;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateStepStatus(long stepExecutionId, StepStatus stepStatus) {
        try {
            entityManagerSession.doTransactedAction(em -> JpaStepStatusDAO.update(em, stepExecutionId, stepStatus));
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    //
    // Random
    //

    @Override
    public void purge(String apptag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateWithFinalPartitionAggregateStepExecution(long rootJobExecutionId, StepContextImpl stepContext) {
        throw new UnsupportedOperationException();
    }
}

