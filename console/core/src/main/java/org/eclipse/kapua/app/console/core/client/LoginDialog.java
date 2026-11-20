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
package org.eclipse.kapua.app.console.core.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.core.client.messages.ConsoleCoreMessages;
import org.eclipse.kapua.app.console.core.shared.model.authentication.GwtLoginCredential;
import org.eclipse.kapua.app.console.core.shared.service.GwtAuthorizationService;
import org.eclipse.kapua.app.console.core.shared.service.GwtAuthorizationServiceAsync;
import org.eclipse.kapua.app.console.core.shared.service.GwtSettingsService;
import org.eclipse.kapua.app.console.core.shared.service.GwtSettingsServiceAsync;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.CookieUtils;
import org.eclipse.kapua.app.console.module.api.client.util.model.MfaTrustMachineCookie;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;

/**
 * Login Dialog
 * <p>
 * Multi-step verification - First step: username and password / cookies verification
 */
public class LoginDialog extends Dialog {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);
    private static final ConsoleCoreMessages CORE_MSGS = GWT.create(ConsoleCoreMessages.class);

    private final GwtAuthorizationServiceAsync gwtAuthorizationService = GWT.create(GwtAuthorizationService.class);
    private final GwtSettingsServiceAsync gwtSettingService = GWT.create(GwtSettingsService.class);

    private GwtSession currentSession;
    private TextField<String> username;
    private TextField<String> password;

    private Button reset;
    private Button login;
    private Button ssoLogin;
    private Status status;

    private boolean allowMainScreen;

    private final MfaLoginDialog mfaLoginDialog = new MfaLoginDialog(this);

    public LoginDialog() {
        FormLayout layout = new FormLayout();

        layout.setLabelWidth(90);
        layout.setDefaultWidth(160);
        setLayout(layout);

        setButtonAlign(HorizontalAlignment.LEFT);
        setButtons(""); // don't show OK button
        setIcon(IconHelper.createStyle("user"));
        setModal(false);
        setBodyBorder(true);
        setBodyStyle("padding: 8px;background: none");
        setWidth(300);
        setResizable(false);
        setClosable(false);

        KeyListener keyListener = new KeyListener() {

            @Override
            public void componentKeyUp(ComponentEvent event) {

                validate();
                if (
                        event.getKeyCode() == 13 &&
                                username.getValue() != null &&
                                username.getValue().trim().length() > 0 &&
                                password.getValue() != null &&
                                password.getValue().trim().length() > 0) {
                    onSubmit();
                }
            }
        };

        Listener<BaseEvent> changeListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                validate();
            }
        };

        username = new TextField<String>();
        username.setFieldLabel(CORE_MSGS.loginUsername());
        username.addKeyListener(keyListener);
        username.setAllowBlank(false);
        username.addListener(Events.OnBlur, changeListener);

        add(username);

        password = new TextField<String>();
        password.setPassword(true);
        password.setFieldLabel(CORE_MSGS.loginPassword());
        password.addKeyListener(keyListener);
        password.setAllowBlank(false);
        password.addListener(Events.OnBlur, changeListener);

        add(password);

        gwtSettingService.getOpenIDEnabled(new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
                ConsoleInfo.display(CORE_MSGS.loginSsoEnabledError(), caught.getLocalizedMessage());
            }

            @Override
            public void onSuccess(Boolean result) {
                ssoLogin.setVisible(result);
            }
        });

    }

    public boolean isAllowMainScreen() {
        return this.allowMainScreen;
    }

    public void setAllowMainScreen(boolean main) {
        this.allowMainScreen = main;
    }

    public GwtSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(GwtSession currentSession) {
        this.currentSession = currentSession;
    }

    public TextField<String> getUsername() {
        return username;
    }

    public void setUsername(TextField<String> username) {
        this.username = username;
    }

    public TextField<String> getPassword() {
        return password;
    }

    public void setPassword(TextField<String> password) {
        this.password = password;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    protected void createButtons() {
        super.createButtons();

        status = new Status();
        status.setBusy(MSGS.waitMsg());
        status.hide();
        status.setAutoWidth(true);

        getButtonBar().add(status);
        getButtonBar().add(new FillToolItem());

        reset = new Button(CORE_MSGS.loginReset());
        reset.disable();

        reset.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                username.reset();
                username.enable();
                password.reset();
                password.enable();
                validate();
                username.focus();
            }
        });

        login = new Button(CORE_MSGS.loginLogin());
        login.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                onSubmit();
            }
        });

        ssoLogin = new Button(CORE_MSGS.loginSsoLogin());
        ssoLogin.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                username.clearInvalid();
                doSsoLogin();
            }

        });

        addButton(reset);
        addButton(login);
        addButton(ssoLogin);
    }

    protected void doSsoLogin() {
        gwtSettingService.getOpenIDLoginUri(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ConsoleInfo.display(CORE_MSGS.loginSsoLoginError(), caught.getLocalizedMessage());
            }

            @Override
            public void onSuccess(String result) {
                Window.Location.assign(result);
            }

        });
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        username.focus();
    }

    /**
     * Login submit
     */
    protected void onSubmit() {
        if (username.getValue() == null && password.getValue() == null) {
            ConsoleInfo.display(MSGS.dialogError(), MSGS.usernameAndPasswordRequired());
            password.markInvalid(password.getErrorMessage());
        } else if (username.getValue() == null) {
            ConsoleInfo.display(MSGS.dialogError(), MSGS.usernameFieldRequired());
        } else if (password.getValue() == null) {
            ConsoleInfo.display(MSGS.dialogError(), MSGS.passwordFieldRequired());
            password.markInvalid(password.getErrorMessage());
        } else {

            status.show();
            getButtonBar().disable();
            username.disable();
            password.disable();
            status.show();
            getButtonBar().disable();

            performLogin();
        }

    }

    // Login

    public void performLogin() {
        // Retrieve MFA Trust Machine cookie value if present
        String mfaTrustMachineCookieValue = CookieUtils.readCookie(MfaTrustMachineCookie.COOKIE_NAME_PREFIX + username.getValue());

        // Build credentials
        GwtLoginCredential credentials = new GwtLoginCredential(username.getValue(), password.getValue());
        credentials.setTrustKey(mfaTrustMachineCookieValue);

        // Login
        gwtAuthorizationService.login(credentials, false, new AsyncCallback<GwtSession>() {

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof GwtKapuaException) {
                    GwtKapuaException ex = (GwtKapuaException) caught;

                    // Check if login is failed because of MFA code not provided
                    if (GwtKapuaErrorCode.REQUIRE_MFA_CODE == ex.getCode()) {
                        mfaLoginDialog.show();
                        return;
                    } else {
                        ConsoleInfo.display(CORE_MSGS.loginError(), caught.getLocalizedMessage());
                    }

                    // If login failed for other reasons, remove any stored MFA trust Machine Cookie
                    CookieUtils.removeCookie(MfaTrustMachineCookie.COOKIE_NAME_PREFIX + username.getValue());
                } else {
                    ConsoleInfo.display("Error while performing login", caught.getLocalizedMessage());
                }
                resetDialog();
            }

            @Override
            public void onSuccess(GwtSession gwtSession) {
                currentSession = gwtSession;
                callMainScreen();
                ConsoleInfo.hideInfo();
            }
        });
    }

    public void callMainScreen() {
        setAllowMainScreen(true);
        hide();
    }

    protected boolean hasValue(TextField<String> field) {
        return field.getValue() != null &&
                !field.getValue().trim().isEmpty();
    }

    protected void validate() {
        login.setEnabled(true);
        reset.setEnabled(hasValue(username) &&
                hasValue(password));
        if (hasValue(username)) {
            username.clearInvalid();
        }
        if (hasValue(password)) {
            password.clearInvalid();
        }
    }

    public void resetDialog() {
        username.reset();
        username.enable();
        password.reset();
        password.enable();
        username.focus();
        status.hide();
        getButtonBar().enable();
        reset.disable();
        password.clearInvalid();
    }

}
