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
package org.eclipse.kapua.service.device.management.asset;

import org.eclipse.kapua.model.KapuaObjectFactory;

/**
 * {@link DeviceAsset} {@link KapuaObjectFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaObjectFactory
 * @since 1.0.0
 */
public interface DeviceAssetFactory extends KapuaObjectFactory {

    /**
     * Instantiates a new {@link DeviceAsset} instance.
     *
     * @return The newly instantiated {@link DeviceAsset}.
     * @since 1.0.0
     */
    DeviceAsset newDeviceAsset();

    /**
     * Instantiates a new {@link DeviceAssetChannel} instance.
     *
     * @return The newly instantiated {@link DeviceAssetChannel}
     * @since 1.0.0
     */
    DeviceAssetChannel newDeviceAssetChannel();
}
