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

/**
 * {@link AccessPermission} service definition.
 *
 * @since 1.0.0
 */
public interface AccessPermissionService extends KapuaEntityService<AccessPermission, AccessPermissionCreator> {

    /**
     * Creates a new {@link AccessPermission} based on the parameters provided in the {@link AccessPermissionCreator}.<br> {@link AccessPermission} must have a unique name within the scope.
     *
     * @param accessPermissionCreator
     *         The creator object from which to create the {@link AccessPermission}.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    AccessPermission create(AccessPermissionCreator accessPermissionCreator) throws KapuaException;

    /**
     * Finds the {@link AccessPermission} by scope identifier and {@link AccessPermission} id.
     *
     * @param scopeId
     *         The scope id in which to search.
     * @param accessPermissionId
     *         The {@link AccessPermission} id to search.
     * @return The {@link AccessPermission} found or {@code null} if no entity was found.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    AccessPermission find(KapuaId scopeId, KapuaId accessPermissionId) throws KapuaException;

    /**
     * Finds the {@link AccessPermission}s by scope identifier and {@link AccessInfo} id.
     *
     * @param scopeId
     *         The scope id in which to search.
     * @param accessInfoId
     *         The {@link AccessInfo} id to search.
     * @return The {@link AccessPermission}s related to the {@link AccessInfo} id.
     * @throws KapuaException
     * @since 1.0.0
     */
    AccessPermissionListResult findByAccessInfoId(KapuaId scopeId, KapuaId accessInfoId) throws KapuaException;

    /**
     * Returns the {@link AccessPermissionListResult} with elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The {@link AccessPermissionListResult} with elements matching the query parameter.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    AccessPermissionListResult query(KapuaQuery query) throws KapuaException;

    /**
     * Returns the count of the {@link AccessPermission} elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The count of the {@link AccessPermission} elements matching the provided query.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    long count(KapuaQuery query) throws KapuaException;

    /**
     * Delete the {@link AccessPermission} by scope id and {@link AccessPermission} id.
     *
     * @param scopeId
     *         The scope id in which to delete.
     * @param accessPermissionId
     *         The {@link AccessPermission} id to delete.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    void delete(KapuaId scopeId, KapuaId accessPermissionId) throws KapuaException;

}
