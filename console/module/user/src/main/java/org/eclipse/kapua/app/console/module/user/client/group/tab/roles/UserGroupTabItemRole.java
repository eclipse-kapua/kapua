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
package org.eclipse.kapua.app.console.module.user.client.group.tab.roles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.KapuaTabItem;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsolePermissionMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

public class UserGroupTabItemRole extends KapuaTabItem<GwtGroup> {

    private static final ConsolePermissionMessages MSGS = GWT.create(ConsolePermissionMessages.class);

    private UserGroupTabRoleGrid groupRoleGrid;

    public UserGroupTabItemRole(GwtSession currentSession) {
        super(currentSession, "Roles", new KapuaIcon(IconSet.STREET_VIEW));

        groupRoleGrid = new UserGroupTabRoleGrid(currentSession, null);
        groupRoleGrid.setRefreshOnRender(false);

        setEnabled(false);
    }

    public UserGroupTabRoleGrid getGroupRoleGrid() {
        return groupRoleGrid;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        add(groupRoleGrid);
    }

    @Override
    public void setEntity(GwtGroup gwtGroup) {
        super.setEntity(gwtGroup);

        setEnabled(gwtGroup != null && "user".equals(gwtGroup.getDomain()));

        groupRoleGrid.setUserGroup(gwtGroup);
    }

    @Override
    protected void doRefresh() {
        groupRoleGrid.refresh();
    }

}
