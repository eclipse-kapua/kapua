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
package org.eclipse.kapua.commons.model.query;

import java.util.Arrays;

import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@Category(JUnitTests.class)
@RunWith(value = Parameterized.class)
public class FieldSortCriteriaTest {

    private final String attributeName;
    private SortOrder sortOrder;

    public FieldSortCriteriaTest(String attributeName) {
        this.attributeName = attributeName;
    }

    @Parameters
    public static Iterable<Object[]> attributeNames() {
        return Arrays.asList(
                new Object[] { "" },
                new Object[] { "NAME" },
                new Object[] { "attributeName" },
                new Object[] { "attribute name" },
                new Object[] { "0123456789" },
                new Object[] { "!#$%&'()=?⁄@‹›€°·‚,.-;:_Èˇ¿<>«‘”’ÉØ∏{}|ÆæÒuF8FFÔÓÌÏÎÅ«»Ç◊Ñˆ¯Èˇ" },
                new Object[] {
                        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefg" });
    }

    @Test
    public void fieldSortCriteriaImplTest() {
        SortOrder sortOrderAscending = SortOrder.ASCENDING;
        FieldSortCriteria fieldSortCriteriaAscending = new FieldSortCriteria(attributeName, sortOrderAscending);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, fieldSortCriteriaAscending.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", sortOrderAscending, fieldSortCriteriaAscending.getSortOrder());
        SortOrder sortOrderDescending = SortOrder.DESCENDING;
        FieldSortCriteria fieldSortCriteriaDescending = new FieldSortCriteria(attributeName, sortOrderDescending);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, fieldSortCriteriaDescending.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", sortOrderDescending, fieldSortCriteriaDescending.getSortOrder());
    }
}
