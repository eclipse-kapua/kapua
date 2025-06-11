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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceEvent} entity definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceEvent")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { //
        "deviceId", //
        "sentOn", //
        "receivedOn", //
        "position", //
        "resource",  //
        "action", //
        "responseCode", //
        "eventMessage" //
}, //
        factoryClass = DeviceEventXmlRegistry.class, //
        factoryMethod = "newDeviceEvent")
public interface DeviceEvent extends KapuaEntity {

    String TYPE = "deviceEvent";

    @Override
    default String getType() {
        return TYPE;
    }

    /**
     * Get the device identifier
     *
     * @return The device identifier.
     * @since 1.0.0
     */
    @XmlElement(name = "deviceId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "WyczTs_GuDM")
    KapuaId getDeviceId();

    /**
     * Set the device identifier
     *
     * @param deviceId
     * @since 1.0.0
     */
    void setDeviceId(KapuaId deviceId);

    /**
     * Get the sent on date
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "sentOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getSentOn();

    /**
     * Set the sent on date
     *
     * @param sentOn
     * @since 1.0.0
     */
    void setSentOn(Date sentOn);

    /**
     * Get the received on date
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "receivedOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getReceivedOn();

    /**
     * Set the received on date
     *
     * @param receivedOn
     * @since 1.0.0
     */
    void setReceivedOn(Date receivedOn);

    /**
     * Get the device position
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "position")
    KapuaPosition getPosition();

    /**
     * Set the device position
     *
     * @param position
     * @since 1.0.0
     */
    void setPosition(KapuaPosition position);

    /**
     * Get resource
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "resource")
    @Schema(example = "BIRTH")
    String getResource();

    /**
     * Set resource
     *
     * @param resource
     */
    void setResource(String resource);

    /**
     * Get action
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "action")
    KapuaMethod getAction();

    /**
     * Set action
     *
     * @param action
     * @since 1.0.0
     */
    void setAction(KapuaMethod action);

    /**
     * Get response code
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "responseCode")
    KapuaResponseCode getResponseCode();

    /**
     * Set the response code
     *
     * @param responseCode
     * @since 1.0.0
     */
    void setResponseCode(KapuaResponseCode responseCode);

    /**
     * Get event message
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "eventMessage")
    @Schema(
        example = "acceptEncoding=gzip~~applicationFramework=Kura~~applicationFrameworkVersion=ESF_6.0" +
                      ".0~~applicationIdentifiers=heaterPROV-V2DEPLOY-V2VPNCLIENT-V2CONF-V1CERT-V1ASSET-V1CMD-V1" +
                      "~~availableProcessors=4~~bios=N/A~~biosVersion=N/A~~connectionInterface=lo (00:00:00:00:00:00)" +
                      "~~connectionIp=127.0.0.1~~containerFramework=Eclipse~~containerFrameworkVersion=1.8" +
                      ".0~~displayName=~~firmware=N/A~~firmwareVersion=N/A~~jvm=Java HotSpot(TM) 64-Bit Server " +
                      "VM~~jvmProfile=Java(TM) SE Runtime Environment 1.8.0_161-b12~~jvmVersion=25.161-b12 mixed " +
                      "mode~~modelId=ESF-Docker-RHEL~~modelName=ESF-Docker-RHEL~~os=Linux~~osArch=amd64~~osVersion=4" +
                      ".9.184-linuxkit #1 SMP Tue Jul 2 22:58:16 UTC " +
                      "2019~~partNumber=ESF-Docker-RHEL~~serialNumber=ESF-Docker-RHEL~~totalMemory=1023488~~uptime" +
                      "=187894320"
    )
    String getEventMessage();

    /**
     * Set the event message
     *
     * @param eventMessage
     * @since 1.0.0
     */
    void setEventMessage(String eventMessage);

}
