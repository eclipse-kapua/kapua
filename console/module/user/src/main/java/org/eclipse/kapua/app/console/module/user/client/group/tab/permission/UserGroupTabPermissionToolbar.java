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
package org.eclipse.kapua.app.console.module.user.client.group.tab.permission;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsolePermissionMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.permission.DomainSessionPermission;
import org.eclipse.kapua.app.console.module.user.shared.model.permission.UserGroupSessionPermission;

public class UserGroupTabPermissionToolbar extends EntityCRUDToolbar<GwtGroupPermission> {

    private GwtGroup gwtUserGroup;
    private static final ConsolePermissionMessages PERMISSION_MSGS = GWT.create(ConsolePermissionMessages.class);

    public UserGroupTabPermissionToolbar(GwtSession currentSession) {
        super(currentSession, true);
    }

    public void setGwtUserGroup(GwtGroup gwtUserGroup) {
        this.gwtUserGroup = gwtUserGroup;
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);
        addEntityButton.setText(PERMISSION_MSGS.dialogAddPermissionButton());

        deleteEntityButton.setText(PERMISSION_MSGS.dialogDeletePermissionButton());
    }

    @Override
    protected KapuaDialog getAddDialog() {
        UserGroupPermissionAddDialog dialog = null;
        if (gwtUserGroup != null) {
            dialog = new UserGroupPermissionAddDialog(currentSession, gwtUserGroup);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        GwtGroupPermission selectedAccessPermission = gridSelectionModel.getSelectedItem();

        UserGroupPermissionDeleteDialog dialog = null;
        if (selectedAccessPermission != null) {
            dialog = new UserGroupPermissionDeleteDialog(selectedAccessPermission);
        }
        return dialog;
    }

    @Override
    protected void updateButtonEnablement() {
        super.updateButtonEnablement();

        addEntityButton.setEnabled(
                currentSession.hasPermission(UserGroupSessionPermission.write()) &&
                currentSession.hasPermission(DomainSessionPermission.read())
        );
    }
}
