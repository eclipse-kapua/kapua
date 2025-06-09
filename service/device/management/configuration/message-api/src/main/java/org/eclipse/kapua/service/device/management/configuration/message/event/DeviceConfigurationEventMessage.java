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
package org.eclipse.kapua.service.device.management.configuration.message.event;

import org.eclipse.kapua.message.KapuaMessage;

/**
 * Device event {@link KapuaMessage} definition.
 *
 * @param <C>
 *         The type of {@link DeviceConfigurationEventChannel}
 * @param <P>
 *         The type of {@link DeviceConfigurationEventPayload}
 * @since 2.0.0
 */
public class DeviceConfigurationEventMessage<C extends DeviceConfigurationEventChannel, P extends DeviceConfigurationEventPayload> extends KapuaMessage<C, P> {

}
