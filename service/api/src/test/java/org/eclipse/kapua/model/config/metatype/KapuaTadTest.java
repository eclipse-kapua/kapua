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
public class KapuaTadTest {

    @Before
    public void createInstanceOfClasses() {

        kapuaTad = new KapuaTad();
        toption = new KapuaToption();
        toption2 = new KapuaToption();
    }

    KapuaTad kapuaTad;
    KapuaToption toption;
    KapuaToption toption2;

    @Test
    public void getOptionToNullTest() {
        kapuaTad.getOption();
        Assert.assertTrue(kapuaTad.getOption().isEmpty());
    }

    @Test
    public void setAndGetOptionRegularTest() {
        List<KapuaToption> option = new ArrayList<>();
        option.add(toption);
        kapuaTad.setOption(option);
        Assert.assertEquals("tadImpl.option", option, kapuaTad.getOption());
    }

    @Test
    public void addAndGetOptionTest() {
        List<KapuaToption> options = new ArrayList<>();
        options.add(toption);
        options.add(toption2);

        kapuaTad.addOption(toption);
        kapuaTad.addOption(toption2);

        Assert.assertEquals("tadImpl.option", options, kapuaTad.getOption());
    }

    @Test
    public void getAnyTest() {
        Assert.assertTrue(kapuaTad.getAny().isEmpty());
    }

    @Test
    public void setAndGetAnyTest() {
        List<Object> options = new ArrayList<>();
        options.add(toption);
        options.add(toption2);
        options.add(null);
        kapuaTad.setAny(options);
        Assert.assertEquals("tadImpl.option", options, kapuaTad.getAny());
    }

    @Test
    public void setAndGetNameToNullTest() {
        kapuaTad.setName(null);
        Assert.assertNull(kapuaTad.getName());
    }

    @Test
    public void setAndGetNameTest() {
        String[] permittedValues = { "", "regularName", "49", "regular Name", "regular name with spaces", "!@#$%&*()_+/->,<", "NAME", "name123" };
        for (String value : permittedValues) {
            kapuaTad.setName(value);
            Assert.assertTrue(kapuaTad.getName().contains(value));
        }
    }

    @Test
    public void setAndGetDescriptionToNullTest() {
        kapuaTad.setDescription(null);
        Assert.assertNull(kapuaTad.getDescription());
    }

    @Test
    public void setAndGetDescriptionTest() {
        String[] permittedValues = { "", "regularDescription", "49", "regular Description", "regular description with spaces", "!@#$%&*()_+/->,<", "DESCRIPTION", "description123" };
        for (String value : permittedValues) {
            kapuaTad.setDescription(value);
            Assert.assertTrue(kapuaTad.getDescription().contains(value));
        }
    }

    @Test
    public void setAndGetIdToNullTest() {
        kapuaTad.setId(null);
        Assert.assertNull(kapuaTad.getId());
    }

    @Test
    public void setAndGetIdTest() {
        String[] permittedValues = { "", "regularId", "49", "regular Id", "regular id with spaces", "!@#$%&*()_+/->,<", "ID", "id123" };
        for (String value : permittedValues) {
            kapuaTad.setId(value);
            Assert.assertTrue(kapuaTad.getId().contains(value));
        }
    }

