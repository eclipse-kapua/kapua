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
package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.model.config.metatype.KapuaTscalar;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class ValueTokenizerTest {

    @Test
    public void valueTokenizerNullStringTest() throws Exception {
        try {
            Assert.assertNotNull(new ValueTokenizer(null));
        } catch (Exception ex) {
            Assert.fail("Exception not expected for parameter");
        }
    }

    @Test
    public void valueTokenizerNotNullTest() throws Exception {
        try {
            Assert.assertNotNull(new ValueTokenizer("valueTokenizerConstructor"));
        } catch (Exception ex) {
            Assert.fail("No exception expected");
        }
    }

    //tests for method ...getValuesAsString();
    @Test
    public void valueTokenizerDelimeterTest() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a123,aaa,rrr");
        try {
            Assert.assertEquals("a123,aaa,rrr", valueTokenizer.getValuesAsString());
        } catch (Exception ex) {
            Assert.fail("Failed to delimit input string");
        }
    }

    @Test
    public void valueTokenizerEscapeTest() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a123\\aaa\\rrr");
        try {
            Assert.assertEquals("a123aaarrr", valueTokenizer.getValuesAsString());
        } catch (Exception ex) {
            Assert.fail("Failed to load escape string");
        }
    }

    @Test
    public void valueTokenizerEscapeAtEndTest() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a123aaarrr\\");
        try {
            Assert.assertEquals("a123aaarrr", valueTokenizer.getValuesAsString());
        } catch (Exception ex) {
            Assert.fail("Failed to load escape string");
        }
    }

    @Test
    public void valueTokenizerEscapeAtBeginningTest() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("\\a123aaarrr");
        try {
            valueTokenizer.getValuesAsString();
            Assert.assertEquals("a123aaarrr", valueTokenizer.getValuesAsString());
        } catch (Exception ex) {
            Assert.fail("Failed to load escape string");
        }
    }

    @Test
    public void valueTokenizerSpaceAtBeginningTest() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer(" a123aaarrr");
        try {
            Assert.assertEquals("a123aaarrr", valueTokenizer.getValuesAsString());
        } catch (Exception ex) {
            Assert.fail("Cannot load string whit white space at the beginning");
        }
    }

    @Test
    public void valueTokenizerSpaceAtTheEndTest() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a123aaarrr ");
        try {
            Assert.assertEquals("a123aaarrr", valueTokenizer.getValuesAsString());
        } catch (Exception ex) {
            Assert.fail("Cannot load string whit white space at the end");
        }
    }

    @Test
    public void valueTokenizerValuesAsStringNullTest() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer(null);
        try {
            Assert.assertNull(valueTokenizer.getValuesAsString());
        } catch (Exception ex) {
            Assert.fail("The value is not NULL");
        }
    }

    @Test
    public void valueTokenizerValueAStringSpaceMiddleTest() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a 123");
        try {
            Assert.assertEquals("a 123", valueTokenizer.getValuesAsString());
        } catch (Exception ex) {
            Assert.fail("Cannot convert value to string");
        }
    }

    //tests for method ...getValuesAsArray();
    @Test
    public void valueTokenizerValuesAsArrayWithDelimeter() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a,123,aaa,rrr");
        String[] expectedValues = new String[] { "a", "123", "aaa", "rrr" };
        try {
            Assert.assertArrayEquals(expectedValues, valueTokenizer.getValuesAsArray());
        } catch (Exception ex) {
            Assert.fail("Arrays are not equal");
        }
    }

    @Test
    public void valueTokenizerValuesAsArrayWithEscape() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a123\\aaarr\\r");
        String[] expectedValues = new String[] { "a123aaarrr" };
        try {
            Assert.assertArrayEquals(expectedValues, valueTokenizer.getValuesAsArray());
        } catch (Exception ex) {
            Assert.fail("Arrays are not equal");
        }
    }

    @Test
    public void valueTokenizerValuesAsArrayWithNullString() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer(null);
        try {
            Assert.assertNull(valueTokenizer.getValuesAsArray());
        } catch (Exception ex) {
            Assert.fail("Arrays are not equal");
        }
    }

    @Test
    public void valueTokenizerValueAsArrayDelimiterAndEscape() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a12\\3,a\\,aa,r\\rr,");
        String[] expectedValues = new String[] { "a123", "a,aa", "rrr", "" };
        try {
            Assert.assertArrayEquals(expectedValues, valueTokenizer.getValuesAsArray());
        } catch (Exception ex) {
            Assert.fail("Arrays are not equal");
        }
    }

    @Test
    public void valueTokenizerValueAsArray() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer(" a123, aaa, rrr");
        String[] expectedValues = new String[] { "a123", "aaa", "rrr" };
        try {
            Assert.assertArrayEquals(expectedValues, valueTokenizer.getValuesAsArray());
        } catch (Exception ex) {
            Assert.fail("Arrays are not equal");
        }
    }

    //test for method ...getValues();
    @Test
    public void valueTokenizerGetValuesWithDelimeter() throws Exception {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a123,aaarrr");
        String[] expectedValues = new String[] { "a123", "aaarrr" };
        try {
            Assert.assertArrayEquals(expectedValues, valueTokenizer.getValues().toArray());
        } catch (Exception ex) {
            Assert.fail("Arrays are not equal");
        }
    }

    @Test
    public void validateWithNullValueTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf");
        String message = valueTokenizer.validate(null);
        Assert.assertEquals("Internal error: null", message);
    }

    @Test
    public void validateWithNullValueTokenizerTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer(null);
        Assert.assertEquals("Null cannot be validated", valueTokenizer.validate(null));
    }

    @Test
    public void validateZeroCardinalityTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf, qwer");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(0);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Cardinality violation: \"asdf,qwer\" has 2 value(s) but must have between 1 and 1 value(s).", message);
    }

    @Test
    public void validateOneCardinalityTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf, qwer");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(1);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Cardinality violation: \"asdf,qwer\" has 2 value(s) but must have between 0 and 1 value(s).", message);
    }

    @Test
    public void validateEmptyStringZeroCardinalityTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(0);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Internal error: null", message);
    }

    @Test
    public void validateEmptyStringNonZeroCardinalityTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(2);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Internal error: null", message);
    }

    @Test
    public void validateNegativeCardinalityTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf, qwer");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(-1);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Cardinality violation: \"asdf,qwer\" has 2 value(s) but must have between 0 and 1 value(s).", message);
    }

    @Test
    public void validateWithoutTypeTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf, qwer");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Internal error: null", message);
    }

    @Test
    public void validateWithBooleanTypeTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf, qwer");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.BOOLEAN);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateWithStringTypeWithoutRestrictionsTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf, qwer");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.STRING);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateWithStringTypeWithMaxRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf, qwer");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.STRING);
        tad.setMax("2");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value asdf is out of range", message);
    }

    // This test currently returns error.
