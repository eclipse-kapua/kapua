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
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsolePermissionMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermission;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupServiceAsync;

public class UserGroupPermissionDeleteDialog extends EntityDeleteDialog {

    private static final ConsolePermissionMessages MSGS = GWT.create(ConsolePermissionMessages.class);

    private static final GwtUserGroupServiceAsync GWT_USER_GROUP_SERVICE = GWT.create(GwtUserGroupService.class);

    private GwtGroupPermission gwtGroupPermission;

    public UserGroupPermissionDeleteDialog(GwtGroupPermission gwtGroupPermission) {
        this.gwtGroupPermission = gwtGroupPermission;

        DialogUtils.resizeDialog(this, 300, 135);
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogDeletePermissionHeader();
    }

    @Override
    public String getInfoMessage() {
        return "Are you sure you want to revoke the selected permission from the Group?";
    }

    @Override
    public void submit() {
        GWT_USER_GROUP_SERVICE.deletePermission(xsrfToken, gwtGroupPermission.getScopeId(), gwtGroupPermission.getId(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void arg0) {
                exitStatus = true;
                exitMessage = MSGS.dialogDeletePermissionConfirmation();
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    exitMessage = MSGS.dialogDeletePermissionError(cause.getLocalizedMessage());
                }
                hide();
            }
        });

    }

}
