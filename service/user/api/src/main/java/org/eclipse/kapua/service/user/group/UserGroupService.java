/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user.group;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;
import org.eclipse.kapua.service.authorization.permission.Permission;

import java.util.Set;

/**
 * {@link UserGroup} {@link KapuaService} definition.
 *
 * @since 2.1.0
 */
public interface UserGroupService extends
        KapuaEntityService<UserGroup, UserGroupCreator>,
        KapuaUpdatableEntityService<UserGroup> {

    @Override
    UserGroupListResult query(KapuaQuery query) throws KapuaException;

    /**
     * Fetch all {@link Permission}s linked to the given {@link UserGroup}
     *
     * @param scopeId The {@link UserGroup#getScopeId()}
     * @param userGroupId The {@link UserGroup#getId()}
     * @return The Set of {@link Permission} retrieved
     * @throws KapuaException
     * @since 2.1.0
     */
    Set<Permission> fetchPermissions(KapuaId scopeId, KapuaId userGroupId) throws KapuaException;
}
