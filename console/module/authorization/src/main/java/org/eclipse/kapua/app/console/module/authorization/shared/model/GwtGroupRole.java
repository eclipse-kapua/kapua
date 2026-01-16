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
package org.eclipse.kapua.app.console.module.authorization.shared.model;

import org.eclipse.kapua.app.console.module.api.shared.model.GwtUpdatableEntityModel;

public class GwtGroupRole extends GwtUpdatableEntityModel {

    private static final long serialVersionUID = 1330881042880793119L;

    public String getUserGroupId() {
        return get("userGroupId");
    }

    public void setUserGroupId(String userGroupId) {
        set("userGroupId", userGroupId);
    }

    public String getRoleId() {
        return get("roleId");
    }

    public void setRoleId(String roleId) {
        set("roleId", roleId);
    }

    public String getRoleName() {
        return get("roleName");
    }

    public void setRoleName(String roleName) {
        set("roleName", roleName);
    }

}
