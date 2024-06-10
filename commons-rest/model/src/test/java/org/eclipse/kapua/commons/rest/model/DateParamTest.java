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

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class DateParamTest {

    @Test
    public void dateParamTest() throws KapuaIllegalArgumentException {
        String[] dates = { "2020-10-27T21:32:52", " 2020-10-27T21:32:52+02:00", " 2020-10-27T19:32:52Z", " 2020-10-27T19:32:52+00:00" };

        for (int i = 0; i < dates.length; i++) {
            DateParam dateParam = new DateParam(dates[i]);
            Assert.assertNotNull("Null not expected.", dateParam.getDate());
        }
    }

    @Test(expected = NullPointerException.class)
    public void dateParamNullTest() throws KapuaIllegalArgumentException {
        DateParam dateParam = new DateParam(null);

        dateParam.getDate();
    }

    @Test(expected = KapuaIllegalArgumentException.class)
    public void dateParamInvalidDateTest() throws KapuaIllegalArgumentException {
        String[] invalidDates = { "", "2020-10-27", " 2020-10-27T21:32 ", " 2020-10-27T25:32:52+02:00", " 2020-10-27T21:32" };

        for (String invalidDate : invalidDates) {
            new DateParam(invalidDate);
        }
    }
} 