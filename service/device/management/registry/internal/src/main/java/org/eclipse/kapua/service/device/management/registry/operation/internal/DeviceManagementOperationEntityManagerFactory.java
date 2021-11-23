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

import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;

/**
 * DeviceManagementOperation Service {@link EntityManagerFactory} implementation.
 *
 * @since 1.1.0
 */
public class DeviceManagementOperationEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-device_management_operation_registry";

    private static final DeviceManagementOperationEntityManagerFactory INSTANCE = new DeviceManagementOperationEntityManagerFactory();

    /**
     * Constructor.
     *
     * @since 1.1.0
     */
    private DeviceManagementOperationEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Returns the {@link EntityManagerFactory} instance.
     *
     * @return The {@link EntityManagerFactory} instance.
     * @since 1.1.0
     */
    public static DeviceManagementOperationEntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
