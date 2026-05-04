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
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;

/**
 * Login Dialog
 * <p>
 * Multi-step verification - First step: username and password / cookies verification
 */
public class LoginDialog extends Dialog {

    private static final ConsoleMessages CONSOLE_MESSAGES = GWT.create(ConsoleMessages.class);
    private static final ConsoleCoreMessages CONSOLE_CORE_MESSAGES = GWT.create(ConsoleCoreMessages.class);

    public static final String ACCOUNT_ID_PARAM = "accountid";

    private static final GwtAuthorizationServiceAsync GWT_AUTHORIZATION_SERVICE = GWT.create(GwtAuthorizationService.class);
    private static final GwtSettingsServiceAsync GWT_SETTINGS_SERVICE = GWT.create(GwtSettingsService.class);

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
        username.setFieldLabel(CONSOLE_CORE_MESSAGES.loginUsername());
        username.addKeyListener(keyListener);
        username.setAllowBlank(false);
        username.addListener(Events.OnBlur, changeListener);

        add(username);

        password = new TextField<String>();
        password.setPassword(true);
        password.setFieldLabel(CONSOLE_CORE_MESSAGES.loginPassword());
        password.addKeyListener(keyListener);
        password.setAllowBlank(false);
        password.addListener(Events.OnBlur, changeListener);

        add(password);

        GWT_SETTINGS_SERVICE.getOpenIDEnabled(new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
                ConsoleInfo.display(CONSOLE_CORE_MESSAGES.loginSsoEnabledError(), caught.getLocalizedMessage());
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
        status.setBusy(CONSOLE_MESSAGES.waitMsg());
        status.hide();
        status.setAutoWidth(true);

        getButtonBar().add(status);
        getButtonBar().add(new FillToolItem());

        reset = new Button(CONSOLE_CORE_MESSAGES.loginReset());
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

        login = new Button(CONSOLE_CORE_MESSAGES.loginLogin());
        login.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                onSubmit();
            }
        });

        ssoLogin = new Button(CONSOLE_CORE_MESSAGES.loginSsoLogin());
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
        final String accountId = Window.Location.getParameter(ACCOUNT_ID_PARAM);

        GWT_SETTINGS_SERVICE.getOpenIDLoginUri(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ConsoleInfo.display(CONSOLE_CORE_MESSAGES.loginSsoLoginError(), caught.getLocalizedMessage());
            }

            @Override
            public void onSuccess(String result) {
                if (accountId != null && !accountId.isEmpty()) {
                    result = result + (result.contains("?") ? "&" : "?") + ACCOUNT_ID_PARAM + "=" + accountId;
                }
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
            ConsoleInfo.display(CONSOLE_MESSAGES.dialogError(), CONSOLE_MESSAGES.usernameAndPasswordRequired());
            password.markInvalid(password.getErrorMessage());
        } else if (username.getValue() == null) {
            ConsoleInfo.display(CONSOLE_MESSAGES.dialogError(), CONSOLE_MESSAGES.usernameFieldRequired());
        } else if (password.getValue() == null) {
            ConsoleInfo.display(CONSOLE_MESSAGES.dialogError(), CONSOLE_MESSAGES.passwordFieldRequired());
            password.markInvalid(password.getErrorMessage());
        } else {

            status.show();
            getButtonBar().disable();
            username.disable();
            password.disable();

            // Open the MFA if needed
            // trust cookie test
            boolean existTrustCookie = CookieUtils.isCookieEnabled(CookieUtils.KAPUA_COOKIE_TRUST + username.getValue());
            status.show();
            getButtonBar().disable();

            CookieUtils cookie = new CookieUtils(username.getValue());
            String trustKey = cookie.getTrustKeyCookie();
            performLogin(cookie != null ? cookie.getTrustKeyCookie() : null);
        }

    }

    // Login

    public void performLogin(String trustKey) {

        GwtLoginCredential credentials = new GwtLoginCredential(username.getValue(), password.getValue());
        credentials.setTrustKey(trustKey);

        GWT_AUTHORIZATION_SERVICE.login(credentials, false, new AsyncCallback<GwtSession>() {

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof GwtKapuaException) {
                    GwtKapuaException ex = (GwtKapuaException) caught;
                    if (GwtKapuaErrorCode.REQUIRE_MFA_CODE == ex.getCode()) {
                        mfaLoginDialog.show();
                        return;
                    } else {
                        ConsoleInfo.display(CONSOLE_CORE_MESSAGES.loginError(), caught.getLocalizedMessage());
                    }
                    CookieUtils.removeCookie(CookieUtils.KAPUA_COOKIE_TRUST + username.getValue());
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

    public void performLogout() {
        GWT_AUTHORIZATION_SERVICE.logout(new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                FailureHandler.handle(caught);
            }

            @Override
            public void onSuccess(Void arg0) {
                ConsoleInfo.display(CONSOLE_MESSAGES.popupInfo(), CONSOLE_MESSAGES.loggedOut());
                resetDialog();
                show();
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