    @Test
    public void setAndGetTypeStringTest() {
        kapuaTad.setType(KapuaTscalar.STRING);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.STRING, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypeLongTest() {
        kapuaTad.setType(KapuaTscalar.LONG);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.LONG, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypeDoubleTest() {
        kapuaTad.setType(KapuaTscalar.DOUBLE);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.DOUBLE, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypeFloatTest() {
        kapuaTad.setType(KapuaTscalar.FLOAT);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.FLOAT, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypeIntegerTest() {
        kapuaTad.setType(KapuaTscalar.INTEGER);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.INTEGER, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypeByteTest() {
        kapuaTad.setType(KapuaTscalar.BYTE);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.BYTE, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypeCharTest() {
        kapuaTad.setType(KapuaTscalar.CHAR);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.CHAR, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypeBooleanTest() {
        kapuaTad.setType(KapuaTscalar.BOOLEAN);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.BOOLEAN, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypeShortTest() {
        kapuaTad.setType(KapuaTscalar.SHORT);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.SHORT, kapuaTad.getType());
    }

    @Test
    public void setAndGetTypePasswordTest() {
        kapuaTad.setType(KapuaTscalar.PASSWORD);
        Assert.assertEquals("tadImpl.type", KapuaTscalar.PASSWORD, kapuaTad.getType());
    }

    @Test
    public void setAndGetCardinalityToNullTest() {
        kapuaTad.setCardinality(null);
        Assert.assertEquals("tadImpl.cardinality", 0, (int) kapuaTad.getCardinality());
    }

    @Test
    public void setAndGetCardinalityRegularTest() {
        int[] permittedValues = { 1, 10, 100, 500, -2147483648, 2147483647, 0 };
        for (int value : permittedValues) {
            kapuaTad.setCardinality(value);
            Assert.assertEquals("tadImpl.cardinality", Integer.valueOf(value), kapuaTad.getCardinality());
        }
    }

    @Test
    public void setAndGetMinToNullTest() {
        kapuaTad.setMin(null);
        Assert.assertNull(kapuaTad.getMin());
    }

    @Test
    public void setAndGetMinTest() {
        String[] permittedValues = { "", "regularMin", "49", "regular Min", "regular min with spaces", "!@#$%&*()_+/->,<", "MIN", "min123" };
        for (String value : permittedValues) {
            kapuaTad.setMin(value);
            Assert.assertTrue(kapuaTad.getMin().contains(value));
        }
    }

    @Test
    public void setAndGetMaxToNullTest() {
        kapuaTad.setMax(null);
        Assert.assertNull(kapuaTad.getMax());
    }

    @Test
    public void setAndGetMaxTest() {
        String[] permittedValues = { "", "regularMax", "49", "regular Max", "regular max with spaces", "!@#$%&*()_+/->,<", "MAX", "max123" };
        for (String value : permittedValues) {
            kapuaTad.setMax(value);
            Assert.assertTrue(kapuaTad.getMax().contains(value));
        }
    }

    @Test
    public void setAndGetDefaultToNullTest() {
        kapuaTad.setDefault(null);
        Assert.assertNull(kapuaTad.getDefault());
    }

    @Test
    public void setAndGetDefaultTest() {
        String[] permittedValues = { "", "regularDefault", "49", "regular Default", "regular default with spaces", "!@#$%&*()_+/->,<", "DEFAULT", "default123" };
        for (String value : permittedValues) {
            kapuaTad.setDefault(value);
            Assert.assertTrue(kapuaTad.getDefault().contains(value));
        }
    }

    @Test
    public void setIsRequiredToNullTest() {
        kapuaTad.setRequired(null);
        Assert.assertTrue(kapuaTad.isRequired());
    }

    @Test
    public void setAndIsRequiredTest() {
        boolean[] permittedValues = { false, true };
        for (boolean value : permittedValues) {
            kapuaTad.setRequired(value);
            Assert.assertEquals("tadImpl.value", value, kapuaTad.isRequired());
        }
    }

    @Test
    public void putAndGetOtherAttributeTest() {
        Map<QName, String> expectedValues = new HashMap<>();

        expectedValues.put(QName.valueOf("1"), "a");
        expectedValues.put(QName.valueOf("2"), "b");
        expectedValues.put(QName.valueOf("3"), "c");

        kapuaTad.putOtherAttribute(QName.valueOf("1"), "a");
        kapuaTad.putOtherAttribute(QName.valueOf("2"), "b");
        kapuaTad.putOtherAttribute(QName.valueOf("3"), "c");

        Assert.assertEquals("tadImpl.attributes", expectedValues, kapuaTad.getOtherAttributes());
    }

    @Test
    public void getOtherAttributeTest() {
        Assert.assertTrue(kapuaTad.getOtherAttributes().isEmpty());
    }
}
