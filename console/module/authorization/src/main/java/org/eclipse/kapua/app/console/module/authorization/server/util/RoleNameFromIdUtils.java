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
package org.eclipse.kapua.app.console.module.authorization.server.util;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleAttributes;
import org.eclipse.kapua.service.authorization.role.RoleFactory;
import org.eclipse.kapua.service.authorization.role.RoleListResult;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RoleQuery;
import org.eclipse.kapua.service.authorization.role.RoleService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Utils to resolve {@link Role#getId()} into respective {@link Role#getName()}.
 *
 * @since 2.1.0
 */
public class RoleNameFromIdUtils {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final RoleService ROLE_SERVICE = LOCATOR.getService(RoleService.class);
    private static final RoleFactory ROLE_FACTORY = LOCATOR.getFactory(RoleFactory.class);

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    private RoleNameFromIdUtils() {
    }

    /**
     * Creates a map of {@link Role#getId()} and {@link Role#getName()} starting from a {@link KapuaListResult} of {@link RolePermission}s
     *
     * @param rolePermissions The {@link KapuaListResult} of {@link RolePermission}s from which to extract the {@link Role#getId()} to resolve
     * @return A {@link Map} of {@link Role#getId()} associated with the {@link Role#getName()}.
     * @throws Exception
     * @since 2.1.0
     */
    public static Map<KapuaId, String> resolveRoleNamesFrom(GroupRoleListResult rolePermissions) throws Exception {
        Set<KapuaId> roleIds = new HashSet<KapuaId>();

        for (GroupRole groupRole : rolePermissions.getItems()) {
            roleIds.add(groupRole.getRoleId());
        }

        return resolveRoleNameFromIds(roleIds);
    }

    /**
     * Creates a map of {@link Role#getId()} and {@link Role#getName()} starting from a {@link Set} of {@link Role#getId()}
     *
     * @param roleIds The {@link Set} of {@link Role#getId()}s to resolve
     * @return A {@link Map} of {@link Role#getId()} associated with the {@link Role#getName()}.
     * @throws KapuaException
     * @since 2.1.0
     */
    public static HashMap<KapuaId, String> resolveRoleNameFromIds(final Set<KapuaId> roleIds) throws KapuaException {
        // Query to find matching ids
        RoleListResult roles = KapuaSecurityUtils.doPrivileged(new Callable<RoleListResult>() {

            @Override
            public RoleListResult call() throws Exception {
                RoleQuery roleQuery = ROLE_FACTORY.newQuery(KapuaId.ANY);

                roleQuery.setPredicate(
                        roleQuery.attributePredicate(RoleAttributes.ENTITY_ID, roleIds)
                );

                return ROLE_SERVICE.query(roleQuery);
            }
        });

        // Produce map with id and role name association
        HashMap<KapuaId, String> idAndRoleNameMap = new HashMap<KapuaId, String>();
        for (Role role : roles.getItems()) {
            idAndRoleNameMap.put(role.getId(), role.getName());
        }

        // Return the map
        return idAndRoleNameMap;
    }
}
