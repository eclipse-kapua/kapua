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
package org.eclipse.kapua.service.device.management.message.event;

import org.eclipse.kapua.message.KapuaMessage;

/**
 * Device event {@link KapuaMessage} definition.
 *
 * @param <C>
 *         The type of {@link KapuaManagementEventChannel}
 * @param <P>
 *         The type of {@link KapuaManagementEventPayload}
 * @since 2.0.0
 */
public class KapuaManagementEventMessage<C extends KapuaManagementEventChannel, P extends KapuaManagementEventPayload> extends KapuaMessage<C, P> {

    private static final long serialVersionUID = 4475824062529778773L;

}
