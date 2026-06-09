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
package org.eclipse.kapua.app.console.core.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.app.console.core.server.util.ConsoleSsoHelper;
import org.eclipse.kapua.app.console.core.server.util.ConsoleSsoLocator;
import org.eclipse.kapua.app.console.core.shared.model.GwtProductInformation;
import org.eclipse.kapua.app.console.core.shared.service.GwtSettingsService;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSetting;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSettingKeys;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.plugin.sso.openid.OpenIDService;
import org.eclipse.kapua.plugin.sso.openid.SSOData;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.token.LoginInfo;

import java.net.URI;
import java.util.UUID;

public class GwtSettingsServiceImpl extends RemoteServiceServlet implements GwtSettingsService {

    private static final long serialVersionUID = -6876999298300071273L;

    //Injection not supported here, unfortunately
    private static final ConsoleSetting CONSOLE_SETTINGS = ConsoleSetting.getInstance();

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private final AuthenticationService authenticationService = LOCATOR.getService(AuthenticationService.class);

    @Override
    public GwtProductInformation getProductInformation() {
        GwtProductInformation result = new GwtProductInformation();
        result.setBackgroundCredits(CONSOLE_SETTINGS.getString(ConsoleSettingKeys.LOGIN_BACKGROUND_CREDITS, ""));
        result.setInformationSnippet(CONSOLE_SETTINGS.getString(ConsoleSettingKeys.LOGIN_GENERIC_SNIPPET, ""));
        result.setProductName(CONSOLE_SETTINGS.getString(ConsoleSettingKeys.PRODUCT_NAME, ""));
        result.setCopyright(CONSOLE_SETTINGS.getString(ConsoleSettingKeys.PRODUCT_COPYRIGHT, ""));

        return result;
    }

    @Override
    public String getOpenIDLoginUri() throws GwtKapuaException {
        try {
            return getOpenIdService().getLoginUri(UUID.randomUUID().toString(), ConsoleSsoHelper.getRedirectUri());
        } catch (Exception t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    //return "" if this account has no supported direct login
    @Override
    public String getOpenIDLoginOrganizationUri() throws GwtKapuaException {
        try {
            LoginInfo loginInfo = authenticationService.getLoginInfo();
            if (loginInfo == null) {
                throw new KapuaIllegalArgumentException("loginInfo", null);
            }
            SSOData ssoData = loginInfo.getSsoData();
            if (ssoData != null &&
                    ssoData.getAccountSupportsBrokering() &&
                    ssoData.getAccountSupportsDirectLogin() &&
                    ssoData.getUriSuffixDirectLogin() != null) {
                String baseConsoleUrl = ConsoleSsoHelper.getHomeUri();
                return baseConsoleUrl + ssoData.getUriSuffixDirectLogin();
            } else {
                return "";
            }
        } catch (Exception t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public String getOpenIDLogoutUri(String idToken) throws GwtKapuaException {
        try {
            if (CONSOLE_SETTINGS.getBoolean(ConsoleSettingKeys.SSO_OPENID_USER_LOGOUT_ENABLED, true)) {
                if (idToken.isEmpty()) {
                    throw new KapuaIllegalArgumentException("ssoIdToken", idToken);
                }

                return getOpenIdService()
                        .getLogoutUri(
                                idToken,
                                URI.create(ConsoleSsoHelper.getHomeUri()),
                                UUID.randomUUID().toString()
                        );
            }
            return "";  // return empty string instead of using a dedicated callback just to check if the logout is enabled
        } catch (Exception t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public boolean getOpenIDEnabled() {
        return getOpenIdService().isEnabled();
    }

    @Override
    public String getHomeUri() throws GwtKapuaException {
        try {
            return ConsoleSsoHelper.getHomeUri();
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
            return null;
        }
    }

    private OpenIDService getOpenIdService() {
        return ConsoleSsoLocator.getLocator(this).getService();
    }
}
