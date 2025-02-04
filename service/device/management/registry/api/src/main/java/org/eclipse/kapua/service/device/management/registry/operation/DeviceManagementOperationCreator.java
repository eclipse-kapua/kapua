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
package org.eclipse.kapua.service.device.management.registry.operation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.management.message.notification.NotifyStatus;

@XmlRootElement(name = "deviceManagementOperationCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceManagementOperationCreator extends KapuaEntityCreator {

    private Date startedOn;
    private KapuaId deviceId;
    private KapuaId operationId;
    private String appId;
    private KapuaMethod action;
    private String resource;
    private NotifyStatus status;
    private List<DeviceManagementOperationProperty> inputProperties;

    public DeviceManagementOperationCreator() {
    }

    public DeviceManagementOperationCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public DeviceManagementOperationCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    @XmlElement(name = "startedOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }

    @XmlElement(name = "deviceId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(KapuaId deviceId) {
        this.deviceId = deviceId;
    }

    @XmlElement(name = "operationId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getOperationId() {
        return operationId;
    }

    public void setOperationId(KapuaId operationId) {
        this.operationId = operationId;
    }

    @XmlElement(name = "appId")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @XmlElement(name = "action")
    public KapuaMethod getAction() {
        return action.normalizeAction();
    }

    public void setAction(KapuaMethod action) {
        this.action = action.normalizeAction();
    }

    @XmlElement(name = "resource")
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @XmlElement(name = "status")
    public NotifyStatus getStatus() {
        return status;
    }

    public void setStatus(NotifyStatus status) {
        this.status = status;
    }

    @XmlElementWrapper(name = "operationProperties")
    @XmlElement(name = "operationProperty")
    public List<DeviceManagementOperationProperty> getInputProperties() {
        if (inputProperties == null) {
            inputProperties = new ArrayList<>();
        }

        return inputProperties;
    }

    public void setInputProperties(List<DeviceManagementOperationProperty> inputProperties) {
        this.inputProperties = inputProperties;
    }
}
