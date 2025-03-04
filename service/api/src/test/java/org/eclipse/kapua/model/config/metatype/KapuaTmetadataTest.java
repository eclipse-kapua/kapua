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
public class KapuaTmetadataTest {

    @Before
    public void createInstanceOfClasses() {
        emptyTocd = new EmptyTocd();
        tocd = new KapuaTocd();
        tmetadata = new KapuaTmetadata();
        tdesignate = new KapuaTdesignate();
    }

    EmptyTocd emptyTocd;
    KapuaTocd tocd;
    KapuaTmetadata tmetadata;
    KapuaTdesignate tdesignate;

    @Test
    public void getOCDTest() {
        Assert.assertTrue(tmetadata.getOCD().isEmpty());
    }

    @Test
    public void setAndGetOCDTest() {
        List<KapuaTocd> ocd = new ArrayList<>();
        ocd.add(tocd);
        ocd.add(emptyTocd);
        tmetadata.setOCD(ocd);
        Assert.assertEquals("tmetadata.ocd", ocd, tmetadata.getOCD());
    }

    @Test
    public void getDesignateTest() {
        Assert.assertTrue(tmetadata.getDesignate().isEmpty());
    }

    @Test
    public void setAndGetDesignateTest() {
        List<KapuaTdesignate> designate = new ArrayList<>();
        designate.add(tdesignate);
        tmetadata.setDesignate(designate);
        Assert.assertEquals("tmetadata.designate", designate, tmetadata.getDesignate());
    }

    @Test
    public void getAnyTest() {
        Assert.assertTrue(tmetadata.getAny().isEmpty());
    }

    @Test
    public void setAndGetAnyTest() {
        List<Object> options = new ArrayList<>();
        options.add(tocd);
        options.add(emptyTocd);
        tmetadata.setAny(options);
        Assert.assertEquals("tmetadata.ocd", options, tmetadata.getAny());
    }

    @Test
    public void setAndGetLocalizationToNullTest() {
        tmetadata.setLocalization(null);
        Assert.assertNull(tmetadata.getLocalization());
    }

    @Test
    public void setAndGetLocalizationTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularLocalization", "regular Localization", "49", "regularLocalization49", "LOCALIZATION",
                "246465494135646120009090049684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            tmetadata.setLocalization(value);
            Assert.assertTrue(tmetadata.getLocalization().contains(value));
        }
    }

    @Test
    public void testGetOtherAttribute() {
        Assert.assertTrue(tmetadata.getOtherAttributes().isEmpty());
    }

    @Test
    public void testSetAndGetOtherAttribute() {
        Map<QName, String> values = new HashMap<>();

        values.put(QName.valueOf("1"), "a");
        values.put(QName.valueOf("2"), "b");
        values.put(QName.valueOf("3"), "c");

        tmetadata.setOtherAttributes(values);
        Assert.assertEquals("tmetadata.otherAttributes", values, tmetadata.getOtherAttributes());
    }
}
