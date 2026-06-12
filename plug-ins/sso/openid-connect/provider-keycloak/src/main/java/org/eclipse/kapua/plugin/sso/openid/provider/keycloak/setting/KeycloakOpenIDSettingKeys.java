/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others
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
package org.eclipse.kapua.plugin.sso.openid.provider.keycloak.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum KeycloakOpenIDSettingKeys implements SettingKey {

    KEYCLOAK_URI("sso.openid.keycloak.uri"), //
    KEYCLOAK_REALM("sso.openid.keycloak.realm"), //
    KEYCLOAK_CLIENT_ID_API("sso.openid.keycloak.client.id.api"), //
    KEYCLOAK_CLIENT_ID_API_SECRET("sso.openid.keycloak.client.id.api.secret"), //
    ;

    private final String key;

    KeycloakOpenIDSettingKeys(final String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
