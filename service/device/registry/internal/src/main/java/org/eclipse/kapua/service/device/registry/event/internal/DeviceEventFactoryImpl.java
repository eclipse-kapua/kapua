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
package org.eclipse.kapua.service.device.registry.event.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;

/**
 * {@link DeviceEventFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceEventFactoryImpl implements DeviceEventFactory {

    @Override
    public DeviceEvent newEntity(KapuaId scopeId) {
        return new DeviceEventImpl(scopeId);
    }

    @Override
    public DeviceEvent clone(DeviceEvent deviceEvent) {
        return new DeviceEventImpl(deviceEvent);
    }
}
