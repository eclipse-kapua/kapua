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
package org.eclipse.kapua.service.authorization.role;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

@XmlRegistry
public class RolePermissionXmlRegistry {

    private final RolePermissionFactory rolePermissionFactory = KapuaLocator.getInstance().getFactory(RolePermissionFactory.class);

    /**
     * Creates a new {@link RolePermission} instance
     *
     * @return
     */
    public RolePermission newRolePermission() {
        return rolePermissionFactory.newEntity(null);
    }
}
