/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.registry.group;

import org.eclipse.kapua.locator.KapuaLocator;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * {@link DeviceGroup} {@link XmlRegistry} definition.
 *
 * @since 2.1.0
 */
@XmlRegistry
public class DeviceGroupXmlRegistry {

    private final DeviceGroupFactory deviceGroupFactory = KapuaLocator.getInstance().getFactory(DeviceGroupFactory.class);

    /**
     * Instantiates a new {@link DeviceGroup} instance
     *
     * @return The newly instantiated {@link DeviceGroup} instance.
     * @since 2.1.0
     */
    public DeviceGroup newEntity() {
        return deviceGroupFactory.newEntity(null);
    }

    /**
     * Instantiates a new {@link DeviceGroupCreator} instance.
     *
     * @return The newly instantiated {@link DeviceGroupCreator} instance.
     * @since 2.1.0
     */
    public DeviceGroupCreator newCreator() {
        return deviceGroupFactory.newCreator(null);
    }

    /**
     * Instantiates a new {@link DeviceGroupListResult} instance.
     *
     * @return The newly instantiated {@link DeviceGroupListResult} instance.
     * @since 2.1.0
     */
    public DeviceGroupListResult newListResult() {
        return deviceGroupFactory.newListResult();
    }

    /**
     * Instantiates a new {@link DeviceGroupQuery} instance.
     *
     * @return The newly instantiated {@link DeviceGroupQuery} instance.
     * @since 2.1.0
     */
    public DeviceGroupQuery newQuery() {
        return deviceGroupFactory.newQuery(null);
    }
}
