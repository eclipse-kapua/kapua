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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class AccessTokenCredentialsImplTest {

    @Test(expected = NullPointerException.class)
    public void accessTokenCredentialsImplCloneConstructorNullTest() {
        new AccessTokenCredentialsImpl((AccessTokenCredentialsImpl) null);
    }

    @Test
    public void accessTokenCredentialsImplCloneConstructorImplTest() {
        AccessTokenCredentialsImpl first = new AccessTokenCredentialsImpl("anAccessToken");

        AccessTokenCredentialsImpl second = new AccessTokenCredentialsImpl(first);

        Assert.assertNotEquals("AccessTokenCredentialImpl", first, second);
        Assert.assertEquals("AccessTokenCredential.tokenId", first.getTokenId(), second.getTokenId());
    }

    @Test
    public void accessTokenCredentialsImplCloneConstructorAnotherTest() {
        AccessTokenCredentialsImpl first = new AccessTokenCredentialsImpl("anAccessToken");

        AccessTokenCredentialsImpl second = new AccessTokenCredentialsImpl(first);

        Assert.assertNotEquals("AccessTokenCredentialImpl", first, second);
        Assert.assertEquals("AccessTokenCredential.tokenId", first.getTokenId(), second.getTokenId());
    }

    @Test
    public void accessTokenCredentialsImplTokenIdParameterTest() {
        String[] tokenIds = { null, "", "!!tokenID-1", "#1(TOKEN.,/token id)9--99", "!$$ 1-2 ID//", "id_tokeN(....)<00>" };

        for (String tokenId : tokenIds) {
            AccessTokenCredentialsImpl accessTokenCredentialsImpl = new AccessTokenCredentialsImpl(tokenId);
            Assert.assertEquals("Expected and actual values should be the same.", tokenId, accessTokenCredentialsImpl.getTokenId());
            Assert.assertEquals("Expected and actual values should be the same.", tokenId, accessTokenCredentialsImpl.getPrincipal());
            Assert.assertEquals("Expected and actual values should be the same.", tokenId, accessTokenCredentialsImpl.getCredentials());
        }
    }

    @Test
    public void setAndGetTokenIdPrincipalAndCredentialsTest() {
        String[] newTokenIds = { null, "", "!!NEWtokenID-1", "#1(newTOKEN.,/token id)9--99", "!$$ 1-2new ID//", "id_tokeN NEW ID(....)<00>" };
        AccessTokenCredentialsImpl accessTokenCredentials = new AccessTokenCredentialsImpl("token id");

        for (String newTokenId : newTokenIds) {
            accessTokenCredentials.setTokenId(newTokenId);
            Assert.assertEquals("Expected and actual values should be the same.", newTokenId, accessTokenCredentials.getTokenId());
            AccessTokenCredentialsImpl accessTokenCredentialsImpl = new AccessTokenCredentialsImpl(accessTokenCredentials);
            accessTokenCredentials.setTokenId(newTokenId);
            Assert.assertEquals("Expected and actual values should be the same.", newTokenId, accessTokenCredentialsImpl.getPrincipal());
            Assert.assertEquals("Expected and actual values should be the same.", newTokenId, accessTokenCredentialsImpl.getCredentials());
        }
    }
}

