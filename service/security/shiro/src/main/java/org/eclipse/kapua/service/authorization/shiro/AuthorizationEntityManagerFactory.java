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
package org.eclipse.kapua.service.authorization.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;

/**
 * {@link AuthorizationServiceImpl} {@link EntityManagerFactory} implementation.
 *
 * @since 1.0.0
 */
public class AuthorizationEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-authorization";

    private static final AuthorizationEntityManagerFactory INSTANCE = new AuthorizationEntityManagerFactory();

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    private AuthorizationEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Returns a {@link EntityManager} instance
     *
     * @return A {@link EntityManager} instance.
     * @since 1.0.0
     */
    public static EntityManager getEntityManager() throws KapuaException {
        return INSTANCE.createEntityManager();
    }

    /**
     * Returns the {@link EntityManagerFactory} instance.
     *
     * @return The {@link EntityManagerFactory} instance.
     * @since 1.0.0
     */
    public static AuthorizationEntityManagerFactory getInstance() {
        return INSTANCE;
    }

}
