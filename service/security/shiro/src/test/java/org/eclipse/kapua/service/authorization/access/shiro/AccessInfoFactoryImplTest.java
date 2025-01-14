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

import java.util.Date;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class AccessInfoFactoryImplTest {

    AccessInfoFactoryImpl accessInfoFactoryImpl;
    KapuaId scopeId;
    AccessInfo accessInfo;
    Date createdOn, modifiedOn;

    @Before
    public void initialize() {
        accessInfoFactoryImpl = new AccessInfoFactoryImpl();
        scopeId = KapuaId.ONE;
        accessInfo = Mockito.mock(AccessInfo.class);
        createdOn = new Date();
        modifiedOn = new Date();

        Mockito.when(accessInfo.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfo.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(accessInfo.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(accessInfo.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(accessInfo.getModifiedBy()).thenReturn(KapuaId.ANY);
        Mockito.when(accessInfo.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(accessInfo.getOptlock()).thenReturn(11);
        Mockito.when(accessInfo.getUserId()).thenReturn(KapuaId.ANY);
    }

    @Test
    public void newEntityTest() {
        AccessInfo accessInfo = accessInfoFactoryImpl.newEntity(scopeId);

        Assert.assertNull("Null expected.", accessInfo.getScopeId());
    }

    @Test
    public void newEntityNullTest() {
        AccessInfo accessInfo = accessInfoFactoryImpl.newEntity(null);

        Assert.assertNull("Null expected.", accessInfo.getScopeId());
    }

    @Test
    public void newCreatorTest() {
        AccessInfoCreator accessInfoCreator = accessInfoFactoryImpl.newCreator(scopeId);

        Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessInfoCreator.getScopeId());
    }

    @Test
    public void newCreatorNullTest() {
        AccessInfoCreator accessInfoCreator = accessInfoFactoryImpl.newCreator(null);

        Assert.assertNull("Null expected.", accessInfoCreator.getScopeId());
    }

    @Test
    public void newQueryTest() {
        KapuaQuery accessInfoQuery = new KapuaQuery(scopeId);

        Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessInfoQuery.getScopeId());
    }

    @Test
    public void newQueryNullTest() {
        KapuaQuery accessInfoQuery = new KapuaQuery((KapuaId) null);

        Assert.assertNull("Null expected.", accessInfoQuery.getScopeId());
    }

    @Test
    public void cloneTest() {
        AccessInfo resultAccessInfo = accessInfoFactoryImpl.clone(accessInfo);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultAccessInfo.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultAccessInfo.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultAccessInfo.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, resultAccessInfo.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultAccessInfo.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, resultAccessInfo.getModifiedOn());
        Assert.assertEquals("Expected and actual values should be the same.", 11, resultAccessInfo.getOptlock());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultAccessInfo.getUserId());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        accessInfoFactoryImpl.clone(null);
    }
}