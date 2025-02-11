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
package org.eclipse.kapua.service.device.management.configuration.message.internal;

import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;

/**
 * {@link DeviceConfiguration} {@link KapuaRequestMessage} implementation
 */
public class ConfigurationRequestMessage extends KapuaRequestMessage<ConfigurationRequestChannel, ConfigurationRequestPayload> {

    private static final long serialVersionUID = -3342738357484399049L;

    @Override
    public Class<ConfigurationRequestMessage> getRequestClass() {
        return ConfigurationRequestMessage.class;
    }

    @Override
    public Class<ConfigurationResponseMessage> getResponseClass() {
        return ConfigurationResponseMessage.class;
    }

}
