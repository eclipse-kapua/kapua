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
package org.eclipse.kapua.app.console.module.account.client.toolbar;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.account.client.messages.ConsoleAccountMessages;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccountCreator;
import org.eclipse.kapua.app.console.module.account.shared.service.GwtAccountService;
import org.eclipse.kapua.app.console.module.account.shared.service.GwtAccountServiceAsync;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaDateField;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaTextField;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.KapuaSafeHtmlUtils;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator.FieldType;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;

public class AccountAddDialog extends EntityAddEditDialog {

    protected static final ConsoleAccountMessages MSGS = GWT.create(ConsoleAccountMessages.class);
    protected static final GwtAccountServiceAsync GWT_ACCOUNT_SERVICE = GWT.create(GwtAccountService.class);
    protected static final ConsoleMessages CMSGS = GWT.create(ConsoleMessages.class);

    private static final int LABEL_WIDTH_FORM = 150;

    protected final FieldSet fieldSet = new FieldSet();

    // Account
    final LabelField parentAccountNameLabel = new LabelField();
    protected final LabelField accountNameLabel = new LabelField();
    protected final KapuaTextField<String> accountNameField = new KapuaTextField<String>();
    protected final KapuaTextField<String> accountPassword = new KapuaTextField<String>();
    protected final KapuaDateField expirationDateField = new KapuaDateField(this);
    protected SimpleComboBox<GwtAccount.GwtAccountStatus> statusCombo;

    // broker cluster
    protected final NumberField optlock = new NumberField();
    protected final LabelField accountClusterLabel = new LabelField();

    // organization
    protected final KapuaTextField<String> organizationName = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationEmail = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationContactName = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationPhoneNumber = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationAddressLine1 = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationAddressLine2 = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationAddressLine3 = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationZipPostCode = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationCity = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationStateProvinceCounty = new KapuaTextField<String>();
    protected final KapuaTextField<String> organizationCountry = new KapuaTextField<String>();


    public AccountAddDialog(GwtSession currentSession) {
        super(currentSession);
        DialogUtils.resizeDialog(this, 600, 580);
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        bodyPanel.setAutoHeight(true);
        setClosable(false);
        setScrollMode(Scroll.AUTO);
        setAutoHeight(true);
    }

