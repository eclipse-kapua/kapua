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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserServiceAsync;

public class UserGroupDeleteDialog extends EntityDeleteDialog {

    private static final GwtUserServiceAsync GWT_USER_SERVICE = GWT.create(GwtUserService.class);

    private final GwtUser selectedUser;
    private final GwtGroup selectedGroup;

    public UserGroupDeleteDialog(GwtUser selectedUser, GwtGroup selectedGroup) {
        this.selectedUser = selectedUser;
        this.selectedGroup = selectedGroup;

        DialogUtils.resizeDialog(this, 300, 135);
    }

    @Override
    public String getHeaderMessage() {
        return "Remove Group: " + selectedGroup.getGroupName();
    }

    @Override
    public String getInfoMessage() {
        return "Are you sure you want to remove the selected Group?";
    }

    @Override
    public void submit() {
        GWT_USER_SERVICE.deleteUserGroup(xsrfToken, selectedUser.getScopeId(), selectedUser.getId(), selectedGroup.getId(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void arg0) {
                exitStatus = true;
                exitMessage = "Group successfully removed";
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    exitMessage = "Error while removing group: " + cause.getLocalizedMessage();
                }
                hide();
            }
        });

    }
}
