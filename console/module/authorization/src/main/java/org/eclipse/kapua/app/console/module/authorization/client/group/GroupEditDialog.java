/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.authorization.client.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.KapuaSafeHtmlUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupServiceAsync;

public class GroupEditDialog extends GroupAddDialog {

    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);

    private static final GwtGroupServiceAsync GWT_GROUP_SERVICE = GWT.create(GwtGroupService.class);

    private GwtGroup selectedGroup;

    public GroupEditDialog(GwtSession currentSession, GwtGroup selectedGroup) {
        super(currentSession);
        this.selectedGroup = selectedGroup;
    }

    @Override
    public void createBody() {
        super.createBody();

        domainsCombo.removeFromParent();
        domainsLabel.show();

        populateEditDialog(selectedGroup);
    }

    @Override
    public void submit() {
        selectedGroup.setGroupName(groupNameField.getValue());
        selectedGroup.setGroupDescription(KapuaSafeHtmlUtils.htmlUnescape(groupDescriptionField.getValue()));

        GWT_GROUP_SERVICE.update(selectedGroup, new AsyncCallback<GwtGroup>() {

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                status.hide();
                formPanel.getButtonBar().enable();
                unmask();
                submitButton.enable();
                cancelButton.enable();
                if (!isPermissionErrorMessage(cause)) {
                    if (cause instanceof GwtKapuaException) {
                        GwtKapuaException gwtCause = (GwtKapuaException) cause;
                        if (gwtCause.getCode().equals(GwtKapuaErrorCode.DUPLICATE_NAME)) {
                            groupNameField.markInvalid(gwtCause.getMessage());
                        }
                    }
                    FailureHandler.handleFormException(formPanel, cause);
                }
            }

            @Override
            public void onSuccess(GwtGroup arg0) {
                exitStatus = true;
                exitMessage = MSGS.dialogEditConfirmation();
                hide();
            }
        });
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogEditHeader(selectedGroup.getGroupName());
    }

    @Override
    public String getInfoMessage() {
        return MSGS.dialogEditInfo();
    }

    private void populateEditDialog(GwtGroup gwtGroup) {
        groupNameField.setValue(gwtGroup.getGroupName());
        groupDescriptionField.setValue(gwtGroup.getUnescapedDescription());
        domainsLabel.setText(gwtGroup.getDomain());

        formPanel.clearDirtyFields();
    }
}
