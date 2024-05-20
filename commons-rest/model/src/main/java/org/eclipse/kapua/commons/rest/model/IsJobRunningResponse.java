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
package org.eclipse.kapua.commons.rest.model;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class IsJobRunningResponse {

    private KapuaId jobId;
    private Boolean isRunning;

    public IsJobRunningResponse() {
    }

    public IsJobRunningResponse(KapuaId jobId, Boolean isRunning) {
        this.jobId = jobId;
        this.isRunning = isRunning;
    }

    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getJobId() {
        return jobId;
    }

    public void setJobId(KapuaId jobId) {
        this.jobId = jobId;
    }

    @XmlElement(name = "isRunning")
    public Boolean isRunning() {
        return isRunning;
    }

    public void setRunning(Boolean isRunning) {
        this.isRunning = isRunning;
    }

}
