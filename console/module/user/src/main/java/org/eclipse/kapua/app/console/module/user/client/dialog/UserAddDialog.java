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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaDateField;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaTextField;
import org.eclipse.kapua.app.console.module.api.client.util.Constants;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.validator.ConfirmPasswordFieldValidator;
import org.eclipse.kapua.app.console.module.api.client.util.validator.PasswordFieldValidator;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator.FieldType;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authentication.shared.model.permission.CredentialSessionPermission;
import org.eclipse.kapua.app.console.module.authentication.shared.service.GwtCredentialService;
import org.eclipse.kapua.app.console.module.authentication.shared.service.GwtCredentialServiceAsync;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.permission.GroupSessionPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupServiceAsync;
import org.eclipse.kapua.app.console.module.user.client.messages.ConsoleUserMessages;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser.GwtUserStatus;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUserCreator;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserServiceAsync;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserAddDialog extends EntityAddEditDialog {

    private enum RadioGroupStatus {
        INTERNAL, EXTERNAL
    }

    protected static final ConsoleUserMessages USER_MSGS = GWT.create(ConsoleUserMessages.class);
    private static final GwtCredentialServiceAsync GWT_CREDENTIAL_SERVICE = GWT.create(GwtCredentialService.class);
    private static final GwtGroupServiceAsync GWT_GROUP_SERVICE = GWT.create(GwtGroupService.class);
    private static final GwtUserServiceAsync GWT_USER_SERVICE = GWT.create(GwtUserService.class);

    protected FieldSet infoFieldSet;

    protected KapuaTextField<String> username;
    protected LabelField usernameLabel;

    protected ComboBox<GwtGroup> groupCombo;

    protected KapuaTextField<String> externalId;
    protected LabelField externalIdLabel;

    protected KapuaTextField<String> externalUsername;
    protected LabelField externalUsernameLabel;

    protected KapuaTextField<String> password;
    protected KapuaTextField<String> confirmPassword;
    protected LabelField passwordTooltip;

    protected KapuaTextField<String> displayName;
    protected KapuaTextField<String> email;
    protected KapuaTextField<String> phoneNumber;
    protected SimpleComboBox<GwtUser.GwtUserStatus> userStatus;

    protected LabelField userType;

    protected KapuaDateField expirationDate;

    protected NumberField optlock;

    protected RadioGroup userTypeRadioGroup = new RadioGroup();

    private String scopeId;

    protected static final GwtGroup NO_GROUP;

    static {
        NO_GROUP = new GwtGroup();
        NO_GROUP.setGroupName("No Group");
        NO_GROUP.setId(null);
    }

    public UserAddDialog(GwtSession currentSession) {
        super(currentSession);
        DialogUtils.resizeDialog(this, 400, 500);
    }

    public UserAddDialog(GwtSession currentSession, String scopeId) {
        this(currentSession);

        this.scopeId = scopeId;
    }

    @Override
    public void createBody() {
        submitButton.disable();

        FormPanel userFormPanel = new FormPanel(FORM_LABEL_WIDTH);
        userFormPanel.setFrame(false);
        userFormPanel.setBorders(false);
        userFormPanel.setBodyBorder(false);
        userFormPanel.setHeaderVisible(false);
        userFormPanel.setPadding(0);
        bodyPanel.add(userFormPanel);

        FormData userFormData = new FormData();
        FormData userFieldsetFormData = new FormData();

        // Info fieldset
        FormLayout userInfoLayout = new FormLayout();
        userInfoLayout.setLabelWidth(Constants.LABEL_WIDTH_FORM);

        infoFieldSet = new FieldSet();
        infoFieldSet.setHeading(USER_MSGS.dialogAddFieldSet());
        infoFieldSet.setBorders(true);
        infoFieldSet.setStyleAttribute("margin", "0px 10px 0px 10px");
        infoFieldSet.setLayout(userInfoLayout);
        infoFieldSet.setStyleAttribute("background-color", "E8E8E8");
        userFormPanel.add(infoFieldSet, userFormData);

        // Username
        usernameLabel = new LabelField();
        usernameLabel.setFieldLabel(USER_MSGS.dialogAddFieldUsername());
        usernameLabel.setLabelSeparator(":");
        usernameLabel.hide();
        usernameLabel.setStyleAttribute("white-space", "nowrap");
        usernameLabel.setStyleAttribute("text-overflow", "ellipsis");
        usernameLabel.setStyleAttribute("overflow", "hidden");
        infoFieldSet.add(usernameLabel);

        username = new KapuaTextField<String>();
        username.setAllowBlank(false);
        username.setMaxLength(255);
        username.setName("userName");
        username.setFieldLabel("* " + USER_MSGS.dialogAddFieldUsername());
        username.setValidator(new TextFieldValidator(username, FieldType.NAME));
        username.setToolTip(USER_MSGS.dialogAddFieldNameTooltip());
        infoFieldSet.add(username, userFieldsetFormData);

        // groupIds
        groupCombo = new ComboBox<GwtGroup>();
        groupCombo.setStore(new ListStore<GwtGroup>());
        groupCombo.setFieldLabel("* Access Group");
        groupCombo.setToolTip("Select an access group in which you want the device to be part of.");
        groupCombo.setForceSelection(true);
        groupCombo.setTypeAhead(false);
        groupCombo.setTriggerAction(TriggerAction.ALL);
        groupCombo.setAllowBlank(false);
        groupCombo.setEditable(false);
        groupCombo.setDisplayField("groupName");
        groupCombo.setTemplate("<tpl for=\".\"><div role=\"listitem\" class=\"x-combo-list-item\" title={groupName}>{groupName}</div></tpl>");
        groupCombo.setValueField("id");
        groupCombo.setEmptyText("Select An Access Group");

        if (currentSession.hasPermission(GroupSessionPermission.read())) {
            Listener<BaseEvent> comboBoxListener = new Listener<BaseEvent>() {

                @Override
                public void handleEvent(BaseEvent be) {
                    UserAddDialog.this.formPanel.fireEvent(Events.OnClick);
                }
            };

            groupCombo.addListener(Events.Select, comboBoxListener);

            GWT_GROUP_SERVICE.findAll(currentSession.getSelectedAccountId(), new AsyncCallback<List<GwtGroup>>() {

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
            infoFieldSet.add(groupCombo, userFieldsetFormData);
        } else {
            groupCombo.getStore().removeAll();
            groupCombo.getStore().add(NO_GROUP);
            groupCombo.setValue(NO_GROUP);
        }

        // User type
        userTypeRadioGroup.setFieldLabel(USER_MSGS.dialogAddFieldUserTypeRadioButton());
        userTypeRadioGroup.setOrientation(Style.Orientation.HORIZONTAL);
        userTypeRadioGroup.setSelectionRequired(true);
        userTypeRadioGroup.setStyleAttribute("margin-left", "5px");
        userTypeRadioGroup.hide();
        infoFieldSet.add(userTypeRadioGroup);

        userTypeRadioGroup.addListener(Events.Change, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent baseEvent) {
                Radio radio = ((RadioGroup) baseEvent.getSource()).getValue();
                if (radio.getValueAttribute().equals(RadioGroupStatus.INTERNAL.name())) {
                    password.show();
                    password.enable();
                    confirmPassword.show();
                    confirmPassword.enable();
                    passwordTooltip.show();

                    externalId.hide();
                    externalId.disable();
                    externalUsername.hide();
                    externalUsername.disable();
                } else if (radio.getValueAttribute().equals(RadioGroupStatus.EXTERNAL.name())) {
                    password.hide();
                    password.disable();
                    confirmPassword.hide();
                    confirmPassword.disable();
                    passwordTooltip.hide();

                    externalId.show();
                    externalId.enable();
                    externalUsername.show();
                    externalUsername.enable();
                }
            }
        });

        Radio userTypeInternalRadio = new Radio();
        userTypeInternalRadio.setValueAttribute(RadioGroupStatus.INTERNAL.name());
        userTypeInternalRadio.setBoxLabel(USER_MSGS.dialogAddFieldInternalUser());
        userTypeInternalRadio.setValue(true);
        userTypeRadioGroup.add(userTypeInternalRadio);

        Radio userTypeExternalRadio = new Radio();
        userTypeExternalRadio.setValueAttribute(RadioGroupStatus.EXTERNAL.name());
        userTypeExternalRadio.setBoxLabel(USER_MSGS.dialogAddFieldExternalUser());
        userTypeRadioGroup.add(userTypeExternalRadio);

        // Password
        password = new KapuaTextField<String>();
        password.setAllowBlank(false);
        password.setName("password");
        password.setFieldLabel("* " + USER_MSGS.dialogAddFieldPassword());
        password.setPassword(true);
        password.setMaxLength(255);

        // Confirm Password
        confirmPassword = new KapuaTextField<String>();
        confirmPassword.setAllowBlank(false);
        confirmPassword.setName("confirmPassword");
        confirmPassword.setFieldLabel("* " + USER_MSGS.dialogAddFieldConfirmPassword());
        confirmPassword.setPassword(true);
        confirmPassword.setMaxLength(255);

        passwordTooltip = new LabelField();
        passwordTooltip.setStyleAttribute("margin-top", "-5px");
        passwordTooltip.setStyleAttribute("color", "gray");
        passwordTooltip.setStyleAttribute("font-size", "10px");

        if (currentSession.hasPermission(CredentialSessionPermission.read())) {
            GWT_CREDENTIAL_SERVICE.getMinPasswordLength(scopeId != null ? scopeId : currentSession.getSelectedAccountId(), new AsyncCallback<Integer>() {

                @Override
                public void onFailure(Throwable caught) {
                    FailureHandler.handle(caught);
                }

                @Override
                public void onSuccess(Integer result) {
                    password.setValidator(new PasswordFieldValidator(password, result));
                    confirmPassword.setValidator(new ConfirmPasswordFieldValidator(confirmPassword, password, result));
                    passwordTooltip.setValue(MSGS.dialogAddTooltipCredentialPassword(result.toString()));
                }
            });
        }

        if (currentSession.hasPermission(CredentialSessionPermission.write())) {
            infoFieldSet.add(password, userFieldsetFormData);
            infoFieldSet.add(confirmPassword, userFieldsetFormData);
            infoFieldSet.add(passwordTooltip);
        }

        // External Id
        externalIdLabel = new LabelField();
        externalIdLabel.setFieldLabel(USER_MSGS.dialogAddFieldExternalId());
        externalIdLabel.setLabelSeparator(":");
        externalIdLabel.setStyleAttribute("white-space", "nowrap");
        externalIdLabel.setStyleAttribute("text-overflow", "ellipsis");
        externalIdLabel.setStyleAttribute("overflow", "hidden");
        externalIdLabel.hide();
        infoFieldSet.add(externalIdLabel);

        externalId = new KapuaTextField<String>();
        externalId.setMaxLength(255);
        externalId.setName("externalId");
        externalId.setFieldLabel(USER_MSGS.dialogAddFieldExternalId());
        externalId.setToolTip(USER_MSGS.dialogAddFieldExternalIdTooltip());
        externalId.hide();
        externalId.disable();
        infoFieldSet.add(externalId, userFieldsetFormData);

        // External Username
        externalUsernameLabel = new LabelField();
        externalUsernameLabel.setFieldLabel("External Username");
        externalUsernameLabel.setLabelSeparator(":");
        externalUsernameLabel.hide();
        infoFieldSet.add(externalUsernameLabel);

        externalUsername = new KapuaTextField<String>();
        externalUsername.setMaxLength(255);
        externalUsername.setName("externalUsername");
        externalUsername.setFieldLabel("External Username");
        externalUsername.setToolTip("Set the external username. This username is required when the external id is not known and it is provided by the OpenId Connect Provider.");
        externalUsername.hide();
        externalUsername.disable();
        infoFieldSet.add(externalUsername, userFieldsetFormData);

        // Display Name
        displayName = new KapuaTextField<String>();
        displayName.setName("displayName");
        displayName.setFieldLabel(USER_MSGS.dialogAddFieldDisplayName());
        displayName.setToolTip(USER_MSGS.dialogAddFieldDisplayNameTooltip());
        displayName.setMaxLength(255);
        displayName.setStyleAttribute("white-space", "nowrap");
        displayName.setStyleAttribute("text-overflow", "ellipsis");
        displayName.setStyleAttribute("overflow", "hidden");
        infoFieldSet.add(displayName, userFieldsetFormData);

        // Email
        email = new KapuaTextField<String>();
        email.setName("userEmail");
        email.setFieldLabel(USER_MSGS.dialogAddFieldEmail());
        email.setValidator(new TextFieldValidator(email, FieldType.EMAIL));
        email.setToolTip(USER_MSGS.dialogAddFieldEmailTooltip());
        email.setMaxLength(255);
        infoFieldSet.add(email, userFieldsetFormData);

        // Phone number
        phoneNumber = new KapuaTextField<String>();
        phoneNumber.setName("phoneNumber");
        phoneNumber.setFieldLabel(USER_MSGS.dialogAddFieldPhoneNumber());
        phoneNumber.setValidator(new TextFieldValidator(phoneNumber, FieldType.PHONE));
        phoneNumber.setToolTip(USER_MSGS.dialogAddFieldPhoneNumberTooltip());
        phoneNumber.setMaxLength(64);
        infoFieldSet.add(phoneNumber, userFieldsetFormData);

        // Optlock
        optlock = new NumberField();
        optlock.setName("optlock");
        optlock.setEditable(false);
        optlock.hide();
        infoFieldSet.add(optlock);

        // Status fieldset
        FormLayout statusLayout = new FormLayout();
        statusLayout.setLabelWidth(Constants.LABEL_WIDTH_FORM);

        FieldSet statusFieldSet = new FieldSet();
        statusFieldSet.setBorders(true);
        statusFieldSet.setStyleAttribute("margin", "5px 10px 0px 10px");
        statusFieldSet.setHeading(USER_MSGS.dialogAddStatus());
        statusFieldSet.setLayout(statusLayout);
        statusFieldSet.setStyleAttribute("background-color", "E8E8E8");
        userFormPanel.add(statusFieldSet, userFormData);

        // User status
        userStatus = new SimpleComboBox<GwtUserStatus>();
        userStatus.setName("comboStatus");
        userStatus.setFieldLabel(USER_MSGS.dialogAddStatus());
        userStatus.setToolTip(USER_MSGS.dialogAddStatusTooltip());
        userStatus.setLabelSeparator(":");
        userStatus.setEditable(false);
        userStatus.setTypeAhead(true);
        userStatus.setTriggerAction(TriggerAction.ALL);

        userStatus.add(GwtUserStatus.ENABLED);
        userStatus.add(GwtUserStatus.DISABLED);
        userStatus.setSimpleValue(GwtUserStatus.ENABLED);
        statusFieldSet.add(userStatus, userFieldsetFormData);

        // Expiration Date
        expirationDate = new KapuaDateField(this);
        expirationDate.setName("expirationDate");
        expirationDate.setFormatValue(true);
        expirationDate.getPropertyEditor().setFormat(DateTimeFormat.getFormat("dd/MM/yyyy"));
        expirationDate.setFieldLabel(USER_MSGS.dialogAddExpirationDate());
        expirationDate.setToolTip(USER_MSGS.dialogAddExpirationDateTooltip());
        expirationDate.setLabelSeparator(":");
        expirationDate.setAllowBlank(true);
        expirationDate.setEmptyText(USER_MSGS.dialogAddNoExpiration());
        expirationDate.setValue(null);
        expirationDate.setMaxLength(10);
        expirationDate.getDatePicker().addListener(Events.Select, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                formPanel.fireEvent(Events.OnClick);
            }
        });
        statusFieldSet.add(expirationDate, userFieldsetFormData);

        // Check SSO enabled.
        if (currentSession.isSsoEnabled()) {
            userTypeRadioGroup.show();
        }
    }

    @Override
    protected void preSubmit() {
        if (userTypeRadioGroup.getValue().getValueAttribute().equals(RadioGroupStatus.EXTERNAL.name())) {
            if ((externalId.getValue() == null || externalId.getValue().isEmpty()) &&
                    (externalUsername.getValue() == null || externalUsername.getValue().isEmpty())) {
                externalId.markInvalid("Either the External Id or the External Username Id are required");
                externalUsername.markInvalid("Either the External Username or the External Id are required");
                return;
            }
        }

        super.preSubmit();
    }

    @Override
    public void submit() {
        GwtUserCreator gwtUserCreator = new GwtUserCreator();

        gwtUserCreator.setScopeId(scopeId != null ? scopeId : currentSession.getSelectedAccountId());
        gwtUserCreator.setUsername(username.getValue());
        gwtUserCreator.setGroupId(groupCombo.getValue().getId());

        if (userTypeRadioGroup.getValue().getValueAttribute().equals(RadioGroupStatus.INTERNAL.name())) {
            gwtUserCreator.setGwtUserType(GwtUser.GwtUserType.INTERNAL);
            gwtUserCreator.setPassword(password.getValue());
        } else if (userTypeRadioGroup.getValue().getValueAttribute().equals(RadioGroupStatus.EXTERNAL.name())) {
            gwtUserCreator.setGwtUserType(GwtUser.GwtUserType.EXTERNAL);
            gwtUserCreator.setExternalId(externalId.getValue());
            gwtUserCreator.setExternalUsername(externalUsername.getValue());
        }

        gwtUserCreator.setDisplayName(displayName.getValue());
        gwtUserCreator.setEmail(email.getValue());
        gwtUserCreator.setPhoneNumber(phoneNumber.getValue());
        gwtUserCreator.setUserStatus(userStatus.getValue().getValue());
        gwtUserCreator.setExpirationDate(expirationDate.getValue());

        GWT_USER_SERVICE.create(xsrfToken, gwtUserCreator, new AsyncCallback<GwtUser>() {

            @Override
            public void onSuccess(GwtUser gwtUser) {
                exitStatus = true;
                exitMessage = USER_MSGS.dialogAddConfirmation();
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
                        switch (gwtCause.getCode()) {
                            case DUPLICATE_NAME:
                            case ENTITY_ALREADY_EXIST_IN_ANOTHER_ACCOUNT:
                                username.markInvalid(gwtCause.getMessage());
                                break;
                            case EXTERNAL_ID_ALREADY_EXIST:
                                externalId.markInvalid(gwtCause.getMessage());
                                break;
                            case EXTERNAL_USERNAME_ALREADY_EXIST:
                                externalUsername.markInvalid(gwtCause.getMessage());
                                break;
                            default:
                                break;
                        }
                    }
                    FailureHandler.handleFormException(formPanel, cause);
                }
            }
        });
    }

    @Override
    public String getHeaderMessage() {
        return USER_MSGS.dialogAddHeader();
    }

    @Override
    public String getInfoMessage() {
        return USER_MSGS.dialogAddInfo();
    }

}
