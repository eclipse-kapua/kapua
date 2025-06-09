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

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.token.AccessTokenCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class AccessTokenCreatorImplTest {

    AccessTokenCreator accessTokenCreator;

    @Before
    public void initialize() {
        accessTokenCreator = new AccessTokenCreator(KapuaId.ONE);
    }

    @Test
    public void accessTokenCreatorImplTest() {
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, accessTokenCreator.getScopeId());
    }

    @Test
    public void setAndGetTokenIdTest() {
        String[] tokenIds = { null, "", "!!tokenID-1", "#1(TOKEN.,/token id)9--99", "!$$ 1-2 ID//", "id_tokeN(....)<00>" };

        Assert.assertNull("Null expected.", accessTokenCreator.getTokenId());
        for (String tokenId : tokenIds) {
            accessTokenCreator.setTokenId(tokenId);
            Assert.assertEquals("Expected and actual values should be the same.", tokenId, accessTokenCreator.getTokenId());
        }
    }

    @Test
    public void setAndGetUserIdTest() {
        KapuaId[] userIds = { null, KapuaId.ONE };

        Assert.assertNull("Null expected.", accessTokenCreator.getUserId());
        for (KapuaId userId : userIds) {
            accessTokenCreator.setUserId(userId);
            Assert.assertEquals("Expected and actual values should be the same.", userId, accessTokenCreator.getUserId());
        }
    }

    @Test
    public void setAndGetExpiresOnTest() {
        Date[] expiresOnDates = { null, new Date(), new Date(1L), new Date(9999999999999L) };
        Assert.assertNull("Null expected.", accessTokenCreator.getExpiresOn());
        for (Date expiresOnDate : expiresOnDates) {
            accessTokenCreator.setExpiresOn(expiresOnDate);
            Assert.assertEquals("Expected and actual values should be the same.", expiresOnDate, accessTokenCreator.getExpiresOn());
        }
    }

    @Test
    public void setAndGetRefreshTokenTest() {
        String[] refreshTokens = { null, "", "!!refreshToken-1", "#1(TOKEN.,/refresh token id)9--99", "!$$ 1-2 REFREsh//", "refresh_tokeN(....)<00>" };

        Assert.assertNull("Null expected.", accessTokenCreator.getRefreshToken());
        for (String refreshToken : refreshTokens) {
            accessTokenCreator.setRefreshToken(refreshToken);
            Assert.assertEquals("Expected and actual values should be the same.", refreshToken, accessTokenCreator.getRefreshToken());
        }
    }

    @Test
    public void setAndGetRefreshExpiresOnTest() {
        Date[] refreshExpiresOnDates = { null, new Date(), new Date(1L), new Date(9999999999999L) };
        Assert.assertNull("Null expected.", accessTokenCreator.getRefreshExpiresOn());
        for (Date refreshExpiresOnDate : refreshExpiresOnDates) {
            accessTokenCreator.setRefreshExpiresOn(refreshExpiresOnDate);
            Assert.assertEquals("Expected and actual values should be the same.", refreshExpiresOnDate, accessTokenCreator.getRefreshExpiresOn());
        }
    }
}