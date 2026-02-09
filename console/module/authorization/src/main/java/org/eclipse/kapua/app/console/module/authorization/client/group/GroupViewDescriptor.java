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
package org.eclipse.kapua.app.console.module.authorization.client.group;

import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.ui.view.EntityView;
import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.AbstractEntityViewDescriptor;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.permission.GroupSessionPermission;

public class GroupViewDescriptor extends AbstractEntityViewDescriptor<GwtGroup> {

    @Override
    public String getViewId() {
        return "groups";
    }

    @Override
    public IconSet getIcon() {
        return IconSet.OBJECT_GROUP;
    }

    @Override
    public Integer getOrder() {
        return 900;
    }

    @Override
    public String getName() {
        return GroupView.getName();
    }

    @Override
    public EntityView<GwtGroup> getViewInstance(GwtSession currentSession) {
        return new GroupView(currentSession, new AccessGroupDataProvider());
    }

    @Override
    public Boolean isEnabled(GwtSession currentSession) {
        return currentSession.hasPermission(GroupSessionPermission.read());
    }
}
