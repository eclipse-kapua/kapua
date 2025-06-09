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
package org.eclipse.kapua.service.device.management.command.message.internal;

import org.eclipse.kapua.service.device.management.command.DeviceCommand;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;

/**
 * {@link DeviceCommand} {@link KapuaRequestMessage} implementation
 */
public class CommandRequestMessage extends KapuaRequestMessage<CommandRequestChannel, CommandRequestPayload> {

    private static final long serialVersionUID = -2546716187486012931L;

    @Override
    public Class<CommandRequestMessage> getRequestClass() {
        return CommandRequestMessage.class;
    }

    @Override
    public Class<CommandResponseMessage> getResponseClass() {
        return CommandResponseMessage.class;
    }

}
