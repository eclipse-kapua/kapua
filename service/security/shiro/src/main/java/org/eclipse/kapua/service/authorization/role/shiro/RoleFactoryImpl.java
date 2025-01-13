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

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleFactory;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RoleQuery;

/**
 * {@link RoleFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class RoleFactoryImpl implements RoleFactory {

    @Override
    public Role newEntity(KapuaId scopeId) {
        return new RoleImpl(scopeId);
    }

    @Override
    public RoleCreator newCreator(KapuaId scopeId) {
        return new RoleCreatorImpl(scopeId);
    }

    @Override
    public RoleQuery newQuery(KapuaId scopeId) {
        return new RoleQueryImpl(scopeId);
    }

    @Override
    public RolePermission newRolePermission() {
        return new RolePermissionImpl();
    }

    @Override
    public Role clone(Role role) {
        try {
            return new RoleImpl(role);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, Role.TYPE, role);
        }
    }
}
