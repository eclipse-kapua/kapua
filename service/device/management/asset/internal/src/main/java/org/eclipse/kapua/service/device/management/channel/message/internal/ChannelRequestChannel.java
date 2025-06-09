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
package org.eclipse.kapua.service.device.management.channel.message.internal;

import org.eclipse.kapua.service.device.management.asset.DeviceAsset;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannel;
import org.eclipse.kapua.service.device.management.asset.message.internal.AssetRequestChannel;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestChannel;

/**
 * {@link DeviceAssetChannel} {@link KapuaRequestChannel} implementation.
 *
 * @since 1.0.0
 */
public class ChannelRequestChannel extends AssetRequestChannel {

    private static final long serialVersionUID = -1578380839404848475L;

    private String assetName;

    /**
     * Gets the {@link DeviceAsset} name.
     *
     * @return The {@link DeviceAsset} name.
     * @since 1.0.0
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * Sets the {@link DeviceAsset} name.
     *
     * @param assetName
     *         The {@link DeviceAsset} name.
     * @since 1.0.0
     */
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}
