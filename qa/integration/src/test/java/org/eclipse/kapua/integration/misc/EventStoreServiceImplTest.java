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
package org.eclipse.kapua.integration.misc;

import java.math.BigInteger;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordCreator;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordRepository;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreServiceImpl;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.storage.TxManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
//TODO: rewrite this
public class EventStoreServiceImplTest {

    @Test
    public void createTest() {
        EventStoreServiceImpl eventStoreServiceImpl = new EventStoreServiceImpl(
                Mockito.mock(AuthorizationService.class),
                Mockito.mock(TxManager.class),
                Mockito.mock(EventStoreRecordRepository.class));
        EventStoreRecordCreator[] creator = { null, new EventStoreRecordCreator(new KapuaEid(BigInteger.ONE)) };
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException();

        for (EventStoreRecordCreator eventStoreRecordCreator : creator) {
            try {
                eventStoreServiceImpl.create(eventStoreRecordCreator);
            } catch (Exception e) {
                Assert.assertEquals("Expected and actual values should be the same.", unsupportedOperationException.toString(), e.toString());
            }
        }
    }
} 
