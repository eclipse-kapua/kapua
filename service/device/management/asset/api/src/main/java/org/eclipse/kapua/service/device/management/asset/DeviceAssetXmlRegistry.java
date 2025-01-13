/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

/**
 * {@link DeviceAsset} XML factory class
 *
 * @since 1.0.0
 */
@XmlRegistry
public class DeviceAssetXmlRegistry {

    private final DeviceAssetFactory deviceAssetFactory = KapuaLocator.getInstance().getFactory(DeviceAssetFactory.class);

    /**
     * Instantiate a new {@link DeviceAsset}.
     *
     * @return The newly instantiated {@link DeviceAsset}.
     * @since 1.0.0
     */
    public DeviceAsset newDeviceAsset() {
        return deviceAssetFactory.newDeviceAsset();
    }

    /**
     * Instantiate a new {@link DeviceAssetChannel}.
     *
     * @return The newly instantiated {@link DeviceAssetChannel}.
     * @since 1.0.0
     */
    public DeviceAssetChannel newDeviceAssetChannel() {
        return deviceAssetFactory.newDeviceAssetChannel();
    }
}
