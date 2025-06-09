/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.message.request;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.device.management.message.KapuaAppProperties;

/**
 * {@link KapuaRequestMessage} {@link KapuaObjectFactory} definition.
 *
 * @since 1.0.0
 */
public interface KapuaRequestMessageFactory extends KapuaObjectFactory {

    /**
     * Instantiates a new {@link KapuaRequestMessage}.
     *
     * @return The newly instantiated {@link KapuaRequestMessage}.
     * @since 1.0.0
     */
    KapuaRequestMessage<?, ?> newRequestMessage();

    /**
     * Instantiates a new {@link KapuaAppProperties}.
     *
     * @return The newly instantiated {@link KapuaAppProperties}.
     * @since 1.0.0
     */
    KapuaAppProperties newAppProperties(String value);
}
