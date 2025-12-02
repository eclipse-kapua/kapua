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

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.exception.DatastoreDisabledException;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageInfo;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;

public interface MessageStoreFacade {
    StorableId store(KapuaMessage<?, ?> message, String messageId)
            throws KapuaIllegalArgumentException,
            DatastoreDisabledException,
            ConfigurationException,
            ClientException, MappingException;

    void onAfterMessageStore(MessageInfo messageInfo, DatastoreMessage message)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            MappingException,
            ClientException;

    DatastoreMessage convertTo(KapuaMessage<?, ?> message, String messageId);

    void delete(KapuaId scopeId, StorableId id)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException;

    DatastoreMessage find(KapuaId scopeId, StorableId id)
            throws KapuaIllegalArgumentException, ClientException;

    MessageListResult query(MessageQuery query)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException;

    long count(MessageQuery query)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException;

    void delete(MessageQuery query)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException;

    void refreshAllIndexes() throws ClientException;

    void deleteAllIndexes() throws ClientException;

    void deleteIndexes(String indexExp) throws ClientException;
}
