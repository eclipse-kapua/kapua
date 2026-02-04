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
package org.eclipse.kapua.app.console.module.device.client.device;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaTextField;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.Constants;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.KapuaSafeHtmlUtils;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator.FieldType;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.permission.GroupSessionPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupServiceAsync;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleDeviceMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceCreator;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates.GwtDeviceStatus;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceServiceAsync;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceAddDialog extends EntityAddEditDialog {

    protected static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);
    protected static final ConsoleDeviceMessages DEVICE_MSGS = GWT.create(ConsoleDeviceMessages.class);

    protected final GwtDeviceServiceAsync gwtDeviceService = GWT.create(GwtDeviceService.class);
    // protected final GwtUserServiceAsync gwtUserService = GWT.create(GwtUserService.class);
    protected final GwtGroupServiceAsync gwtGroupService = GWT.create(GwtGroupService.class);

    // General info fields
    protected LabelField clientIdLabel;
    protected KapuaTextField<String> clientIdField;
    protected ComboBox<GwtGroup> groupCombo;
    protected KapuaTextField<String> displayNameField;
    protected SimpleComboBox<GwtDeviceStatus> statusCombo;
    protected FieldSet fieldSet;

    // Security Options fields
    // protected SimpleComboBox<String> credentialsTightCombo;
    // protected ComboBox<GwtUser> deviceUserCombo;
    // protected CheckBox allowCredentialsChangeCheckbox;

    // Custom attributes
    protected KapuaTextField<String> customAttribute1Field;
    protected KapuaTextField<String> customAttribute2Field;
    protected KapuaTextField<String> customAttribute3Field;
    protected KapuaTextField<String> customAttribute4Field;
    protected KapuaTextField<String> customAttribute5Field;

    protected NumberField optlock;

    protected static final GwtGroup NO_GROUP;

    static {
        NO_GROUP = new GwtGroup();
        NO_GROUP.setGroupName(DEVICE_MSGS.deviceFormNoGroup());
        NO_GROUP.setId(null);
    }

    public DeviceAddDialog(GwtSession currentSession) {
        super(currentSession);

        DialogUtils.resizeDialog(this, 550, 450);
    }

    @Override
    public void createBody() {
        generateBody();
        submitButton.disable();

        // hide fields used for edit
        clientIdLabel.hide();
    }

    protected void generateBody() {

        FormData formData = new FormData("-20");

        FormPanel formPanel = new FormPanel(FORM_LABEL_WIDTH);
        formPanel.setFrame(false);
        formPanel.setBodyBorder(false);
        formPanel.setHeaderVisible(false);
        formPanel.setScrollMode(Scroll.AUTOY);

        Listener<BaseEvent> comboBoxListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                DeviceAddDialog.this.formPanel.fireEvent(Events.OnClick);
            }
        };

        // Device general info fieldset
        fieldSet = new FieldSet();
        FormLayout layout = new FormLayout();
        layout.setLabelWidth(Constants.LABEL_WIDTH_DEVICE_FORM);
        fieldSet.setLayout(layout);
        fieldSet.setHeading(DEVICE_MSGS.deviceFormFieldsetGeneralInfo());

        // Device Client ID
        clientIdLabel = new LabelField();
        clientIdLabel.setFieldLabel(DEVICE_MSGS.deviceFormClientID());
        clientIdLabel.setLabelSeparator(":");
        clientIdLabel.setWidth(225);
        fieldSet.add(clientIdLabel, formData);

        clientIdField = new KapuaTextField<String>();
        clientIdField.setAllowBlank(false);
        clientIdField.setName("clientID");
        clientIdField.setFieldLabel("* " + DEVICE_MSGS.deviceFormClientID());
        clientIdField.setValidator(new TextFieldValidator(clientIdField, FieldType.DEVICE_CLIENT_ID));
        clientIdField.setToolTip(DEVICE_MSGS.deviceFormClientIDTooltip());
        clientIdField.setWidth(225);
        clientIdField.setMaxLength(255);

        fieldSet.add(clientIdField, formData);

        // Display name
        displayNameField = new KapuaTextField<String>();
        displayNameField.setName("displayName");
        displayNameField.setFieldLabel(DEVICE_MSGS.deviceFormDisplayName());
        displayNameField.setToolTip(DEVICE_MSGS.deviceFormDisplayNameTooltip());
        displayNameField.setWidth(225);
        displayNameField.setMaxLength(255);
        fieldSet.add(displayNameField, formData);

        // Device Status
        statusCombo = new SimpleComboBox<GwtDeviceQueryPredicates.GwtDeviceStatus>();
        statusCombo.setAllowBlank(false);
        statusCombo.setName("status");
        statusCombo.setFieldLabel("* " + DEVICE_MSGS.deviceFormStatus());
        statusCombo.setToolTip(DEVICE_MSGS.deviceFormStatusTooltip());
        statusCombo.setEditable(false);
        statusCombo.setTriggerAction(TriggerAction.ALL);

        statusCombo.setEmptyText(DEVICE_MSGS.deviceFilteringPanelStatusEmptyText());
        statusCombo.add(GwtDeviceQueryPredicates.GwtDeviceStatus.ENABLED);
        statusCombo.add(GwtDeviceQueryPredicates.GwtDeviceStatus.DISABLED);
        statusCombo.setSimpleValue(GwtDeviceStatus.ENABLED);

        fieldSet.add(statusCombo, formData);

        groupCombo = new ComboBox<GwtGroup>();
        groupCombo.setStore(new ListStore<GwtGroup>());
        groupCombo.setFieldLabel("* " + DEVICE_MSGS.deviceFormGroup());
        groupCombo.setToolTip(DEVICE_MSGS.deviceFormGroupTooltip());
        groupCombo.setForceSelection(true);
        groupCombo.setTypeAhead(false);
        groupCombo.setTriggerAction(TriggerAction.ALL);
        groupCombo.setAllowBlank(false);
        groupCombo.setEditable(false);
        groupCombo.setDisplayField("groupName");
        groupCombo.setTemplate("<tpl for=\".\"><div role=\"listitem\" class=\"x-combo-list-item\" title={groupName}>{groupName}</div></tpl>");
        groupCombo.setValueField("id");
        groupCombo.setEmptyText(DEVICE_MSGS.deviceFilteringPanelGroupEmptyText());

        if (currentSession.hasPermission(GroupSessionPermission.read())) {
            groupCombo.addListener(Events.Select, comboBoxListener);

            gwtGroupService.findAll(currentSession.getSelectedAccountId(), "device", new AsyncCallback<List<GwtGroup>>() {

                @Override
                public void onFailure(Throwable caught) {
                    exitStatus = false;
                    if (!isPermissionErrorMessage(caught)) {
                        FailureHandler.handle(caught);
                        hide();
                    }
                }

                @Override
                public void onSuccess(List<GwtGroup> result) {
                    groupCombo.getStore().removeAll();
                    groupCombo.getStore().add(NO_GROUP);

                    Collections.sort(result, new Comparator<GwtGroup>() {

                        @Override
                        public int compare(GwtGroup group1, GwtGroup group2) {
                            return group1.getGroupName().compareTo(group2.getGroupName());
                        }
                    });

                    groupCombo.getStore().add(result);
                    groupCombo.setValue(NO_GROUP);
                }
            });
            fieldSet.add(groupCombo, formData);
        } else {
            groupCombo.getStore().removeAll();
            groupCombo.getStore().add(NO_GROUP);
            groupCombo.setValue(NO_GROUP);
        }

        // Device Custom attributes fieldset
        FieldSet fieldSetCustomAttributes = new FieldSet();
        FormLayout layoutCustomAttributes = new FormLayout();
        layoutCustomAttributes.setLabelWidth(Constants.LABEL_WIDTH_DEVICE_FORM);
        fieldSetCustomAttributes.setLayout(layoutCustomAttributes);
        fieldSetCustomAttributes.setHeading(DEVICE_MSGS.deviceFormFieldsetCustomAttributes());

        // Custom Attribute #1
        customAttribute1Field = new KapuaTextField<String>();
        customAttribute1Field.setName("customAttribute1");
        customAttribute1Field.setFieldLabel(DEVICE_MSGS.deviceFormCustomAttribute1());
        customAttribute1Field.setToolTip(DEVICE_MSGS.deviceFormCustomAttributesTooltip());
        customAttribute1Field.setWidth(225);
        customAttribute1Field.setMaxLength(255);
        fieldSetCustomAttributes.add(customAttribute1Field, formData);

        // Custom Attribute #2
        customAttribute2Field = new KapuaTextField<String>();
        customAttribute2Field.setName("customAttribute2");
        customAttribute2Field.setFieldLabel(DEVICE_MSGS.deviceFormCustomAttribute2());
        customAttribute2Field.setToolTip(DEVICE_MSGS.deviceFormCustomAttributesTooltip());
        customAttribute2Field.setWidth(225);
        customAttribute2Field.setMaxLength(255);
        fieldSetCustomAttributes.add(customAttribute2Field, formData);

        // Custom Attribute #3
        customAttribute3Field = new KapuaTextField<String>();
        customAttribute3Field.setName("customAttribute3");
        customAttribute3Field.setFieldLabel(DEVICE_MSGS.deviceFormCustomAttribute3());
        customAttribute3Field.setToolTip(DEVICE_MSGS.deviceFormCustomAttributesTooltip());
        customAttribute3Field.setWidth(225);
        customAttribute3Field.setMaxLength(255);
        fieldSetCustomAttributes.add(customAttribute3Field, formData);

        // Custom Attribute #4
        customAttribute4Field = new KapuaTextField<String>();
        customAttribute4Field.setName("customAttribute4");
        customAttribute4Field.setFieldLabel(DEVICE_MSGS.deviceFormCustomAttribute4());
        customAttribute4Field.setToolTip(DEVICE_MSGS.deviceFormCustomAttributesTooltip());
        customAttribute4Field.setWidth(225);
        customAttribute4Field.setMaxLength(255);
        fieldSetCustomAttributes.add(customAttribute4Field, formData);

        // Custom Attribute #5
        customAttribute5Field = new KapuaTextField<String>();
        customAttribute5Field.setName("customAttribute5");
        customAttribute5Field.setFieldLabel(DEVICE_MSGS.deviceFormCustomAttribute5());
        customAttribute5Field.setToolTip(DEVICE_MSGS.deviceFormCustomAttributesTooltip());
        customAttribute5Field.setWidth(225);
        customAttribute5Field.setMaxLength(255);
        fieldSetCustomAttributes.add(customAttribute5Field, formData);

        // Optlock
        optlock = new NumberField();
        optlock.setName("optlock");
        optlock.setEditable(false);
        optlock.setVisible(false);
        fieldSet.add(optlock, formData);

        formPanel.add(fieldSet);
        formPanel.add(fieldSetCustomAttributes);

        bodyPanel.add(formPanel);
    }

    public void validateDeviceInput() {
        if (clientIdField.isVisible() && clientIdField.getValue() == null || displayNameField.getValue() == null || statusCombo.getValue() == null || groupCombo.getValue() == null) {
            ConsoleInfo.display("Error", MSGS.allFieldsRequired());
        }
    }

    @Override
    protected void preSubmit() {
        validateDeviceInput();
        super.preSubmit();
    }

    @Override
    public void submit() {
        GwtDeviceCreator gwtDeviceCreator = new GwtDeviceCreator();
        gwtDeviceCreator.setScopeId(currentSession.getSelectedAccountId());

        gwtDeviceCreator.setClientId(clientIdField.getValue());
        gwtDeviceCreator.setGroupId(groupCombo.getValue().getId());
        gwtDeviceCreator.setDisplayName(displayNameField.getValue());
        gwtDeviceCreator.setDeviceStatus(statusCombo.getSimpleValue().name());

        // Custom attributes
        gwtDeviceCreator.setCustomAttribute1(KapuaSafeHtmlUtils.htmlUnescape(customAttribute1Field.getValue()));
        gwtDeviceCreator.setCustomAttribute2(KapuaSafeHtmlUtils.htmlUnescape(customAttribute2Field.getValue()));
        gwtDeviceCreator.setCustomAttribute3(KapuaSafeHtmlUtils.htmlUnescape(customAttribute3Field.getValue()));
        gwtDeviceCreator.setCustomAttribute4(KapuaSafeHtmlUtils.htmlUnescape(customAttribute4Field.getValue()));
        gwtDeviceCreator.setCustomAttribute5(KapuaSafeHtmlUtils.htmlUnescape(customAttribute5Field.getValue()));
        // Submit
        gwtDeviceService.createDevice(xsrfToken, gwtDeviceCreator, new AsyncCallback<GwtDevice>() {

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
                            clientIdField.markInvalid(gwtCause.getMessage());
                        }
                    }
                    FailureHandler.handleFormException(formPanel, cause);
                }
            }

            @Override
            public void onSuccess(GwtDevice gwtDevice) {
                exitStatus = true;
                exitMessage = DEVICE_MSGS.deviceCreationSuccess();
                hide();
            }
        });
    }

    @Override
    public String getHeaderMessage() {
        return DEVICE_MSGS.deviceFormHeadingNew();
    }

    @Override
    public String getInfoMessage() {
        return DEVICE_MSGS.dialogDeviceAddInfoMessage();
    }

}
