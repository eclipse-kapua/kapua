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
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollisionEntityManagerFactory extends AbstractEntityManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CollisionEntityManagerFactory.class);

    private static final String PERSISTENCE_UNIT_NAME = "kapua-commons-unit-test";

    private static final CollisionEntityManagerFactory INSTANCE = new CollisionEntityManagerFactory();

    /**
     * Constructs a new entity manager factory and configure it to use the user persistence unit.
     */
    private CollisionEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Return the {@link EntityManager} singleton instance
     *
     * @return
     */
    public static EntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
