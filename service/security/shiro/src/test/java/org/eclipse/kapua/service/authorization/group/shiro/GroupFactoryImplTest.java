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
package org.eclipse.kapua.service.authorization.group.shiro;

import java.util.Date;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class GroupFactoryImplTest {

    GroupFactoryImpl groupFactoryImpl;
    KapuaId scopeId;
    String[] names;
    Group group;
    Date createdOn, modifiedOn;

    @Before
    public void initialize() {
        groupFactoryImpl = new GroupFactoryImpl();
        scopeId = KapuaId.ONE;
        names = new String[] { "", "  na123)(&*^&NAME  <>", "Na-,,..,,Me name ---", "-&^454536 na___,,12 NAME name    ", "! 2#@ na     meNEMA 2323", "12&^%4   ,,,. '|<>*(", "       ,,123name;;'",
                "12#name--765   ,.aaa!!#$%^<> " };
        group = Mockito.mock(Group.class);
        createdOn = new Date();
        modifiedOn = new Date();

        Mockito.when(group.getName()).thenReturn("group name");
        Mockito.when(group.getDescription()).thenReturn("description");
        Mockito.when(group.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(group.getScopeId()).thenReturn(KapuaId.ANY);
        Mockito.when(group.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(group.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(group.getModifiedBy()).thenReturn(KapuaId.ANY);
        Mockito.when(group.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(group.getOptlock()).thenReturn(11);
    }

    @Test
    public void newCreatorScopeIdNameParametersTest() {
        for (String name : names) {
            GroupCreator groupCreator = new GroupCreator(scopeId, name);
            Assert.assertEquals("Expected and actual values should be the same.", name, groupCreator.getName());
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, groupCreator.getScopeId());
        }
    }

    @Test
    public void newCreatorNullScopeIdNameParametersTest() {
        for (String name : names) {
            GroupCreator groupCreator = new GroupCreator(null, name);
            Assert.assertEquals("Expected and actual values should be the same.", name, groupCreator.getName());
            Assert.assertNull("Null expected.", groupCreator.getScopeId());
        }
    }

    @Test
    public void newCreatorScopeIdNullNameParametersTest() {
        GroupCreator groupCreator = new GroupCreator(scopeId, null);
        Assert.assertNull("Null expected.", groupCreator.getName());
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, groupCreator.getScopeId());
    }

    @Test
    public void newEntityTest() {
        Group group = groupFactoryImpl.newEntity(scopeId);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, group.getScopeId());
    }

    @Test
    public void newEntityNullTest() {
        Group group = groupFactoryImpl.newEntity(null);
        Assert.assertNull("Null expected.", group.getScopeId());
    }

    @Test
    public void newCreatorTest() {
        GroupCreator groupCreator = new GroupCreator(scopeId);
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, groupCreator.getScopeId());
    }

    @Test
    public void newCreatorNullTest() {
        GroupCreator groupCreator = new GroupCreator(null);
        Assert.assertNull("Null expected.", groupCreator.getScopeId());
    }

    @Test
    public void cloneTest() {
        Group resultGroup = groupFactoryImpl.clone(group);
        Assert.assertEquals("Expected and actual values should be the same.", "group name", resultGroup.getName());
        Assert.assertEquals("Expected and actual values should be the same.", "description", resultGroup.getDescription());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultGroup.getId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultGroup.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, resultGroup.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, resultGroup.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, resultGroup.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, resultGroup.getModifiedOn());
        Assert.assertEquals("Expected and actual values should be the same.", 11, resultGroup.getOptlock());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        groupFactoryImpl.clone(null);
    }
}