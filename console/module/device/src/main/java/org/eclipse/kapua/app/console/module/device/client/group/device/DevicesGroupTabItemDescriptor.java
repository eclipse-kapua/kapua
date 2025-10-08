/*******************************************************************************
 * Copyright (c) 2018, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.device.client.group.device;

import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.AbstractEntityTabDescriptor;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.group.GroupView;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.device.shared.model.permission.DeviceSessionPermission;

public class DevicesGroupTabItemDescriptor extends AbstractEntityTabDescriptor<GwtGroup, DevicesGroupTabItem, GroupView> {

    @Override
    public String getViewId() {
        return "group.devices";
    }

    @Override
    public Integer getOrder() {
        return 200;
    }

    @Override
    public DevicesGroupTabItem getTabViewInstance(GroupView view, GwtSession currentSession) {
        return new DevicesGroupTabItem(currentSession);
    }

    @Override
    public Boolean isEnabled(GwtSession currentSession) {
        return currentSession.hasPermission(DeviceSessionPermission.read());
    }
}
