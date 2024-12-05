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
package org.eclipse.kapua.job.engine.jbatch.persistence.jpa;

import com.ibm.jbatch.container.jobinstance.JobInstanceImpl;

import javax.batch.runtime.JobInstance;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * JPA counterpart of {@link JobInstance}
 *
 * @since 1.2.0
 */
@Entity(name = "JobInstanceData")
@Table(name = "jbtc_job_instance_data")
@NamedQueries({
        @NamedQuery(name = "JobInstanceData.selectByName",
                query = "SELECT DISTINCT(jid) FROM JobInstanceData jid WHERE jid.name NOT LIKE :name"),
        @NamedQuery(name = "JobInstanceData.selectIdsByName",
                query = "SELECT jid.id FROM JobInstanceData jid WHERE jid.name = :name ORDER BY jid.id DESC"),
        @NamedQuery(name = "JobInstanceData.selectIdsByNameTagApp",
                query = "SELECT jid.id FROM JobInstanceData jid WHERE jid.name = :name AND jid.appTag = :appTag ORDER BY jid.id DESC"),
        @NamedQuery(name = "JobInstanceData.countByName",
                query = "SELECT COUNT(jid) FROM JobInstanceData jid WHERE jid.name = :name"),
        @NamedQuery(name = "JobInstanceData.countByNameTagApp",
                query = "SELECT COUNT(jid) FROM JobInstanceData jid WHERE jid.name = :name AND jid.appTag = :appTag"),
        @NamedQuery(name = "JobInstanceData.deleteByName",
                query = "DELETE FROM JobInstanceData jid WHERE jid.name = :name"),
        @NamedQuery(name = "JobInstanceData.deleteById",
                query = "DELETE FROM JobInstanceData jid WHERE jid.id = :id")

})
public class JpaJobInstanceData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jobinstanceid", updatable = false, nullable = false)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "appTag")
    private String appTag;

    @Transient
    private String jobXml;

    public JpaJobInstanceData() {
        // Required by JPA
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppTag() {
        return appTag;
    }

    public void setAppTag(String appTag) {
        this.appTag = appTag;
    }

    public String getJobXml() {
        return jobXml;
    }

    public void setJobXml(String jobXml) {
        this.jobXml = jobXml;
    }

    public JobInstance toJobInstance() {
        JobInstanceImpl jobInstance = new JobInstanceImpl(getId(), getJobXml());
        jobInstance.setJobName(name);

        return jobInstance;
    }
}
