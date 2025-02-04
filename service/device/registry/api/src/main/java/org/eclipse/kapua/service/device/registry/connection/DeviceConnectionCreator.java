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

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.ConnectionUserCouplingMode;

/**
 * Device connection creator service definition.
 *
 * @since 1.0
 */
public class DeviceConnectionCreator extends KapuaUpdatableEntityCreator {

    private static final long serialVersionUID = 2740394157765904615L;

    private DeviceConnectionStatus status;
    private String clientId;
    private KapuaId userId;
    private ConnectionUserCouplingMode userCouplingMode;
    private KapuaId reservedUserId;
    private boolean allowUserChange;
    private String authenticationType;
    private String lastAuthenticationType;
    private String protocol;
    private String clientIp;
    private String serverIp;

    public DeviceConnectionCreator() {
    }

    public DeviceConnectionCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public DeviceConnectionCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    /**
     * Get the device connection status
     *
     * @return
     */
    @XmlElement(name = "status")
    public DeviceConnectionStatus getStatus() {
        return status;
    }

    /**
     * Set the device connection status
     *
     * @param status
     */
    public void setStatus(DeviceConnectionStatus status) {
        this.status = status;
    }

    /**
     * Get the client identifier
     *
     * @return
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Set the client identifier
     *
     * @param clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Get the user identifier
     *
     * @return
     */
    public KapuaId getUserId() {
        return userId;
    }

    /**
     * Set the user identifier
     *
     * @param userId
     */
    public void setUserId(KapuaId userId) {
        this.userId = userId;
    }

    /**
     * Get the device connection user coupling mode.
     *
     * @return
     */
    public ConnectionUserCouplingMode getUserCouplingMode() {
        return userCouplingMode;
    }

    /**
     * Set the device connection user coupling mode.
     *
     * @param userCouplingMode
     */
    public void setUserCouplingMode(ConnectionUserCouplingMode userCouplingMode) {
        this.userCouplingMode = userCouplingMode;
    }

    /**
     * Get the reserved user identifier
     *
     * @return
     */
    public KapuaId getReservedUserId() {
        return reservedUserId;
    }

    /**
     * Set the reserved user identifier
     *
     * @param reservedUserId
     */
    public void setReservedUserId(KapuaId reservedUserId) {
        this.reservedUserId = reservedUserId;
    }

    /**
     * Gets whether or not the {@link DeviceConnection} can change user on the next login.
     *
     * @return <code>true</code> if device can changhe user to connect, <code>false</code> if not.
     */
    @XmlElement(name = "allowUserChange")
    public boolean getAllowUserChange() {
        return allowUserChange;
    }

    /**
     * Sets whether or not the {@link DeviceConnection} can change user on the next login.
     *
     * @param allowUserChange
     */
    public void setAllowUserChange(boolean allowUserChange) {
        this.allowUserChange = allowUserChange;
    }

    /**
     * Gets the allowed authentication type.
     *
     * @return The allowed authentication type.
     * @since 2.0.0
     */
    public String getAuthenticationType() {
        return authenticationType;
    }

    /**
     * Sets the allowed authentication type.
     *
     * @param authenticationType
     *         The allowed authentication type.
     * @since 2.0.0
     */
    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    /**
     * Gets the last used authentication type.
     *
     * @return The last used authentication type.
     * @since 2.0.0
     */
    public String getLastAuthenticationType() {
        return lastAuthenticationType;
    }

    /**
     * Sets the last used authentication type.
     *
     * @param lastAuthenticationType
     *         The last used authentication type.
     * @since 2.0.0
     */
    public void setLastAuthenticationType(String lastAuthenticationType) {
        this.lastAuthenticationType = lastAuthenticationType;
    }

    /**
     * Get the device protocol
     *
     * @return
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Set the device protocol
     *
     * @param protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Get the client ip
     *
     * @return
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * Set the client ip
     *
     * @param clientIp
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * Get the server ip
     *
     * @return
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * Set the server ip
     *
     * @param serverIp
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

}
