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
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsolePermissionMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupRole;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupServiceAsync;

public class UserGroupRoleDeleteDialog extends EntityDeleteDialog {

    private static final ConsolePermissionMessages MSGS = GWT.create(ConsolePermissionMessages.class);

    private static final GwtUserGroupServiceAsync GWT_USER_GROUP_SERVICE = GWT.create(GwtUserGroupService.class);

    private GwtGroupRole gwtGroupRole;

    public UserGroupRoleDeleteDialog(GwtGroupRole gwtAccessRole) {
        this.gwtGroupRole = gwtAccessRole;

        DialogUtils.resizeDialog(this, 300, 135);
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogDeleteRoleHeader(gwtGroupRole.getRoleName());
    }

    @Override
    public String getInfoMessage() {
        return "Remove the association between the Group and the Role?";
    }

    @Override
    public void submit() {
        GWT_USER_GROUP_SERVICE.deleteRole(xsrfToken, gwtGroupRole.getScopeId(), gwtGroupRole.getId(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void arg0) {
                exitStatus = true;
                exitMessage = MSGS.dialogDeleteRoleConfirmation();
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    exitMessage = MSGS.dialogDeleteRoleError(cause.getLocalizedMessage());
                }
                hide();
            }
        });

    }

}
