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
package org.eclipse.kapua.service.datastore;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoQuery;
import org.eclipse.kapua.service.storable.StorableService;
import org.eclipse.kapua.service.storable.model.id.StorableId;

/**
 * {@link ChannelInfoRegistryService} definition.
 * <p>
 * {@link StorableService} for {@link ChannelInfo}
 *
 * @since 1.0.0
 */
public interface ChannelInfoRegistryService extends KapuaService, StorableService<ChannelInfo, ChannelInfoListResult, ChannelInfoQuery> {
    void delete(KapuaId scopeId, StorableId id)
            throws KapuaException;

    void delete(ChannelInfoQuery query)
            throws KapuaException;

}
