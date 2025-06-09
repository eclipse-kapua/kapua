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
package org.eclipse.kapua.commons.model.query.predicate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class AndPredicateTest {

    @Test
    public void andPredicateImpl() {
        AndPredicate andPredicate = new AndPredicate();
        ArrayList<Object> array = new ArrayList<>();
        Assert.assertEquals("Actual and expected values are not the same!", array, andPredicate.getPredicates());
    }

    @Test
    public void andPredicateImplQueryPredicateId() {
        QueryPredicate queryPredicate = new AndPredicate();
        AndPredicate andPredicate = new AndPredicate(queryPredicate);
        Assert.assertEquals("Actual and expected values are not the same!", queryPredicate, andPredicate.getPredicates().get(0));
        QueryPredicate queryPredicate1 = new AndPredicate();
        QueryPredicate queryPredicate2 = new AndPredicate();
        QueryPredicate queryPredicate3 = new AndPredicate();
        QueryPredicate[] queryPredicateArray = { queryPredicate1, queryPredicate2, queryPredicate3 };
        AndPredicate andPredicateWithMultiplePredicates = new AndPredicate(queryPredicate1, queryPredicate2, queryPredicate3);
        for (int i = 0; i < queryPredicateArray.length; i++) {
            Assert.assertEquals("Actual and expected values are not the same!", queryPredicateArray[i], andPredicateWithMultiplePredicates.getPredicates().get(i));
        }
    }

    @Test
    public void andTest() {
        AndPredicate andPredicate = new AndPredicate();
        QueryPredicate queryPredicate = new AndPredicate();
        andPredicate.and(queryPredicate);
        Assert.assertEquals("Actual and expected values are not the same!", queryPredicate, andPredicate.getPredicates().get(0));
    }

    @Test
    public void andWithNullPredicateTest() {
        AndPredicate andPredicate = new AndPredicate();
        andPredicate.and(null);
        Assert.assertNull(andPredicate.getPredicates().get(0));
    }

    @Test
    public void getPredicatesTest() {
        AndPredicate andPredicate = new AndPredicate();
        List<QueryPredicate> predicates = new ArrayList<>();
        predicates.add(new AndPredicate());
        predicates.add(new AndPredicate());
        predicates.add(new AndPredicate());
        andPredicate.setPredicates(predicates);
        for (int i = 0; i < predicates.size(); i++) {
            Assert.assertEquals("Actual and expected values are not the same!", predicates.get(i), andPredicate.getPredicates().get(i));
        }
    }
}
