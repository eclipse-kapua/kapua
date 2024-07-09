/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.config.metatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaTocdTest {

    @Before
    public void createInstanceOfClasses() {

        kapuaTocd = new KapuaTocd();
        kapuaTad1 = new KapuaTad();
        kapuaTad2 = new KapuaTad();
        kapuaTad3 = new KapuaTad();
        ticon = new KapuaTicon();
        ticon2 = new KapuaTicon();
    }

    KapuaTocd kapuaTocd;
    KapuaTad kapuaTad1;
    KapuaTad kapuaTad2;
    KapuaTad kapuaTad3;
    KapuaTicon ticon;
    KapuaTicon ticon2;

    @Test
    public void getADTest() {
        Assert.assertTrue(kapuaTocd.getAD().isEmpty());
    }

    @Test
    public void setAndGetADTest() {
        List<KapuaTad> expectedValues = new ArrayList<>();
        expectedValues.add(kapuaTad1);
        kapuaTocd.setAD(expectedValues);
        Assert.assertEquals("tocdImpl.ad", expectedValues, kapuaTocd.getAD());
    }

    @Test
    public void addAndGetADTest() {
        kapuaTocd.addAD(kapuaTad3);
        kapuaTocd.addAD(kapuaTad2);
        kapuaTocd.getAD();
        Assert.assertTrue(kapuaTocd.getAD().contains(kapuaTad3) && kapuaTocd.getAD().contains(kapuaTad2));
    }

    @Test
    public void getIconTest() {
        Assert.assertTrue(kapuaTocd.getIcon().isEmpty());
    }

    @Test
    public void setAndGetIconTest() {
        List<KapuaTicon> listOfObj = new ArrayList<>();
        listOfObj.add(ticon);
        kapuaTocd.setIcon(listOfObj);
        Assert.assertEquals("tocdImpl.icon", listOfObj, kapuaTocd.getIcon());
    }

    @Test
    public void addAndGetIconTest() {
        kapuaTocd.addIcon(ticon);
        kapuaTocd.addIcon(ticon2);
        kapuaTocd.getIcon();
        Assert.assertTrue(kapuaTocd.getIcon().contains(ticon) && kapuaTocd.getIcon().contains(ticon2));
    }

    @Test
    public void getAnyTest() {
        Assert.assertTrue(kapuaTocd.getAny().isEmpty());
    }

    @Test
    public void setAndGetAnyTest() {
        List<Object> options = new ArrayList<>();
        options.add(ticon);
        options.add(ticon2);
        kapuaTocd.setAny(options);
        Assert.assertEquals("tocdImpl.any", options, kapuaTocd.getAny());
    }

    @Test
    public void addAndGetAnyTest() {
        kapuaTocd.addAny(ticon);
        kapuaTocd.addAny(ticon2);
        Assert.assertTrue(kapuaTocd.getAny().contains(ticon) && kapuaTocd.getAny().contains(ticon2));
    }

    @Test
    public void setAndGetNameToNullTest() {
        kapuaTocd.setName(null);
        Assert.assertNull(kapuaTocd.getName());
    }

    @Test
    public void setAndGetNameTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularName", "regular Name", "49", "regularName49", "REGULAR", "246465494135646120009090049684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            kapuaTocd.setName(value);
            Assert.assertTrue(kapuaTocd.getName().contains(value));
        }
    }

    @Test
    public void setAndGetDescriptionToNullTest() {
        kapuaTocd.setDescription(null);
        Assert.assertNull(kapuaTocd.getDescription());
    }

    @Test
    public void setAndGetDescriptionTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regular Description", "49", "regularDescription49", "DESCRIPTION", "246465494135646120009090049684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            kapuaTocd.setDescription(value);
            Assert.assertTrue(kapuaTocd.getDescription().contains(value));
        }
    }

    @Test
    public void setAndGetIdToNullTest() {
        kapuaTocd.setId(null);
        Assert.assertNull(kapuaTocd.getId());
    }

    @Test
    public void setAndGetIdTest() {
        String[] permittedValues = { "", "regularId", "49", "regular Id", "regular id with spaces", "!@#$%&*()_+/->,<", "ID", "id123" };
        for (String value : permittedValues) {
            kapuaTocd.setId(value);
            Assert.assertTrue(kapuaTocd.getId().contains(value));
        }
    }

    @Test
    public void putAndGetOtherAttributeTest() {
        Map<QName, String> expectedValues = new HashMap<>();

        expectedValues.put(QName.valueOf("1"), "a");
        expectedValues.put(QName.valueOf("2"), "b");
        expectedValues.put(QName.valueOf("3"), "c");

        kapuaTocd.putOtherAttribute(QName.valueOf("1"), "a");
        kapuaTocd.putOtherAttribute(QName.valueOf("2"), "b");
        kapuaTocd.putOtherAttribute(QName.valueOf("3"), "c");

        Assert.assertEquals("tocdImpl.otherAttributes", expectedValues, kapuaTocd.getOtherAttributes());
    }

    @Test
    public void getOtherAttributeTest() {
        Assert.assertTrue(kapuaTocd.getOtherAttributes().isEmpty());
    }

    @Test
    public void testSetAndGetOtherAttribute() {
        Map<QName, String> values = new HashMap<>();

        values.put(QName.valueOf("1"), "a");
        values.put(QName.valueOf("2"), "b");
        values.put(QName.valueOf("3"), "c");

        kapuaTocd.setOtherAttributes(values);
        Assert.assertEquals("tocdImpl.otherAttributes", values, kapuaTocd.getOtherAttributes());
    }
}
