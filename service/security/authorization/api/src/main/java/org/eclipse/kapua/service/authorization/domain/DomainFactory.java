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
package org.eclipse.kapua.service.authorization.domain;

import org.eclipse.kapua.model.KapuaEntityFactory;

/**
 * {@link DomainFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface DomainFactory extends KapuaEntityFactory<Domain, DomainCreator> {

    /**
     * Instantiates a new {@link DomainCreator}
     *
     * @param name
     *         The name to set into the {@link Domain}.
     * @return The newly instantiated {@link Domain}.
     * @since 1.0.0
     */
    DomainCreator newCreator(String name);

}
