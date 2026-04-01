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
package org.eclipse.kapua.app.console.module.device.client.device.packages.dialog;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.TabbedDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaNumberField;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaTextField;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.CssLiterals;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator;
import org.eclipse.kapua.app.console.module.api.client.util.validator.TextFieldValidator.FieldType;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleDeviceMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.management.packages.GwtFileType;
import org.eclipse.kapua.app.console.module.device.shared.model.management.packages.GwtPackageInstallRequest;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceManagementService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceManagementServiceAsync;

public class PackageInstallDialog extends TabbedDialog {

    private static final ConsoleDeviceMessages DEVICE_MSGS = GWT.create(ConsoleDeviceMessages.class);
    private static final GwtDeviceManagementServiceAsync GWT_DEVICE_MANAGEMENT_SERVICE = GWT.create(GwtDeviceManagementService.class);

    private String scopeId;
    private String deviceId;

    private TabItem packageInfoTab;
    private TabItem operationOptionsTab;
    private TabItem advancedOptionsTab;

    private FormPanel packageInfoForm;
    private Text dpInfoText;
    private KapuaTextField<String> dpURIField;
    private KapuaTextField<String> dpNameField;
    private KapuaTextField<String> dpVersionField;

    private SimpleComboBox<GwtFileType> dpFileTypeField;
    private KapuaTextField<String> dpFileHash;
    private KapuaTextField<String> usernameField;
    private KapuaTextField<String> passwordField;

    private FormPanel operationOptionsForm;
    private Text operationInfoText;
    private CheckBox operationRebootField;
    private CheckBox installAfterDownloadField;
    private KapuaNumberField operationRebootDelayField;

    private FormPanel advancedOptionsForm;
    private Text advancedInfoText;
    private CheckBox operationRestartField;
    private KapuaNumberField blockSizeField;
    private KapuaNumberField blockDelayField;
    private KapuaNumberField blockTimeoutField;
    private KapuaNumberField notifyBlockSizeField;
    private KapuaTextField<String> installVerifierURIField;


    public PackageInstallDialog(String scopeId, String deviceId) {
        super();

        this.scopeId = scopeId;
        this.deviceId = deviceId;

        DialogUtils.resizeDialog(this, 490, 350);
    }

    @Override
    public String getInfoMessage() {
        return null;
    }

    @Override
    public void createTabItems() {
        // Deployment package info tab content
        {
            submitButton.disable();
            FormData formData = new FormData("-15");

            FormLayout formLayout = new FormLayout();
            formLayout.setLabelWidth(FORM_LABEL_WIDTH);

            packageInfoForm = new FormPanel();
            packageInfoForm.setFrame(false);
            packageInfoForm.setHeaderVisible(false);
            packageInfoForm.setBodyBorder(false);
            packageInfoForm.setBorders(false);
            packageInfoForm.setLayout(formLayout);

            dpInfoText = new Text();
            dpInfoText.setText(DEVICE_MSGS.packageInstallDpDialogTabInfo());
            dpInfoText.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "10px");
            packageInfoForm.add(dpInfoText);

            dpURIField = new KapuaTextField<String>();
            dpURIField.setMaxLength(2048);
            dpURIField.setName("dpUri");
            dpURIField.setAllowBlank(false);
            dpURIField.setValidator(new TextFieldValidator(dpURIField, FieldType.URL));
            dpURIField.setFieldLabel("* " + DEVICE_MSGS.packageInstallDpDialogUri());
            dpURIField.setToolTip(DEVICE_MSGS.devicePackagesUrlTooltip());
            packageInfoForm.add(dpURIField, formData);

            dpNameField = new KapuaTextField<String>();
            dpNameField.setMaxLength(256);
            dpNameField.setName("dpName");
            dpNameField.setAllowBlank(false);
            dpNameField.setFieldLabel("* " + DEVICE_MSGS.packageInstallDpDialogName());
            dpNameField.setToolTip(DEVICE_MSGS.devicePackageNameTooltip());
            packageInfoForm.add(dpNameField, formData);

            dpVersionField = new KapuaTextField<String>();
            dpVersionField.setMaxLength(256);
            dpVersionField.setName("dpVersion");
            dpVersionField.setValidator(new TextFieldValidator(dpVersionField, FieldType.PACKAGE_VERSION));
            dpVersionField.setAllowBlank(false);
            dpVersionField.setFieldLabel("* " + DEVICE_MSGS.packageInstallDpDialogVersion());
            dpVersionField.setToolTip(DEVICE_MSGS.packageInstallDpDialogVersionTooltip());
            packageInfoForm.add(dpVersionField, formData);

            dpFileTypeField = new SimpleComboBox<GwtFileType>();
            dpFileTypeField.setName("fileType");
            dpFileTypeField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogFileType());
            dpFileTypeField.setToolTip(DEVICE_MSGS.packageInstallDpDialogFileTypeTooltip());
            dpFileTypeField.setLabelSeparator(":");
            dpFileTypeField.setEditable(false);
            dpFileTypeField.setTypeAhead(true);
            dpFileTypeField.setTriggerAction(ComboBox.TriggerAction.ALL);