    @Override
    public void createBody() {
        submitButton.disable();
        FormPanel accountFormPanel = new FormPanel(FORM_LABEL_WIDTH);

        // //////////////////////////////////////////
        // Account Information field set
        // //////////////////////////////////////////
        fieldSet.setHeading(MSGS.accountFormInformation());
        FormLayout layoutAccount = new FormLayout();
        layoutAccount.setLabelWidth(LABEL_WIDTH_FORM);
        fieldSet.setLayout(layoutAccount);

        FormData accountFieldsetFormData = new FormData("-11");
        // Show parent account name
        parentAccountNameLabel.setName("parentAccount");
        parentAccountNameLabel.setFieldLabel(MSGS.accountFormParentAccount());
        parentAccountNameLabel.setLabelSeparator(":");
        parentAccountNameLabel.setToolTip(MSGS.accountFormParentAccountTooltip());
        parentAccountNameLabel.setStyleAttribute("word-wrap", "break-word");
        parentAccountNameLabel.setValue(currentSession.getSelectedAccountName());
        fieldSet.add(parentAccountNameLabel, accountFieldsetFormData);
        // Account name field
        accountNameLabel.setFieldLabel(MSGS.accountFormName());
        accountNameLabel.setLabelSeparator(":");
        accountNameLabel.setVisible(false);
        accountNameLabel.setStyleAttribute("word-wrap", "break-word");
        fieldSet.add(accountNameLabel, accountFieldsetFormData);

        accountNameField.setAllowBlank(false);
        accountNameField.setMaxLength(50);
        accountNameField.setName("accountName");
        accountNameField.setFieldLabel("* " + MSGS.accountFormName());
        accountNameField.setValidator(new TextFieldValidator(accountNameField, FieldType.SIMPLE_NAME));
        accountNameField.setToolTip(MSGS.accountFormParentAccountNameTooltip());
        fieldSet.add(accountNameField, accountFieldsetFormData);

        expirationDateField.setEmptyText(MSGS.accountFormNoExpiration());
        expirationDateField.setFieldLabel(MSGS.accountFormExpirationDate());
        expirationDateField.setToolTip(MSGS.accountFormExpirationDateTooltip());
        expirationDateField.setFormatValue(true);
        expirationDateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("dd/MM/yyyy"));
        expirationDateField.setMaxLength(10);
        expirationDateField.getDatePicker().addListener(Events.Select, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                formPanel.fireEvent(Events.OnClick);
            }
        });
        fieldSet.add(expirationDateField, accountFieldsetFormData);

        accountFormPanel.add(fieldSet);

        // Account Status
        statusCombo = new SimpleComboBox<GwtAccount.GwtAccountStatus>();
        statusCombo.setAllowBlank(false);
        statusCombo.setName(MSGS.accountStatus());
        statusCombo.setFieldLabel(MSGS.accountStatus());
        statusCombo.setToolTip(MSGS.accountStatusTooltip());
        statusCombo.setEditable(false);
        statusCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        statusCombo.add(GwtAccount.GwtAccountStatus.ENABLED);
        statusCombo.add(GwtAccount.GwtAccountStatus.DISABLED);
        statusCombo.setSimpleValue(GwtAccount.GwtAccountStatus.ENABLED);

        fieldSet.add(statusCombo);

        // //////////////////////////////////////////
        // Deployment Information field set
        // //////////////////////////////////////////
        FieldSet fieldSetDeployment = new FieldSet();
        fieldSetDeployment.setHeading(MSGS.accountFormDeploymentInformation());
        FormLayout layoutDeployment = new FormLayout();
        layoutDeployment.setLabelWidth(LABEL_WIDTH_FORM);
        fieldSetDeployment.setLayout(layoutDeployment);

        optlock.setName("optlock");
        optlock.setEditable(false);
        optlock.setVisible(false);
        fieldSetDeployment.add(optlock);

        // add the field set and reset
        // accountFormPanel.add(fieldSetDeployment);

        // //////////////////////////////////////////
        // Organization Information field set
        // //////////////////////////////////////////
        FieldSet fieldSetOrg = new FieldSet();
        fieldSetOrg.setHeading(MSGS.accountFormOrgInformation());
        FormLayout layoutOrg = new FormLayout();
        layoutOrg.setLabelWidth(LABEL_WIDTH_FORM);
        fieldSetOrg.setLayout(layoutOrg);
        // Organization name
        organizationName.setAllowBlank(false);
        organizationName.setMaxLength(255);
        organizationName.setName("organizationName");
        organizationName.setFieldLabel("* " + MSGS.accountFormOrgName());
        organizationName.setToolTip(MSGS.accountFormOrgNameTooltip());
        fieldSetOrg.add(organizationName, accountFieldsetFormData);
        // Organization email
        organizationEmail.setAllowBlank(false);
        organizationEmail.setMaxLength(255);
        organizationEmail.setName("organizationEmail");
        organizationEmail.setFieldLabel("* " + MSGS.accountFormOrgEmail());
        organizationEmail.setValidator(new TextFieldValidator(organizationEmail, FieldType.EMAIL));
        organizationEmail.setToolTip(MSGS.accountFormOrgEmailTooltip());
        fieldSetOrg.add(organizationEmail, accountFieldsetFormData);

        // //////////////////////////////////////////
        // Organization Information sub field set
        // //////////////////////////////////////////
        FieldSet organizationSubFieldSet = new FieldSet();
        Listener<BaseEvent> fieldSetListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                sync(true);
            }
        };
        organizationSubFieldSet.addListener(Events.Collapse, fieldSetListener);
        organizationSubFieldSet.addListener(Events.Expand, fieldSetListener);

        organizationSubFieldSet.setHeading(MSGS.accountFormOrgPrimaryContact());
        organizationSubFieldSet.setBorders(false);
        organizationSubFieldSet.setCollapsible(true);
        organizationSubFieldSet.setWidth(540);
        organizationSubFieldSet.setAutoHeight(true);

        FormLayout organizationSubLayout = new FormLayout();
        organizationSubLayout.setLabelWidth(LABEL_WIDTH_FORM - 11);
        organizationSubFieldSet.setLayout(organizationSubLayout);
        // Other organization data

        FormData subFieldsetFormData = new FormData("-11");

        organizationContactName.setName("organizationContactName");
        organizationContactName.setMaxLength(255);
        organizationContactName.setFieldLabel(MSGS.accountFormOrgContactName());
        organizationContactName.setToolTip(MSGS.accountFormOrgContactNameTooltip());
        organizationSubFieldSet.add(organizationContactName, subFieldsetFormData);

        organizationPhoneNumber.setName("organizationPhoneNumber");
        organizationPhoneNumber.setMaxLength(64);
        organizationPhoneNumber.setFieldLabel(MSGS.accountFormOrgPhoneNumber());
        organizationPhoneNumber.setValidator(new TextFieldValidator(organizationPhoneNumber, FieldType.PHONE));
        organizationPhoneNumber.setToolTip(MSGS.accountFormOrgPhoneNumberTooltip());
        organizationSubFieldSet.add(organizationPhoneNumber, subFieldsetFormData);

        organizationAddressLine1.setName("organizationAddressLine1");
        organizationAddressLine1.setMaxLength(255);
        organizationAddressLine1.setFieldLabel(MSGS.accountFormOrgAddress1());
        organizationAddressLine1.setToolTip(MSGS.accountFormOrgAddress1Tooltip());
        organizationSubFieldSet.add(organizationAddressLine1, subFieldsetFormData);

        organizationAddressLine2.setName("organizationAddressLine2");
        organizationAddressLine2.setMaxLength(255);
        organizationAddressLine2.setFieldLabel(MSGS.accountFormOrgAddress2());
        organizationAddressLine2.setToolTip(MSGS.accountFormOrgAddress2Tooltip());
        organizationAddressLine2.setMaxLength(255);
        organizationSubFieldSet.add(organizationAddressLine2, subFieldsetFormData);

        organizationAddressLine3.setName("organizationAddressLine3");
        organizationAddressLine3.setMaxLength(255);
        organizationAddressLine3.setFieldLabel(MSGS.accountFormOrgAddress3());
        organizationAddressLine3.setToolTip(MSGS.accountFormOrgAddress3Tooltip());
        organizationAddressLine3.setMaxLength(255);
        organizationSubFieldSet.add(organizationAddressLine3, subFieldsetFormData);

        organizationZipPostCode.setName("organizationZipPostCode");
        organizationZipPostCode.setMaxLength(255);
        organizationZipPostCode.setFieldLabel(MSGS.accountFormOrgZipPostCode());
        organizationZipPostCode.setToolTip(MSGS.accountFormOrgZipPostCodeTooltip());
        organizationSubFieldSet.add(organizationZipPostCode, subFieldsetFormData);

        organizationCity.setName("organizationCity");
        organizationCity.setMaxLength(255);
        organizationCity.setFieldLabel(MSGS.accountFormOrgCity());
        organizationCity.setToolTip(MSGS.accountFormOrgCityTooltip());
        organizationSubFieldSet.add(organizationCity, subFieldsetFormData);

        organizationStateProvinceCounty.setName("organizationStateProvinceCounty");
        organizationStateProvinceCounty.setMaxLength(255);
        organizationStateProvinceCounty.setFieldLabel(MSGS.accountFormOrgState());
        organizationStateProvinceCounty.setToolTip(MSGS.accountFormOrgStateTooltip());
        organizationSubFieldSet.add(organizationStateProvinceCounty, subFieldsetFormData);

        organizationCountry.setName("organizationCountry");
        organizationCountry.setMaxLength(255);
        organizationCountry.setFieldLabel(MSGS.accountFormOrgCountry());
        organizationCountry.setToolTip(MSGS.accountFormOrgCountryTooltip());
        organizationSubFieldSet.add(organizationCountry, subFieldsetFormData);

        // add the field set and reset
        fieldSetOrg.add(organizationSubFieldSet);
        accountFormPanel.add(fieldSetOrg);

        bodyPanel.add(accountFormPanel);
    }

    public void validateAccount() {
        if (accountNameField.getValue() == null || organizationName.getValue() == null || organizationEmail.getValue() == null) {
            ConsoleInfo.display(CMSGS.error(), CMSGS.allFieldsRequired());
        } else if (!expirationDateField.isValid()) {
            ConsoleInfo.display(CMSGS.error(), KapuaSafeHtmlUtils.htmlUnescape(expirationDateField.getErrorMessage()));
        } else if (!organizationEmail.isValid()) {
            ConsoleInfo.display(CMSGS.error(), organizationEmail.getErrorMessage());
        } else if (!organizationPhoneNumber.isValid()) {
            ConsoleInfo.display(CMSGS.error(), organizationPhoneNumber.getErrorMessage());
        }
    }

    @Override
    protected void preSubmit() {
        validateAccount();
        super.preSubmit();
    }

    @Override
    public void submit() {
        GwtAccountCreator gwtAccountCreator = new GwtAccountCreator();
        gwtAccountCreator.setParentAccountId(currentSession.getSelectedAccountId());
        gwtAccountCreator.setAccountName(accountNameField.getValue());
        gwtAccountCreator.setAccountPassword(accountPassword.getValue());
        gwtAccountCreator.setExpirationDate(expirationDateField.getValue());

        // Organization data
        gwtAccountCreator.setOrganizationName(organizationName.getValue());
        gwtAccountCreator.setOrganizationPersonName(organizationContactName.getValue());
        gwtAccountCreator.setOrganizationEmail(organizationEmail.getValue());
        gwtAccountCreator.setOrganizationPhoneNumber(organizationPhoneNumber.getValue());
        gwtAccountCreator.setOrganizationAddressLine1(organizationAddressLine1.getValue());
        gwtAccountCreator.setOrganizationAddressLine2(organizationAddressLine2.getValue());
        gwtAccountCreator.setOrganizationAddressLine3(organizationAddressLine3.getValue());
        gwtAccountCreator.setOrganizationCity(organizationCity.getValue());
        gwtAccountCreator.setOrganizationZipPostCode(organizationZipPostCode.getValue());
        gwtAccountCreator.setOrganizationStateProvinceCounty(organizationStateProvinceCounty.getValue());
        gwtAccountCreator.setOrganizationCountry(organizationCountry.getValue());
        gwtAccountCreator.setScopeId(currentSession.getSelectedAccountId());
        gwtAccountCreator.setAccountStatus(statusCombo.getSimpleValue().name());

        GWT_ACCOUNT_SERVICE.create(xsrfToken,
                gwtAccountCreator,
                new AsyncCallback<GwtAccount>() {

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
                                if (gwtCause.getCode().equals(GwtKapuaErrorCode.DUPLICATE_NAME)
                                        || gwtCause.getCode().equals(GwtKapuaErrorCode.ENTITY_ALREADY_EXIST_IN_ANOTHER_ACCOUNT)) {
                                    accountNameField.markInvalid(gwtCause.getMessage());
                                } else if (gwtCause.getCode().equals(GwtKapuaErrorCode.ILLEGAL_ARGUMENT)) {
                                    if (gwtCause.getArguments()[0].equals("expirationDate")) {
                                        expirationDateField.markInvalid(MSGS.conflictingExpirationDate());
                                    } else if (gwtCause.getArguments()[0].equals("status")) {
                                        statusCombo.markInvalid(gwtCause.getArguments()[1] != null ? (gwtCause.getArguments()[1]) : "an error occurred");
                                    }
                                }
                            }
                            FailureHandler.handleFormException(formPanel, cause);
                        }
                    }

                    @Override
                    public void onSuccess(GwtAccount account) {
                        exitStatus = true;
                        exitMessage = MSGS.accountCreatedConfirmation();
                        hide();
                    }
                });
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.accountAddDialogHeader();
    }

    @Override
    public String getInfoMessage() {
        return MSGS.accountAddDialogInfoMessage();
    }

}
