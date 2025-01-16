/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @since 1.0.0
 */
@XmlRootElement(name = "countResult")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CountResult {

    private long count;

    protected CountResult() {
    }

    public CountResult(long count) {
        this.count = count;
    }

    @XmlElement(name = "count")
    @Schema(description = "The total count of the Entities available in the Scope")
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
