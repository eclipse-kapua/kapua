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
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class AbstractKapuaUpdatableEntityTest {

    private final static Random RANDOM = RandomUtils.getInstance();

    private class ActualKapuaUpdatableEntity extends AbstractKapuaUpdatableEntity {

        @Override
        public String getType() {
            return null;
        }

        public ActualKapuaUpdatableEntity() {
            super();
        }

        public ActualKapuaUpdatableEntity(KapuaId scopeId) {
            super(scopeId);
        }

        public ActualKapuaUpdatableEntity(KapuaUpdatableEntity entity) {
            super(entity);
        }
    }

    @Test
    public void abstractKapuaUpdatableEntityScopeIdTest() {
        KapuaId scopeId = new KapuaEid();
        AbstractKapuaUpdatableEntity updatableEntity = new ActualKapuaUpdatableEntity(scopeId);
        Assert.assertEquals("Actual and expected values are not the same!", scopeId, updatableEntity.getScopeId());
    }

    @Test
    public void abstractKapuaUpdatableEntityEntityIdTest() {
        KapuaUpdatableEntity entity = new ActualKapuaUpdatableEntity();
        //        entity.setEntityAttributes(new Properties());
        //        entity.setEntityProperties(new Properties());
        entity.setOptlock(10);
        AbstractKapuaUpdatableEntity updatableEntity = new ActualKapuaUpdatableEntity(entity);
        updatableEntity.setModifiedOn(new Date());
        updatableEntity.setModifiedBy(new KapuaEid());
        Assert.assertNotNull("Expected true", updatableEntity.getModifiedOn());
        Assert.assertNotNull("Expected true", updatableEntity.getModifiedBy());
        Assert.assertNotNull("Expected true", updatableEntity.getOptlock());
        Assert.assertNotNull("Expected true", updatableEntity.getEntityAttributes());
        Assert.assertNotNull("Expected true", updatableEntity.getEntityProperties());
    }

    @Test
    public void getModifiedOnTest() {
        KapuaId scopeId = new KapuaEid();
        AbstractKapuaUpdatableEntity updatableEntity = new ActualKapuaUpdatableEntity(scopeId);
        Date modifiedOn = new Date();
        updatableEntity.setModifiedOn(modifiedOn);
        Assert.assertEquals("Actual and expected values are not the same!", modifiedOn, updatableEntity.getModifiedOn());
    }

    @Test
    public void getModifiedByTest() {
        KapuaId scopeId = new KapuaEid();
        AbstractKapuaUpdatableEntity updatableEntity = new ActualKapuaUpdatableEntity(scopeId);
        KapuaId modifiedBy = new KapuaEid();
        updatableEntity.setModifiedBy(modifiedBy);
        Assert.assertEquals("Actual and expected values are not the same!", modifiedBy, updatableEntity.getModifiedBy());
    }

    @Test
    public void getOptlockTest() {
        KapuaId scopeId = new KapuaEid();
        AbstractKapuaUpdatableEntity updatableEntity = new ActualKapuaUpdatableEntity(scopeId);
        int optlock = 25;
        updatableEntity.setOptlock(optlock);
        Assert.assertEquals("Actual and expected values are not the same!", optlock, updatableEntity.getOptlock());
    }

    @Test
    public void prePersistsActionTest() {
        KapuaId scopeId = new KapuaEid();
        BigInteger eid = new BigInteger(64, RANDOM);
        AbstractKapuaUpdatableEntity updatableEntity = new ActualKapuaUpdatableEntity(scopeId);
        KapuaSession mockedSession = Mockito.mock(KapuaSession.class);
        Mockito.when(mockedSession.getUserId()).thenReturn(new KapuaEid(eid));
        KapuaSecurityUtils.setSession(mockedSession);
        updatableEntity.prePersistsAction();
        Assert.assertNotNull("Expected true", updatableEntity.getModifiedBy());
        Assert.assertNotNull("Expected true", updatableEntity.getModifiedOn());
    }

    @Test
    public void preUpdateActionTest() {
        BigInteger eid = new BigInteger(64, RANDOM);
        AbstractKapuaUpdatableEntity updatableEntity = new ActualKapuaUpdatableEntity();
        KapuaSession mockedSession = Mockito.mock(KapuaSession.class);
        Mockito.when(mockedSession.getUserId()).thenReturn(new KapuaEid(eid));
        KapuaSecurityUtils.setSession(mockedSession);
        updatableEntity.preUpdateAction();
        Assert.assertNotNull("Expected true", updatableEntity.getModifiedBy());
        Assert.assertNotNull("Expected true", updatableEntity.getModifiedOn());
    }
}
