/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others.
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
package org.eclipse.kapua.plugin.sso.openid.provider.keycloak;

import com.google.common.base.Strings;
import org.eclipse.kapua.plugin.sso.openid.exception.OpenIDIllegalArgumentException;
import org.eclipse.kapua.plugin.sso.openid.exception.uri.OpenIDIllegalUriException;
import org.eclipse.kapua.plugin.sso.openid.provider.keycloak.setting.KeycloakOpenIDSetting;
import org.eclipse.kapua.plugin.sso.openid.provider.keycloak.setting.KeycloakOpenIDSettingKeys;

import javax.inject.Inject;

/**
 * The Keycloak OpenID service utility class.
 */
public class KeycloakOpenIDUtils {

    private final KeycloakOpenIDSetting keycloakOpenIDSetting;
    public static final String KEYCLOAK_URI_COMMON_PART = "/realms/";

    @Inject
    public KeycloakOpenIDUtils(KeycloakOpenIDSetting keycloakOpenIDSetting) {
        this.keycloakOpenIDSetting = keycloakOpenIDSetting;
    }

    /**
     * Get the Keycloak realm.
     *
     * @return the Keycloak realm in the form of a String.
     * @throws OpenIDIllegalArgumentException if the realm is not set.
     */
    public String getRealm() throws OpenIDIllegalArgumentException {
        String realm = keycloakOpenIDSetting.getString(KeycloakOpenIDSettingKeys.KEYCLOAK_REALM);
        if (Strings.isNullOrEmpty(realm)) {
            throw new OpenIDIllegalArgumentException(KeycloakOpenIDSettingKeys.KEYCLOAK_REALM.key(), realm);
        }
        return realm;
    }

    /**
     * Get the Keycloak provider URI.
     *
     * @return the Keycloak provider URI in the form of a String.
     * @throws OpenIDIllegalUriException if the Keycloak provider URI is not set.
     */
    public String getProviderUri() throws OpenIDIllegalUriException {
        String providerUri = keycloakOpenIDSetting.getString(KeycloakOpenIDSettingKeys.KEYCLOAK_URI);
        if (Strings.isNullOrEmpty(providerUri)) {
            throw new OpenIDIllegalUriException(KeycloakOpenIDSettingKeys.KEYCLOAK_URI.key(), providerUri);
        }
        return providerUri;
    }

    public String getKeycloakApiClientID() throws OpenIDIllegalArgumentException {
        String clientId = keycloakOpenIDSetting.getString(KeycloakOpenIDSettingKeys.KEYCLOAK_CLIENT_ID_API);
        if (Strings.isNullOrEmpty(clientId)) {
            throw new OpenIDIllegalArgumentException(KeycloakOpenIDSettingKeys.KEYCLOAK_CLIENT_ID_API.key(), clientId);
        }
        return clientId;
    }

    public String getKeycloakApiClientSecret() throws OpenIDIllegalArgumentException {
        String secret = keycloakOpenIDSetting.getString(KeycloakOpenIDSettingKeys.KEYCLOAK_CLIENT_ID_API_SECRET);
        if (Strings.isNullOrEmpty(secret)) {
            throw new OpenIDIllegalArgumentException(KeycloakOpenIDSettingKeys.KEYCLOAK_CLIENT_ID_API.key(), secret);
        }
        return secret;
    }



}
