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
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.shiro.realm.AccessTokenCredentialsMatcher;
import org.eclipse.kapua.service.authentication.shiro.realm.SessionAuthenticationInfo;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class AccessTokenCredentialsMatcherTest {

    AuthenticationToken authenticationToken;
    SessionAuthenticationInfo authenticationInfo;
    String jwt;
    AccessToken accessToken;
    AccessTokenCredentialsMatcher accessTokenCredentialsMatcher;

    @Before
    public void initialize() {
        authenticationToken = Mockito.mock(AuthenticationToken.class);
        authenticationInfo = Mockito.mock(SessionAuthenticationInfo.class);
        jwt = "jwt";
        accessToken = Mockito.mock(AccessToken.class);
        accessTokenCredentialsMatcher = new AccessTokenCredentialsMatcher();
    }

    @Test
    public void doCredentialsMatchDifferentCredentialsTest() {
        Mockito.when(authenticationToken.getCredentials()).thenReturn(jwt);
        Mockito.when(authenticationInfo.getAccessToken()).thenReturn(accessToken);
        Mockito.when(accessToken.getTokenId()).thenReturn("token id");
        Assert.assertFalse("False expected.", accessTokenCredentialsMatcher.doCredentialsMatch(authenticationToken, authenticationInfo));
    }

    @Test
    public void doCredentialsMatchEqualCredentialsErrorJWTValidationTest() {
        Mockito.when(authenticationToken.getCredentials()).thenReturn(jwt);
        Mockito.when(authenticationInfo.getAccessToken()).thenReturn(accessToken);
        Mockito.when(accessToken.getTokenId()).thenReturn(jwt);
        Assert.assertFalse("False expected.", accessTokenCredentialsMatcher.doCredentialsMatch(authenticationToken, authenticationInfo));
    }
}