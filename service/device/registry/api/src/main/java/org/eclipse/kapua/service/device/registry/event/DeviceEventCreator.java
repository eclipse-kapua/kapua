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
package org.eclipse.kapua.service.device.registry.event;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseCode;

/**
 * Device event creator service definition.
 *
 * @since 1.0
 */
public class DeviceEventCreator extends KapuaEntityCreator<DeviceEvent> {

    private static final long serialVersionUID = -3982569213440658172L;
    @XmlElement(name = "deviceId")
    private KapuaId deviceId;

    @XmlElement(name = "receivedOn")
    private Date receivedOn;

    @XmlElement(name = "sentOn")
    private Date sentOn;

    @XmlElement(name = "position")
    private KapuaPosition position;

    @XmlElement(name = "resource")
    private String resource;

    @XmlElement(name = "action")
    private KapuaMethod action;

    @XmlElement(name = "responseCode")
    private KapuaResponseCode responseCode;

    @XmlElement(name = "eventMessage")
    private String eventMessage;

    public DeviceEventCreator() {
    }

    public DeviceEventCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public DeviceEventCreator(KapuaEntityCreator<DeviceEvent> entityCreator) {
        super(entityCreator);
    }

    public DeviceEventCreator(KapuaId scopeId, KapuaId deviceId, Date receivedOn, String resource) {
        super(scopeId);
        this.action = KapuaMethod.CREATE;
        this.deviceId = deviceId;
        this.receivedOn = new Date(receivedOn.getTime());
        this.resource = resource;
    }

    /**
     * Get the device identifier
     *
     * @return
     */
    public KapuaId getDeviceId() {
        return deviceId;
    }

    /**
     * Set the device identifier
     *
     * @param deviceId
     */
    public void setDeviceId(KapuaId deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Get the sent on date
     *
     * @return
     */
    public Date getSentOn() {
        return sentOn != null ? new Date(sentOn.getTime()) : null;
    }

    /**
     * Set the sent on date
     *
     * @param sentOn
     */
    public void setSentOn(Date sentOn) {
        this.sentOn = sentOn;
    }

    /**
     * Get the received on date
     *
     * @return
     */
    public Date getReceivedOn() {
        return receivedOn != null ? new Date(receivedOn.getTime()) : null;
    }

    /**
     * Set the received on date
     *
     * @param receivedOn
     */
    public void setReceivedOn(Date receivedOn) {
        this.receivedOn = receivedOn;
    }

    /**
     * Get device position
     *
     * @return
     */
    public KapuaPosition getPosition() {
        return position;
    }

    /**
     * Set device position
     *
     * @param position
     */
    public void setPosition(KapuaPosition position) {
        this.position = position;
    }

    /**
     * Get resource
     *
     * @return
     */
    public String getResource() {
        return resource;
    }

    /**
     * Set resource
     *
     * @param resource
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * GHet action
     *
     * @return
     */
    public KapuaMethod getAction() {
        return action.normalizeAction();
    }

    /**
     * Set action
     *
     * @param action
     */
    public void setAction(KapuaMethod action) {
        this.action = action.normalizeAction();
    }

    /**
     * Get response code
     *
     * @return
     */
    public KapuaResponseCode getResponseCode() {
        return responseCode;
    }

    /**
     * Set response code
     *
     * @param responseCode
     */
    public void setResponseCode(KapuaResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Get event message
     *
     * @return
     */
    public String getEventMessage() {
        return eventMessage;
    }

    /**
     * Set event message
     *
     * @param eventMessage
     */
    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

}
