/*******************************************************************************
 * Copyright (c) 2024, 2024 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.jbatch.overrides.callback;

import com.ibm.jbatch.container.callback.JobEndCallback;
import com.ibm.jbatch.container.servicesmanager.ServicesManager;
import com.ibm.jbatch.container.servicesmanager.ServicesManagerImpl;
import org.eclipse.kapua.job.engine.jbatch.driver.JbatchDriver;
import org.eclipse.kapua.job.engine.jbatch.persistence.JPAPersistenceManagerImpl;
import org.eclipse.kapua.job.engine.jbatch.persistence.jpa.JpaJobInstanceData;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link JpaJobInstanceData} cleanup {@link JobEndCallback}.
 * <p>
 * This {@link JobEndCallback} removes {@link JpaJobInstanceData} after the {@link JobExecution} ends it execution.
 * This is done to avoid that {@link Job}s that run a lot of times (thousands or more) fill up the {@link JpaJobInstanceData}
 * table making {@link JbatchDriver#isRunningJob(KapuaId, KapuaId)} tanking a lot of time to execute.
 * <p>
 * {@link JpaJobInstanceData} aren't required nor useful once the {@link JobExecution} ends its processing so they can be safely deleted.
 *
 * @since 2.1.0
 */
public class JobDataCleanupJobEndCallback implements JobEndCallback {

    private static final Logger LOG = LoggerFactory.getLogger(JobDataCleanupJobEndCallback.class);

    private final JPAPersistenceManagerImpl persistenceService;
    private long jobExecutionId;

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    public JobDataCleanupJobEndCallback() {
       ServicesManager servicesManager = ServicesManagerImpl.getInstance();
       persistenceService = (JPAPersistenceManagerImpl) servicesManager.getPersistenceManagerService();
    }

    @Override
    public void setExecutionId(long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public void done(long jobExecutionId) {
        try {
            long jobInstanceId = persistenceService.getJobInstanceIdByExecutionId(jobExecutionId);

            LOG.info("Deleting JobInstanceData {} after JobExecution {} has completed...", jobInstanceId, jobExecutionId);
            persistenceService.deleteJobInstanceData(jobInstanceId);
            LOG.info("Deleting JobInstanceData {} after JobExecution {} has completed... DONE!", jobInstanceId, jobExecutionId);
        }
        catch (Exception e) {
            LOG.error("Deleting JobInstanceData for JobExecution {} has completed... ERROR! This will leave JobInstanceData in the DB until the Job gets deleted", jobExecutionId, e);
        }
    }
}
