/*******************************************************************************
 * Copyright (c) 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core.model.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * One page of distinct channel names, each with the date of its last message.
 * <p>
 * {@link #getAfter()} is the cursor to pass to get the next page, {@code null} when this is the last page.
 *
 * @since 2.1.0
 */
@XmlRootElement(name = "distinctChannelNamesResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistinctChannelNamesResult {

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<DistinctChannelName> items;

    @XmlElement(name = "after")
    private String after;

    public DistinctChannelNamesResult() {
    }

    public DistinctChannelNamesResult(List<DistinctChannelName> items, String after) {
        this.items = items;
        this.after = after;
    }

    public List<DistinctChannelName> getItems() {
        return items;
    }

    public void setItems(List<DistinctChannelName> items) {
        this.items = items;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
