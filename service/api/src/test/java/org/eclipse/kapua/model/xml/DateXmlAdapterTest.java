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
package org.eclipse.kapua.model.xml;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


@Category(JUnitTests.class)
public class DateXmlAdapterTest {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String TIME_ZONE_UTC = "UTC";

    DateXmlAdapter dateXmlAdapter;
    Date date;

    @Before
    public void createInstanceOfClass() {
        dateXmlAdapter = new DateXmlAdapter();
        date = new Date();
    }

    @Test
    public void marshalTest() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
        String expectedString = dateFormat.format(date);
        Assert.assertEquals("Expected and actual values should be the same.", expectedString, dateXmlAdapter.marshal(date));
    }

    @Test
    public void marshalNullTest() throws Exception {
        Assert.assertNull("Null expected", dateXmlAdapter.marshal(null));
    }

    @Test
    public void unmarshalTest() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
        String dateStringValue = dateFormat.format(date);

        Assert.assertEquals("Expected and actual values should be the same.", date, dateXmlAdapter.unmarshal(dateStringValue));
    }

    @Test
    public void unmarshalNullOrEmptyStringsTest() {
        String[] nullOrEmptyStrings = {null, ""};
        for (String nullOrEmptyString : nullOrEmptyStrings) {
            Assert.assertNull("Null expected", dateXmlAdapter.unmarshal(nullOrEmptyString));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unmarshalInvalidTimeStringTest() {
        String invalidTimeString = "Mon Aug 17 10:26:15 CEST 2020";
        dateXmlAdapter.unmarshal(invalidTimeString);
    }
}
