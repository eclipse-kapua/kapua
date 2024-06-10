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
package org.eclipse.kapua.app.api.core.settings;

import org.eclipse.kapua.commons.setting.SettingKey;

/**
 * REST API {@link SettingKey}s
 *
 * @since 1.0.0
 */
public enum KapuaApiCoreSettingKeys implements SettingKey {

    /**
     * @since 1.0.0
     */
    API_PATH_PARAM_SCOPEID_WILDCARD("api.path.param.scopeId.wildcard"),

    /**
     * Whether DeviceManagementPakages.download and DeviceManagementPakages.uninstall should return 200 with DeviceManagementOperation entity ({@code false}) or 204 without a content ({@code false})
     *
     * @since 2.0.0
     */
    API_DEVICE_MANAGEMENT_PACKAGE_RESPONSE_LEGACY_MODE("api.device.management.package.response.legacy.mode");

    private final String key;

    /**
     * Constructor
     *
     * @param key
     *         The value for the setting.
     * @since 1.0.0
     */
    KapuaApiCoreSettingKeys(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
