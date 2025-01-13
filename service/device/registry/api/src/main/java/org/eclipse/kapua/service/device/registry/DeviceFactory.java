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
package org.eclipse.kapua.service.device.registry;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link Device} {@link KapuaEntityFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface DeviceFactory extends KapuaEntityFactory<Device, DeviceCreator, DeviceQuery> {

    /**
     * Instantiates a new {@link DeviceCreator}
     *
     * @param scopeId
     *         The scope {@link KapuaId} to set into the {@link DeviceCreator}
     * @param clientId
     *         The client id to set into the {@link DeviceCreator}
     * @return The newly instantiated {@link DeviceCreator}.
     * @since 1.0.0
     * @deprecated Since 1.5.0. Please use {@link #newCreator(KapuaId)}.
     */
    @Deprecated
    DeviceCreator newCreator(KapuaId scopeId, String clientId);

    /**
     * Instantiates a new {@link DeviceExtendedProperty}.
     *
     * @param groupName
     *         The {@link DeviceExtendedProperty#getGroupName()}.
     * @param name
     *         The {@link DeviceExtendedProperty#getName()}.
     * @param value
     *         The {@link DeviceExtendedProperty#getValue()}.
     * @return The newly instantiated {@link DeviceExtendedProperty}.
     * @since 1.5.0
     */
    DeviceExtendedProperty newExtendedProperty(String groupName, String name, String value);
}
