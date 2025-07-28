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
package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;

/**
 * Datastore module {@link EntityManagerFactory} impleemntation
 *
 * @since 1.0.0
 */
public class DatastoreEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-datastore";

    private static final DatastoreEntityManagerFactory INSTANCE = new DatastoreEntityManagerFactory();

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    private DatastoreEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Returns the {@link EntityManagerFactory} instance.
     *
     * @return The {@link EntityManagerFactory} instance.
     * @since 1.0.0
     */
    public static DatastoreEntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
