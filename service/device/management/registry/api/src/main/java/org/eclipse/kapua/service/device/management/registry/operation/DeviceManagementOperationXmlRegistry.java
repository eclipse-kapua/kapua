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

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

/**
 * {@link DeviceManagementOperation} xml factory class
 *
 * @since 1.0
 */
@XmlRegistry
public class DeviceManagementOperationXmlRegistry {

    private final KapuaLocator locator = KapuaLocator.getInstance();
    private final DeviceManagementOperationFactory factory = locator.getFactory(DeviceManagementOperationFactory.class);

    DeviceManagementOperation newDeviceManagementOperation() {
        return factory.newEntity(null);
    }

    DeviceManagementOperationCreator newDeviceManagementOperationCreator() {
        return factory.newCreator(null);
    }

    DeviceManagementOperationQuery newQuery() {
        return factory.newQuery(null);
    }
}
