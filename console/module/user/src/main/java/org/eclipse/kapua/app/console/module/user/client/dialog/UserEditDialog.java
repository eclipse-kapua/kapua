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
package org.eclipse.kapua.app.console.module.user.client.dialog;

import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.InfoDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.InfoDialog.InfoDialogType;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.KapuaSafeHtmlUtils;
import org.eclipse.kapua.app.console.module.api.client.util.SplitTooltipStringUtil;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser.GwtUserStatus;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserServiceAsync;

public class UserEditDialog extends UserAddDialog {

    private static final GwtUserServiceAsync GWT_USER_SERVICE = GWT.create(GwtUserService.class);

    public static final int MAX_LINE_LENGTH = 30;
    public static final int MAX_TOOLTIP_WIDTH = 300;

    private GwtUser selectedUser;
    private boolean isChanged;

    public UserEditDialog(GwtSession currentSession, GwtUser selectedUser) {
        super(currentSession);

        this.selectedUser = selectedUser;

        DialogUtils.resizeDialog(this, 400, 390);
    }

    @Override
    public void createBody() {
        super.createBody();

        loadUser();
    }

    private void loadUser() {
        maskDialog();

        GWT_USER_SERVICE.find(selectedUser.getScopeId(), selectedUser.getId(), new AsyncCallback<GwtUser>() {
            @Override
            public void onSuccess(GwtUser gwtUser) {
                unmaskDialog();
                populateEditDialog(gwtUser);
                selectedUser = gwtUser;
            }

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    exitMessage = USER_MSGS.dialogEditLoadFailed(cause.getLocalizedMessage());
                }
                unmaskDialog();
                hide();
            }
        });
    }

    @Override
    protected void preSubmit() {
        super.preSubmit();

        if (displayName.isDirty()) {
            isChanged = true;
        }
    }

    @Override
    public void submit() {
        selectedUser.setUsername(username.getValue());
        selectedUser.setDisplayName(KapuaSafeHtmlUtils.htmlUnescape(displayName.getValue()));
        selectedUser.setEmail(email.getValue());
        selectedUser.setPhoneNumber(phoneNumber.getValue());
        selectedUser.setStatus(userStatus.getValue().getValue().toString());
        selectedUser.setExpirationDate(expirationDate.getValue());
        selectedUser.setExternalId(externalId.getValue());
        selectedUser.setExternalUsername(externalUsername.getValue());

        GWT_USER_SERVICE.update(xsrfToken, selectedUser, new AsyncCallback<GwtUser>() {

            @Override
            public void onSuccess(GwtUser gwtUser) {
                if (currentSession.getUserName().equals(gwtUser.getUsername()) && isChanged) {
                    InfoDialog infoDialog = new InfoDialog(InfoDialogType.INFO, USER_MSGS.dialogEditUserName());
                    infoDialog.show();
                }
                exitStatus = true;
                exitMessage = USER_MSGS.dialogEditConfirmation();
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                status.hide();
                formPanel.getButtonBar().enable();
                unmask();
                submitButton.enable();
                cancelButton.enable();
                if (!isPermissionErrorMessage(cause)) {
                    exitMessage = USER_MSGS.dialogEditError(cause.getLocalizedMessage());
                    FailureHandler.handleFormException(formPanel, cause);
                    if (cause instanceof GwtKapuaException) {
                        GwtKapuaException gwtCause = (GwtKapuaException) cause;
                        if (gwtCause.getCode().equals(GwtKapuaErrorCode.DUPLICATE_NAME)) {
                            username.markInvalid(gwtCause.getMessage());
                        } else if (gwtCause.getCode().equals(GwtKapuaErrorCode.ILLEGAL_ARGUMENT)) {
                            if (gwtCause.getArguments().length == 2 && gwtCause.getArguments()[0].equals("status") && gwtCause.getArguments()[1].equals("DISABLED")) {
                                userStatus.markInvalid(gwtCause.getMessage());
                            }
                        } else if (gwtCause.getCode().equals(GwtKapuaErrorCode.OPERATION_NOT_ALLOWED_ON_ADMIN_USER)) {
                            if (userStatus.getValue().getValue().equals(GwtUserStatus.DISABLED)) {
                                userStatus.markInvalid(USER_MSGS.dialogEditAdminUserStatusError());
                            }
                            if (expirationDate.getValue() != null) {
                                expirationDate.markInvalid(USER_MSGS.dialogEditAdminExpirationDateError());
                            }
                        } else if (gwtCause.getCode().equals(GwtKapuaErrorCode.EXTERNAL_ID_ALREADY_EXIST)) {
                            externalId.markInvalid(gwtCause.getMessage());
                        } else if (gwtCause.getCode().equals(GwtKapuaErrorCode.EXTERNAL_USERNAME_ALREADY_EXIST)) {
                            externalUsername.markInvalid(gwtCause.getMessage());
                        }
                    }
                }
            }
        });

    }

    @Override
    public String getHeaderMessage() {
        return USER_MSGS.dialogEditHeader(selectedUser.getUsername());
    }

    @Override
    public String getInfoMessage() {
        return USER_MSGS.dialogEditInfo();
    }

    private void populateEditDialog(GwtUser gwtUser) {

        // GroupId
        groupCombo.hide();

        // Username
        usernameLabel.show();
        username.hide();

        // User type
        userTypeRadioGroup.hide();

        // User type EXTERNAL
        if (currentSession.isSsoEnabled() && gwtUser.getUserTypeEnum() == GwtUser.GwtUserType.EXTERNAL) {
            externalId.enable();
            externalId.show();
            externalUsername.enable();
            externalUsername.show();
        }

        password.hide();
        password.disable();
        confirmPassword.hide();
        confirmPassword.disable();
        passwordTooltip.hide();
        passwordTooltip.disable();
        // Populate Fields
        usernameLabel.setValue(gwtUser.getUsername());
        usernameLabel.setToolTip(createTooltipForLongValues(gwtUser.getUsername()));

        username.setValue(gwtUser.getUsername());

        displayName.setValue(gwtUser.getUnescapedDisplayName());
        displayName.setToolTip(createTooltipForLongValues(gwtUser.getDisplayName()));

        if (currentSession.isSsoEnabled() && gwtUser.getUserTypeEnum() == GwtUser.GwtUserType.EXTERNAL) {
            externalId.setValue(gwtUser.getExternalId());
            externalIdLabel.setValue(gwtUser.getExternalId());
            externalIdLabel.setToolTip(createTooltipForLongValues(gwtUser.getExternalId()));

            externalUsername.setValue(gwtUser.getExternalUsername());
            externalUsernameLabel.setValue(gwtUser.getExternalUsername());
            externalUsernameLabel.setToolTip(createTooltipForLongValues(gwtUser.getExternalUsername()));
        }

        email.setValue(gwtUser.getEmail());
        phoneNumber.setValue(gwtUser.getPhoneNumber());
        userStatus.setSimpleValue(gwtUser.getStatusEnum());
        expirationDate.setValue(gwtUser.getExpirationDate());
    }

    public ToolTipConfig createTooltipForLongValues(String toolTipText) {
        if (toolTipText == null) {
            return null;
        }

        String splitToolTipText = SplitTooltipStringUtil.splitTooltipString(toolTipText, MAX_LINE_LENGTH);

        ToolTipConfig externalIdToolTipConfig = new ToolTipConfig();
        externalIdToolTipConfig.setMaxWidth(MAX_TOOLTIP_WIDTH);
        externalIdToolTipConfig.setText(splitToolTipText);

        return externalIdToolTipConfig;
    }
}
