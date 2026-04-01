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
package org.eclipse.kapua.service.security;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Category(JUnitTests.class)
public class EnhModularRealmAuthorizerTest {

    EnhModularRealmAuthorizer enhModularRealmAuthorizer1, enhModularRealmAuthorizer2;
    Collection<Realm> realms;
    Realm realm1, realm2, realm3;
    AuthorizingRealm authorizingRealm1, authorizingRealm2, authorizingRealm3;
    PrincipalCollection principals;
    List<Permission> permissions;
    Permission permission1, permission2, permission3;
    boolean[] booleanArray1, booleanArray2;
    PermissionResolver permissionResolver;
    String stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3;

    @Before
    public void initialize() {
        enhModularRealmAuthorizer1 = new EnhModularRealmAuthorizer();
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        realms = new HashSet<>();
        realm1 = Mockito.mock(Realm.class);
        realm2 = Mockito.mock(Realm.class);
        realm3 = Mockito.mock(Realm.class);
        authorizingRealm1 = Mockito.mock(AuthorizingRealm.class);
        authorizingRealm2 = Mockito.mock(AuthorizingRealm.class);
        authorizingRealm3 = Mockito.mock(AuthorizingRealm.class);
        principals = new SimplePrincipalCollection();
        permissions = new LinkedList<>();
        permission1 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permission2 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        permission3 = Mockito.mock(org.apache.shiro.authz.Permission.class);
        booleanArray1 = new boolean[]{true, false, true};
        booleanArray2 = new boolean[]{false, true};
        permissionResolver = Mockito.mock(PermissionResolver.class);
        stringPermissionResolver1 = "permissionResolver1";
        stringPermissionResolver2 = "permissionResolver2";
        stringPermissionResolver3 = "permissionResolver3";
    }

    @Test
    public void enhModularRealmAuthorizerWithoutParameterTest() {
        Assert.assertNull("Null expected.", enhModularRealmAuthorizer1.getRealms());
    }

    @Test
    public void enhModularRealmAuthorizerWithEmptyRealmsTest() {
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        Assert.assertTrue("True expected.", enhModularRealmAuthorizer2.getRealms().isEmpty());
    }

    @Test
    public void enhModularRealmAuthorizerWithRealmsTest() {
        realms.add(realm1);
        realms.add(realm2);
        realms.add(realm3);

        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        Assert.assertEquals("Expected and actual values should be the same.", realms, enhModularRealmAuthorizer2.getRealms());
    }

    @Test
    public void enhModularRealmAuthorizerWithNullParameterTest() {
        EnhModularRealmAuthorizer enhModularRealmAuthorizer = new EnhModularRealmAuthorizer(null);
        Assert.assertNull("Null expected.", enhModularRealmAuthorizer.getRealms());
    }

    @Test
    public void isPermittedAuthorizerWithoutParameterTest() {
        try {
            enhModularRealmAuthorizer1.isPermitted(principals, permissions);
            Assert.fail("Exception expected.");
        } catch (Exception e) {
            Assert.assertEquals("Expected and actual values should be the same.", new IllegalStateException("Configuration error:  No realms have been configured!  One or more realms must be present to execute an authorization operation.").toString(), e.toString());
        }
    }

    @Test
    public void isPermittedAuthorizerWithNullRealmsTest() {
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(null);
        try {
            enhModularRealmAuthorizer2.isPermitted(principals, permissions);
            Assert.fail("Exception expected.");
        } catch (Exception e) {
            Assert.assertEquals("Expected and actual values should be the same.", new IllegalStateException("Configuration error:  No realms have been configured!  One or more realms must be present to execute an authorization operation.").toString(), e.toString());
        }
    }

    @Test
    public void isPermittedAuthorizerWithEmptyRealmsTest() {
        try {
            enhModularRealmAuthorizer2.isPermitted(principals, permissions);
            Assert.fail("Exception expected.");
        } catch (Exception e) {
            Assert.assertEquals("Expected and actual values should be the same.", new IllegalStateException("Configuration error:  No realms have been configured!  One or more realms must be present to execute an authorization operation.").toString(), e.toString());
        }
    }

