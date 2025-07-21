/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.type;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Date;

/**
 * Tests for {@link DateConverter}
 *
 * @since 2.1.0
 */
@Category(JUnitTests.class)
public class DateConverterTest {

    Date date;
    String dateString;

    @Before
    public void initialize() {
        date = new Date(1735689600000L);

        dateString = "2025-01-01T00:00:00.000Z";
    }

    @Test
    public void toStringConvertDate() {
        String convertedDateString = DateConverter.toString(date);

        Assert.assertEquals(dateString, convertedDateString);
    }

    @Test
    public void toStringConvertDateNull() {
        String convertedDateString = DateConverter.toString(null);

        Assert.assertNull(convertedDateString);
    }

    @Test
    public void fromStringConvertString() {
        Date convertedDate = DateConverter.fromString(dateString);

        Assert.assertEquals(date, convertedDate);
    }

    @Test
    public void fromStringConvertStringNull() {
        Date convertedDate = DateConverter.fromString(null);

        Assert.assertNull(convertedDate);
    }

}
