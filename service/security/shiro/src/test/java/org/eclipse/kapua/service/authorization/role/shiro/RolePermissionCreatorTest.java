/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authorization.role.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.RolePermissionCreator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class RolePermissionCreatorTest {

    @Test
    public void rolePermissionCreatorTest() {
        KapuaId[] scopeIds = { null, KapuaId.ANY };
        for (KapuaId scopeId : scopeIds) {
            RolePermissionCreator rolePermissionCreator = new RolePermissionCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, rolePermissionCreator.getScopeId());
            Assert.assertNull("Null expected.", rolePermissionCreator.getRoleId());
            Assert.assertNull("Null expected.", rolePermissionCreator.getPermission());
        }
    }

    @Test
    public void setAndGetRoleIdTest() {
        KapuaId[] roleIds = { null, KapuaId.ONE };
        RolePermissionCreator rolePermissionCreator = new RolePermissionCreator(KapuaId.ONE);

        for (KapuaId roleId : roleIds) {
            rolePermissionCreator.setRoleId(roleId);
            Assert.assertEquals("Expected and actual values should be the same.", roleId, rolePermissionCreator.getRoleId());
        }
    }

    @Test
    public void setAndGetPermissionTest() {
        Permission[] permissions = { null, Mockito.mock(Permission.class) };
        RolePermissionCreator rolePermissionCreator = new RolePermissionCreator(KapuaId.ONE);

        for (Permission permission : permissions) {
            rolePermissionCreator.setPermission(permission);
            Assert.assertEquals("Expected and actual values should be the same.", permission, rolePermissionCreator.getPermission());
        }
    }
}