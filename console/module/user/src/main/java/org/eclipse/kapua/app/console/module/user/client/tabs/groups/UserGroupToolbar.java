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
package org.eclipse.kapua.app.console.module.user.client.tabs.groups;

import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.group.GroupToolbarGrid;
import org.eclipse.kapua.app.console.module.user.client.group.UserGroupDataProvider;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.model.permission.UserSessionPermission;

public class UserGroupToolbar extends GroupToolbarGrid {

    private GwtUser selectedUser;

    public UserGroupToolbar(GwtSession currentSession) {
        super(currentSession, new UserGroupDataProvider(), true);

        setEditButtonVisible(false);
    }

    public void setSelectedUser(GwtUser selectedUser) {
        this.selectedUser = selectedUser;

        updateButtonEnablement();
    }

    @Override
    protected KapuaDialog getAddDialog() {
        UserGroupAddDialog dialog = null;
        if (selectedUser != null) {
            dialog = new UserGroupAddDialog(currentSession, selectedUser);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        UserGroupDeleteDialog dialog = null;
        if (selectedEntity != null) {
            dialog = new UserGroupDeleteDialog(selectedUser, selectedEntity);
        }
        return dialog;
    }

    @Override
    protected void updateButtonEnablement() {
        if (addEntityButton != null) {
            addEntityButton.setEnabled(selectedUser != null
                    && currentSession.hasPermission(UserSessionPermission.write()));
        }
        if (deleteEntityButton != null) {
            deleteEntityButton.setEnabled(selectedUser != null && selectedEntity != null
                    && currentSession.hasPermission(UserSessionPermission.write()));
        }
        if (refreshEntityButton != null) {
            refreshEntityButton.setEnabled(selectedUser != null);
        }
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);

        setBorders(true);

        addEntityButton.setText("Assign");
        deleteEntityButton.setText("Remove");

        addEntityButton.setEnabled(
            selectedUser != null
            // selectedUser != null &&
            // currentSession.hasPermission(UserSessionPermission.write())
        );

        deleteEntityButton.setEnabled(
            selectedUser != null &&
            selectedEntity != null
            // currentSession.hasPermission(UserSessionPermission.delete())
        );
    }
}
