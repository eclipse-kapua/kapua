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

import java.util.TimerTask;

/**
 * The {@link TimeoutTimerTask} run when timeout expires.
 *
 * @since 1.0.0
 */
public class TimeoutTimerTask extends TimerTask {

    private final MqttResponseTimeoutTimer mqttResponseTimeoutTimer;

    public TimeoutTimerTask(MqttResponseTimeoutTimer mqttResponseTimeoutTimer) {
        this.mqttResponseTimeoutTimer = mqttResponseTimeoutTimer;
    }

    @Override
    public void run() {
        synchronized (mqttResponseTimeoutTimer.mqttResponseCallback) {
            mqttResponseTimeoutTimer.mqttResponseCallback.notifyAll();
        }
    }
}
