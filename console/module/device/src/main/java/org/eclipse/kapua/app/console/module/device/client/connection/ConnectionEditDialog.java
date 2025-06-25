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
package org.eclipse.kapua.app.console.module.device.client.connection;

import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.util.Constants;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleConnectionMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection.GwtConnectionUserCouplingMode;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionOption;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceConnectionOptionService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceConnectionOptionServiceAsync;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUserQuery;
import org.eclipse.kapua.app.console.module.user.shared.model.permission.UserSessionPermission;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserServiceAsync;

import java.util.Set;

public class ConnectionEditDialog extends EntityAddEditDialog {

    private static final GwtUserServiceAsync GWT_USER_SERVICE = GWT.create(GwtUserService.class);
    private static final GwtDeviceConnectionOptionServiceAsync GWT_CONNECTION_OPTION_SERVICE = GWT.create(GwtDeviceConnectionOptionService.class);
    private static final ConsoleConnectionMessages MSGS = GWT.create(ConsoleConnectionMessages.class);

    private GwtDeviceConnection selectedDeviceConnection;
    // Security Options fields
    private SimpleComboBox<String> couplingModeCombo;

    private ComboBox<GwtUser> reservedUserCombo;
    private CheckBox allowUserChangeCheckbox;
    private LabelField lastUserField;
    private SimpleComboBox<String> authenticationTypeCombo;

    private LabelField lastAuthenticationTypeLabel;

    private static final GwtUser NO_USER;

    static {
        NO_USER = new GwtUser();
        NO_USER.setUsername(MSGS.connectionFormReservedUserNoUser());
        NO_USER.setId(null);
    }

    public ConnectionEditDialog(GwtSession currentSession, GwtDeviceConnection selectedDeviceConnection) {
        super(currentSession);
        DialogUtils.resizeDialog(this, 440, 260);
        this.selectedDeviceConnection = selectedDeviceConnection;
    }

