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
package org.eclipse.kapua.service.user;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link UserFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface UserFactory extends KapuaEntityFactory<User, UserCreator, UserQuery> {

    /**
     * Instantiates a new {@link UserCreator}
     *
     * @param scopedId
     *         The scope {@link KapuaId} to set into the {@link UserCreator}.
     * @param name
     *         The name to set into the {@link UserCreator}.
     * @return The newly instantiated {@link UserCreator}
     * @since 1.0.0
     */
    UserCreator newCreator(KapuaId scopedId, String name);

}
