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
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class AccessRoleCreatorTest {

    AccessRoleCreator accessRoleCreatorImpl;

    @Before
    public void initialize() {
        accessRoleCreatorImpl = new AccessRoleCreator(KapuaId.ONE);
    }

    @Test
    public void accessRoleCreatorImplTest() {
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessRoleCreatorImpl.getScopeId());
        Assert.assertNull("Null expected.", accessRoleCreatorImpl.getAccessInfoId());
        Assert.assertNull("Null expected.", accessRoleCreatorImpl.getRoleId());
    }

    @Test
    public void accessRoleCreatorImplNullTest() {
        AccessRoleCreator accessRoleCreatorImplNullId = new AccessRoleCreator(null);
        Assert.assertNull("Null expected.", accessRoleCreatorImplNullId.getScopeId());
        Assert.assertNull("Null expected.", accessRoleCreatorImplNullId.getAccessInfoId());
        Assert.assertNull("Null expected.", accessRoleCreatorImplNullId.getRoleId());
    }

    @Test
    public void setAndGetAccessInfoIdTest() {
        accessRoleCreatorImpl.setAccessInfoId(KapuaId.ANY);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessRoleCreatorImpl.getAccessInfoId());

        accessRoleCreatorImpl.setAccessInfoId(null);
        Assert.assertNull("Null expected.", accessRoleCreatorImpl.getAccessInfoId());
    }

    @Test
    public void setAndGetRoleIdTest() {
        accessRoleCreatorImpl.setRoleId(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessRoleCreatorImpl.getRoleId());

        accessRoleCreatorImpl.setRoleId(null);
        Assert.assertNull("Null expected.", accessRoleCreatorImpl.getRoleId());
    }
}