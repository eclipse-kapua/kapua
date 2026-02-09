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
package org.eclipse.kapua.app.console.module.authorization.client.tabs.permission;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsolePermissionMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessInfo;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessPermissionCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtDomain;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtPermission.GwtAction;
import org.eclipse.kapua.app.console.module.authorization.shared.model.permission.GroupSessionPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtAccessInfoService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtAccessInfoServiceAsync;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtAccessPermissionService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtAccessPermissionServiceAsync;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtDomainRegistryService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtDomainRegistryServiceAsync;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupServiceAsync;

import java.util.List;

public class PermissionAddDialog extends EntityAddEditDialog {

    private static final ConsolePermissionMessages MSGS = GWT.create(ConsolePermissionMessages.class);
    private static final ConsoleMessages CMSGS = GWT.create(ConsoleMessages.class);

    private static final GwtDomainRegistryServiceAsync GWT_DOMAIN_SERVICE = GWT.create(GwtDomainRegistryService.class);
    private static final GwtAccessPermissionServiceAsync GWT_ACCESS_PERMISSION_SERVICE = GWT.create(GwtAccessPermissionService.class);
    private static final GwtAccessInfoServiceAsync GWT_ACCESS_INFO_SERVICE = GWT.create(GwtAccessInfoService.class);
    private static final GwtGroupServiceAsync GWT_GROUP_SERVICE = GWT.create(GwtGroupService.class);

    private ComboBox<GwtDomain> domainsCombo;
    private SimpleComboBox<GwtAction> actionsCombo;
    private ComboBox<GwtGroup> groupsCombo;
    private CheckBoxGroup forwardableChecboxGroup;
    private CheckBox forwardableChecbox;

    private final GwtGroup allGroup;
    private final GwtDomain allDomain = new GwtDomain(null, "ALL");
    private final GwtAction allAction = GwtAction.ALL;

    private String accessInfoId;

    public PermissionAddDialog(GwtSession currentSession, String userId) {
        super(currentSession);

        allGroup = new GwtGroup();
        allGroup.setId(null);
        allGroup.setGroupName("ALL");

        GWT_ACCESS_INFO_SERVICE.findByUserIdOrCreate(currentSession.getSelectedAccountId(), userId, new AsyncCallback<GwtAccessInfo>() {

            @Override
            public void onSuccess(GwtAccessInfo result) {
                accessInfoId = result.getId();
                submitButton.enable();
            }

            @Override
            public void onFailure(Throwable caught) {
                exitStatus = false;
                if (!isPermissionErrorMessage(caught)) {
                    exitMessage = MSGS.dialogAddPermissionErrorAccessInfo(caught.getLocalizedMessage());
                }
                hide();
            }
        });

        DialogUtils.resizeDialog(this, 500, 240);
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        submitButton.disable();
    }

