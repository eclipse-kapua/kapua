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
package org.eclipse.kapua.app.console.module.device.client.device.group;

import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.AbstractEntityTabDescriptor;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.permission.GroupSessionPermission;
import org.eclipse.kapua.app.console.module.device.client.device.DeviceView;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;

public class DeviceTabGroupsDescriptor extends AbstractEntityTabDescriptor<GwtDevice, DeviceTabGroups, DeviceView> {

    @Override
    public DeviceTabGroups getTabViewInstance(DeviceView view, GwtSession currentSession) {
        return new DeviceTabGroups(currentSession);
    }

    @Override
    public String getViewId() {
        return "device.groups";
    }

    @Override
    public Integer getOrder() {
        return 150;
    }

    @Override
    public Boolean isEnabled(GwtSession currentSession) {
        return currentSession.hasPermission(GroupSessionPermission.read());
    }
}
