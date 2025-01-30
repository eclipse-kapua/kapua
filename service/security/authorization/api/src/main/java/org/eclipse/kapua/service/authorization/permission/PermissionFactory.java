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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.authorization.permission;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.Group;

/**
 * {@link Permission} object factory.
 */
public interface PermissionFactory extends KapuaObjectFactory {

    /**
     * Instantiate a new {@link Permission} implementing object with the provided parameters.
     *
     * @param domain
     *         The {@link Domain} of the new {@link Permission}.
     * @param action
     *         The {@link Actions} of the new {@link Permission}.
     * @param targetScopeId
     *         The target scope id of the new {@link Permission}.
     * @return A instance of the implementing class of {@link Permission}.
     */
    default Permission newPermission(String domain, Actions action, KapuaId targetScopeId) {
        return newPermission(domain, action, targetScopeId, null);
    }

    /**
     * Instantiate a new {@link Permission} implementing object with the provided parameters.
     *
     * @param domain
     *         The {@link Domain} of the new {@link Permission}.
     * @param action
     *         The {@link Actions} of the new {@link Permission}.
     * @param targetScopeId
     *         The target scope id of the new {@link Permission}.
     * @param groupId
     *         The {@link Group} id that this {@link Permission} gives access.
     * @return A instance of the implementing class of {@link Permission}.
     */
    default Permission newPermission(String domain, Actions action, KapuaId targetScopeId, KapuaId groupId) {
        return newPermission(domain, action, targetScopeId, groupId, false);
    }

    /**
     * Instantiate a new {@link Permission} implementing object with the provided parameters.
     *
     * @param domain
     *         The {@link Domain} of the new {@link Permission}.
     * @param action
     *         The {@link Actions} of the new {@link Permission}.
     * @param targetScopeId
     *         The target scope id of the new {@link Permission}.
     * @param groupId
     *         The {@link Group} id that this {@link Permission} gives access.
     * @param forwardable
     *         If the {@link Permission} is forward-able to children scopeIds
     * @return A instance of the implementing class of {@link Permission}.
     */
    Permission newPermission(String domain, Actions action, KapuaId targetScopeId, KapuaId groupId, boolean forwardable);

    /**
     * Instantiate new {@link Permission}s implementing object with the provided parameters.
     *
     * @param domain
     *         The {@link Domain} of the new {@link Permission}.
     * @param targetScopeId
     *         The target scope id of the new {@link Permission}.
     * @param actions
     *         The {@link Actions} of the new {@link Permission}s.
     * @return A collection of instances of the implementing class of {@link Permission}.
     */
    default Collection<Permission> newPermissions(String domain, KapuaId targetScopeId, Actions... actions) {
        return Arrays.stream(actions)
                .map(action -> newPermission(domain, action, targetScopeId, null, false))
                .collect(Collectors.toList());
    }
}
