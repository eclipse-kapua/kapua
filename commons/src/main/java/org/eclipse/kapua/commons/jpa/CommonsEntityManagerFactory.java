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
package org.eclipse.kapua.commons.jpa;

import org.eclipse.kapua.KapuaException;

/**
 * Commons module entity manager reference service.
 *
 * @since 1.0
 */
public class CommonsEntityManagerFactory extends AbstractEntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-commons";

    private static final CommonsEntityManagerFactory INSTANCE = new CommonsEntityManagerFactory();

    /**
     * Constructor
     */
    private CommonsEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Return the entity manager
     *
     * @return
     * @throws KapuaException
     */
    public static EntityManager getEntityManager() throws KapuaException {
        return INSTANCE.createEntityManager();
    }

    /**
     * Return the {@link CommonsEntityManagerFactory} instance (singleton)
     *
     * @return
     */
    public static CommonsEntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
