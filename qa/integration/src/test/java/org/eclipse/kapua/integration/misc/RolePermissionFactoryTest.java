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
package org.eclipse.kapua.integration.misc;

import java.util.Date;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionFactoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class RolePermissionFactoryTest {

    RolePermissionFactoryImpl rolePermissionFactoryImpl;
    KapuaId scopeId;
    RolePermission rolePermission;
    Date createdOn, modifiedOn;
    PermissionImpl permission;

    @Before
    public void initialize() {
        rolePermissionFactoryImpl = new RolePermissionFactoryImpl();
        scopeId = KapuaId.ONE;
        createdOn = new Date();
        modifiedOn = new Date();
        rolePermission = Mockito.mock(RolePermission.class);
        permission = Mockito.mock(PermissionImpl.class);

        Mockito.when(rolePermission.getId()).thenReturn(KapuaId.ANY);
        Mockito.when(rolePermission.getRoleId()).thenReturn(KapuaId.ONE);
        Mockito.when(rolePermission.getPermission()).thenReturn(permission);
        Mockito.when(rolePermission.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(rolePermission.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(rolePermission.getCreatedOn()).thenReturn(createdOn);
    }

    @Test
    public void newEntityTest() {
        Assert.assertTrue("True expected.", rolePermissionFactoryImpl.newEntity(scopeId) instanceof RolePermission);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, rolePermissionFactoryImpl.newEntity(scopeId).getScopeId());
    }

    @Test
    public void newEntityNullTest() {
        Assert.assertTrue("True expected.", rolePermissionFactoryImpl.newEntity(null) instanceof RolePermission);
        Assert.assertNull("Null expected.", rolePermissionFactoryImpl.newEntity(null).getScopeId());
    }

    @Test
    public void cloneTest() {
        RolePermission resultRolePermission = rolePermissionFactoryImpl.clone(rolePermission);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultRolePermission.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultRolePermission.getRoleId());
        Assert.assertEquals("Expected and actual values should be the same.", permission, resultRolePermission.getPermission());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultRolePermission.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultRolePermission.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, resultRolePermission.getCreatedOn());
    }

    @Test(expected = NullPointerException.class)
    public void cloneNullTest() {
        rolePermissionFactoryImpl.clone(null);
    }
}