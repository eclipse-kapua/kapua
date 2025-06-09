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

import org.eclipse.kapua.service.device.management.inventory.internal.DeviceInventoryAppProperties;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseChannel;

/**
 * {@link DeviceInventory} {@link KapuaResponseChannel} implementation.
 *
 * @since 1.5.0
 */
public class InventoryResponseChannel extends KapuaResponseChannel {

    private static final long serialVersionUID = 6073193292259010928L;

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    public InventoryResponseChannel() {
        setAppName(DeviceInventoryAppProperties.APP_NAME);
        setVersion(DeviceInventoryAppProperties.APP_VERSION);
    }
}
