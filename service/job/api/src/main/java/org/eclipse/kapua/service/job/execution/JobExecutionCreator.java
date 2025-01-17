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
package org.eclipse.kapua.service.job.execution;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

/**
 * {@link JobExecutionCreator} defintion.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobExecutionCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobExecutionCreator extends KapuaUpdatableEntityCreator<JobExecution> {

    private static final long serialVersionUID = 3119071638220738358L;

    private KapuaId jobId;
    private Date startedOn;
    private Set<KapuaId> targetIds;

    public JobExecutionCreator() {
    }

    public JobExecutionCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public JobExecutionCreator(KapuaEntityCreator<JobExecution> entityCreator) {
        super(entityCreator);
    }

    /**
     * @return
     * @since 1.0.0
     */
    public KapuaId getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     * @since 1.0.0
     */
    public void setJobId(KapuaId jobId) {
        this.jobId = jobId;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public Date getStartedOn() {
        return startedOn;
    }

    /**
     * @param startedOn
     * @since 1.0.0
     */
    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }

    /**
     * @return
     * @since 1.1.0
     */
    @XmlElement(name = "targetIds")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public Set<KapuaId> getTargetIds() {
        return targetIds;
    }

    /**
     * @param targetIds
     * @since 1.1.0
     */
    public void setTargetIds(Set<KapuaId> targetIds) {
        this.targetIds = targetIds;
    }
}
