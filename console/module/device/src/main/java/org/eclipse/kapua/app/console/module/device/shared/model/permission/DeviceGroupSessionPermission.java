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
package org.eclipse.kapua.app.console.module.device.shared.model.permission;

import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermission;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermissionAction;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermissionScope;

public class DeviceGroupSessionPermission extends GwtSessionPermission {

    protected DeviceGroupSessionPermission() {
        super();
    }

    private DeviceGroupSessionPermission(GwtSessionPermissionAction action) {
        super("device_group", action, GwtSessionPermissionScope.SELF);
    }

    public static DeviceGroupSessionPermission read() {
        return new DeviceGroupSessionPermission(GwtSessionPermissionAction.read);
    }

    public static DeviceGroupSessionPermission write() {
        return new DeviceGroupSessionPermission(GwtSessionPermissionAction.write);
    }

    public static DeviceGroupSessionPermission delete() {
        return new DeviceGroupSessionPermission(GwtSessionPermissionAction.delete);
    }
}