            dpFileTypeField.add(GwtFileType.DEPLOYMENT_PACKAGE);
            dpFileTypeField.add(GwtFileType.EXECUTABLE_SCRIPT);
            dpFileTypeField.setSimpleValue(GwtFileType.DEPLOYMENT_PACKAGE);
            packageInfoForm.add(dpFileTypeField, formData);

            dpFileHash = new KapuaTextField<String>();
            dpFileHash.setMaxLength(125);
            dpFileHash.setName("fileHash");
            dpFileHash.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogFileHash());
            dpFileHash.setToolTip(DEVICE_MSGS.packageInstallDpDialogFileHashTooltip());
            packageInfoForm.add(dpFileHash, formData);

            usernameField = new KapuaTextField<String>();
            usernameField.setName("username");
            usernameField.setMaxLength(256);
            usernameField.setAllowBlank(true);
            usernameField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogUsername());
            usernameField.setToolTip(DEVICE_MSGS.packageInstallDpDialogUsernameTooltip());
            packageInfoForm.add(usernameField, formData);

            passwordField = new KapuaTextField<String>();
            passwordField.setName("password");
            passwordField.setAllowBlank(true);
            passwordField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogPassword());
            passwordField.setToolTip(DEVICE_MSGS.packageInstallDpDialogPasswordTooltip());
            passwordField.setPassword(true);
            passwordField.setMaxLength(255);
            packageInfoForm.add(passwordField, formData);
        }

        // Operation options tab content
        {
            FormData formData = new FormData("-10");

            FormLayout formLayout = new FormLayout();
            formLayout.setLabelWidth(FORM_LABEL_WIDTH);

            operationOptionsForm = new FormPanel();
            operationOptionsForm.setFrame(false);
            operationOptionsForm.setHeaderVisible(false);
            operationOptionsForm.setBodyBorder(false);
            operationOptionsForm.setBorders(false);
            operationOptionsForm.setLayout(formLayout);

            operationInfoText = new Text();
            operationInfoText.setText(DEVICE_MSGS.packageInstallDpDialogOperationTabInfo());
            operationInfoText.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "5px");
            operationOptionsForm.add(operationInfoText);

            operationRebootField = new CheckBox();
            operationRebootField.setName("reboot");
            operationRebootField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogOperationReboot());
            operationRebootField.setToolTip(DEVICE_MSGS.packageInstallDpDialogOperationRebootTooltip());
            operationRebootField.setBoxLabel("");
            operationRebootField.setValue(Boolean.FALSE);

            operationRebootField.addListener(Events.Change, new Listener<BaseEvent>() {

                @Override
                public void handleEvent(BaseEvent be) {
                    if (operationRebootField.getValue()) {
                        operationRebootDelayField.enable();
                    } else {
                        operationRebootDelayField.clear();
                        operationRebootDelayField.disable();
                    }
                }
            });

            installAfterDownloadField = new CheckBox();
            installAfterDownloadField.setName("install");
            installAfterDownloadField.setFieldLabel(DEVICE_MSGS.packageInstallAfterDownload());
            installAfterDownloadField.setToolTip(DEVICE_MSGS.packageInstallAfterDownloadTip());
            installAfterDownloadField.setBoxLabel("");
            installAfterDownloadField.setValue(Boolean.TRUE);

            operationOptionsForm.add(installAfterDownloadField, formData);
            operationOptionsForm.add(operationRebootField, formData);

            operationRebootDelayField = new KapuaNumberField();
            operationRebootDelayField.setName("installRebootDelay");
            operationRebootDelayField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogOperationRebootDelay());
            operationRebootDelayField.setToolTip(DEVICE_MSGS.packageInstallDpDialogOperationRebootDelayTooltip());
            operationRebootDelayField.setEmptyText("0");
            operationRebootDelayField.setAllowDecimals(false);
            operationRebootDelayField.setAllowNegative(false);
            operationRebootDelayField.disable();
            operationRebootDelayField.setMaxLength(5);
            operationRebootDelayField.setMaxValue(65535);
            operationRebootDelayField.setPropertyEditorType(Integer.class);
            operationOptionsForm.add(operationRebootDelayField, formData);
        }

        // Advanced options tab content
        {
            FormData formData = new FormData("-10");

            FormLayout formLayout = new FormLayout();
            formLayout.setLabelWidth(FORM_LABEL_WIDTH);

            advancedOptionsForm = new FormPanel();
            advancedOptionsForm.setFrame(false);
            advancedOptionsForm.setHeaderVisible(false);
            advancedOptionsForm.setBodyBorder(false);
            advancedOptionsForm.setBorders(false);
            advancedOptionsForm.setLayout(formLayout);

            advancedInfoText = new Text();
            advancedInfoText.setText(DEVICE_MSGS.packageInstallDpDialogAdvancedTabInfo());
            advancedInfoText.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "5px");
            advancedOptionsForm.add(advancedInfoText);

            operationRestartField = new CheckBox();
            operationRestartField.setName("restart");
            operationRestartField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogOperationRestart());
            operationRestartField.setToolTip(DEVICE_MSGS.packageInstallDpDialogOperationRestartTooltip());
            operationRestartField.setBoxLabel("");
            operationRestartField.setValue(Boolean.FALSE);
            advancedOptionsForm.add(operationRestartField, formData);

            blockSizeField = new KapuaNumberField();
            blockSizeField.setName("installBlockSize");
            blockSizeField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogAdvancedBlockSize());
            blockSizeField.setToolTip(DEVICE_MSGS.packageInstallDpDialogAdvancedBlockSizeTooltip());
            blockSizeField.setAllowDecimals(false);
            blockSizeField.setAllowNegative(false);
            blockSizeField.setMaxLength(12);
            blockSizeField.setMaxValue(2147483646);
            blockSizeField.setPropertyEditorType(Integer.class);
            advancedOptionsForm.add(blockSizeField, formData);

            blockDelayField = new KapuaNumberField();
            blockDelayField.setName("installBlockDelay");
            blockDelayField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogAdvancedBlockDelay());
            blockDelayField.setToolTip(DEVICE_MSGS.packageInstallDpDialogAdvancedBlockDelayTooltip());
            blockDelayField.setAllowDecimals(false);
            blockDelayField.setAllowNegative(false);
            blockDelayField.setMaxLength(12);
            blockDelayField.setMaxValue(2147483646);
            blockDelayField.setPropertyEditorType(Integer.class);
            advancedOptionsForm.add(blockDelayField, formData);

            blockTimeoutField = new KapuaNumberField();
            blockTimeoutField.setName("installBlockTimeout");
            blockTimeoutField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogAdvancedBlockTimeout());
            blockTimeoutField.setToolTip(DEVICE_MSGS.packageInstallDpDialogAdvancedBlockTimeoutTooltip());
            blockTimeoutField.setAllowDecimals(false);
            blockTimeoutField.setAllowNegative(false);
            blockTimeoutField.setMaxLength(12);
            blockTimeoutField.setMaxValue(2147483646);
            blockTimeoutField.setPropertyEditorType(Integer.class);
            advancedOptionsForm.add(blockTimeoutField, formData);

            notifyBlockSizeField = new KapuaNumberField();
            notifyBlockSizeField.setName("installNotifyBlockSize");
            notifyBlockSizeField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogAdvancedNotifyBlockSize());
            notifyBlockSizeField.setToolTip(DEVICE_MSGS.packageInstallDpDialogAdvancedNotifyBlockSizeTooltip());
            notifyBlockSizeField.setAllowDecimals(false);
            notifyBlockSizeField.setAllowNegative(false);
            notifyBlockSizeField.setMaxLength(12);
            notifyBlockSizeField.setMaxValue(2147483646);
            notifyBlockSizeField.setPropertyEditorType(Integer.class);
            advancedOptionsForm.add(notifyBlockSizeField, formData);

            installVerifierURIField = new KapuaTextField<String>();
            installVerifierURIField.setMaxLength(125);
            installVerifierURIField.setName("installVerifiedURI");
            installVerifierURIField.setAllowBlank(true);
            installVerifierURIField.setValidator(new TextFieldValidator(installVerifierURIField, FieldType.URL));
            installVerifierURIField.setFieldLabel(DEVICE_MSGS.packageInstallDpDialogAdvancedInstallVerifierURI());
            installVerifierURIField.setToolTip(DEVICE_MSGS.packageInstallDpDialogAdvancedInstallVerifierURITooltip());
            advancedOptionsForm.add(installVerifierURIField, formData);
        }
        // Tabs setup
        packageInfoTab = new TabItem(DEVICE_MSGS.packageInstallDpDialogTabTitle());
        packageInfoTab.setBorders(false);
        packageInfoTab.setLayout(new FormLayout());
        packageInfoTab.add(packageInfoForm);
        tabsPanel.add(packageInfoTab);

        operationOptionsTab = new TabItem(DEVICE_MSGS.packageInstallDpDialogOperationTabTitle());
        operationOptionsTab.setBorders(false);
        operationOptionsTab.setLayout(new FormLayout());
        operationOptionsTab.add(operationOptionsForm);
        tabsPanel.add(operationOptionsTab);

        advancedOptionsTab = new TabItem(DEVICE_MSGS.packageInstallDpDialogAdvancedTabTitle());
        advancedOptionsTab.setBorders(false);
        advancedOptionsTab.setLayout(new FormLayout());
        advancedOptionsTab.add(advancedOptionsForm);
        tabsPanel.add(advancedOptionsTab);
    }

    @Override
    public void preSubmit() {
        validateFields();
        if (!packageInfoForm.isValid()) {
            tabsPanel.setSelection(packageInfoTab);
            return;
        }

        if (!operationOptionsForm.isValid()) {
            tabsPanel.setSelection(operationOptionsTab);
            return;
        }

        super.preSubmit();
    }

    @Override
    public void submit() {
        GwtPackageInstallRequest gwtPackageInstallRequest = new GwtPackageInstallRequest();

        // General info
        gwtPackageInstallRequest.setScopeId(scopeId);
        gwtPackageInstallRequest.setDeviceId(deviceId);

        // Base info
        gwtPackageInstallRequest.setPackageURI(dpURIField.getValue());
        gwtPackageInstallRequest.setPackageName(dpNameField.getValue());
        gwtPackageInstallRequest.setPackageVersion(dpVersionField.getValue());
        gwtPackageInstallRequest.setFileType(dpFileTypeField.getSimpleValue());
        gwtPackageInstallRequest.setFileHash(dpFileHash.getValue());
        gwtPackageInstallRequest.setUsername(usernameField.getValue());
        gwtPackageInstallRequest.setPassword(passwordField.getValue());

        // Options info
        gwtPackageInstallRequest.setReboot(operationRebootField.getValue());

        gwtPackageInstallRequest.setInstall(installAfterDownloadField.getValue());

        Number nValue = operationRebootDelayField.getValue();
        if (nValue != null) {
            gwtPackageInstallRequest.setRebootDelay(nValue.intValue() * 1000);
        }

        // Advanced info
        gwtPackageInstallRequest.setRestart(operationRestartField.getValue());

        nValue = blockSizeField.getValue();
        if (nValue != null) {
            gwtPackageInstallRequest.setBlockSize(nValue.intValue());
        }

        nValue = blockDelayField.getValue();
        if (nValue != null) {
            gwtPackageInstallRequest.setBlockDelay(nValue.intValue());
        }

        nValue = blockTimeoutField.getValue();
        if (nValue != null) {
            gwtPackageInstallRequest.setBlockTimeout(nValue.intValue());
        }

        nValue = notifyBlockSizeField.getValue();
        if (nValue != null) {
            gwtPackageInstallRequest.setNotifyBlockSize(nValue.intValue());
        }

        gwtPackageInstallRequest.setInstallVerifierURI(installVerifierURIField.getValue());

        GWT_DEVICE_MANAGEMENT_SERVICE.installPackage(xsrfToken, gwtPackageInstallRequest, new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                exitStatus = true;
                exitMessage = DEVICE_MSGS.packageInstallSuccess();
                hide();
            }

            @Override
            public void onFailure(Throwable caught) {
                exitStatus = false;
                status.hide();
                unmask();
                submitButton.enable();
                cancelButton.enable();
                if (!isPermissionErrorMessage(caught)) {
                    FailureHandler.handle(caught);
                }
            }
        });
    }

    @Override
    protected void addListeners() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getHeaderMessage() {
        return DEVICE_MSGS.packageInstallNewPackage();
    }

    @Override
    public KapuaIcon getInfoIcon() {
        return null;
    }

    public void validateFields() {
        if (!dpURIField.isValid()) {
            ConsoleInfo.display("Error", dpURIField.getErrorMessage());
        } else if (!dpVersionField.isValid()) {
            ConsoleInfo.display("Error", dpVersionField.getErrorMessage());
        }
    }
}
