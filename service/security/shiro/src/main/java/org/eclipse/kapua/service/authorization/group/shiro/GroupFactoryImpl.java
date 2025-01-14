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

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupFactory;

/**
 * {@link GroupFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class GroupFactoryImpl implements GroupFactory {

    @Override
    public GroupCreator newCreator(KapuaId scopeId, String name) {
        GroupCreator creator = newCreator(scopeId);

        creator.setName(name);

        return creator;
    }

    @Override
    public Group newEntity(KapuaId scopeId) {
        return new GroupImpl(scopeId);
    }

    @Override
    public GroupCreator newCreator(KapuaId scopeId) {
        return new GroupCreatorImpl(scopeId);
    }

    @Override
    public Group clone(Group group) {
        try {
            return new GroupImpl(group);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, Group.TYPE, group);
        }
    }
}
