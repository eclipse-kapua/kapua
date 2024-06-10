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
package org.eclipse.kapua.commons.rest.model;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @since 2.0.0
 */
@XmlRootElement(name = "setResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class SetResult {

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private Set<String> items;

    public SetResult() {
    }

    public SetResult(Set<String> items) {
        this.items = items;
    }

    public Set<String> getItems() {
        return items;
    }

    public void setItems(Set<String> items) {
        this.items = items;
    }
}
