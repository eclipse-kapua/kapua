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
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaEntityJpaRepository;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleAttributes;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.storage.TxContext;

public class GroupRoleRepositoryJpa
        extends KapuaEntityJpaRepository<GroupRole, GroupRoleImpl, GroupRoleListResult>
        implements GroupRoleRepository {
    public GroupRoleRepositoryJpa(KapuaJpaRepositoryConfiguration configuration) {
        super(GroupRoleImpl.class, GroupRole.TYPE, GroupRoleListResultImpl::new, configuration);
    }

    @Override
    public GroupRoleListResult findByGroupId(TxContext txContext, KapuaId scopeId, KapuaId groupId) throws KapuaException {
        final GroupRoleListResult res = listSupplier.get();
        res.addItems(doFindAllByField(txContext, scopeId, GroupRoleAttributes.GROUP_ID, groupId));
        return res;
    }
}
