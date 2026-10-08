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

import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.UUID;

/**
 * Message returned by the data store find services
 *
 * @since 1.0
 */
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "id",
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
public interface DatastoreMessage extends Storable {

    String TYPE = "DatastoreMessage";

    @Override
    default String getType() {
        return TYPE;
    }


    /**
     * Get the message identifier
     *
     * @return
     */
    @XmlElement(name = "id")
    UUID getId();

    /**
     * Set the message identifier
     *
     * @param id
     */
    void setId(UUID id);

    /**
     * Stored message identifier
     *
     * @return
     */
    @XmlElement(name = "datastoreId")
    @XmlJavaTypeAdapter(StorableIdXmlAdapter.class)
    StorableId getDatastoreId();

    /**
     * Stored message identifier
     */
    void setDatastoreId(StorableId storableId);

    /**
     * Stored message timestamp
     *
     * @return
     */
    Date getTimestamp();

    /**
     * Stored message timestamp
     */
    void setTimestamp(Date timestamp);

    /**
     * Get client identifier
     *
     * @return
     */
    @XmlElement(name = "clientId")
    String getClientId();

    /**
     * Set client identifier
     *
     * @param clientId
     */
    void setClientId(String clientId);

    /**
     * Get device identifier
     *
     * @return
     */
    @XmlElement(name = "deviceId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getDeviceId();

    /**
     * Set device identifier
     *
     * @param deviceId
     */
    void setDeviceId(KapuaId deviceId);

    /**
     * Get the message received on date
     *
     * @return
     */
    @XmlElement(name = "receivedOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getReceivedOn();

    /**
     * Set the message received on date
     *
     * @param receivedOn
     */
    void setReceivedOn(Date receivedOn);

    /**
     * Get the message sent on date
     *
     * @return
     */
    @XmlElement(name = "sentOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getSentOn();

    /**
     * Set the message sent on date
     *
     * @param sentOn
     */
    void setSentOn(Date sentOn);

    /**
     * Get the message captured on date
     *
     * @return
     */
    @XmlElement(name = "capturedOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getCapturedOn();

    /**
     * Set the message captured on date
     *
     * @param capturedOn
     */
    void setCapturedOn(Date capturedOn);

    /**
     * Get the device position
     *
     * @return
     */
    @XmlElement(name = "position")
    KapuaPosition getPosition();

    /**
     * Set the device position
     *
     * @param position
     */
    void setPosition(KapuaPosition position);

    /**
     * Get the message channel
     *
     * @return
     */
    @XmlElement(name = "channel")
    KapuaDataChannel getChannel();

    /**
     * Set the message channel
     *
     * @param semanticChannel
     */
    void setChannel(KapuaDataChannel semanticChannel);

    /**
     * Get the message payload
     *
     * @return
     */
    @XmlElement(name = "payload")
    KapuaPayload getPayload();

    /**
     * Set the message payload
     *
     * @param payload
     */
    void setPayload(KapuaPayload payload);
}
