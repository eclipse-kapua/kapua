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
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.registry.operation.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationProperty;

/**
 * {@link DeviceManagementOperationFactory} implementation
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceManagementOperationFactoryImpl implements DeviceManagementOperationFactory {

    @Override
    public DeviceManagementOperation newEntity(KapuaId scopeId) {
        return new DeviceManagementOperationImpl(scopeId);
    }

    @Override
    public DeviceManagementOperationProperty newStepProperty(String name, String propertyType, String propertyValue) {
        return new DeviceManagementOperationPropertyImpl(name, propertyType, propertyValue);
    }

    @Override
    public DeviceManagementOperation clone(DeviceManagementOperation deviceManagementOperation) {
        try {
            return new DeviceManagementOperationImpl(deviceManagementOperation);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, DeviceManagementOperation.TYPE, deviceManagementOperation);
        }
    }
}
