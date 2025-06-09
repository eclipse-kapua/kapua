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
package org.eclipse.kapua.service.job.step.definition;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link JobStepDefinitionCreator} {@link org.eclipse.kapua.model.KapuaEntityCreator} definition
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobStepDefinitionCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobStepDefinitionCreator extends KapuaNamedEntityCreator {

    private static final long serialVersionUID = 4602067255120049746L;

    private JobStepType jobStepType;
    private String readerName;
    private String processorName;
    private String writerName;
    private List<JobStepProperty> jobStepProperties;

    public JobStepDefinitionCreator() {
    }

    public JobStepDefinitionCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public JobStepDefinitionCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }

    public JobStepType getStepType() {
        return jobStepType;
    }

    public JobStepDefinitionCreator setStepType(JobStepType jobStepType) {
        this.jobStepType = jobStepType;
        return this;
    }

    public String getReaderName() {
        return readerName;
    }

    public JobStepDefinitionCreator setReaderName(String readerName) {
        this.readerName = readerName;
        return this;
    }

    public String getProcessorName() {
        return processorName;
    }

    public JobStepDefinitionCreator setProcessorName(String processorName) {
        this.processorName = processorName;
        return this;
    }

    public String getWriterName() {
        return writerName;
    }

    public JobStepDefinitionCreator setWriterName(String writerName) {
        this.writerName = writerName;
        return this;
    }

    public List<JobStepProperty> getStepProperties() {
        if (jobStepProperties == null) {
            jobStepProperties = new ArrayList<>();
        }

        return jobStepProperties;
    }

    public JobStepDefinitionCreator setStepProperties(List<JobStepProperty> jobStepProperties) {
        this.jobStepProperties = jobStepProperties;
        return this;
    }
}
