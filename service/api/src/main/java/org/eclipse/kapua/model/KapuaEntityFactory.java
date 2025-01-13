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
package org.eclipse.kapua.model;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;

/**
 * {@link KapuaEntityFactory} definition.
 *
 * @param <E>
 *         The {@link KapuaEntity} for which this {@link KapuaEntityFactory} is for.
 * @param <C>
 *         The {@link KapuaEntityCreator} for which this {@link KapuaEntityFactory} is for.
 * @param <Q>
 *         The {@link KapuaQuery} for which this {@link KapuaEntityFactory} is for.
 * @since 1.0.0
 */
public interface KapuaEntityFactory<E extends KapuaEntity, C extends KapuaEntityCreator<E>, Q extends KapuaQuery> extends KapuaObjectFactory {

    /**
     * Instantiates a new {@link KapuaEntity}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to be set in the {@link KapuaEntity}
     * @return The newly instantiated {@link KapuaEntity}
     * @since 1.0.0
     */
    E newEntity(KapuaId scopeId);

    /**
     * Instantiates a new {@link KapuaEntityCreator}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to be set in the {@link KapuaEntityCreator}
     * @return The newly instantiated {@link KapuaEntityCreator}
     * @since 1.0.0
     */
    C newCreator(KapuaId scopeId);

    /**
     * Instantiates a new {@link KapuaQuery}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to be set in the {@link KapuaQuery}
     * @return The newly instantiated {@link KapuaQuery}
     * @since 1.0.0
     */
    Q newQuery(KapuaId scopeId);

    /**
     * Deeply clones the given {@link KapuaEntity}.
     *
     * @param entity
     *         The {@link KapuaEntity} to be cloned.
     * @return A deep clone of the {@link KapuaEntity}
     * @throws KapuaEntityCloneException
     *         When error occurs while cloning the {@link KapuaEntity}
     * @since 1.1.0
     */
    E clone(E entity) throws KapuaEntityCloneException;
}
