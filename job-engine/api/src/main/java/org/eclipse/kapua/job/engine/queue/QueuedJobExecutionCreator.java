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
package org.eclipse.kapua.job.engine.queue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link QueuedJobExecutionCreator} definition.
 *
 * @since 1.1.0
 */
@XmlRootElement(name = "queuedJobExecutionCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class QueuedJobExecutionCreator extends KapuaUpdatableEntityCreator<QueuedJobExecution> {

    private static final long serialVersionUID = 3119071638220738358L;

    private KapuaId jobId;
    private KapuaId jobExecutionId;
    private KapuaId waitForJobExecutionId;
    private QueuedJobExecutionStatus status;

    public QueuedJobExecutionCreator() {
    }

    public QueuedJobExecutionCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public QueuedJobExecutionCreator(KapuaEntityCreator<QueuedJobExecution> entityCreator) {
        super(entityCreator);
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.job.Job} {@link KapuaId}.
     *
     * @return The {@link org.eclipse.kapua.service.job.Job} {@link KapuaId}.
     * @since 1.1.0
     */
    public KapuaId getJobId() {
        return jobId;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.job.Job} {@link KapuaId}.
     *
     * @param jobId
     *         The {@link org.eclipse.kapua.service.job.Job} {@link KapuaId}.
     * @since 1.1.0
     */
    public void setJobId(KapuaId jobId) {
        this.jobId = jobId;
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.job.execution.JobExecution} {@link KapuaId}.
     *
     * @return The {@link org.eclipse.kapua.service.job.execution.JobExecution} {@link KapuaId}.
     * @since 1.1.0
     */
    public KapuaId getJobExecutionId() {
        return jobExecutionId;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.job.execution.JobExecution} {@link KapuaId}.
     *
     * @param jobExecutionId
     *         The {@link org.eclipse.kapua.service.job.execution.JobExecution} {@link KapuaId}.
     * @since 1.1.0
     */
    public void setJobExecutionId(KapuaId jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.job.execution.JobExecution} {@link KapuaId} that needs to complete.
     *
     * @return The {@link org.eclipse.kapua.service.job.execution.JobExecution} {@link KapuaId} that needs to complete.
     * @since 1.1.0
     */
    public KapuaId getWaitForJobExecutionId() {
        return waitForJobExecutionId;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.job.execution.JobExecution} {@link KapuaId} that needs to complete.
     *
     * @param waitForJobExecutionId
     *         The {@link org.eclipse.kapua.service.job.execution.JobExecution} {@link KapuaId} that needs to complete.
     * @since 1.1.0
     */
    public void setWaitForJobExecutionId(KapuaId waitForJobExecutionId) {
        this.waitForJobExecutionId = waitForJobExecutionId;
    }

    /**
     * Gets the {@link QueuedJobExecutionStatus}.
     *
     * @return The {@link QueuedJobExecutionStatus}.
     * @since 1.1.0
     */
    public QueuedJobExecutionStatus getStatus() {
        return status;
    }

    /**
     * Sets the {@link QueuedJobExecutionStatus}.
     *
     * @param status
     *         The {@link QueuedJobExecutionStatus}.
     * @since 1.1.0
     */
    public void setStatus(QueuedJobExecutionStatus status) {
        this.status = status;
    }

}
