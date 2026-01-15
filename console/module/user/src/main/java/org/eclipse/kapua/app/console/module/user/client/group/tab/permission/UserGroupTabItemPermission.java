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
package org.eclipse.kapua.app.console.module.user.client.group.tab.permission;

import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.KapuaTabItem;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

public class UserGroupTabItemPermission extends KapuaTabItem<GwtGroup> {

    private UserGroupTabPermissionGrid userGroupTabPermissionGrid;

    public UserGroupTabItemPermission(GwtSession currentSession) {
        super(currentSession, "Permission", new KapuaIcon(IconSet.CHECK_CIRCLE));

        userGroupTabPermissionGrid = new UserGroupTabPermissionGrid(null, currentSession);
        userGroupTabPermissionGrid.setRefreshOnRender(false);

        setEnabled(false);
    }

    public UserGroupTabPermissionGrid getUserGroupTabPermissionGrid() {
        return userGroupTabPermissionGrid;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        setBorders(false);

        add(userGroupTabPermissionGrid);
    }

    @Override
    public void setEntity(GwtGroup gwtGroup) {
        super.setEntity(gwtGroup);

        setEnabled(gwtGroup != null && "user".equals(gwtGroup.getDomain()));

        userGroupTabPermissionGrid.setUserGroup(gwtGroup);
    }

    @Override
    protected void doRefresh() {
        userGroupTabPermissionGrid.refresh();
    }

}
