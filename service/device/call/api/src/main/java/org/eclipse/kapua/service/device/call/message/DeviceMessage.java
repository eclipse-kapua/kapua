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
package org.eclipse.kapua.service.device.call.message;

import java.util.Date;

import org.eclipse.kapua.message.Channel;
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.message.Payload;

/**
 * {@link DeviceMessage} definition.
 *
 * @param <C>
 *         {@link DeviceChannel} type
 * @param <P>
 *         {@link DevicePayload} type
 * @since 1.0.0
 */
public interface DeviceMessage<C extends DeviceChannel & Channel, P extends DevicePayload & Payload> extends Message<C, P> {

    /**
     * Gets the {@link DeviceChannel}.
     *
     * @return The {@link DeviceChannel}.
     * @since 1.0.0
     */
    C getChannel();

    /**
     * Sets the {@link DeviceChannel}.
     *
     * @param channel
     *         The {@link DeviceChannel} to set.
     * @since 1.0.0
     */
    void setChannel(C channel);

    /**
     * Gets the {@link DevicePayload}.
     *
     * @return The {@link DevicePayload}.
     * @since 1.0.0
     */
    P getPayload();

    /**
     * Sets the {@link DevicePayload}.
     *
     * @param payload
     *         The {@link DevicePayload} to set.
     * @since 1.0.0
     */
    void setPayload(P payload);

    /**
     * Gets the timestamp.
     *
     * @return The timestamp.
     * @since 1.0.0
     */
    Date getTimestamp();

    /**
     * Sets the timestamp.
     *
     * @param timestamp
     *         The timestamp to set.
     * @since 1.0.0
     */
    void setTimestamp(Date timestamp);
}
