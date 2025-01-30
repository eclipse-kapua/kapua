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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.shiro.AccessPermissionImpl;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class AccessPermissionImplTest {

    AccessPermissionImpl accessPermissionImpl1, accessPermissionImpl2, accessPermissionImpl;
    AccessPermission accessPermission;
    Permission permission1, permission2;
    Permission newPermission;
    Date createdOn;

    @Before
    public void initialize() {
        accessPermissionImpl1 = new AccessPermissionImpl(KapuaId.ONE);
        accessPermissionImpl2 = new AccessPermissionImpl(KapuaId.ONE);
        accessPermission = Mockito.mock(AccessPermission.class);
        permission1 = new Permission("domain", Actions.connect, KapuaId.ONE, KapuaId.ANY, false);
        permission2 = new Permission("another", null, null);
        newPermission = Mockito.mock(Permission.class);
        createdOn = new Date();

        Mockito.when(accessPermission.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessPermission.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(accessPermission.getCreatedBy()).thenReturn(KapuaId.ANY);
        Mockito.when(accessPermission.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(accessPermission.getAccessInfoId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessPermission.getPermission()).thenReturn(permission1);

        accessPermissionImpl = new AccessPermissionImpl(accessPermission);
    }

    @Test
    public void accessPermissionImplWithoutParametersTest() throws Exception {
        Constructor<AccessPermissionImpl> accessPermissionImplWithoutParameters = AccessPermissionImpl.class.getDeclaredConstructor();
        accessPermissionImplWithoutParameters.setAccessible(true);
        accessPermissionImplWithoutParameters.newInstance();
        Assert.assertTrue("True expected.", Modifier.isProtected(accessPermissionImplWithoutParameters.getModifiers()));
    }

    @Test
    public void accessPermissionImplScopeIdParameterTest() {
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessPermissionImpl1.getScopeId());
        Assert.assertNull("Null expected", accessPermissionImpl1.getAccessInfoId());
        Assert.assertEquals("Expected and actual values should be the same.", "*:*:*:*", accessPermissionImpl1.getPermission().toString());
        Assert.assertNull("Null expected", accessPermissionImpl1.getPermission().getDomain());
        Assert.assertNull("Null expected", accessPermissionImpl1.getPermission().getAction());
        Assert.assertNull("Null expected", accessPermissionImpl1.getPermission().getTargetScopeId());
        Assert.assertNull("Null expected", accessPermissionImpl1.getPermission().getGroupId());
    }

    @Test
    public void accessPermissionImplNullScopeIdParameterTest() {
        AccessPermissionImpl accessPermission = new AccessPermissionImpl((KapuaId) null);

        Assert.assertNull("Null expected", accessPermission.getScopeId());
        Assert.assertNull("Null expected", accessPermission.getAccessInfoId());
        Assert.assertEquals("Expected and actual values should be the same.", "*:*:*:*", accessPermission.getPermission().toString());
        Assert.assertNull("Null expected", accessPermission.getPermission().getDomain());
        Assert.assertNull("Null expected", accessPermission.getPermission().getAction());
        Assert.assertNull("Null expected", accessPermission.getPermission().getTargetScopeId());
        Assert.assertNull("Null expected", accessPermission.getPermission().getGroupId());
    }

    @Test
    public void accessPermissionImplAccessPermissionParameterTest() {
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessPermissionImpl.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessPermissionImpl.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessPermissionImpl.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, accessPermissionImpl.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessPermissionImpl.getAccessInfoId());
        Assert.assertEquals("Expected and actual values should be the same.", permission1, accessPermissionImpl.getPermission());
        Assert.assertEquals("Expected and actual values should be the same.", "domain", accessPermissionImpl.getPermission().getDomain());
        Assert.assertEquals("Expected and actual values should be the same.", Actions.connect, accessPermissionImpl.getPermission().getAction());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessPermissionImpl.getPermission().getTargetScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessPermissionImpl.getPermission().getGroupId());
    }

    @Test(expected = NullPointerException.class)
    public void accessPermissionImplNullAccessPermissionParameterTest() {
        new AccessPermissionImpl((AccessPermission) null);
    }

    @Test
    public void setAndGetAccessInfoIdTest() {
        accessPermissionImpl1.setAccessInfoId(KapuaId.ANY);
        Assert.assertEquals("Expected and actual values should be the same.", new KapuaEid(KapuaId.ANY), accessPermissionImpl1.getAccessInfoId());
        accessPermissionImpl1.setAccessInfoId(new KapuaEid(KapuaId.ANY));
        Assert.assertEquals("Expected and actual values should be the same.", new KapuaEid(KapuaId.ANY), accessPermissionImpl1.getAccessInfoId());
        accessPermissionImpl1.setAccessInfoId(null);
        Assert.assertNull("Null expected.", accessPermissionImpl1.getAccessInfoId());

        accessPermissionImpl.setAccessInfoId(KapuaId.ANY);
        Assert.assertEquals("Expected and actual values should be the same.", new KapuaEid(KapuaId.ANY), accessPermissionImpl.getAccessInfoId());
        accessPermissionImpl.setAccessInfoId(new KapuaEid(KapuaId.ANY));
        Assert.assertEquals("Expected and actual values should be the same.", new KapuaEid(KapuaId.ANY), accessPermissionImpl.getAccessInfoId());
        accessPermissionImpl.setAccessInfoId(null);
        Assert.assertNull("Null expected.", accessPermissionImpl.getAccessInfoId());

    }

    @Test
    public void setAndGetPermissionTest() {
        accessPermissionImpl1.setPermission(permission1);
        Assert.assertEquals("Expected and actual values should be the same.", permission1, accessPermissionImpl1.getPermission());
        accessPermissionImpl1.setPermission(null);
        Assert.assertEquals("Expected and actual values should be the same.", "*:*:*:*", accessPermissionImpl1.getPermission().toString());
        accessPermissionImpl1.setPermission(newPermission);
        Assert.assertEquals("Expected and actual values should be the same.", "*:*:*:*", accessPermissionImpl1.getPermission().toString());

        accessPermissionImpl.setPermission(permission2);
        Assert.assertEquals("Expected and actual values should be the same.", permission2, accessPermissionImpl.getPermission());
        accessPermissionImpl.setPermission(null);
        Assert.assertEquals("Expected and actual values should be the same.", "*:*:*:*", accessPermissionImpl.getPermission().toString());
        accessPermissionImpl.setPermission(newPermission);
        Assert.assertEquals("Expected and actual values should be the same.", "*:*:*:*", accessPermissionImpl.getPermission().toString());
    }

    @Test
    public void hashCodeNullAccessInfoIdNullPermissionTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 961, accessPermissionImpl1.hashCode());
    }

    @Test
    public void hashCodeNullPermissionTest() {
        accessPermissionImpl1.setAccessInfoId(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", 1953, accessPermissionImpl1.hashCode());
    }

    @Test
    public void hashCodeNullAccessInfoIdTest() {
        Permission permission = Mockito.mock(Permission.class);
        Mockito.when(permission.getDomain()).thenReturn(null);
        Mockito.when(permission.getAction()).thenReturn(null);
        Mockito.when(permission.getTargetScopeId()).thenReturn(null);
        Mockito.when(permission.getGroupId()).thenReturn(null);
        accessPermissionImpl1.setPermission(permission);
        Assert.assertEquals("Expected and actual values should be the same.", 924482, accessPermissionImpl1.hashCode());
    }

    @Test
    public void hashCodeTest() {
        accessPermissionImpl1.setAccessInfoId(KapuaId.ONE);
        Permission permission = Mockito.mock(Permission.class);
        Mockito.when(permission.getDomain()).thenReturn(null);
        Mockito.when(permission.getAction()).thenReturn(null);
        Mockito.when(permission.getTargetScopeId()).thenReturn(null);
        Mockito.when(permission.getGroupId()).thenReturn(null);
        accessPermissionImpl1.setPermission(permission);
        Assert.assertEquals("Expected and actual values should be the same.", 925474, accessPermissionImpl1.hashCode());
    }

    @Test
    public void equalsSameObjectTest() {
        Assert.assertTrue("True expected.", accessPermissionImpl1.equals(accessPermissionImpl1));
    }

    @Test
    public void equalsNullObjectTest() {
        Assert.assertFalse("False expected.", accessPermissionImpl1.equals(null));
    }

    @Test
    public void equalsObjectTest() {
        Assert.assertFalse("False expected.", accessPermissionImpl1.equals(new Object()));
    }

    @Test
    public void equalsNullBothAccessInfoIdsNullPermissionsTest() {
        Assert.assertTrue("True expected.", accessPermissionImpl1.equals(accessPermissionImpl2));
    }

    @Test
    public void equalsNullBothAccessInfoIdsEqualPermissionsTest() {
        accessPermissionImpl1.setPermission(permission2);
        accessPermissionImpl2.setPermission(permission2);
        Assert.assertTrue("True expected.", accessPermissionImpl1.equals(accessPermissionImpl2));
    }

    @Test
    public void equalsNullBothAccessInfoIdsDifferentPermissionsTest() {
        accessPermissionImpl1.setPermission(permission2);
        Assert.assertFalse("False expected.", accessPermissionImpl1.equals(accessPermissionImpl2));
    }

    @Test
    public void equalsNullAccessInfoIdTest() {
        accessPermissionImpl2.setAccessInfoId(KapuaId.ANY);
        Assert.assertFalse("False expected.", accessPermissionImpl1.equals(accessPermissionImpl2));
    }

    @Test
    public void equalsDifferentAccessInfoIdsTest() {
        accessPermissionImpl1.setAccessInfoId(KapuaId.ONE);
        accessPermissionImpl2.setAccessInfoId(KapuaId.ANY);
        Assert.assertFalse("False expected.", accessPermissionImpl1.equals(accessPermissionImpl2));
    }

    @Test
    public void equalsEqualAccessInfoIdsNullPermissionsTest() {
        accessPermissionImpl1.setAccessInfoId(KapuaId.ONE);
        accessPermissionImpl2.setAccessInfoId(KapuaId.ONE);
        Assert.assertTrue("True expected.", accessPermissionImpl1.equals(accessPermissionImpl2));
    }

    @Test
    public void equalsEqualAccessInfoIdsEqualPermissionsTest() {
        accessPermissionImpl1.setAccessInfoId(KapuaId.ONE);
        accessPermissionImpl2.setAccessInfoId(KapuaId.ONE);
        accessPermissionImpl1.setPermission(permission2);
        accessPermissionImpl2.setPermission(permission2);
        Assert.assertTrue("True expected.", accessPermissionImpl1.equals(accessPermissionImpl2));
    }

    @Test
    public void equalsEqualAccessInfoIdsDifferentPermissionsTest() {
        accessPermissionImpl1.setAccessInfoId(KapuaId.ONE);
        accessPermissionImpl2.setAccessInfoId(KapuaId.ONE);
        accessPermissionImpl1.setPermission(new Permission("another", null, null));
        Assert.assertFalse("False expected.", accessPermissionImpl1.equals(accessPermissionImpl2));
    }
}