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
package org.eclipse.kapua.integration.misc;

import org.apache.shiro.authc.AuthenticationToken;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.shiro.AccessTokenCredentialsImpl;
import org.eclipse.kapua.service.authentication.shiro.realm.AccessTokenAuthenticatingRealm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class AccessTokenAuthenticationRealmTest {

    AccessTokenAuthenticatingRealm accessTokenAuthenticatingRealm;

    @Before
    public void initialize() throws KapuaException {
        accessTokenAuthenticatingRealm = new AccessTokenAuthenticatingRealm();
    }

    @Test
    public void accessTokenAuthenticatingRealmTest() {
        Assert.assertEquals("Expected and actual values should be the same.", "accessTokenAuthenticatingRealm", accessTokenAuthenticatingRealm.getName());
        Assert.assertNotNull("NotNull expected.", accessTokenAuthenticatingRealm.getCredentialsMatcher());
    }

    @Test
    public void supportsTrueTest() {
        AccessTokenCredentialsImpl authenticationToken = new AccessTokenCredentialsImpl("token id");
        Assert.assertTrue("True expected.", accessTokenAuthenticatingRealm.supports(authenticationToken));
    }

    @Test
    public void supportsFalseTest() {
        AuthenticationToken authenticationToken = Mockito.mock(AuthenticationToken.class);
        Assert.assertFalse("False expected.", accessTokenAuthenticatingRealm.supports(authenticationToken));
    }
}