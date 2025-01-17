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
package org.eclipse.kapua.service.authentication.credential.shiro;

import java.util.Date;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class CredentialCreatorTest {

    CredentialCreator credentialCreatorImpl1, credentialCreatorImpl2;

    @Before
    public void initialize() {
        credentialCreatorImpl1 = new CredentialCreator(KapuaId.ONE, KapuaId.ONE, "API_KEY", "credential key", CredentialStatus.ENABLED, new Date());
        credentialCreatorImpl2 = new CredentialCreator(KapuaId.ONE);
    }

    @Test
    public void credentialCreatorImplMultipleParametersTest() {
        KapuaId[] scopeIds = {null, KapuaId.ONE};
        KapuaId[] userIds = {null, KapuaId.ONE};
        String[] credentialKeys = {null, "", "!!credentialKey-1", "#1(credentialKey.,/Key)9--99", "!$$ 1-2 key//", "credential_K_ey(....)<00>"};
        String[] credentialTypes = {null, "PASSWORD", "API_KEY", "JWT"};
        CredentialStatus[] credentialStatuses = {null, CredentialStatus.ENABLED, CredentialStatus.DISABLED};
        Date[] dates = {null, new Date()};

        for (KapuaId scopeId : scopeIds) {
            for (KapuaId userId : userIds) {
                for (String credentialType : credentialTypes) {
                    for (String credentialKey : credentialKeys) {
                        for (CredentialStatus credentialStatus : credentialStatuses) {
                            for (Date date : dates) {
                                CredentialCreator credentialCreatorImpl = new CredentialCreator(scopeId, userId, credentialType, credentialKey, credentialStatus, date);
                                Assert.assertEquals("Expected and actual values should be the same.", scopeId, credentialCreatorImpl.getScopeId());
                                Assert.assertEquals("Expected and actual values should be the same.", userId, credentialCreatorImpl.getUserId());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialType, credentialCreatorImpl.getCredentialType());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialKey, credentialCreatorImpl.getCredentialPlainKey());
                                Assert.assertEquals("Expected and actual values should be the same.", credentialStatus, credentialCreatorImpl.getCredentialStatus());
                                Assert.assertEquals("Expected and actual values should be the same.", date, credentialCreatorImpl.getExpirationDate());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void credentialCreatorImplScopeIdParameterTest() {
        KapuaId[] scopeIds = {null, KapuaId.ONE};
        for (KapuaId scopeId : scopeIds) {
            CredentialCreator credentialCreatorImpl = new CredentialCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, credentialCreatorImpl.getScopeId());
            Assert.assertNull("Null expected.", credentialCreatorImpl.getUserId());
            Assert.assertNull("Null expected.", credentialCreatorImpl.getCredentialType());
            Assert.assertNull("Null expected.", credentialCreatorImpl.getCredentialPlainKey());
            Assert.assertNull("Null expected.", credentialCreatorImpl.getCredentialStatus());
            Assert.assertNull("Null expected.", credentialCreatorImpl.getExpirationDate());
        }
    }

    @Test
    public void setAndGetUserIdTest() {
        KapuaId[] newUserIds = {null, KapuaId.ONE};
        for (KapuaId newUserId : newUserIds) {
            credentialCreatorImpl1.setUserId(newUserId);
            credentialCreatorImpl2.setUserId(newUserId);
            Assert.assertEquals("Expected and actual values should be the same.", newUserId, credentialCreatorImpl1.getUserId());
            Assert.assertEquals("Expected and actual values should be the same.", newUserId, credentialCreatorImpl2.getUserId());
        }
    }

    @Test
    public void setAndGetCredentialTypeTest() {
        String[] newCredentialTypes = {null, "PASSWORD", "API_KEY", "JWT"};
        for (String newCredentialType : newCredentialTypes) {
            credentialCreatorImpl1.setCredentialType(newCredentialType);
            credentialCreatorImpl2.setCredentialType(newCredentialType);
            Assert.assertEquals("Expected and actual values should be the same.", newCredentialType, credentialCreatorImpl1.getCredentialType());
            Assert.assertEquals("Expected and actual values should be the same.", newCredentialType, credentialCreatorImpl2.getCredentialType());
        }
    }

    @Test
    public void setAndGetCredentialPlainKeyTest() {
        String[] newCredentialKeys = {null, "", "!!NEWcredentialKey-1", "#1(new credentialKey.,/Key)9--99", "!$$ 1-2 key//", "credential_K_ey NEW KEY(....)<00>"};
        for (String newCredentialKey : newCredentialKeys) {
            credentialCreatorImpl1.setCredentialPlainKey(newCredentialKey);
            credentialCreatorImpl2.setCredentialPlainKey(newCredentialKey);
            Assert.assertEquals("Expected and actual values should be the same.", newCredentialKey, credentialCreatorImpl1.getCredentialPlainKey());
            Assert.assertEquals("Expected and actual values should be the same.", newCredentialKey, credentialCreatorImpl2.getCredentialPlainKey());
        }
    }

    @Test
    public void setAndGetExpirationDateTest() {
        Date[] newDates = {null, new Date()};
        for (Date newDate : newDates) {
            credentialCreatorImpl1.setExpirationDate(newDate);
            credentialCreatorImpl2.setExpirationDate(newDate);
            Assert.assertEquals("Expected and actual values should be the same.", newDate, credentialCreatorImpl1.getExpirationDate());
            Assert.assertEquals("Expected and actual values should be the same.", newDate, credentialCreatorImpl2.getExpirationDate());
        }
    }

    @Test
    public void setAndGetCredentialStatusTest() {
        CredentialStatus[] newCredentialStatuses = {null, CredentialStatus.ENABLED, CredentialStatus.DISABLED};
        for (CredentialStatus newCredentialStatus : newCredentialStatuses) {
            credentialCreatorImpl1.setCredentialStatus(newCredentialStatus);
            credentialCreatorImpl2.setCredentialStatus(newCredentialStatus);
            Assert.assertEquals("Expected and actual values should be the same.", newCredentialStatus, credentialCreatorImpl1.getCredentialStatus());
            Assert.assertEquals("Expected and actual values should be the same.", newCredentialStatus, credentialCreatorImpl2.getCredentialStatus());
        }
    }
}