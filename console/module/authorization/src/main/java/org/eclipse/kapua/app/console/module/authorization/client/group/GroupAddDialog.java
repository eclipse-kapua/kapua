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
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaTextField;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator.FieldType;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtDomain;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtDomainRegistryService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtDomainRegistryServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class GroupAddDialog extends EntityAddEditDialog {

    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);
    private static final ConsoleMessages CONSOLE_MSGS = GWT.create(ConsoleMessages.class);

    private static final GwtDomainRegistryServiceAsync DOMAIN_SERVICE = GWT.create(GwtDomainRegistryService.class);

    private final EntityGroupDataProvider entityGroupDataProvider;

    public KapuaTextField<String> groupNameField;
    protected KapuaTextField<String> groupDescriptionField;

    protected ComboBox<GwtDomain> domainsCombo;

    protected LabelField domainsLabel;

    public GroupAddDialog(GwtSession currentSession, EntityGroupDataProvider entityGroupDataProvider) {
        super(currentSession);

        this.entityGroupDataProvider = entityGroupDataProvider;

        DialogUtils.resizeDialog(this, 400, 200);
    }

    @Override
    public void createBody() {
        submitButton.disable();

        FormPanel groupFormPanel = new FormPanel(FORM_LABEL_WIDTH);

        String assignableDomain = entityGroupDataProvider.getAssignableDomain();

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
        if (assignableDomain == null) {
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

                    List<GwtDomain> groupableDomains = new ArrayList<GwtDomain>();

                    for (GwtDomain gwtDomain : result) {
                        if (gwtDomain.getGroupable()) {
                            groupableDomains.add(gwtDomain);
                        }
                    }

                    domainsCombo.getStore().add(groupableDomains);
                    domainsCombo.enable();
                }
            });
        }

        domainsLabel = new LabelField();
        domainsLabel.setFieldLabel("Domain");
        domainsLabel.setLabelSeparator(":");
        domainsLabel.setVisible(assignableDomain != null);
        if (assignableDomain != null) {
            domainsLabel.setValue(entityGroupDataProvider.getAssignableDomain());
        }
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
        if (domainsCombo != null) {
            gwtGroupCreator.setDomain(domainsCombo.getValue().getDomainName());
        }

        entityGroupDataProvider.handleCreateEntityGroup(this, gwtGroupCreator);
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
