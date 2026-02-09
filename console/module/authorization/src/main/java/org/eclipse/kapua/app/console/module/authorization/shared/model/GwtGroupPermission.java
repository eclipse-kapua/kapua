/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.authorization.shared.model;

import org.eclipse.kapua.app.console.module.api.shared.model.GwtUpdatableEntityModel;

public class GwtGroupPermission extends GwtUpdatableEntityModel {

    private static final long serialVersionUID = 1330881042880793119L;

    public String getGroupId() {
        return get("groupId");
    }

    public void setGroupId(String groupId) {
        set("groupId", groupId);
    }

    public String getPermissionDomain() {
        return get("permissionDomain");
    }

    public void setPermissionDomain(String permissionDomain) {
        set("permissionDomain", permissionDomain == null ? "ALL" : permissionDomain);
    }

    public String getPermissionAction() {
        return get("permissionAction");
    }

    public void setPermissionAction(String permissionAction) {
        set("permissionAction", permissionAction == null ? "ALL" : permissionAction);
    }

    public String getPermissionTargetScopeId() {
        return get("permissionTargetScopeId");
    }

    public void setPermissionTargetScopeId(String permissionTargetScopeId) {
        set("permissionTargetScopeId", permissionTargetScopeId == null ? "ALL" : permissionTargetScopeId);
    }

    public String getPermissionTargetScopeIdByName() {
        return get("permissionTargetScopeIdByName");
    }

    public void setPermissionTargetScopeIdByName(String permissionTargetScopeIdByName) {
        set("permissionTargetScopeIdByName", permissionTargetScopeIdByName == null ? "ALL" : permissionTargetScopeIdByName);
    }

    public String getPermissionGroupId() {
        return get("permissionGroupId");
    }

    public void setPermissionGroupId(String permissionGroupId) {
        set("permissionGroupId", permissionGroupId == null ? "ALL" : permissionGroupId);
    }

    public boolean getPermissionForwardable() {
        return get("permissionForwardable");
    }

    public void setPermissionForwardable(boolean forwardable) {
        set("permissionForwardable", forwardable);
    }

    public String getGroupName() {
        return get("groupName");
    }

    public void setGroupName(String groupName) {
        set("groupName", groupName);
    }

    /**
     * Returns the string representation for this {@link GwtPermission} in the following format:
     * <p>
     * {domain}[:{action}[:{targetScopeId]]
     * </p>
     *
     * @return the formatted string representation of this {@link GwtPermission}
     * @since 2.1.0
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(getPermissionDomain());

        sb.append(":");
        if (getPermissionAction() != null) {
            sb.append(getPermissionAction());
        } else {
            sb.append("*");
        }

        sb.append(":");
        if (getPermissionTargetScopeId() != null) {
            sb.append(getPermissionTargetScopeId());
        } else {
            sb.append("*");
        }

        sb.append(":");
        if (getPermissionGroupId() != null) {
            sb.append(getPermissionGroupId());
        } else {
            sb.append("*");
        }

        return sb.toString();
    }
}
