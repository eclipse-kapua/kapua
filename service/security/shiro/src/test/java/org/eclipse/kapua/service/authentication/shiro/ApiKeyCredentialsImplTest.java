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
package org.eclipse.kapua.service.authentication.shiro;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.ApiKeyCredentials;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class ApiKeyCredentialsImplTest {

    @Test(expected = NullPointerException.class)
    public void apiKeyCredentialsImplCloneConstructorNullTest() {
        new ApiKeyCredentialsImpl((ApiKeyCredentials) null);
    }

    @Test
    public void apiKeyCredentialsImplCloneConstructorImplTest() {
        ApiKeyCredentials first = new ApiKeyCredentials("anApiKey");

        ApiKeyCredentialsImpl second = new ApiKeyCredentialsImpl(first);

        Assert.assertNotEquals("ApiKeyCredentialImpl", first, second);
        Assert.assertEquals("ApiKeyCredential.apiKey", first.getApiKey(), second.getApiKey());
    }

    @Test
    public void apiKeyCredentialsImplTest() {
        String[] apiKeys = { null, "", "!!api key-1", "#1(API KEY.,/api key)9--99", "!$$ 1-2 KEY//", "APIkey(....)<00>" };

        for (String apiKey : apiKeys) {
            ApiKeyCredentialsImpl apiKeyCredentialsImpl = new ApiKeyCredentialsImpl(apiKey);
            Assert.assertEquals("Expected and actual values should be the same.", apiKey, apiKeyCredentialsImpl.getApiKey());
            Assert.assertEquals("Expected and actual values should be the same.", apiKey, apiKeyCredentialsImpl.getPrincipal());
            Assert.assertEquals("Expected and actual values should be the same.", apiKey, apiKeyCredentialsImpl.getCredentials());
        }
    }

    @Test
    public void setAndGetApiKeyPrincipalAndCredentialsTest() {
        String[] newApiKeys = { null, "", "!!api key-1NEW", "#1(new API KEY.,/api key)9--99", "!$$ 1-2 newKEY//", "NEwAPIkey(....)<00>" };
        ApiKeyCredentials apiKeyCredentials = new ApiKeyCredentials("apiKey");

        for (String newApiKey : newApiKeys) {
            apiKeyCredentials.setApiKey(newApiKey);
            ApiKeyCredentialsImpl apiKeyCredentialsImpl = new ApiKeyCredentialsImpl(apiKeyCredentials);
            Assert.assertEquals("Expected and actual values should be the same.", newApiKey, apiKeyCredentialsImpl.getApiKey());
            Assert.assertEquals("Expected and actual values should be the same.", newApiKey, apiKeyCredentialsImpl.getPrincipal());
            Assert.assertEquals("Expected and actual values should be the same.", newApiKey, apiKeyCredentialsImpl.getCredentials());
        }
    }
}