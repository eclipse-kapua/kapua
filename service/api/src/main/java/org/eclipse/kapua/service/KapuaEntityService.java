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
import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;

/**
 * Base {@code interface} for all {@link KapuaService}s that are managing {@link KapuaEntity}es.
 *
 * @param <E>
 *         Type of the {@link KapuaEntity} being managed.
 * @param <C>
 *         Type of the {@link KapuaEntityCreator} being managed.
 * @since 1.0.0
 */
public interface KapuaEntityService<E, C extends KapuaEntityCreator> extends KapuaService {

    /**
     * Creates a {@link KapuaEntity} using information provided in the given {@link KapuaEntityCreator}.
     *
     * @param creator
     *         The {@link KapuaEntityCreator}.
     * @return The newly created {@link KapuaEntity}.
     * @throws KapuaException
     * @since 1.0.0
     */
    E create(@NotNull C creator) throws KapuaException;

    /**
     * Finds a {@link KapuaEntity} identified by its {@link KapuaEntity#getScopeId()} and {@link KapuaEntity#getId()}.
     *
     * @param scopeId
     *         The {@link KapuaEntity#getScopeId()}.
     * @param entityId
     *         The {@link KapuaEntity#getId()}.
     * @return The found {@link KapuaEntity}, or {@code null}.
     * @throws KapuaException
     * @since 1.0.0
     */
    E find(KapuaId scopeId, @NotNull KapuaId entityId) throws KapuaException;

    /**
     * Queries the {@link KapuaEntity}es with the criteria in the given {@link KapuaQuery}.
     *
     * @param query
     *         The {@link KapuaQuery} to filter results.
     * @return The {@link KapuaListResult} matching the {@link KapuaQuery}.
     * @throws KapuaException
     * @since 1.0.0
     */
    KapuaListResult<E> query(@NotNull KapuaQuery query) throws KapuaException;

    /**
     * Counts the {@link KapuaEntity}es with the criteria in the given {@link KapuaQuery}.
     *
     * @param query
     *         The {@link KapuaQuery} to filter the count.
     * @return The count of {@link KapuaEntity}es matching the {@link KapuaQuery}.
     * @throws KapuaException
     * @since 1.0.0
     */
    long count(KapuaQuery query) throws KapuaException;

    /**
     * Deletes a {@link KapuaEntity} identified by its {@link KapuaEntity#getScopeId()} and {@link KapuaEntity#getId()}.
     *
     * @param scopeId
     *         The {@link KapuaEntity#getScopeId()}.
     * @param entityId
     *         The {@link KapuaEntity#getId()}.
     * @throws KapuaException
     * @since 1.0.0
     */
    void delete(KapuaId scopeId, KapuaId entityId) throws KapuaException;
}