/*    @Test
    public void validateWithStringTypeWithMinRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("asdf, qwer");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.STRING);
        tad.setMin("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value asdf is out of range", message);
    }*/

    @Test
    public void validateWithIntegerTypeWithoutRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.INTEGER);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateMinAndMaxIntegerRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.INTEGER);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateCornerCasesIntegerRestrictionsTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 100");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.INTEGER);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateEqualMinAndMaxIntegerRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 10");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.INTEGER);
        tad.setMin("10");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateMixedMinAndMaxIntegerRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("21");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.INTEGER);
        tad.setMin("100");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 21 is out of range", message);
    }

    @Test
    public void validateWithIntegerTypeWitMaxRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.INTEGER);
        tad.setMax("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 99 is out of range", message);
    }

    @Test
    public void validateWithIntegerTypeWitMinRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.INTEGER);
        tad.setMin("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 12 is out of range", message);
    }

    @Test
    public void validateWithLongTypeWithoutRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.LONG);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateWithLongTypeWitMaxRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.LONG);
        tad.setMax("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 99 is out of range", message);
    }

    @Test
    public void validateWithLongTypeWitMinRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.LONG);
        tad.setMin("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 12 is out of range", message);
    }

    @Test
    public void validateMinAndMaxLongRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.LONG);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateCornerCasesLongRestrictionsTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 100");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.LONG);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateEqualMinAndMaxLongRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 10");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.LONG);
        tad.setMin("10");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateMixedMinAndMaxLongRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("21");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.LONG);
        tad.setMin("100");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 21 is out of range", message);
    }

    @Test
    public void validateWithDoubleTypeWithoutRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.DOUBLE);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateWithDoubleTypeWitMaxRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.DOUBLE);
        tad.setMax("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 99 is out of range", message);
    }

    @Test
    public void validateWithDoubleTypeWitMinRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.DOUBLE);
        tad.setMin("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 12 is out of range", message);
    }

    @Test
    public void validateMinAndMaxDoubleRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.DOUBLE);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateCornerCasesDoubleRestrictionsTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 100");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.DOUBLE);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateEqualMinAndMaxDoubleRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 10");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.DOUBLE);
        tad.setMin("10");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateMixedMinAndMaxDoubleRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("21");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.DOUBLE);
        tad.setMin("100");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 21 is out of range", message);
    }

    @Test
    public void validateWithCharTypeWithoutRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a, z");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.CHAR);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateWithCharTypeWitMaxRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a, z");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.CHAR);
        tad.setMax("b");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value z is out of range", message);
    }

    @Test
    public void validateWithCharTypeWitMinRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("a, z");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.CHAR);
        tad.setMin("b");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value a is out of range", message);
    }

    @Test
    public void validateWithFloatTypeWithoutRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.FLOAT);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateWithFloatTypeWitMaxRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.FLOAT);
        tad.setMax("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 99 is out of range", message);
    }

    @Test
    public void validateWithFloatTypeWitMinRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.FLOAT);
        tad.setMin("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 12 is out of range", message);
    }

    @Test
    public void validateMinAndMaxFloatRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.FLOAT);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateCornerCasesFloatRestrictionsTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 100");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.FLOAT);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateEqualMinAndMaxFloatRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 10");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.FLOAT);
        tad.setMin("10");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateMixedMinAndMaxFloatRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("21");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.FLOAT);
        tad.setMin("100");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 21 is out of range", message);
    }

    @Test
    public void validateWithShortTypeWithoutRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.SHORT);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateWithShortTypeWitMaxRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.SHORT);
        tad.setMax("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 99 is out of range", message);
    }

    @Test
    public void validateWithShortTypeWitMinRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.SHORT);
        tad.setMin("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 12 is out of range", message);
    }

    @Test
    public void validateMinAndMaxShortRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.SHORT);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateCornerCasesShortRestrictionsTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 100");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.SHORT);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateEqualMinAndMaxShortRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 10");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.SHORT);
        tad.setMin("10");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateMixedMinAndMaxShortRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("21");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.SHORT);
        tad.setMin("100");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 21 is out of range", message);
    }

    @Test
    public void validateWithByteTypeWithoutRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.BYTE);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateWithByteTypeWitMaxRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.BYTE);
        tad.setMax("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 99 is out of range", message);
    }

    @Test
    public void validateWithByteTypeWitMinRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.BYTE);
        tad.setMin("50");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 12 is out of range", message);
    }

    @Test
    public void validateMinAndMaxByteRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.BYTE);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateCornerCasesByteRestrictionsTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 100");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.BYTE);
        tad.setMin("10");
        tad.setMax("100");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateEqualMinAndMaxByteRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("10, 10");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.BYTE);
        tad.setMin("10");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("", message);
    }

    @Test
    public void validateMixedMinAndMaxByteRestrictionTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("21");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.BYTE);
        tad.setMin("100");
        tad.setMax("10");
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Value 21 is out of range", message);
    }

    @Test
    public void validateTest() {
        ValueTokenizer valueTokenizer = new ValueTokenizer("12, 99");
        KapuaTad tad = new KapuaTad();
        tad.setCardinality(10);
        tad.setType(KapuaTscalar.FLOAT);
        KapuaToption option = null;
        tad.addOption(option);
        String message = valueTokenizer.validate(tad);
        Assert.assertEquals("Internal error: null", message);
    }
}
