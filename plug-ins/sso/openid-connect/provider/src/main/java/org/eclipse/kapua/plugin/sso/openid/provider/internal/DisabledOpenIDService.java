/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.plugin.sso.openid.provider.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.config.metatype.EmptyTocd;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.plugin.sso.openid.OpenIDService;
import org.eclipse.kapua.plugin.sso.openid.SSOData;

import javax.json.JsonObject;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class DisabledOpenIDService implements OpenIDService {

    public static final String DISABLED_ID = "disabled";
    private final KapuaTocd emptyTocd = new EmptyTocd(OpenIDService.class.getName(), OpenIDService.class.getSimpleName());

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getLoginUri(final String state, final URI redirectUri) {
        return null;
    }

    @Override
    public String getLogoutUri(String idTokenHint, URI postLogoutRedirectUri, String state) {
        return null;
    }

    @Override
    public JsonObject getTokens(final String authCode, final URI redirectUri) {
        return null;
    }

    @Override
    public JsonObject getUserInfo(String authCode) {
        return null;
    }

    @Override
    public String getId() {
        return DISABLED_ID;
    }

    @Override
    public SSOData retrieveSSODataForAccount(KapuaId accountId) throws KapuaException {
        return null;
    }

    @Override
    public KapuaTocd getConfigMetadata(KapuaId scopeId) throws KapuaException {
        return emptyTocd;
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId) throws KapuaException {
        return Collections.emptyMap();
    }

    @Override
    public void setConfigValues(KapuaId scopeId, KapuaId parentId, Map<String, Object> values) throws KapuaException {
        throw new UnsupportedOperationException();
    }
}
