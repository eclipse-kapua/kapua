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
public class CountResultTest {

    @Test
    public void countResultWithoutParameterTest() {
        CountResult countResult = new CountResult();
        Assert.assertEquals("Expected and actual values should be the same.", 0, countResult.getCount());
    }

    @Test
    public void countResultWithParameterTest() {
        long[] countValues = { -9223372036854775808L, -10000000L, -10, -1L, 0L, 1L, 10, 9223372036854775807L };

        for (long countValue : countValues) {
            CountResult countResult = new CountResult(countValue);
            Assert.assertEquals("Expected and actual values should be the same.", countValue, countResult.getCount());
        }
    }

    @Test
    public void setAndGetCount() {
        long[] countValues = { -9223372036854775808L, -10000000L, -10L, -1L, 0L, 1L, 10L, 9223372036854775807L };
        CountResult countResult1 = new CountResult();
        CountResult countResult2 = new CountResult(100L);

        for (long countValue : countValues) {
            countResult1.setCount(countValue);
            countResult2.setCount(countValue);

            Assert.assertEquals("Expected and actual values should be the same.", countValue, countResult1.getCount());
            Assert.assertEquals("Expected and actual values should be the same.", countValue, countResult2.getCount());
        }
    }
} 