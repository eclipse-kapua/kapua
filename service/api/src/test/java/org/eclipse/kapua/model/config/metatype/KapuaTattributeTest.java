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
public class KapuaTattributeTest {

    @Before
    public void createInstanceOfClass() {

        kapuaTattribute = new KapuaTattribute();
    }

    KapuaTattribute kapuaTattribute;

    @Test
    public void getValueTest() {
        Assert.assertTrue(kapuaTattribute.getValue().isEmpty());
    }

    @Test
    public void getAnyTest() {
        Assert.assertTrue(kapuaTattribute.getAny().isEmpty());
    }

    @Test
    public void setAndGetAdrefToNullTest() {
        kapuaTattribute.setAdref(null);
        Assert.assertNull(kapuaTattribute.getAdref());
    }

    @Test
    public void setAndGetAdrefTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularAdref", "regular Adref", "49", "regularAdref49", "ADREF", "246465494135646120009090049684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            kapuaTattribute.setAdref(value);
            Assert.assertTrue(kapuaTattribute.getAdref().contains(value));
        }
    }

    @Test
    public void setAndGetContentToNullTest() {
        kapuaTattribute.setContent(null);
        Assert.assertNull(kapuaTattribute.getContent());
    }

    @Test
    public void setAndGetContentTest() {
        String[] permittedValues = { "", "!@#$%^^&**(-()_)+/|", "regularContent", "regular Content", "49", "regularContent49", "CONTENT",
                "246465494135646120009090049684646496468456468496846464968496844" };
        for (String value : permittedValues) {
            kapuaTattribute.setContent(value);
            Assert.assertTrue(kapuaTattribute.getContent().contains(value));
        }
    }

    @Test
    public void getOtherAttributesTest() {
        Assert.assertTrue(kapuaTattribute.getOtherAttributes().isEmpty());
    }
}
