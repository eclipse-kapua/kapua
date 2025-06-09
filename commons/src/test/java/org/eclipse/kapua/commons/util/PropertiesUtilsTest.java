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

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Properties;

import org.eclipse.kapua.PropertiesUtils;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class PropertiesUtilsTest {

    @Test
    public void testConstructor() throws Exception {
        Constructor<PropertiesUtils> propertiesUtils = PropertiesUtils.class.getDeclaredConstructor();
        propertiesUtils.setAccessible(true);
        propertiesUtils.newInstance();
    }

    @Test
    public void readPropertiesFromStringTest() throws Exception {
        String[] inputString = new String[] { "StringString", "!string\n !string", "   #string\n String=S123", "     string!     Strin=g     ", "strin#g   :  string",
                "string         str1, \\\nstr2, \\\nstr3", "string\\ String 12 34", "123\\:\\=12%#34:String", "Str\ting String", "Strin\fg=String", "string\n " };
        String[] emptyOrNullString = new String[] { null, "" };

        Properties prop1 = new Properties();
        prop1.setProperty("StringString", "");
        Properties prop2 = new Properties();
        Properties prop3 = new Properties();
        prop3.setProperty("String", "S123");
        Properties prop4 = new Properties();
        prop4.setProperty("string!", "Strin=g     ");
        Properties prop5 = new Properties();
        prop5.setProperty("strin#g", "string");
        Properties prop6 = new Properties();
        prop6.setProperty("string", "str1, str2, str3");
        Properties prop7 = new Properties();
        prop7.setProperty("string String", "12 34");
        Properties prop8 = new Properties();
        prop8.setProperty("123:=12%#34", "String");
        Properties prop9 = new Properties();
        prop9.setProperty("Str", "ing String");
        Properties prop10 = new Properties();
        prop10.setProperty("Strin", "g=String");
        Properties prop11 = new Properties();
        prop11.setProperty("string", "");

        Properties invalidProp1 = new Properties();
        invalidProp1.setProperty("String", "String");
        Properties invalidProp2 = new Properties();
        invalidProp2.setProperty("!string", "!string");
        Properties invalidProp3 = new Properties();
        invalidProp3.setProperty("#string", "String=S123");
        Properties invalidProp4 = new Properties();
        invalidProp4.setProperty("string!     Strin", "g     ");
        Properties invalidProp5 = new Properties();
        invalidProp5.setProperty("strin#g", ":string");
        Properties invalidProp6 = new Properties();
        invalidProp6.setProperty("string", "str1");
        Properties invalidProp7 = new Properties();
        invalidProp7.setProperty("string", "String 12 34");
        Properties invalidProp8 = new Properties();
        invalidProp8.setProperty("123", "=12%#34:String");
        Properties invalidProp9 = new Properties();
        invalidProp9.setProperty("String", "String");
        Properties invalidProp10 = new Properties();
        invalidProp10.setProperty("String", "String");
        Properties invalidProp11 = new Properties();
        invalidProp11.setProperty("string", "string");

        Properties emptyProp1 = new Properties();
        Properties emptyProp2 = new Properties();
        emptyProp2.setProperty("", "");

        Properties[] validPropArray = new Properties[] { prop1, prop2, prop3, prop4, prop5, prop6, prop7, prop8, prop9, prop10, prop11 };
        Properties[] invalidPropArray = new Properties[] { invalidProp1, invalidProp2, invalidProp3, invalidProp4, invalidProp5, invalidProp6, invalidProp7, invalidProp8, invalidProp9, invalidProp10,
                invalidProp11 };

        //Positive tests
        for (int i = 0; i < inputString.length; i++) {
            try {
                Assert.assertEquals(validPropArray[i], PropertiesUtils.readPropertiesFromString(inputString[i]));
            } catch (AssertionError e) {
                Assert.fail("Assertion Error not expected for " + inputString[i]);
            }
        }
        for (int i = 0; i < emptyOrNullString.length; i++) {
            try {
                Assert.assertEquals(emptyProp1, PropertiesUtils.readPropertiesFromString(emptyOrNullString[i]));
            } catch (AssertionError e) {
                Assert.fail("Assertion Error not expected for " + emptyOrNullString[i]);
            }
        }

        //Negative tests
        for (int i = 0; i < inputString.length; i++) {
            try {
                Assert.assertEquals(invalidPropArray[i], PropertiesUtils.readPropertiesFromString(inputString[i]));
                Assert.fail("Assertion Error expected");
            } catch (AssertionError e) {
                //Expected
            }
        }

        for (int i = 0; i < emptyOrNullString.length; i++) {
            try {
                Assert.assertEquals(emptyProp2, PropertiesUtils.readPropertiesFromString(emptyOrNullString[i]));
                Assert.fail("Assertion Error expected");
            } catch (AssertionError e) {
                //Expected
            }
        }
    }

    @Test
    public void writePropertiesToStringTest() throws Exception {
        Properties prop1 = new Properties();
        Properties prop2 = new Properties();
        Properties prop3 = new Properties();
        Properties prop4 = new Properties();
        Properties prop5 = new Properties();

        prop1.setProperty("String", "=");
        prop2.setProperty("String:", " ff!String");
        prop3.setProperty("string", ":str");
        prop4.setProperty("st ring#a", "StriNG aaa';");
        prop5.setProperty(" String", "ssss12_ ;;     ");

        String lineSeparator = System.lineSeparator();

        String str1 = "#" + new Date().toString() + lineSeparator + "String=\\=" + lineSeparator;
        String str2 = "#" + new Date().toString() + lineSeparator + "String\\:=\\ ff\\!String" + lineSeparator;
        String str3 = "#" + new Date().toString() + lineSeparator + "string=\\:str" + lineSeparator;
        String str4 = "#" + new Date().toString() + lineSeparator + "st\\ ring\\#a=StriNG aaa';" + lineSeparator;
        String str5 = "#" + new Date().toString() + lineSeparator + "\\ String=ssss12_ ;;     " + lineSeparator;

        String emptyString = "";

        String invalidString1 = "#" + new Date().toString() + lineSeparator + "String=\\=";
        String invalidString2 = "String\\:=\\ ff\\!String";
        String invalidString3 = new Date().toString() + lineSeparator + "string=\\:str" + lineSeparator;
        String invalidString4 = "#" + new Date().toString() + lineSeparator + "st ring#a=StriNG aaa';" + lineSeparator;
        String invalidString5 = "#" + new Date().toString() + "\\ String=ssss12_ ;;     " + lineSeparator;

        String[] validOutputString = new String[] { str1, str2, str3, str4, str5 };
        String[] invalidOutputString = new String[] { invalidString1, invalidString2, invalidString3, invalidString4, invalidString5 };
        Properties[] propArray = new Properties[] { prop1, prop2, prop3, prop4, prop5 };

        //Positive tests
        for (int i = 0; i < propArray.length; i++) {
            try {
                Assert.assertEquals(validOutputString[i], PropertiesUtils.writePropertiesToString(propArray[i]));
            } catch (ComparisonFailure failure) {
                Assert.fail("Comparison Failure not expected for " + propArray[i]);
            }
        }

        try {
            Assert.assertNull(PropertiesUtils.writePropertiesToString(null));
        } catch (AssertionError e) {
            Assert.fail("Assertion Error not expected for null properties");
        }

        //Negative tests
        for (int i = 0; i < propArray.length; i++) {
            try {
                Assert.assertEquals(invalidOutputString[i], PropertiesUtils.writePropertiesToString(propArray[i]));
                Assert.fail("Comparison Failure expected");
            } catch (ComparisonFailure failure) {
                //Expected
            }
        }

        try {
            Assert.assertEquals(emptyString, PropertiesUtils.writePropertiesToString(null));
            Assert.fail("Assertion Error expected");
        } catch (AssertionError e) {
            //Expected
        }
    }
}
