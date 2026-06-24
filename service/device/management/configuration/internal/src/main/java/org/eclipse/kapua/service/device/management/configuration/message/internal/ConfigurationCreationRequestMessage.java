/*******************************************************************************
 * Copyright (c) 2025 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;

public class ConfigurationCreationRequestMessage extends KapuaMessageImpl<ConfigurationRequestChannel, ConfigurationCreationRequestPayload>
        implements KapuaRequestMessage<ConfigurationRequestChannel, ConfigurationCreationRequestPayload> {

    @Override
    public Class<ConfigurationCreationRequestMessage> getRequestClass() {
        return ConfigurationCreationRequestMessage.class;
    }

    @Override
    public Class<ConfigurationCreationResponseMessage> getResponseClass() {
        return ConfigurationCreationResponseMessage.class;
    }
}
