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
package org.eclipse.kapua.service.datastore.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdXmlAdapter;

/**
 * Message returned by the data store find services
 *
 * @since 1.0
 */
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "id",
        "scopeId",
        "datastoreId",
        "timestamp",
        "deviceId",
        "clientId",
        "receivedOn",
        "sentOn",
        "capturedOn",
        "position",
        "channel",
        "payload",
})
public class DatastoreMessage extends KapuaMessage<KapuaDataChannel, KapuaPayload> implements Storable {

    private static final long serialVersionUID = 1L;

    private StorableId datastoreId;
    private Date timestamp;

    /**
     * Stored message identifier
     *
     * @return
     */
    @XmlElement(name = "datastoreId")
    @XmlJavaTypeAdapter(StorableIdXmlAdapter.class)
    public StorableId getDatastoreId() {
        return datastoreId;
    }

    /**
     * Stored message identifier
     */
    public void setDatastoreId(StorableId id) {
        this.datastoreId = id;
    }

    /**
     * Stored message timestamp
     *
     * @return
     */
    public Date getTimestamp() {
        return this.timestamp;
    }

    /**
     * Stored message timestamp
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
