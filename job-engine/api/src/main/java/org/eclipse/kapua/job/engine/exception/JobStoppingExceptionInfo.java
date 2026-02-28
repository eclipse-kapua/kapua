/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

@XmlRootElement(name = "jobStartingExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobStoppingExceptionInfo extends JobScopedEngineExceptionInfo {

    @XmlElement(name = "executionId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private KapuaId executionId;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected JobStoppingExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param jobStoppingException
     *         The root exception.
     * @since 1.0.0
     */
    public JobStoppingExceptionInfo(JobStoppingException jobStoppingException, boolean showStackTrace) {
        super(500/*Status.INTERNAL_SERVER_ERROR*/, jobStoppingException, showStackTrace);

        this.executionId = jobStoppingException.getJobExecutionId();
    }

    /**
     * Gets the {@link JobStoppingException#getJobExecutionId()}.
     *
     * @return The {@link JobStoppingException#getJobExecutionId()}.
     * @since 1.0.0
     */
    public KapuaId getExecutionId() {
        return executionId;
    }
}
