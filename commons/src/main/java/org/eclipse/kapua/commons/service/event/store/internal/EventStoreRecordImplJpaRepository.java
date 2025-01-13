/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecord;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordListResult;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordRepository;

public class EventStoreRecordImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<EventStoreRecord, EventStoreRecordImpl, EventStoreRecordListResult>
        implements EventStoreRecordRepository {

    public EventStoreRecordImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(EventStoreRecordImpl.class, EventStoreRecord.TYPE, () -> new EventStoreRecordListResult(), jpaRepoConfig);
    }

}
