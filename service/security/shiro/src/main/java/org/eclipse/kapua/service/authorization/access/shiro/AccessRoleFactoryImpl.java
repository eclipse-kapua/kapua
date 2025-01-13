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
package org.eclipse.kapua.service.authorization.access.shiro;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.eclipse.kapua.service.authorization.access.AccessRoleFactory;
import org.eclipse.kapua.service.authorization.access.AccessRoleQuery;

/**
 * {@link AccessRoleFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessRoleFactoryImpl implements AccessRoleFactory {

    @Override
    public AccessRole newEntity(KapuaId scopeId) {
        return new AccessRoleImpl(scopeId);
    }

    @Override
    public AccessRoleCreator newCreator(KapuaId scopeId) {
        return new AccessRoleCreatorImpl(scopeId);
    }

    @Override
    public AccessRoleQuery newQuery(KapuaId scopeId) {
        return new AccessRoleQueryImpl(scopeId);
    }

    @Override
    public AccessRole clone(AccessRole accessRole) {
        try {
            return new AccessRoleImpl(accessRole);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, AccessRole.TYPE, accessRole);
        }
    }
}
