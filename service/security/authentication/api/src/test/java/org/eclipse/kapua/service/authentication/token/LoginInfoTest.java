/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.token;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class LoginInfoTest {

    LoginInfo loginInfo;
    Set<RolePermission> rolePermissions;
    Set<AccessPermission> accessPermissions;

    @Before
    public void initialize() {
        loginInfo = new LoginInfo();
        rolePermissions = new HashSet<>();
        accessPermissions = new HashSet<>();
    }

    @Test
    public void setAndGetAccessTokenTest() {
        AccessToken[] accessTokens = { null, Mockito.mock(AccessToken.class) };
        Assert.assertNull("Null expected.", loginInfo.getAccessToken());
        for (AccessToken accessToken : accessTokens) {
            loginInfo.setAccessToken(accessToken);
            Assert.assertEquals("Expected and actual values should be the same.", accessToken, loginInfo.getAccessToken());
        }
    }

    @Test
    public void setAndGetRolePermissionEmptySetTest() {
        Assert.assertNull("Null expected.", loginInfo.getRolePermission());
        loginInfo.setRolePermission(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfo.getRolePermission());
    }

    @Test
    public void setAndGetRolePermissionTest() {
        rolePermissions.add(Mockito.mock(RolePermission.class));
        rolePermissions.add(Mockito.mock(RolePermission.class));

        Assert.assertNull("Null expected.", loginInfo.getRolePermission());
        loginInfo.setRolePermission(rolePermissions);
        Assert.assertEquals("Expected and actual values should be the same.", rolePermissions, loginInfo.getRolePermission());
    }

    @Test
    public void setAndGetRolePermissionNullTest() {
        Assert.assertNull("Null expected.", loginInfo.getRolePermission());
        loginInfo.setRolePermission(null);
        Assert.assertNull("Null expected.", loginInfo.getRolePermission());
    }

    @Test
    public void setAndGetAccessPermissionEmptySetTest() {
        Assert.assertNull("Null expected.", loginInfo.getAccessPermission());
        loginInfo.setAccessPermission(accessPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", accessPermissions, loginInfo.getAccessPermission());
    }

    @Test
    public void setAndGetAccessPermissionTest() {
        accessPermissions.add(Mockito.mock(AccessPermission.class));
        accessPermissions.add(Mockito.mock(AccessPermission.class));

        Assert.assertNull("Null expected.", loginInfo.getAccessPermission());
        loginInfo.setAccessPermission(accessPermissions);
        Assert.assertEquals("Expected and actual values should be the same.", accessPermissions, loginInfo.getAccessPermission());
    }

    @Test
    public void setAndGetAccessPermissionNullTest() {
        Assert.assertNull("Null expected.", loginInfo.getAccessPermission());
        loginInfo.setAccessPermission(null);
        Assert.assertNull("Null expected.", loginInfo.getAccessPermission());
    }
}