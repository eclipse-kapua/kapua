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
package org.eclipse.kapua.commons.setting;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;


@Category(JUnitTests.class)
public class AbstractBaseKapuaSettingTest {

    SimpleSettingKey key;
    Map<String, Object> map;
    MapConfiguration configuration;
    DataConfiguration dataConfiguration;
    AbstractBaseKapuaSetting abstractBaseKapuaSetting;
    Object[] objects;
    Class[] classes;

    @Before
    public void initialize() {
        key = new SimpleSettingKey("Key");
        map = new HashMap<>();
        configuration = new MapConfiguration(map);
        dataConfiguration = new DataConfiguration(configuration);
        abstractBaseKapuaSetting = new AbstractBaseKapuaSetting(dataConfiguration);
        objects = new Object[]{10, true, "String", 10L, 10.11f, 10.11d};
        classes = new Class[]{Integer.class, Boolean.class, String.class, Long.class, Float.class, Double.class};
    }

    @Test
    public void fromMapNullTest() {
        Assert.assertNull("Null expected.", AbstractBaseKapuaSetting.fromMap(null));
    }

    @Test
    public void fromMapEmptyMapTest() {
        Assert.assertThat("Instance of AbstractBaseKapuaSetting expected.", AbstractBaseKapuaSetting.fromMap(map), IsInstanceOf.instanceOf(AbstractBaseKapuaSetting.class));
        Assert.assertNotNull("Null not expected.", AbstractBaseKapuaSetting.fromMap(map).config);
        Assert.assertFalse("False expected.", AbstractBaseKapuaSetting.fromMap(map).isSystemPropertyHotSwap());
    }

    @Test
    public void fromMapTest() {
        map.put("Key", "Value");
        Assert.assertThat("Instance of AbstractBaseKapuaSetting expected.", AbstractBaseKapuaSetting.fromMap(map), IsInstanceOf.instanceOf(AbstractBaseKapuaSetting.class));
        Assert.assertNotNull("Null not expected.", AbstractBaseKapuaSetting.fromMap(map).config);
        Assert.assertFalse("False expected.", AbstractBaseKapuaSetting.fromMap(map).isSystemPropertyHotSwap());
        Assert.assertTrue("True expected.", AbstractBaseKapuaSetting.fromMap(map).config.containsKey("Key"));
    }

    @Test(expected = NullPointerException.class)
    public void abstractBaseKapuaSettingNullTest() {
        AbstractBaseKapuaSetting abstractBaseKapuaSetting = new AbstractBaseKapuaSetting(null);
    }

    @Test
    public void abstractBaseKapuaSettingEmptyMapTest() {
        Assert.assertEquals("Expected and actual values should be the same.", dataConfiguration, abstractBaseKapuaSetting.config);
        Assert.assertFalse("False expected.", abstractBaseKapuaSetting.isSystemPropertyHotSwap());
    }

    @Test
    public void abstractBaseKapuaSettingTest() {
        map.put("Key", 10);

        Assert.assertEquals("Expected and actual values should be the same.", dataConfiguration, abstractBaseKapuaSetting.config);
        Assert.assertFalse("False expected.", abstractBaseKapuaSetting.isSystemPropertyHotSwap());
        Assert.assertTrue("True expected.", abstractBaseKapuaSetting.config.containsKey("Key"));
    }

    @Test
    public void getNullClassEmptyMapTest() {
        Assert.assertNull("Null expected.", abstractBaseKapuaSetting.get(null, key));
    }

    @Test(expected = NullPointerException.class)
    public void getNullClassTest() {
        map.put("Key", 10);
        abstractBaseKapuaSetting.get(null, key);
    }

    @Test(expected = NullPointerException.class)
    public void getNullKeyTest() {
        abstractBaseKapuaSetting.get(String.class, null);
    }

