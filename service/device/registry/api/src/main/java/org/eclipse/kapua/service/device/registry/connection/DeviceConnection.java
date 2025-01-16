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
package org.eclipse.kapua.service.device.registry.connection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.device.registry.ConnectionUserCouplingMode;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceConnection} {@link KapuaEntity} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceConnection")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceConnectionXmlRegistry.class, factoryMethod = "newDeviceConnection")
public interface DeviceConnection extends KapuaUpdatableEntity {

    String TYPE = "deviceConnection";

    @Override
    @Schema(example = "deviceConnection")
    default String getType() {
        return TYPE;
    }

    /**
     * Get the device connection status
     *
     * @return
     */
    @XmlElement(name = "status")
    DeviceConnectionStatus getStatus();

    /**
     * Set the device connection status
     *
     * @param status
     */
    void setStatus(DeviceConnectionStatus status);

    /**
     * Get the client identifier
     *
     * @return
     */
    @XmlElement(name = "clientId")
    @Schema(example = "Client-Id-1")
    String getClientId();

    /**
     * Set the client identifier
     *
     * @param clientId
     */
    void setClientId(String clientId);

    /**
     * Get the user identifier
     *
     * @return
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getUserId();

    /**
     * Set the user identifier
     *
     * @param userId
     */
    void setUserId(KapuaId userId);

    /**
     * Gets whether or not the {@link DeviceConnection} can change user on the next login.
     *
     * @return <code>true</code> if device can changhe user to connect, <code>false</code> if not.
     */
    @XmlElement(name = "allowUserChange")
    boolean getAllowUserChange();

    /**
     * Sets whether or not the {@link DeviceConnection} can change user on the next login.
     *
     * @param allowUserChange
     */
    void setAllowUserChange(boolean allowUserChange);

    /**
     * Get the device connection user coupling mode.
     *
     * @return
     */
    @XmlElement(name = "userCouplingMode")
    ConnectionUserCouplingMode getUserCouplingMode();

    /**
     * Set the device connection user coupling mode.
     *
     * @param userCouplingMode
     */
    void setUserCouplingMode(ConnectionUserCouplingMode userCouplingMode);

    /**
     * Get the reserved user identifier
     *
     * @return
     */
    @XmlElement(name = "reservedUserId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getReservedUserId();

    /**
     * Set the reserved user identifier
     *
     * @param reservedUserId
     */
    void setReservedUserId(KapuaId reservedUserId);

    /**
     * Gets the allowed authentication type.
     *
     * @return The allowed authentication type.
     * @since 2.0.0
     */
    @Schema(description = "The authentication type that must be used when connection")
    String getAuthenticationType();

    /**
     * Sets the allowed authentication type.
     *
     * @param authenticationType The allowed authentication type.
     * @since 2.0.0
     */
    void setAuthenticationType(String authenticationType);

    /**
     * Gets the last used authentication type.
     *
     * @return The last used authentication type.
     * @since 2.0.0
     */
    @Schema(description = "The authentication type used when connecting last time")
    String getLastAuthenticationType();

    /**
     * Sets the last used authentication type.
     *
     * @param lastAuthenticationType The last used authentication type.
     * @since 2.0.0
     */
    void setLastAuthenticationType(String lastAuthenticationType);

    /**
     * Get the device protocol
     *
     * @return
     */
    @XmlElement(name = "protocol")
    @Schema(example = "MQTT")
    String getProtocol();

    /**
     * Set the device protocol
     *
     * @param protocol
     */
    void setProtocol(String protocol);

    /**
     * Get the client ip
     *
     * @return
     */
    @XmlElement(name = "clientIp")
    @Schema(description = "tcp://172.21.0.1:44400")
    String getClientIp();

    /**
     * Set the client ip
     *
     * @param clientIp
     */
    void setClientIp(String clientIp);

    /**
     * Get the server ip
     *
     * @return
     */
    @XmlElement(name = "serverIp")
    @Schema(description = "broker")
    String getServerIp();

    /**
     * Set the server ip
     *
     * @param serverIp
     */
    void setServerIp(String serverIp);
}
