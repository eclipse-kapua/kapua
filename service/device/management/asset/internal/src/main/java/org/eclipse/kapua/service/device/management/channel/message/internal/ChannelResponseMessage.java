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
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;

/**
 * {@link DeviceAssetChannel} {@link KapuaResponseMessage} implementation.
 *
 * @since 1.0.0
 */
public class ChannelResponseMessage extends KapuaResponseMessage<ChannelResponseChannel, ChannelResponsePayload> {

    private static final long serialVersionUID = 3851995320275868922L;
}
