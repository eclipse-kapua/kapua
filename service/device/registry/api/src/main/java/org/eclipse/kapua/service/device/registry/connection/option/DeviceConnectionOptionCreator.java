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
package org.eclipse.kapua.service.device.registry.connection.option;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.ConnectionUserCouplingMode;

/**
 * Device connection options creator service definition.
 *
 * @since 1.0
 */
public class DeviceConnectionOptionCreator extends KapuaUpdatableEntityCreator {

    private static final long serialVersionUID = 2740394157765904615L;

    private ConnectionUserCouplingMode userCouplingMode;
    private KapuaId reservedUserId;

    private String authenticationType;

    public DeviceConnectionOptionCreator() {
    }

    public DeviceConnectionOptionCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public DeviceConnectionOptionCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
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
}
