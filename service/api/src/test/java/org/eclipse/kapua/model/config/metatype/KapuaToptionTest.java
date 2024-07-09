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

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaToptionTest {

    @Before
    public void createInstanceOfClass() {

        toption = new KapuaToption();
    }

    KapuaToption toption;

    @Test
    public void getAnyWithNullTest() {
        Assert.assertTrue(toption.getAny().isEmpty());
    }

    @Test
    public void setAndGetLabelToNullTest() {
        toption.setLabel(null);
        Assert.assertNull(toption.getLabel());
    }

    @Test
    public void setAndGetLabelTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularLabel", "regular Label", "49", "regularLabel49", "LABEL", "246465494135646120009090049684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            toption.setLabel(value);
            Assert.assertTrue(toption.getLabel().contains(value));
        }
    }

    @Test
    public void setAndGetValueToNullTest() {
        toption.setValue(null);
        Assert.assertNull(toption.getValue());
    }

    @Test
    public void setAndGetValueTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularValue", "regular Value", "49", "regularValue49", "VALUE", "246465494135646120009090049684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            toption.setValue(value);
            Assert.assertTrue(toption.getValue().contains(value));
        }
    }

    @Test
    public void getOtherAttributesTest() {
        Assert.assertTrue(toption.getOtherAttributes().isEmpty());
    }
}
