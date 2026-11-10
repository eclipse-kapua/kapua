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

import com.google.gwt.user.client.rpc.IsSerializable;
import org.eclipse.kapua.app.console.module.api.shared.model.KapuaBaseModel;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates.GwtSortAttribute;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates.GwtSortOrder;

import java.io.Serializable;

public class GwtDeviceConnectionQueryPredicates extends KapuaBaseModel implements Serializable {

    private static final long serialVersionUID = 571130152596171388L;

    private static final String CLIENT_ID = "clientId";

    public GwtDeviceConnectionQueryPredicates() {
        setSortAttribute(GwtSortAttribute.CLIENT_ID.name());
        setSortOrder(GwtSortOrder.ASCENDING.name());
    }

    public enum GwtDeviceConnectionUser implements IsSerializable {
        ANY, NONE;

        private GwtDeviceConnectionUser() {
        }
    }

    public enum GwtDeviceConnectionReservedUser implements IsSerializable {
        ANY, NONE;

        private GwtDeviceConnectionReservedUser() {
        }
    }

    @Override
    public <X> X get(String property) {
        if ("statusEnum".equals(property)) {
            return (X) (GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.valueOf(getConnectionStatus()));
        } else if ("deviceConnectionUserEnum".equals(property)) {
            return (X) (GwtDeviceConnectionQueryPredicates.GwtDeviceConnectionUser.valueOf(getDeviceConnectionUser()));
        } else {
            return super.get(property);
        }
    }

    public String getClientId() {
        return (String) get(CLIENT_ID);
    }

    public String getUnescapedClientId() {
        return (String) getUnescaped(CLIENT_ID);
    }

    public void setClientId(String clientId) {
        set(CLIENT_ID, clientId);
    }

    public String getConnectionStatus() {
        return get("connectionStatus");
    }

    public GwtDeviceQueryPredicates.GwtDeviceConnectionStatus getConnectionStatusEnum() {
        return get("connectionStatusEnum");
    }

    public void setConnectionStatus(String deviceConnectionStatus) {
        set("connectionStatus", deviceConnectionStatus);
    }

    public String getSortOrder() {
        return (String) get("sortOrder");
    }

    public GwtSortOrder getSortOrderEnum() {
        return get("sortOrderEnum");
    }

    public void setSortOrder(String sortOrder) {
        set("sortOrder", sortOrder);
    }

    public String getSortAttribute() {
        return (String) get("sortAttribute");
    }

    public GwtSortAttribute getSortAttributeEnum() {
        return get("sortAttributeEnum");
    }

    public void setSortAttribute(String sortAttribute) {
        set("sortAttribute", sortAttribute);
    }

    public String getUserId() {
        return (String) get("userId");
    }

    public void setUserId(String userId) {
        set("userId", userId);
    }

    public GwtDeviceConnectionUser getDeviceConnectionUserEnum() {
        return get("deviceConnectionUserEnum");
    }

    public void setDeviceConnectionUser(String deviceConnectionUser) {
        set("deviceConnectionUser", deviceConnectionUser);
    }

    public String getDeviceConnectionUser() {
        return (String) get("deviceConnectionUser");
    }

}
