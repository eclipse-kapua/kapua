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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.service.job.step.JobStep;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Job} {@link KapuaEntityCreator} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = JobXmlRegistry.class, factoryMethod = "newJobCreator")
@Schema(description = "An object containing the properties for the new Job to be created",
    example = "{\"type\": \"jobCreator\", \"name\": \"newJob\", \"description\": \"A new Job with a fancy description\"}")
public interface JobCreator extends KapuaNamedEntityCreator<Job> {

    /**
     * Gets the {@link List} of {@link JobStep}.
     *
     * @return The {@link List} of {@link JobStep}.
     * @since 1.0.0
     * @deprecated Since 1.1.0. The {@link JobStep} are no longer bound to the {@link Job}.
     */
    @Deprecated
    @XmlTransient
    List<JobStep> getJobSteps();

    /**
     * Sets the {@link List} of {@link JobStep}.
     *
     * @param jobSteps The {@link List} of {@link JobStep}.
     * @since 1.0.0
     * @deprecated Since 1.1.0. The {@link JobStep} are no longer bound to the {@link Job}.
     */
    @Deprecated
    void setJobSteps(List<JobStep> jobSteps);

    /**
     * Gets the jBatch Job xml definition.
     *
     * @return The jBatch Job xml definition.
     * @since 1.0.0
     * @deprecated Since 1.1.0. The definition is no longer generated.
     */
    @Deprecated
    String getJobXmlDefinition();

    /**
     * Sets the jBatch Job xml definition.
     *
     * @param jobXmlDefinition The jBatch Job xml definition.
     * @since 1.0.0
     * @deprecated Since 1.1.0. The definition is no longer generated.
     */
    @Deprecated
    void setJobXmlDefinition(String jobXmlDefinition);

}
