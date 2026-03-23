/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.consumer.telemetry.config;

import org.eclipse.kapua.commons.setting.SettingKey;

/**
 * Consumer telemetry settings key implementation.
 *
 * @since 1.0
 */
public enum ConsumerTelemetrySettingKey implements SettingKey {

    /**
     * Telemetry consumer - service settings cache size
     */
    TELEMETRY_SERVICE_SETTINGS_CACHE_SIZE("consumer.telemetry.service.settings.cache.size"),
    /**
     * Telemetry consumer - service settings cache ttl
     */
    TELEMETRY_SERVICE_SETTINGS_CACHE_TTL("consumer.telemetry.service.settings.cache.ttl");

    private String key;

    /**
     * Constructor
     *
     * @param key
     */
    private ConsumerTelemetrySettingKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
