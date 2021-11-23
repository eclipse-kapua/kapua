/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.job.internal;

import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;

/**
 * {@link org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperation} {@link EntityManagerFactory}
 *
 * @since 1.1.0
 */
public class JobDeviceManagementOperationEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-job-device-management-operation";

    private static final JobDeviceManagementOperationEntityManagerFactory INSTANCE = new JobDeviceManagementOperationEntityManagerFactory();

    /**
     * Constructs a new entity manager factory and configure it to use the job persistence unit.
     *
     * @since 1.1.0
     */
    private JobDeviceManagementOperationEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Return the {@link EntityManager} singleton instance
     *
     * @return
     * @since 1.1.0
     */
    public static JobDeviceManagementOperationEntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
