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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@Category(JUnitTests.class)
@RunWith(value = Parameterized.class)
public class AttributePredicateTest {

    private final String attributeName;

    private final AttributePredicate.Operator operator;

    public AttributePredicateTest(String attributeName, AttributePredicate.Operator operator) {
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
    public void attributePredicateImplTest() {
        String attributeValueStr = "";
        AttributePredicate<String> attributePredicateStr = new AttributePredicate<>(attributeName, attributeValueStr);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateStr.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValueStr, attributePredicateStr.getAttributeValue());

        Integer attributeValueInt = 1234567890;
        AttributePredicate<Integer> attributePredicateInt = new AttributePredicate<>(attributeName, attributeValueInt);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateInt.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValueInt, attributePredicateInt.getAttributeValue());

        Double attributeValueDouble = 123.23d;
        AttributePredicate<Double> attributePredicateDouble = new AttributePredicate<>(attributeName, attributeValueDouble);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateDouble.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValueDouble, attributePredicateDouble.getAttributeValue());

        Character attributeValueChar = 'a';
        AttributePredicate<Character> attributePredicateChar = new AttributePredicate<>(attributeName, attributeValueChar);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateChar.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValueChar, attributePredicateChar.getAttributeValue());

        Long attributeValueLong = 12345678910L;
        AttributePredicate<Long> attributePredicateLong = new AttributePredicate<>(attributeName, attributeValueLong);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateLong.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValueLong, attributePredicateLong.getAttributeValue());

        AttributePredicate<Boolean> attributePredicateBooleanTrue = new AttributePredicate<>(attributeName, true);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateBooleanTrue.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", true, attributePredicateBooleanTrue.getAttributeValue());
        AttributePredicate<Boolean> attributePredicateBooleanFalse = new AttributePredicate<>(attributeName, false);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateBooleanFalse.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", false, attributePredicateBooleanFalse.getAttributeValue());

        Short attributeValueShort = Short.MAX_VALUE;
        AttributePredicate<Short> attributePredicateShort = new AttributePredicate<>(attributeName, attributeValueShort);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateShort.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValueShort, attributePredicateShort.getAttributeValue());

        Float attributeValueFloat = 123.23f;
        AttributePredicate<Float> attributePredicateFloat = new AttributePredicate<>(attributeName, attributeValueFloat);
        Assert.assertEquals("Actual and expected values are not the same!", attributeName, attributePredicateFloat.getAttributeName());
        Assert.assertEquals("Actual and expected values are not the same!", attributeValueFloat, attributePredicateFloat.getAttributeValue());
    }

    @Test
    public void getOperatorTest() {
        AttributePredicate<Integer> attributePredicate = new AttributePredicate<>(attributeName, 1234567890, operator);
        Assert.assertEquals("Actual and expected values are not the same!", operator, attributePredicate.getOperator());
    }
}
