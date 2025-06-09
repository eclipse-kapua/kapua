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
package org.eclipse.kapua.service.job.targets;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

/**
 * {@link JobTargetCreator} encapsulates all the information needed to create a new JobTarget in the system.<br> The data provided will be used to seed the new JobTarget.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobTargetCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobTargetCreator extends KapuaUpdatableEntityCreator {

    private static final long serialVersionUID = 3119071638220738358L;

    private KapuaId jobId;
    private KapuaId jobTargetId;

    public JobTargetCreator() {
    }

    public JobTargetCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public JobTargetCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getJobId() {
        return jobId;
    }

    public void setJobId(KapuaId jobId) {
        this.jobId = jobId;
    }

    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getJobTargetId() {
        return jobTargetId;
    }

    public void setJobTargetId(KapuaId jobTargetId) {
        this.jobTargetId = jobTargetId;
    }
}
