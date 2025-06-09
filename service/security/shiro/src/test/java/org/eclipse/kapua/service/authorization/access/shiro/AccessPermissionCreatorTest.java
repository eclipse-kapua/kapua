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
package org.eclipse.kapua.service.authorization.access.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.access.AccessPermissionCreator;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class AccessPermissionCreatorTest {

    @Test
    public void accessPermissionCreatorImplTest() {
        AccessPermissionCreator accessPermissionCreatorImpl = new AccessPermissionCreator(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessPermissionCreatorImpl.getScopeId());
    }

    @Test
    public void accessPermissionCreatorImplNullTest() {
        AccessPermissionCreator accessPermissionCreatorImpl = new AccessPermissionCreator(null);
        Assert.assertNull("Null expected.", accessPermissionCreatorImpl.getScopeId());
    }

    @Test
    public void setAndGetAccessInfoIdTest() {
        AccessPermissionCreator accessPermissionCreatorImpl = new AccessPermissionCreator(KapuaId.ONE);

        Assert.assertNull("Null expected.", accessPermissionCreatorImpl.getAccessInfoId());
        accessPermissionCreatorImpl.setAccessInfoId(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessPermissionCreatorImpl.getAccessInfoId());
        accessPermissionCreatorImpl.setAccessInfoId(null);
        Assert.assertNull("Null expected.", accessPermissionCreatorImpl.getAccessInfoId());
    }

    @Test
    public void setAndGetPermissionTest() {
        AccessPermissionCreator accessPermissionCreatorImpl = new AccessPermissionCreator(KapuaId.ONE);
        Permission permission = Mockito.mock(Permission.class);

        Assert.assertNull("Null expected.", accessPermissionCreatorImpl.getPermission());
        accessPermissionCreatorImpl.setPermission(permission);
        Assert.assertEquals("Expected and actual values should be the same.", permission, accessPermissionCreatorImpl.getPermission());
        accessPermissionCreatorImpl.setPermission(null);
        Assert.assertNull("Null expected.", accessPermissionCreatorImpl.getPermission());
    }
}