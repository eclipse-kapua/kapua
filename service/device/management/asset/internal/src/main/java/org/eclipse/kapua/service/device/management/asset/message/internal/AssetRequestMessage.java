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
package org.eclipse.kapua.service.device.management.asset.message.internal;

import org.eclipse.kapua.service.device.management.asset.DeviceAsset;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;

/**
 * {@link DeviceAsset} {@link KapuaRequestMessage} implementation.
 *
 * @since 1.0.0
 */
public class AssetRequestMessage extends KapuaRequestMessage<AssetRequestChannel, AssetRequestPayload> {

    private static final long serialVersionUID = 590225089986525842L;

    @Override
    public Class<AssetRequestMessage> getRequestClass() {
        return AssetRequestMessage.class;
    }

    @Override
    public Class<AssetResponseMessage> getResponseClass() {
        return AssetResponseMessage.class;
    }

}
