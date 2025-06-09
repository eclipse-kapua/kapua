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
package org.eclipse.kapua.message;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

/**
 * {@link KapuaMessage} provides an abstraction over the {@link org.eclipse.kapua.message.Message}s sent in and out of the Kapua platform.
 * <p>
 * It encapsulates all the information regarding the message: the topic it was addressed to, the timestamp when it was received by the platform, and the payload contained in the message.
 * <p>
 * The payload can be represented by a raw binary array or by an {@link KapuaPayload} object if it was formatted as such when the message was composed and sent. Refer to the {@link KapuaPayload}
 * documentation for more details on how {@link KapuaPayload} are modelled and how they can be constructed.
 * <p>
 * The {@link KapuaMessage} class is used both by the messages/search API to return message results from the platform, as well as by messages/store and messages/publish API to send messages to the
 * platform.
 *
 * @param <C>
 *         channel type
 * @param <P>
 *         payload type
 * @since 1.0.0
 */
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { //
        "id", //
        "scopeId", //
        "deviceId", //
        "clientId", //
        "receivedOn", //
        "sentOn", //
        "capturedOn", //
        "position", //
        "channel", //
        "payload", //
})
@XmlSeeAlso(KapuaDataMessage.class)
@XmlTransient
public class KapuaMessage<C extends KapuaChannel, P extends KapuaPayload>
        implements
        Comparable<KapuaMessage<C, P>>, Message<C, P> {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private KapuaId scopeId;
    private KapuaId deviceId;
    private String clientId;

    private Date receivedOn;
    private Date sentOn;
    private Date capturedOn;

    private KapuaPosition position;

    private C channel;

    private P payload;

    /**
     * Constructor
     */
    public KapuaMessage() {
        super();
    }

    /**
     * Constructor.
     *
     * @param channel
     *         The {@link KapuaChannel} of the {@link KapuaMessage}
     * @param payload
     *         The {@link KapuaPayload} of the {@link KapuaMessage}
     * @since 1.0.0
     */
    public KapuaMessage(C channel, P payload) {
        this();

        this.channel = channel;
        this.payload = payload;
    }

    /**
     * Gets the unique identifier.
     *
     * @return The unique identifier.
     * @since 1.0.0
     */
    @XmlElement(name = "id")
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier.
     *
     * @param id
     *         The unique identifier.
     * @since 1.0.0
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the scope {@link KapuaId}
     *
     * @return The scope {@link KapuaId}
     * @since 1.0.0
     */
    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Sets the scope {@link KapuaId}.
     *
     * @param scopeId
     *         The scope {@link KapuaId}
     * @since 1.0.0
     */
    public void setScopeId(KapuaId scopeId) {
        this.scopeId = scopeId;
    }

    /**
     * Gets the device client identifier
     *
     * @return The device client identifier.
     * @since 1.0.0
     */
    @XmlElement(name = "clientId")
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the device client identifier.
     *
     * @param clientId
     *         The device client identifier.
     * @since 1.0.0
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the device {@link KapuaId}.
     *
     * @return The device {@link KapuaId}.
     * @since 1.0.0
     */
    @XmlElement(name = "deviceId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device {@link KapuaId}.
     *
     * @param deviceId
     *         The device {@link KapuaId}.
     * @since 1.0.0
     */
    public void setDeviceId(KapuaId deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the received on {@link Date}.
     *
     * @return The received on {@link Date}.
     * @since 1.0.0
     */
    @XmlElement(name = "receivedOn")
    public Date getReceivedOn() {
        return receivedOn;
    }

    /**
     * Sets the received on {@link Date}.
     *
     * @param receivedOn
     *         The received on {@link Date}.
     * @since 1.0.0
     */
    public void setReceivedOn(Date receivedOn) {
        this.receivedOn = receivedOn;
    }

    /**
     * Gets the sent on {@link Date}.
     *
     * @return The sent on {@link Date}.
     * @since 1.0.0
     */
    @XmlElement(name = "sentOn")
    public Date getSentOn() {
        return sentOn;
    }

    /**
     * Sets the sent on {@link Date}.
     *
     * @param sentOn
     *         The sent on {@link Date}.
     * @since 1.0.0
     */
    public void setSentOn(Date sentOn) {
        this.sentOn = sentOn;
    }

    /**
     * Gets the captured on {@link Date}.
     *
     * @return The captured on {@link Date}.
     * @since 1.0.0
     */
    @XmlElement(name = "capturedOn")
    public Date getCapturedOn() {
        return capturedOn;
    }

    /**
     * Sets the captured on {@link Date}.
     *
     * @param capturedOn
     *         The captured on {@link Date}.
     * @since 1.0.0
     */
    public void setCapturedOn(Date capturedOn) {
        this.capturedOn = capturedOn;
    }

    /**
     * Gets the device {@link KapuaPosition}.
     *
     * @return The device {@link KapuaPosition}.
     * @since 1.0.0
     */
    @XmlElement(name = "position")
    public KapuaPosition getPosition() {
        return position;
    }

    /**
     * Sets the device {@link KapuaPosition}.
     *
     * @param position
     *         The device {@link KapuaPosition}.
     * @since 1.0.0
     */
    public void setPosition(KapuaPosition position) {
        this.position = position;
    }

    /**
     * Gets the {@link KapuaChannel}.
     *
     * @return The {@link KapuaChannel}.
     * @since 1.0.0
     */
    @XmlElement(name = "channel")
    public C getChannel() {
        return channel;
    }

    /**
     * Sets the {@link KapuaChannel}.
     *
     * @param semanticChannel
     *         The {@link KapuaChannel}.
     * @since 1.0.0
     */
    public void setChannel(C semanticChannel) {
        this.channel = semanticChannel;
    }

    /**
     * Gets the {@link KapuaPayload}.
     *
     * @return The {@link KapuaPayload}.
     * @since 1.0.0
     */
    @XmlElement(name = "payload")
    public P getPayload() {
        return payload;
    }

    /**
     * Sets the {@link KapuaPayload}.
     *
     * @param payload
     *         The {@link KapuaPayload}.
     * @since 1.0.0
     */
    public void setPayload(P payload) {
        this.payload = payload;
    }

    public int compareTo(KapuaMessage<C, P> msg) {
        return (receivedOn.compareTo(msg.getReceivedOn()) * (-1));
    }

}
