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
package org.eclipse.kapua.service.device.registry.group.internal;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupCreator;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupFactory;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupListResult;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupQuery;

import javax.inject.Singleton;

/**
 * {@link DeviceGroupFactory} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class DeviceGroupFactoryImpl implements DeviceGroupFactory {

    @Override
    public DeviceGroupCreator newCreator(KapuaId scopeId) {
        return new DeviceGroupCreatorImpl(scopeId);
    }

    @Override
    public DeviceGroup newEntity(KapuaId scopeId) {
        return new DeviceGroupImpl(scopeId);
    }

    @Override
    public DeviceGroupListResult newListResult() {
        return new DeviceGroupListResultImpl();
    }

    @Override
    public DeviceGroupQuery newQuery(KapuaId scopeId) {
        return new DeviceGroupQueryImpl(scopeId);
    }

    @Override
    public DeviceGroup clone(DeviceGroup deviceGroup) throws KapuaEntityCloneException {
        return DeviceGroupImpl.parse(deviceGroup);
    }
}
