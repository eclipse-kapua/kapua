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
package org.eclipse.kapua.commons.jersey.rest.filters;

import org.eclipse.kapua.commons.setting.SettingKey;

/**
 * REST API {@link SettingKey}s
 *
 * @since 1.0.0
 */
public enum KapuaRestFiltersSettingKeys implements SettingKey {

    /**
     * @since 1.5.0
     */
    API_CORS_REFRESH_INTERVAL("api.cors.refresh.interval"),
    /**
     * @since 1.5.0
     */
    API_CORS_ORIGINS_ALLOWED("api.cors.origins.allowed");

    private final String key;

    /**
     * Constructor
     *
     * @param key
     *         The value for the setting.
     * @since 1.0.0
     */
    KapuaRestFiltersSettingKeys(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
