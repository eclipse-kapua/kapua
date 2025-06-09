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
package org.eclipse.kapua.service.authentication.credential.mfa.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class MfaOptionCreatorTest {

    KapuaId[] scopeIds;
    KapuaId[] userIds;
    KapuaId[] newUserIds;
    MfaOptionCreator mfaOptionCreatorImpl1;
    MfaOptionCreator mfaOptionCreatorImpl2;

    @Before
    public void initialize() {
        scopeIds = new KapuaId[] { null, KapuaId.ONE };
        userIds = new KapuaId[] { null, KapuaId.ONE };
        newUserIds = new KapuaId[] { null, KapuaId.ANY };
        mfaOptionCreatorImpl1 = new MfaOptionCreator(KapuaId.ONE, KapuaId.ONE);
        mfaOptionCreatorImpl2 = new MfaOptionCreator(KapuaId.ONE);
    }

    @Test
    public void mfaOptionCreatorImplScopeIdUserIdSecretKeyParametersTest() {
        for (KapuaId scopeId : scopeIds) {
            for (KapuaId userId : userIds) {
                MfaOptionCreator mfaOptionCreatorImpl = new MfaOptionCreator(scopeId, userId);
                Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOptionCreatorImpl.getScopeId());
                Assert.assertEquals("Expected and actual values should be the same.", userId, mfaOptionCreatorImpl.getUserId());
            }
        }
    }

    @Test
    public void mfaOptionCreatorImplScopeIdParameterTest() {
        for (KapuaId scopeId : scopeIds) {
            MfaOptionCreator mfaOptionCreatorImpl = new MfaOptionCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOptionCreatorImpl.getScopeId());
            Assert.assertNull("Null expected.", mfaOptionCreatorImpl.getUserId());
        }
    }

    @Test
    public void setAndGetUserIdTest() {
        for (KapuaId newUserId : newUserIds) {
            mfaOptionCreatorImpl1.setUserId(newUserId);
            Assert.assertEquals("Expected and actual values should be the same.", newUserId, mfaOptionCreatorImpl1.getUserId());

            mfaOptionCreatorImpl2.setUserId(newUserId);
            Assert.assertEquals("Expected and actual values should be the same.", newUserId, mfaOptionCreatorImpl2.getUserId());
        }
    }
}