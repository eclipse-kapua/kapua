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
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupAttributes;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Utils to resolve {@link Permission#getGroupId()} into respective {@link Group#getName()}.
 *
 * @since 2.1.0
 */
public class GroupNameFromIdUtils {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final GroupService GROUP_SERVICE = LOCATOR.getService(GroupService.class);
    private static final GroupFactory GROUP_FACTORY = LOCATOR.getFactory(GroupFactory.class);

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    private GroupNameFromIdUtils() {
    }

    /**
     * Creates a map of {@link Group#getId()} and {@link Group#getName()} starting from a {@link KapuaListResult} of {@link GroupPermission}s
     *
     * @param groupPermissions The {@link KapuaListResult} of {@link GroupPermission}s from which to extract the {@link Group#getId()} to resolve
     * @return A {@link Map} of {@link Group#getId()} associated with the {@link Group#getName()}.
     * @throws Exception
     * @since 2.1.0
     */
    public static Map<KapuaId, String> resolveGroupNamesFrom(KapuaListResult<GroupPermission> groupPermissions) throws Exception {
        Set<KapuaId> groupIds = new HashSet<KapuaId>();

        for (GroupPermission groupPermission : groupPermissions.getItems()) {
            groupIds.add(groupPermission.getGroupId());
            groupIds.add(groupPermission.getPermission().getGroupId());
        }

        return resolveGroupNameFromIds(groupIds);
    }

    /**
     * Creates a map of {@link Group#getId()} and {@link Group#getName()} starting from a {@link Set} of {@link Group#getId()}
     *
     * @param groupIds The {@link Set} of {@link Group#getId()}s to resolve
     * @return A {@link Map} of {@link Group#getId()} associated with the {@link Group#getName()}.
     * @throws KapuaException
     * @since 2.1.0
     */
    public static HashMap<KapuaId, String> resolveGroupNameFromIds(final Set<KapuaId> groupIds) throws KapuaException {
        // Query to find matching ids
        GroupListResult groups = KapuaSecurityUtils.doPrivileged(new Callable<GroupListResult>() {

            @Override
            public GroupListResult call() throws Exception {
                GroupQuery groupQuery = GROUP_FACTORY.newQuery(KapuaId.ANY);

                groupQuery.setPredicate(
                        groupQuery.attributePredicate(GroupAttributes.ENTITY_ID, groupIds)
                );

                return GROUP_SERVICE.query(groupQuery);
            }
        });

        // Produce map with id and group name association
        HashMap<KapuaId, String> idAndGroupNameMap = new HashMap<KapuaId, String>();
        for (Group group : groups.getItems()) {
            idAndGroupNameMap.put(group.getId(), group.getName());
        }

        // Return the map
        return idAndGroupNameMap;
    }
}
