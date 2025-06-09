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
package org.eclipse.kapua.service.authorization.role;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;

/**
 * {@link RolePermission} service definition.
 *
 * @since 1.0.0
 */
public interface RolePermissionService extends KapuaEntityService<RolePermission, RolePermissionCreator> {

    /**
     * Creates a new {@link RolePermission} based on the parameters provided in the {@link RolePermissionCreator}.<br> {@link RolePermission} must have a unique name within the scope.
     *
     * @param rolePermissionCreator
     *         The creator object from which to create the {@link RolePermission}.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    RolePermission create(RolePermissionCreator rolePermissionCreator) throws KapuaException;

    /**
     * Finds the {@link RolePermission} by scope identifier and {@link RolePermission} id.
     *
     * @param scopeId
     *         The scope id in which to search.
     * @param rolePermissionId
     *         The {@link RolePermission} id to search.
     * @return The {@link RolePermission} found or {@code null} if no entity was found.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    RolePermission find(KapuaId scopeId, KapuaId rolePermissionId) throws KapuaException;

    /**
     * Finds the {@link RolePermission}s by scope identifier and {@link Role} id.
     *
     * @param scopeId
     *         The scope id in which to search.
     * @param roleId
     *         The {@link Role} id to search.
     * @return The {@link RolePermission}s related to the {@link Role} id.
     * @throws KapuaException
     * @since 1.0.0
     */
    RolePermissionListResult findByRoleId(KapuaId scopeId, KapuaId roleId) throws KapuaException;

    /**
     * Returns the {@link RolePermissionListResult} with elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The {@link RolePermissionListResult} with elements matching the query parameter.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    RolePermissionListResult query(KapuaQuery query) throws KapuaException;

    /**
     * Returns the count of the {@link RolePermission} elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The count of the {@link RolePermission} elements matching the provided query.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    long count(KapuaQuery query) throws KapuaException;

    /**
     * Delete the {@link RolePermission} by scope id and {@link RolePermission} id.
     *
     * @param scopeId
     *         The scope id in which to delete.
     * @param rolePermissionId
     *         The {@link RolePermission} id to delete.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    void delete(KapuaId scopeId, KapuaId rolePermissionId) throws KapuaException;

}
