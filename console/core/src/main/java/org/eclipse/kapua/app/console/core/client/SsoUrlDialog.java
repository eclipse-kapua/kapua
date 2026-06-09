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
package org.eclipse.kapua.app.console.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.core.client.messages.ConsoleCoreMessages;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.InfoDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;

/**
 * Read-only dialog that displays an SSO login URL to the user.
 *
 * @since 2.0.0
 */
public class SsoUrlDialog extends InfoDialog {

    private static final ConsoleCoreMessages CONSOLE_MSGS = GWT.create(ConsoleCoreMessages.class);

    /**
     * Constructor.
     *
     * @param ssoUrl the SSO login URL to display.
     * @since 2.0.0
     */
    public SsoUrlDialog(String ssoUrl) {
        super(
                CONSOLE_MSGS.ssoUrlDialogHeader(),
                new KapuaIcon(IconSet.SIGN_IN),
                ssoUrl.isEmpty() ? CONSOLE_MSGS.ssoUrlMissingDialogMessage() : new SafeHtmlBuilder().appendEscapedLines(CONSOLE_MSGS.ssoUrlDialogMessage(ssoUrl)).toSafeHtml().asString()
        );
        setStyleAttribute("background-color", "#F0F0F0");
        setBodyStyle("background-color: #F0F0F0");
        DialogUtils.resizeDialog(this, 400, 220);
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
    }
}

