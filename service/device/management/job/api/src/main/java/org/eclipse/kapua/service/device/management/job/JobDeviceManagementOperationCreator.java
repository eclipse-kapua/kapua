/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link JobDeviceManagementOperationCreator} encapsulates all the information needed to create a new {@link JobDeviceManagementOperation} in the system.<br> The data provided will be used to seed
 * the new {@link JobDeviceManagementOperation}.
 *
 * @since 1.1.0
 */
@XmlRootElement(name = "jobDeviceManagementOperationCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobDeviceManagementOperationCreator extends KapuaEntityCreator {

    private KapuaId jobId;
    private KapuaId deviceManagementOperationId;

    public JobDeviceManagementOperationCreator() {
    }

    public JobDeviceManagementOperationCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public JobDeviceManagementOperationCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.job.Job} {@link KapuaId}.
     *
     * @return The {@link org.eclipse.kapua.service.job.Job} {@link KapuaId}.
     * @since 1.1.0
     */
    public KapuaId getJobId() {
        return jobId;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.job.Job} {@link KapuaId}.
     *
     * @param jobId
     *         The {@link org.eclipse.kapua.service.job.Job} {@link KapuaId}.
     * @since 1.1.0
     */
    public void setJobId(KapuaId jobId) {
        this.jobId = jobId;
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation} {@link KapuaId}.
     *
     * @return The {@link org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation} {@link KapuaId}.
     * @since 1.1.0
     */
    public KapuaId getDeviceManagementOperationId() {
        return deviceManagementOperationId;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation} {@link KapuaId}.
     *
     * @param deviceManagementOperationId
     *         The {@link org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation} {@link KapuaId}.
     * @since 1.1.0
     */
    public void setDeviceManagementOperationId(KapuaId deviceManagementOperationId) {
        this.deviceManagementOperationId = KapuaEid.parseKapuaId(deviceManagementOperationId);
    }
}
