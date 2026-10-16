/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.AbstractKapuaService;
import org.eclipse.kapua.commons.service.internal.KapuaServiceDisabledException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.datastore.ClientInfoRegistryService;
import org.eclipse.kapua.service.datastore.DatastoreDomains;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.internal.mediator.ClientInfoField;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreMediator;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageField;
import org.eclipse.kapua.service.datastore.internal.model.query.MessageQueryImpl;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfoListResult;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientInitializationException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.model.query.predicate.TermPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Client information registry implementation.
 *
 * @since 1.0.0
 */
@KapuaProvider
public class ClientInfoRegistryServiceImpl extends AbstractKapuaService implements ClientInfoRegistryService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientInfoRegistryServiceImpl.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final StorablePredicateFactory STORABLE_PREDICATE_FACTORY = LOCATOR.getFactory(StorablePredicateFactory.class);


    private final AccountService accountService;
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final ClientInfoRegistryFacade clientInfoRegistryFacade;
    private final MessageStoreService messageStoreService;
    private final DatastorePredicateFactory datastorePredicateFactory;

    private static final String QUERY = "query";
    private static final String QUERY_SCOPE_ID = "query.scopeId";

    /**
     * Default constructor
     *
     * @throws ClientInitializationException
     */
    public ClientInfoRegistryServiceImpl() throws ClientInitializationException {
        super(DatastoreEntityManagerFactory.getInstance());

        KapuaLocator locator = KapuaLocator.getInstance();
        accountService = locator.getService(AccountService.class);
        authorizationService = locator.getService(AuthorizationService.class);
        permissionFactory = locator.getFactory(PermissionFactory.class);
        messageStoreService = locator.getService(MessageStoreService.class);
        datastorePredicateFactory = KapuaLocator.getInstance().getFactory(DatastorePredicateFactory.class);

        MessageStoreService messageStoreService = KapuaLocator.getInstance().getService(MessageStoreService.class);
        ConfigurationProviderImpl configurationProvider = new ConfigurationProviderImpl(messageStoreService, accountService, DatastoreCacheManager.getInstance().getConfigurationCache());
        clientInfoRegistryFacade = new ClientInfoRegistryFacade(configurationProvider, DatastoreMediator.getInstance());
        DatastoreMediator.getInstance().setClientInfoStoreFacade(clientInfoRegistryFacade);
    }

    @Override
    public ClientInfo find(KapuaId scopeId, StorableId id) throws KapuaException {
        return find(scopeId, id, StorableFetchStyle.SOURCE_FULL);
    }

    @Override
    public ClientInfo find(KapuaId scopeId, StorableId id, StorableFetchStyle fetchStyle) throws KapuaException {
        if (!isServiceEnabled(scopeId)) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(id, "id");

        checkAccess(scopeId, Actions.read);
        try {
            ClientInfo clientInfo = clientInfoRegistryFacade.find(scopeId, id);
            if (clientInfo != null) {
                // populate the lastMessageTimestamp
                updateLastPublishedFields(clientInfo);
            }
            return clientInfo;
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public ClientInfoListResult query(ClientInfoQuery query)
            throws KapuaException {
        if (!isServiceEnabled(query.getScopeId())) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        checkAccess(query.getScopeId(), Actions.read);
        try {
            ClientInfoListResult result = clientInfoRegistryFacade.query(query);
            if (result != null && query.getFetchAttributes().contains(ClientInfoField.TIMESTAMP.field())) {
                // populate the lastMessageTimestamp
                for (ClientInfo clientInfo : result.getItems()) {
                    updateLastPublishedFields(clientInfo);
                }
            }
            return result;
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public long count(ClientInfoQuery query)
            throws KapuaException {
        if (!isServiceEnabled(query.getScopeId())) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        checkAccess(query.getScopeId(), Actions.read);
        try {
            return clientInfoRegistryFacade.count(query);
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    void delete(ClientInfoQuery query)
            throws KapuaException {
        if (!isServiceEnabled(query.getScopeId())) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        checkAccess(query.getScopeId(), Actions.delete);
        try {
            clientInfoRegistryFacade.delete(query);
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    void delete(KapuaId scopeId, StorableId id)
            throws KapuaException {
        if (!isServiceEnabled(scopeId)) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(id, "id");

        checkAccess(scopeId, Actions.delete);
        try {
            clientInfoRegistryFacade.delete(scopeId, id);
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    private void checkAccess(KapuaId scopeId, Actions action)
            throws KapuaException {
        Permission permission = permissionFactory.newPermission(DatastoreDomains.DATASTORE_DOMAIN, action, scopeId);
        authorizationService.checkPermission(permission);
    }

    /**
     * Update the last published date and last published message identifier for the specified client info, so it gets the timestamp and the message identifier of the last published message for the
     * account/clientId in the client info
     *
     * @throws KapuaException
     * @since 1.0.0
     */
    private void updateLastPublishedFields(ClientInfo clientInfo) throws KapuaException {
        List<SortField> sort = new ArrayList<>();
        sort.add(SortField.descending(MessageSchema.MESSAGE_TIMESTAMP));

        MessageQuery messageQuery = new MessageQueryImpl(clientInfo.getScopeId());
        messageQuery.setAskTotalCount(true);
        messageQuery.setFetchStyle(StorableFetchStyle.FIELDS);
        messageQuery.setLimit(1);
        messageQuery.setOffset(0);
        messageQuery.setSortFields(sort);

        RangePredicate messageIdPredicate = STORABLE_PREDICATE_FACTORY.newRangePredicate(ClientInfoField.TIMESTAMP, clientInfo.getFirstMessageOn(), null);
        TermPredicate clientIdPredicate = datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, clientInfo.getClientId());

        AndPredicate andPredicate = STORABLE_PREDICATE_FACTORY.newAndPredicate();
        andPredicate.getPredicates().add(messageIdPredicate);
        andPredicate.getPredicates().add(clientIdPredicate);
        messageQuery.setPredicate(andPredicate);

        MessageListResult messageList = messageStoreService.query(messageQuery);

        StorableId lastPublishedMessageId = null;
        Date lastPublishedMessageTimestamp = null;
        if (messageList.getSize() == 1) {
            lastPublishedMessageId = messageList.getFirstItem().getDatastoreId();
            lastPublishedMessageTimestamp = messageList.getFirstItem().getTimestamp();
        } else if (messageList.isEmpty()) {
            // this condition could happens due to the ttl of the messages (so if it happens, it does not necessarily mean there has been an error!)
            LOG.warn("Cannot find last timestamp for the specified client id '{}' - account '{}'", clientInfo.getScopeId(), clientInfo.getClientId());
        } else {
            // this condition shouldn't never happens since the query has a limit 1
            // if happens it means than an elasticsearch internal error happens and/or our driver didn't set it correctly!
            LOG.error("Cannot find last timestamp for the specified client id '{}' - account '{}'. More than one result returned by the query!", clientInfo.getScopeId(), clientInfo.getClientId());
        }
        clientInfo.setLastMessageId(lastPublishedMessageId);
        clientInfo.setLastMessageOn(lastPublishedMessageTimestamp);
    }

    @Override
    protected boolean isServiceEnabled(KapuaId scopeId) {
        return !DatastoreSettings.getInstance().getBoolean(DatastoreSettingsKey.DISABLE_DATASTORE, false);
    }

}
