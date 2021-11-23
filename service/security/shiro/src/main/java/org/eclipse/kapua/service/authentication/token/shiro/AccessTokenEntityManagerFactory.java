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
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.eclipse.kapua.service.authentication.shiro.AuthenticationServiceShiroImpl;

/**
 * {@link AuthenticationServiceShiroImpl} {@link EntityManagerFactory} implementation.
 *
 * @since 1.0.0
 */
public class AccessTokenEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "kapua-authentication";

    private static final AccessTokenEntityManagerFactory INSTANCE = new AccessTokenEntityManagerFactory();

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    private AccessTokenEntityManagerFactory() {
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
