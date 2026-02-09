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

import org.eclipse.kapua.app.console.module.api.shared.model.GwtEntityCreator;

public class GwtGroupRoleCreator extends GwtEntityCreator {

    private static final long serialVersionUID = -1333808048669893906L;

    private String userGroupId;

    private String roleId;

    public GwtGroupRoleCreator() {
        super();
    }

    public String getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(String name) {
        this.userGroupId = name;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
