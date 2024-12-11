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

import java.util.Date;
import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.app.api.core.model.message.JsonKapuaPayload;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonKapuaDataMessage {

    private UUID id;

    private KapuaId scopeId;
    private KapuaId deviceId;
    private String clientId;

    private Date receivedOn;
    private Date sentOn;
    private Date capturedOn;

    private KapuaPosition position;
    private KapuaDataChannel channel;
    private JsonKapuaPayload payload;

    public JsonKapuaDataMessage() {
    }

    public JsonKapuaDataMessage(KapuaDataMessage kapuaDataMessage) {
        setId(kapuaDataMessage.getId());

        setScopeId(kapuaDataMessage.getScopeId());
        setDeviceId(kapuaDataMessage.getDeviceId());
        setClientId(kapuaDataMessage.getClientId());

        setReceivedOn(kapuaDataMessage.getReceivedOn());
        setSentOn(kapuaDataMessage.getSentOn());
        setCapturedOn(kapuaDataMessage.getCapturedOn());

        setPosition(kapuaDataMessage.getPosition());
        setChannel(kapuaDataMessage.getChannel());
        setPayload(kapuaDataMessage.getPayload());
    }

    @XmlElement(name = "id")
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getScopeId() {
        return scopeId;
    }

    public void setScopeId(KapuaId scopeId) {
        this.scopeId = scopeId;
    }

    @XmlElement(name = "deviceId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(KapuaId deviceId) {
        this.deviceId = deviceId;
    }

    @XmlElement(name = "clientId")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @XmlElement(name = "receivedOn")
    public Date getReceivedOn() {
        return receivedOn;
    }

    public void setReceivedOn(Date receivedOn) {
        this.receivedOn = receivedOn;
    }

    @XmlElement(name = "sentOn")
    public Date getSentOn() {
        return sentOn;
    }

    public void setSentOn(Date sentOn) {
        this.sentOn = sentOn;
    }

    @XmlElement(name = "capturedOn")
    public Date getCapturedOn() {
        return capturedOn;
    }

    public void setCapturedOn(Date capturedOn) {
        this.capturedOn = capturedOn;
    }

    @XmlElement(name = "position")
    public KapuaPosition getPosition() {
        return position;
    }

    public void setPosition(KapuaPosition position) {
        this.position = position;
    }

    @XmlElement(name = "channel")
    public KapuaDataChannel getChannel() {
        return channel;
    }

    public void setChannel(KapuaDataChannel channel) {
        this.channel = channel;
    }

    @XmlElement(name = "payload")
    public JsonKapuaPayload getPayload() {
        return payload;
    }

    public void setPayload(JsonKapuaPayload payload) {
        this.payload = payload;
    }

    @XmlTransient
    @JsonIgnore
    public void setPayload(KapuaPayload payload) {
        setPayload(new JsonKapuaPayload(payload));
    }
}
