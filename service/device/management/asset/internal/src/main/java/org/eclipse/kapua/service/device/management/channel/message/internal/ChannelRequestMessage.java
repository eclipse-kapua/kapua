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
package org.eclipse.kapua.service.device.management.channel.message.internal;

import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannel;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;

/**
 * {@link DeviceAssetChannel} {@link KapuaRequestMessage} implementation.
 *
 * @since 1.0.0
 */
public class ChannelRequestMessage extends KapuaRequestMessage<ChannelRequestChannel, ChannelRequestPayload> {

    private static final long serialVersionUID = 5108417578217263516L;

    @Override
    public Class<ChannelRequestMessage> getRequestClass() {
        return ChannelRequestMessage.class;
    }

    @Override
    public Class<ChannelResponseMessage> getResponseClass() {
        return ChannelResponseMessage.class;
    }

}
