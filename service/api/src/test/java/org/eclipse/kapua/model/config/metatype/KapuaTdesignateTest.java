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

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaTdesignateTest {

    @Before
    public void createInstanceOfClass() {

        tdesignate = new KapuaTdesignate();
    }

    KapuaTdesignate tdesignate;

    @Test
    public void setAndGetObjectToNullTest() {
        tdesignate.setObject(null);
        Assert.assertNull(tdesignate.getObject());
    }

    @Test
    public void setAndGetObjectTest() {
        KapuaTobject tobject = new KapuaTobject();
        tdesignate.setObject(tobject);
        Assert.assertEquals("tdesignate.object", tobject, tdesignate.getObject());
    }

    @Test
    public void getAnyTest() {
        Assert.assertTrue(tdesignate.getAny().isEmpty());
    }

    @Test
    public void setAndGetAnyToNullTest() {
        tdesignate.setAny(null);
        Assert.assertTrue(tdesignate.getAny().isEmpty());
    }

    @Test
    public void setAndGetAnyTest() {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        objectArrayList.add("String");
        objectArrayList.add(2L);
        objectArrayList.add(100);
        objectArrayList.add(11122.33);
        objectArrayList.add(true);
        objectArrayList.add(false);
        objectArrayList.add(new KapuaToption());

        tdesignate.setAny(objectArrayList);
        Assert.assertEquals("tdesignate.any", objectArrayList, tdesignate.getAny());
    }

    @Test
    public void setAndGetPidToNullTest() {
        tdesignate.setPid(null);
        Assert.assertNull(tdesignate.getPid());
    }

    @Test
    public void setAndGetPidTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularPid", "regular Pid", "49", "regularPid49", "PID", "2 46 465494 13564612 0009 09004 9684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            tdesignate.setPid(value);
            Assert.assertTrue(tdesignate.getPid().contains(value));
        }
    }

    @Test
    public void setAndGetFactoryPidToNullTest() {
        tdesignate.setFactoryPid(null);
        Assert.assertNull(tdesignate.getFactoryPid());
    }

    @Test
    public void setAndGetFactoryPidTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularPid", "regular Pid", "49", "regularPid49", "PID", "2 46 465494 13564612 0009 09004 9684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            tdesignate.setFactoryPid(value);
            Assert.assertTrue(tdesignate.getFactoryPid().contains(value));
        }
    }

    @Test
    public void setAndGetBundleToNullTest() {
        tdesignate.setBundle(null);
        Assert.assertNull(tdesignate.getBundle());
    }

    @Test
    public void setAndGetBundleTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularBundle", "regular Bundle", "49", "regularBundle49", "BUNDLE",
                "2 46 465494 13564612 0009 09004 9684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            tdesignate.setBundle(value);
            Assert.assertTrue(tdesignate.getBundle().contains(value));
        }
    }

    @Test
    public void setAndGetOptionalToNullTest() {
        tdesignate.setOptional(null);
        Assert.assertFalse(tdesignate.isOptional());
    }

    @Test
    public void setAndIsOptionalTest() {
        boolean[] permittedValues = { false, true };
        for (boolean value : permittedValues) {
            tdesignate.setOptional(value);
            Assert.assertEquals("tdesignate.isOptional", value, tdesignate.isOptional());
        }
    }

    @Test
    public void setAndGetMergeToNullTest() {
        tdesignate.setMerge(null);
        Assert.assertFalse(tdesignate.isMerge());
    }

    @Test
    public void setAndIsMergeTest() {
        boolean[] permittedValues = { false, true };
        for (boolean value : permittedValues) {
            tdesignate.setMerge(value);
            Assert.assertEquals("tdesignate.isMerge", value, tdesignate.isMerge());
        }
    }

    @Test
    public void getOtherAttributesTest() {
        Assert.assertTrue(tdesignate.getOtherAttributes().isEmpty());
    }

    @Test
    public void setAndGetOtherAttributesToNullTest() {
        tdesignate.setOtherAttributes(null);
        Assert.assertNull(tdesignate.getOtherAttributes());
    }
}