    @Test
    public void isPermittedAuthorizerWithRealmsEmptyPermissionsTest() {
        realms.add(realm1);
        realms.add(realm2);
        realms.add(realm3);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 0, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithOneRealmTest() {
        realms.add(realm1);
        permissions.add(permission1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 1, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithRealmsTest() {
        realms.add(realm1);
        realms.add(realm2);
        realms.add(realm3);
        permissions.add(permission1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 1, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithSingleAuthorizingRealmTest() {
        permissions.add(permission1);
        realms.add(authorizingRealm1);

        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        Mockito.when(authorizingRealm1.isPermitted(principals, permissions)).thenReturn(booleanArray1);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 3, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithMultipleAuthorizingRealmsTest() {
        permissions.add(permission1);
        realms.add(authorizingRealm1);
        realms.add(authorizingRealm2);
        realms.add(authorizingRealm3);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Mockito.when(authorizingRealm1.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm2.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm3.isPermitted(principals, permissions)).thenReturn(booleanArray1);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 1, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithMultipleAuthorizingRealmsAllTrueTest() {
        permissions.add(permission1);
        permissions.add(permission2);
        realms.add(authorizingRealm1);
        realms.add(authorizingRealm2);

        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Mockito.when(authorizingRealm1.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm2.isPermitted(principals, permissions)).thenReturn(booleanArray2);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 2, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerWithMultipleRealmsAndAuthorizingRealmsTest() {
        permissions.add(permission1);
        permissions.add(permission2);
        permissions.add(permission3);

        realms.add(authorizingRealm1);
        realms.add(authorizingRealm2);
        realms.add(authorizingRealm3);
        realms.add(realm1);
        realms.add(realm2);

        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Mockito.when(authorizingRealm1.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm2.isPermitted(principals, permissions)).thenReturn(booleanArray1);
        Mockito.when(authorizingRealm3.isPermitted(principals, permissions)).thenReturn(booleanArray1);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, permissions) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 3, enhModularRealmAuthorizer2.isPermitted(principals, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerNullPermissionsTest() {
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        try {
            enhModularRealmAuthorizer2.isPermitted(principals, (List) null);
            Assert.fail("Exception expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test
    public void isPermittedAuthorizerNullPrincipalsTest() {
        realms.add(realm1);
        realms.add(realm2);
        realms.add(realm3);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(null, permissions) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 0, enhModularRealmAuthorizer2.isPermitted(null, permissions).length);
    }

    @Test
    public void isPermittedAuthorizerNullTest() {
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        try {
            enhModularRealmAuthorizer2.isPermitted(null, (List) null);
            Assert.fail("Exception expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test
    public void isPermittedStringPermissionsTest() {
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 3, enhModularRealmAuthorizer2.isPermitted(principals, stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3).length);
    }

    @Test
    public void isPermittedStringPermissionsNullPrincipalsTest() {
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(null, stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 3, enhModularRealmAuthorizer2.isPermitted(null, stringPermissionResolver1, stringPermissionResolver2, stringPermissionResolver3).length);
    }

    @Test
    public void isPermittedOneNullStringPermissionTest() {
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, null, stringPermissionResolver1) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 2, enhModularRealmAuthorizer2.isPermitted(principals, null, stringPermissionResolver1).length);
    }

    @Test
    public void isPermittedNullStringPermissionsTest() {
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(principals, null, null) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 2, enhModularRealmAuthorizer2.isPermitted(principals, null, null).length);
    }

    @Test
    public void isPermittedStringPermissionsNullTest() {
        realms.add(realm1);
        enhModularRealmAuthorizer2 = new EnhModularRealmAuthorizer(realms);
        enhModularRealmAuthorizer2.setPermissionResolver(permissionResolver);

        Assert.assertTrue("Instance of boolean[] expected.", enhModularRealmAuthorizer2.isPermitted(null, null, null) instanceof boolean[]);
        Assert.assertEquals("Expected and actual values should be the same.", 2, enhModularRealmAuthorizer2.isPermitted(null, null, null).length);
    }
}