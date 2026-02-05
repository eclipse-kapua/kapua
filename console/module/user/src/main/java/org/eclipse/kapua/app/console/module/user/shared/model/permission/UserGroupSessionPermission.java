/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.user.shared.model.permission;

import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermission;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermissionAction;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermissionScope;

public class UserGroupSessionPermission extends GwtSessionPermission {

    protected UserGroupSessionPermission() {
        super();
    }

    private UserGroupSessionPermission(GwtSessionPermissionAction action) {
        super("user_group", action, GwtSessionPermissionScope.SELF);
    }

    public static UserGroupSessionPermission read() {
        return new UserGroupSessionPermission(GwtSessionPermissionAction.read);
    }

    public static UserGroupSessionPermission write() {
        return new UserGroupSessionPermission(GwtSessionPermissionAction.write);
    }

    public static UserGroupSessionPermission delete() {
        return new UserGroupSessionPermission(GwtSessionPermissionAction.delete);
    }
}
