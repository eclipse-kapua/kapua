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
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.ClientInfoField;
import org.eclipse.kapua.service.datastore.internal.mediator.ClientInfoRegistryMediator;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.model.ClientInfoListResultImpl;
import org.eclipse.kapua.service.datastore.internal.model.query.ClientInfoQueryImpl;
import org.eclipse.kapua.service.datastore.internal.schema.ClientInfoSchema;
import org.eclipse.kapua.service.datastore.internal.schema.Metadata;
import org.eclipse.kapua.service.datastore.internal.schema.SchemaUtil;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.elasticsearch.client.model.ResultList;
import org.eclipse.kapua.service.elasticsearch.client.model.TypeDescriptor;
import org.eclipse.kapua.service.elasticsearch.client.model.UpdateRequest;
import org.eclipse.kapua.service.elasticsearch.client.model.UpdateResponse;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdFactory;
import org.eclipse.kapua.service.storable.model.query.predicate.IdsPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client information registry facade
 *
 * @since 1.0.0
 */
public class ClientInfoRegistryFacade extends AbstractRegistryFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ClientInfoRegistryFacade.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final StorableIdFactory STORABLE_ID_FACTORY = LOCATOR.getFactory(StorableIdFactory.class);
    private static final StorablePredicateFactory STORABLE_PREDICATE_FACTORY = LOCATOR.getFactory(StorablePredicateFactory.class);

    private final ClientInfoRegistryMediator mediator;
    private final Object metadataUpdateSync = new Object();

    private static final String QUERY = "query";
    private static final String QUERY_SCOPE_ID = "query.scopeId";

    /**
     * Constructs the client info registry facade
     *
     * @param configProvider
     * @param mediator
     * @since 1.0.0
     */
    public ClientInfoRegistryFacade(ConfigurationProvider configProvider, ClientInfoRegistryMediator mediator) {
        super(configProvider);

        this.mediator = mediator;
    }

    /**
     * Update the client information after a message store operation
     *
     * @param clientInfo
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     * @since 1.0.0
     */
    public StorableId upstore(ClientInfo clientInfo) throws KapuaIllegalArgumentException, ConfigurationException, ClientException, MappingException {
        ArgumentValidator.notNull(clientInfo, "clientInfo");
        ArgumentValidator.notNull(clientInfo.getScopeId(), "clientInfo.scopeId");
        ArgumentValidator.notNull(clientInfo.getFirstMessageId(), "clientInfo.firstPublishedMessageId");
        ArgumentValidator.notNull(clientInfo.getFirstMessageOn(), "clientInfo.firstPublishedMessageTimestamp");

        String clientInfoId = ClientInfoField.getOrDeriveId(clientInfo.getId(), clientInfo);
        StorableId storableId = STORABLE_ID_FACTORY.newStorableId(clientInfoId);

        UpdateResponse response = null;
        // Store client. Look up client in the cache, and cache it if it doesn't exist
        if (!DatastoreCacheManager.getInstance().getClientsCache().get(clientInfo.getClientId())) {
            // The code is safe even without the synchronized block
            // Synchronize in order to let the first thread complete its update
            // then the others of the same type will find the cache updated and
            // skip the update.
            synchronized (metadataUpdateSync) {
                if (!DatastoreCacheManager.getInstance().getClientsCache().get(clientInfoId)) {
                    // fix #REPLACE_ISSUE_NUMBER
                    ClientInfo storedField = find(clientInfo.getScopeId(), storableId);
                    if (storedField == null) {
                        Metadata metadata = mediator.getMetadata(clientInfo.getScopeId(), clientInfo.getFirstMessageOn().getTime());
                        String kapuaIndexName = metadata.getClientRegistryIndexName();

                        UpdateRequest request = new UpdateRequest(clientInfo.getId().toString(), new TypeDescriptor(kapuaIndexName, ClientInfoSchema.CLIENT_TYPE_NAME), clientInfo);
                        response = getElasticsearchClient().upsert(request);

                        LOG.debug("Upsert on asset successfully executed [{}.{}, {} - {}]", kapuaIndexName, ClientInfoSchema.CLIENT_TYPE_NAME, response.getId(), response.getId());
                    }
                    // Update cache if client update is completed successfully
                    DatastoreCacheManager.getInstance().getClientsCache().put(clientInfoId, true);
                }
            }
        }
        return storableId;
    }

    /**
     * Delete client information by identifier.<br>
     * <b>Be careful using this function since it doesn't guarantee the datastore consistency.<br>
     * It just deletes the client info registry entry by id without checking the consistency of the others registries or the message store.</b>
     *
     * @param scopeId
     * @param id
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public void delete(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(id, "id");

        if (!isDatastoreServiceEnabled(scopeId)) {
            LOG.debug("Storage not enabled for account {}, return", scopeId);
            return;
        }

        String indexName = SchemaUtil.getClientIndexName(scopeId);
        TypeDescriptor typeDescriptor = new TypeDescriptor(indexName, ClientInfoSchema.CLIENT_TYPE_NAME);
        getElasticsearchClient().delete(typeDescriptor, id.toString());
    }

    /**
     * Find client information by identifier
     *
     * @param scopeId
     * @param id
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public ClientInfo find(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(id, "id");

        ClientInfoQueryImpl idsQuery = new ClientInfoQueryImpl(scopeId);
        idsQuery.setLimit(1);

        IdsPredicate idsPredicate = STORABLE_PREDICATE_FACTORY.newIdsPredicate(ClientInfoSchema.CLIENT_TYPE_NAME);
        idsPredicate.addId(id);
        idsQuery.setPredicate(idsPredicate);

        ClientInfoListResult result = query(idsQuery);
        return result.getFirstItem();
    }

    /**
     * Find clients informations matching the given query
     *
     * @param query
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public ClientInfoListResult query(ClientInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, returning empty result", query.getScopeId());
            return new ClientInfoListResultImpl();
        }

        String indexName = SchemaUtil.getClientIndexName(query.getScopeId());
        TypeDescriptor typeDescriptor = new TypeDescriptor(indexName, ClientInfoSchema.CLIENT_TYPE_NAME);
        ResultList<ClientInfo> rl = getElasticsearchClient().query(typeDescriptor, query, ClientInfo.class);
        ClientInfoListResult result = new ClientInfoListResultImpl(rl);
        setLimitExceed(query, rl.getTotalHitsExceedsCount(), result);
        return result;
    }

    /**
     * Get clients informations count matching the given query
     *
     * @param query
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public long count(ClientInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, returning empty result", query.getScopeId());
            return 0;
        }

        String dataIndexName = SchemaUtil.getClientIndexName(query.getScopeId());
        TypeDescriptor typeDescriptor = new TypeDescriptor(dataIndexName, ClientInfoSchema.CLIENT_TYPE_NAME);
        return getElasticsearchClient().count(typeDescriptor, query);
    }

    /**
     * Delete clients informations count matching the given query.<br>
     * <b>Be careful using this function since it doesn't guarantee the datastore consistency.<br>
     * It just deletes the client info registry entries that matching the query without checking the consistency of the others registries or the message store.</b>
     *
     * @param query
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    public void delete(ClientInfoQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, skipping delete", query.getScopeId());
            return;
        }

        String indexName = SchemaUtil.getClientIndexName(query.getScopeId());
        TypeDescriptor typeDescriptor = new TypeDescriptor(indexName, ClientInfoSchema.CLIENT_TYPE_NAME);
        getElasticsearchClient().deleteByQuery(typeDescriptor, query);
    }
}
