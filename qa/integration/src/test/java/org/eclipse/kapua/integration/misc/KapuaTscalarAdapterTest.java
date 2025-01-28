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
package org.eclipse.kapua.integration.misc;

import org.eclipse.kapua.model.config.metatype.KapuaTscalar;
import org.eclipse.kapua.model.config.metatype.KapuaTscalarAdapter;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaTscalarAdapterTest {

    KapuaTscalarAdapter kapuaTscalarAdapter;
    KapuaTscalar[] kapuaTscalars;
    String[] stringValue;
    String[] invalidStringValue;

    @Before
    public void initialize() {
        kapuaTscalarAdapter = new KapuaTscalarAdapter();
        kapuaTscalars = new KapuaTscalar[] { KapuaTscalar.STRING, KapuaTscalar.LONG, KapuaTscalar.DOUBLE, KapuaTscalar.FLOAT, KapuaTscalar.INTEGER,
                KapuaTscalar.BYTE, KapuaTscalar.CHAR, KapuaTscalar.BOOLEAN, KapuaTscalar.SHORT, KapuaTscalar.PASSWORD };
        stringValue = new String[] { "String", "Long", "Double", "Float", "Integer", "Byte", "Char", "Boolean", "Short", "Password" };
        invalidStringValue = new String[] { null, "Invalid Value" };
    }

    @Test
    public void marshalTest() throws Exception {
        String[] expectedStringValue = stringValue;
        for (int i = 0; i < kapuaTscalars.length; i++) {
            Assert.assertEquals("Expected and actual values should be the same.", expectedStringValue[i], kapuaTscalarAdapter.marshal(kapuaTscalars[i]));
        }
    }

    @Test(expected = NullPointerException.class)
    public void marshalNullParameterTest() throws Exception {
        kapuaTscalarAdapter.marshal(null);
    }

    @Test
    public void unmarshalTest() throws Exception {
        KapuaTscalar[] expectedTscalars = kapuaTscalars;
        for (int i = 0; i < stringValue.length; i++) {
            Assert.assertEquals("Expected and actual values should be the same.", expectedTscalars[i], kapuaTscalarAdapter.unmarshal(stringValue[i]));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unmarshalInvalidParameterValueTest() throws Exception {
        for (String invalidValue : invalidStringValue) {
            kapuaTscalarAdapter.unmarshal(invalidValue);
        }
    }
}
