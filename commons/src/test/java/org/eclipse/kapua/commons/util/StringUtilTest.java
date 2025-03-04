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
package org.eclipse.kapua.commons.util;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.model.config.metatype.Password;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class StringUtilTest {

    @Test
    public void splitValuesNullTest() {
        Assert.assertNull("Null expected.", StringUtil.splitValues(null));
    }

    @Test
    public void splitValuesTest() {
        String[] delimiterStrings = { "string ,aa", "s,tring", ",string", "string,", "string,,", ",,string", "str,in,g", ",str,in,g," };
        String[] escapeStrings = { "\\str \\ing", "\\string \\", "\\str ing", "s\\\\tring", " strin\\g", "st\\ring", "\\", "str\\ing", "stri\\ng", "strin\\g", "string\\", "\\\\" };
        String[] regularStrings = { "ss  sss               s", "string", "", "s tring", "s tri ng", "!@#$%^&*()_/.'|<>?|:1234567890" };

        String[][] expectedDelimiterStrings = { { "string", "aa" }, { "s", "tring" }, { "", "string" }, { "string", "" }, { "string", "", "" }, { "", "", "string" }, { "str", "in", "g" },
                { "", "str", "in", "g", "" } };
        String[][] expectedEscapeStrings = { { "str ing" }, { "string" }, { "str ing" }, { "s\\tring" }, { "string" }, { "string" }, { "" }, { "string" }, { "string" }, { "string" }, { "string" },
                { "\\" } };
        String[][] expectedRegularStrings = { { "ss  sss               s" }, { "string" }, { "" }, { "s tring" }, { "s tri ng" }, { "!@#$%^&*()_/.'|<>?|:1234567890" } };

        for (int i = 0; i < delimiterStrings.length; i++) {
            Assert.assertArrayEquals("Expected and actual values should be the same.", expectedDelimiterStrings[i], StringUtil.splitValues(delimiterStrings[i]));
        }

        for (int i = 0; i < escapeStrings.length; i++) {
            Assert.assertArrayEquals("Expected and actual values should be the same.", expectedEscapeStrings[i], StringUtil.splitValues(escapeStrings[i]));
        }

        for (int i = 0; i < regularStrings.length; i++) {
            Assert.assertArrayEquals("Expected and actual values should be the same.", expectedRegularStrings[i], StringUtil.splitValues(regularStrings[i]));
        }
    }

    @Test
    public void stringToValueNullStringTest() throws Exception {
        try {
            Assert.assertNull("Null expected.", StringUtil.stringToValue("stringType", null));
        } catch (KapuaIllegalArgumentException e) {
            Assert.fail("No exception expected for NULL string in StringUtil.stringToValue method");

        }
    }

    @Test
    public void stringToValueNullTypeTest() {
        try {
            StringUtil.stringToValue(null, "string");
        } catch (KapuaException e) {
            Assert.assertEquals("Expected and actual values should be the same.", KapuaException.internalError("Invalid type").toString(), e.toString());
        }
    }

    @Test
    public void stringToValueEmptyTypeTest() {
        try {
            StringUtil.stringToValue("", "string");
        } catch (KapuaException e) {
            Assert.assertEquals("Expected and actual values should be the same.", KapuaException.internalError("Invalid type").toString(), e.toString());
        }
    }

    @Test
    public void stringToValueIncorrectTypeTest() {
        try {
            StringUtil.stringToValue("Object", "string");
        } catch (Exception e) {
            Assert.assertEquals("Expected and actual values should be the same.", new IllegalArgumentException("Object").toString(), e.toString());
        }
    }

    @Test
    public void stringToValueStringTypeTest() throws Exception {
        String[] stringArray = new String[] { "a", "ab", "abc", "string", "String", "STRING", "!#$%&'()=?*QWERTYUIOPŠĐASDFGHJKLČĆŽZXCVBNM;:_>Z⁄@‹›€°·‚Test±}{∏Ø’”ÆæÒÔÓÌ~«¿ˇÈ¯Ñ◊" };
        String[] type = new String[] { "sTring", "strinG", "string", "STRING" };

        // Positive tests
        for (int k = 0; k < stringArray.length; k++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", stringArray[k], StringUtil.stringToValue("String", stringArray[k]));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + stringArray[k] + "string");
            }
        }

        // Negative tests
        for (int i = 0; i < stringArray.length; i++) {
            for (int j = 0; j < type.length; j++) {
                try {
                    StringUtil.stringToValue(type[j], stringArray[i]);
                    Assert.fail("Assert should fail as 'type' parameter is not valid");
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(type[j]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void stringToValueBooleanTypeTest() throws Exception {
        String booleanNegativeType[] = new String[] { "bOolean", "booleaN", "BOOLEAN" };

        String booleanTrueValues[] = new String[] { "true", "True", "tRue", "truE", "TRUE" };
        String booleanFalseValues[] = new String[] { "false", "False", "faLse", "fals", "FALSE" };

        // boolean.True positive tests
        for (int i = 0; i < booleanTrueValues.length; i++) {
            Assert.assertEquals("Expected and actual values should be the same.", Boolean.TRUE, StringUtil.stringToValue("Boolean", booleanTrueValues[i]));
        }

        // bolean.True positive tests
        for (int i = 0; i < booleanTrueValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", Boolean.TRUE, StringUtil.stringToValue("Boolean", booleanTrueValues[i]));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + booleanTrueValues[i]);
            }
        }
        // boolean.True negative tests
        for (int i = 0; i < booleanTrueValues.length; i++) {
            for (int j = 0; j < booleanNegativeType.length; j++) {
                try {
                    StringUtil.stringToValue(booleanNegativeType[j], booleanTrueValues[i]);
                    Assert.fail("Illegal argument provided for 'type' parameter in method stringToValue().");
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(booleanNegativeType[j]).toString(), e.toString());
                }
            }
        }

        //boolean.False positive tests
        for (int i = 0; i < booleanFalseValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", Boolean.FALSE, StringUtil.stringToValue("Boolean", booleanFalseValues[i]));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + booleanFalseValues[i]);
            }
        }

        // boolean.False negative tests
        for (int i = 0; i < booleanFalseValues.length; i++) {
            for (int j = 0; j < booleanNegativeType.length; j++) {
                try {
                    StringUtil.stringToValue(booleanNegativeType[j], booleanFalseValues[i]);
                    Assert.fail("Illegal argument provided for 'type' parameter in method stringToValue().");
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(booleanNegativeType[j]).toString(), e.toString());
                }

            }
        }
    }

    @Test
    public void stringToValueByteTypeTest() throws Exception {
        String[] byteTypeInvalidValues = new String[] { "bYte", "bytE", "BYTE" };
        String byteTypeValidValues = "Byte";
        Byte[] byteValues = new Byte[] { -128, 127, 0 };

        // Byte positive tests
        for (int i = 0; i < byteValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", byteValues[i], StringUtil.stringToValue(byteTypeValidValues, byteValues[i].toString()));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + byteValues[i]);
            }
        }

        // Byte negative tests
        for (int i = 0; i < byteTypeInvalidValues.length; i++) {
            for (int j = 0; j < byteValues.length; j++) {
                try {
                    StringUtil.stringToValue(byteTypeInvalidValues[i], byteValues[j].toString());
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(byteTypeInvalidValues[i]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void stringToValueCharTypeTest() throws Exception {
        Character[] characterArray = new Character[] { '!', '#', '$', '%', '&', '(', ')', '=', '?', '*', '/', '1', '2', '3', '4', 'A', 'B', 'C', 'a', 'b', 'c' };
        String[] charInvalidType = new String[] { "cHar", "chaR", "Character", "cHaracter", "CHARACTER", "CHAR" };
        String charValidType = "Char";

        // Character positive tests
        for (int i = 0; i < characterArray.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", characterArray[i], StringUtil.stringToValue(charValidType, String.valueOf(characterArray[i])));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + characterArray[i]);
            }
        }

        // Character negative tests
        for (int i = 0; i < characterArray.length; i++) {
            for (int j = 0; j < charInvalidType.length; j++) {
                try {
                    StringUtil.stringToValue(charInvalidType[j], String.valueOf(characterArray[i]));
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(charInvalidType[j]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void stringToValueDoubleTypeTest() throws Exception {
        String doubleValidType = "Double";
        String[] doubleInvalidType = new String[] { "dOuble", "doublE", "DOUBLE" };
        Double[] doubleValues = new Double[] { -1.781273812737812731273129312, 1.781273812737812731273129312, 0.0 };

        // Double positive tests
        for (int i = 0; i < doubleValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", doubleValues[i], StringUtil.stringToValue(doubleValidType, String.valueOf(doubleValues[i])));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + doubleValues[i]);
            }
        }

        // Double negative tests
        for (int i = 0; i < doubleValues.length; i++) {
            for (int j = 0; j < doubleInvalidType.length; j++) {
                try {
                    StringUtil.stringToValue(doubleInvalidType[j], String.valueOf(doubleValues[i]));
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(doubleInvalidType[j]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void stringToValueFloatTypeTest() throws Exception {
        String floatValidType = "Float";
        String[] floatinvalidType = new String[] { "fLoat", "floaT", "FLOAT" };
        Float[] floatValues = new Float[] { -1.7811233F, 1.7811233F, 0F };

        // Float positive tests
        for (int i = 0; i < floatValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", floatValues[i], StringUtil.stringToValue(floatValidType, String.valueOf(floatValues[i])));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + floatValues[i]);
            }
        }

        // Float negative tests
        for (int i = 0; i < floatValues.length; i++) {
            for (int j = 0; j < floatinvalidType.length; j++) {
                try {
                    StringUtil.stringToValue(floatinvalidType[j], String.valueOf(floatValues[i]));
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(floatinvalidType[j]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void stringToValueIntegerTypeTest() throws Exception {
        String integerValidType = "Integer";
        String[] integerInvalidType = new String[] { "iNteger", "integeR", "INTEGER" };
        Integer[] integerValues = new Integer[] { -2147483648, 2147483647, 0 };

        // Float positive tests
        for (int i = 0; i < integerValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", integerValues[i], StringUtil.stringToValue(integerValidType, String.valueOf(integerValues[i])));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + integerValues[i]);
            }
        }

        // Float negative tests
        for (int i = 0; i < integerValues.length; i++) {
            for (int j = 0; j < integerInvalidType.length; j++) {
                try {
                    StringUtil.stringToValue(integerInvalidType[j], String.valueOf(integerValues[i]));
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(integerInvalidType[j]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void stringToValueLongTypeTest() throws Exception {
        String longValidType = "Long";
        String[] longInvalidType = new String[] { "lOng", "lonG", "LONG" };
        Long[] longValues = new Long[] { -922337203685477600L, 922337203685477600L, 0L };

        // Long positive tests
        for (int i = 0; i < longValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", longValues[i], StringUtil.stringToValue(longValidType, String.valueOf(longValues[i])));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + longValues[i]);
            }
        }

        // Long negative tests
        for (int i = 0; i < longValues.length; i++) {
            for (int j = 0; j < longInvalidType.length; j++) {
                try {
                    StringUtil.stringToValue(longInvalidType[j], String.valueOf(longValues[i]));
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(longInvalidType[j]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void stringToValueShortTypeTest() throws Exception {
        String shortValidType = "Short";
        String[] shortInvalidType = new String[] { "sHort", "shorT", "SHORT" };
        Short[] shortValues = new Short[] { -32768, 32767, 0 };

        // Short positive tests
        for (int i = 0; i < shortValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", shortValues[i], StringUtil.stringToValue(shortValidType, String.valueOf(shortValues[i])));
            } catch (Exception e) {
                Assert.fail("No exception expected for" + shortValues[i]);
            }
        }

        // Long negative tests
        for (int i = 0; i < shortValues.length; i++) {
            for (int j = 0; j < shortInvalidType.length; j++) {
                try {
                    StringUtil.stringToValue(shortInvalidType[j], String.valueOf(shortValues[i]));
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(shortInvalidType[j]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void stringToValuePasswordTypeTest() throws Exception {
        String passwordValidType = "Password";
        String[] passwordInvalidType = new String[] { "pAssword", "passworD", "PASSWORD" };
        Password password1 = new Password("a");
        Password password2 = new Password("aaaaaaaaaaaaaaaaaaaa");
        Password password3 = new Password("⁄@‹›€°·‚Short}{∏ÆæÒ”");
        Object[] passwordValues = new Object[] { password1, password2, password3 };
        // Password [] passwordValues = new Password[]{"a", "aaaaaaaaaaaaaaaaaaaa", "⁄@‹›€°·‚Short}{∏ÆæÒ”"};

        // Password positive tests
        for (int i = 0; i < passwordValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", String.valueOf(passwordValues[i]),
                        StringUtil.stringToValue(passwordValidType, String.valueOf(passwordValues[i])).toString());
            } catch (Exception e) {
                Assert.fail("No exception expected for" + passwordValues[i]);
            }
        }

        // Password negative tests
        for (int i = 0; i < passwordValues.length; i++) {
            for (int j = 0; j < passwordInvalidType.length; j++) {
                try {
                    StringUtil.stringToValue(passwordInvalidType[j], String.valueOf(passwordValues[i])).toString();
                } catch (Exception e) {
                    Assert.assertEquals("IllegalArgumentException expected.", new IllegalArgumentException(passwordInvalidType[j]).toString(), e.toString());
                }
            }
        }
    }

    @Test
    public void valueToStringNullTest() {
        Assert.assertNull("Null expected.", StringUtil.valueToString(null));
    }

    @Test
    public void valueToStringTypeStringTest() {
        String[] stringValues = new String[] { "", "s", "string", "STRING",
                "abcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweqabcdefgrtqyweuqywueuqeuuqweq" };

        for (int i = 0; i < stringValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", stringValues[i], StringUtil.valueToString(stringValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeLongTest() {
        Long[] longValues = new Long[] { 0L, 922337203685477600L, -922337203685477600L };
        for (int i = 0; i < longValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", longValues[i].toString(), StringUtil.valueToString(longValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeDoubleTest() {
        Double[] doubleValues = new Double[] { 0.0, 1.781273812737812731273129312, -1.781273812737812731273129312 };
        for (int i = 0; i < doubleValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", doubleValues[i].toString(), StringUtil.valueToString(doubleValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeFloatTest() {
        Float[] floatValues = new Float[] { 0F, 1.781123312321311231F, -1.7811233323123F };
        for (int i = 0; i < floatValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", floatValues[i].toString(), StringUtil.valueToString(floatValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeIntegerTest() {
        Integer[] integerValues = new Integer[] { 2147483647, -2147483647, 0 };
        for (int i = 0; i < integerValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", integerValues[i].toString(), StringUtil.valueToString(integerValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeByteTest() {
        Byte[] byteValues = new Byte[] { -128, 127, 0 };
        for (int i = 0; i < byteValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", byteValues[i].toString(), StringUtil.valueToString(byteValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeCharacterTest() {
        Character[] characterValues = new Character[] { 'a', '1', '@' };
        for (int i = 0; i < characterValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", characterValues[i].toString(), StringUtil.valueToString(characterValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeBooleanTest() {
        Boolean[] booleanValues = new Boolean[] { Boolean.TRUE, Boolean.FALSE };
        for (int i = 0; i < booleanValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", booleanValues[i].toString(), StringUtil.valueToString(booleanValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeShortTest() {
        Short[] shortValues = new Short[] { -32768, 32767, 0 };
        for (int i = 0; i < shortValues.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", shortValues[i].toString(), StringUtil.valueToString(shortValues[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypePassword() {
        Password password1 = new Password("abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPRQSTUVXYZ");
        Password password2 = new Password("01234567890");
        Password password3 = new Password("!#$%&'()=?*/+-.,<>;:_⁄@‹›€°·‚Password±}{∏¿ˇÈ~");
        Password[] passwordValue = new Password[] { password1, password2, password3 };
        for (int i = 0; i < passwordValue.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", passwordValue[i].toString(), StringUtil.valueToString(passwordValue[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for  method valueToString()");
            }
        }
    }

    @Test
    public void valueToStringTypeStringArrayTest() {
        String[] stringArray = new String[] { null, "string", "string2", "string3" };
        String resultString = "string,string2,string3";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(stringArray));
    }

    @Test
    public void valueToStringTypeLongArrayTest() {
        Long[] longArray = new Long[] { null, 0L, 1L, 12L, 123L, 1234L, 12345L, 123456L, 1234567L, 922337203685477600L, -1L, -12L, -123L, -1234L, -12345L, -922337203685477600L };
        String resultString = "0,1,12,123,1234,12345,123456,1234567,922337203685477600,-1,-12,-123,-1234,-12345,-922337203685477600";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(longArray));
    }

    @Test
    public void valueToStringTypeDoubleArrayTest() {
        Double[] doubleArray = new Double[] { null, 0.0, 1.1, 1.23, 1.234, 1.2345, 1.23456, 1.234567, 1.7812738127378127 };
        String resultString = "0.0,1.1,1.23,1.234,1.2345,1.23456,1.234567,1.7812738127378127";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(doubleArray));
    }

    @Test
    public void valueToStringTypeFloatArrayTest() {
        Float[] floatArray = new Float[] { null, 0.0F, 1.0F, 1.2F, 12.34F, 123.45F, 1234.56F, 12345.6789F, 1.7811233F, -0.0F, -1.23F, -12345.12345F };
        String resultString = "0.0,1.0,1.2,12.34,123.45,1234.56,12345.679,1.7811233,-0.0,-1.23,-12345.123";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(floatArray));
    }

    @Test
    public void valueToStringTypeIntegerArrayTest() {
        Integer[] integerArray = new Integer[] { null, 0, 1, 123, 12345, 12345, 32678, -0, -1, -12, -1234, -12345, -32676 };
        String resultString = "0,1,123,12345,12345,32678,0,-1,-12,-1234,-12345,-32676";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(integerArray));
    }

    @Test
    public void valueToStringTypeByteArrayTest() {
        Byte[] byteArray = new Byte[] { null, 0, 1, 12, 123, 127, -0, -1, -12, -123, -128 };
        String resultString = "0,1,12,123,127,0,-1,-12,-123,-128";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(byteArray));
    }

    @Test
    public void valueToStringTypeCharacterArrayTest() {
        Character[] characterArray = new Character[] { null, 'a', 'A', 'z', 'Z', '@', '!' };
        String resultString = "a,A,z,Z,@,!";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(characterArray));
    }

    @Test
    public void valueToStringTypeBooleanArrayTest() {
        Boolean[] booleanArray = new Boolean[] { null, Boolean.FALSE, Boolean.TRUE };
        String resultString = "false,true";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(booleanArray));
    }

    @Test
    public void valueToStringTypeShortArrayTest() {
        Short[] shortArray = new Short[] { null, 0, 1, 12, 123, 1234, 32767, -32768, -1234, -123, -12, -1, -0 };
        String resultString = "0,1,12,123,1234,32767,-32768,-1234,-123,-12,-1,0";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(shortArray));
    }

    @Test
    public void valueToStringTypePasswordArrayTest() {
        Password password1 = new Password("abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPRQSTUVXYZ");
        Password password2 = new Password("01234567890");
        Password password3 = new Password("!#$%&'()=?*/+-.<>;:_⁄@‹›€°·‚Password±}{∏¿ˇÈ~");
        Password[] passwordArray = new Password[] { null, password1, password2, password3 };
        String resultString = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPRQSTUVXYZ,01234567890,!#$%&'()=?*/+-.<>;:_⁄@‹›€°·‚Password±}{∏¿ˇÈ~";
        Assert.assertEquals("Expected and actual values should be the same.", resultString, StringUtil.valueToString(passwordArray));
    }

    @Test
    public void valueToStringObjectTest() {
        Object object = new Object();
        Assert.assertNull("Null expected.", StringUtil.valueToString(object));
    }

    @Test
    public void escapeStringTest() {
        String escapeString = "\\, ";
        String expectedString = "\\\\\\,\\ ";
        Assert.assertEquals("Expected and actual values should be the same.", expectedString, StringUtil.escapeString(escapeString));
    }

    @Test
    public void escapeStringNullTest() {
        try {
            StringUtil.escapeString(null);
        } catch (Exception e) {
            Assert.assertEquals("Expected and actual values should be the same.", new NullPointerException().toString(), e.toString());
        }
    }

    @Test
    public void unescapeStringTest() {
        String[] stringArray = new String[] { "        string", "string      ", "   str  ing", "str  ing   ", "str ing", "str\\ ing", "str\\\\\\,ing", "   str\\ \\, ing   " };
        String[] resultString = new String[] { "string", "string", "str  ing", "str  ing", "str ing", "str ing", "str,ing", "str , ing" };
        for (int i = 0; i < stringArray.length; i++) {
            try {
                Assert.assertEquals("Expected and actual values should be the same.", resultString[i], StringUtil.unescapeString(stringArray[i]));
            } catch (Exception e) {
                Assert.fail("Illegal argument provided for method escapedString()");
            }
        }
    }

    @Test
    public void unescapeStringNullTest() {
        try {
            StringUtil.unescapeString(null);
        } catch (Exception e) {
            Assert.assertEquals("Expected and actual values should be the same.", new NullPointerException().toString(), e.toString());
        }
    }
}
