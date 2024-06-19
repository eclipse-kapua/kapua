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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashSet;
import java.util.Set;

public class MultipleJobIdRequest {

    private Set<KapuaId> jobIds = new HashSet<>();

    @XmlElementWrapper(name = "jobIds")
    @XmlElement(name = "jobId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public Set<KapuaId> getJobIds() {
        return jobIds;
    }

    public void setJobIds(Set<KapuaId> jobIds) {
        this.jobIds = jobIds;
    }

}
