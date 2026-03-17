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

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

/**
 * Telemetry consumer setting implementation.<br>
 * This class handles settings for the {@link ConsumerTelemetrySettingKey}.
 *
 * @since 1.0
 */
public class ConsumerTelemetrySetting extends AbstractKapuaSetting<ConsumerTelemetrySettingKey> {

    /**
     * Constant representing name of the resource properties file used by this settings.
     */
    private static final String CONFIG_RESOURCE_NAME = "kapua-consumer-telemetry-settings.properties";

    private static ConsumerTelemetrySetting instance;

    private ConsumerTelemetrySetting() {
        super(CONFIG_RESOURCE_NAME);
    }

    public static ConsumerTelemetrySetting getInstance() {
        synchronized (ConsumerTelemetrySetting.class) {
            if (instance == null) {
                instance = new ConsumerTelemetrySetting();
            }
            return instance;
        }
    }

    /**
     * Allow re-setting the global instance
     * <p>
     * This method clears out the internal global instance in order to let the next call
     * to {@link #getInstance()} return a fresh instance.
     * </p>
     * <p>
     * This may be helpful for unit tests which need to change system properties for testing
     * different behaviors.
     * </p>
     */
    public static void resetInstance() {
        synchronized (ConsumerTelemetrySetting.class) {
            instance = null;
        }
    }
}
