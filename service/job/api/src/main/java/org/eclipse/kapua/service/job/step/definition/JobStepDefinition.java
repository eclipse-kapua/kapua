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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link JobStepDefinition} {@link org.eclipse.kapua.model.KapuaEntity} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobStepDefinition")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = JobStepDefinitionXmlRegistry.class, factoryMethod = "newJobStepDefinition")
public interface JobStepDefinition extends KapuaNamedEntity {

    String TYPE = "stepDefinition";

    @Override
    @Schema(example = "stepDefinition")
    default String getType() {
        return TYPE;
    }

    JobStepType getStepType();

    void setStepType(JobStepType jobStepType);

    String getReaderName();

    void setReaderName(String readerName);

    @Schema(example = "org.eclipse.kapua.service.device.management.asset.job.DeviceAssetWriteTargetProcessor")
    String getProcessorName();

    void setProcessorName(String processorName);

    String getWriterName();

    void setWriterName(String writesName);

    List<JobStepProperty> getStepProperties();

    JobStepProperty getStepProperty(String name);

    void setStepProperties(List<JobStepProperty> jobStepProperties);
}
