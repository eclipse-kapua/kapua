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
import org.eclipse.kapua.service.authentication.JwtCredentials;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class JwtCredentialsTest {

    JwtCredentials jwtCredentials;
    String[] accessTokens, idsToken, newAccessTokens, newIdsToken;

    @Before
    public void initialize() {
        jwtCredentials = new JwtCredentials("accessToken", "idToken");
        idsToken = new String[] { null, "", "   ID tokenID 747.,.,,,82(*&%<> ", "   token((11@-", "id)__.,TOKen65", "TOKENid543$#%&t   oken", "to-ken_id++=,", "id,,,,id3$^&" };
        accessTokens = new String[] { null, "", "  j_w=t110.,<> jwt", "(!!)432j&^w$#3t", "##<>/.JWT    ", "__J!#W(-8T    ", "jw&* 990t  ", "jwt987)_=;'''     .", "jwt JWT-123" };
        newAccessTokens = new String[] { null, "", "new_Jwt1122#$%", "   JWT)(..,,new", "NEW_jwt ./??)_)*", "<> 1111      ", "jwttt&^$$##Z||'", "%%%KEY NEW-JWT11" };
        newIdsToken = new String[] { null, "", "NEW tokenID0000@!!,,,#", "!@#00tokenID new.", " new id TOK --EN-44<>", "pA_ss0###woE**9()", "    tokenID new tokenID  12344*&^%" };
    }

    @Test(expected = NullPointerException.class)
    public void jwtCredentialsImplCloneConstructorNullTest() {
        new JwtCredentialsImpl(null);
    }

    @Test
    public void jwtCredentialsImplCloneConstructorImplTest() {
        JwtCredentials first = new JwtCredentials("aJwt", "anIdToken");

        JwtCredentialsImpl second = new JwtCredentialsImpl(first);

        Assert.assertNotEquals("JwtCredentialImpl", first, second);
        Assert.assertEquals("JwtCredential.accessToken", first.getAccessToken(), second.getAccessToken());
        Assert.assertEquals("JwtCredential.idToken", first.getIdToken(), second.getIdToken());
    }

    @Test
    public void jwtCredentialsImplCloneConstructorAnotherTest() {
        JwtCredentials first = new JwtCredentials("aJwt", "anIdToken");

        JwtCredentialsImpl second = new JwtCredentialsImpl(first);

        Assert.assertNotEquals("JwtCredentialImpl", first, second);
        Assert.assertEquals("JwtCredential.accessToken", first.getAccessToken(), second.getAccessToken());
        Assert.assertEquals("JwtCredential.idToken", first.getIdToken(), second.getIdToken());
    }

    @Test
    public void jwtCredentialsImplTest() {
        for (String accessToken : accessTokens) {
            for (String idToken : idsToken) {
                JwtCredentialsImpl jwtCredentialsImpl = new JwtCredentialsImpl(new JwtCredentials(accessToken, idToken));
                Assert.assertEquals("Expected and actual values should be the same.", accessToken, jwtCredentialsImpl.getAccessToken());
                Assert.assertEquals("Expected and actual values should be the same.", idToken, jwtCredentialsImpl.getIdToken());
                Assert.assertEquals("Expected and actual values should be the same.", accessToken, jwtCredentialsImpl.getPrincipal());
                Assert.assertEquals("Expected and actual values should be the same.", accessToken, jwtCredentialsImpl.getCredentials());
            }
        }
    }

    @Test
    public void setAndGetJwtPrincipalAndCredentialTest() {
        for (String newAccessToken : newAccessTokens) {
            jwtCredentials.setAccessToken(newAccessToken);
            final JwtCredentialsImpl credentials = new JwtCredentialsImpl(jwtCredentials);
            Assert.assertEquals("Expected and actual values should be the same.", newAccessToken, credentials.getAccessToken());
            Assert.assertEquals("Expected and actual values should be the same.", newAccessToken, credentials.getPrincipal());
            Assert.assertEquals("Expected and actual values should be the same.", newAccessToken, credentials.getCredentials());
        }
    }

    @Test
    public void setAndGetIdTokenTest() {
        for (String newIdToken : newIdsToken) {
            jwtCredentials.setIdToken(newIdToken);
            Assert.assertEquals("Expected and actual values should be the same.", newIdToken, jwtCredentials.getIdToken());
        }
    }
}
