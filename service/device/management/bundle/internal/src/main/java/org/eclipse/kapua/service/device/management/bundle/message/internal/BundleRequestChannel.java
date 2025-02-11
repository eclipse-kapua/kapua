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
package org.eclipse.kapua.service.device.management.bundle.message.internal;

import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestChannel;

/**
 * {@link DeviceBundle} {@link KapuaRequestChannel} implementation.
 *
 * @since 1.0.0
 */
public class BundleRequestChannel extends KapuaRequestChannel {

    private static final long serialVersionUID = -1300715857785309970L;

    private String bundleId;
    private boolean start;

    /**
     * Gets the {@link DeviceBundle} identifier.
     *
     * @return The {@link DeviceBundle} identifier.
     * @since 1.0.0
     */
    public String getBundleId() {
        return bundleId;
    }

    /**
     * Sets the {@link DeviceBundle} identifier.
     *
     * @param bundleId
     *         The {@link DeviceBundle} identifier.
     * @since 1.0.0
     */
    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    /**
     * Checks if the {@link DeviceBundle} is started.
     *
     * @return {@code true} if the {@link DeviceBundle} is started, {@code false} otherwise.
     * @since 1.0.0
     */
    public boolean isStart() {
        return start;
    }

    /**
     * Sets whether or not the {@link DeviceBundle} has started.
     *
     * @param start
     *         {@code true} if the {@link DeviceBundle} has started, {@code false} otherwise.
     * @since 1.0.0
     */
    public void setStart(boolean start) {
        this.start = start;
    }
}
