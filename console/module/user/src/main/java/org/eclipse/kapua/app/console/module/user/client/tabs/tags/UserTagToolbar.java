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
package org.eclipse.kapua.app.console.module.user.client.tabs.tags;

import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.model.permission.UserSessionPermission;
import org.eclipse.kapua.app.console.module.tag.client.TagToolbarGrid;

public class UserTagToolbar extends TagToolbarGrid {

    private GwtUser selectedUser;
    public UserTagToolbar(GwtSession currentSession) {
        super(currentSession, true);

        setEditButtonVisible(false);
    }

    public void setSelectedUser(GwtUser selectedUser) {
        this.selectedUser = selectedUser;
        updateButtonEnablement();
    }

    @Override
    protected KapuaDialog getAddDialog() {
        UserTagAddDialog dialog = null;
        if (selectedUser != null) {
            dialog = new UserTagAddDialog(currentSession, selectedUser);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        UserTagDeleteDialog dialog = null;
        if (selectedEntity != null) {
            dialog = new UserTagDeleteDialog(selectedUser, selectedEntity);
        }
        return dialog;
    }

    @Override
    protected void updateButtonEnablement() {
        if (addEntityButton != null) {
            addEntityButton.setEnabled(
                    selectedUser != null &&
                    currentSession.hasPermission(UserSessionPermission.write())
            );
        }

        if (deleteEntityButton != null) {
            deleteEntityButton.setEnabled(
                    selectedUser != null &&
                    selectedEntity != null &&
                    currentSession.hasPermission(UserSessionPermission.write())
            );
        }

        if (addEntityButton != null) {
            refreshEntityButton.setEnabled(selectedUser != null);
        }
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);

        setBorders(true);

        addEntityButton.setText("Apply");
        deleteEntityButton.setText("Remove");

        addEntityButton.setEnabled(
                selectedUser != null &&
                currentSession.hasPermission(UserSessionPermission.write()));
    }
}
