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
package org.eclipse.kapua.service.device.registry.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceExtendedProperty;
import org.eclipse.kapua.service.device.registry.DeviceFactory;

/**
 * {@link DeviceFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceFactoryImpl implements DeviceFactory {

    @Override
    public Device newEntity(KapuaId scopeId) {
        return new DeviceImpl(scopeId);
    }

    @Override
    public DeviceExtendedProperty newExtendedProperty(String groupName, String name, String value) {
        return new DeviceExtendedPropertyImpl(groupName, name, value);
    }

    @Override
    public Device clone(Device device) {
        try {
            return new DeviceImpl(device);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, Device.TYPE, device);
        }
    }
}