    @Override
    public void createBody() {
        FormPanel permissionFormPanel = new FormPanel(FORM_LABEL_WIDTH);
        // Domain
        domainsCombo = new ComboBox<GwtDomain>();
        domainsCombo.setStore(new ListStore<GwtDomain>());
        domainsCombo.setEditable(false);
        domainsCombo.setTypeAhead(false);
        domainsCombo.setAllowBlank(false);
        domainsCombo.disable();
        domainsCombo.setFieldLabel(MSGS.dialogAddPermissionDomain());
        domainsCombo.setToolTip(MSGS.permissionAddDialogDomainTooltip());
        domainsCombo.setTriggerAction(TriggerAction.ALL);
        domainsCombo.setEmptyText(MSGS.dialogAddPermissionLoading());
        domainsCombo.setDisplayField("domainName");
        GWT_DOMAIN_SERVICE.findAll(new AsyncCallback<List<GwtDomain>>() {

            @Override
            public void onFailure(Throwable caught) {
                exitStatus = false;
                if (!isPermissionErrorMessage(caught)) {
                    exitMessage = MSGS.dialogAddPermissionErrorDomains(caught.getLocalizedMessage());
                }
                hide();
            }

            @Override
            public void onSuccess(List<GwtDomain> result) {
                domainsCombo.getStore().add(allDomain);
                domainsCombo.getStore().add(result);
                domainsCombo.setValue(allDomain);
                domainsCombo.enable();
            }
        });

        domainsCombo.addSelectionChangedListener(new SelectionChangedListener<GwtDomain>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<GwtDomain> se) {
                final GwtDomain selectedDomain = se.getSelectedItem();

                GWT_DOMAIN_SERVICE.findActionsByDomainName(selectedDomain.getDomainName(), new AsyncCallback<List<GwtAction>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        exitStatus = false;
                        if (!isPermissionErrorMessage(caught)) {
                            exitMessage = MSGS.dialogAddPermissionErrorActions(caught.getLocalizedMessage());
                        }
                        hide();
                    }

                    @Override
                    public void onSuccess(List<GwtAction> result) {
                        actionsCombo.removeAll();
                        actionsCombo.add(allAction);
                        actionsCombo.add(result);
                        actionsCombo.setSimpleValue(allAction);
                        actionsCombo.enable();
                        groupsCombo.clearInvalid();

                        if (allDomain.equals(selectedDomain)) {
                            groupsCombo.setEnabled(true);
                            groupsCombo.setValue(allGroup);
                        } else if (selectedDomain.getGroupable()) {
                            groupsCombo.setEnabled(selectedDomain.getGroupable());
                            groupsCombo.setValue(allGroup);

                            if (currentSession.hasPermission(GroupSessionPermission.read())) {
                                GWT_GROUP_SERVICE.findAll(currentSession.getSelectedAccountId(), selectedDomain.getDomainName(), new AsyncCallback<List<GwtGroup>>() {

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        exitStatus = false;
                                        if (!isPermissionErrorMessage(caught)) {
                                            exitMessage = MSGS.dialogAddPermissionErrorGroups(caught.getLocalizedMessage());
                                        }
                                        hide();
                                    }

                                    @Override
                                    public void onSuccess(List<GwtGroup> result) {
                                        groupsCombo.getStore().removeAll();
                                        groupsCombo.getStore().add(allGroup);
                                        groupsCombo.getStore().add(result);
                                        groupsCombo.setValue(allGroup);
                                        groupsCombo.enable();
                                    }
                                });

                                groupsCombo.addSelectionChangedListener(new SelectionChangedListener<GwtGroup>() {

                                    @Override
                                    public void selectionChanged(SelectionChangedEvent<GwtGroup> se) {
                                        domainsCombo.clearInvalid();
                                        actionsCombo.clearInvalid();
                                        groupsCombo.clearInvalid();
                                    }
                                });

                            } else {
                                groupsCombo.getStore().removeAll();
                                groupsCombo.getStore().add(allGroup);
                                groupsCombo.setValue(allGroup);
                            }
                        } else {
                            groupsCombo.setEnabled(selectedDomain.getGroupable());
                            groupsCombo.setRawValue(MSGS.dialogAddPermissionGroupIdNotGroupable());
                        }
                    }
                });

            }
        });
        permissionFormPanel.add(domainsCombo);
        // Action
        actionsCombo = new SimpleComboBox<GwtAction>();
        actionsCombo.disable();
        actionsCombo.setTypeAhead(false);
        actionsCombo.setAllowBlank(false);
        actionsCombo.setEditable(false);
        actionsCombo.setFieldLabel(MSGS.dialogAddPermissionAction());
        actionsCombo.setToolTip(MSGS.permissionAddDialogActionTooltip());
        actionsCombo.setTriggerAction(TriggerAction.ALL);
        actionsCombo.setEmptyText(MSGS.dialogAddPermissionLoading());

        actionsCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<GwtAction>>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<GwtAction>> se) {
                domainsCombo.clearInvalid();
                actionsCombo.clearInvalid();
                groupsCombo.clearInvalid();
            }
        });

        permissionFormPanel.add(actionsCombo);
        // Target Scope Id
        LabelField labelField = new LabelField();
        labelField.setFieldLabel("Target Scope");
        labelField.setLabelSeparator(":");
        labelField.setToolTip("The scope on which the permission is given.");
        labelField.setValue(currentSession.getSelectedAccountName());
        permissionFormPanel.add(labelField);

        // Groups
        groupsCombo = new ComboBox<GwtGroup>();
        groupsCombo.setStore(new ListStore<GwtGroup>());
        groupsCombo.setEditable(false);
        groupsCombo.setTypeAhead(false);
        groupsCombo.setAllowBlank(false);
        groupsCombo.setDisplayField("groupName");
        groupsCombo.setValueField("id");
        groupsCombo.setFieldLabel(MSGS.dialogAddPermissionGroupId());
        groupsCombo.setToolTip(MSGS.permissionAddDialogGroupTooltip());
        groupsCombo.setTriggerAction(TriggerAction.ALL);
        groupsCombo.setEmptyText(MSGS.dialogAddPermissionLoading());
        groupsCombo.disable();
        permissionFormPanel.add(groupsCombo);

        // Forwardable
        forwardableChecbox = new CheckBox();
        forwardableChecbox.setBoxLabel("");
        forwardableChecbox.setToolTip(MSGS.permissionAddDialogForwardableTooltip());

        forwardableChecboxGroup = new CheckBoxGroup();
        forwardableChecboxGroup.setFieldLabel(MSGS.dialogAddPermissionForwardable());
        forwardableChecboxGroup.add(forwardableChecbox);
        permissionFormPanel.add(forwardableChecboxGroup);
        // Add form panel to body
        bodyPanel.add(permissionFormPanel);
    }

    @Override
    public void submit() {

        GwtPermission permission = new GwtPermission();
        permission.setDomain(domainsCombo.getValue().getDomainId());
        permission.setAction(actionsCombo.getValue().getValue().toString());
        permission.setTargetScopeId(currentSession.getSelectedAccountId());
        permission.setGroupId(groupsCombo.getValue() != null ? groupsCombo.getValue().getId() : null);
        permission.setForwardable(forwardableChecboxGroup.getValue() != null);

        GwtAccessPermissionCreator gwtAccessPermissionCreator = new GwtAccessPermissionCreator();
        gwtAccessPermissionCreator.setScopeId(currentSession.getSelectedAccountId());
        gwtAccessPermissionCreator.setAccessInfoId(accessInfoId);
        gwtAccessPermissionCreator.setPermission(permission);

        GWT_ACCESS_PERMISSION_SERVICE.create(xsrfToken, gwtAccessPermissionCreator, new AsyncCallback<GwtAccessPermission>() {

            @Override
            public void onSuccess(GwtAccessPermission gwtAccessPermission) {
                exitStatus = true;
                exitMessage = MSGS.dialogAddPermissionConfirmation();
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
                    if ((cause instanceof GwtKapuaException) &&
                            (GwtKapuaErrorCode.ENTITY_UNIQUENESS.equals(((GwtKapuaException) cause).getCode()))) {
                        exitMessage = MSGS.dialogAddPermissionAlreadyExists();
                    } else {
                        exitMessage = MSGS.dialogAddError(MSGS.dialogAddPermissionError(cause.getLocalizedMessage()));
                    }
                }
                domainsCombo.markInvalid(exitMessage);
                actionsCombo.markInvalid(exitMessage);
                if (groupsCombo.isEnabled()) {
                    groupsCombo.markInvalid(exitMessage);
                }
                ConsoleInfo.display(CMSGS.error(), exitMessage);
            }
        });
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogAddPermissionHeader();
    }

    @Override
    public String getInfoMessage() {
        return MSGS.dialogAddPermissionInfo();
    }
}
