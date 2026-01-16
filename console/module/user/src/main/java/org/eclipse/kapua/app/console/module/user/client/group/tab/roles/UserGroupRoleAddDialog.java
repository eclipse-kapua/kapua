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

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsolePermissionMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.as.GwtGroupRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.as.GwtGroupRoleCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtRoleService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtRoleServiceAsync;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupServiceAsync;

import java.util.List;

public class UserGroupRoleAddDialog extends EntityAddEditDialog {

    private static final ConsolePermissionMessages MSGS = GWT.create(ConsolePermissionMessages.class);
    private static final ConsoleMessages CMSGS = GWT.create(ConsoleMessages.class);

    private static final GwtRoleServiceAsync GWT_ROLE_SERVICE = GWT.create(GwtRoleService.class);
    private static final GwtUserGroupServiceAsync GWT_USER_GROUP_SERVICE = GWT.create(GwtUserGroupService.class);

    private ComboBox<GwtRole> rolesCombo;
    private GwtGroup gwtGroup;

    public UserGroupRoleAddDialog(GwtSession currentSession, GwtGroup userGroup) {
        super(currentSession);

        this.gwtGroup = userGroup;

        DialogUtils.resizeDialog(this, 400, 150);
    }

    @Override
    public void submit() {
        GwtGroupRoleCreator gwtGroupRoleCreator = new GwtGroupRoleCreator();

        gwtGroupRoleCreator.setScopeId(currentSession.getSelectedAccountId());

        gwtGroupRoleCreator.setUserGroupId(gwtGroup.getId());
        gwtGroupRoleCreator.setRoleId(rolesCombo.getValue().getId());

        GWT_USER_GROUP_SERVICE.createRole(xsrfToken, gwtGroupRoleCreator, new AsyncCallback<GwtGroupRole>() {

            @Override
            public void onSuccess(GwtGroupRole arg0) {
                exitStatus = true;
                exitMessage = MSGS.dialogAddRoleConfirmation();
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                unmask();

                submitButton.enable();
                cancelButton.enable();
                status.hide();

                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    switch (((GwtKapuaException) cause).getCode()) {
                        case DUPLICATE_NAME:
                            exitMessage = MSGS.dialogAddRoleDuplicateError();
                            break;
                        default:
                            exitMessage = MSGS.dialogAddError(MSGS.dialogAddRoleError(cause.getLocalizedMessage()));
                    }
                }
                rolesCombo.markInvalid(exitMessage);
                ConsoleInfo.display(CMSGS.error(), exitMessage);
            }
        });
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogAddRoleHeader();
    }

    @Override
    public String getInfoMessage() {
        return "Select a role that should be assigned to the Group.";
    }

    @Override
    public void createBody() {
        FormPanel roleFormPanel = new FormPanel(FORM_LABEL_WIDTH);
        // Role
        rolesCombo = new ComboBox<GwtRole>();
        rolesCombo.setEditable(false);
        rolesCombo.setTypeAhead(false);
        rolesCombo.setAllowBlank(false);
        rolesCombo.setFieldLabel("* " + MSGS.dialogAddRoleComboName());
        rolesCombo.setToolTip(MSGS.dialogAddRoleComboNameTooltip());
        rolesCombo.setTriggerAction(TriggerAction.ALL);
        rolesCombo.setStore(new ListStore<GwtRole>());
        rolesCombo.setDisplayField("name");
        rolesCombo.setTemplate("<tpl for=\".\"><div role=\"listitem\" class=\"x-combo-list-item\" title={name}>{name}</div></tpl>");
        rolesCombo.setValueField("id");
        rolesCombo.addListener(Events.Select, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                UserGroupRoleAddDialog.this.formPanel.fireEvent(Events.OnClick);
            }
        });

        GWT_ROLE_SERVICE.findAll(currentSession.getSelectedAccountId(), new AsyncCallback<List<GwtRole>>() {

            @Override
            public void onFailure(Throwable caught) {
                exitStatus = false;
                FailureHandler.handle(caught);
                hide();
            }

            @Override
            public void onSuccess(List<GwtRole> result) {
                rolesCombo.getStore().add(result);
                rolesCombo.setEmptyText(result.isEmpty() ? MSGS.dialogAddRoleComboEmptyTextNoRoles() : MSGS.dialogAddRoleComboEmptyText());
            }
        });
        roleFormPanel.add(rolesCombo);
        // Add form panel to body
        bodyPanel.add(roleFormPanel);
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        submitButton.disable();
    }
}
