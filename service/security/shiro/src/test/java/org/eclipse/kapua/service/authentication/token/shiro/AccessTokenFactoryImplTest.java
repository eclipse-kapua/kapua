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
package org.eclipse.kapua.service.authentication.token.shiro;

import java.util.Date;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenCreator;
import org.eclipse.kapua.service.authentication.token.LoginInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class AccessTokenFactoryImplTest {

    AccessTokenFactoryImpl accessTokenFactoryImpl;
    KapuaId[] scopeIds;
    KapuaEid[] userIds;
    String[] tokenIds, refreshTokens, tokenIdentifiers;
    Date[] expiresOnDates, refreshExpiresOnDates;
    AccessToken accessToken;
    Date modifiedOn, createdOn, invalidatedOn;

    @Before
    public void initialize() {
        accessTokenFactoryImpl = new AccessTokenFactoryImpl();
        scopeIds = new KapuaId[] { null, KapuaId.ONE };
        userIds = new KapuaEid[] { null, new KapuaEid() };
        tokenIds = new String[] { null, "", "!!token id-1", "#1(token.,/tokenID)9--99", "!$$ 1-2 id//", "to_ken_id(....)<00>" };
        refreshTokens = new String[] { null, "", "!!token refresh token-1", "#1(REfreSF.,/token_refresh)9--99", "!$$ 1-2 id//", "to_ken_refRESH token(....)<00>" };
        expiresOnDates = new Date[] { null, new Date() };
        refreshExpiresOnDates = new Date[] { null, new Date() };
        accessToken = Mockito.mock(AccessToken.class);
        modifiedOn = new Date();
        createdOn = new Date();
        invalidatedOn = new Date();
        tokenIdentifiers = new String[] { "a2fe104f-5d03-4a09-a28d-817ebbc85901", "8e075aeb-be2a-49a7-8dec-346760375d19", "e71b2f52-e02e-4e24-9147-96674e3bf599" };
    }

    @Test
    public void newCreatorMultipleParametersTest() {
        for (KapuaId scopeId : scopeIds) {
            for (KapuaEid userId : userIds) {
                for (String tokenId : tokenIds) {
                    for (Date expiresOnDate : expiresOnDates) {
                        for (String refreshToken : refreshTokens) {
                            for (Date refreshExpiresOnDate : refreshExpiresOnDates) {
                                for (String tokenIdenfier : tokenIdentifiers) {
                                    AccessTokenCreatorImpl accessTokenCreatorImpl = accessTokenFactoryImpl.newCreator(scopeId, userId, tokenId, expiresOnDate, refreshToken, refreshExpiresOnDate,
                                            tokenIdenfier);
                                    Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessTokenCreatorImpl.getScopeId());
                                    Assert.assertEquals("Expected and actual values should be the same.", userId, accessTokenCreatorImpl.getUserId());
                                    Assert.assertEquals("Expected and actual values should be the same.", tokenId, accessTokenCreatorImpl.getTokenId());
                                    Assert.assertEquals("Expected and actual values should be the same.", expiresOnDate, accessTokenCreatorImpl.getExpiresOn());
                                    Assert.assertEquals("Expected and actual values should be the same.", refreshToken, accessTokenCreatorImpl.getRefreshToken());
                                    Assert.assertEquals("Expected and actual values should be the same.", refreshExpiresOnDate, accessTokenCreatorImpl.getRefreshExpiresOn());
                                    Assert.assertEquals("Expected and actual values should be the same.", tokenIdenfier, accessTokenCreatorImpl.getTokenIdentifier());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void newEntityTest() {
        for (KapuaId scopeId : scopeIds) {
            AccessToken accessToken = accessTokenFactoryImpl.newEntity(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessToken.getScopeId());
        }
    }

    @Test
    public void newCreatorScopeIdParameterTest() {
        for (KapuaId scopeId : scopeIds) {
            AccessTokenCreator accessTokenCreator = accessTokenFactoryImpl.newCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, accessTokenCreator.getScopeId());
        }
    }

    @Test
    public void cloneTest() {
        Mockito.when(accessToken.getId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessToken.getCreatedOn()).thenReturn(createdOn);
        Mockito.when(accessToken.getCreatedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(accessToken.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(accessToken.getUserId()).thenReturn(KapuaId.ANY);
        Mockito.when(accessToken.getTokenId()).thenReturn("token id");
        Mockito.when(accessToken.getExpiresOn()).thenReturn(expiresOnDates[1]);
        Mockito.when(accessToken.getRefreshToken()).thenReturn("refresh token");
        Mockito.when(accessToken.getRefreshExpiresOn()).thenReturn(refreshExpiresOnDates[1]);
        Mockito.when(accessToken.getInvalidatedOn()).thenReturn(invalidatedOn);
        Mockito.when(accessToken.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(accessToken.getModifiedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(accessToken.getOptlock()).thenReturn(10);

        AccessToken accessTokenResult = accessTokenFactoryImpl.clone(accessToken);
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenResult.getId());
        Assert.assertEquals("Expected and actual values should be the same.", createdOn, accessTokenResult.getCreatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenResult.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenResult.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ANY, accessTokenResult.getUserId());
        Assert.assertEquals("Expected and actual values should be the same.", "token id", accessTokenResult.getTokenId());
        Assert.assertEquals("Expected and actual values should be the same.", expiresOnDates[1], accessTokenResult.getExpiresOn());
        Assert.assertEquals("Expected and actual values should be the same.", "refresh token", accessTokenResult.getRefreshToken());
        Assert.assertEquals("Expected and actual values should be the same.", refreshExpiresOnDates[1], accessTokenResult.getRefreshExpiresOn());
        Assert.assertEquals("Expected and actual values should be the same.", invalidatedOn, accessTokenResult.getInvalidatedOn());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, accessTokenResult.getModifiedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenResult.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", 10, accessTokenResult.getOptlock());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        accessTokenFactoryImpl.clone(null);
    }

    @Test
    public void newLoginInfoTest() {
        Assert.assertTrue("True expected.", accessTokenFactoryImpl.newLoginInfo() instanceof LoginInfo);
    }
}