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
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;

/**
 * @since 1.0
 */
public class DeviceManagementOperationEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-device_management_operation_registry";

    private static final DeviceManagementOperationEntityManagerFactory INSTANCE = new DeviceManagementOperationEntityManagerFactory();

    /**
     * Constructs a new entity manager factory and configure it to use the job persistence unit.
     */
    private DeviceManagementOperationEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Return the {@link EntityManager} singleton instance
     *
     * @return
     */
    public static DeviceManagementOperationEntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
