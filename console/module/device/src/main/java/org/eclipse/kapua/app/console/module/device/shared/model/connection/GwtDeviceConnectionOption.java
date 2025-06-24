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
package org.eclipse.kapua.app.console.module.device.shared.model.connection;

import org.eclipse.kapua.app.console.module.api.shared.model.GwtUpdatableEntityModel;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection.GwtConnectionUserCouplingMode;

import java.io.Serializable;

public class GwtDeviceConnectionOption extends GwtUpdatableEntityModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5059430797167078209L;

    public GwtDeviceConnectionOption() {
    }

    public GwtDeviceConnectionOption(GwtDeviceConnection gwtDeviceConnection) {
        setId(gwtDeviceConnection.getId());
        setScopeId(gwtDeviceConnection.getScopeId());
        setConnectionUserCouplingMode(gwtDeviceConnection.getConnectionUserCouplingMode());
        setUserId(gwtDeviceConnection.getUserId());
        setReservedUserId(gwtDeviceConnection.getUserId());
        setAllowUserChange(gwtDeviceConnection.getAllowUserChange());
        setAuthenticationType(gwtDeviceConnection.getAuthenticationType());
    }

    @Override
    public <X> X get(String property) {
        if ("connectionUserCouplingModeEnum".equals(property)) {
            return (X) GwtConnectionUserCouplingMode.getEnumFromLabel(getConnectionUserCouplingMode());
        } else {
            return super.get(property);
        }
    }

    public String getConnectionUserCouplingMode() {
        return get("connectionUserCouplingMode");
    }

    public GwtConnectionUserCouplingMode getConnectionUserCouplingModeEnum() {
        return get("connectionUserCouplingModeEnum");
    }

    public void setConnectionUserCouplingMode(String connectionUserCouplingMode) {
        set("connectionUserCouplingMode", connectionUserCouplingMode);
    }

    public String getUserId() {
        return get("userId");
    }

    public void setUserId(String userId) {
        set("userId", userId);
    }

    public String getReservedUserId() {
        return get("reservedUserId");
    }

    public void setReservedUserId(String userId) {
        set("reservedUserId", userId);
    }

    public Boolean getAllowUserChange() {
        return get("allowUserChange");
    }

    public void setAllowUserChange(Boolean allowUserChange) {
        set("allowUserChange", allowUserChange);
    }

    public String getAuthenticationType() {
        return get("authenticationType");
    }

    public void setAuthenticationType(String authenticationType) {
        set("authenticationType", authenticationType);
    }
}
