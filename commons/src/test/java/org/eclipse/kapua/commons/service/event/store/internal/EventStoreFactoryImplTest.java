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
package org.eclipse.kapua.commons.service.event.store.internal;

import java.math.BigInteger;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecord;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class EventStoreFactoryImplTest {

    @Test
    public void newEntityTest() {
        EventStoreFactoryImpl eventStoreFactoryImpl = new EventStoreFactoryImpl();
        KapuaId[] scopeIdList = { null, new KapuaEid(BigInteger.ONE) };

        for (KapuaId scopeId : scopeIdList) {
            Assert.assertNotNull("Null not expected.", eventStoreFactoryImpl.newEntity(scopeId));
            Assert.assertThat("EventStoreRecordImpl object expected.", eventStoreFactoryImpl.newEntity(scopeId), IsInstanceOf.instanceOf(EventStoreRecordImpl.class));
        }
    }

    @Test
    public void cloneTest() {
        EventStoreFactoryImpl eventStoreFactoryImpl = new EventStoreFactoryImpl();
        EventStoreRecord eventStoreRecord = Mockito.mock(EventStoreRecord.class);
        EventStoreRecord nullEventStoreRecord = null;
        KapuaEntityCloneException kapuaEntityCloneException = new KapuaEntityCloneException(new Exception(), EventStoreRecord.TYPE, nullEventStoreRecord);

        Assert.assertNotNull("Null not expected.", eventStoreFactoryImpl.clone(eventStoreRecord));
        Assert.assertThat("EventStoreRecordImpl object expected.", eventStoreFactoryImpl.clone(eventStoreRecord), IsInstanceOf.instanceOf(EventStoreRecordImpl.class));

        try {
            eventStoreFactoryImpl.clone(nullEventStoreRecord);
        } catch (Exception e) {
            Assert.assertEquals("KapuaEntityCloneException expected.", kapuaEntityCloneException.toString(), e.toString());
        }
    }
} 
