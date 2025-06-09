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

import javax.inject.Inject;

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.mediator.InfoFieldHelper;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoQuery;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdFactory;
import org.eclipse.kapua.service.storable.model.query.predicate.IdsPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Channel information registry facade
 *
 * @since 1.0.0
 */
public class ChannelInfoRegistryFacadeImpl extends AbstractDatastoreFacade implements ChannelInfoRegistryFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelInfoRegistryFacadeImpl.class);

    private final StorableIdFactory storableIdFactory;
    private final StorablePredicateFactory storablePredicateFactory;
    private final ChannelInfoRepository repository;
    private final DatastoreCacheManager datastoreCacheManager;
    private final Object metadataUpdateSync = new Object();

    private static final String QUERY = "query";
    private static final String QUERY_SCOPE_ID = "query.scopeId";

    /**
     * Constructs the channel info registry facade
     *
     * @param configProvider
     * @since 1.0.0
     */
    @Inject
    public ChannelInfoRegistryFacadeImpl(
            ConfigurationProvider configProvider,
            StorableIdFactory storableIdFactory,
            StorablePredicateFactory storablePredicateFactory,
            ChannelInfoRepository channelInfoRepository,
            DatastoreCacheManager datastoreCacheManager) {
        super(configProvider);
        this.storableIdFactory = storableIdFactory;
        this.storablePredicateFactory = storablePredicateFactory;
        this.repository = channelInfoRepository;
        this.datastoreCacheManager = datastoreCacheManager;
    }

    /**
     * Update the channel information after a message store operation
     *
     * @param channelInfo
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    @Override
    public StorableId upstore(ChannelInfo channelInfo) throws KapuaIllegalArgumentException, ConfigurationException, ClientException, MappingException {
        ArgumentValidator.notNull(channelInfo, "channelInfo");
        ArgumentValidator.notNull(channelInfo.getScopeId(), "channelInfo.scopeId");
        ArgumentValidator.notNull(channelInfo.getName(), "channelInfo.name");
        ArgumentValidator.notNull(channelInfo.getFirstMessageId(), "channelInfo.messageId");
        ArgumentValidator.notNull(channelInfo.getFirstMessageOn(), "channelInfo.messageTimestamp");

        String channelInfoId = InfoFieldHelper.getOrDeriveId(channelInfo.getId(), channelInfo);
        StorableId storableId = storableIdFactory.newStorableId(channelInfoId);

        // Store channel. Look up channel in the cache, and cache it if it doesn't exist
        if (!datastoreCacheManager.getChannelsCache().get(channelInfoId)) {
            // The code is safe even without the synchronized block
            // Synchronize in order to let the first thread complete its
            // update then the others of the same type will find the cache
            // updated and skip the update.
            synchronized (metadataUpdateSync) {
                if (!datastoreCacheManager.getChannelsCache().get(channelInfoId)) {
                    ChannelInfo storedField = doFind(channelInfo.getScopeId(), storableId);
                    if (storedField == null) {
                        repository.upsert(channelInfoId, channelInfo);
                    }
                    // Update cache if channel update is completed successfully
                    datastoreCacheManager.getChannelsCache().put(channelInfoId, true);
                }
            }
        }
        return storableId;
    }

    /**
     * Delete channel information by identifier.
     *
     * <b>Be careful using this function since it doesn't guarantee the datastore consistency.<br>
     * It just deletes the channel info registry entry by id without checking the consistency of the others registries or the message store.</b>
     *
     * @param scopeId
     * @param id
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    @Override
    public void delete(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(id, "id");

        if (!isDatastoreServiceEnabled(scopeId)) {
            LOG.debug("Storage not enabled for account {}, return", scopeId);
            return;
        }

        ChannelInfo channelInfo = doFind(scopeId, id);
        if (channelInfo != null) {
            repository.delete(scopeId, id);
        }
    }

    /**
     * Find channel information by identifier
     *
     * @param scopeId
     * @param id
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    @Override
    public ChannelInfo find(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        return doFind(scopeId, id);
    }

    private ChannelInfo doFind(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ChannelInfoQuery idsQuery = new ChannelInfoQuery(scopeId);
        idsQuery.setLimit(1);

        IdsPredicate idsPredicate = storablePredicateFactory.newIdsPredicate();
        idsPredicate.addId(id);
        idsQuery.setPredicate(idsPredicate);

        ChannelInfoListResult result = query(idsQuery);
        return result.getFirstItem();
    }

    /**
     * Find channels informations matching the given query
     *
     * @param query
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    @Override
    public ChannelInfoListResult query(ChannelInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, returning empty result", query.getScopeId());
            return new ChannelInfoListResult();
        }

        return repository.query(query);
    }

    /**
     * Get channels informations count matching the given query
     *
     * @param query
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    @Override
    public long count(ChannelInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, returning empty result", query.getScopeId());
            return 0;
        }

        return repository.count(query);
    }

    /**
     * Delete channels informations count matching the given query.
     *
     * <b>Be careful using this function since it doesn't guarantee the datastore consistency.<br>
     * It just deletes the channel info registry entries that matching the query without checking the consistency of the others registries or the message store.</b>
     *
     * @param query
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    @Override
    public void delete(ChannelInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, skipping delete", query.getScopeId());
            return;
        }
        repository.delete(query);
    }
}
