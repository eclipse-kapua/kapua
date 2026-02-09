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
package org.eclipse.kapua.service.authorization.group;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;

/**
 * {@link GroupRole} {@link KapuaEntityService} definition.
 *
 * @since 2.1.0
 */
public interface GroupRoleService extends KapuaEntityService<GroupRole, GroupRoleCreator> {

    /**
     * Finds the {@link GroupRole}s by scope identifier and {@link Group#getId()}
     *
     * @param scopeId      The scope id in which to search.
     * @param groupId The {@link Group} id to search.
     * @return The {@link GroupRole}s related to the {@link Group#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    GroupRoleListResult findByGroupId(KapuaId scopeId, KapuaId groupId) throws KapuaException;

    /**
     * Returns the {@link GroupRoleListResult} with elements matching the provided query.
     *
     * @param query The {@link GroupRoleQuery} used to filter results.
     * @return The {@link GroupRoleListResult} with elements matching the query parameter.
     * @throws KapuaException
     * @since 2.1.0
     */
    @Override
    GroupRoleListResult query(KapuaQuery query) throws KapuaException;

}
