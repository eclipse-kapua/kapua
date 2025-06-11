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
package org.eclipse.kapua.service.device.management.registry.operation.notification;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.device.management.message.notification.NotifyStatus;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link ManagementOperationNotification} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "managementOperationNotification")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = ManagementOperationNotificationXmlRegistry.class, factoryMethod = "newManagementOperationNotification")
public interface ManagementOperationNotification extends KapuaEntity {

    static final String TYPE = "managementOperationNotification";

    @Override
    @Schema(example = "managementOperationNotification")
    default String getType() {
        return TYPE;
    }

    /**
     * Gets the {@link DeviceManagementOperation#getId()}.
     *
     * @return The {@link DeviceManagementOperation#getId()}.
     * @since 1.0.0
     */
    @XmlElement(name = "operationId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "cjmXXKvLUY8")
    KapuaId getOperationId();

    /**
     * Sets the {@link DeviceManagementOperation#getId()}.
     *
     * @param operationId The {@link DeviceManagementOperation#getId()}.
     * @since 1.0.0
     */
    void setOperationId(KapuaId operationId);

    /**
     * Gets the {@link Date} of when the notification has been sent to the platform.
     *
     * @return The {@link Date} of when the notification has been sent to the platform.
     * @since 1.0.0
     */
    @XmlElement(name = "sentOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getSentOn();

    /**
     * Sets the {@link Date} of when the notification has been sent to the platform.
     *
     * @param sentOn The {@link Date} of when the notification has been sent to the platform.
     * @since 1.0.0
     */
    void setSentOn(Date sentOn);

    /**
     * Gets the {@link NotifyStatus}
     *
     * @return The {@link NotifyStatus}
     * @since 1.0.0
     */
    @XmlElement(name = "status")
    DeviceManagementOperationStatus getStatus();

    /**
     * Sets the {@link NotifyStatus}
     *
     * @param status The {@link NotifyStatus}
     * @since 1.0.0
     */
    void setStatus(DeviceManagementOperationStatus status);

    /**
     * Gets the {@link DeviceManagementOperation#getResource()}
     *
     * @return The {@link DeviceManagementOperation#getResource()}
     * @since 1.0.0
     */
    @XmlElement(name = "resource")
    @Schema(example = "download")
    String getResource();

    /**
     * Sets the {@link DeviceManagementOperation#getResource()}
     *
     * @param resource The {@link DeviceManagementOperation#getResource()}
     * @since 1.0.0
     */
    void setResource(String resource);

    /**
     * Gets the progress percentage of the processing.
     *
     * @return The progress percentage of the processing.
     * @since 1.0.0
     */
    @XmlElement(name = "progress")
    Integer getProgress();

    /**
     * Sets the progress percentage of the processing.
     *
     * @param progress The progress percentage of the processing.
     * @since 1.0.0
     */
    void setProgress(Integer progress);

    /**
     * Gets the detailed message related to the {@link NotifyStatus}
     *
     * @return The detailed message related to the {@link NotifyStatus}
     * @since 1.2.0
     */
    String getMessage();

    /**
     * Sets the detailed message related to the {@link NotifyStatus}
     *
     * @param message The detailed message related to the {@link NotifyStatus}
     * @since 1.2.0
     */
    void setMessage(String message);
}
