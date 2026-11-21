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

import org.eclipse.kapua.model.xml.DateXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * A distinct channel name with the id and the date of the last message published on it by any client.
 *
 * @since 2.1.0
 */
@XmlRootElement(name = "distinctChannelName")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistinctChannelName {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "lastMessageId")
    private String lastMessageId;

    @XmlElement(name = "lastMessageOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date lastMessageOn;

    public DistinctChannelName() {
    }

    public DistinctChannelName(String name, String lastMessageId, Date lastMessageOn) {
        this.name = name;
        this.lastMessageId = lastMessageId;
        this.lastMessageOn = lastMessageOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public Date getLastMessageOn() {
        return lastMessageOn;
    }

    public void setLastMessageOn(Date lastMessageOn) {
        this.lastMessageOn = lastMessageOn;
    }
}
