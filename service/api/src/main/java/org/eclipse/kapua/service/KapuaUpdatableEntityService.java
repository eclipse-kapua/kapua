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
package org.eclipse.kapua.service;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaUpdatableEntity;

/**
 * Base {@code interface} for all {@link KapuaService}s that are managing {@link KapuaUpdatableEntity}es.
 *
 * @param <E>
 *         Type of the {@link KapuaEntity} being managed.
 * @since 1.0
 */
public interface KapuaUpdatableEntityService<E> extends KapuaService {

    /**
     * Updates the given {@link KapuaUpdatableEntity}.
     *
     * @param entity
     *         The {@link KapuaUpdatableEntity}.
     * @return The updated {@link KapuaUpdatableEntity}.
     * @throws KapuaException
     * @since 1.0.0
     */
    E update(@NotNull E entity) throws KapuaException;
}
