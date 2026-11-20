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
package org.eclipse.kapua.app.console.module.authentication.client.tabs.credentials;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.util.CookieUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.model.MfaTrustMachineCookie;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtSecurityTokenService;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtSecurityTokenServiceAsync;
import org.eclipse.kapua.app.console.module.authentication.client.messages.ConsoleCredentialMessages;
import org.eclipse.kapua.app.console.module.authentication.shared.model.GwtMfaCredentialOptions;
import org.eclipse.kapua.app.console.module.authentication.shared.service.GwtMfaCredentialOptionsService;
import org.eclipse.kapua.app.console.module.authentication.shared.service.GwtMfaCredentialOptionsServiceAsync;

/**
 * Trust machine - Forget dialog
 */
public class DisableMfaDialog extends Dialog {

    private static final ConsoleCredentialMessages MSGS = GWT.create(ConsoleCredentialMessages.class);

    private final GwtMfaCredentialOptionsServiceAsync gwtMfaCredentialOptionsService = GWT.create(GwtMfaCredentialOptionsService.class);
    private final GwtSecurityTokenServiceAsync securityTokenService = GWT.create(GwtSecurityTokenService.class);

    public DisableMfaDialog(final String username, final String scopeId, final String mfaCredentialOptionsId, final boolean selfManagement) {
        super();

        setHeading(MSGS.disableMfaConfirmationHeader(username));
        setButtons(Dialog.YESNO);
        setModal(true);
        setBodyBorder(true);
        setBodyStyle("padding: 8px; background: none");
        setWidth(300);
        setResizable(false);
        setClosable(false);

        add(new HTML("<br/>"));
        add(new Label(MSGS.disableMfaConfirmation(username)));
        add(new HTML("<br/>"));

        // Events
        getButtonById("yes").addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(final ButtonEvent buttonEvent) {

                gwtMfaCredentialOptionsService.find(scopeId, mfaCredentialOptionsId, selfManagement, new AsyncCallback<GwtMfaCredentialOptions>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        unmask();
                        FailureHandler.handle(caught);
                    }

                    @Override
                    public void onSuccess(GwtMfaCredentialOptions result) {
                        if (result != null) {

                            mask(MSGS.maskDisableMfa());
                            // Ok - Confirm
                            securityTokenService.generateSecurityToken(new AsyncCallback<GwtXSRFToken>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    unmask();

                                    FailureHandler.handle(caught);
                                }

                                @Override
                                public void onSuccess(GwtXSRFToken xsrfToken) {
                                    gwtMfaCredentialOptionsService.delete(xsrfToken, scopeId, mfaCredentialOptionsId, selfManagement, new AsyncCallback<Void>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                            FailureHandler.handle(caught);
                                            unmask();
                                        }

                                        @Override
                                        public void onSuccess(Void result) {
                                            CookieUtils.removeCookie(MfaTrustMachineCookie.COOKIE_NAME_PREFIX + username);

                                            hide(buttonEvent.getButton());
                                            unmask();
                                        }
                                    });
                                }
                            });

                        } else {
                            // This 'else' branch allows handling the case where the MfaOption has already been deisabled with the UserActionMenu,
                            // but the MfaManagementPanel has not been updated. Thus, we throw here a 'dummy' exception.
                            try {
                                throw new GwtKapuaException(GwtKapuaErrorCode.ENTITY_NOT_FOUND, null, "MfaOption", null);
                            } catch (GwtKapuaException e) {
                                unmask();
                                hide(buttonEvent.getButton());
                                FailureHandler.handle(e);
                            }
                        }
                    }
                });

            }
        });

        getButtonById("no").addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                // Cancel
                hide(ce.getButton());
            }
        });
    }
}
