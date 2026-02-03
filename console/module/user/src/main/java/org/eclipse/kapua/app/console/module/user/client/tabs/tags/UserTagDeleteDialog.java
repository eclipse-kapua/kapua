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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserServiceAsync;
import org.eclipse.kapua.app.console.module.tag.shared.model.GwtTag;

public class UserTagDeleteDialog extends EntityDeleteDialog {

    private static final GwtUserServiceAsync GWT_USER_SERVICE = GWT.create(GwtUserService.class);

    private GwtUser selectedUser;
    private GwtTag selectedTag;

    public UserTagDeleteDialog(GwtUser selectedUser, GwtTag selectedTag) {
        this.selectedUser = selectedUser;
        this.selectedTag = selectedTag;

        DialogUtils.resizeDialog(this, 300, 135);
    }

    @Override
    public String getHeaderMessage() {
        return "Remove Tag: " + selectedTag.getTagName();
    }

    @Override
    public String getInfoMessage() {
        return "Are you sure you want to remove the selected tag?";
    }

    @Override
    public void submit() {
        GWT_USER_SERVICE.deleteUserTag(xsrfToken, selectedUser.getScopeId(), selectedUser.getId(), selectedTag.getId(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void arg0) {
                exitStatus = true;
                exitMessage = "Tag successfully removed";
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    exitMessage = "Error while removing tag: " + cause.getLocalizedMessage();
                }
                hide();
            }
        });

    }
}
