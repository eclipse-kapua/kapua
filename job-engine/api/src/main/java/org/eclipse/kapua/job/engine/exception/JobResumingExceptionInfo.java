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

@XmlRootElement(name = "jobResumingExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobResumingExceptionInfo extends JobScopedEngineExceptionInfo {

    @XmlElement(name = "executionId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private KapuaId executionId;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected JobResumingExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param jobResumingException
     *         The root exception.
     * @since 1.0.0
     */
    public JobResumingExceptionInfo(JobResumingException jobResumingException, boolean showStackTrace) {
        super(500/*Status.INTERNAL_SERVER_ERROR*/, jobResumingException, showStackTrace);

        this.executionId = jobResumingException.getJobExecutionId();
    }

    /**
     * Gets the {@link JobResumingException#getJobExecutionId()}.
     *
     * @return The {@link JobResumingException#getJobExecutionId()}.
     * @since 1.0.0
     */
    public KapuaId getExecutionId() {
        return executionId;
    }
}