    @Test
    public void getTest() {
        for (int i = 0; i < objects.length; i++) {
            map.put("Key", objects[i]);
            Assert.assertEquals("Expected and actual values should be the same.", objects[i], abstractBaseKapuaSetting.get(classes[i], key));
        }
    }

    @Test
    public void getDefaultValueNullClassEmptyMapTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.get(null, key, 10));
    }

    @Test(expected = NullPointerException.class)
    public void getDefaultValueNullClassTest() {
        map.put("Key", 10);
        abstractBaseKapuaSetting.get(null, key, 10);
    }

    @Test(expected = NullPointerException.class)
    public void getDefaultValueNullKeyTest() {
        abstractBaseKapuaSetting.get(String.class, null, 10);
    }

    @Test
    public void getNullDefaultValueTest() {
        for (int i = 0; i < objects.length; i++) {
            map.put("Key", objects[i]);
            Assert.assertEquals("Expected and actual values should be the same.", objects[i], abstractBaseKapuaSetting.get(classes[i], key, null));
        }
    }

    @Test
    public void getDefaultValueTest() {
        for (int i = 0; i < objects.length; i++) {
            map.put("Key", objects[i]);
            Assert.assertEquals("Expected and actual values should be the same.", objects[i], abstractBaseKapuaSetting.get(classes[i], key, 10));
        }
    }

    @Test
    public void getListNullClassEmptyMapTest() {
        Assert.assertThat("Instance of List expected.", abstractBaseKapuaSetting.getList(null, key), IsInstanceOf.instanceOf(List.class));
        Assert.assertTrue("True expected.", abstractBaseKapuaSetting.getList(null, key).isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void getListNullClassTest() {
        map.put("Key", 10);
        abstractBaseKapuaSetting.getList(null, key);
    }

    @Test(expected = NullPointerException.class)
    public void getListNullKeyTest() {
        abstractBaseKapuaSetting.getList(String.class, null);
    }

    @Test
    public void getListTest() {
        map.put("Key", 10);
        List expectedList = new LinkedList();
        expectedList.add(10);

        Assert.assertEquals("Expected and actual values should be the same.", expectedList, abstractBaseKapuaSetting.getList(Integer.class, key));
    }

    @Test
    public void getMapNullClassKeyRegexTest() {
        Assert.assertTrue("True expected.", abstractBaseKapuaSetting.getMap(null, key, "Key").isEmpty());

        map.put("Key", "Value");
        Map<String, Object> expectedMap = new HashMap();

        Assert.assertTrue("True expected.", abstractBaseKapuaSetting.getMap(null, key, "Key").isEmpty());
        Assert.assertEquals("Expected and actual values should be the same.", expectedMap, abstractBaseKapuaSetting.getMap(null, key, "Key"));

        map.put("Key.Key", "Value");
        try {
            abstractBaseKapuaSetting.getMap(null, key, "Key");
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test(expected = NullPointerException.class)
    public void getMapClassNullKeyRegexTest() {
        abstractBaseKapuaSetting.getMap(String.class, null, "Key");
    }

    @Test
    public void getMapClassKeyNullRegexTest() {
        Assert.assertTrue("True expected.", abstractBaseKapuaSetting.getMap(String.class, key, null).isEmpty());

        map.put("Key", "Value");
        try {
            abstractBaseKapuaSetting.getMap(String.class, key, null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }

        map.put("Key.Key", "Value");
        try {
            abstractBaseKapuaSetting.getMap(String.class, key, null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test
    public void getMapClassKeyRegexTest() {
        map.put("Key", "Value");
        Map<String, Object> expectedMap = new HashMap();

        Assert.assertTrue("True expected.", abstractBaseKapuaSetting.getMap(String.class, key, "Key").isEmpty());
        Assert.assertEquals("Expected and actual values should be the same.", expectedMap, abstractBaseKapuaSetting.getMap(String.class, key, "Key"));

        map.put("Key.Key", "Value");
        expectedMap.put("Key", "Value");

        Assert.assertFalse("False expected.", abstractBaseKapuaSetting.getMap(String.class, key, "Key").isEmpty());
        Assert.assertEquals("Expected and actual values should be the same.", expectedMap, abstractBaseKapuaSetting.getMap(String.class, key, "Key"));
    }

    @Test
    public void getMapNullClassKeyTest() {
        Assert.assertTrue("True expected.", abstractBaseKapuaSetting.getMap(null, key).isEmpty());

        map.put("Key.key", "Value");
        try {
            abstractBaseKapuaSetting.getMap(null, key);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test(expected = NullPointerException.class)
    public void getMapClassNullKeyTest() {
        abstractBaseKapuaSetting.getMap(String.class, null);
    }

    @Test
    public void getMapClassKeyTest() {
        map.put("Key.key", "Value");
        Map<String, Object> expectedMap = new HashMap();
        expectedMap.put("key", "Value");

        Assert.assertEquals("Expected and actual values should be the same.", expectedMap, abstractBaseKapuaSetting.getMap(String.class, key));
    }

    @Test(expected = NullPointerException.class)
    public void getIntNullTest() {
        abstractBaseKapuaSetting.getInt(null);
    }

    @Test
    public void getIntTest() {
        map.put("Key", 10);

        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key));

        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);
        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key));

        System.setProperty("Key", "10");
        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key));
        System.clearProperty("Key");
    }

    @Test(expected = NoSuchElementException.class)
    public void getIntEmptyMapTest() {
        abstractBaseKapuaSetting.getInt(key);
    }

    @Test(expected = NoSuchElementException.class)
    public void getIntDifferentKeyValueTest() {
        map.put("Other Key", 10);

        abstractBaseKapuaSetting.getInt(key);
    }

    @Test(expected = NullPointerException.class)
    public void getIntNullDefaultValueTest() {
        abstractBaseKapuaSetting.getInt(key, null);
    }

    @Test(expected = NullPointerException.class)
    public void getIntDefaultValueNullKeyTest() {
        abstractBaseKapuaSetting.getInt(null, 10);
    }

    @Test
    public void getIntDefaultValueTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 20, abstractBaseKapuaSetting.getInt(key, 20));

        map.put("Key", 10);
        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key, 10));

        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);
        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key, 10));

        System.setProperty("Key", "10");
        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key, 10));
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getIntNullDefaultValueIntegerClassTest() {
        abstractBaseKapuaSetting.getInt(key, (Integer) null);
    }

    @Test(expected = NullPointerException.class)
    public void getIntDefaultValueNullKeyIntegerClassTest() {
        abstractBaseKapuaSetting.getInt(null, (Integer) 10);
    }

    @Test
    public void getIntDefaultValueIntegerClassTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 20, abstractBaseKapuaSetting.getInt(key, (Integer) 20));

        map.put("Key", 10);
        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key, (Integer) 10));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key, (Integer) 10));
        System.setProperty("Key", "10");
        Assert.assertEquals("Expected and actual values should be the same.", 10, abstractBaseKapuaSetting.getInt(key, (Integer) 10));
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getBooleanNullTest() {
        abstractBaseKapuaSetting.getBoolean(null);
    }

    @Test
    public void getBooleanTest() {
        map.put("Key", true);

        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key));
        System.setProperty("Key", "true");
        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key));
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getBooleanNullDefaultValueTest() {
        abstractBaseKapuaSetting.getBoolean(key, null);
    }

    @Test(expected = NullPointerException.class)
    public void getBooleanDefaultValueNullKeyTest() {
        abstractBaseKapuaSetting.getBoolean(null, true);
    }

    @Test
    public void getBooleanDefaultValueTest() {
        Assert.assertFalse("Expected and actual values should be the same.", abstractBaseKapuaSetting.getBoolean(key, false));

        map.put("Key", true);
        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key, true));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key, true));
        System.setProperty("Key", "true");
        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key, true));
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getBooleanNullDefaultValueBooleanClassTest() {
        abstractBaseKapuaSetting.getBoolean(key, (Boolean) null);
    }

    @Test(expected = NullPointerException.class)
    public void getBooleanDefaultValueNullKeyBooleanClassTest() {
        abstractBaseKapuaSetting.getBoolean(null, (Boolean) true);
    }

    @Test
    public void getBooleanDefaultValueBooleanClassTest() {
        map.put("Key", true);

        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key, (Boolean) true));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key, (Boolean) true));
        System.setProperty("Key", "true");
        Assert.assertEquals("Expected and actual values should be the same.", true, abstractBaseKapuaSetting.getBoolean(key, (Boolean) true));
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getStringNullTest() {
        abstractBaseKapuaSetting.getString(null);
    }

    @Test
    public void getStringTest() {
        map.put("Key", "Value");

        Assert.assertEquals("Expected and actual values should be the same.", "Value", abstractBaseKapuaSetting.getString(key));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", "Value", abstractBaseKapuaSetting.getString(key));
        System.setProperty("Key", "Value");
        Assert.assertEquals("Expected and actual values should be the same.", "Value", abstractBaseKapuaSetting.getString(key));
        System.clearProperty("Key");
    }

    @Test
    public void getStringNullDefaultValueTest() {
        Assert.assertNull("Null expected.", abstractBaseKapuaSetting.getString(key, null));
        map.put("Key", "Value");
        Assert.assertEquals("Expected and actual values should be the same.", "Value", abstractBaseKapuaSetting.getString(key, null));
    }

    @Test(expected = NullPointerException.class)
    public void getStringDefaultValueNullKeyTest() {
        abstractBaseKapuaSetting.getString(null, "Value");
    }

    @Test
    public void getStringDefaultValueTest() {
        Assert.assertEquals("Expected and actual values should be the same.", "Default Value", abstractBaseKapuaSetting.getString(key, "Default Value"));

        map.put("Key", "Value");
        Assert.assertEquals("Expected and actual values should be the same.", "Value", abstractBaseKapuaSetting.getString(key, "Value"));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", "Value", abstractBaseKapuaSetting.getString(key, "Value"));
        System.setProperty("Key", "Value");
        Assert.assertEquals("Expected and actual values should be the same.", "Value", abstractBaseKapuaSetting.getString(key, "Value"));
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getLongNullTest() {
        abstractBaseKapuaSetting.getLong(null);
    }

    @Test
    public void getLongTest() {
        map.put("Key", 10L);

        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key));
        System.setProperty("Key", "10");
        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key));
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getLongNullDefaultValueTest() {
        abstractBaseKapuaSetting.getLong(key, null);
    }

    @Test(expected = NullPointerException.class)
    public void getLongDefaultValueNullKeyTest() {
        abstractBaseKapuaSetting.getLong(null, 10L);
    }

    @Test
    public void getLongDefaultValueTest() {
        map.put("Key", 10L);

        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key, 10L));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key, 10L));
        System.setProperty("Key", "10");
        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key, 10L));
        System.clearProperty("Key");
    }

    @Test
    public void getLongDefaultValueLongClassTest() {
        map.put("Key", 10L);

        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key, (Long) 10L));
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key, (Long) 10L));
        System.setProperty("Key", "10");
        Assert.assertEquals("Expected and actual values should be the same.", 10L, abstractBaseKapuaSetting.getLong(key, (Long) 10L));
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getLongNullDefaultValueLongClassTest() {
        abstractBaseKapuaSetting.getLong(key, (Long) null);
    }

    @Test(expected = NullPointerException.class)
    public void getLongDefaultValueNullKeyLongClassTest() {
        abstractBaseKapuaSetting.getLong(null, (Long) 10L);
    }

    @Test(expected = NullPointerException.class)
    public void getFloatNullTest() {
        abstractBaseKapuaSetting.getFloat(null);
    }

    @Test
    public void getFloatTest() {
        map.put("Key", 10.11);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key), 0.2);
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key), 0.2);
        System.setProperty("Key", "10.11");

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key), 0.2);
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getFloatNullDefaultValueTest() {
        abstractBaseKapuaSetting.getFloat(key, null);
    }

    @Test(expected = NullPointerException.class)
    public void getFloatDefaultValueNullKeyTest() {
        abstractBaseKapuaSetting.getFloat(null, 10.11f);
    }

    @Test
    public void getFloatDefaultValueTest() {
        map.put("Key", 10.11);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key, 10.11f), 0.2);
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key, 10.11f), 0.2);
        System.setProperty("Key", "10.11");
        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key, 10.11f), 0.2);
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getFloatNullDefaultValueFloatClassTest() {
        abstractBaseKapuaSetting.getFloat(key, (Float) null);
    }

    @Test(expected = NullPointerException.class)
    public void getFloatDefaultValueNullKeyFloatClassTest() {
        abstractBaseKapuaSetting.getFloat(null, (Float) 10.11f);
    }

    @Test
    public void getFloatDefaultValueFloatClassTest() {
        map.put("Key", 10.11);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key, (Float) 10.11f), 0.2);
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key, (Float) 10.11f), 0.2);
        System.setProperty("Key", "10.11");
        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getFloat(key, (Float) 10.11f), 0.2);
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getDoubleNullTest() {
        map.put("Key", 10.11);

        abstractBaseKapuaSetting.getDouble(null);
    }

    @Test
    public void getDoubleTest() {
        map.put("Key", 10.11);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key), 0.2);
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key), 0.2);
        System.setProperty("Key", "10.11");

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key), 0.2);
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getDoubleNullDefaultValueTest() {
        abstractBaseKapuaSetting.getDouble(key, null);
    }

    @Test(expected = NullPointerException.class)
    public void getDoubleDefaultValueNullKeyTest() {
        abstractBaseKapuaSetting.getDouble(null, 10.11);
    }

    @Test
    public void getDoubleDefaultValueTest() {
        map.put("Key", 10.11);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key, 10.11), 0.2);
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key, 10.11), 0.2);
        System.setProperty("Key", "10.11");
        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key, 10.11), 0.2);
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void getDoubleNullDefaultValueDoubleClassTest() {
        abstractBaseKapuaSetting.getDouble(key, (Double) null);
    }

    @Test(expected = NullPointerException.class)
    public void getDoubleDefaultValueNullKeyDoubleClassTest() {
        abstractBaseKapuaSetting.getDouble(null, (Double) 10.11);
    }

    @Test
    public void getDoubleDefaultValueDoubleClassTest() {
        map.put("Key", 10.11);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key, (Double) 10.11), 0.2);
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);

        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key, (Double) 10.11), 0.2);
        System.setProperty("Key", "10.11");
        Assert.assertEquals("Expected and actual values should be the same.", 10.11, abstractBaseKapuaSetting.getDouble(key, (Double) 10.11), 0.2);
        System.clearProperty("Key");
    }

    @Test(expected = NullPointerException.class)
    public void isSystemPropertyHotSwapNullTest() {
        abstractBaseKapuaSetting.setSystemPropertyHotSwap((Boolean) null);
    }

    @Test
    public void isSystemPropertyHotSwapTest() {
        Assert.assertFalse("False expected.", abstractBaseKapuaSetting.isSystemPropertyHotSwap());
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(true);
        Assert.assertTrue("True expected.", abstractBaseKapuaSetting.isSystemPropertyHotSwap());
        abstractBaseKapuaSetting.setSystemPropertyHotSwap(false);
        Assert.assertFalse("False expected.", abstractBaseKapuaSetting.isSystemPropertyHotSwap());
    }
}
