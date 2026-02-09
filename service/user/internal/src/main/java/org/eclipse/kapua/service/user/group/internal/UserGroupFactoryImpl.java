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
package org.eclipse.kapua.service.user.group.internal;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupCreator;
import org.eclipse.kapua.service.user.group.UserGroupFactory;
import org.eclipse.kapua.service.user.group.UserGroupListResult;
import org.eclipse.kapua.service.user.group.UserGroupQuery;

import javax.inject.Singleton;

/**
 * {@link UserGroupFactory} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class UserGroupFactoryImpl implements UserGroupFactory {

    @Override
    public UserGroupCreator newCreator(KapuaId scopeId) {
        return new UserGroupCreatorImpl(scopeId);
    }

    @Override
    public UserGroup newEntity(KapuaId scopeId) {
        return new UserGroupImpl(scopeId);
    }

    @Override
    public UserGroupListResult newListResult() {
        return new UserGroupListResultImpl();
    }

    @Override
    public UserGroupQuery newQuery(KapuaId scopeId) {
        return new UserGroupQueryImpl(scopeId);
    }

    @Override
    public UserGroup clone(UserGroup userGroup) throws KapuaEntityCloneException {
        return UserGroupImpl.parse(userGroup);
    }
}
