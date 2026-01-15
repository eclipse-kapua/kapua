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

import org.eclipse.kapua.app.console.module.api.shared.model.GwtEntityCreator;

public class GwtGroupPermissionCreator extends GwtEntityCreator {

    private String groupId;

    private GwtPermission permission;

    public GwtGroupPermissionCreator() {
        super();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String name) {
        this.groupId = name;
    }

    public GwtPermission getPermission() {
        return permission;
    }

    public void setPermission(GwtPermission permission) {
        this.permission = permission;
    }

}
