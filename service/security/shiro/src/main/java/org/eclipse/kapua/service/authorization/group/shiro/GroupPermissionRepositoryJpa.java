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
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaEntityJpaRepository;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionAttributes;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionQuery;
import org.eclipse.kapua.storage.TxContext;

public class GroupPermissionRepositoryJpa
        extends KapuaEntityJpaRepository<GroupPermission, GroupPermissionImpl, GroupPermissionListResult>
        implements GroupPermissionRepository {

    public GroupPermissionRepositoryJpa(KapuaJpaRepositoryConfiguration configuration) {
        super(GroupPermissionImpl.class, AccessPermission.TYPE, GroupPermissionListResultImpl::new, configuration);
    }

    @Override
    public GroupPermissionListResult findByGroupId(TxContext tx, KapuaId scopeId, KapuaId groupId) throws KapuaException {
        GroupPermissionQuery query = new GroupPermissionQueryImpl(scopeId);
        query.setPredicate(query.attributePredicate(GroupPermissionAttributes.GROUP_ID, groupId));
        return this.query(tx, query);
    }
}
