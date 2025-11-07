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

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleFactory;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleCreator;
import org.eclipse.kapua.service.authorization.group.GroupRoleFactory;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.group.GroupRoleQuery;

import javax.inject.Singleton;

/**
 * {@link AccessRoleFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class GroupRoleFactoryImpl implements GroupRoleFactory {

    @Override
    public GroupRole newEntity(KapuaId scopeId) {
        return new GroupRoleImpl(scopeId);
    }

    @Override
    public GroupRoleCreator newCreator(KapuaId scopeId) {
        return new GroupRoleCreatorImpl(scopeId);
    }

    @Override
    public GroupRoleQuery newQuery(KapuaId scopeId) {
        return new GroupRoleQueryImpl(scopeId);
    }

    @Override
    public GroupRoleListResult newListResult() {
        return new GroupRoleListResultImpl();
    }

    @Override
    public GroupRole clone(GroupRole accessRole) {
        try {
            return new GroupRoleImpl(accessRole);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, AccessRole.TYPE, accessRole);
        }
    }
}
