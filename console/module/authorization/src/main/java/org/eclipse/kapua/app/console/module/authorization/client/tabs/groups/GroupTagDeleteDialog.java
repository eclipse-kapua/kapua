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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupServiceAsync;
import org.eclipse.kapua.app.console.module.tag.shared.model.GwtTag;

public class GroupTagDeleteDialog extends EntityDeleteDialog {

    private static final GwtGroupServiceAsync GWT_USER_SERVICE = GWT.create(GwtGroupService.class);

    private GwtGroup selectedGroup;
    private GwtTag selectedTag;

    public GroupTagDeleteDialog(GwtGroup selectedGroup, GwtTag selectedTag) {
        this.selectedGroup = selectedGroup;
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
        GWT_USER_SERVICE.deleteGroupTag(xsrfToken, selectedGroup.getScopeId(), selectedGroup.getId(), selectedTag.getId(), new AsyncCallback<Void>() {

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
