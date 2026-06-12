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
package org.eclipse.kapua.plugin.sso.openid.provider.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

/**
 * @since 1.0.0
 */
public enum OpenIDSettingKeys implements SettingKey {

    /**
     * @since 1.0.0
     */
    SSO_OPENID_PROVIDER("sso.openid.provider"),
    /**
     * @since 1.0.0
     */
    SSO_OPENID_CONF_PATH("sso.openid.conf.path"),
    /**
     * @since 1.0.0
     */
    SSO_OPENID_CLIENT_ID("sso.openid.client.id"),
    /**
     * @since 1.0.0
     */
    SSO_OPENID_CLIENT_SECRET("sso.openid.client.secret"),
    /**
     * @since 1.0.0
     */
    SSO_OPENID_JWT_PROCESSOR_TIMEOUT("sso.openid.jwt_processor_timeout"),

    /**
     * @since 2.0.0
     */
    SSO_OPENID_CLAIMS_EXTERNAL_USERNAME_KEY("sso.openid.claims.externalUsername.key"),

    /**
     * @since 2.0.0
     */
    SSO_OPENID_BROKERING_ENABLED("sso.openid.broker.enabled");

    private final String key;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    OpenIDSettingKeys(final String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
