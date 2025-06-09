/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.call.message.app;

import org.eclipse.kapua.service.device.call.message.DeviceMessage;

/**
 * {@link DeviceAppMessage} definition.
 *
 * @param <C>
 *         The {@link DeviceAppChannel} type
 * @param <P>
 *         The {@link DeviceAppPayload} type
 * @since 1.0.0
 */
public interface DeviceAppMessage<C extends DeviceAppChannel, P extends DeviceAppPayload> extends DeviceMessage<C, P> {

}
