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
import org.eclipse.kapua.service.authentication.RefreshTokenCredentials;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class RefreshTokenCredentialsTest {

    String[] idsToken, refreshTokens, newIdsToken, newRefreshTokens;
    RefreshTokenCredentials refreshTokenCredentials;

    @Before
    public void initialize() {
        idsToken = new String[] { null, "", "   ID tokenID 747.,.,,,82(*&%<> ", "   token((11@-", "id)__.,TOKen65", "TOKENid543$#%&t   oken", "to-ken_id++=,", "id,,,,id3$^&" };
        refreshTokens = new String[] { null, "", "   refresh tokenREFRESH 747.,.,,,82(*&%<> ", "   token((11@-", "REFresh)__.,TOKen65", "TOKENrefresh543$#%&t   oken", "to-ken_++rE=fresh,",
                "refresh,,,,id3$^&" };
        newIdsToken = new String[] { null, "", "NEW tokenID0000@!!,,,#", "!@#00tokenID new.", " new id TOK --EN-44<>", "pA_ss0###woE**9()", "    tokenID new tokenID  12344*&^%" };
        newRefreshTokens = new String[] { null, "", "new_refresh1122TOKEN#$%", "   JWT)(..,,new", "NEW_token .refresh/??)_)*", "<> 1111      ", "jwttt&^$$##Z||'", "%%%KEY NEW-TOKEN1r-e5f&resh" };
    }

    @Test
    public void refreshTokenCredentialsTest() {
        for (String idToken : idsToken) {
            for (String refreshToken : refreshTokens) {
                refreshTokenCredentials = new RefreshTokenCredentials(idToken, refreshToken);
                Assert.assertEquals("Expected and actual values should be the same.", idToken, refreshTokenCredentials.getTokenId());
                Assert.assertEquals("Expected and actual values should be the same.", refreshToken, refreshTokenCredentials.getRefreshToken());
            }
        }
    }

    @Test
    public void setAndGetTokenIdTest() {
        refreshTokenCredentials = new RefreshTokenCredentials("token id", "refresh token");
        for (String newIdToken : newIdsToken) {
            refreshTokenCredentials.setTokenId(newIdToken);
            Assert.assertEquals("Expected and actual values should be the same.", newIdToken, refreshTokenCredentials.getTokenId());
        }
    }

    @Test
    public void setAndGetRefreshTokenTest() {
        refreshTokenCredentials = new RefreshTokenCredentials("token id", "refresh token");
        for (String newRefreshToken : newRefreshTokens) {
            refreshTokenCredentials.setRefreshToken(newRefreshToken);
            Assert.assertEquals("Expected and actual values should be the same.", newRefreshToken, refreshTokenCredentials.getRefreshToken());
        }
    }
}