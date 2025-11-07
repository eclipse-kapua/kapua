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
public class GroupRoleXmlRegistry {

    private final GroupRoleFactory groupRoleFactory = KapuaLocator.getInstance().getFactory(GroupRoleFactory.class);

    public GroupRole newEntity() {
        return groupRoleFactory.newEntity(null);
    }

    public GroupRoleCreator newCreator() {
        return groupRoleFactory.newCreator(null);
    }

    public GroupRoleListResult newListResult() {
        return groupRoleFactory.newListResult();
    }

    public GroupRoleQuery newQuery() {
        return groupRoleFactory.newQuery(null);
    }
}
