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

import java.util.Date;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOption;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class MfaOptionFactoryImplTest {

    MfaOptionFactoryImpl mfaOptionFactoryImpl;
    KapuaId[] scopeIds;
    KapuaEid[] userIds;
    MfaOption mfaOption;
    Date trustExpirationDate;
    Date modifiedOn;

    @Before
    public void initialize() {
        mfaOptionFactoryImpl = new MfaOptionFactoryImpl();
        scopeIds = new KapuaId[] { null, KapuaId.ONE };
        userIds = new KapuaEid[] { null, new KapuaEid() };
        mfaOption = Mockito.mock(MfaOption.class);
        trustExpirationDate = new Date();
        modifiedOn = new Date();
    }

    @Test
    public void newEntityTest() {
        for (KapuaId scopeId : scopeIds) {
            MfaOption mfaOption = mfaOptionFactoryImpl.newEntity(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOption.getScopeId());
        }
    }

    @Test
    public void newCreatorScopeIdParameterTest() {
        for (KapuaId scopeId : scopeIds) {
            MfaOptionCreator mfaOptionCreator = new MfaOptionCreator(scopeId);
            Assert.assertEquals("Expected and actual values should be the same.", scopeId, mfaOptionCreator.getScopeId());
        }
    }

    @Test
    public void cloneTest() {
        Mockito.when(mfaOption.getScopeId()).thenReturn(KapuaId.ONE);
        Mockito.when(mfaOption.getUserId()).thenReturn(KapuaId.ONE);
        Mockito.when(mfaOption.getMfaSecretKey()).thenReturn("mfa secret key");
        Mockito.when(mfaOption.getTrustKey()).thenReturn("thrust key");
        Mockito.when(mfaOption.getTrustExpirationDate()).thenReturn(trustExpirationDate);
        Mockito.when(mfaOption.getModifiedOn()).thenReturn(modifiedOn);
        Mockito.when(mfaOption.getModifiedBy()).thenReturn(KapuaId.ONE);
        Mockito.when(mfaOption.getOptlock()).thenReturn(10);

        MfaOption mfaOptionResult = mfaOptionFactoryImpl.clone(mfaOption);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, mfaOptionResult.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, mfaOptionResult.getUserId());
        Assert.assertEquals("Expected and actual values should be the same.", "mfa secret key", mfaOptionResult.getMfaSecretKey());
        Assert.assertEquals("Expected and actual values should be the same.", "thrust key", mfaOptionResult.getTrustKey());
        Assert.assertEquals("Expected and actual values should be the same.", trustExpirationDate, mfaOptionResult.getTrustExpirationDate());
        Assert.assertEquals("Expected and actual values should be the same.", modifiedOn, mfaOptionResult.getModifiedOn());
        Assert.assertEquals("Expected and actual values should be the same.", KapuaId.ONE, mfaOptionResult.getModifiedBy());
        Assert.assertEquals("Expected and actual values should be the same.", 10, mfaOptionResult.getOptlock());
    }

    @Test(expected = KapuaEntityCloneException.class)
    public void cloneNullTest() {
        mfaOptionFactoryImpl.clone(null);
    }
}