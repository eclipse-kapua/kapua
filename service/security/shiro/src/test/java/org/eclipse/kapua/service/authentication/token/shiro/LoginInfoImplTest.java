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
package org.eclipse.kapua.service.authentication.token.shiro;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;


@Category(JUnitTests.class)
public class LoginInfoImplTest {

    LoginInfoImpl loginInfoImpl;
    Set<RolePermission> rolePermissions;
    Set<AccessPermission> accessPermissions;
    Set<GroupPermission> groupPermissions;

    @Before
    public void initialize() {
        loginInfoImpl = new LoginInfoImpl();

        rolePermissions = new HashSet<>();
        accessPermissions = new HashSet<>();
        groupPermissions = new HashSet<>();
    }

    @Test
    public void setAndGetAccessTokenTest() {
        AccessToken[] accessTokens = {null, Mockito.mock(AccessToken.class)};
        Assert.assertNull("Null expected", loginInfoImpl.getAccessToken());
        for (AccessToken accessToken : accessTokens) {
            loginInfoImpl.setAccessToken(accessToken);
            Assert.assertEquals("Expected and actual values should be the same.", accessToken, loginInfoImpl.getAccessToken());
        }
    }

    @Test
    public void setAndGetRolePermissionEmptySetTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getRolePermission().isEmpty());
        loginInfoImpl.setRolePermission(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfoImpl.getRolePermission());
    }

    @Test
    public void setAndGetRolePermissionTest() {
        rolePermissions.add(Mockito.mock(RolePermission.class));
        rolePermissions.add(Mockito.mock(RolePermission.class));

        Assert.assertTrue("Empty expected", loginInfoImpl.getRolePermission().isEmpty());
        loginInfoImpl.setRolePermission(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfoImpl.getRolePermission());
    }

    @Test
    public void setAndGetRolePermissionNullTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getRolePermission().isEmpty());
        loginInfoImpl.setRolePermission(null);
        Assert.assertTrue("Empty expected", loginInfoImpl.getRolePermission().isEmpty());
    }

    @Test
    public void setAndGetAccessPermissionEmptySetTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getAccessPermission().isEmpty());
        loginInfoImpl.setAccessPermission(accessPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", accessPermissions, loginInfoImpl.getAccessPermission());
    }

    @Test
    public void setAndGetAccessPermissionTest() {
        accessPermissions.add(Mockito.mock(AccessPermission.class));
        accessPermissions.add(Mockito.mock(AccessPermission.class));

        Assert.assertTrue("Empty expected", loginInfoImpl.getAccessPermission().isEmpty());
        loginInfoImpl.setAccessPermission(accessPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", accessPermissions, loginInfoImpl.getAccessPermission());
    }

    @Test
    public void setAndGetAccessPermissionNullTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getAccessPermission().isEmpty());
        loginInfoImpl.setAccessPermission(null);
        Assert.assertTrue("Empty expected", loginInfoImpl.getAccessPermission().isEmpty());
    }

    @Test
    public void setAndGetGroupRolePermissionEmptySetTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupRolePermission().isEmpty());
        loginInfoImpl.setGroupRolePermission(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfoImpl.getGroupRolePermission());
    }

    @Test
    public void setAndGetGroupRolePermissionTest() {
        rolePermissions.add(Mockito.mock(RolePermission.class));
        rolePermissions.add(Mockito.mock(RolePermission.class));

        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupRolePermission().isEmpty());
        loginInfoImpl.setGroupRolePermission(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfoImpl.getGroupRolePermission());
    }

    @Test
    public void setAndGetGroupRolePermissionNullTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupRolePermission().isEmpty());
        loginInfoImpl.setGroupRolePermission(null);
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupRolePermission().isEmpty());
    }

    @Test
    public void setAndGetGroupPermissionEmptySetTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupPermission().isEmpty());
        loginInfoImpl.setGroupPermission(groupPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", groupPermissions, loginInfoImpl.getGroupPermission());
    }

    @Test
    public void setAndGetGroupPermissionTest() {
        groupPermissions.add(Mockito.mock(GroupPermission.class));
        groupPermissions.add(Mockito.mock(GroupPermission.class));

        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupPermission().isEmpty());
        loginInfoImpl.setGroupPermission(groupPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", groupPermissions, loginInfoImpl.getGroupPermission());
    }

    @Test
    public void setAndGetGroupPermissionNullTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupPermission().isEmpty());
        loginInfoImpl.setGroupPermission(null);
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupPermission().isEmpty());
    }


    @Test
    public void setAndGetRolePermissionsEmptySetTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getRolePermissions().isEmpty());
        loginInfoImpl.setRolePermissions(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfoImpl.getRolePermissions());
    }

    @Test
    public void setAndGetRolePermissionsTest() {
        rolePermissions.add(Mockito.mock(RolePermission.class));
        rolePermissions.add(Mockito.mock(RolePermission.class));

        Assert.assertTrue("Empty expected", loginInfoImpl.getRolePermissions().isEmpty());
        loginInfoImpl.setRolePermissions(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfoImpl.getRolePermissions());
    }

    @Test
    public void setAndGetRolePermissionsNullTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getRolePermissions().isEmpty());
        loginInfoImpl.setRolePermissions(null);
        Assert.assertTrue("Empty expected", loginInfoImpl.getRolePermissions().isEmpty());
    }

    @Test
    public void setAndGetAccessPermissionsEmptySetTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getAccessPermissions().isEmpty());
        loginInfoImpl.setAccessPermissions(accessPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", accessPermissions, loginInfoImpl.getAccessPermissions());
    }

    @Test
    public void setAndGetAccessPermissionsTest() {
        accessPermissions.add(Mockito.mock(AccessPermission.class));
        accessPermissions.add(Mockito.mock(AccessPermission.class));

        Assert.assertTrue("Empty expected", loginInfoImpl.getAccessPermissions().isEmpty());
        loginInfoImpl.setAccessPermissions(accessPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", accessPermissions, loginInfoImpl.getAccessPermissions());
    }

    @Test
    public void setAndGetAccessPermissionsNullTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getAccessPermissions().isEmpty());
        loginInfoImpl.setAccessPermissions(null);
        Assert.assertTrue("Empty expected", loginInfoImpl.getAccessPermissions().isEmpty());
    }

    @Test
    public void setAndGetGroupRolePermissionsEmptySetTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupRolePermissions().isEmpty());
        loginInfoImpl.setGroupRolePermissions(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfoImpl.getGroupRolePermissions());
    }

    @Test
    public void setAndGetGroupRolePermissionsTest() {
        rolePermissions.add(Mockito.mock(RolePermission.class));
        rolePermissions.add(Mockito.mock(RolePermission.class));

        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupRolePermissions().isEmpty());
        loginInfoImpl.setGroupRolePermissions(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfoImpl.getGroupRolePermissions());
    }

    @Test
    public void setAndGetGroupRolePermissionsNullTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupRolePermissions().isEmpty());
        loginInfoImpl.setGroupRolePermissions(null);
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupRolePermissions().isEmpty());
    }

    @Test
    public void setAndGetGroupPermissionsEmptySetTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupPermissions().isEmpty());
        loginInfoImpl.setGroupPermissions(groupPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", groupPermissions, loginInfoImpl.getGroupPermissions());
    }

    @Test
    public void setAndGetGroupPermissionsTest() {
        groupPermissions.add(Mockito.mock(GroupPermission.class));
        groupPermissions.add(Mockito.mock(GroupPermission.class));

        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupPermissions().isEmpty());
        loginInfoImpl.setGroupPermissions(groupPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", groupPermissions, loginInfoImpl.getGroupPermissions());
    }

    @Test
    public void setAndGetGroupPermissionsNullTest() {
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupPermissions().isEmpty());
        loginInfoImpl.setGroupPermissions(null);
        Assert.assertTrue("Empty expected", loginInfoImpl.getGroupPermissions().isEmpty());
    }
}