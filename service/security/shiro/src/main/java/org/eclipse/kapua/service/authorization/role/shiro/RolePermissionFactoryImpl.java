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
package org.eclipse.kapua.service.authorization.role.shiro;

import javax.inject.Singleton;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionCreator;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionQuery;

/**
 * {@link RolePermissionFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class RolePermissionFactoryImpl implements RolePermissionFactory {

    @Override
    public RolePermission newEntity(KapuaId scopeId) {
        return new RolePermissionImpl(scopeId);
    }

    @Override
    public RolePermissionCreator newCreator(KapuaId scopeId) {
        return new RolePermissionCreatorImpl(scopeId);
    }

    @Override
    public RolePermissionQuery newQuery(KapuaId scopeId) {
        return new RolePermissionQueryImpl(scopeId);
    }

    @Override
    public RolePermission clone(RolePermission rolePermission) {
        return new RolePermissionImpl(rolePermission);
    }
}
