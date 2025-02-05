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
package org.eclipse.kapua.service.device.management.packages.model;

import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;

/**
 * {@link DevicePackageOptions} definition.
 *
 * @since 1.1.0
 */
public abstract class DevicePackageOptions {

    private Long timeout;

    /**
     * The {@link DeviceManagementOperation} timeout.
     *
     * @return The {@link DeviceManagementOperation} timeout.
     * @since 1.1.0
     */
    public Long getTimeout() {
        return timeout;
    }

    /**
     * Sets the {@link DeviceManagementOperation} timeout.
     *
     * @param timeout
     *         The {@link DeviceManagementOperation} timeout.
     * @since 1.1.0
     */
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
