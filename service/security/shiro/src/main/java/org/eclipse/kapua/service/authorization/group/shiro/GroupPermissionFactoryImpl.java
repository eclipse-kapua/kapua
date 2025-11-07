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

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionCreator;
import org.eclipse.kapua.service.authorization.group.GroupPermissionFactory;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionQuery;

import javax.inject.Singleton;

/**
 * {@link GroupPermissionFactory} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class GroupPermissionFactoryImpl implements GroupPermissionFactory {

    @Override
    public GroupPermission newEntity(KapuaId scopeId) {
        return new GroupPermissionImpl(scopeId);
    }

    @Override
    public GroupPermissionCreator newCreator(KapuaId scopeId) {
        return new GroupPermissionCreatorImpl(scopeId);
    }

    @Override
    public GroupPermissionQuery newQuery(KapuaId scopeId) {
        return new GroupPermissionQueryImpl(scopeId);
    }

    @Override
    public GroupPermissionListResult newListResult() {
        return new GroupPermissionListResultImpl();
    }

    @Override
    public GroupPermission clone(GroupPermission accessPermission) {
        return new GroupPermissionImpl(accessPermission);
    }
}
