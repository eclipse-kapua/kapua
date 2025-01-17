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

/**
 * {@link Device} {@link KapuaEntityFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface DeviceFactory extends KapuaEntityFactory<Device> {

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
