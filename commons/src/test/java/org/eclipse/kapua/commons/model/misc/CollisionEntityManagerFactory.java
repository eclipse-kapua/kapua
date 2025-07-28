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
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.model.misc;

import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link CollisionEntityService} {@link EntityManagerFactory} implementation
 *
 * @since 1.0.0
 */
public class CollisionEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CollisionEntityManagerFactory.class);

    private static final String PERSISTENCE_UNIT_NAME = "kapua-commons-unit-test";

    private static final CollisionEntityManagerFactory INSTANCE = new CollisionEntityManagerFactory();

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    private CollisionEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Returns the {@link EntityManagerFactory} instance.
     *
     * @return The {@link EntityManagerFactory} instance.
     * @since 1.0.0
     */
    public static EntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
