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
package org.eclipse.kapua.service.job;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.step.JobStep;

/**
 * {@link Job} {@link KapuaEntityCreator} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobCreator extends KapuaNamedEntityCreator<Job> {

    private static final long serialVersionUID = 3119071638220738358L;

    private List<JobStep> jobSteps;
    private String jobXmlDefinition;

    public JobCreator() {
    }

    public JobCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public JobCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }

    /**
     * Gets the {@link List} of {@link JobStep}.
     *
     * @return The {@link List} of {@link JobStep}.
     * @since 1.0.0
     * @deprecated Since 1.1.0. The {@link JobStep} are no longer bound to the {@link Job}.
     */
    @Deprecated
    @XmlTransient
    public List<JobStep> getJobSteps() {
        if (jobSteps == null) {
            jobSteps = new ArrayList<>();
        }

        return jobSteps;
    }

    /**
     * Sets the {@link List} of {@link JobStep}.
     *
     * @param jobSteps
     *         The {@link List} of {@link JobStep}.
     * @since 1.0.0
     * @deprecated Since 1.1.0. The {@link JobStep} are no longer bound to the {@link Job}.
     */
    @Deprecated
    public void setJobSteps(List<JobStep> jobSteps) {
        this.jobSteps = jobSteps;
    }

    /**
     * Gets the jBatch Job xml definition.
     *
     * @return The jBatch Job xml definition.
     * @since 1.0.0
     * @deprecated Since 1.1.0. The definition is no longer generated.
     */
    @Deprecated
    public String getJobXmlDefinition() {
        return jobXmlDefinition;
    }

    /**
     * Sets the jBatch Job xml definition.
     *
     * @param jobXmlDefinition
     *         The jBatch Job xml definition.
     * @since 1.0.0
     * @deprecated Since 1.1.0. The definition is no longer generated.
     */
    @Deprecated
    public void setJobXmlDefinition(String jobXmlDefinition) {
        this.jobXmlDefinition = jobXmlDefinition;
    }

}
