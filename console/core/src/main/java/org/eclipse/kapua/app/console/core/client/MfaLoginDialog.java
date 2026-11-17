/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.core.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import org.eclipse.kapua.app.console.core.client.messages.ConsoleCoreMessages;
import org.eclipse.kapua.app.console.core.shared.model.authentication.GwtLoginCredential;
import org.eclipse.kapua.app.console.core.shared.service.GwtAuthorizationService;
import org.eclipse.kapua.app.console.core.shared.service.GwtAuthorizationServiceAsync;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.CookieUtils;
import org.eclipse.kapua.app.console.module.api.client.util.model.MfaTrustMachineCookie;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authentication.client.messages.ConsoleCredentialMessages;

import java.util.Date;

/**
 * Multi Factor Authentication - Login second step: MFA code and trust this machine request
 */
public class MfaLoginDialog extends Dialog {

    private static final ConsoleMessages CONSOLE_MESSAGES = GWT.create(ConsoleMessages.class);
    private static final ConsoleCoreMessages CONSOLE_CORE_MESSAGES = GWT.create(ConsoleCoreMessages.class);
    private static final ConsoleCredentialMessages CONSOLE_CREDENTIAL_MESSAGES = GWT.create(ConsoleCredentialMessages.class);

    private static final GwtAuthorizationServiceAsync GWT_AUTHORIZATION_SERVICE = GWT.create(GwtAuthorizationService.class);

    private static final String BR = "<br/>";

    protected TextField<String> code;
    final CheckBox trustCheckbox;

    protected Button back;
    protected Button submit;
    protected Status status;

    private LoginDialog loginDialog;

    public MfaLoginDialog(LoginDialog loginDialog) {
        this.loginDialog = loginDialog;

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(90);
        layout.setDefaultWidth(160);
        setLayout(layout);

        setButtonAlign(HorizontalAlignment.LEFT);
        setButtons(""); // don't show OK button
        setIcon(IconHelper.createStyle("user"));

        setHeading(CONSOLE_CREDENTIAL_MESSAGES.mfaLoginTitle());

        setModal(true);
        setBodyBorder(true);
        setBodyStyle("padding: 8px;background: none");
        setWidth(300);
        setResizable(false);
        setClosable(false);

        KeyListener keyListener = new KeyListener() {

            @Override
            public void componentKeyUp(ComponentEvent event) {
                validate();
                if (event.getKeyCode() == 13) {
                    if (code.getValue() != null && code.getValue().trim().length() > 0) {
                        onSubmit();
                    }
                }
            }
        };

        Label title = new Label(CONSOLE_MESSAGES.loginDialogMfaTitle());
        title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        add(title);

        // MFA presentation text
        add(new HTML(BR));
        add(new Label(CONSOLE_MESSAGES.loginMfa()));
        add(new HTML(BR));
        add(new Label(CONSOLE_MESSAGES.loginMfa1()));
        add(new HTML(BR));

        // MFA code
        code = new TextField<String>();
        code.setFieldLabel(CONSOLE_MESSAGES.loginCode());
        code.addKeyListener(keyListener);
        add(code);

        // checkbox
        trustCheckbox = new CheckBox();
        trustCheckbox.setName("Trustpc");
        trustCheckbox.setBoxLabel(CONSOLE_MESSAGES.loginDialogMfaBoxLabel());
        trustCheckbox.setLabelSeparator("");
        trustCheckbox.setToolTip(CONSOLE_MESSAGES.userFormLockedTooltip());
        add(trustCheckbox);

        if (this.isVisible()) {
            setFocusWidget(code);
        }
    }

    protected void onSubmit() {
        status.show();
        getButtonBar().disable();
        trustCheckbox.disable();
        code.disable();
        GwtLoginCredential credentials = new GwtLoginCredential(loginDialog.getUsername().getValue(), loginDialog.getPassword().getValue());
        credentials.setAuthenticationCode(code.getValue());

        GWT_AUTHORIZATION_SERVICE.login(credentials, trustCheckbox.getValue(), new AsyncCallback<GwtSession>() {

            @Override
            public void onFailure(Throwable caught) {
                ConsoleInfo.display(CONSOLE_CORE_MESSAGES.loginError(), caught.getLocalizedMessage());
                reset();

                // Go back
                MfaLoginDialog.this.hide();

                loginDialog.resetDialog();

                loginDialog.show();
            }

            @Override
            public void onSuccess(final GwtSession gwtSession) {
                loginDialog.setCurrentSession(gwtSession);

                // If trust is checked we set the cookie for the first time
                if (trustCheckbox.getValue()) {
                    String username = gwtSession.getUserName();
                    String mfaTrustKey = gwtSession.getMfaTrustKey();
                    Date mfaTrustKeyExpirationDate = gwtSession.getMfaTrustKeyExpirationDate();

                    MfaTrustMachineCookie mfaTrustMachineCookie = new MfaTrustMachineCookie(
                            username,
                            mfaTrustKey,
                            mfaTrustKeyExpirationDate
                    );

                    CookieUtils.addCookie(mfaTrustMachineCookie);
                }

                // Return to main login
                MfaLoginDialog.this.hide();
                loginDialog.callMainScreen();
            }
        });
    }

    @Override
    protected void createButtons() {
        super.createButtons();

        status = new Status();
        status.setBusy(CONSOLE_MESSAGES.waitMsg());
        status.hide();
        status.setAutoWidth(true);

        getButtonBar().add(status);
        getButtonBar().add(new FillToolItem());

        // submit
        submit = new Button(CONSOLE_CORE_MESSAGES.loginLogin());
        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                onSubmit();
            }
        });

        // back
        back = new Button("Back");
        back.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                status.hide();

                reset();
                MfaLoginDialog.this.hide();

                loginDialog.getUsername().focus();
                loginDialog.getStatus().hide();
                loginDialog.getButtonBar().enable();

                loginDialog.getUsername().enable();
                loginDialog.getPassword().enable();

                loginDialog.show();
            }
        });

        // add the buttons
        addButton(back);
        addButton(submit);
    }

    protected boolean hasWellFormedCode(TextField<String> field) {
        return field.getValue() != null && field.getValue().length() > 0;
    }

    protected void validate() {
        submit.setEnabled(hasWellFormedCode(code));
    }

    private void reset() {
        code.enable();
        code.reset();
        code.focus();

        trustCheckbox.enable();
        trustCheckbox.reset();
        status.hide();
        getButtonBar().enable();

        validate();
    }

}
