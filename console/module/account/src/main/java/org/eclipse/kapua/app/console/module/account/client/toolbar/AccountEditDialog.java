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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtOrganization;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;

public class AccountEditDialog extends AccountAddDialog {

    GwtAccount selectedAccount;

    public AccountEditDialog(GwtSession currentSession, GwtAccount selectedAccount) {
        super(currentSession);
        this.selectedAccount = selectedAccount;
        DialogUtils.resizeDialog(this, 600, 580);
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        bodyPanel.setAutoHeight(true);
        setClosable(false);
        setScrollMode(Scroll.AUTO);

        fieldSet.remove(accountNameField);
        fieldSet.remove(parentAccountNameLabel);

        accountNameLabel.setVisible(true);
        accountNameLabel.setToolTip(MSGS.accountFormParentAccountNameEditDialogTooltip());

        accountNameLabel.setValue(selectedAccount.getName());
        accountNameField.setValue(selectedAccount.getName());

        expirationDateField.setValue(selectedAccount.getExpirationDate());
        expirationDateField.setOriginalValue(selectedAccount.getExpirationDate());

        organizationName.setValue(selectedAccount.getGwtOrganization().getName());
        organizationName.setOriginalValue(selectedAccount.getGwtOrganization().getName());

        organizationContactName.setValue(selectedAccount.getGwtOrganization().getPersonName());
        organizationContactName.setOriginalValue(selectedAccount.getGwtOrganization().getPersonName());

        organizationEmail.setValue(selectedAccount.getGwtOrganization().getEmailAddress());
        organizationEmail.setOriginalValue(selectedAccount.getGwtOrganization().getEmailAddress());

        organizationPhoneNumber.setValue(selectedAccount.getGwtOrganization().getPhoneNumber());
        organizationPhoneNumber.setOriginalValue(selectedAccount.getGwtOrganization().getPhoneNumber());

        organizationAddressLine1.setValue(selectedAccount.getGwtOrganization().getAddressLine1());
        organizationAddressLine1.setOriginalValue(selectedAccount.getGwtOrganization().getAddressLine1());

        organizationAddressLine2.setValue(selectedAccount.getGwtOrganization().getAddressLine2());
        organizationAddressLine2.setOriginalValue(selectedAccount.getGwtOrganization().getAddressLine2());

        organizationAddressLine3.setValue(selectedAccount.getGwtOrganization().getAddressLine3());
        organizationAddressLine3.setOriginalValue(selectedAccount.getGwtOrganization().getAddressLine3());

        organizationZipPostCode.setValue(selectedAccount.getGwtOrganization().getZipPostCode());
        organizationZipPostCode.setOriginalValue(selectedAccount.getGwtOrganization().getZipPostCode());

        organizationCity.setValue(selectedAccount.getGwtOrganization().getCity());
        organizationCity.setOriginalValue(selectedAccount.getGwtOrganization().getCity());

        organizationStateProvinceCounty.setValue(selectedAccount.getGwtOrganization().getStateProvinceCounty());
        organizationStateProvinceCounty.setOriginalValue(selectedAccount.getGwtOrganization().getStateProvinceCounty());

        organizationCountry.setValue(selectedAccount.getGwtOrganization().getCountry());
        organizationCountry.setOriginalValue(selectedAccount.getGwtOrganization().getCountry());

        statusCombo.setSimpleValue(GwtAccount.GwtAccountStatus.valueOf(selectedAccount.getAccountStatus()));
        //statusCombo.setOriginalValue(GwtAccount.GwtAccountStatus.valueOf(selectedAccount.getAccountStatus()));

        accountClusterLabel.setVisible(true);
        optlock.setValue(selectedAccount.getOptlock());
    }

    @Override
    public void submit() {
        // Organization data
        GwtOrganization gwtOrganization = new GwtOrganization();
        gwtOrganization.setName(organizationName.getValue());
        gwtOrganization.setPersonName(organizationContactName.getValue());
        gwtOrganization.setEmailAddress(organizationEmail.getValue());
        gwtOrganization.setPhoneNumber(organizationPhoneNumber.getValue());
        gwtOrganization.setAddressLine1(organizationAddressLine1.getValue());
        gwtOrganization.setAddressLine2(organizationAddressLine2.getValue());
        gwtOrganization.setAddressLine3(organizationAddressLine3.getValue());
        gwtOrganization.setZipPostCode(organizationZipPostCode.getValue());
        gwtOrganization.setCity(organizationCity.getValue());
        gwtOrganization.setStateProvinceCounty(organizationStateProvinceCounty.getValue());
        gwtOrganization.setCountry(organizationCountry.getValue());
        selectedAccount.setGwtOrganization(gwtOrganization);
        selectedAccount.setExpirationDate(expirationDateField.getValue());
        selectedAccount.setAccountStatus(statusCombo.getSimpleValue().name());

        GWT_ACCOUNT_SERVICE.update(xsrfToken,
                selectedAccount,
                new AsyncCallback<GwtAccount>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        exitStatus = false;
                        status.hide();
                        formPanel.getButtonBar().enable();
                        unmask();
                        submitButton.enable();
                        cancelButton.enable();
                        if (!isPermissionErrorMessage(caught)) {
                            if (caught instanceof GwtKapuaException) {
                                GwtKapuaException gwtCause = (GwtKapuaException) caught;
                                switch (gwtCause.getCode()) {
                                case DUPLICATE_NAME:
                                    accountNameField.markInvalid(gwtCause.getMessage());
                                    break;
                                case ILLEGAL_ARGUMENT:
                                    if (gwtCause.getArguments()[0].equals("expirationDate")) {
                                        expirationDateField.markInvalid(MSGS.conflictingExpirationDate());
                                        ConsoleInfo.display("Error", MSGS.conflictingExpirationDate());
                                    } else if (gwtCause.getArguments()[0].equals("notAllowedExpirationDate")) {
                                        expirationDateField.markInvalid(MSGS.notAllowedExpirationDate());
                                        ConsoleInfo.display("Error", MSGS.notAllowedExpirationDate());
                                    } else if (gwtCause.getArguments()[0].equals("status")) {
                                        statusCombo.markInvalid(gwtCause.getArguments()[1] != null ? (gwtCause.getArguments()[1]) : "an error occurred");
                                    }
                                    break;
                                default:
                                    break;
                                }
                            }
                            FailureHandler.handleFormException(formPanel, caught);
                        }
                    }

                    @Override
                    public void onSuccess(GwtAccount account) {
                        ConsoleInfo.display(MSGS.info(), MSGS.accountUpdatedConfirmation());
                        hide();
                    }
                });
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.accountEditDialogHeader(selectedAccount.getName());
    }

    @Override
    public String getInfoMessage() {
        return MSGS.accountEditInfoMessage();
    }
}
