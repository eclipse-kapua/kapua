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
package org.eclipse.kapua.service.device.registry.connection.option.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOption;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionCreator;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionFactory;

/**
 * {@link DeviceConnectionOptionFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceConnectionOptionFactoryImpl implements DeviceConnectionOptionFactory {

    @Override
    public DeviceConnectionOptionCreator newCreator(KapuaId scopeId) {
        return new DeviceConnectionOptionCreatorImpl(scopeId);
    }

    @Override
    public DeviceConnectionOption newEntity(KapuaId scopeId) {
        return new DeviceConnectionOptionImpl(scopeId);
    }

    @Override
    public DeviceConnectionOption clone(DeviceConnectionOption deviceConnectionOption) {
        try {
            return new DeviceConnectionOptionImpl(deviceConnectionOption);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, DeviceConnectionOption.TYPE, deviceConnectionOption);
        }
    }
}
