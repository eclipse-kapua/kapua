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
package org.eclipse.kapua.commons.rest.model;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class ByteArrayParamTest {

    @Test
    public void byteArrayParamTest() {
        String[] base64encoded = { "", "Some0-)9 t  ext    12", "1text5--=<>67890", "!@#$stri%^ng&12*()", "   _ +' string;.text,|<>", "  te5432<> string 98   <>..,,, d",
                " ,,:!2text 09 00238*&^  ,," };
        for (String encoded : base64encoded) {
            ByteArrayParam byteArrayParam = new ByteArrayParam(encoded);
            Assert.assertNotNull("NotNull expected.", byteArrayParam.getValue());
        }
    }

    @Test
    public void byteArrayParamNullTest() {
        ByteArrayParam byteArrayParam = new ByteArrayParam(null);
        Assert.assertNull("Null expected.", byteArrayParam.getValue());
    }
} 