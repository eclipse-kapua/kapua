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
package org.eclipse.kapua.service.authorization.domain.shiro;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class DomainFactoryImplTest {

    DomainFactoryImpl domainFactoryImpl;

    @Before
    public void initialize() {
        domainFactoryImpl = new DomainFactoryImpl();
    }

    @Test
    public void newEntityTest() {
        Domain domain = domainFactoryImpl.newEntity(KapuaId.ONE);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, domain.getScopeId());
    }

    @Test
    public void newEntityNullTest() {
        Domain domain = domainFactoryImpl.newEntity(null);
        Assert.assertNull("Null expected.", domain.getScopeId());
    }

    @Test
    public void cloneTest() {
        Domain domain = Mockito.mock(Domain.class);
        Set<Actions> actions = new HashSet<>();
        Date createdOn = new Date();

        Mockito.when(domain.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(domain.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(domain.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(domain.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(domain.getName()).thenReturn("name");
        Mockito.when(domain.getActions()).thenReturn(actions);
        Mockito.when(domain.getGroupable()).thenReturn(true);

        Domain resultDomain = domainFactoryImpl.clone(domain);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultDomain.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultDomain.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultDomain.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, resultDomain.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", "name", resultDomain.getName());
        Assert.assertEquals("Expected and actual values should be the same.", actions, resultDomain.getActions());
        Assert.assertEquals("Expected and actual values should be the same.", true, resultDomain.getGroupable());
    }

    @Test(expected = NullPointerException.class)
    public void cloneNullTest() {
        domainFactoryImpl.clone(null);
    }
}