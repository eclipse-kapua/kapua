/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaTextField;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator.FieldType;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtDomain;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtDomainRegistryService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtDomainRegistryServiceAsync;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupServiceAsync;

import java.util.List;

public class GroupAddDialog extends EntityAddEditDialog {

    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);
    private static final ConsoleMessages CONSOLE_MSGS = GWT.create(ConsoleMessages.class);

    private static final GwtDomainRegistryServiceAsync DOMAIN_SERVICE = GWT.create(GwtDomainRegistryService.class);

    private static final GwtGroupServiceAsync GWT_GROUP_SERVICE = GWT.create(GwtGroupService.class);

    protected KapuaTextField<String> groupNameField;
    protected KapuaTextField<String> groupDescriptionField;

    protected ComboBox<GwtDomain> domainsCombo;

    protected LabelField domainsLabel;

    public GroupAddDialog(GwtSession currentSession) {
        super(currentSession);
        DialogUtils.resizeDialog(this, 400, 200);
    }

    @Override
    public void createBody() {
        submitButton.disable();

        FormPanel groupFormPanel = new FormPanel(FORM_LABEL_WIDTH);

        // Name
        groupNameField = new KapuaTextField<String>();
        groupNameField.setAllowBlank(false);
        groupNameField.setMinLength(3);
        groupNameField.setMaxLength(255);
        groupNameField.setFieldLabel("* " + MSGS.dialogAddFieldName());
        groupNameField.setValidator(new TextFieldValidator(groupNameField, FieldType.EXTENDED_NAME));
        groupNameField.setToolTip(MSGS.dialogAddFieldNameTooltip());
        groupFormPanel.add(groupNameField);

        // Description
        groupDescriptionField = new KapuaTextField<String>();
        groupDescriptionField.setAllowBlank(true);
        groupDescriptionField.setMaxLength(255);
        groupDescriptionField.setName("description");
        groupDescriptionField.setFieldLabel(MSGS.dialogAddFieldDescription());
        groupNameField.setToolTip(MSGS.dialogAddFieldDescriptionTooltip());
        groupFormPanel.add(groupDescriptionField);

        // Domain
        domainsCombo = new ComboBox<GwtDomain>();
        domainsCombo.setStore(new ListStore<GwtDomain>());
        domainsCombo.setEditable(false);
        domainsCombo.setTypeAhead(false);
        domainsCombo.setAllowBlank(false);
        domainsCombo.disable();
        domainsCombo.setFieldLabel("* Domain");
        domainsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        domainsCombo.setEmptyText("Loading Domains...");
        domainsCombo.setToolTip("Select a target domain for the Group");
        domainsCombo.setDisplayField("domainName");
        domainsCombo.addSelectionChangedListener(new SelectionChangedListener<GwtDomain>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<GwtDomain> selectionChangedEvent) {
                setSubmitButtonState();
            }
        });
        groupFormPanel.add(domainsCombo);

        // Load domains
        DOMAIN_SERVICE.findAll(new AsyncCallback<List<GwtDomain>>() {

            @Override
            public void onFailure(Throwable caught) {
                exitStatus = false;
                if (!isPermissionErrorMessage(caught)) {
                    exitMessage = MSGS.dialogAddError(caught.getLocalizedMessage());
                }
                hide();
            }

            @Override
            public void onSuccess(List<GwtDomain> result) {
                domainsCombo.setEmptyText("Select a Domain...");

                domainsCombo.getStore().add(result);
                domainsCombo.enable();
            }
        });

        domainsLabel = new LabelField();
        domainsLabel.setFieldLabel("Domain");
        domainsLabel.setLabelSeparator(":");
        domainsLabel.hide();
        groupFormPanel.add(domainsLabel);

        // Add form panel to main body
        bodyPanel.add(groupFormPanel);
    }

    public void validateGroups() {
        if (groupNameField.getValue() == null) {
            ConsoleInfo.display("Error", CONSOLE_MSGS.allFieldsRequired());
        }
    }

    @Override
    protected void preSubmit() {
        validateGroups();

        super.preSubmit();
    }

    @Override
    public void submit() {
        GwtGroupCreator gwtGroupCreator = new GwtGroupCreator();
        gwtGroupCreator.setScopeId(currentSession.getSelectedAccountId());
        gwtGroupCreator.setName(groupNameField.getValue());
        gwtGroupCreator.setDescription(groupDescriptionField.getValue());
        gwtGroupCreator.setDomain(domainsCombo.getValue().getDomainName());

        GWT_GROUP_SERVICE.create(gwtGroupCreator, new AsyncCallback<GwtGroup>() {

            @Override
            public void onSuccess(GwtGroup gwtGroup) {
                exitStatus = true;
                exitMessage = MSGS.dialogAddConfirmation();
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
                    if (cause instanceof GwtKapuaException) {
                        GwtKapuaException gwtCause = (GwtKapuaException) cause;
                        if (gwtCause.getCode().equals(GwtKapuaErrorCode.DUPLICATE_NAME)) {
                            groupNameField.markInvalid(gwtCause.getMessage());
                        }
                    }
                    FailureHandler.handleFormException(formPanel, cause);
                }
            }
        });

    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogAddHeader();
    }

    @Override
    public String getInfoMessage() {
        return MSGS.dialogAddInfo();
    }
}
