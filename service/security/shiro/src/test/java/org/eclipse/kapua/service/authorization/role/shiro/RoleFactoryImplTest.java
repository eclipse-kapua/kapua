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

import java.util.Date;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class RoleFactoryImplTest {

    RoleFactoryImpl roleFactoryImpl;
    KapuaId scopeId;
    Role role;
    Date createdOn, modifiedOn;

    @Before
    public void initialize() {
        roleFactoryImpl = new RoleFactoryImpl();
        scopeId = KapuaId.ONE;
        createdOn = new Date();
        modifiedOn = new Date();
        role = Mockito.mock(Role.class);

        Mockito.when(role.getName()).thenReturn("role name");
        Mockito.when(role.getDescription()).thenReturn("role description");
        Mockito.when(role.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(role.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(role.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(role.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(role.getModifiedBy()).thenReturn(KapuaId.ANY);
        Mockito.when(role.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(role.getOptlock()).thenReturn(11);
    }

    @Test
    public void newEntityTest() {
        Assert.assertTrue("True expected.", roleFactoryImpl.newEntity(scopeId) instanceof Role);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, roleFactoryImpl.newEntity(scopeId).getScopeId());
    }

    @Test
    public void newEntityNullTest() {
        Assert.assertTrue("True expected.", roleFactoryImpl.newEntity(null) instanceof Role);
        Assert.assertNull("Null expected.", roleFactoryImpl.newEntity(null).getScopeId());
    }

    @Test
    public void newCreatorTest() {
        Assert.assertTrue("True expected.", roleFactoryImpl.newCreator(scopeId) instanceof RoleCreator);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, roleFactoryImpl.newCreator(scopeId).getScopeId());
    }

    @Test
    public void newCreatorNullTest() {
        Assert.assertTrue("True expected.", roleFactoryImpl.newCreator(null) instanceof RoleCreator);
        Assert.assertNull("Null expected.", roleFactoryImpl.newCreator(null).getScopeId());
    }

    @Test
    public void newRolePermissionTest() {
        Assert.assertTrue("True expected.", roleFactoryImpl.newRolePermission() instanceof RolePermission);
    }

    @Test
    public void cloneTest() {
        Role resultRole = roleFactoryImpl.clone(role);

        Assert.assertEquals("Expected and actual values should be the same.", "role name", resultRole.getName());
        Assert.assertEquals("Expected and actual values should be the same.", "role description", resultRole.getDescription());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultRole.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultRole.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultRole.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, resultRole.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultRole.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, resultRole.getModifiedOn());
        Assert.assertEquals("Expected and actual values should be the same.", 11, resultRole.getOptlock());
    }

    @Test
    public void cloneNullTest() {
        try {
            roleFactoryImpl.clone(null);
        } catch (Exception e) {
            Assert.assertEquals("Expected and actual values should be the same", "org.eclipse.kapua.KapuaEntityCloneException: Severe error while cloning: role", e.toString());
        }
    }
}