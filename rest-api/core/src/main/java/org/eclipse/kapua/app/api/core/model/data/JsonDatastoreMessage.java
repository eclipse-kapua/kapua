/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class JsonDatastoreMessage extends JsonKapuaDataMessage implements Storable {

    private StorableId datastoreId;

    private Date timestamp;

    public JsonDatastoreMessage() {
        super();
    }

    public JsonDatastoreMessage(DatastoreMessage datastoreMessage) {
        super();

        setId(datastoreMessage.getId());
        setDatastoreId(datastoreMessage.getDatastoreId());
        setTimestamp(datastoreMessage.getTimestamp());

        setScopeId(datastoreMessage.getScopeId());
        setDeviceId(datastoreMessage.getDeviceId());
        setClientId(datastoreMessage.getClientId());

        setReceivedOn(datastoreMessage.getReceivedOn());
        setSentOn(datastoreMessage.getSentOn());
        setCapturedOn(datastoreMessage.getCapturedOn());

        setPosition(datastoreMessage.getPosition());
        setChannel(datastoreMessage.getChannel());
        setPayload(datastoreMessage.getPayload());
    }

    @Override
    public String getType() {
        return DatastoreMessage.TYPE;
    }

    @XmlElement(name = "datastoreId")
    @XmlJavaTypeAdapter(StorableIdXmlAdapter.class)
    public StorableId getDatastoreId() {
        return datastoreId;
    }

    public void setDatastoreId(StorableId datastoreId) {
        this.datastoreId = datastoreId;
    }

    @XmlElement(name = "timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
