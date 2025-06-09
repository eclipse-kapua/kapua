/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.transport.mqtt;

import java.util.Timer;

import javax.validation.constraints.NotNull;

/**
 * The {@link Timer} to handle the timeout of {@link MqttResponseCallback}.
 *
 * @since 1.0.0
 */
public class MqttResponseTimeoutTimer extends Timer {

    private static final String MQTT_RESPONSE_TIMEOUT_TIMER_NAME_FORMAT = MqttResponseTimeoutTimer.class.getSimpleName() + "-%s";

    protected final MqttResponseCallback mqttResponseCallback;

    /**
     * Starts a {@link Timer} at the given timeout and runs {@link TimeoutTimerTask} when timeout expires.
     *
     * @param clientId
     *         The clientId of the {@link MqttClient}. Used to set the {@link Timer} name.
     * @param mqttClientCallback
     *         The {@link MqttResponseCallback} on which to wait.
     * @param timeout
     *         The timeout of the waiting.
     * @since 1.0.0
     */
    public MqttResponseTimeoutTimer(@NotNull String clientId, @NotNull MqttResponseCallback mqttClientCallback, long timeout) {
        super(String.format(MQTT_RESPONSE_TIMEOUT_TIMER_NAME_FORMAT, clientId), true);

        this.mqttResponseCallback = mqttClientCallback;

        schedule(new TimeoutTimerTask(this), timeout);
    }

}
