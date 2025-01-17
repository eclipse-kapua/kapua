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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.domain.DomainCreator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class DomainCreatorTest {

    @Test
    public void domainCreatorImplTest() {
        String[] names = { "", "  na123)(&*^&NAME  <>", "Na-,,..,,Me name ---", "-&^454536 na___,,12 NAME name    ", "! 2#@ na     meNEMA 2323", "12&^%4   ,,,. '|<>*(", "       ,,123name;;'",
                "12#name--765   ,.aaa!!#$%^<> " };

        for (String name : names) {
            DomainCreator domainCreatorImpl = new DomainCreator(name);
            Assert.assertNull("Null expected.", domainCreatorImpl.getScopeId());
            Assert.assertEquals("Expected and actual values should be the same.", name, domainCreatorImpl.getName());
            Assert.assertNull("Null expected.", domainCreatorImpl.getActions());
            Assert.assertFalse("False expected.", domainCreatorImpl.getGroupable());
        }
    }

    @Test
    public void domainCreatorImplNullTest() {
        DomainCreator domainCreatorImpl = new DomainCreator((KapuaId) null);
        Assert.assertNull("Null expected.", domainCreatorImpl.getScopeId());
        Assert.assertNull("Null expected.", domainCreatorImpl.getName());
        Assert.assertNull("Null expected.", domainCreatorImpl.getActions());
        Assert.assertFalse("False expected.", domainCreatorImpl.getGroupable());
    }

    @Test
    public void setAndGetNameTest() {
        String[] newNames = { "", "  na123)(&*^&NAME NEWname  <>", "newNa-,,..,,Me name NEW NEW---", "-&^454536 na___,,12 NAME New    name    ", "! 2#@ na     meNewNAME 23NeW23",
                "12&^%4   ,,,. '|<new name>*(", "       ,,123name;;'", "12#name-new-name765   ,.aaa!!#$%^<> " };

        DomainCreator domainCreatorImpl = new DomainCreator("name");
        for (String newName : newNames) {
            domainCreatorImpl.setName(newName);
            Assert.assertEquals("Expected and actual values should be the same.", newName, domainCreatorImpl.getName());
        }
        domainCreatorImpl.setName(null);
        Assert.assertNull("Null expected.", domainCreatorImpl.getName());
    }

    @Test
    public void setAndGetActionsTest() {
        Set<Actions> actions = new HashSet<>();
        DomainCreator domainCreatorImpl = new DomainCreator("name");

        domainCreatorImpl.setActions(actions);
        Assert.assertTrue("True expected", domainCreatorImpl.getActions().isEmpty());

        actions.add(Actions.read);
        actions.add(Actions.delete);
        actions.add(Actions.connect);
        actions.add(Actions.execute);
        actions.add(Actions.write);
        domainCreatorImpl.setActions(actions);
        Assert.assertEquals("Expected and actual values should be the same.", 5, domainCreatorImpl.getActions().size());

        domainCreatorImpl.setActions(null);
        Assert.assertNull("Null expected.", domainCreatorImpl.getActions());
    }

    @Test
    public void setAndGetGroupableTest() {
        boolean[] groupables = { true, false };
        DomainCreator domainCreatorImpl = new DomainCreator("name");

        for (boolean groupable : groupables) {
            domainCreatorImpl.setGroupable(groupable);
            Assert.assertEquals("Expected and actual values should be the same.", groupable, domainCreatorImpl.getGroupable());
        }
    }
}