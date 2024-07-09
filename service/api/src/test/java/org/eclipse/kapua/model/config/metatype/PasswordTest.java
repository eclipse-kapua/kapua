/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.config.metatype;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class PasswordTest {

    @Test
    public void testGetPassword() throws Exception {
        Password password = new Password("pass");
        Password password2 = new Password(null);
        Assert.assertEquals("pass", password.getPassword());
        Assert.assertNull(password2.getPassword());
    }

    @Test
    public void testToString() {
        Password password = new Password("pass");
        Password password2 = new Password(null);
        Assert.assertEquals("pass", password.toString());
        Assert.assertNull(password2.getPassword());
    }
}
