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

import java.util.Date;

import javax.inject.Singleton;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventQuery;

/**
 * {@link DeviceEventFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceEventFactoryImpl implements DeviceEventFactory {

    @Override
    public DeviceEventCreator newCreator(KapuaId scopeId, KapuaId deviceId, Date receivedOn, String resource) {
        DeviceEventCreator creator = newCreator(scopeId);

        creator.setDeviceId(deviceId);
        creator.setAction(KapuaMethod.CREATE);
        creator.setReceivedOn(new Date(receivedOn.getTime()));
        creator.setResource(resource);

        return creator;
    }

    @Override
    public DeviceEventQuery newQuery(KapuaId scopeId) {
        return new DeviceEventQueryImpl(scopeId);
    }

    @Override
    public DeviceEvent newEntity(KapuaId scopeId) {
        return new DeviceEventImpl(scopeId);
    }

    @Override
    public DeviceEventCreator newCreator(KapuaId scopeId) {
        return new DeviceEventCreatorImpl(scopeId);
    }

    @Override
    public DeviceEvent clone(DeviceEvent deviceEvent) {
        return new DeviceEventImpl(deviceEvent);
    }
}
