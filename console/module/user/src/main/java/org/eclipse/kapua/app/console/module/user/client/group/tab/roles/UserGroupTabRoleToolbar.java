/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsolePermissionMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.as.GwtGroupRole;

public class UserGroupTabRoleToolbar extends EntityCRUDToolbar<GwtGroupRole> {

    private GwtGroup gwtUserGroup;
    private static final ConsolePermissionMessages PERMISSION_MSGS = GWT.create(ConsolePermissionMessages.class);

    public UserGroupTabRoleToolbar(GwtSession currentSession) {
        super(currentSession, true);
    }

    public void setUserGroup(GwtGroup gwtUserGroup) {
        this.gwtUserGroup = gwtUserGroup;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        GwtGroupRole selectedAccessRole = gridSelectionModel.getSelectedItem();
        UserGroupRoleDeleteDialog dialog = null;
        if (selectedAccessRole != null) {
            dialog = new UserGroupRoleDeleteDialog(selectedAccessRole);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getAddDialog() {
        UserGroupRoleAddDialog dialog = null;
        if (gwtUserGroup != null) {
            dialog = new UserGroupRoleAddDialog(currentSession, gwtUserGroup);
        }
        return dialog;
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);
        addEntityButton.setText(PERMISSION_MSGS.dialogAddRoleButton());
        deleteEntityButton.setText(PERMISSION_MSGS.dialogDeleteRoleButton());
        addEntityButton.setEnabled(gwtUserGroup != null);
        deleteEntityButton.setEnabled(gridSelectionModel != null && gridSelectionModel.getSelectedItem() != null);
        refreshEntityButton.setEnabled(gridSelectionModel != null && gridSelectionModel.getSelectedItem() != null);
    }

    @Override
    protected void updateButtonEnablement() {
        super.updateButtonEnablement();
        addEntityButton.setEnabled(gwtUserGroup != null);
    }
}
