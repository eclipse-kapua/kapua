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
package org.eclipse.kapua.app.console.module.authorization.client.tabs.groups;

import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.permission.GroupSessionPermission;
import org.eclipse.kapua.app.console.module.tag.client.TagToolbarGrid;

public class GroupTagToolbar extends TagToolbarGrid {

    private GwtGroup selectedGroup;
    public GroupTagToolbar(GwtSession currentSession) {
        super(currentSession, true);

        setEditButtonVisible(false);
    }

    public void setSelectedGroup(GwtGroup selectedGroup) {
        this.selectedGroup = selectedGroup;
        updateButtonEnablement();
    }

    @Override
    protected KapuaDialog getAddDialog() {
        GroupTagAddDialog dialog = null;
        if (selectedGroup != null) {
            dialog = new GroupTagAddDialog(currentSession, selectedGroup);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        GroupTagDeleteDialog dialog = null;
        if (selectedEntity != null) {
            dialog = new GroupTagDeleteDialog(selectedGroup, selectedEntity);
        }
        return dialog;
    }

    @Override
    protected void updateButtonEnablement() {
        if (addEntityButton != null) {
            addEntityButton.setEnabled(
                    selectedGroup != null &&
                    currentSession.hasPermission(GroupSessionPermission.write())
            );
        }

        if (deleteEntityButton != null) {
            deleteEntityButton.setEnabled(
                    selectedGroup != null &&
                    selectedEntity != null &&
                    currentSession.hasPermission(GroupSessionPermission.write())
            );
        }

        if (addEntityButton != null) {
            refreshEntityButton.setEnabled(selectedGroup != null);
        }
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);

        setBorders(true);

        addEntityButton.setText("Apply");
        deleteEntityButton.setText("Remove");

        addEntityButton.setEnabled(
                selectedGroup != null &&
                currentSession.hasPermission(GroupSessionPermission.write()));
    }
}
