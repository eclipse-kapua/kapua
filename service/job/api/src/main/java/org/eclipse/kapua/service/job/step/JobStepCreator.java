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
package org.eclipse.kapua.service.job.step;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;

/**
 * {@link JobStepCreator} {@link org.eclipse.kapua.model.KapuaEntityCreator} definition
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobStepCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobStepCreator extends KapuaNamedEntityCreator<JobStep> {

    private static final long serialVersionUID = 3119071638220738358L;

    private KapuaId jobId;
    private Integer stepIndex;
    private KapuaId jobStepDefinitionId;
    private List<JobStepProperty> jobStepProperty;

    public JobStepCreator() {
    }

    public JobStepCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public JobStepCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }

    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getJobId() {
        return jobId;
    }

    public void setJobId(KapuaId jobId) {
        this.jobId = jobId;
    }

    public Integer getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getJobStepDefinitionId() {
        return jobStepDefinitionId;
    }

    public void setJobStepDefinitionId(KapuaId jobStepDefinitionId) {
        this.jobStepDefinitionId = jobStepDefinitionId;
    }

    @XmlElementWrapper(name = "stepProperties")
    @XmlElement(name = "stepProperty")
    public List<JobStepProperty> getStepProperties() {
        if (jobStepProperty == null) {
            jobStepProperty = new ArrayList<>();
        }

        return jobStepProperty;
    }

    public void setStepProperties(List<JobStepProperty> jobStepProperty) {
        this.jobStepProperty = jobStepProperty;
    }

    /**
     * @deprecated since 2.0.0. Please make use of {@link #getStepProperties()}. This method is deprecated because of issue #3580 (i.e. the step properties' field is called different depending on what
     *         request are you using).
     */
    @Deprecated
    @XmlElementWrapper(name = "jobStepProperties")
    @XmlElement(name = "jobStepProperty")
    public List<JobStepProperty> getJobStepPropertiesDeprecated() {
        return getStepProperties();
    }

    /**
     * @deprecated since 2.0.0. Please make use of {@link #setStepProperties(List)}. This method is deprecated because of issue #3580 (i.e. the step properties' field is called different depending on
     *         what request are you using).
     */
    @Deprecated
    public void setJobStepProperties(List<JobStepProperty> jobStepProperties) {
        setStepProperties(jobStepProperties);
    }

    /**
     * @deprecated since 2.0.0. Please make use of {@link #setStepProperties(List)}. This method is deprecated because of issue #3580 (i.e. the step properties' field is called different depending on
     *         what request are you using).
     */
    @Deprecated
    public void setJobStepPropertiesDeprecated(List<JobStepProperty> jobStepProperties) {
        setStepProperties(jobStepProperties);
    }
}
