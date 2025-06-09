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
package org.eclipse.kapua.service.authentication.shiro.realm;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.ApiKeyCredentials;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.shiro.ApiKeyCredentialsImpl;
import org.eclipse.kapua.service.authentication.shiro.realm.model.NotProcessableCredentials;
import org.eclipse.kapua.service.authentication.shiro.realm.model.NotProcessableCredentialsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class ApiKeyCredentialsConverterTest {

    ApiKeyCredentialsConverter instance;

    @Before
    public void setUp() {
        instance = new ApiKeyCredentialsConverter();
    }

    @Test
    public void apoKeyCredentialsImplCanProcessNullTest() {
        Assert.assertFalse(instance.canProcess(null));
    }

    @Test
    public void apoKeyCredentialsImplCanProcessImplTest() throws KapuaAuthenticationException {
        ApiKeyCredentials apoKeyCredentials = new ApiKeyCredentials("anApiKey");
        NotProcessableCredentials notProcessableCredentials = new NotProcessableCredentialsImpl();

        Assert.assertTrue(instance.canProcess(apoKeyCredentials));
        Assert.assertFalse(instance.canProcess(notProcessableCredentials));
    }

    @Test
    public void apiKeyCredentialsImplMapToShiroImplTest() throws KapuaAuthenticationException {
        ApiKeyCredentials first = new ApiKeyCredentials("anApiKey");

        ApiKeyCredentialsImpl second = (ApiKeyCredentialsImpl) instance.convertToShiro(first);

        Assert.assertNotEquals(first, second);
        Assert.assertNotEquals(first, second);
        Assert.assertEquals(first.getApiKey(), second.getApiKey());
    }

    @Test(expected = NullPointerException.class)
    public void apiKeyCredentialsImplMapToShiroNullTest() throws KapuaAuthenticationException {
        instance.convertToShiro(null);
    }

    @Test(expected = KapuaAuthenticationException.class)
    public void apiKeyCredentialsImplMapToShiroEmptyTest() throws KapuaAuthenticationException {
        ApiKeyCredentials first = new ApiKeyCredentials((String) null);

        Assert.assertNotNull(first);

        instance.convertToShiro(first);
    }
}