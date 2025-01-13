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
package org.eclipse.kapua.service.device.registry.connection.option;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

/**
 * {@link DeviceConnectionOptionService} XML factory class
 *
 * @since 1.0
 */
@XmlRegistry
public class DeviceConnectionOptionXmlRegistry {

    private final DeviceConnectionOptionFactory deviceConnectionOptionFactory = KapuaLocator.getInstance().getFactory(DeviceConnectionOptionFactory.class);

    /**
     * Creates a new {@link DeviceConnectionOption}
     *
     * @return
     */
    public DeviceConnectionOption newDeviceConnectionOption() {
        return deviceConnectionOptionFactory.newEntity(null);
    }

    public DeviceConnectionOptionQuery newQuery() {
        return deviceConnectionOptionFactory.newQuery(null);
    }
}
