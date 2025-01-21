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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.service.internal.KapuaServiceDisabledException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.datastore.ClientInfoRegistryService;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfoListResult;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoField;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.MessageField;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.MessageSchema;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.model.query.predicate.TermPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client information registry implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class ClientInfoRegistryServiceImpl implements ClientInfoRegistryService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientInfoRegistryServiceImpl.class);
    protected final Integer maxResultWindowValue;
    private final StorablePredicateFactory storablePredicateFactory;
    private final AccountService accountService;
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final ClientInfoRegistryFacade clientInfoRegistryFacade;
    private final DatastorePredicateFactory datastorePredicateFactory;
    private final MessageRepository messageRepository;
    private final DatastoreSettings datastoreSettings;

    private static final String QUERY = "query";
    private static final String QUERY_SCOPE_ID = "query.scopeId";

    /**
     * Default constructor
     */
    @Inject
    public ClientInfoRegistryServiceImpl(
            StorablePredicateFactory storablePredicateFactory,
            AccountService accountService,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DatastorePredicateFactory datastorePredicateFactory,
            ClientInfoRegistryFacade clientInfoRegistryFacade,
            MessageRepository messageRepository,
            DatastoreSettings datastoreSettings) {
        this.storablePredicateFactory = storablePredicateFactory;
        this.accountService = accountService;
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.datastorePredicateFactory = datastorePredicateFactory;
        this.clientInfoRegistryFacade = clientInfoRegistryFacade;
        this.messageRepository = messageRepository;
        this.datastoreSettings = datastoreSettings;
        this.maxResultWindowValue = datastoreSettings.getInt(DatastoreSettingsKey.MAX_RESULT_WINDOW_VALUE);
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
        if (query.getLimit() != null && query.getOffset() != null) {
            ArgumentValidator.notNegative(query.getLimit(), "limit");
            ArgumentValidator.notNegative(query.getOffset(), "offset");
            ArgumentValidator.numLessThenOrEqual(query.getLimit() + query.getOffset(), maxResultWindowValue, "limit + offset");
        }
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

    @Override
    public void delete(ClientInfoQuery query)
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

    @Override
    public void delete(KapuaId scopeId, StorableId id)
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
        Permission permission = permissionFactory.newPermission(Domains.DATASTORE, action, scopeId);
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

        MessageQuery messageQuery = new MessageQuery(clientInfo.getScopeId());
        messageQuery.setAskTotalCount(true);
        messageQuery.setFetchStyle(StorableFetchStyle.FIELDS);
        messageQuery.setLimit(1);
        messageQuery.setOffset(0);
        messageQuery.setSortFields(sort);

        RangePredicate messageIdPredicate = storablePredicateFactory.newRangePredicate(ClientInfoField.TIMESTAMP, clientInfo.getFirstMessageOn(), null);
        TermPredicate clientIdPredicate = datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, clientInfo.getClientId());

        AndPredicate andPredicate = storablePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(messageIdPredicate);
        andPredicate.getPredicates().add(clientIdPredicate);
        messageQuery.setPredicate(andPredicate);

        MessageListResult messageList = messageRepository.query(messageQuery);
        final List<DatastoreMessage> messages = Optional.ofNullable(messageList).map(ml -> ml.getItems()).orElse(new ArrayList<>());

        StorableId lastPublishedMessageId = null;
        Date lastPublishedMessageTimestamp = null;
        if (messages.size() == 1) {
            lastPublishedMessageId = messages.get(0).getDatastoreId();
            lastPublishedMessageTimestamp = messages.get(0).getTimestamp();
        } else if (messages.isEmpty()) {
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
    public boolean isServiceEnabled(KapuaId scopeId) {
        return !datastoreSettings.getBoolean(DatastoreSettingsKey.DISABLE_DATASTORE, false);
    }

}
