/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.jersey.rest.model.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.device.management.exception.DeviceNotConnectedException;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;

@XmlRootElement(name = "deviceNotConnectedExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceNotConnectedExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "deviceId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private KapuaId deviceId;

    @XmlElement(name = "connectionStatus")
    private DeviceConnectionStatus connectionStatus;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected DeviceNotConnectedExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param deviceNotConnectedException
     *         The root exception.
     * @since 1.0.0
     */
    public DeviceNotConnectedExceptionInfo(DeviceNotConnectedException deviceNotConnectedException, boolean showStackTrace) {
        super(409/*Response.Status.CONFLICT*/, deviceNotConnectedException, showStackTrace);

        this.deviceId = deviceNotConnectedException.getDeviceId();
        this.connectionStatus = deviceNotConnectedException.getCurrentConnectionStatus();
    }

    /**
     * Gets the {@link DeviceNotConnectedException#getDeviceId()}.
     *
     * @return The {@link DeviceNotConnectedException#getDeviceId()}.
     * @since 1.0.0
     */
    public KapuaId getDeviceId() {
        return deviceId;
    }

    /**
     * Gets the {@link DeviceNotConnectedException#getCurrentConnectionStatus()}.
     *
     * @return The {@link DeviceNotConnectedException#getCurrentConnectionStatus()}.
     * @since 1.0.0
     */
    public DeviceConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
}
