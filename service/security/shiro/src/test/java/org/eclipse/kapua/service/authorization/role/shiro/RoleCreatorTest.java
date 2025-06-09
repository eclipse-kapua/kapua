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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(JUnitTests.class)
public class RoleCreatorTest {

    RoleCreator roleCreatorImpl;
    Set<Permission> permissions;
    Permission permission1, permission2;

    @Before
    public void initialize() {
        roleCreatorImpl = new RoleCreator(KapuaId.ONE);
        permissions = new HashSet<>();
        permission1 = Mockito.mock(Permission.class);
        permission2 = Mockito.mock(Permission.class);
    }

    @Test
    public void roleCreatorImplTest() {
        KapuaId[] scopeIds = {KapuaId.ONE, null};
        for (KapuaId scopeId : scopeIds) {
            RoleCreator roleCreatorImpl = new RoleCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, roleCreatorImpl.getScopeId());
            Assert.assertTrue("True expected.", roleCreatorImpl.getPermissions().isEmpty());
        }
    }

    @Test
    public void setAndGetEmptyPermissionsTest() {
        roleCreatorImpl.setPermissions(permissions);
        Assert.assertTrue("True expected.", roleCreatorImpl.getPermissions().isEmpty());
    }

    @Test
    public void setAndGetPermissionsTest() {
        permissions.add(permission1);
        permissions.add(permission2);
        permissions.add(null);

        roleCreatorImpl.setPermissions(permissions);
        Assert.assertFalse("False expected.", roleCreatorImpl.getPermissions().isEmpty());
        Assert.assertEquals("Expected and actual values should be the same.", permissions, roleCreatorImpl.getPermissions());
        Assert.assertEquals("Expected and actual values should be the same.", 3, roleCreatorImpl.getPermissions().size());
    }

    @Test
    public void setAndGetNullPermissionsTest() {
        roleCreatorImpl.setPermissions(null);
        Assert.assertTrue("True expected.", roleCreatorImpl.getPermissions().isEmpty());
    }
}