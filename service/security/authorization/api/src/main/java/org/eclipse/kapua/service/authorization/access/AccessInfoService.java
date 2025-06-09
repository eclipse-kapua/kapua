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
package org.eclipse.kapua.service.authorization.access;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.user.User;

/**
 * {@link AccessInfo} service definition.
 *
 * @since 1.0.0
 */
public interface AccessInfoService extends KapuaEntityService<AccessInfo, AccessInfoCreator> {

    /**
     * Creates a new {@link AccessInfo} entity.<br> Is up to the implementation whether or not to check the existence of the referred {@link User} entity.
     *
     * @param accessInfoCreator
     *         The {@link AccessInfoCreator} form which create the {@link AccessInfo}
     * @return The created {@link AccessInfo} entity.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    AccessInfo create(AccessInfoCreator accessInfoCreator)
            throws KapuaException;

    /**
     * Finds the {@link AccessInfo} by scope identifier and {@link AccessInfo} id.
     *
     * @param scopeId
     *         The scope id in which to search.
     * @param userId
     *         The {@link User} id to search.
     * @return The {@link AccessInfo} found or {@code null} if no entity was found.
     * @throws KapuaException
     * @since 1.0.0
     */
    AccessInfo findByUserId(KapuaId scopeId, KapuaId userId)
            throws KapuaException;

    /**
     * Finds the {@link AccessInfo} by scope identifier and {@link User} id.
     *
     * @param scopeId
     *         The scope id in which to search.
     * @param accessInfoId
     *         The {@link AccessInfo} id to search.
     * @return The {@link AccessInfo} found or {@code null} if no entity was found.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    AccessInfo find(KapuaId scopeId, KapuaId accessInfoId)
            throws KapuaException;

    /**
     * Returns the {@link AccessInfoListResult} with elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The {@link AccessInfoListResult} with elements matching the query parameter.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    AccessInfoListResult query(KapuaQuery query)
            throws KapuaException;

    /**
     * Returns the count of the {@link AccessInfo} elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The count of the {@link AccessInfo} elements matching the provided query.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    long count(KapuaQuery query)
            throws KapuaException;

    /**
     * Delete the {@link AccessInfo} by scope id and {@link AccessInfo} id.
     *
     * @param scopeId
     *         The scope id in which to delete.
     * @param accessInfoId
     *         The {@link AccessInfo} id to delete.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    void delete(KapuaId scopeId, KapuaId accessInfoId)
            throws KapuaException;
}
