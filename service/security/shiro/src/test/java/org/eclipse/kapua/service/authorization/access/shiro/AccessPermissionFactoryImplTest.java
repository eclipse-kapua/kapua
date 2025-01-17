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
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class AccessPermissionFactoryImplTest {

    AccessPermissionFactoryImpl accessPermissionFactoryImpl;
    KapuaId[] scopeIds;

    @Before
    public void initialize() {
        accessPermissionFactoryImpl = new AccessPermissionFactoryImpl();
        scopeIds = new KapuaId[] { null, KapuaId.ONE };
    }

    @Test
    public void newEntityTest() {
        for (KapuaId scopeId : scopeIds) {
            Assert.assertTrue("True expected.", accessPermissionFactoryImpl.newEntity(scopeId) instanceof AccessPermission);
        }
    }

    @Test
    public void cloneTest() {
        AccessPermission accessPermission = Mockito.mock(AccessPermission.class);
        Assert.assertTrue("True expected.", accessPermissionFactoryImpl.clone(accessPermission) instanceof AccessPermission);
    }

    @Test(expected = NullPointerException.class)
    public void cloneNullTest() {
        accessPermissionFactoryImpl.clone(null);
    }
}