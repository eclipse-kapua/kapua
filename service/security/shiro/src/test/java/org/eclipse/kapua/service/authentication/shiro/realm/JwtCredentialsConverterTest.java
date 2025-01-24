/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.shiro.realm;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.JwtCredentials;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.shiro.JwtCredentialsImpl;
import org.eclipse.kapua.service.authentication.shiro.realm.model.NotProcessableCredentials;
import org.eclipse.kapua.service.authentication.shiro.realm.model.NotProcessableCredentialsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class JwtCredentialsConverterTest {

    JwtCredentialsConverter instance;

    @Before
    public void setUp() {
        instance = new JwtCredentialsConverter();
    }

    @Test
    public void jwtCredentialsImplCanProcessNullTest() {
        Assert.assertFalse(instance.canProcess(null));
    }

    @Test
    public void jwtCredentialsImplCanProcessImplTest() throws KapuaAuthenticationException {
        JwtCredentials jwtCredentialsImpl = new JwtCredentials("aJwt", "anIdToken");
        NotProcessableCredentials notProcessableCredentials = new NotProcessableCredentialsImpl();

        Assert.assertTrue(instance.canProcess(jwtCredentialsImpl));
        Assert.assertFalse(instance.canProcess(notProcessableCredentials));
    }

    @Test
    public void jwtCredentialsImplMapToShiroImplTest() throws KapuaAuthenticationException {
        JwtCredentials first = new JwtCredentials("aJwt", "anIdToken");

        JwtCredentialsImpl second = (JwtCredentialsImpl) instance.convertToShiro(first);

        Assert.assertNotNull(second);
        Assert.assertNotEquals(first, second);
        Assert.assertEquals(first.getAccessToken(), second.getAccessToken());
        Assert.assertEquals(first.getIdToken(), second.getIdToken());
    }

    @Test(expected = NullPointerException.class)
    public void jwtCredentialsImplMapToShiroNullTest() throws KapuaAuthenticationException {
        instance.convertToShiro(null);
    }

    @Test(expected = KapuaAuthenticationException.class)
    public void jwtCredentialsImplMapToShiroEmptyTest() throws KapuaAuthenticationException {
        JwtCredentials first = new JwtCredentials(null, null);

        Assert.assertNotNull(first);

        instance.convertToShiro(first);
    }
}