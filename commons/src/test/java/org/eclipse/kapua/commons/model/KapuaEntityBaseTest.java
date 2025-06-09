/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.model;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.commons.util.RandomUtils;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class KapuaEntityBaseTest {

    private final static Random RANDOM = RandomUtils.getInstance();

    private class ActualKapuaEntity extends AbstractKapuaEntity {

        public ActualKapuaEntity() {
            super();
        }

        public ActualKapuaEntity(KapuaId scopeId) {
            super(scopeId);
        }

        public ActualKapuaEntity(KapuaEntity entity) {
            super(entity);
        }

        @Override
        public String getType() {
            return null;
        }
    }

    @Test
    public void abstractKapuaEntityScopeIdTest() {
        AbstractKapuaEntity kapuaEntity = new ActualKapuaEntity();
        BigInteger eid = new BigInteger(64, RANDOM);
        KapuaId scopeIdNull = new KapuaEid();
        KapuaId scopeId = new KapuaEid(eid);
        kapuaEntity.setScopeId(scopeIdNull);
        Assert.assertEquals("Expected and actual values should be the same!", scopeIdNull, kapuaEntity.getScopeId());
        kapuaEntity.setScopeId(scopeId);
        Assert.assertEquals("Expected and actual values should be the same!", scopeId, kapuaEntity.getScopeId());

    }

    @Test
    public void abstractKapuaEntityEntityIdTest() {
        KapuaId scopeId = new KapuaEid();
        AbstractKapuaEntity kapuaEntity = new ActualKapuaEntity(scopeId);
        kapuaEntity.setId(new KapuaEid());
        kapuaEntity.setCreatedBy(new KapuaEid());
        kapuaEntity.setCreatedOn(new Date());
        AbstractKapuaEntity kapuaCopyEntity = new ActualKapuaEntity(kapuaEntity);
        Assert.assertEquals("Expected and actual values should be the same!", kapuaEntity.getId(), kapuaCopyEntity.getId());
        Assert.assertEquals("Expected and actual values should be the same!", kapuaEntity.getScopeId(), kapuaCopyEntity.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same!", kapuaEntity.getCreatedBy(), kapuaCopyEntity.getCreatedBy());
        Assert.assertEquals("Expected and actual values should be the same!", kapuaEntity.getCreatedOn(), kapuaCopyEntity.getCreatedOn());
    }

    @Test
    public void prePersistsActionTest() {
        KapuaId scopeId = new KapuaEid();
        BigInteger eid = new BigInteger(64, RANDOM);
        AbstractKapuaEntity kapuaEntity = new ActualKapuaEntity(scopeId);
        KapuaSession kapuaSession = Mockito.mock(KapuaSession.class);
        Mockito.when(kapuaSession.getUserId()).thenReturn(new KapuaEid(eid));
        KapuaSecurityUtils.setSession(kapuaSession);
        kapuaEntity.prePersistsAction();
        Assert.assertNotNull("Not Null expected!", kapuaEntity.getId());
        Assert.assertNotNull("Not Null expected!", kapuaEntity.getCreatedBy());
        Assert.assertNotNull("Not Null expected!", kapuaEntity.getCreatedOn());
    }
}
