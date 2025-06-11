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

import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link JobExecution} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobExecution")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = JobExecutionXmlRegistry.class, factoryMethod = "newJobExecution")
public interface JobExecution extends KapuaUpdatableEntity {

    String TYPE = "jobExecution";

    @Override
    @Schema(example = "jobExecution")
    default String getType() {
        return TYPE;
    }

    /**
     * @return
     * @since 1.0.0
     */
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "GTh9xBWezHY")
    KapuaId getJobId();

    /**
     * @param jobId
     * @since 1.0.0
     */
    void setJobId(KapuaId jobId);

    /**
     * @return
     * @since 1.0.0
     */
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getStartedOn();

    /**
     * @param startedOn
     * @since 1.0.0
     */
    void setStartedOn(Date startedOn);

    /**
     * @return
     * @since 1.0.0
     */
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getEndedOn();

    /**
     * @param endedOn
     * @since 1.0.0
     */
    void setEndedOn(Date endedOn);

    /**
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "targetIds")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "[\"Y-vYl9TKaf8\"]")
    <I extends KapuaId> Set<I> getTargetIds();

    /**
     * @param tagTargetIds
     * @since 1.1.0
     */
    void setTargetIds(Set<KapuaId> tagTargetIds);

    /**
     * @return
     * @since 1.1.0
     */
    @Schema(
        example = "[INFO] 12/6/19 11:00 AM - Running before job...\\n[INFO] 12/6/19 11:00 AM - Run " +
                      "configuration:\\n[INFO] 12/6/19 11:00 AM - \\tTarget count:           all\\n[INFO] 12/6/19 " +
                      "11:00 AM - \\tReset step index:       false\\n[INFO] 12/6/19 11:00 AM - \\tFrom step index:   " +
                      "     0\\n[INFO] 12/6/19 11:00 AM - \\tResuming job execution: none\\n[INFO] 12/6/19 11:00 AM -" +
                      " \\tEnqueue:                false\\n[INFO] 12/6/19 11:00 AM - Creating job execution.." +
                      ".\\n[INFO] 12/6/19 11:00 AM - Creating job execution... DONE!\\n[INFO] 12/6/19 11:00 AM - " +
                      "Running before job... DONE!\\n[INFO] 12/6/19 11:00 AM - Reading step: download package (index:" +
                      " 0)...\\n[INFO] 12/6/19 11:00 AM - Reading step: download package (index: 0)... DONE!\\n[INFO]" +
                      " 12/6/19 11:00 AM - RReading target: pahoClient (id: SbQbzB6oOOo)...\\n[INFO] 12/6/19 11:00 AM" +
                      " - Reading target:pahoClient (id:SbQbzB6oOOo)... DONE!\\n[INFO] 12/6/19 11:00 AM - Processing " +
                      "target: pahoClient (id: SbQbzB6oOOo)\\n[INFO] 12/6/19 11:01 AM - Processing target: pahoClient" +
                      " (id: SbQbzB6oOOo) - DONE!\\n[INFO] 12/6/19 11:01 AM - Reading target: client2 (id: " +
                      "A4QbzB6oZZo)...\\n[INFO] 12/6/19 11:01 AM - Reading target: client2 (id: A4QbzB6oZZo)... " +
                      "DONE!\\n[INFO] 12/6/19 11:01 AM - Writing target processing results...\\n[INFO] 12/6/19 11:01 " +
                      "AM - Writing target processing results... DONE!\\n[INFO] 12/6/19 11:01 AM - Running after job." +
                      "..\\n"
    )
    String getLog();

    /**
     * @param log
     * @since 1.1.0
     */
    void setLog(String log);

    /**
     * @return
     * @since 2.1.0
     */
    JobExecutionStatus getStatus();

    /**
     * @param status
     * @since 2.1.0
     */
    void setStatus(JobExecutionStatus status);
}
