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
package org.eclipse.kapua.job.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.model.id.KapuaId;

@XmlRootElement(name = "isJobRunningMultipleResponse")
public class IsJobRunningMultipleResponse {

    private List<IsJobRunningResponse> list = new ArrayList<>();

    public IsJobRunningMultipleResponse() {
    }

    public IsJobRunningMultipleResponse(Map<KapuaId, Boolean> map) {
        map.forEach((key, value) -> list.add(new IsJobRunningResponse(key, value)));
    }

    @XmlElementWrapper
    public List<IsJobRunningResponse> getList() {
        return list;
    }

    public void setMap(List<IsJobRunningResponse> list) {
        this.list = list;
    }

}
