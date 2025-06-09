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
package org.eclipse.kapua.service.device.management.command.message.internal;

import org.eclipse.kapua.service.device.management.command.DeviceCommand;
import org.eclipse.kapua.service.device.management.command.internal.CommandAppProperties;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseChannel;

/**
 * {@link DeviceCommand} {@link KapuaResponseChannel} implementation.
 *
 * @since 1.0.0
 */
public class CommandResponseChannel extends KapuaResponseChannel {

    private static final long serialVersionUID = 1788983749020930113L;

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    public CommandResponseChannel() {
        setAppName(CommandAppProperties.APP_NAME);
        setVersion(CommandAppProperties.APP_VERSION);
    }
}
