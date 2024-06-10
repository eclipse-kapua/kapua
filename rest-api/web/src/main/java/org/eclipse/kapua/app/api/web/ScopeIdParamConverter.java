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
package org.eclipse.kapua.app.api.web;

import java.math.BigInteger;
import java.util.Base64;

import javax.ws.rs.ext.ParamConverter;

import org.eclipse.kapua.app.api.core.exception.SessionNotPopulatedException;
import org.eclipse.kapua.app.api.core.settings.KapuaApiCoreSetting;
import org.eclipse.kapua.app.api.core.settings.KapuaApiCoreSettingKeys;
import org.eclipse.kapua.commons.rest.model.ScopeId;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;

public class ScopeIdParamConverter implements ParamConverter<ScopeId> {

    private static final String SCOPE_ID_WILDCARD = KapuaLocator.getInstance().getComponent(KapuaApiCoreSetting.class).getString(KapuaApiCoreSettingKeys.API_PATH_PARAM_SCOPEID_WILDCARD);

    @Override
    public ScopeId fromString(String compactScopeId) {
        if (SCOPE_ID_WILDCARD.equals(compactScopeId)) {
            KapuaSession session = KapuaSecurityUtils.getSession();

            if (session == null) {
                throw new SessionNotPopulatedException();
            }

            return new ScopeId(session.getScopeId().getId());
        } else {
            byte[] bytes = Base64.getUrlDecoder().decode(compactScopeId);
            return new ScopeId(new BigInteger(bytes));
        }
    }

    @Override
    public String toString(ScopeId scopeId) {
        return Base64.getUrlEncoder().encodeToString(scopeId.getId().toByteArray());
    }
}