    @Override
    public void createBody() {
        submitButton.disable();
        FormPanel groupFormPanel = new FormPanel(FORM_LABEL_WIDTH + 10);
        FormLayout layoutSecurityOptions = new FormLayout();
        layoutSecurityOptions.setLabelWidth(Constants.LABEL_WIDTH_DEVICE_FORM);

        // Last User
        lastUserField = new LabelField();
        lastUserField.setName("connectionUserLastUserField");
        lastUserField.setLabelSeparator(":");
        lastUserField.setFieldLabel(MSGS.connectionFormLastUser());
        lastUserField.setToolTip(MSGS.connectionFormLastUserTooltip());
        lastUserField.setWidth(225);
        lastUserField.setReadOnly(true);
        groupFormPanel.add(lastUserField);

        // Connection user coupling mode
        couplingModeCombo = new SimpleComboBox<String>();
        couplingModeCombo.setName("connectionUserCouplingModeCombo");
        couplingModeCombo.setEditable(false);
        couplingModeCombo.setTypeAhead(false);
        couplingModeCombo.setAllowBlank(false);
        couplingModeCombo.setFieldLabel(MSGS.connectionFormUserCouplingMode());
        couplingModeCombo.setToolTip(MSGS.connectionFormUserCouplingModeTooltip());
        couplingModeCombo.setTriggerAction(TriggerAction.ALL);

        couplingModeCombo.add(GwtConnectionUserCouplingMode.INHERITED.getLabel());
        couplingModeCombo.add(GwtConnectionUserCouplingMode.LOOSE.getLabel());
        couplingModeCombo.add(GwtConnectionUserCouplingMode.STRICT.getLabel());

        couplingModeCombo.setSimpleValue(GwtConnectionUserCouplingMode.INHERITED.getLabel());
        groupFormPanel.add(couplingModeCombo);

        //
        // Reserved User
        if (currentSession.hasPermission(UserSessionPermission.read())) {

            reservedUserCombo = new ComboBox<GwtUser>();
            reservedUserCombo.setName("connectionUserReservedUserCombo");
            reservedUserCombo.setEditable(true);
            reservedUserCombo.setTypeAhead(false);
            reservedUserCombo.setAllowBlank(true);
            reservedUserCombo.setEmptyText("No user");
            reservedUserCombo.setFieldLabel(MSGS.connectionFormReservedUser());
            reservedUserCombo.setToolTip(MSGS.connectionFormReservedUserTooltip());
            reservedUserCombo.setTriggerAction(TriggerAction.QUERY);
            reservedUserCombo.setDisplayField("username");
            reservedUserCombo.setTemplate("<tpl for=\".\"><div role=\"listitem\" class=\"x-combo-list-item\" title={username}>{username}</div></tpl>");
            reservedUserCombo.setValueField("id");
            groupFormPanel.add(reservedUserCombo);

            reservedUserCombo.setPageSize(100);
        }

        RpcProxy<PagingLoadResult<GwtUser>> userDataProxy = new RpcProxy<PagingLoadResult<GwtUser>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GwtUser>> callback) {

                GwtUserQuery query = new GwtUserQuery();
                query.setScopeId(selectedDeviceConnection.getScopeId());
                query.setName(reservedUserCombo.getRawValue()); // This filters results with the user keyword used

                GWT_USER_SERVICE.query((PagingLoadConfig) loadConfig,
                        query,
                        callback);
            }
        };

        BasePagingLoader<PagingLoadResult<GwtUser>> userPagingLoader = new BasePagingLoader<PagingLoadResult<GwtUser>>(userDataProxy);
        ListStore<GwtUser> userListStore = new ListStore<GwtUser>(userPagingLoader);

        reservedUserCombo.setStore(userListStore);

        //
        // Allow credential change
        allowUserChangeCheckbox = new CheckBox();
        allowUserChangeCheckbox.setName("connectionUserAllowUserChangeCheckbox");
        allowUserChangeCheckbox.setFieldLabel(MSGS.connectionFormAllowUserChange());
        allowUserChangeCheckbox.setToolTip(MSGS.connectionFormAllowUserChangeTooltip());
        allowUserChangeCheckbox.setBoxLabel("");
        groupFormPanel.add(allowUserChangeCheckbox);

        // Authentication type
        authenticationTypeCombo = new SimpleComboBox<String>();
        authenticationTypeCombo.setName("authenticationTypeCombo");
        authenticationTypeCombo.setEditable(false);
        authenticationTypeCombo.setTypeAhead(false);
        authenticationTypeCombo.setAllowBlank(false);
        authenticationTypeCombo.setFieldLabel("Authentication Type");
        authenticationTypeCombo.setToolTip("The authentication type of the Device Connection");
        authenticationTypeCombo.setTriggerAction(TriggerAction.ALL);

        GWT_CONNECTION_OPTION_SERVICE.getAvailableAuthenticationTypes(new AsyncCallback<Set<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                FailureHandler.handle(caught);
                authenticationTypeCombo.setEmptyText("Error while loading available Authentication Types");
            }

            @Override
            public void onSuccess(Set<String> availableAuthenticationTypes) {
                for (String authenticationType : availableAuthenticationTypes) {
                    authenticationTypeCombo.add(authenticationType);
                }

                setAuthenticationType();
            }
        });

        groupFormPanel.add(authenticationTypeCombo);

        // Last Authentication type
        lastAuthenticationTypeLabel = new LabelField();
        lastAuthenticationTypeLabel.setName("lastAuthenticationType");
        lastAuthenticationTypeLabel.setLabelSeparator(":");
        lastAuthenticationTypeLabel.setFieldLabel("Last Authentication Type");
        lastAuthenticationTypeLabel.setToolTip("Last used authentication type by this Device Connection");
        lastAuthenticationTypeLabel.setWidth(225);
        lastAuthenticationTypeLabel.setReadOnly(true);

        groupFormPanel.add(lastAuthenticationTypeLabel);

        // Add Panel to main panel
        bodyPanel.add(groupFormPanel);

        populateEditDialog(selectedDeviceConnection);
    }

    @Override
    public void submit() {
        // convertDeviceAssetChannel the connection to connection option
        GwtDeviceConnectionOption selectedDeviceConnectionOption = new GwtDeviceConnectionOption(selectedDeviceConnection);
        selectedDeviceConnectionOption.setAllowUserChange(allowUserChangeCheckbox.getValue());
        selectedDeviceConnectionOption.setConnectionUserCouplingMode(couplingModeCombo.getValue() != null ? couplingModeCombo.getValue().getValue() : null);
        selectedDeviceConnectionOption.setAuthenticationType(authenticationTypeCombo.getSimpleValue());

        if (currentSession.hasPermission(UserSessionPermission.read())) {
            selectedDeviceConnectionOption.setReservedUserId(reservedUserCombo.getValue() != null ? reservedUserCombo.getValue().getId() : null);
        }

        GWT_CONNECTION_OPTION_SERVICE.update(xsrfToken, selectedDeviceConnectionOption, new AsyncCallback<GwtDeviceConnectionOption>() {

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                status.hide();
                unmask();
                submitButton.enable();
                cancelButton.enable();
                if (!isPermissionErrorMessage(cause)) {
                    if (cause instanceof GwtKapuaException) {
                        GwtKapuaException gwtCause = (GwtKapuaException) cause;
                        if (gwtCause.getCode().equals(GwtKapuaErrorCode.INTERNAL_ERROR)) {
                            reservedUserCombo.markInvalid(cause.getMessage());
                        }
                        FailureHandler.handle(cause);
                    }
                }
            }

            @Override
            public void onSuccess(GwtDeviceConnectionOption gwtDeviceConnectionOption) {
                exitStatus = true;
                exitMessage = MSGS.dialogEditConfirmation();
                hide();
            }
        });
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogEditHeader(selectedDeviceConnection.getClientId());
    }

    @Override
    public String getInfoMessage() {
        return MSGS.dialogEditInfo();
    }

    private void populateEditDialog(GwtDeviceConnection gwtDeviceConnection) {
        if (currentSession.hasPermission(UserSessionPermission.read()) && gwtDeviceConnection.getUserId() != null) {
            GWT_USER_SERVICE.find(currentSession.getSelectedAccountId(), gwtDeviceConnection.getUserId(), new AsyncCallback<GwtUser>() {

                @Override
                public void onFailure(Throwable caught) {
                    exitStatus = false;
                    if (!isPermissionErrorMessage(caught)) {
                        FailureHandler.handle(caught);
                        hide();
                    }
                }

                @Override
                public void onSuccess(GwtUser gwtUser) {
                    if (gwtUser != null) {
                        lastUserField.setValue(gwtUser.getUsername());
                    } else {
                        lastUserField.setValue("N/A");
                    }
                }
            });
        } else {
            lastUserField.setValue("N/A");
        }

        GwtConnectionUserCouplingMode gwtConnectionUserCouplingMode = null;
        if (gwtDeviceConnection.getConnectionUserCouplingMode() != null) {
            gwtConnectionUserCouplingMode = GwtConnectionUserCouplingMode.getEnumFromLabel(gwtDeviceConnection.getConnectionUserCouplingMode());
        }
        couplingModeCombo.setSimpleValue(gwtConnectionUserCouplingMode != null ? gwtConnectionUserCouplingMode.getLabel() : "N/A");
        setReservedUser();
        allowUserChangeCheckbox.setValue(gwtDeviceConnection.getAllowUserChange());
        authenticationTypeCombo.setSimpleValue(gwtDeviceConnection.getAuthenticationType());
        lastAuthenticationTypeLabel.setValue(gwtDeviceConnection.getLastAuthenticationType() != null ? gwtDeviceConnection.getLastAuthenticationType() : "N/A");

        formPanel.clearDirtyFields();
    }

    private void setAuthenticationType() {
        if (selectedDeviceConnection != null) {
            authenticationTypeCombo.setSimpleValue(selectedDeviceConnection.getAuthenticationType());
        }
    }

    private void setReservedUser() {
        String reservedUserId = selectedDeviceConnection.getReservedUserId();

        if (reservedUserId != null) {
            for (GwtUser listStoreUser : reservedUserCombo.getStore().getModels()) {
                if (listStoreUser.getId().equals(reservedUserId)) {
                    reservedUserCombo.setValue(listStoreUser);

                    return;
                }
            }

            // If not found in the current page of the list store...
            GWT_USER_SERVICE.find(currentSession.getSelectedAccountId(), selectedDeviceConnection.getReservedUserId(), new AsyncCallback<GwtUser>() {

                @Override
                public void onFailure(Throwable caught) {
                    exitStatus = false;
                    if (!isPermissionErrorMessage(caught)) {
                        FailureHandler.handle(caught);
                        hide();
                    }
                }

                @Override
                public void onSuccess(GwtUser reservedUser) {
                    if (reservedUser != null) {
                        reservedUserCombo.setValue(reservedUser);
                    } else {
                        // ??
                    }
                }
            });
        }
    }

}
