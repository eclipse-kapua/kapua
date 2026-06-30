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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.device.call.kura.model.wire;

import org.eclipse.kapua.service.device.call.message.DeviceMetrics;
import org.eclipse.kapua.service.device.call.message.app.DeviceAppMetrics;

/**
 * Wire {@link DeviceMetrics}.
 *
 * @since 2.1.0
 */
public enum WireMetrics implements DeviceAppMetrics {

    /**
     * Application identifier.
     *
     * @since 2.1.0
     */
    APP_ID("WIRE"),

    /**
     * Application version.
     *
     * @since 2.1.0
     */
    APP_VERSION("V1"),
    ;

    /**
     * The name of the metric.
     *
     * @since 2.1.0
     */
    private final String name;

    /**
     * Constructor.
     *
     * @param name The name of the metric.
     * @since 2.1.0
     */
    WireMetrics(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
