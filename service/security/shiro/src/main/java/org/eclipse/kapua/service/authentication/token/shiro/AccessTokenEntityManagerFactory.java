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
package org.eclipse.kapua.service.authentication.token.shiro;

import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;

/**
 * Entity manager factory for the {@link AccessTokenServiceImpl} module.
 *
 * @since 1.0.0
 */
public class AccessTokenEntityManagerFactory extends AbstractEntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-authentication";

    private static final AccessTokenEntityManagerFactory INSTANCE = new AccessTokenEntityManagerFactory();

    /**
     * Constructs a new entity manager factory and configure it to use the user persistence unit.
     */
    private AccessTokenEntityManagerFactory() {
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
