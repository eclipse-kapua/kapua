/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.registry.operation.notification;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.device.management.message.notification.NotifyStatus;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationStatus;

/**
 * {@link ManagementOperationNotificationCreator} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceManagementOperationNotificationCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class ManagementOperationNotificationCreator extends KapuaEntityCreator<ManagementOperationNotification> {

    private KapuaId operationId;
    private Date sentOn;
    private DeviceManagementOperationStatus status;
    private String resource;
    private Integer progress;
    private String message;

    public ManagementOperationNotificationCreator() {
    }

    public ManagementOperationNotificationCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public ManagementOperationNotificationCreator(KapuaEntityCreator<ManagementOperationNotification> entityCreator) {
        super(entityCreator);
    }

    /**
     * Gets the {@link DeviceManagementOperation#getId()}.
     *
     * @return The {@link DeviceManagementOperation#getId()}.
     * @since 1.0.0
     */
    @XmlElement(name = "operationId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getOperationId() {
        return operationId;
    }

    /**
     * Sets the {@link DeviceManagementOperation#getId()}.
     *
     * @param operationId
     *         The {@link DeviceManagementOperation#getId()}.
     * @since 1.0.0
     */
    public void setOperationId(KapuaId operationId) {
        this.operationId = operationId;
    }

    /**
     * Gets the {@link Date} of when the notification has been sent to the platform.
     *
     * @return The {@link Date} of when the notification has been sent to the platform.
     * @since 1.0.0
     */
    @XmlElement(name = "sentOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getSentOn() {
        return sentOn;
    }

    /**
     * Sets the {@link Date} of when the notification has been sent to the platform.
     *
     * @param sentOn
     *         The {@link Date} of when the notification has been sent to the platform.
     * @since 1.0.0
     */
    public void setSentOn(Date sentOn) {
        this.sentOn = sentOn;
    }

    /**
     * Gets the {@link NotifyStatus}
     *
     * @return The {@link NotifyStatus}
     * @since 1.0.0
     */
    @XmlElement(name = "status")
    public DeviceManagementOperationStatus getStatus() {
        return status;
    }

    /**
     * Sets the {@link NotifyStatus}
     *
     * @param status
     *         The {@link NotifyStatus}
     * @since 1.0.0
     */
    public void setStatus(DeviceManagementOperationStatus status) {
        this.status = status;
    }

    /**
     * Gets the {@link DeviceManagementOperation#getResource()}
     *
     * @return The {@link DeviceManagementOperation#getResource()}
     * @since 1.0.0
     */
    @XmlElement(name = "resource")
    public String getResource() {
        return resource;
    }

    /**
     * Sets the {@link DeviceManagementOperation#getResource()}
     *
     * @param resource
     *         The {@link DeviceManagementOperation#getResource()}
     * @since 1.0.0
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Gets the progress percentage of the processing.
     *
     * @return The progress percentage of the processing.
     * @since 1.0.0
     */
    @XmlElement(name = "progress")
    public Integer getProgress() {
        return progress;
    }

    /**
     * Sets the progress percentage of the processing.
     *
     * @param progress
     *         The progress percentage of the processing.
     * @since 1.0.0
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * Gets the detailed message related to the {@link NotifyStatus}
     *
     * @return The detailed message related to the {@link NotifyStatus}
     * @since 1.2.0
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the detailed message related to the {@link NotifyStatus}
     *
     * @param message
     *         The detailed message related to the {@link NotifyStatus}
     * @since 1.2.0
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
