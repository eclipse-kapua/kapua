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

import java.util.List;

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

    /**
     * Gets all the distinct channel names in the given scope, sorted ascending.
     *
     * @param scopeId The scope id.
     * @return All the distinct channel names.
     * @throws KapuaException
     */
    List<String> fetchDistinctChannelNames(KapuaId scopeId)
            throws KapuaException;

    /**
     * Gets one page of the distinct channel names matching the given {@link ChannelInfoQuery}, sorted ascending.
     * <p>
     * Only the scope id and the predicate of the query are used: offset, limit and sort fields are ignored.
     *
     * @param query The {@link ChannelInfoQuery} used to filter the channels.
     * @param after The channel name after which the page starts (exclusive), {@code null} to start from the first one.
     * @param limit The maximum number of channel names to return.
     * @return At most {@code limit} distinct channel names following {@code after}.
     * @throws KapuaException
     */
    List<String> fetchDistinctChannelNames(ChannelInfoQuery query, String after, int limit)
            throws KapuaException;

    /**
     * Same as {@link #fetchDistinctChannelNames(ChannelInfoQuery, String, int)} but returns {@link ChannelInfo}s, which have only the name,
     * {@link ChannelInfo#getFirstMessageOn()} (the earliest among all the clients) and {@link ChannelInfo#getLastMessageOn()} set.
     * The latter is the timestamp of the last message published on that channel by any client (one query per channel name).
     *
     * @param query The {@link ChannelInfoQuery} used to filter the channels.
     * @param after The channel name after which the page starts (exclusive), {@code null} to start from the first one.
     * @param limit The maximum number of channels to return.
     * @return At most {@code limit} {@link ChannelInfo}s, one for each distinct channel name following {@code after}.
     * @throws KapuaException
     */
    List<ChannelInfo> fetchDistinctChannels(ChannelInfoQuery query, String after, int limit)
            throws KapuaException;

}
