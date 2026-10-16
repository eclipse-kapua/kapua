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
package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.Metric;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.storeengine.client.exception.ClientException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.repository.StorableRepository;

import java.util.Map;

public interface MessageRepository extends StorableRepository<DatastoreMessage, MessageListResult, MessageQuery> {

    String store(DatastoreMessage messageToStore, Map<String, Metric> metrics) throws ClientException;

    void delete(KapuaId scopeId, StorableId id, long time);

    DatastoreMessage find(KapuaId scopeId, StorableId storableId, long time);
}
