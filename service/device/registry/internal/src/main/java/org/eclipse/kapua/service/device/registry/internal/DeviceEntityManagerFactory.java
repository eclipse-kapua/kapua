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
package org.eclipse.kapua.service.device.registry.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManager;

/**
 * Entity manager factory for the device module.
 *
 * @since 1.0
 */
public class DeviceEntityManagerFactory extends AbstractEntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-device";

    private static final DeviceEntityManagerFactory INSTANCE = new DeviceEntityManagerFactory();

    /**
     * Constructs a new entity manager factory and configure it to use the device persistence unit.
     */
    private DeviceEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Return a new {@link EntityManager} instance
     *
     * @return
     */
    public static EntityManager getEntityManager() throws KapuaException {
        return INSTANCE.createEntityManager();
    }

    /**
     * Return the {@link EntityManager} singleton instance
     *
     * @return
     */
    public static DeviceEntityManagerFactory instance() {
        return INSTANCE;
    }

}
