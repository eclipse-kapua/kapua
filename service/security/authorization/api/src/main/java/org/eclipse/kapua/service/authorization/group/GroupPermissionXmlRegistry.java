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
package org.eclipse.kapua.service.authorization.group;

import org.eclipse.kapua.locator.KapuaLocator;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class GroupPermissionXmlRegistry {

    private final GroupPermissionFactory groupPermissionFactory = KapuaLocator.getInstance().getFactory(GroupPermissionFactory.class);

    /**
     * Creates a new {@link GroupPermissionCreator} instance
     *
     * @return
     */
    public GroupPermissionCreator newCreator() {
        return groupPermissionFactory.newCreator(null);
    }

    /**
     * Creates a new {@link GroupPermission} instance
     *
     * @return
     */
    public GroupPermission newEntity() {
        return groupPermissionFactory.newEntity(null);
    }

    /**
     * Creates a new {@link GroupPermissionListResult} instance
     *
     * @return
     */
    public GroupPermissionListResult newListResult() {
        return groupPermissionFactory.newListResult();
    }

    public GroupPermissionQuery newQuery() {
        return groupPermissionFactory.newQuery(null);
    }
}
