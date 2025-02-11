/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.inventory.internal.message;

import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundleAction;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainerAction;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestChannel;

/**
 * {@link DeviceInventory} {@link KapuaRequestChannel} implementation.
 *
 * @since 1.5.0
 */
public class InventoryRequestChannel extends KapuaRequestChannel {

    private static final long serialVersionUID = 9127157971609776985L;

    private DeviceInventoryBundleAction bundleAction;

    private DeviceInventoryContainerAction containerAction;

    /**
     * Gets the {@link DeviceInventoryBundleAction} to perform when {@link #getResource()} is {@code bundles}.
     *
     * @return The {@link DeviceInventoryBundleAction} to perform.
     * @since 1.5.0
     */
    public DeviceInventoryBundleAction getBundleAction() {
        return bundleAction;
    }

    /**
     * Sets the {@link DeviceInventoryBundleAction} to perform when {@link #getResource()} is {@code bundles}
     *
     * @param bundleAction
     *         The {@link DeviceInventoryBundleAction} to perform.
     * @since 1.5.0
     */
    public void setBundleAction(DeviceInventoryBundleAction bundleAction) {
        this.bundleAction = bundleAction;
    }

    /**
     * Gets the {@link DeviceInventoryContainerAction} to perform when {@link #getResource()} is {@code containers}.
     *
     * @return The {@link DeviceInventoryContainerAction} to perform.
     * @since 2.0.0
     */
    public DeviceInventoryContainerAction getContainerAction() {
        return containerAction;
    }

    /**
     * Sets the {@link DeviceInventoryContainerAction} to perform when {@link #getResource()} is {@code containers}
     *
     * @param containerAction
     *         The {@link DeviceInventoryContainerAction} to perform.
     * @since 2.0.0
     */
    public void setContainerAction(DeviceInventoryContainerAction containerAction) {
        this.containerAction = containerAction;
    }

}
