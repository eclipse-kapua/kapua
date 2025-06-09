/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.registry.operation;

import org.eclipse.kapua.model.KapuaEntityFactory;

/**
 * {@link DeviceManagementOperationFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface DeviceManagementOperationFactory extends KapuaEntityFactory<DeviceManagementOperation> {

    /**
     * Instantiates a new {@link DeviceManagementOperationProperty}.
     *
     * @param name
     *         The name to set in the {@link DeviceManagementOperationProperty}
     * @param propertyType
     *         The property type to set in the {@link DeviceManagementOperationProperty}
     * @param propertyValue
     *         The property value to set in the {@link DeviceManagementOperationProperty}
     * @return The newly instantiated {@link DeviceManagementOperationProperty}
     * @since 1.0.0
     */
    DeviceManagementOperationProperty newStepProperty(String name, String propertyType, String propertyValue);
}
