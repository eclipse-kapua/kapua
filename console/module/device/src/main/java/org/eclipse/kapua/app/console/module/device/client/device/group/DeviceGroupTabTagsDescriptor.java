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
package org.eclipse.kapua.app.console.module.device.client.device.group;

import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.AbstractEntityTabDescriptor;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.tabs.groups.GroupTabTags;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.tag.shared.model.permission.TagSessionPermission;

public class DeviceGroupTabTagsDescriptor extends AbstractEntityTabDescriptor<GwtGroup, GroupTabTags, DeviceGroupView> {

    @Override
    public GroupTabTags getTabViewInstance(DeviceGroupView view, GwtSession currentSession) {
        return new GroupTabTags(currentSession);
    }

    @Override
    public String getViewId() {
        return "group.tags";
    }

    @Override
    public Integer getOrder() {
        return 200;
    }

    @Override
    public Boolean isEnabled(GwtSession currentSession) {
        return currentSession.hasPermission(TagSessionPermission.read());
    }
}
