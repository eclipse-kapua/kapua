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
package org.eclipse.kapua.app.api.core.model.data;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.XmlAdaptedSortField;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;


@Category(JUnitTests.class)
public class JsonMessageQueryTest {

    KapuaId kapuaId;
    JsonMessageQuery jsonMessageQuery1, jsonMessageQuery2;
    StorableFetchStyle[] fetchStyles;

    @Before
    public void initialize() {
        kapuaId = KapuaId.ONE;
        jsonMessageQuery1 = new JsonMessageQuery();
        jsonMessageQuery2 = new JsonMessageQuery(kapuaId);
        fetchStyles = new StorableFetchStyle[]{StorableFetchStyle.FIELDS, StorableFetchStyle.SOURCE_FULL, StorableFetchStyle.SOURCE_SELECT};
    }

    @Test
    public void jsonMessageQueryWithoutParameterTest() {
        JsonMessageQuery jsonMessageQuery = new JsonMessageQuery();

        Assert.assertNull("Null expected.", jsonMessageQuery.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", StorableFetchStyle.SOURCE_FULL, jsonMessageQuery.getFetchStyle());
        Assert.assertNotNull("NotNull expected.", jsonMessageQuery.getFetchAttributes());
        Assert.assertFalse("False expected.", jsonMessageQuery.isAskTotalCount());
    }

    @Test
    public void jsonMessageQueryWithParameterTest() {
        JsonMessageQuery jsonMessageQuery = new JsonMessageQuery(kapuaId);

        Assert.assertEquals("Expected and actual values should be the same.", kapuaId, jsonMessageQuery.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", StorableFetchStyle.SOURCE_FULL, jsonMessageQuery.getFetchStyle());
        Assert.assertNotNull("NotNull expected.", jsonMessageQuery.getFetchAttributes());
        Assert.assertFalse("False expected.", jsonMessageQuery.isAskTotalCount());
    }

    @Test
    public void jsonMessageQueryWithNullParameterTest() {
        JsonMessageQuery jsonMessageQuery = new JsonMessageQuery(null);

        Assert.assertNull("Null expected.", jsonMessageQuery.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", StorableFetchStyle.SOURCE_FULL, jsonMessageQuery.getFetchStyle());
        Assert.assertNotNull("NotNull expected.", jsonMessageQuery.getFetchAttributes());
        Assert.assertFalse("False expected.", jsonMessageQuery.isAskTotalCount());
    }

    @Test
    public void setAndGetScopeIdTest() {
        jsonMessageQuery1.setScopeId(KapuaId.ANY);
        jsonMessageQuery2.setScopeId(KapuaId.ANY);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, jsonMessageQuery1.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, jsonMessageQuery2.getScopeId());

        jsonMessageQuery1.setScopeId(null);
        jsonMessageQuery2.setScopeId(null);

        Assert.assertNull("Null expected.", jsonMessageQuery1.getScopeId());
        Assert.assertNull("Null expected.", jsonMessageQuery2.getScopeId());
    }

    @Test
    public void setAndGetPredicateTest() {
        StorablePredicate storablePredicate = Mockito.mock(StorablePredicate.class);

        jsonMessageQuery1.setPredicate(storablePredicate);
        jsonMessageQuery2.setPredicate(storablePredicate);

        Assert.assertEquals("Expected and actual values should be the same.", storablePredicate, jsonMessageQuery1.getPredicate());
        Assert.assertEquals("Expected and actual values should be the same.", storablePredicate, jsonMessageQuery2.getPredicate());

        jsonMessageQuery1.setPredicate(null);
        jsonMessageQuery2.setPredicate(null);

        Assert.assertNull("Null expected.", jsonMessageQuery1.getPredicate());
        Assert.assertNull("Null expected.", jsonMessageQuery2.getPredicate());
    }

    @Test
    public void setAndGetOffsetTest() {
        Integer[] offsets = {-2147483648, -1000000, -10, 0, 10, 1000000, 2147483647};

        for (Integer offset : offsets) {
            jsonMessageQuery1.setOffset(offset);
            jsonMessageQuery2.setOffset(offset);

            Assert.assertEquals("Expected and actual values should be the same.", offset, jsonMessageQuery1.getOffset());
            Assert.assertEquals("Expected and actual values should be the same.", offset, jsonMessageQuery2.getOffset());
        }

        jsonMessageQuery1.setOffset(null);
        jsonMessageQuery2.setOffset(null);

        Assert.assertNull("Null expected.", jsonMessageQuery1.getOffset());
        Assert.assertNull("Null expected.", jsonMessageQuery2.getOffset());
    }

    @Test
    public void setAndGetLimitTest() {
        Integer[] limits = {-2147483648, -1000000, -10, 0, 10, 1000000, 2147483647};

        for (Integer limit : limits) {
            jsonMessageQuery1.setLimit(limit);
            jsonMessageQuery2.setLimit(limit);

            Assert.assertEquals("Expected and actual values should be the same.", limit, jsonMessageQuery1.getLimit());
            Assert.assertEquals("Expected and actual values should be the same.", limit, jsonMessageQuery2.getLimit());
        }

        jsonMessageQuery1.setLimit(null);
        jsonMessageQuery2.setLimit(null);

        Assert.assertNull("Null expected.", jsonMessageQuery1.getLimit());
        Assert.assertNull("Null expected.", jsonMessageQuery2.getLimit());
    }

    @Test
    public void setAndIsAskTotalCountTest() {
        boolean[] askTotalCounts = {true, false};

        for (boolean askTotalCount : askTotalCounts) {
            jsonMessageQuery1.setAskTotalCount(askTotalCount);
            jsonMessageQuery2.setAskTotalCount(askTotalCount);

            Assert.assertEquals("Expected and actual values should be the same.", askTotalCount, jsonMessageQuery1.isAskTotalCount());
            Assert.assertEquals("Expected and actual values should be the same.", askTotalCount, jsonMessageQuery2.isAskTotalCount());
        }
    }

    @Test
    public void setAndGetFetchStyleTest() {
        for (StorableFetchStyle fetchStyle : fetchStyles) {
            jsonMessageQuery1.setFetchStyle(fetchStyle);
            jsonMessageQuery2.setFetchStyle(fetchStyle);

            Assert.assertEquals("Expected and actual values should be the same.", fetchStyle, jsonMessageQuery1.getFetchStyle());
            Assert.assertEquals("Expected and actual values should be the same.", fetchStyle, jsonMessageQuery2.getFetchStyle());
        }
        jsonMessageQuery1.setFetchStyle(null);
        jsonMessageQuery2.setFetchStyle(null);

        Assert.assertNull("Null expected.", jsonMessageQuery1.getFetchStyle());
        Assert.assertNull("Null expected.", jsonMessageQuery2.getFetchStyle());
    }

    @Test
    public void setGetAndAddFetchAttributesTest() {
        List<String> fetchAttributeNames = new LinkedList<>();
        List<String> expectedValue = new LinkedList<>();
        String attributeName1 = "AttributeName1";
        String attributeName2 = "AttributeName2";
        String attributeName3 = "AttributeName3";

        fetchAttributeNames.add(attributeName1);
        fetchAttributeNames.add(attributeName2);

        expectedValue.add(attributeName1);
        expectedValue.add(attributeName2);

        jsonMessageQuery1.setFetchAttributes(fetchAttributeNames);
        jsonMessageQuery2.setFetchAttributes(fetchAttributeNames);

        Assert.assertEquals("Expected and actual values should be the same.", expectedValue, jsonMessageQuery1.getFetchAttributes());
        Assert.assertEquals("Expected and actual values should be the same.", expectedValue, jsonMessageQuery2.getFetchAttributes());

        jsonMessageQuery1.addFetchAttributes(attributeName3);
        jsonMessageQuery2.addFetchAttributes(attributeName3);

        expectedValue.add(attributeName3);
        expectedValue.add(attributeName3);

        Assert.assertEquals("Expected and actual values should be the same.", expectedValue, jsonMessageQuery1.getFetchAttributes());
        Assert.assertEquals("Expected and actual values should be the same.", expectedValue, jsonMessageQuery2.getFetchAttributes());

        jsonMessageQuery1.setFetchAttributes(null);
        jsonMessageQuery2.setFetchAttributes(null);

        Assert.assertNull("Null expected.", jsonMessageQuery1.getFetchAttributes());
        Assert.assertNull("Null expected.", jsonMessageQuery2.getFetchAttributes());
    }

    @Test
    public void setAndGetSortFieldsTest() {
        List<XmlAdaptedSortField> sortFields = new LinkedList<>();
        XmlAdaptedSortField xmlAdaptedSortField1 = Mockito.mock(XmlAdaptedSortField.class);
        XmlAdaptedSortField xmlAdaptedSortField2 = Mockito.mock(XmlAdaptedSortField.class);
        sortFields.add(xmlAdaptedSortField1);
        sortFields.add(xmlAdaptedSortField2);

        jsonMessageQuery1.setSortFields(sortFields);
        jsonMessageQuery2.setSortFields(sortFields);

        Assert.assertEquals("Expected and actual values should be the same.", sortFields, jsonMessageQuery1.getSortFields());
        Assert.assertEquals("Expected and actual values should be the same.", sortFields, jsonMessageQuery2.getSortFields());

        jsonMessageQuery1.setSortFields(null);
        jsonMessageQuery2.setSortFields(null);

        Assert.assertNull("Null expected.", jsonMessageQuery1.getSortFields());
        Assert.assertNull("Null expected.", jsonMessageQuery2.getSortFields());
    }

    @Test
    public void getIncludesTest() {
        for (StorableFetchStyle fetchStyle : fetchStyles) {
            Assert.assertNotNull("NotNull expected.", jsonMessageQuery1.getIncludes(fetchStyle));
            Assert.assertNotNull("NotNull expected.", jsonMessageQuery2.getIncludes(fetchStyle));
        }
    }

    @Test
    public void getIncludesNullTest() {
        try {
            jsonMessageQuery1.getIncludes(null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }

        try {
            jsonMessageQuery2.getIncludes(null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test
    public void getExcludesTest() {
        for (StorableFetchStyle fetchStyle : fetchStyles) {
            Assert.assertNotNull("NotNull expected.", jsonMessageQuery1.getExcludes(fetchStyle));
            Assert.assertNotNull("NotNull expected.", jsonMessageQuery2.getExcludes(fetchStyle));
        }
    }

    @Test
    public void getExcludesNullTest() {
        try {
            jsonMessageQuery1.getExcludes(null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }

        try {
            jsonMessageQuery2.getExcludes(null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test
    public void getFieldsTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 6, jsonMessageQuery1.getFields().length);
        Assert.assertEquals("Expected and actual values should be the same.", 6, jsonMessageQuery2.getFields().length);
    }
} 