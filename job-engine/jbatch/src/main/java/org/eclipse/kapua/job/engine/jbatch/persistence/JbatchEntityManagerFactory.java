/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.jbatch.persistence;

import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.eclipse.kapua.job.engine.jbatch.driver.JbatchDriver;

/**
 * {@link JbatchDriver} {@link EntityManagerFactory} implementation.
 *
 * @since 1.2.0
 */
public class JbatchEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory {

    private static final String PERSISTENCE_UNIT_NAME = "jbatch";

    private static final JbatchEntityManagerFactory INSTANCE = new JbatchEntityManagerFactory();

    /**
     * Constructor.
     *
     * @since 1.2.0
     */
    private JbatchEntityManagerFactory() {
        super(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Returns the {@link EntityManagerFactory} instance.
     *
     * @return The {@link EntityManagerFactory} instance.
     * @since 1.2.0
     */
    public static JbatchEntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
