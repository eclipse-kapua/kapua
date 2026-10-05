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
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.storeengine.client.exception.ClientException;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;

public interface ClientInfoRegistryFacade {
    StorableId upstore(ClientInfo clientInfo) throws KapuaIllegalArgumentException, ConfigurationException, ClientException, MappingException;

    void delete(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ConfigurationException, ClientException;

    ClientInfo find(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ConfigurationException, ClientException;

    ClientInfoListResult query(ClientInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException;

    long count(ClientInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException;

    void delete(ClientInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException;
}
