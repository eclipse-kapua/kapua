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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.KapuaSortCriteria;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.model.query.predicate.OrPredicate;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

@Category(JUnitTests.class)
@RunWith(value = Parameterized.class)
public class KapuaQueryTest {

    private final String attributeName;

    private final AttributePredicate.Operator operator;

    public KapuaQueryTest(String attributeName, AttributePredicate.Operator operator) {
        this.attributeName = attributeName;
        this.operator = operator;
    }

    @Parameters
    public static Collection<Object[]> attributeNamesAndOperators() {
        return Arrays.asList(new Object[][] {
                { "", AttributePredicate.Operator.EQUAL },
                { "NAME", AttributePredicate.Operator.NOT_EQUAL },
                { "attributeName", AttributePredicate.Operator.IS_NULL },
                { "attribute name", AttributePredicate.Operator.NOT_NULL },
                { "0123456789", AttributePredicate.Operator.GREATER_THAN },
                { "!#$%&'()=?⁄@‹›€°·‚,.-;:_Èˇ¿<>«‘”’ÉØ∏{}|ÆæÒuF8FFÔÓÌÏÎÅ«»Ç◊Ñˆ¯Èˇ", AttributePredicate.Operator.GREATER_THAN_OR_EQUAL },
                { "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefg",
                        AttributePredicate.Operator.STARTS_WITH },
                { "ATTRIBUTE NAME", AttributePredicate.Operator.LIKE },
                { "name123", AttributePredicate.Operator.LESS_THAN },
                { "#$attribute", AttributePredicate.Operator.LESS_THAN_OR_EQUAL },
        });
    }

    @Test
    public void abstractKapuaQueryScopeIdTest() {
        KapuaId scopeId = new KapuaEid();
        KapuaQuery kapuaQuery = new KapuaQuery(scopeId);
        Assert.assertEquals("Actual and expected values are not the same!", scopeId, kapuaQuery.getScopeId());
    }

    @Test
    public void abstractKapuaQueryQueryIdTest() {
        KapuaId scopeId = new KapuaEid();
        List<String> fetchAttributes = new ArrayList<>();
        QueryPredicate predicate = Mockito.mock(AndPredicate.class);
        SortOrder sortOrder = SortOrder.ASCENDING;
        KapuaSortCriteria sortCriteria = new FieldSortCriteria(attributeName, sortOrder);
        Integer limit = 48;
        Integer offset = 56;
        KapuaQuery kapuaQuery = new KapuaQuery(scopeId);
        kapuaQuery.setFetchAttributes(fetchAttributes);
        kapuaQuery.setPredicate(predicate);
        kapuaQuery.setLimit(limit);
        kapuaQuery.setOffset(offset);
        kapuaQuery.setSortCriteria(sortCriteria);
        KapuaQuery kapuaCopyQuery = new KapuaQuery(kapuaQuery);
        Assert.assertEquals("Actual and expected values are not the same!", kapuaQuery.getFetchAttributes(), kapuaCopyQuery.getFetchAttributes());
        Assert.assertEquals("Actual and expected values are not the same!", kapuaQuery.getPredicate(), kapuaCopyQuery.getPredicate());
        Assert.assertEquals("Actual and expected values are not the same!", kapuaQuery.getLimit(), kapuaCopyQuery.getLimit());
        Assert.assertEquals("Actual and expected values are not the same!", kapuaQuery.getOffset(), kapuaCopyQuery.getOffset());
        Assert.assertEquals("Actual and expected values are not the same!", kapuaQuery.getSortCriteria(), kapuaCopyQuery.getSortCriteria());
    }

    @Test
    public void addFetchAttributesTest() {
        KapuaQuery kapuaQuery = new KapuaQuery();
        String emptyFetchAttribute = "";
        String fetchAttribute = "fetchAttribute";
        kapuaQuery.addFetchAttributes(null);
        Assert.assertEquals("Actual and expected values are not the same!", null, kapuaQuery.getFetchAttributes().get(0));
        kapuaQuery.addFetchAttributes(fetchAttribute);
        Assert.assertEquals("Actual and expected values are not the same!", fetchAttribute, kapuaQuery.getFetchAttributes().get(1));
        kapuaQuery.addFetchAttributes(emptyFetchAttribute);
        Assert.assertEquals("Actual and expected values are not the same!", emptyFetchAttribute, kapuaQuery.getFetchAttributes().get(2));
    }

    @Test
    public void attributePredicateTest() {
        KapuaQuery kapuaQuery = new KapuaQuery();
        Object attributeValue = new Object();
        AttributePredicate<Object> attributePredicate = kapuaQuery.attributePredicate(attributeName, attributeValue);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicate.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValue, attributePredicate.getAttributeValue());
    }

    @Test
    public void attributePredicateWithOperatorTest() {
        KapuaQuery kapuaQuery = new KapuaQuery();
        Object attributeValue = new Object();
        AttributePredicate<Object> attributePredicate = kapuaQuery.attributePredicate(attributeName, attributeValue, operator);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicate.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValue, attributePredicate.getAttributeValue());
        Assert.assertEquals("Actual and expected values are not the same!", operator, attributePredicate.getOperator());
    }

    @Test
    public void andPredicateTest() {
        KapuaQuery kapuaQuery = new KapuaQuery();
        AndPredicate andPredicate = kapuaQuery.andPredicate();
        ArrayList<QueryPredicate> queryPredicateArray = new ArrayList<>();
        Assert.assertEquals("Actual and expected values are not the same!", queryPredicateArray, andPredicate.getPredicates());
    }

    @Test
    public void andPredicateWithQueryPredicatesTest() {
        KapuaQuery kapuaQuery = new KapuaQuery();
        QueryPredicate[] queryPredicateArray = new QueryPredicate[10];
        for (int i = 0; i < queryPredicateArray.length; i++) {
            queryPredicateArray[i] = Mockito.mock(AndPredicate.class);
        }
        AndPredicate andPredicate = new AndPredicate(queryPredicateArray);
        Assert.assertEquals("Actual and expected values are not the same!", andPredicate.getPredicates(), kapuaQuery.andPredicate(queryPredicateArray).getPredicates());
    }

    @Test
    public void orPredicateTest() {
        KapuaQuery kapuaQuery = new KapuaQuery();
        OrPredicate orPredicate = kapuaQuery.orPredicate();
        ArrayList<QueryPredicate> queryPredicateArray = new ArrayList<>();
        Assert.assertEquals("Actual and expected values are not the same!", queryPredicateArray, orPredicate.getPredicates());
    }

    @Test
    public void orPredicateWithQueryPredicatesTest() {
        KapuaQuery kapuaQuery = new KapuaQuery();
        QueryPredicate[] queryPredicateArray = new QueryPredicate[5];
        for (int i = 0; i < queryPredicateArray.length; i++) {
            queryPredicateArray[i] = Mockito.mock(AndPredicate.class);
        }
        OrPredicate orPredicate = new OrPredicate(queryPredicateArray);
        Assert.assertEquals("Actual and expected values are not the same!", orPredicate.getPredicates(), kapuaQuery.orPredicate(queryPredicateArray).getPredicates());
    }

    @Test
    public void getAskTotalCountTest() {
        KapuaQuery kapuaQuery = new KapuaQuery();
        kapuaQuery.setAskTotalCount(true);
        Assert.assertEquals("Actual and expected values are not the same!", true, kapuaQuery.getAskTotalCount());
        kapuaQuery.setAskTotalCount(false);
        Assert.assertEquals("Actual and expected values are not the same!", false, kapuaQuery.getAskTotalCount());
    }
}
