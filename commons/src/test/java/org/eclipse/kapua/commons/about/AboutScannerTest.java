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
package org.eclipse.kapua.commons.about;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;


@Category(JUnitTests.class)
public class AboutScannerTest {

    @Test
    public void scanTest() {
        final AboutScanner scanner = AboutScanner.scan();
        Assert.assertNotNull("Null not expected", scanner);
    }

    @Test
    public void getEntriesTest() throws NoSuchFieldException, IllegalAccessException {
        final AboutScanner aboutScanner = AboutScanner.scan();
        List<AboutEntry> aboutEntry = new LinkedList<>();
        final Field field = aboutScanner.getClass().getDeclaredField("entries");
        field.setAccessible(true);
        field.set(aboutScanner, aboutEntry);
        final List<AboutEntry> result = aboutScanner.getEntries();

        Assert.assertEquals("Field wasn't retrieved properly", result, aboutEntry);
    }

    @Test
    public void scanWithParameterTest() {
        AboutScanner scanner = AboutScanner.scan(ClasspathHelper.forClassLoader().stream());
        Assert.assertNotNull("Null not expected", scanner);

        NullPointerException nullPointerException = new NullPointerException();
        try {
            AboutScanner invalidScanner = AboutScanner.scan(null);
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected", e instanceof NullPointerException);
        }
    }
} 
