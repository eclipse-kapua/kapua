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
package org.eclipse.kapua.service.user.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;

/**
 * {@link UserFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class UserFactoryImpl implements UserFactory {

    @Override
    public UserCreator newCreator(KapuaId scopeId, String name) {
        UserCreator creator = newCreator(scopeId);

        creator.setName(name);

        return creator;
    }

    @Override
    public User newEntity(KapuaId scopeId) {
        return new UserImpl(scopeId);
    }

    @Override
    public UserCreator newCreator(KapuaId scopeId) {
        return new UserCreatorImpl(scopeId);
    }

    @Override
    public User clone(User user) {
        try {
            return new UserImpl(user);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, User.TYPE, user);
        }
    }
}
